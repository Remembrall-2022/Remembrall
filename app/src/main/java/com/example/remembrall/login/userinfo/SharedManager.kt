package com.example.remembrall.login.userinfo

import android.content.Context
import android.content.SharedPreferences
import com.example.remembrall.login.userinfo.PreferenceHelper.set
import com.example.remembrall.login.userinfo.PreferenceHelper.get

class LoginData(
    val grantType : String?,
    val accessToken : String?,
    val refreshToken : String?
)

class DiaryData(
    val isWritten :  Boolean = false
)

class SharedManager(context : Context) {
    private val prefs: SharedPreferences = PreferenceHelper.defaultPrefs(context)
    fun loginCurrentUser(user : LoginData){
        prefs["grantType"] = user.grantType
        prefs["accessToken"] = user.accessToken
        prefs["refreshToken"] = user.refreshToken
    }
    fun getCurrentUser() : LoginData{
        return LoginData(
            grantType = prefs["grantType"],
            accessToken = prefs["accessToken"],
            refreshToken = prefs["refreshToken"]
        )
    }
    fun logoutCurrentUser(){
        prefs["grantType"] = null
        prefs["accessToken"] = null
        prefs["refreshToken"] = null
    }

    fun writeDiary(){
        prefs["isWritten"] = true
    }

    fun  midNightInitialization(){
        prefs["isWritten"] = false
    }
}