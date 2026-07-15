package com.kychnoo.skinanalysis_android_client.data.repository

import android.content.Context
import android.net.Uri
import com.kychnoo.skinanalysis_android_client.data.DataStoreManager
import com.kychnoo.skinanalysis_android_client.data.model.response.TaskResponse
import com.kychnoo.skinanalysis_android_client.data.remote.ApiService
import com.kychnoo.skinanalysis_android_client.provider.AndroidResourceProvider
import com.kychnoo.skinanalysis_android_client.provider.DeviceIdProvider
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class SkinAnalysisRepositoryTest {

    private val apiService: ApiService = mockk()
    private val deviceIdProvider: DeviceIdProvider = mockk()
    private val resources: AndroidResourceProvider = mockk()
    private val dataStoreManager: DataStoreManager = mockk(relaxed = true)
    private val context: Context = mockk()

    private lateinit var repository: SkinAnalysisRepository

    @Before
    fun setup() {
        repository = SkinAnalysisRepository(
            apiService,
            deviceIdProvider,
            resources,
            dataStoreManager,
            context
        )
    }

    @Test
    fun `getTaskStatus success returns TaskResponse`() = runTest {
        val taskId = "123"
        val expectedResponse = mockk<TaskResponse>()
        coEvery { apiService.getTaskStatus(taskId) } returns Response.success(expectedResponse)

        val result = repository.getTaskStatus(taskId)

        assertTrue(result.isSuccess)
        assertEquals(expectedResponse, result.getOrNull())
    }

    @Test
    fun `getTaskStatus failure returns Result failure with message`() = runTest {
        val taskId = "123"
        val errorCode = 404
        val errorMessage = "Not Found"
        
        coEvery { apiService.getTaskStatus(taskId) } returns Response.error(errorCode, "".toResponseBody())
        every { resources.getHttpErrorMessage(errorCode) } returns errorMessage

        val result = repository.getTaskStatus(taskId)

        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    @Test
    fun `getTaskStatus 403 clears connection id`() = runTest {
        val taskId = "123"
        val errorCode = 403
        
        coEvery { apiService.getTaskStatus(taskId) } returns Response.error(errorCode, "".toResponseBody())
        every { resources.getHttpErrorMessage(errorCode) } returns "Forbidden"

        repository.getTaskStatus(taskId)

        io.mockk.coVerify { dataStoreManager.clearConnectionId() }
    }
}
