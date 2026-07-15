package com.kychnoo.skinanalysis_android_client.ui.viewmodel

import android.net.Uri
import app.cash.turbine.test
import com.kychnoo.skinanalysis_android_client.data.manager.snackbar.SnackbarManager
import com.kychnoo.skinanalysis_android_client.data.model.TaskStatus
import com.kychnoo.skinanalysis_android_client.data.model.response.AnalysisResponse
import com.kychnoo.skinanalysis_android_client.data.model.response.TaskResponse
import com.kychnoo.skinanalysis_android_client.data.model.states.camera.CameraState
import com.kychnoo.skinanalysis_android_client.data.model.types.SnackbarType
import com.kychnoo.skinanalysis_android_client.data.repository.CameraRepository
import com.kychnoo.skinanalysis_android_client.data.repository.ConnectionRepository
import com.kychnoo.skinanalysis_android_client.data.repository.SkinAnalysisRepository
import com.kychnoo.skinanalysis_android_client.provider.ResourceProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalysisViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val analysisRepository: SkinAnalysisRepository = mockk()
    private val cameraRepository: CameraRepository = mockk(relaxed = true)
    private val connectionRepository: ConnectionRepository = mockk()
    private val resourceProvider: ResourceProvider = mockk()
    private val snackbarManager: SnackbarManager = mockk(relaxed = true)

    private val cameraStateFlow = MutableStateFlow(CameraState())
    private lateinit var viewModel: AnalysisViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Uri::class)
        
        every { connectionRepository.getConnectionIdAsFlow() } returns flowOf("test-id")
        every { cameraRepository.cameraState } returns cameraStateFlow
        every { resourceProvider.getString(any()) } returns "Mocked String"
        
        viewModel = AnalysisViewModel(
            analysisRepository,
            cameraRepository,
            connectionRepository,
            resourceProvider,
            snackbarManager
        )
    }

    @After
    fun tearDown() {
        cameraStateFlow.value = CameraState()
        Dispatchers.resetMain()
    }

    @Test
    fun `uploadAndAnalyse success path - polls until completed`() = runTest {
        val mockUri = mockk<Uri>()
        val taskId = "task-123"
        val imageUrl = "http://result.com/image.jpg"

        coEvery { analysisRepository.analyzeImage(mockUri) } returns Result.success(taskId)
        coEvery { analysisRepository.getTaskStatus(taskId) } returns Result.success(
            TaskResponse(
                taskId = taskId,
                status = TaskStatus.COMPLETED,
                message = "Done",
                createdAt = "2023-01-01T00:00:00"
            )
        )
        coEvery { analysisRepository.getTaskResult(taskId) } returns Result.success(
            AnalysisResponse(
                status = "success",
                message = "Analysis completed",
                imageUrl = imageUrl,
                analysisResult = emptyList()
            )
        )

        viewModel.uploadAndAnalyse(mockUri)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(imageUrl, state.screenState.analysisResultUrl)
            assertFalse(state.screenState.isAnalysing)
        }

        coVerify { cameraRepository.deletePhoto(mockUri) }
    }

    @Test
    fun `uploadAndAnalyse failure path - shows snackbar on upload error`() = runTest {
        viewModel.uiState.test {
            awaitItem()

            val mockUri = mockk<Uri>()
            val errorMessage = "Upload failed"

            coEvery { analysisRepository.analyzeImage(mockUri) } returns Result.failure(Exception(errorMessage))

            viewModel.uploadAndAnalyse(mockUri)
            
            awaitItem() // isAnalysing = true
            awaitItem() // isAnalysing = false after failure

            coVerify { snackbarManager.showSnackbar(errorMessage, SnackbarType.ERROR) }
            assertFalse(viewModel.uiState.value.screenState.isAnalysing)
        }
    }

    @Test
    fun `pollTaskStatus handles FAILED status`() = runTest {
        viewModel.uiState.test {
            awaitItem() // initial

            val mockUri = mockk<Uri>()
            val taskId = "task-123"
            val failureMessage = "Analysis failed on server"

            coEvery { analysisRepository.analyzeImage(mockUri) } returns Result.success(taskId)
            coEvery { analysisRepository.getTaskStatus(taskId) } returns Result.success(
                TaskResponse(
                    taskId = taskId,
                    status = TaskStatus.FAILED,
                    message = failureMessage,
                    createdAt = "2023-01-01T00:00:00"
                )
            )

            viewModel.uploadAndAnalyse(mockUri)
            
            awaitItem() // isAnalysing = true
            awaitItem() // isAnalysing = false after poll failure

            coVerify { snackbarManager.showSnackbar(failureMessage, SnackbarType.ERROR) }
            assertFalse(viewModel.uiState.value.screenState.isAnalysing)
        }
    }

    @Test
    fun `takePhoto shows warning when dark condition is detected`() = runTest {
        viewModel.uiState.test {
            awaitItem() // skip initial

            cameraStateFlow.value = CameraState(isDarkCondition = true)
            awaitItem() // wait for update

            every { resourceProvider.getString(any()) } returns "Low brightness"

            viewModel.takePhoto()

            coVerify { snackbarManager.showSnackbar("Low brightness", SnackbarType.INFO) }
        }
    }

    @Test
    fun `takePhoto shows snackbar on camera error`() = runTest {
        val errorMessage = "Camera capture failed"
        every { resourceProvider.getString(any()) } returns "Camera Error"
        
        // Simulate error callback
        every { cameraRepository.takePhoto(any(), any()) } answers {
            val onError = secondArg<(Exception) -> Unit>()
            onError(Exception(errorMessage))
        }

        viewModel.takePhoto()

        coVerify { snackbarManager.showSnackbar(errorMessage, SnackbarType.ERROR) }
    }

    @Test
    fun `initializeCamera calls repository`() {
        val lifecycleOwner = mockk<androidx.lifecycle.LifecycleOwner>()
        val previewView = mockk<androidx.camera.view.PreviewView>()

        viewModel.initializeCamera(lifecycleOwner, previewView)

        io.mockk.verify { cameraRepository.initializeCamera(lifecycleOwner, previewView) }
    }
}
