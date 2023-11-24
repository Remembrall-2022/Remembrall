package com.rememberall.remembrall.user

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.rememberall.remembrall.BuildConfig.SERVER
import com.rememberall.remembrall.MainActivity
import com.rememberall.remembrall.R
import com.rememberall.remembrall.user.req.ReIssueRequest
import com.rememberall.remembrall.user.res.Error
import com.rememberall.remembrall.user.res.LoginResponse
import com.rememberall.remembrall.user.res.UserInfoResponse
import com.rememberall.remembrall.user.userinfo.LoginData
import com.rememberall.remembrall.user.userinfo.SharedManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedManager : SharedManager by lazy { SharedManager(this@SplashActivity) }
        val authToken = sharedManager.getCurrentUser().accessToken
        val refreshToken = sharedManager.getCurrentUser().refreshToken
        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor()).build()

        // 레트로핏 객체 생성.
        val retrofit = Retrofit.Builder().baseUrl(SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // 로그인 서비스 올리기
        val loginService: UserService = retrofit.create(UserService::class.java)

        // 로컬에 저장해둔 token을 활용하여 자동 로그인
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if (authToken != null){
                loginService.userInfo(authToken).enqueue(object: Callback<UserInfoResponse> {
                    override fun onResponse(
                        call: Call<UserInfoResponse>,
                        response: Response<UserInfoResponse>
                    ) {
                        // 토큰이 유효하면 바로 mainActivity로 이동
                        if(response.isSuccessful){
                            val intent = Intent(baseContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            try {
                                // 토큰이 유효하지 않으면 재발급
                                val body = response.errorBody()!!.string()
                                val error = Gson().fromJson(body, Error::class.java)
                                Log.e("Token", "error - body : $body")
                                if (error?.errorName == "EXPIRED_TOKEN"){
                                    loginService.reIssueToken(ReIssueRequest(authToken, refreshToken!!)).enqueue(object : Callback<LoginResponse>{
                                        override fun onResponse(
                                            call: Call<LoginResponse>,
                                            response: Response<LoginResponse>
                                        ) {
                                            if(response.isSuccessful){
                                                val loginData = response?.body()!!
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
                                            else{ //재발급 X -> splashActivity로 이동
                                                val body = response.errorBody()!!.string()
                                                Log.e(ContentValues.TAG, "error - body : $body")
                                                val intent = Intent(baseContext, SignUpLoginActivity::class.java)
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
            val intent = Intent(baseContext, StartActivity::class.java)
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