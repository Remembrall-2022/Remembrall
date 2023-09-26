package com.rememberall.remembrall.login.res

data class AuthResponse(
    val response: AuthCodeResponse,
    val error: Error?
)

data class AuthCodeResponse(
    val message: String,
)