package com.example.buildbuddy

import android.os.Build
import android.view.animation.OvershootInterpolator
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.DONUT)
@Composable
fun SplashScreen(navController: NavHostController) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(true) {
        // Sequential animations
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = tween(800, easing = {
                OvershootInterpolator(3f).getInterpolation(it)
            })
        )
        scale.animateTo(1f, tween(400))
        rotation.animateTo(360f, tween(1000))
        alpha.animateTo(1f, tween(1200))

        delay(2500)
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }

    // Background gradient
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D47A1), Color(0xFF42A5F5))
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Rotating & Scaling Logo
            Image(
                painter = painterResource(id = R.drawable.applogo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale.value)
                    .rotate(rotation.value)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Shimmering App Title
            ShimmerText(
                text = "BuildBuddy",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}

@Composable
fun ShimmerText(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerX by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = LinearEasing),
            RepeatMode.Restart
        )
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(Color.Gray, Color.White, Color.Gray),
        start = androidx.compose.ui.geometry.Offset(shimmerX, 0f),
        end = androidx.compose.ui.geometry.Offset(shimmerX + 200f, 200f)
    )

    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        style = MaterialTheme.typography.titleLarge.copy(
            brush = shimmerBrush
        ),
        modifier = modifier
    )
}
