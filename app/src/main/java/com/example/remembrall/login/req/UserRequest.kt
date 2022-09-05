package com.example.remembrall.login.req

data class LoginRequest (
    var email : String,
    var password :String)

data class SignUpRequest (
    var email : String,
    var password : String,
    val name : String
    )

data class AuthCodeRequest(
    val email: String,
    val authCode : String
)

data class KakaoLoginRequest(
    var kakaoToken : String
)

data class UserRequest(
    var accessToken : String
)
