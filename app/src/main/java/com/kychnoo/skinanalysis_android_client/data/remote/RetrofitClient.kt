package com.kychnoo.skinanalysis_android_client.data.remote

import android.content.Context
import com.kychnoo.skinanalysis_android_client.data.DataStoreManager
import com.kychnoo.skinanalysis_android_client.provider.DeviceIdProvider
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var apiService: ApiService? = null

    var interceptor: ApiInterceptor? = null // Public getter for interceptor.
        private set // Private setter.

    fun getApiService(context: Context): ApiService {
        return apiService ?: synchronized(this) { // If Api Service is null, initialize his.
            DeviceIdProvider.init(context) // Initialize Device ID Provider.
            val dataStore = DataStoreManager(context) // Create new DataStore instance.

            val newInterceptor = ApiInterceptor(dataStore) // Create new ApiInterceptor instance.
            interceptor = newInterceptor

            val client = OkHttpClient.Builder() // Build new OkHttpClient.
                .addInterceptor(newInterceptor) // Add interceptor to client.
                .build()

            val retrofit = Retrofit.Builder() // Build retrofit instance.
                .baseUrl(ApiService.BASE_URL) // Set Base url.
                .addConverterFactory(GsonConverterFactory.create()) // Add converter to convert json into objects.
                .client(client) // Set OkHttpClient.
                .build()

            val service = retrofit.create(ApiService::class.java)
            apiService = service
            service
        }
    }
}