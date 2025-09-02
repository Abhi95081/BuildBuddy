package com.example.buildbuddy.net

import retrofit2.http.*

interface ForgeApi {
    @POST("build")
    suspend fun createBuild(@Body body: BuildRequest): BuildCreated

    @GET("status/{id}")
    suspend fun getStatus(@Path("id") id: String): BuildStatusDto

    // You won’t call download via Retrofit (it’s a binary).
    // Use a normal URL: BASE_URL + "download/{id}"
}
