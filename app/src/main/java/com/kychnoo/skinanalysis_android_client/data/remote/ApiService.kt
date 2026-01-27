package com.kychnoo.skinanalysis_android_client.data.remote

import com.kychnoo.skinanalysis_android_client.data.model.request.DeviceRegisterRequest
import com.kychnoo.skinanalysis_android_client.data.model.response.AnalysisResponse
import com.kychnoo.skinanalysis_android_client.data.model.response.TaskResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

// Api Service interface for request to API using retrofit.
interface ApiService {

    @Multipart
    @POST("/analyze")
    suspend fun analyzeImage( // Analyze image function.
        @Part file: MultipartBody.Part
    ): Response<TaskResponse>

    @GET("/auth/check")
    suspend fun checkSecureEndpoint(): Response<Map<String, Any>>

    @GET("/tasks/{task_id}/status")
    suspend fun getTaskStatus( // Function to get selected task status.
        @Path("task_id") taskId: String
    ): Response<TaskResponse>

    @GET("/tasks/{task_id}/result")
    suspend fun getTaskResult( // Function to get selected task result(if available).
        @Path("task_id") taskId: String
    ): Response<AnalysisResponse>

    @POST("/auth/register-device")
    suspend fun registerDevice( // Function to send post request for register device.
        @Body request: DeviceRegisterRequest
    ): Response<Map<String, Any>>

    companion object {
        const val BASE_URL = "http://192.168.1.71:8000"
    }
}