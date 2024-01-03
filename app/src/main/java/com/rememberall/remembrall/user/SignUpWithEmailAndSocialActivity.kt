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
import com.kakao.sdk.user.UserApiClient
import com.rememberall.remembrall.ApiClient
import com.rememberall.remembrall.MainActivity
import com.rememberall.remembrall.databinding.ActivitySignUpWithEmailAndSocialBinding
import com.rememberall.remembrall.user.req.KakaoLoginRequest
import com.rememberall.remembrall.user.res.Error
import com.rememberall.remembrall.user.res.LoginResponse
import com.rememberall.remembrall.user.userinfo.LoginData
import com.rememberall.remembrall.user.userinfo.SharedManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SignUpWithEmailAndSocialActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpWithEmailAndSocialBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpWithEmailAndSocialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // user 서비스 올리기
        val userService: UserService = ApiClient.create(UserService::class.java)

        // 현재 유저 정보
        val sharedManager = SharedManager(this)

        // 이메일로 회원가입
        binding.btnSignUpWithEmail.setOnClickListener {
            val intent = Intent(this, SignUpWithEmailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }

        // 카카오 계정으로 회원가입
        binding.btnSignUpWithKakao.setOnClickListener {
            // 카카오계정으로 로그인 공통 callback 구성
            // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("Login", "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.i("Login", "카카오계정으로 로그인 성공 ${token.accessToken}")
                    userService.signUpKakao(KakaoLoginRequest(token.accessToken)).enqueue(object :
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
                                Toast.makeText(this@SignUpWithEmailAndSocialActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                                intent =  Intent(this@SignUpWithEmailAndSocialActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                startActivity(intent)
                                finish()
                            }
                            else{
                                try {
                                    val body = response.errorBody()!!.string()
                                    val error = Gson().fromJson(body, Error::class.java)
                                    Log.e("SignUp", "error - body : $body")
                                    Toast.makeText(this@SignUpWithEmailAndSocialActivity, error?.errorMessage, Toast.LENGTH_SHORT).show()
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
            binding.btnSignUpWithKakao.setOnClickListener {
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@SignUpWithEmailAndSocialActivity)) {
                    UserApiClient.instance.loginWithKakaoTalk(this@SignUpWithEmailAndSocialActivity) { token, error ->
                        if (error != null) {
                            Log.e("Login", "카카오톡으로 로그인 실패", error)
                            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                return@loginWithKakaoTalk
                            }
                            // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                            UserApiClient.instance.loginWithKakaoAccount(this@SignUpWithEmailAndSocialActivity, callback = callback)
                        } else if (token != null) {
                            Log.i(ContentValues.TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                            userService.signUpKakao(KakaoLoginRequest(token.accessToken)).enqueue(object:
                                Callback<LoginResponse> {
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
                                        Toast.makeText(this@SignUpWithEmailAndSocialActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                                        intent =  Intent(this@SignUpWithEmailAndSocialActivity, MainActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else {
                                        try {
                                            val body = response.errorBody()!!.string()
                                            val error = Gson().fromJson(body, Error::class.java)
                                            Log.e(ContentValues.TAG, "error - body : $body")
                                            Toast.makeText(this@SignUpWithEmailAndSocialActivity, error?.errorMessage, Toast.LENGTH_SHORT).show()
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
                    UserApiClient.instance.loginWithKakaoAccount(this@SignUpWithEmailAndSocialActivity, callback = callback)
                }
            }
        }
        // 뒤로가기
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, SignUpLoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}