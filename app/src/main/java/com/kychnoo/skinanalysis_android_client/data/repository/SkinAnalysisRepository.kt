package com.kychnoo.skinanalysis_android_client.data.repository

import android.content.Context
import android.net.Uri
import android.os.Build
import com.kychnoo.skinanalysis_android_client.R
import com.kychnoo.skinanalysis_android_client.data.model.request.DeviceRegisterRequest
import com.kychnoo.skinanalysis_android_client.data.model.response.AnalysisResponse
import com.kychnoo.skinanalysis_android_client.data.model.response.TaskResponse
import com.kychnoo.skinanalysis_android_client.data.remote.ApiService
import com.kychnoo.skinanalysis_android_client.provider.AndroidResourceProvider
import com.kychnoo.skinanalysis_android_client.provider.DeviceIdProvider
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class SkinAnalysisRepository(
    private val apiService: ApiService,
    private val resources: AndroidResourceProvider
    ) {

    suspend fun analyzeImage(context: Context, imageUri: Uri): Result<String> {
        return try {
            val file = imageUri.toFile(context) // Convert uri to file.
            if (file == null) return Result.failure(Exception("File is null"))
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull()) // Convert file to request body as image media type.

            val imagePart = MultipartBody.Part.createFormData("file", file.name, requestFile) // Create multipart request.

            val response = apiService.analyzeImage(imagePart) // Send request to api for analyze image.

            if (response.isSuccessful) {
                Result.success(response.body()?.taskId ?: "") // Get task id if successfully response.
            } else {
                val message = resources.getHttpErrorMessage(response.code())
                Result.failure(Exception(message)) // Else return error message.
            }
        } catch (e: Exception) {
            val errorMsg = resources.getErrorMessage(e)
            Result.failure(Exception(errorMsg))
        }
    }

    suspend fun getTaskStatus(taskId: String): Result<TaskResponse> {
        return try {
            val response = apiService.getTaskStatus(taskId) // Get task status from api.
            if (response.isSuccessful) {
                Result.success(response.body()!!) // Get body if response is successfully.
            } else {
                val message = resources.getHttpErrorMessage(response.code())
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            val errorMsg = resources.getErrorMessage(e)
            Result.failure(Exception(errorMsg))
        }
    }

    suspend fun getTaskResult(taskId: String): Result<AnalysisResponse> {
        return try {
            val response = apiService.getTaskResult(taskId) // Get task result from api.
            if (response.isSuccessful) {
                Result.success(response.body()!!) // Get body if response is successfully.
            } else {
                val message = resources.getHttpErrorMessage(response.code())
                Result.failure(Exception(message))
            }

        } catch (e: Exception) {
            val errorMsg = resources.getErrorMessage(e)
            Result.failure(Exception(errorMsg))
        }
    }

    suspend fun checkSecureEndpoint(): Result<String> {
        return try {
            val response = apiService.checkSecureEndpoint()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val message = body["message"] as? String // Try get message from body as String.
                    if (message != null) {
                        Result.success(message)
                    } else {
                        Result.failure(Exception(resources.getString(R.string.message_not_found_in_response)))
                    }
                } else {
                    Result.failure(Exception(resources.getString(R.string.empty_response_body)))
                }
            } else {
                val message = resources.getHttpErrorMessage(response.code())
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            val errorMsg = resources.getErrorMessage(e)
            Result.failure(Exception(errorMsg))
        }
    }

    suspend fun registerDevice(
        connectionId: String,
        platform: String = "Android",
        deviceName: String = Build.MODEL,
    ): Result<String> {
        return try {
            val request = DeviceRegisterRequest( // Create new Device Register Request.
                platform = platform, // Set the platform.
                deviceUid = DeviceIdProvider.getDeviceId(), // Get device uid from device provider and set here.
                name = deviceName, // Set the device name.
                model = Build.MODEL, // Set the device model.
                osVersion = Build.VERSION.RELEASE, // Set the Android version.
            )

            val response = apiService.registerDevice(request) // Send request for register device.
            if (response.isSuccessful) {
                Result.success(connectionId)
            } else {
                val message = resources.getHttpErrorMessage(response.code())
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            val errorMsg = resources.getErrorMessage(e)
            Result.failure(Exception(errorMsg))
        }
    }

    suspend fun getFullAnalysis(taskId: String): Result<AnalysisResponse> {
        return try {
            val response = apiService.getTaskResult(taskId)
            if (!response.isSuccessful) {
                return Result.failure(Exception(resources.getHttpErrorMessage(response.code())))
            }

            response.body()?.let { body ->
                Result.success(body)
            } ?: Result.failure(Exception(resources.getHttpErrorMessage(response.code())))
        } catch (e: Exception) {
            val errorMsg = resources.getErrorMessage(e)
            Result.failure(Exception(errorMsg))
        }
    }

    fun Uri.toFile(context: Context): File? {
        val inputStream = context.contentResolver.openInputStream(this) ?: return null

        val tempFile = try {
            File.createTempFile("image_prefix", ".jpg", context.cacheDir) // Create new temporary file.
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        return try {
            tempFile.outputStream().use { fileOut ->
                inputStream.copyTo(fileOut)
            }
            tempFile.deleteOnExit()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            tempFile.delete()
            null
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}