package com.rememberall.remembrall.login.res

data class AuthResponse(
    val error: Error?,
    val response: AuthCodeResponse,
    val success: Boolean
)

data class AuthCodeResponse(
    val message: String,
)