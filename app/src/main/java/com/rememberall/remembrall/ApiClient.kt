package com.rememberall.remembrall

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import com.rememberall.remembrall.BuildConfig.SERVER

object ApiClient {
    private const val BASE_URL =
        SERVER

    private val client = OkHttpClient.Builder()
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
}