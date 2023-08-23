package com.rememberall.remembrall
import android.app.Application
import com.rememberall.remembrall.R
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate(){
        super.onCreate()
        // kakao sdk 초기화
        KakaoSdk.init(this, getString(R.string.NATIVE_APP_KEY))
        prefs = PreferenceUtil(applicationContext)
    }
}