package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_pref")
    companion object { val FIRST_TIME_LAUNCH_KAY = booleanPreferencesKey("first_time_launch") }
    suspend fun saveAsFirstTimeLaunch(isFirstTime: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[FIRST_TIME_LAUNCH_KAY] = isFirstTime

        }
    }
    fun isFirstTimeLaunch(): Flow<Boolean> =
        context.dataStore.data
            .map { preferences ->
                preferences[FIRST_TIME_LAUNCH_KAY] ?: false
            }
}