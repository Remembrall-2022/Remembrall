package com.rememberall.remembrall.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.rememberall.remembrall.BuildConfig.SERVER
import com.rememberall.remembrall.MainActivity
import com.rememberall.remembrall.databinding.ActivityLoginBinding
import com.rememberall.remembrall.user.req.LoginRequest
import com.rememberall.remembrall.user.res.Error
import com.rememberall.remembrall.user.res.LoginResponse
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
class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@LoginActivity

        var intent =  Intent(this, SignUpLoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor()).build()

        // 레트로핏 객체 생성
        val retrofit = Retrofit.Builder()
            .baseUrl(SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // 로그인 서비스 올리기
        val loginService: UserService = retrofit.create(UserService::class.java)

        // 현재 유저 정보
        val sharedManager = SharedManager(this)

        binding.btnJoin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            loginService.loginEmail(LoginRequest(email, password)).enqueue(object: Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if(response.isSuccessful){
                        Log.d("Login", "로그인 성공 " + response.code())
                        val loginData = response.body()!!
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
                            val error = Gson().fromJson(body, Error::class.java)
                            Log.e("Login", "error - body : $body")
                            binding.tvLoginError.text = error?.errorMessage
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("Login", "로그인 실패 " + t.toString())
                    Toast.makeText(applicationContext,"error : 로그인 실패", Toast.LENGTH_SHORT).show()
                    binding.tvLoginError.text = t.toString()
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
                "HttpLogging",
                message + ""
            )
        }
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    }
    override fun onBackPressed() {
        super.onBackPressed()
    }
}
