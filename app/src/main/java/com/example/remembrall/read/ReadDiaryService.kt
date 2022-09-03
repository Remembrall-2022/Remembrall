package com.example.remembrall.read

import com.example.remembrall.ApiClient
import com.tickaroo.tikxml.annotation.Path
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface ReadDiaryService {
    @GET("/tripLog/{tripLogId}/dateLog/{dateLogId}")
    @Headers("Content-Type: application/json")
    fun getDateDiary(
        @Header("X-AUTH-TOKEN") authToken : String,
        @Path("tripLogId") tripLogId:Int,
        @Path("dateLogId") dateLogId: Int,
    ) : Call<ReadDiaryResponse>

    companion object{
        fun getRetrofitReadDateDiary(authToken: String, tripLogId: Int, dateLogId: Int): Call<ReadDiaryResponse>{
            return ApiClient.create(ReadDiaryService::class.java).getDateDiary(authToken, tripLogId, dateLogId)
        }
    }
}