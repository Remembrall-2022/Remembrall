package com.example.remembrall.login

import com.example.remembrall.login.req.*
import com.example.remembrall.login.res.*
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

    @POST("/signup/kakao")
    fun signUpKakao(
        @Body kakaoLoginRequest: KakaoLoginRequest
    ) : Call <LoginResponse>

    @POST("/login/kakao")
    fun loginKakao(
        @Body kakaoLoginRequest: KakaoLoginRequest
    ) : Call <LoginResponse>

    @GET("/user/info")
    fun userInfo(
        @Header("X-AUTH-TOKEN") authToken : String
    ) : Call <UserInfoResponse>

    @POST("/user/name")
    fun changeUserName(
        @Header("X-AUTH-TOKEN") authToken : String,
        @Body name : String
    ) : Call <UserNameResponse>

    @POST("/user/password/request")
    fun requestPassword(
        @Body email : String
    ) : Call <AuthResponse>

    @POST("/user/password/valid")
    fun validPassword(
        @Body authCodeRequest: AuthCodeRequest
    ) : Call <AuthResponse>

    @POST("/user/password")
    fun changePassword(
        @Header("X-AUTH-TOKEN") authToken : String,
        @Body name : String
    ) : Call <UserNameResponse>

    @DELETE("/user/signout")
    fun signOut(
        @Header("X-AUTH-TOKEN") authToken : String
    ) : Call <AuthResponse>

}