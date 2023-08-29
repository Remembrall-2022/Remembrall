package com.rememberall.remembrall.write

import okhttp3.MultipartBody

data class WriteDiaryRecyclerViewData(
    val place: String,
    var image: String,
    val comment: String,
    var imgFile: MultipartBody.Part,
    var x: Double,
    var y: Double
)
