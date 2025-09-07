package com.example.buildbuddy.Screens

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buildbuddy.BuildViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildScreen(onBack: () -> Unit, vm: BuildViewModel = viewModel()) {
    val context = LocalContext.current
    val state by vm.state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Gradient background
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF0D47A1), Color(0xFF42A5F5))
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("New Build", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Card container
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.12f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = state.repoUrl,
                            onValueChange = vm::onRepoChange,
                            label = { Text("GitHub repo URL", color = Color.White) },
                            placeholder = { Text("https://github.com/owner/repo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.LightGray,
                                cursorColor = Color.White,
                                focusedLabelColor = Color.White
                            )
                        )

                        Spacer(Modifier.height(18.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (state.repoUrl.isBlank()) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Please enter a repository URL")
                                        }
                                        return@Button
                                    }
                                    if (!state.isBuilding) vm.startBuild()
                                },
                                enabled = !state.isBuilding,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(50)
                            ) {
                                if (state.isBuilding) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Building...")
                                } else {
                                    Text("Start Build")
                                }
                            }

                            OutlinedButton(
                                onClick = vm::reset,
                                enabled = !state.isBuilding,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("Reset")
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            "Status: ${state.status.uppercase()}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                        state.message?.let {
                            Spacer(Modifier.height(6.dp))
                            Text(it, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        }

                        Spacer(Modifier.height(20.dp))

                        state.downloadUrl?.let { url ->
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = { openInBrowser(context, url) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
                                ) {
                                    Text("✨ Open Download Page ✨", color = Color.Black)
                                }

                                OutlinedButton(
                                    onClick = { enqueueDownload(context, url) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                                ) {
                                    Text("⬇️ Download APK (Direct)")
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Tip: For private repos, extend ViewModel to send a GitHub token with the request.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}

private fun openInBrowser(context: Context, url: String) {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

private fun enqueueDownload(context: Context, url: String) {
    val dm = context.getSystemService<DownloadManager>() ?: return
    val req = DownloadManager.Request(Uri.parse(url))
        .setTitle("BuildBuddy APK")
        .setDescription("Downloading APK")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "buildbuddy-app.apk")
        .setMimeType("application/vnd.android.package-archive")
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)
    dm.enqueue(req)
}
