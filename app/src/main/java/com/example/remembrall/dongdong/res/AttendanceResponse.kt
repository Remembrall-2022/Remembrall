package com.example.remembrall.dongdong.res

data class AttendanceResponse(
    val error: Error?,
    val response: Message?,
    val success: Boolean?
)

data class Message(
    val message: String
)