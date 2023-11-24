package com.rememberall.remembrall

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import com.rememberall.remembrall.BuildConfig.SERVER
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

object ApiClient {
    private const val BASE_URL =
        SERVER

    private val client = OkHttpClient.Builder()
//        .addInterceptor(AppInterceptor())
        .addInterceptor(
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
                .setLevel(HttpLoggingInterceptor.Level.BODY)
                .setLevel(HttpLoggingInterceptor.Level.HEADERS)
        )
        .build()

    // 레트로핏 객체 생성.
    var retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    //리트로핏 객체 생성
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

//    class AppInterceptor : Interceptor {
//        @Throws(IOException::class)
//        override fun intercept(chain: Interceptor.Chain) : Response = with(chain) {
//            val sharedManager : SharedManager by lazy { SharedManager() }
//            var authToken = sharedManager.getCurrentUser().accessToken
//            val accessToken = GlobalApplication.prefs.getString("accessToken", "") // ViewModel에서 지정한 key로 JWT 토큰을 가져온다.
//
//            val newRequest = request().newBuilder()
//                .addHeader("Authorization","Bearer $accessToken") // 헤더에 authorization라는 key로 JWT 를 넣어준다.
//                .build()
//            proceed(newRequest)
//        }
//    }
}