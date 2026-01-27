package com.kychnoo.skinanalysis_android_client.data.repository

import com.kychnoo.skinanalysis_android_client.data.DataStoreManager
import kotlinx.coroutines.flow.Flow

class ConnectionRepository(private val dataStoreManager: DataStoreManager) {
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