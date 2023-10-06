package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.entity.LaunchInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_pref")
    companion object {
        val FIRST_TIME_LAUNCH_KAY = booleanPreferencesKey("first_time_launch")
        val IS_LOGIN_KAY = booleanPreferencesKey("login_key")

    }
    suspend fun saveAsFirstTimeLaunch(launchInfo: LaunchInfo) {
        context.dataStore.edit { preferences ->
            preferences[FIRST_TIME_LAUNCH_KAY] = launchInfo.isFirstTimeLaunch
            preferences[IS_LOGIN_KAY] = launchInfo.isLogin

        }
    }
    fun isFirstTimeLaunch(): Flow<LaunchInfo> =
        context.dataStore.data
            .map { preferences ->
                val firstTimeLaunch = preferences[FIRST_TIME_LAUNCH_KAY] ?: false
                val isLogin = preferences[IS_LOGIN_KAY] ?: false
                LaunchInfo(firstTimeLaunch, isLogin)
            }
}