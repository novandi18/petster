package com.novandiramadhan.petster.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.domain.datastore.AuthDataStore
import com.novandiramadhan.petster.domain.model.AuthState
import com.novandiramadhan.petster.presentation.main.PetsterApp
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authDataStore: AuthDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetsterTheme {
                Box(
                    modifier = Modifier.safeDrawingPadding()
                ) {
                    val authState = authDataStore.state.collectAsState(
                        initial = AuthState(uuid = null, email = null, userType = UserType.NONE)
                    ).value

                    PetsterApp(
                        userLoggedInType = authState.userType
                    )
                }
            }
        }
    }
}