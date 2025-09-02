package com.example.buildbuddy.net

// POST /build request
data class BuildRequest(
    val repoUrl: String,
    val branch: String? = "main",
    val token: String? = null
)

// POST /build response
data class BuildCreated(
    val buildId: String
)

// GET /status/{id} response (mirrors server)
data class BuildStatusDto(
    val id: String,
    val repo: String,
    val branch: String?,
    val status: String,            // queued | cloning | building | success | failed
    val message: String?,
    val artifact_path: String?,    // server local path (optional)
    val started_at: Double?,
    val finished_at: Double?
)
