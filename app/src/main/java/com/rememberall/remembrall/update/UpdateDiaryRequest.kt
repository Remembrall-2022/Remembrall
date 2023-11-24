package com.rememberall.remembrall.update

import com.google.gson.annotations.SerializedName

data class UpdateDate(
    @SerializedName("LocalDate") val LocalDate: String
)

data class UpdateDateLog(
    @SerializedName("date") val date: String,
    @SerializedName("weatherInfo") val weatherInfo: Weather,
    @SerializedName("questionId") val questionId: Long,
    @SerializedName("answer") val answer: String,
    @SerializedName("placeLogList") val placeLogList: ArrayList<UpdatePlaceLogList>,
) {
    data class Weather(
        @SerializedName("weather") val weather: String,
        @SerializedName("degree") val degree: Int
    )
}

data class UpdatePlaceLogList(
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

