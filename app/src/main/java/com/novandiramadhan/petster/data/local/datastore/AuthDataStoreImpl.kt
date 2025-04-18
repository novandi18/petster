package com.novandiramadhan.petster.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.novandiramadhan.petster.common.DataStoreKeys
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.domain.datastore.AuthDataStore
import com.novandiramadhan.petster.domain.model.AuthState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDataStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context
): AuthDataStore {
    override val state: Flow<AuthState> = context.authDataStore.data
        .catch { exception ->
            if (exception is Exception) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val userTypeString = preferences[AUTH_USER_TYPE]
            val userTypeEnum = try {
                userTypeString?.let { UserType.valueOf(it) } ?: UserType.NONE
            } catch (_: IllegalArgumentException) {
                UserType.NONE
            }

            AuthState(
                uuid = preferences[AUTH_UUID],
                email = preferences[AUTH_EMAIL],
                userType = userTypeEnum
            )
        }

    override suspend fun setAuthState(state: AuthState) {
        context.authDataStore.edit { preferences ->
            preferences[AUTH_UUID] = state.uuid as String
            preferences[AUTH_EMAIL] = state.email as String
            preferences[AUTH_USER_TYPE] = state.userType.name
        }
    }

    override suspend fun deleteAuthState() {
        context.authDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val Context.authDataStore by preferencesDataStore(name = DataStoreKeys.AUTH_PREFERENCE)
        private val AUTH_UUID = stringPreferencesKey(DataStoreKeys.AUTH_UUID)
        private val AUTH_EMAIL = stringPreferencesKey(DataStoreKeys.AUTH_EMAIL)
        private val AUTH_USER_TYPE = stringPreferencesKey(DataStoreKeys.AUTH_USER_TYPE)
    }
}