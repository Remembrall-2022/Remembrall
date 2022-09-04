package com.example.remembrall.login

import android.content.ContentValues
import android.content.ContentValues.TAG
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
import com.example.remembrall.login.res.LoginResponse
import com.example.remembrall.login.userinfo.LoginData
import com.example.remembrall.login.userinfo.SharedManager
import com.google.gson.JsonObject
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
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
            val email = binding.etEmail.text.toString().trim()
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

        val sharedManager = SharedManager(this)

        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(ContentValues.TAG, "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i(ContentValues.TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
                userService.signUpKakao(token.accessToken).enqueue(object:Callback<LoginResponse>{
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful){
                            var loginData = response?.body()!!.response!!
                            val currentUser = LoginData(
                                grantType = loginData.grantType.toString(),
                                accessToken = loginData.accessToken.toString(),
                                refreshToken = loginData.refreshToken.toString()
                            )
                            sharedManager.loginCurrentUser(currentUser)
                            Toast.makeText(this@SignUpActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                            intent =  Intent(this@SignUpActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            startActivity(intent)
                            finish()
                        }
                    }
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.e("SignUpKakao", "통신 실패")
                    }

                })
            }
        }

        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
        binding.btnKakaoSignUp.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@SignUpActivity)) {
                UserApiClient.instance.loginWithKakaoTalk(this@SignUpActivity) { token, error ->
                    if (error != null) {
                        Log.e(ContentValues.TAG, "카카오톡으로 로그인 실패", error)

                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(this@SignUpActivity, callback = callback)
                    } else if (token != null) {
                        Log.i(ContentValues.TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                        userService.signUpKakao(token.accessToken).enqueue(object:Callback<LoginResponse>{
                            override fun onResponse(
                                call: Call<LoginResponse>,
                                response: Response<LoginResponse>
                            ) {
                                if (response.isSuccessful){
                                    var loginData = response?.body()!!.response!!
                                    val currentUser = LoginData(
                                        grantType = loginData.grantType.toString(),
                                        accessToken = loginData.accessToken.toString(),
                                        refreshToken = loginData.refreshToken.toString()
                                    )
                                    sharedManager.loginCurrentUser(currentUser)
                                    Toast.makeText(this@SignUpActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                                    intent =  Intent(this@SignUpActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                Log.e("SignUpKakao", "통신 실패")
                            }

                        })
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this@SignUpActivity, callback = callback)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}