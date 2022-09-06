package com.example.remembrall.read

import com.example.remembrall.ApiClient
import retrofit2.Call
import retrofit2.http.*

interface ReadDiaryService {
    @GET("/tripLog/{tripLogId}/dateLog/{dateLogId}")
    @Headers("Content-Type: application/json")
    fun getDateDiary(
        @Header("X-AUTH-TOKEN") authToken : String,
        @Path("tripLogId") tripLogId:Long,
        @Path("dateLogId") dateLogId: Long,
    ) : Call<ReadDiaryResponse>

    @GET("/tripLog/{id}/onlyId")
    fun getTripLog(
        @Header("X-AUTH-TOKEN") authToken : String,
        @Path("id") id: Long
    ): Call<ReadTripLogResponse>

    @GET("/tripLog/{id}")
    fun getAlldiary(
        @Header("X-AUTH-TOKEN") authToken : String,
        @Path("id") id: Long
    ):Call<ReadAllDiaryResponse>

    companion object{
        fun getRetrofitReadDateDiary(authToken: String, tripLogId: Long, dateLogId: Long): Call<ReadDiaryResponse>{
            return ApiClient.create(ReadDiaryService::class.java).getDateDiary(authToken, tripLogId, dateLogId)
        }
        fun getRetrofitReadTripLog(authToken: String, id: Long): Call<ReadTripLogResponse>{
            return ApiClient.create(ReadDiaryService::class.java).getTripLog(authToken, id)
        }
        fun getRetrofitAllDiary(authToken: String, id: Long):Call<ReadAllDiaryResponse>{
            return ApiClient.create(ReadDiaryService::class.java).getAlldiary(authToken,id)
        }
    }
}