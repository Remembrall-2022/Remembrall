package com.rememberall.remembrall.update

import android.net.Uri
import okhttp3.MultipartBody

data class UpdateDiaryRecyclerViewData(
    val placeLogId: Long,
    val name: String,
    val address: String,
    val longitude: Double,
    val latitude: Double,
    val userLogImgId: Long,
    val imgUrl: Uri,
    val comment: String
)
