package com.kychnoo.skinanalysis_android_client.data.repository

import com.kychnoo.skinanalysis_android_client.data.DataStoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {
    suspend fun saveConnectionId(id: String) {
        dataStoreManager.saveConnectionId(id)
    }

    fun getConnectionId(): Flow<String?> {
        return dataStoreManager.getConnectionIdFlow
    }

    suspend fun clearConnectionId() {
        dataStoreManager.clearConnectionId()
    }
}