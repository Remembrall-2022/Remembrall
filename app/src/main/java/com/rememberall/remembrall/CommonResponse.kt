package com.rememberall.remembrall

import com.google.gson.annotations.SerializedName

data class CommonResponse(
    @SerializedName("success") val success: String,
    @SerializedName("response") val response: String,
    @SerializedName("error") val error: Error
)

data class Error(
    @SerializedName("errorName") val errorName: String,
    @SerializedName("errorMessage") val errorMessage: String
)