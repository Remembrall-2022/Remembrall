package com.rememberall.remembrall.read.Triplog.res

data class GetTriplogListResponse(
    val error: Error?,
    val response: List<Response?>?,
    val success: Boolean
)

data class Response(
    val title: String?,
    val tripEndDate: String?,
    val tripLogImgUrl: String?,
    val tripStartDate: String?,
    val triplogId: Int?
)