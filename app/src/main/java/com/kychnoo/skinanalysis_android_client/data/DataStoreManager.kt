package com.kychnoo.skinanalysis_android_client.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

val Context.dataStorage: DataStore<Preferences> by preferencesDataStore(name = "app_data")

class DataStoreManager(private val context: Context) {
    companion object {
        val CONNECTION_ID_KEY = stringPreferencesKey("connection_id")
    }

    suspend fun saveConnectionId(connectionId: String) {
        context.dataStorage.edit { preferences ->
            preferences[CONNECTION_ID_KEY] = connectionId
        }
    }

    val getConnectionIdFlow: Flow<String?> get() = context.dataStorage.data.map { preferences -> preferences[CONNECTION_ID_KEY] }

    suspend fun getConnectionId(): String? {
        return context.dataStorage.data.map { preferences ->
            preferences[CONNECTION_ID_KEY]
        }.firstOrNull()
    }

    suspend fun clearConnectionId() {
        context.dataStorage.edit { preferences -> preferences.remove(CONNECTION_ID_KEY) }
    }
}