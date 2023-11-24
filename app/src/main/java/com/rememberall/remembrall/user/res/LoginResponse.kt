package com.rememberall.remembrall.user.res

data class LoginResponse(
    val accessToken: String?,
    val grantType: String?,
    val refreshToken: String?
)

data class Error(
    val errorName : String,
    val errorMessage : String?
    )