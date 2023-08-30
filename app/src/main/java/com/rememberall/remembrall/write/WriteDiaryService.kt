package com.rememberall.remembrall.write

import com.rememberall.remembrall.ApiClient
import com.rememberall.remembrall.CommonResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface WriteDiaryService {
    @Multipart
    @POST("/tripLog/{tripLogId}/dateLog/new")
    @Headers("Accept: application/json")
    fun postSaveDiary(
        @Header("X-AUTH-TOKEN") authToken : String,
        @Path("tripLogId") tripLogId:Long,
        @Part ("saveRequestDto") saveRequestDto: RequestBody,
        @Part file: List<MultipartBody.Part>
    ) : Call<CommonResponse>

    @GET("/question/random")
    @Headers("Content-Type: application/json", "Accept-Type: application/json")
    fun getQuestion(
        @Header("X-AUTH-TOKEN") authToken : String,
    ) : Call<GetQuestionResponse>

    @GET("/questions")
    @Headers("application-Type: application/json")
    fun getAllQuestion(
        @Header("X-AUTH-TOKEN") authToken : String,
    ) : Call<List<GetQuestionResponse>>

    companion object{
        fun getRetrofitSaveDiary(authToken: String, tripLogId: Long, saveRequestDto: RequestBody, file: List<MultipartBody.Part>):Call<CommonResponse>{
            return ApiClient.create(WriteDiaryService::class.java).postSaveDiary(authToken, tripLogId, saveRequestDto, file)
        }
        fun getRetrofitRefreshQuestion(authToken: String): Call<GetQuestionResponse> {
            return ApiClient.create(WriteDiaryService::class.java).getQuestion(authToken)
        }
        fun getRetrofitAllQuestion(authToken: String): Call<List<GetQuestionResponse>>{
            return ApiClient.create(WriteDiaryService::class.java).getAllQuestion(authToken)
        }
    }
}