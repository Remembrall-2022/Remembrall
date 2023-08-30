package com.rememberall.remembrall.write

import com.google.gson.annotations.SerializedName

data class GetQuestionResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("category") val category: String,
    @SerializedName("questionName") val questionName: String
)
