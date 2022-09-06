package com.example.remembrall.read.Triplog

import com.example.remembrall.read.Triplog.req.TriplogRequest
import com.example.remembrall.read.Triplog.res.CreateTriplogResponse
import com.example.remembrall.read.Triplog.res.DeleteTriplogResponse
import com.example.remembrall.read.Triplog.res.GetTriplogListResponse
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

    @DELETE("/tripLog/{id}")
    fun deleteTripLog(
        @Header("X-AUTH-TOKEN") authToken: String,
        @Path("id") triplogId : Long
    ): Call<DeleteTriplogResponse>
}