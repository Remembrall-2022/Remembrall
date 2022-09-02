package com.example.remembrall.login

import com.example.remembrall.login.req.AuthCodeRequest
import com.example.remembrall.login.req.LoginRequest
import com.example.remembrall.login.req.SignUpRequest
import com.example.remembrall.login.res.AuthCodeResponse
import com.example.remembrall.login.res.AuthResponse
import com.example.remembrall.login.res.LoginResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    @POST("/login/email")
    fun loginEmail(
        @Body loginRequest: JsonObject
    ) : Call<LoginResponse>

    @POST("/signup/email")
    fun signUpEmail(
        @Body signUpRequest : SignUpRequest
    ) : Call <AuthResponse>

    @POST("/signup/email/authcode/send")
    fun sendAuthCode(
        @Body email : String
    ) : Call <AuthResponse>

    @GET("/signup/email/authcode")
    fun receiveAuthCode(
        @Query("email") email: String,
        @Query("authCode") authCode: String
    ) : Call <AuthResponse>


}