package com.example.buildbuddy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.compose.*
import com.example.buildbuddy.ui.theme.BuildBuddyTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuildBuddyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BuildBuddyApp()
                }
            }
        }
    }
}

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

/**
 * Build screen:
 * - Accepts repo url
 * - Starts a simulated build flow (replace with real network calls later)
 * - Shows status and a download/open button when build completes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var repoUrl by rememberSaveable { mutableStateOf("") }
    var status by rememberSaveable { mutableStateOf("idle") } // idle | queued | building | success | failed
    var message by rememberSaveable { mutableStateOf<String?>(null) }
    var downloadUrl by rememberSaveable { mutableStateOf<String?>(null) }
    var isBuilding by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Build") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = repoUrl,
                onValueChange = { repoUrl = it },
                label = { Text("GitHub repo URL") },
                placeholder = { Text("https://github.com/owner/repo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { /* dismiss keyboard */ })
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        // Kick off build (simulated). Replace this block with real API call.
                        if (repoUrl.isBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("Please enter a repository URL") }
                            return@Button
                        }
                        if (isBuilding) {
                            scope.launch { snackbarHostState.showSnackbar("A build is already running") }
                            return@Button
                        }

                        // Simulated build flow
                        isBuilding = true
                        status = "queued"
                        message = "Queued for build"
                        downloadUrl = null

                        scope.launch {
                            try {
                                delay(1000L) // queued
                                status = "building"
                                message = "Cloning repository and building..."
                                delay(2500L) // building
                                // simulate success 85% of time
                                val success = (0..100).random() < 85
                                if (success) {
                                    status = "success"
                                    message = "Build complete"
                                    // In real app you'll get actual download link from server
                                    downloadUrl = simulateDownloadUrl(repoUrl)
                                } else {
                                    status = "failed"
                                    message = "Build failed: Gradle error (simulated)"
                                }
                            } catch (e: Exception) {
                                status = "failed"
                                message = "Build failed: ${e.localizedMessage}"
                            } finally {
                                isBuilding = false
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isBuilding) "Building..." else "Start Build")
                }

                OutlinedButton(
                    onClick = {
                        repoUrl = ""
                        status = "idle"
                        message = null
                        downloadUrl = null
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text("Status: ${status.uppercase()}", style = MaterialTheme.typography.bodyLarge)
            message?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            downloadUrl?.let { url ->
                Button(
                    onClick = {
                        // Open the download URL in browser (or use direct download logic later)
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open Download / Install")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Helpful hint
            Text(
                text = "Tip: For real builds, replace the simulated build flow with calls to your ForgeServer API. The server should return a download link which this screen will use to download/install the APK.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/** Simulate a plausible download URL derived from the repo name */
private fun simulateDownloadUrl(repoUrl: String): String {
    // If it's a GitHub URL, craft a fake artifact link — in real usage the server will provide this.
    val cleaned = repoUrl.trim().removeSuffix("/")
    val repoPart = cleaned.substringAfterLast("/", "app")
    return "https://example.com/artifacts/$repoPart/latest/app-debug.apk"
}

@Preview(showBackground = true)
@Composable
fun BuildBuddyPreview() {
    BuildBuddyTheme {
        BuildBuddyApp()
    }
}
