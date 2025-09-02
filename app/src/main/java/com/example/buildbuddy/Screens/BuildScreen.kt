package com.example.buildbuddy.Screens

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Build") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                value = state.repoUrl,
                onValueChange = vm::onRepoChange,
                label = { Text("GitHub repo URL") },
                placeholder = { Text("https://github.com/owner/repo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { /* dismiss */ })
            )

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val scope = rememberCoroutineScope()

                Button(
                    onClick = {
                        if (state.repoUrl.isBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("Please enter a repository URL") }
                            return@Button
                        }
                        if (!state.isBuilding) vm.startBuild()
                    },
                    enabled = !state.isBuilding,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (state.isBuilding) "Building..." else "Start Build")
                }


                OutlinedButton(
                    onClick = vm::reset,
                    enabled = !state.isBuilding,
                    modifier = Modifier.weight(1f)
                ) { Text("Reset") }
            }

            Spacer(Modifier.height(18.dp))

            Text("Status: ${state.status.uppercase()}", style = MaterialTheme.typography.bodyLarge)
            state.message?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(16.dp))

            state.downloadUrl?.let { url ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { openInBrowser(context, url) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Open Download Page") }

                    // Optional: direct download via DownloadManager (saves to app's Downloads)
                    OutlinedButton(
                        onClick = { enqueueDownload(context, url) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Download APK (Direct)") }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Tip: For private repos, extend ViewModel to send a GitHub token with the request.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun show(host: SnackbarHostState, msg: String) {
    LaunchedEffect(msg) {
        host.showSnackbar(msg)
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
