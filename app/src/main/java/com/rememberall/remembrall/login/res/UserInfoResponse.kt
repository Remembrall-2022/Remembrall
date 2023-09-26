package com.rememberall.remembrall.login.res
data class UserInfoResponse(
    val alarmAgree: Boolean?,
    val authType: String?,
    val email: String?,
    val name: String?,
    val termAgree: Boolean?
)