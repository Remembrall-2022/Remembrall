package com.example.remembrall.write

import android.net.Uri
import android.widget.ImageView
import okhttp3.MultipartBody
import java.io.File

data class WriteDiaryRecyclerViewData(
    val place: String,
    var image: String,
    val coment: String,
    var imgFile: MultipartBody.Part
)
