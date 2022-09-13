package com.rememberall.remembrall.dongdong

import com.rememberall.remembrall.dongdong.res.AttendanceResponse
import com.rememberall.remembrall.dongdong.res.DongDongResponse
import retrofit2.Call
import retrofit2.http.*

interface DongDongService {

    @GET("/dongdong")
    @Headers("Content-Type: application/json")
    fun getDongDong(
        @Header("X-AUTH-TOKEN") authToken : String
    ) : Call<DongDongResponse>

    @POST("/dongdong/attendance")
    @Headers("Content-Type: application/json")
    fun attendance(
        @Header("X-AUTH-TOKEN") authToken : String
    ) : Call<DongDongResponse>

    @GET("/dongdong/attendance")
    @Headers("Content-Type: application/json")
    fun getAttendance(
        @Header("X-AUTH-TOKEN") authToken : String
    ) : Call<AttendanceResponse>

}