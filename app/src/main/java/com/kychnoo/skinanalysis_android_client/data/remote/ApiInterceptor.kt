package com.kychnoo.skinanalysis_android_client.data.remote

import com.kychnoo.skinanalysis_android_client.data.DataStoreManager
import com.kychnoo.skinanalysis_android_client.provider.DeviceIdProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale

class ApiInterceptor(
    private val dataStoreManager: DataStoreManager,
    private var onAuthFailed: (() -> Unit)? = null
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("X-Device-ID", DeviceIdProvider.getDeviceId()) // Add headers to builder.
            .header("Accept-Language", Locale.getDefault().language)

        runBlocking {
            dataStoreManager.getConnectionId()?.let { id ->
                requestBuilder.header("connection-id", id) // Get connection id and send request to API.
            }
        }

        val response = chain.proceed(requestBuilder.build()) // Get response from request.

        if (response.code == 403) { // Check status code.
            runBlocking {
                dataStoreManager.clearConnectionId() // If status is connection expired then clear connection id.
            }
            onAuthFailed?.invoke() // Send callback for return to add connection id screen.
        }

        return response
    }

    fun setAuthListener(listener: () -> Unit) {
        this.onAuthFailed = listener
    }
}