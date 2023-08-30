package com.rememberall.remembrall.read.Triplog.res

data class GetTriplogListResponse(
    val triplogId: Int?,
    val title: String?,
    val tripStartDate: String?,
    val tripEndDate: String?,
    val tripLogImgUrl: String?,
)