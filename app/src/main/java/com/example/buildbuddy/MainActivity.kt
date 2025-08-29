package com.example.buildbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.buildbuddy.Routes.AppNavigator
import com.example.buildbuddy.ui.theme.BuildBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuildBuddyTheme {
                Surface(modifier = Modifier) {
                    AppNavigator()
                }
            }
        }
    }
}
