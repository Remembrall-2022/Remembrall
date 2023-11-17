package com.rememberall.remembrall.user.req

data class LoginRequest (
    var email : String,
    var password :String)

data class SignUpRequest (
    var email : String,
    var password : String,
    val name : String
    )

data class PasswordAuthCodeRequest(
    val email: String
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

data class ReIssueRequest(
    var accessToken : String,
    var refreshToken : String
)
