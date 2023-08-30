package com.rememberall.remembrall.map.Gallery

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TourRecommendApi {
    @GET("/B551011/KorService/locationBasedList")
    fun getTourList(
        @Query("MobileOS") MobileOS: String,
        @Query("MobileApp") MobileApp: String,
        @Query("serviceKey") numOfRows: String,
        @Query("mapX") mapX: String,
        @Query("mapY") mapY : String,
        @Query("radius") radius: String,
    ) : Call<TourRecommendResponse>
}