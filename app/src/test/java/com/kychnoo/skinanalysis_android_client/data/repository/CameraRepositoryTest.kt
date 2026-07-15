package com.kychnoo.skinanalysis_android_client.data.repository

import android.net.Uri
import com.kychnoo.skinanalysis_android_client.data.camera.CameraManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CameraRepositoryTest {

    private val cameraManager: CameraManager = mockk(relaxed = true)
    private lateinit var repository: CameraRepository

    @Before
    fun setup() {
        repository = CameraRepository(cameraManager)
    }

    @Test
    fun `takePhoto delegates to CameraManager`() {
        val onPhotoTaken: (Uri) -> Unit = {}
        val onError: (Exception) -> Unit = {}

        repository.takePhoto(onPhotoTaken, onError)

        verify { cameraManager.takePhoto(onPhotoTaken, onError) }
    }

    @Test
    fun `deletePhoto delegates to CameraManager`() {
        val mockUri = mockk<Uri>()
        
        repository.deletePhoto(mockUri)

        verify { cameraManager.deletePhoto(mockUri) }
    }

    @Test
    fun `shutdown delegates to CameraManager`() {
        repository.shutdown()

        verify { cameraManager.shutdown() }
    }
}
