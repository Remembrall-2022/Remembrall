package com.rememberall.remembrall.login

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rememberall.remembrall.BuildConfig.SERVER
import com.rememberall.remembrall.MainActivity
import com.rememberall.remembrall.databinding.ActivityLoginBinding
import com.rememberall.remembrall.login.req.LoginRequest
import com.rememberall.remembrall.login.res.LoginResponse
import com.rememberall.remembrall.login.userinfo.LoginData
import com.rememberall.remembrall.login.userinfo.SharedManager
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    var context: Context? = null
    fun getLoginContext(): Context? {
        return context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@LoginActivity

        var intent =  Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor()).build()

        // 레트로핏 객체 생성.
        var retrofit = Retrofit.Builder()
            .baseUrl(SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // 로그인 서비스 올리기
        var loginService: UserService = retrofit.create(UserService::class.java)

        // 현재 유저 정보
        val sharedManager = SharedManager(this)

        binding.btnJoin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            loginService.loginEmail(LoginRequest(email, password)).enqueue(object: Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("Login", t.toString())
                    Toast.makeText(applicationContext,"error : 로그인 실패", Toast.LENGTH_SHORT)
                }
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val login = response
                    Log.d("Login:", "login success : "+response.code())

                    if(response.isSuccessful){
                        var loginData = login?.body()!!.response!!
                        val currentUser = LoginData(
                            grantType = loginData.grantType.toString(),
                            accessToken = loginData.accessToken.toString(),
                            refreshToken = loginData.refreshToken.toString()
                        )
                        sharedManager.loginCurrentUser(currentUser)
                        intent =  Intent(this@LoginActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        startActivity(intent)
                        finishAffinity()
                    }
                    else {
                        try {
                            val body = response.errorBody()!!.string()
                            val error = Gson().fromJson(body, LoginResponse::class.java)
                            Log.e(TAG, "error - body : $body")
                            binding.tvLoginError.text = error.error?.errorMessage
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        }

        binding.btnBack.setOnClickListener{
            startActivity(intent)
            finish()
        }
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
    override fun onBackPressed() {
        super.onBackPressed()
    }
}
