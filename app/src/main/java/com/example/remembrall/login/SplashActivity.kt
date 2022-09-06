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
import com.example.remembrall.constellation.ConstellationActivity
import com.example.remembrall.databinding.ActivityLoginBinding
import com.example.remembrall.databinding.ActivitySplashBinding
import com.example.remembrall.login.SignUpActivity
import com.example.remembrall.login.req.KakaoLoginRequest
import com.example.remembrall.login.res.LoginResponse
import com.example.remembrall.login.userinfo.LoginData
import com.example.remembrall.login.userinfo.SharedManager
import com.example.remembrall.map.MapSearchActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

class SplashActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        var keyHash = Utility.getKeyHash(this)
        // 해시 키 찾기
        Log.e("hash key : ", keyHash)

        val sharedManager = SharedManager(this)

        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor()).build()

        // 레트로핏 객체 생성.
        var retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.SERVER))
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // 로그인 서비스 올리기
        var userService: UserService = retrofit.create(UserService::class.java)

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
                            var loginData = response?.body()!!.response!!
                            val currentUser = LoginData(
                                grantType = loginData.grantType.toString(),
                                accessToken = loginData.accessToken.toString(),
                                refreshToken = loginData.refreshToken.toString()
                            )
                            sharedManager.loginCurrentUser(currentUser)
                            Toast.makeText(this@SplashActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                            intent =  Intent(this@SplashActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            startActivity(intent)
                            finish()
                        }
                        else{

                        }
                    }
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.e("LoginKakao", "통신 실패")
                    }
                })
            }
        }
        binding.btnLoginKakao.setOnClickListener{
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@SplashActivity)) {
                UserApiClient.instance.loginWithKakaoTalk(this@SplashActivity) { token, error ->
                    if (error != null) {
                        Log.e(ContentValues.TAG, "카카오톡으로 로그인 실패", error)
                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(this@SplashActivity, callback = callback)
                    } else if (token != null) {
                        Log.i(ContentValues.TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                        userService.loginKakao(KakaoLoginRequest(token.accessToken)).enqueue(object:
                            Callback<LoginResponse> {
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
                                    Toast.makeText(this@SplashActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                                    intent =  Intent(this@SplashActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                Log.e("LoginKakao", "통신 실패")
                            }

                        })
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this@SplashActivity, callback = callback)
            }
        }
        // 이메일로 로그인
        binding.btnLoginEmail.setOnClickListener {
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        // 회원가입
        binding.btnLoginSignUp.setOnClickListener {
            intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 로그인 스킵
        binding.button.setOnClickListener{
            startActivity(intent)
            finish()
        }

        // 별자리 지도 만들기
        binding.buttonConstellation.setOnClickListener {
            intent = Intent(this, ConstellationActivity::class.java)
            startActivity(intent)
            finish()
        }

        // map search
        binding.buttonMap.setOnClickListener {
            intent = Intent(this, MapSearchActivity::class.java)
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