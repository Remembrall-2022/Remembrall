package com.rememberall.remembrall.write

import com.google.gson.annotations.SerializedName

data class GetQuestionResponse(
    @SerializedName("success") val success: String,
    @SerializedName("response") val response: Response,
    @SerializedName("error") val error: String
){
    data class Response(
        @SerializedName("id") val id: Long,
        @SerializedName("questionName") val questionName: String
    )
}

data class GetAllQuestionResponse(
    @SerializedName("success") val success: String,
    @SerializedName("response") val response: List<Response>,
    @SerializedName("error") val error: String
) {
    data class Response(
        @SerializedName("id") val id: Long,
        @SerializedName("questionName") val questionName: String
    )
}