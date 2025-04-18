package com.novandiramadhan.petster.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novandiramadhan.petster.domain.datastore.WelcomeDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val welcomeDataStore: WelcomeDataStore
) : ViewModel() {
    val welcomeState: StateFlow<Boolean> = welcomeDataStore.state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    suspend fun setWelcomeState(isWelcome: Boolean) {
        welcomeDataStore.setWelcomeState(isWelcome)
    }
}