package com.rememberall.remembrall.dongdong.res

data class DongDongResponse(
    val error: Error?,
    val response: Response?,
    val success: Boolean?
)

data class Response(
    val dongdongImgUrl: String?,
    val exp: Int?,
    val maxExp: Int?,
    val level: Int?,
    val point: Int?,
    val userId: Int?
)