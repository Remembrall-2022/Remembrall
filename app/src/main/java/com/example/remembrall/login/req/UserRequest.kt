package com.example.remembrall.login.req

data class LoginRequest (
    var email : String,
    var password :String)

data class SignUpRequest (
    var email : String,
    var password : String,
    val name : String
    )