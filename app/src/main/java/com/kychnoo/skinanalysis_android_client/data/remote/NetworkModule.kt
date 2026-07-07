package com.kychnoo.skinanalysis_android_client.data.remote

import android.content.Context
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.kychnoo.skinanalysis_android_client.data.DataStoreManager
import com.kychnoo.skinanalysis_android_client.provider.DeviceIdProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideApiInterceptor(
        @ApplicationContext context: Context,
        dataStoreManager: DataStoreManager,
        deviceIdProvider: DeviceIdProvider
    ): ApiInterceptor { // Initialize Device ID Provider.
        return ApiInterceptor(dataStoreManager, deviceIdProvider) // Create new ApiInterceptor instance.
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: ApiInterceptor): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    @Provides
    @Singleton
    fun provideApiService(client: OkHttpClient): ApiService = Retrofit.Builder()
        .baseUrl(ApiService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) // Add converter to convert JSON into objects.
        .client(client)
        .build()
        .create<ApiService>(ApiService::class.java) // Create api service.

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = okHttpClient
                    )
                )
            }
            .crossfade(true)
            .build()
    }
}