package com.example.buildbuddy.Routes

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.buildbuddy.Screens.BuildScreen
import com.example.buildbuddy.Screens.HomeScreen
import com.example.buildbuddy.SplashScreen

/** Nav routes */
private object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val BUILD = "build"
}

@Composable
fun AppNavigator() {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(navController = nav)
        }
        composable(Routes.HOME) {
            HomeScreen(onStartClick = { nav.navigate(Routes.BUILD) })
        }
        composable(Routes.BUILD) {
            BuildScreen(onBack = { nav.popBackStack() })
        }
    }
}
