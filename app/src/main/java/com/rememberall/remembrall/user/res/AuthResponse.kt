package com.rememberall.remembrall.user.res

data class AuthResponse(
    val response: AuthCodeResponse,
    val error: Error?
)

data class AuthCodeResponse(
    val message: String,
)