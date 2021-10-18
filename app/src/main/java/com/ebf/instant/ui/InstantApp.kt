package com.ebf.instant.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ebf.instant.ui.onboarding.WelcomeScreen
import com.ebf.instant.ui.theme.InstantTheme

@Composable
fun InstantApp() {
    InstantTheme {
        Auth()
    }
}

@Composable
fun Auth() {
    // var userId by remember { mutableStateOf(Firebase.auth.currentUser?.uid ?: "") }
    // if (userId.isEmpty()) {
        // LoginScreen(onLoginSuccess = { uid -> userId = uid })
        WelcomeScreen()
//    } else {
//        AppContainer()
//    }
}

@Composable
fun AppContainer() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Instant")
                },
                actions = {
                    IconButton(onClick = { navController.navigate(MainDestinations.CAMERA) }) {
                        Icon(imageVector = Icons.Default.Camera, contentDescription = null)
                    }
                }
            )
        },
    ) { innerPadding ->
        InstantNavGraph(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}
