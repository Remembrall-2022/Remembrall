package com.example.remembrall.login

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.remembrall.MainActivity
import com.example.remembrall.R
import com.example.remembrall.login.req.ReIssueRequest
import com.example.remembrall.login.res.LoginResponse
import com.example.remembrall.login.res.UserInfoResponse
import com.example.remembrall.login.userinfo.LoginData
import com.example.remembrall.login.userinfo.SharedManager
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RealSplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_splash)

        val sharedManager : SharedManager by lazy { SharedManager(this@RealSplashActivity) }
        var authToken = sharedManager.getCurrentUser().accessToken
        var refreshToken = sharedManager.getCurrentUser().refreshToken
        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor()).build()

        // 레트로핏 객체 생성.
        var retrofit = Retrofit.Builder().baseUrl(getString(R.string.SERVER))
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // 로그인 서비스 올리기
        var loginService: UserService = retrofit.create(UserService::class.java)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if (authToken != null){
                loginService.userInfo(authToken).enqueue(object: Callback<UserInfoResponse> {
                    override fun onResponse(
                        call: Call<UserInfoResponse>,
                        response: Response<UserInfoResponse>
                    ) {
                        //토큰이 유효하면 바로 main
                        if(response.body()?.success.toString() == "true"){
                            val intent = Intent(baseContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            try {
                                //유효하지 않으면 재발급
                                val body = response.errorBody()!!.string()
                                val error = Gson().fromJson(body, LoginResponse::class.java)
                                Log.e(ContentValues.TAG, "error - body : $body")
                                if (error.error?.errorName == "EXPIRED_TOKEN"){
                                    loginService.reIssueToken(ReIssueRequest(authToken, refreshToken!!)).enqueue(object : Callback<LoginResponse>{
                                        override fun onResponse(
                                            call: Call<LoginResponse>,
                                            response: Response<LoginResponse>
                                        ) {
                                            if(response.body()?.success.toString() == "true"){
                                                var loginData = response?.body()!!.response!!
                                                val currentUser = LoginData(
                                                    grantType = loginData.grantType.toString(),
                                                    accessToken = loginData.accessToken.toString(),
                                                    refreshToken = loginData.refreshToken.toString()
                                                )
                                                sharedManager.loginCurrentUser(currentUser)
                                                val intent = Intent(baseContext, MainActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            }
                                            else{ //재발급 원활 X -> splash
                                                val body = response.errorBody()!!.string()
                                                val error = Gson().fromJson(body, LoginResponse::class.java)
                                                Log.e(ContentValues.TAG, "error - body : $body")
                                                val intent = Intent(baseContext, SplashActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            }
                                        }
                                        override fun onFailure(
                                            call: Call<LoginResponse>,
                                            t: Throwable
                                        ) {
                                            Log.e("reIssue", t.toString())
                                        }

                                    })

                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                        Log.e("Login", t.toString())
                    }

                })
            }
            val intent = Intent(baseContext, SplashActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000) // 2초

    }
    private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor { message ->
            Log.e(
                "HttpLogging:",
                message + ""
            )
        }
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}