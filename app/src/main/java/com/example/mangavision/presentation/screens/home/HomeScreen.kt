package com.example.mangavision.presentation.screens.home

// presentation/screens/home/HomeScreen.kt

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mangavision.presentation.screens.facerecognition.FaceRecognitionScreen
import com.example.mangavision.presentation.screens.manga.MangaScreen
import com.example.mangavision.presentation.screens.manga.MangaDetailsScreen

data class Screen(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val color : Color = Color.White
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1A1A1A)
            ) {

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "manga",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("manga") { 
                MangaScreen(
                    onMangaClick = { mangaJson ->
                        navController.navigate("manga_details/${java.net.URLEncoder.encode(mangaJson, "UTF-8")}")
                    }
                ) 
            }
            composable("face_recognition") { FaceRecognitionScreen() }
            composable(
                route = "manga_details/{mangaJson}",
                arguments = listOf(
                    navArgument("mangaJson") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val mangaJson = backStackEntry.arguments?.getString("mangaJson") ?: return@composable
                MangaDetailsScreen(
                    mangaJson = mangaJson,
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
}

val items = listOf(
    Screen("manga", Icons.Default.Book, "Manga", color = Color.White),
    Screen("face_recognition", Icons.Default.Face, "Face Recognition",color = Color.White)
)