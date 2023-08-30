package com.rememberall.remembrall.write

import com.google.gson.annotations.SerializedName

data class WriteDiaryRequest(
    @SerializedName("date") val date: String,
    @SerializedName("weatherInfo") val weatherInfo: Weather,
    @SerializedName("questionId") val questionId: Int,
    @SerializedName("answer") val answer: String,
    @SerializedName("placeLogList") val placeLogList: List<PlaceLogList>,
//    @SerializedName("ImgList") val ImgList: List<File>
){
    data class Weather(
        @SerializedName("weather") val weather: String,
        @SerializedName("degree") val degree: Int
    )

    data class PlaceLogList(
        @SerializedName("placeInfo") val placeInfo: PlaceInfo,
        @SerializedName("comment") val comment: String,
        @SerializedName("imgName") val imgName: String
    ){
        data class PlaceInfo(
            @SerializedName("placeId") val placeId: Int,
            @SerializedName("name") val name: String,
            @SerializedName("address") val address: String,
            @SerializedName("longitude") val longitude: Double,
            @SerializedName("latitude") val latitude: Double,
        )
    }
}
