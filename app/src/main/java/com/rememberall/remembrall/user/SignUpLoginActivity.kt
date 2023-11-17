package com.rememberall.remembrall.user

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.rememberall.remembrall.BuildConfig.SERVER
import com.rememberall.remembrall.MainActivity
import com.rememberall.remembrall.databinding.ActivitySignUpLoginBinding
import com.rememberall.remembrall.user.req.KakaoLoginRequest
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

class SignUpLoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        val keyHash = Utility.getKeyHash(this)

        // 해시 키 찾기
        Log.e("hash key : ", keyHash)

        // 현재 유저 정보
        val sharedManager = SharedManager(this)

        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor()).build()

        // 레트로핏 객체 생성.
        val retrofit = Retrofit.Builder()
            .baseUrl(SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // 로그인 서비스 올리기
        val userService: UserService = retrofit.create(UserService::class.java)

        // 로그인하기
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            userService.loginEmail(LoginRequest(email, password)).enqueue(object: Callback<LoginResponse> {
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
                        intent =  Intent(this@SignUpLoginActivity, MainActivity::class.java)
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


        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(ContentValues.TAG, "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i(ContentValues.TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
                userService.loginKakao(KakaoLoginRequest(token.accessToken)).enqueue(object:
                    Callback<LoginResponse> {
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
                            Toast.makeText(this@SignUpLoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                            intent =  Intent(this@SignUpLoginActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            Toast.makeText(this@SignUpLoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.e("LoginKakao", "통신 실패")
                        Toast.makeText(this@SignUpLoginActivity, "로그인 통신 실패", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
        binding.btnLoginKakao.setOnClickListener{
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@SignUpLoginActivity)) {
                UserApiClient.instance.loginWithKakaoTalk(this@SignUpLoginActivity) { token, error ->
                    if (error != null) {
                        Log.e(ContentValues.TAG, "카카오톡으로 로그인 실패", error)
                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(this@SignUpLoginActivity, callback = callback)
                    } else if (token != null) {
                        Log.i(ContentValues.TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                        userService.loginKakao(KakaoLoginRequest(token.accessToken)).enqueue(object: Callback<LoginResponse> {
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
                                    Toast.makeText(this@SignUpLoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                                    intent =  Intent(this@SignUpLoginActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                Log.e("LoginKakao", "통신 실패")
                                Toast.makeText(this@SignUpLoginActivity, "로그인 통신 실패", Toast.LENGTH_SHORT).show()
                            }

                        })
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this@SignUpLoginActivity, callback = callback)
            }
        }
        // 뒤로가기
        binding.btnBack.setOnClickListener {
            intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            finish()
        }
        // 회원가입
        binding.btnLoginSignUp.setOnClickListener {
            intent = Intent(this, SignUpActivity::class.java)
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
}