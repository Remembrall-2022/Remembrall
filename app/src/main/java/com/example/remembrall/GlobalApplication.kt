package com.example.remembrall
import android.app.Application
import com.example.remembrall.R
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate(){
        super.onCreate()
        // kakao sdk 초기화
        KakaoSdk.init(this, getString(R.string.NATIVE_APP_KEY))
    }
}