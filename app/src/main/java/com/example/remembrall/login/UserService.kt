package com.example.remembrall.login

import com.example.remembrall.login.req.AuthCodeRequest
import com.example.remembrall.login.req.LoginRequest
import com.example.remembrall.login.req.SignUpRequest
import com.example.remembrall.login.res.AuthResponse
import com.example.remembrall.login.res.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @POST("/login/email")
    @Headers("Content-Type: application/json")
    fun loginEmail(
        @Body loginRequest: LoginRequest
    ) : Call<LoginResponse>

    @POST("/signup/email")
    fun signUpEmail(
        @Body signUpRequest : SignUpRequest
    ) : Call <AuthResponse>

    @POST("/signup/email/authcode/send")
    fun sendAuthCode(
        @Body email : String
    ) : Call <AuthResponse>

    @POST("/signup/email/authcode")
    fun receiveAuthCode(
        @Body authCodeRequest : AuthCodeRequest
    ) : Call <AuthResponse>


}