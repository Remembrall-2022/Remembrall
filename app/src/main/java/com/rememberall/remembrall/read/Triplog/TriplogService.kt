package com.rememberall.remembrall.read.Triplog

import com.rememberall.remembrall.read.Triplog.req.TriplogRequest
import com.rememberall.remembrall.read.Triplog.res.CreateTriplogResponse
import com.rememberall.remembrall.read.Triplog.res.DeleteTriplogResponse
import com.rememberall.remembrall.read.Triplog.res.GetTriplogListResponse
import retrofit2.Call
import retrofit2.http.*

interface TriplogService {
    @POST("/tripLog/new")
    fun createTripLog(
        @Header("X-AUTH-TOKEN") authToken : String,
        @Body triplogRequest : TriplogRequest
    ) : Call<CreateTriplogResponse>

    @GET("/tripLog/list")
    fun getTripLogList(
        @Header("X-AUTH-TOKEN") authToken : String
    ): Call<GetTriplogListResponse>

    @POST("/tripLog/{id}")
    fun updateTripLog(
        @Header("X-AUTH-TOKEN") authToken: String,
        @Path("id") triplogId : Long,
        @Body triplogRequest : TriplogRequest
    ): Call<CreateTriplogResponse>

    @DELETE("/tripLog/{id}")
    fun deleteTripLog(
        @Header("X-AUTH-TOKEN") authToken: String,
        @Path("id") triplogId : Long
    ): Call<DeleteTriplogResponse>
}