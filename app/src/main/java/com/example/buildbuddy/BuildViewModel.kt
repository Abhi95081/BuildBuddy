package com.example.buildbuddy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buildbuddy.data.BuildRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class BuildUiState(
    val repoUrl: String = "",
    val status: String = "idle",
    val message: String? = null,
    val isBuilding: Boolean = false,
    val downloadUrl: String? = null,
)

class BuildViewModel(
    private val repo: BuildRepository = BuildRepository()
) : ViewModel() {

    var state = androidx.compose.runtime.mutableStateOf(BuildUiState())
        private set

    private var job: Job? = null

    fun onRepoChange(value: String) {
        state.value = state.value.copy(repoUrl = value)
    }

    fun reset() {
        job?.cancel()
        state.value = BuildUiState()
    }

    fun startBuild(branch: String? = "main", token: String? = null) {
        val url = state.value.repoUrl.trim()
        if (url.isBlank() || state.value.isBuilding) return

        state.value = state.value.copy(
            status = "queued",
            message = "Queued for build",
            isBuilding = true,
            downloadUrl = null
        )

        job = viewModelScope.launch {
            try {
                val buildId = repo.startBuild(url, branch, token)
                state.value = state.value.copy(status = "cloning", message = "Cloning repository...")

                val final = repo.waitForCompletion(buildId)
                if (final.status == "success") {
                    state.value = state.value.copy(
                        status = "success",
                        message = final.message ?: "Build complete",
                        downloadUrl = repo.downloadUrl(buildId),
                        isBuilding = false
                    )
                } else {
                    state.value = state.value.copy(
                        status = "failed",
                        message = final.message ?: "Build failed",
                        isBuilding = false,
                        downloadUrl = null
                    )
                }
            } catch (e: Exception) {
                state.value = state.value.copy(
                    status = "failed",
                    message = "Error: ${e.localizedMessage}",
                    isBuilding = false,
                    downloadUrl = null
                )
            }
        }
    }
}
