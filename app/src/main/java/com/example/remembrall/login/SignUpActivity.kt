package com.example.remembrall.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.remembrall.MainActivity
import com.example.remembrall.R
import com.example.remembrall.databinding.ActivitySignUpBinding
import com.example.remembrall.login.req.AuthCodeRequest
import com.example.remembrall.login.req.SignUpRequest
import com.example.remembrall.login.res.AuthResponse
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var intent =  Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        binding.btnBack.setOnClickListener{
            startActivity(intent)
            finish()
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
                    .setLevel(HttpLoggingInterceptor.Level.HEADERS)
            )
            .build()

        // 레트로핏 객체 생성.
        var retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.SERVER))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // 로그인 서비스 올리기
        var userService: UserService = retrofit.create(UserService::class.java)

        binding.btnAuthRequest.setOnClickListener {
            val email = "parkyena01@sookmyung.ac.kr"
            userService.sendAuthCode(email).enqueue(object: Callback<AuthResponse>{
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    val authCodeRes = response.body()
                    Log.d("SendAuthCode", response.body().toString())
                    if (authCodeRes?.success.toString() == "true"){
                        Log.d("SendAuthCode", authCodeRes?.response?.message.toString())
                    }
                    else {
                        Toast.makeText(this@SignUpActivity, authCodeRes?.error?.errorMessage.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Log.e("SendAuthCode", "통신 실패")
                }
            })
        }

        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val name = binding.etName.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val authCode = binding.etAuthNum.text.toString().trim()
            Log.e("authCode", authCode)
            userService.receiveAuthCode(AuthCodeRequest(email, authCode)).enqueue(object: Callback<AuthResponse>{
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    val authRes = response.body()
                    if (authRes?.success.toString() == "true"){
                        userService.signUpEmail(SignUpRequest(email, password, name)).enqueue(object: Callback<AuthResponse>{
                            override fun onResponse(
                                call: Call<AuthResponse>,
                                response: Response<AuthResponse>
                            ) {
                                val signUpEmailRes = response.body()
                                Log.d("SignUpEmail", signUpEmailRes.toString())
                                if(signUpEmailRes?.success.toString() == "true"){
                                    intent =  Intent(this@SignUpActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                                Log.e("SignUpEmail", "통신 실패")
                            }
                        })
                    }
                    else {
                        Toast.makeText(this@SignUpActivity, authRes?.error?.errorMessage.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Log.e("ReceiveAuthCode", "통신 실패")
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}