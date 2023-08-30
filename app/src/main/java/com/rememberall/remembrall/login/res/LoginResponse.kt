package com.rememberall.remembrall.login.res

data class LoginResponse(
    val response: Response?,
    val error: Error?
)

data class Response(
    val accessToken: String?,
    val grantType: String?,
    val refreshToken: String?
)

data class Error(
    val errorName : String?,
    val errorMessage : String?
    )

data class LoginV2Response(
    val accessToken: String?,
    val grantType: String?,
    val refreshToken: String?
)