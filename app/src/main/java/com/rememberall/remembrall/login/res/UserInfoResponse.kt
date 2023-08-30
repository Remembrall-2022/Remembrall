package com.rememberall.remembrall.login.res

data class UserInfoResponse(
    val error: Error?,
    val response: UserResponse?,
    val success: Boolean?
)

data class UserResponse(
    val alarmAgree: Boolean?,
    val authType: String?,
    val email: String?,
    val name: String?,
    val termAgree: Boolean?
)