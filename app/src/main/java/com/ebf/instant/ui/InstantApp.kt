package com.ebf.instant.ui

import androidx.compose.animation.Crossfade
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.ebf.instant.ui.camera.CameraScreen
import com.ebf.instant.ui.comment.CommentScreen
import com.ebf.instant.ui.create.CreateAccountScreen
import com.ebf.instant.ui.feed.FeedScreen
import com.ebf.instant.ui.login.LoginScreen
import com.ebf.instant.ui.signin.Ok
import org.koin.androidx.compose.getViewModel
import timber.log.Timber

@Composable
fun InstantApp(
    instantAppViewModel: InstantAppViewModel = getViewModel()
) {
    val userState by instantAppViewModel.userState.collectAsState()
    Timber.d("Current state is $userState")

    Crossfade(targetState = userState) { state ->
        when(state) {
            Ok.NOT_CONNECTED -> {
                LoginScreen()
            }
            Ok.LOGGED_NOT_VALID -> {
                CreateAccountScreen()
            }
            Ok.LOGGED_VALID -> {
                InstantGraph()
            }
            else -> { /* TODO placeholder ? */ }
        }
    }

}

@Composable
fun InstantGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            FeedScreen(
                navigateToPostComments = { navController.navigate("post/$it/comments") },
                navigateTo = { navController.navigate(it) }
            )
        }
        composable("camera") {
            CameraScreen()
        }
        composable("post/{postId}/comments") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")!!
            CommentScreen(postId = postId, onBack = { navController.navigateUp() })
        }
        composable("account") {
            Text(text = "TODO")
        }
        composable(
            route = "account/{userId}",
            deepLinks = listOf(navDeepLink {
                uriPattern = "instant://account/{userId}"
            })
        ) {
            Text(text = "account")
        }
    }
}
