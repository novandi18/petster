package com.novandiramadhan.petster.domain.datastore

import kotlinx.coroutines.flow.Flow

interface WelcomeDataStore {
    val state: Flow<Boolean>
    suspend fun setWelcomeState(isWelcome: Boolean)
}