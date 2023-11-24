package com.rememberall.remembrall.update

import com.rememberall.remembrall.ApiClient
import com.rememberall.remembrall.CommonResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UpdateDiaryService {
    @POST("/dateLog/{dateLogId}")
    @Headers("Accept: application/json")
    fun postUpdateDiary(
        @Header("X-AUTH-TOKEN") authToken : String,
        @Path("dateLogId") dateLogId:Long,
    ) : Call<CommonResponse>

    @POST("/userImg/{userLogImgId}")
    @Headers("Accept: application/json")
    fun postUpdateImage(
        @Header("X-AUTH-TOKEN") authToken : String,
        @Path("userLogImgId") userLogImgId:Int,
        @Part file: MultipartBody.Part
    ): Call<CommonResponse>

    @POST("/dateLog/{dateLogId}/placeLog/new")
    @Headers("Accept: application/json")
    fun postAddPlaceLog(
        @Header("X-AUTH-TOKEN") authToken : String,
        @Path("dateLogId") dateLogId: Long,
        @Part ("placeLogSaveRequestDto") placeLogSaveRequestDto: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<CommonResponse>

    @DELETE("/placeLog/{placeLogId}")
    @Headers("Accept: application/json")
    fun deletePlaceLog(
        @Header("X-AUTH-TOKEN") authToken : String,
        @Path("placeLogId") placeLogId: Int
    ): Call<CommonResponse>

    companion object {
        fun getRetrofitUpdateDateLog(authToken: String, dateLogId: Long): Call<CommonResponse> {
            return ApiClient.create(UpdateDiaryService::class.java).postUpdateDiary(authToken, dateLogId)
        }
        fun getRetrofitUpdateImage(authToken: String, userLogImgId: Int, file: MultipartBody.Part):Call<CommonResponse>{
            return ApiClient.create(UpdateDiaryService::class.java).postUpdateImage(authToken,userLogImgId,file)
        }
        fun getRetrofitPostAddPlaceLog(authToken: String, dateLogId: Long, placeLogSaveRequestDto: RequestBody, file: MultipartBody.Part): Call<CommonResponse>{
            return ApiClient.create(UpdateDiaryService::class.java).postAddPlaceLog(authToken,dateLogId,placeLogSaveRequestDto, file)
        }
        fun getRetrofitDeletePlaceLog(authToken: String, placeLogId: Int): Call<CommonResponse>{
            return ApiClient.create(UpdateDiaryService::class.java).deletePlaceLog(authToken, placeLogId)
        }
    }
}