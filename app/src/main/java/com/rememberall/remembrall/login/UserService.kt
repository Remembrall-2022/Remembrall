package com.rememberall.remembrall.login

import com.rememberall.remembrall.login.req.*
import com.rememberall.remembrall.login.res.*
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @POST("/login/email")
    @Headers("Content-Type: application/json")
    fun loginEmail(
        @Body loginRequest: LoginRequest
    ) : Call<LoginV2Response>

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
    fun requestPasswordAuthCode(
        @Body passwordAuthCodeRequest: PasswordAuthCodeRequest
    ) : Call <AuthResponse>

    @POST("/user/password/valid")
    fun validPasswordAuthCode(
        @Body authCodeRequest: AuthCodeRequest
    ) : Call <AuthResponse>

    @POST("/user/password")
    fun changePassword(
        @Body password : String
    ) : Call  <AuthResponse>

    @DELETE("/user/signout")
    fun signOut(
        @Header("X-AUTH-TOKEN") authToken : String
    ) : Call <AuthResponse>

    @POST("/reissue")
    fun reIssueToken(
        @Body reIssueRequest: ReIssueRequest
    ) : Call <LoginResponse>

}