package com.example.buildbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.buildbuddy.Routes.AppNavigator
import com.example.buildbuddy.ui.theme.BuildBuddyTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuildBuddyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigator()
                }
            }
        }
    }
}

// ----------------- SPLASH SCREEN -------------------
@Composable
fun SplashScreen(navController: NavHostController) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(true) {
        // Logo bounce animation
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        delay(2000) // wait before navigating
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.applogo), // change logo here
            contentDescription = "App Logo",
            modifier = Modifier
                .size(150.dp)
                .scale(scale.value)
        )
    }
}
