package com.kychnoo.skinanalysis_android_client.di

import com.kychnoo.skinanalysis_android_client.provider.AndroidResourceProvider
import com.kychnoo.skinanalysis_android_client.provider.ResourceProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ResourceProviderModule {
    @Binds
    @Singleton
    abstract fun bindResourceProvider(
        implementation: AndroidResourceProvider
    ): ResourceProvider
}