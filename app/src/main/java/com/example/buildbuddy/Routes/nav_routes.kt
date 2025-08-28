package com.example.buildbuddy.Routes

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.buildbuddy.BuildScreen
import com.example.buildbuddy.HomeScreen

/** Nav routes */
private object Routes {
    const val HOME = "home"
    const val BUILD = "build"
}

@Composable
fun BuildBuddyApp() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(onStartClick = { nav.navigate(Routes.BUILD) })
        }
        composable(Routes.BUILD) {
            BuildScreen(onBack = { nav.popBackStack() })
        }
    }
}
