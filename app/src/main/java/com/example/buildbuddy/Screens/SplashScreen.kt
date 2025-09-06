package com.example.buildbuddy

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(true) {
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = tween(800, easing = {
                OvershootInterpolator(3f).getInterpolation(it)
            })
        )
        scale.animateTo(1f, tween(400))
        rotation.animateTo(360f, tween(1000))
        alpha.animateTo(1f, tween(1200))

        delay(3000)
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }

    // Animated gradient background
    val infiniteTransition = rememberInfiniteTransition()
    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val animatedGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0D47A1), Color(0xFF42A5F5), Color(0xFF00C853)),
        start = Offset(0f, gradientShift),
        end = Offset(gradientShift, 0f)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(animatedGradient)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Pulsing ripple effect behind logo
            PulsingRipple()

            // Rotating, glowing logo
            Image(
                painter = painterResource(id = R.drawable.applogo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale.value)
                    .rotate(rotation.value)
                    .shadowGlow()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Shimmering App Title
            ShimmerText(
                text = "BuildBuddy",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Shimmering subtitle tagline
            ShimmerText(
                text = "Turn GitHub repos into APKs ✨",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}

@Composable
fun PulsingRipple() {
    val infiniteTransition = rememberInfiniteTransition()
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(
        modifier = Modifier
            .size(220.dp)
            .scale(rippleScale)
            .alpha(rippleAlpha)
    ) {
        drawCircle(color = Color.White.copy(alpha = 0.3f))
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
        start = Offset(shimmerX, 0f),
        end = Offset(shimmerX + 200f, 200f)
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

// Glow effect modifier
fun Modifier.shadowGlow(
    color: Color = Color.White,
    radius: Float = 40f,
    alpha: Float = 0.6f
): Modifier = this.then(
    Modifier.drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint()
            paint.color = color.copy(alpha = alpha)
            paint.asFrameworkPaint().apply {
                isAntiAlias = true
                maskFilter = android.graphics.BlurMaskFilter(radius, android.graphics.BlurMaskFilter.Blur.NORMAL)
            }
            canvas.drawCircle(center, size.minDimension / 2.2f, paint)
        }
    }
)
