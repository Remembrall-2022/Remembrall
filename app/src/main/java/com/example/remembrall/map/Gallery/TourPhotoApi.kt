package com.example.remembrall.map.Gallery

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TourPhotoApi {
    @GET("/galleryList")
    fun getGalleryList(
        @Query("MobileOS") MobileOS: String,
        @Query("MobileApp") MobileApp: String,
        @Query("serviceKey") numOfRows: String,
    ) : Call<GalleryResponse>
}