package com.rememberall.remembrall.update

import okhttp3.MultipartBody

data class UpdateDiaryRecyclerViewData(
    val placeLogId: Long,
    val name: String,
    val address: String,
    val longitude: Double,
    val latitude: Double,
    val userLogImgId: Long,
    val imgUrl: String,
    val comment: String
)
