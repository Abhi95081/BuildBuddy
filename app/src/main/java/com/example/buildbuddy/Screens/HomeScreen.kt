package com.example.buildbuddy.Screens

import android.R.attr.scaleX
import android.R.attr.scaleY
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onStartClick: () -> Unit) {
    // Futuristic background (scientist / cosmic feel)
    val backgroundGradient = Brush.verticalGradient(
        listOf(Color(0xFF000000), Color(0xFF0F2027), Color(0xFF2C5364))
    )

    // Pulsating glow animation for button
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            tween(1200, easing = LinearEasing),
            RepeatMode.Reverse
        )
    )

    // Shimmer effect for subtitle
    val shimmerColors = listOf(
        Color(0x55FFFFFF),
        Color(0x22FFFFFF),
        Color(0x55FFFFFF)
    )
    val shimmerTranslate = infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            tween(2500, easing = LinearEasing)
        )
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("🔬 BuildBuddy Lab", color = Color.Cyan, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Futuristic Title
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(2000)),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "⚛️ Quantum Builder",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Cyan
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Glassmorphic Card
                Card(
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(20.dp, RoundedCornerShape(30.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.07f)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .padding(28.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    shimmerColors
                                ),
                                alpha = 0.1f
                            )
                    ) {
                        Text(
                            text = "Upload code → Run experiments → Extract your APK 🧪",
                            fontSize = 18.sp,
                            color = Color.White,
                            lineHeight = 26.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

                // Floating Pulsating Button
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .graphicsLayer {
                                scaleX = pulse
                                scaleY = pulse
                            }
                            .clip(CircleShape)
                            .background(Color.Cyan.copy(alpha = 0.15f))
                    )
                    Button(
                        onClick = onStartClick,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(65.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Cyan,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            "🚀 Initiate Build",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
