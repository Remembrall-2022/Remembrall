package com.example.remembrall.write

import com.example.remembrall.ApiClient
import retrofit2.Call
import retrofit2.http.*

interface WriteDiaryService {
    @POST("/tripLog/{tripLogId}/dateLog/new")
    @Headers("Accept: application/json")
    fun postSaveDiary(
        @Header("X-AUTH-TOKEN") authToken : String,
        @Path("tripLogId") tripLogId:Int,
        @Body saveRequestDto: WriteDiaryRequest
    ) : Call<WriteDiaryResponse>

    @GET("/question/random")
    @Headers("Content-Type: application/json", "Accept-Type: application/json")
    fun getQuestion(
        @Header("X-AUTH-TOKEN") authToken : String,
    ) : Call<GetQuestionResponse>

    @GET("/questions")
    @Headers("application-Type: application/json")
    fun getAllQuestion(
        @Header("X-AUTH-TOKEN") authToken : String,
    ) : Call<GetAllQuestionResponse>

    companion object{
        fun getRetrofitSaveDiary(authToken: String, tripLogId: Int, saveRequestDto: WriteDiaryRequest):Call<WriteDiaryResponse>{
            return ApiClient.create(WriteDiaryService::class.java).postSaveDiary(authToken, tripLogId, saveRequestDto)
        }
        fun getRetrofitRefreshQuestion(authToken: String): Call<GetQuestionResponse> {
            return ApiClient.create(WriteDiaryService::class.java).getQuestion(authToken)
        }
        fun getRetrofitAllQuestion(authToken: String): Call<GetAllQuestionResponse>{
            return ApiClient.create(WriteDiaryService::class.java).getAllQuestion(authToken)
        }

    }
}