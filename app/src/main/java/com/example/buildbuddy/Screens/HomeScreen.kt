package com.example.buildbuddy.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Home screen - welcoming UI */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onStartClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("BuildBuddy") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏗️ BuildBuddy",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Paste a GitHub Android repo, build it remotely, and install the APK on your device — all from your phone.",
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(28.dp))
            Button(
                onClick = onStartClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start a New Build")
            }
        }
    }
}
