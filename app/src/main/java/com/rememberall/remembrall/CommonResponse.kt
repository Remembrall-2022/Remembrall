package com.rememberall.remembrall

import com.google.gson.annotations.SerializedName

data class CommonResponse(
    @SerializedName("success") val success: String,
    @SerializedName("response") val response: Message,
    @SerializedName("error") val error: Error
)

data class Message(
    @SerializedName("message") val response: String
)

data class Error(
    @SerializedName("errorName") val errorName: String,
    @SerializedName("errorMessage") val errorMessage: String
)

