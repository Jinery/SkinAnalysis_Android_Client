package com.kychnoo.skinanalysis_android_client.di

import com.kychnoo.skinanalysis_android_client.data.manager.snackbar.SnackbarManager
import com.kychnoo.skinanalysis_android_client.data.manager.snackbar.SnackbarManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SnackbarManagerModule {
    @Binds
    @Singleton
    abstract fun bindSnackbarManager(
        implementation: SnackbarManagerImpl
    ): SnackbarManager
}