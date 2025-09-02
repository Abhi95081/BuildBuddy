package com.example.buildbuddy.data

import com.example.buildbuddy.net.*
import kotlinx.coroutines.delay

class BuildRepository(
    private val api: ForgeApi = NetworkModule.api
) {
    suspend fun startBuild(repoUrl: String, branch: String? = "main", token: String? = null): String {
        val created = api.createBuild(BuildRequest(repoUrl, branch, token))
        return created.buildId
    }

    /** Poll server for status until terminal state. Returns final status DTO. */
    suspend fun waitForCompletion(buildId: String, pollMs: Long = 2000L, timeoutMs: Long = 15 * 60 * 1000): BuildStatusDto {
        val start = System.currentTimeMillis()
        while (true) {
            val s = api.getStatus(buildId)
            when (s.status) {
                "success", "failed" -> return s
            }
            if (System.currentTimeMillis() - start > timeoutMs) {
                return s.copy(status = "failed", message = "Timed out waiting for build")
            }
            delay(pollMs)
        }
    }

    fun downloadUrl(buildId: String): String = NetworkConfig.BASE_URL + "download/$buildId"
}
