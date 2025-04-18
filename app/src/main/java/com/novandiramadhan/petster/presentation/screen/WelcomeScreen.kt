package com.novandiramadhan.petster.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.presentation.ui.theme.Black
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import com.novandiramadhan.petster.presentation.ui.theme.White
import com.novandiramadhan.petster.presentation.viewmodel.WelcomeViewModel
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = hiltViewModel(),
    navigateToHome: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val welcomeState by viewModel.welcomeState.collectAsState()

    if (welcomeState) {
        navigateToHome()
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
                .background(White),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.fillMaxWidth()
            )

            ElevatedButton(
                onClick = {
                    coroutineScope.launch {
                        viewModel.setWelcomeState(true)
                    }
                    navigateToHome()
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Black,
                    contentColor = White
                )
            ) {
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = stringResource(R.string.get_started),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    PetsterTheme {
        WelcomeScreen(navigateToHome = {})
    }
}