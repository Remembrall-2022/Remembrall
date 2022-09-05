package com.example.remembrall.setting

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.remembrall.databinding.DialogSignOutBinding
import com.example.remembrall.login.SplashActivity
import com.example.remembrall.login.UserService
import com.example.remembrall.login.res.AuthResponse
import com.example.remembrall.login.userinfo.SharedManager
import com.kakao.sdk.user.UserApiClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignOutDialog (
    context: Context
) : Dialog(context){ // 뷰를 띄워야 하므로 Dialog 클래스는 context를 인자로 받는다.
    private lateinit var binding: DialogSignOutBinding

    val client = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor()).build()

    // TODO : url key에서 들고오기
    var retrofit = Retrofit.Builder()
        .baseUrl("http://ec2-13-124-98-176.ap-northeast-2.compute.amazonaws.com:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    var userService : UserService = retrofit.create(UserService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSignOutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() = with(binding) {
        binding.btnSignOut.setOnClickListener {
            val sharedManager : SharedManager by lazy { SharedManager(context) }
            var authToken = sharedManager.getCurrentUser().accessToken
            userService.signOut(authToken).enqueue(object :
                Callback<AuthResponse> {
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    Log.e("Signout", response.body().toString())
                    if (response.body()?.success.toString() == "true") {
                        Toast.makeText(context, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                            if (error != null) {
                                Log.e(TAG, "토큰 정보 보기 실패", error)
                            } else if (tokenInfo != null) {
                                Log.i(
                                    TAG, "토큰 정보 보기 성공" +
                                            "\n회원번호: ${tokenInfo.id}" +
                                            "\n만료시간: ${tokenInfo.expiresIn} 초"
                                )
                                UserApiClient.instance.unlink { error ->
                                    if (error != null) {
                                        Log.e(TAG, "연결 끊기 실패", error)
                                    } else {
                                        Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                                        var intent = Intent(context, SplashActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                        context.startActivity(intent)
                                        dismiss()
                                    }
                                }
                            }
                            var intent = Intent(context, SplashActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            context.startActivity(intent)
                            dismiss()
                        }
                    }
                }
                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                        Toast.makeText(context, "회원 탈퇴 실패", Toast.LENGTH_SHORT).show()
                    }
                })
            binding.btnClose.setOnClickListener {
                Toast.makeText(context,"탈퇴 취소", Toast.LENGTH_SHORT).show()
                dismiss()
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
}
