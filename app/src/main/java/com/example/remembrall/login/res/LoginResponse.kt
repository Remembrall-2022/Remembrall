package com.example.remembrall.login.res

data class LoginResponse(
    val error: Error?,
    val response: Response,
    val success: Boolean
)

data class Response(
    val accessToken: String,
    val grantType: String,
    val refreshToken: String
)

data class Error(
    val errorName : String,
    val errorMessage : String
    )