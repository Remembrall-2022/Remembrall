package com.rememberall.remembrall.update

import android.net.Uri
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class UpdateDiaryRecyclerViewData(
    var placeLogId: Int,
    var placeLogIndex: Int,
    var name: String,
    var address: String,
    var longitude: Double,
    var latitude: Double,
    var userLogImgId: Int,
    var comment: String,
    var image: String,
    var imgFile: MultipartBody.Part
)

data class PlaceLogList(
    @SerializedName("placeLogId") val placeLogId: Int,
    @SerializedName("placeLogIndex") val placeLogIndex: Int,
    @SerializedName("place") val place: Place,
    @SerializedName("comment") val comment: String,
) {
    data class Place(
        @SerializedName("name") val name: String,
        @SerializedName("address") val address: String,
        @SerializedName("longitude") val longitude: Double,
        @SerializedName("latitude") val latitude: Double,
    )
}