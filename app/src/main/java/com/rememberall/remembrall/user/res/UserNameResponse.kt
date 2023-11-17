package com.rememberall.remembrall.user.res

data class UserNameResponse(
    val error: Error?,
    val response: Message?,
    val success: Boolean?
)

data class Message(
    val message: String?
)