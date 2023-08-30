package com.rememberall.remembrall.login.res

data class UserNameResponse(
    val error: Error?,
    val response: Message?,
    val success: Boolean?
)

data class Message(
    val message: String?
)