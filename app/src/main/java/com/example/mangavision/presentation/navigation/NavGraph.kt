package com.example.mangavision.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mangavision.presentation.screens.auth.SignInScreen
import com.example.mangavision.presentation.screens.home.HomeScreen
import com.example.mangavision.presentation.viewmodel.AuthViewModel

// presentation/navigation/NavGraph.kt
@Composable
fun MangaVisionNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn) "home" else "signin"
    ) {
        composable("signin") {
            SignInScreen(
                onSignIn = { email, password ->
                    // Call your ViewModel's signIn
                    // If successful, navigate:
                    navController.navigate("home") {
                        popUpTo("signin") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onSignOut = {
                    navController.navigate("signin") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}