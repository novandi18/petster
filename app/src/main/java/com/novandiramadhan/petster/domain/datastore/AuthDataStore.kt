package com.novandiramadhan.petster.domain.datastore

import com.novandiramadhan.petster.domain.model.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthDataStore {
    val state: Flow<AuthState>
    suspend fun setAuthState(state: AuthState)
    suspend fun deleteAuthState()
}