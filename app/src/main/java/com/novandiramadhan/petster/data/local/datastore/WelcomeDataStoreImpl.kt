package com.novandiramadhan.petster.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.novandiramadhan.petster.common.DataStoreKeys
import com.novandiramadhan.petster.domain.datastore.WelcomeDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WelcomeDataStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context
): WelcomeDataStore {
    override val state: Flow<Boolean> = context.welcomeDataStore.data
        .catch { exception ->
            if (exception is Exception) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[WELCOME_STATUS] ?: false
        }

    override suspend fun setWelcomeState(isWelcome: Boolean) {
        context.welcomeDataStore.edit { preferences ->
            preferences[WELCOME_STATUS] = isWelcome
        }
    }

    companion object {
        private val Context.welcomeDataStore by preferencesDataStore(name = DataStoreKeys.WELCOME_PREFERENCE)
        private val WELCOME_STATUS = booleanPreferencesKey(name = DataStoreKeys.WELCOME_STATUS)
    }
}