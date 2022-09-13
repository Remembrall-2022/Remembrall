package com.rememberall.remembrall.read.Triplog.res

data class DeleteTriplogResponse(
    val error: Error?,
    val response: Message?,
    val success: Boolean?
)

data class Message(
    val message: String
)