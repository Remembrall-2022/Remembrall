package com.rememberall.remembrall.login

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.rememberall.remembrall.BuildConfig.SERVER
import com.rememberall.remembrall.MainActivity
import com.rememberall.remembrall.databinding.ActivitySignUpBinding
import com.rememberall.remembrall.login.req.AuthCodeRequest
import com.rememberall.remembrall.login.req.KakaoLoginRequest
import com.rememberall.remembrall.login.req.SignUpRequest
import com.rememberall.remembrall.login.res.AuthResponse
import com.rememberall.remembrall.login.res.Error
import com.rememberall.remembrall.login.res.LoginResponse
import com.rememberall.remembrall.login.userinfo.LoginData
import com.rememberall.remembrall.login.userinfo.SharedManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.Timer
import kotlin.concurrent.timer

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    var timerTask: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        binding.btnBack.setOnClickListener {
            startActivity(intent)
            finish()
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(
                httpLoggingInterceptor()
            )
            .build()

        // 레트로핏 객체 생성.
        val retrofit = Retrofit.Builder()
            .baseUrl(SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // user 서비스 올리기
        val userService: UserService = retrofit.create(UserService::class.java)

        // 현재 유저 정보
        val sharedManager = SharedManager(this)

        // 인증 코드 전송
        binding.btnAuthRequest.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            timerTask?.cancel()
            userService.sendAuthCode(email).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    if (response.isSuccessful) {
                        var time = 300000
                        Log.d("AuthCode", "인증 코드 전송 성공 " + response.body().toString())
                        binding.llAuthStatus.visibility = View.VISIBLE
                        binding.llAuthVerify.visibility = View.VISIBLE
                        timerTask = timer(period = 1000) {
                            time -= 1000 // period=10, 0.01초마다 time를 1씩 감소
                            val min = time / 60000 // time/100, 나눗셈의 몫 (초 부분)
                            val sec = (time % 60000) / 1000    // time%100, 나눗셈의 나머지 (밀리초 부분)
                            // UI조작을 위한 메서드
                            runOnUiThread {
                                binding.tvAuthStatus.text = "$min:$sec"
                            }
                            if (time == 0) {
                                timerTask?.cancel()
                            }
                        }
                    } else {
                        try {
                            val body = response.errorBody()!!.string()
                            val error = Gson().fromJson(body, Error::class.java)
                            Log.e("AuthCode", "send error - body : $body")
                            timerTask?.cancel()
                            binding.tvAuthStatus.text = error?.errorMessage
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Log.e("AuthCode", " send 통신 실패")
                    timerTask?.cancel()
                    binding.tvAuthStatus.text = "통신 실패"
                }
            })
        }

        // 인증 코드 검증
        binding.btnAuthVerify.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val authCode = binding.etAuthNum.text.toString().trim()
            userService.receiveAuthCode(AuthCodeRequest(email, authCode))
                .enqueue(object : Callback<AuthResponse> {
                    override fun onResponse(
                        call: Call<AuthResponse>,
                        response: Response<AuthResponse>
                    ) {
                        if (response.isSuccessful) {
                            timerTask?.cancel()
                            binding.tvAuthStatus.text = "인증 성공"
                        } else {
                            try {
                                val body = response.errorBody()!!.string()
                                val error = Gson().fromJson(body, Error::class.java)
                                Log.e("AuthCode", "verify error - body : $body")
                                timerTask?.cancel()
                                binding.tvAuthStatus.text = error?.errorMessage
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                        Log.e("AuthCode", "통신 실패")
                        timerTask?.cancel()
                        binding.tvAuthStatus.text = "통신 실패"
                    }

                })
        }

        // 회원가입
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val name = binding.etName.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            userService.signUpEmail(SignUpRequest(email, password, name))
                .enqueue(object : Callback<AuthResponse> {
                    override fun onResponse(
                        call: Call<AuthResponse>,
                        response: Response<AuthResponse>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@SignUpActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                            intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            try {
                                val body = response.errorBody()!!.string()
                                val error = Gson().fromJson(body, Error::class.java)
                                Log.e("SignUp", "error - body : $body")
                                binding.tvSignUpError.text = error?.errorMessage
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                        Log.e("SignUpEmail", "통신 실패")
                    }
                })
        }
    // 카카오계정으로 로그인 공통 callback 구성
    // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("Login", "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i("Login", "카카오계정으로 로그인 성공 ${token.accessToken}")
                userService.signUpKakao(KakaoLoginRequest(token.accessToken)).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful){
                            val loginData = response?.body()!!
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
                        else{
                            try {
                                val body = response.errorBody()!!.string()
                                val error = Gson().fromJson(body, Error::class.java)
                                Log.e("SignUp", "error - body : $body")
                                binding.tvSignUpError.text = error?.errorMessage
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
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
                        Log.e("Login", "카카오톡으로 로그인 실패", error)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(this@SignUpActivity, callback = callback)
                    } else if (token != null) {
                        Log.i(ContentValues.TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                        userService.signUpKakao(KakaoLoginRequest(token.accessToken)).enqueue(object:Callback<LoginResponse>{
                            override fun onResponse(
                                call: Call<LoginResponse>,
                                response: Response<LoginResponse>
                            ) {
                                if (response.isSuccessful){
                                    var loginData = response?.body()!!
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
                                else {
                                    try {
                                        val body = response.errorBody()!!.string()
                                        val error = Gson().fromJson(body, Error::class.java)
                                        Log.e(TAG, "error - body : $body")
                                        Toast.makeText(this@SignUpActivity, error?.errorMessage, Toast.LENGTH_SHORT).show()
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
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
        private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
            val interceptor = HttpLoggingInterceptor { message ->
                Log.e(
                    "HttpLogging:",
                    message + ""
                )
            }
            return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        override fun onDestroy() {
            super.onDestroy()
        }

        override fun onBackPressed() {
            super.onBackPressed()
        }
    }