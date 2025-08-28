package com.example.buildbuddy.Screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
