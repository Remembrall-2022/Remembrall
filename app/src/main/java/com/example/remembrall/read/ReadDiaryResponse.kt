package com.example.remembrall.read

import com.example.remembrall.write.WriteDiaryRequest
import com.google.gson.annotations.SerializedName

data class ReadDiaryResponse(
    @SerializedName("success") val success: String,
    @SerializedName("response") val response: Response,
    @SerializedName("error") val error: String
){
    data class Response(
        @SerializedName("date") val date: String,
        @SerializedName("weatherInfo") val weatherInfo: Weather,
        @SerializedName("question") val question: Question?,
        @SerializedName("answer") val answer: String,
        @SerializedName("placeLogList") val placeLogList: List<PlaceLogList>,
    ){
        data class Weather(
            @SerializedName("weather") val weather: String,
            @SerializedName("degree") val degree: Int
        )

        data class Question(
            @SerializedName("id") val id: Int?,
            @SerializedName("questionName") val questionName: String?
        )

        data class PlaceLogList(
            @SerializedName("placeLogId") val placeLogId: Int,
            @SerializedName("placeLogIndex") val placeLogIndex: Int,
            @SerializedName("place") val place: Place,
            @SerializedName("userLogImg") val userLogImg: UserLogImg,
            @SerializedName("comment") val comment: String,
        ){
            data class Place(
                @SerializedName("id") val id: Int,
                @SerializedName("name") val name: String,
                @SerializedName("address") val address: String,
                @SerializedName("longitude") val longitude: Double,
                @SerializedName("latitude") val latitude: Double,
            )
            data class UserLogImg(
                @SerializedName("userLogImgId") val userLogImgId: Int,
                @SerializedName("imgUrl") val imgUrl: String
            )
        }
    }
}

data class ReadTripLogResponse(
    @SerializedName("success") val success: String,
    @SerializedName("response") val response: Response,
    @SerializedName("error") val error: String
) {
    data class Response(
        @SerializedName("triplogId") val triplogId: Long,
        @SerializedName("title") val title: String,
        @SerializedName("tripStartDate") val tripStartDate: String,
        @SerializedName("tripEndDate") val anstripEndDatewer: String,
        @SerializedName("placeLogIdList") val placeLogIdList: List<Long>,
    )
}

data class ReadAllDiaryResponse(
    @SerializedName("success") val success: String,
    @SerializedName("response") val response: Response,
    @SerializedName("error") val error: String
) {
    data class Response(
        @SerializedName("triplogId") val triplogId: Long,
        @SerializedName("title") val title: String,
        @SerializedName("tripStartDate") val tripStartDate: String,
        @SerializedName("tripEndDate") val tripEndDate: String,
        @SerializedName("dateLogResponseDtoList") val dateLogResponseDtoList: List<DateLogResponseDtoList>,
    ){
        data class DateLogResponseDtoList(
            @SerializedName("date") val date: String,
            @SerializedName("weatherInfo") val weatherInfo: Weather,
            @SerializedName("question") val question: Question,
            @SerializedName("answer") val answer: String,
            @SerializedName("placeLogList") val placeLogList: List<PlaceLogList>,
        ){
            data class Weather(
                @SerializedName("weather") val weather: String,
                @SerializedName("degree") val degree: Int
            )
            data class Question(
                @SerializedName("id") val id: Int,
                @SerializedName("questionName") val questionName: String
            )
            data class PlaceLogList(
                @SerializedName("placeLogId") val placeLogId: Int,
                @SerializedName("placeLogIndex") val placeLogIndex: Int,
                @SerializedName("place") val place: Place,
                @SerializedName("userLogImg") val userLogImg: UserLogImg,
                @SerializedName("comment") val comment: String,
            ){
                data class Place(
                    @SerializedName("id") val id: Int,
                    @SerializedName("name") val name: String,
                    @SerializedName("address") val address: String,
                    @SerializedName("longitude") val longitude: Double,
                    @SerializedName("latitude") val latitude: Double,
                )
                data class UserLogImg(
                    @SerializedName("userLogImgId") val userLogImgId: Int,
                    @SerializedName("imgUrl") val imgUrl: String
                )
            }
        }
    }
}