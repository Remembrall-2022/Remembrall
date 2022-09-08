package com.example.remembrall.setting

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.remembrall.BuildConfig.SERVER
import com.example.remembrall.databinding.DialogChangeNameBinding
import com.example.remembrall.databinding.DialogChangePasswordBinding
import com.example.remembrall.databinding.DialogTriplogCreateBinding
import com.example.remembrall.login.LoginActivity
import com.example.remembrall.login.UserService
import com.example.remembrall.login.req.AuthCodeRequest
import com.example.remembrall.login.req.PasswordAuthCodeRequest
import com.example.remembrall.login.req.SignUpRequest
import com.example.remembrall.login.res.AuthResponse
import com.example.remembrall.login.res.LoginResponse
import com.example.remembrall.login.res.UserInfoResponse
import com.example.remembrall.login.res.UserNameResponse
import com.example.remembrall.login.userinfo.SharedManager
import com.example.remembrall.read.Triplog.TriplogService
import com.example.remembrall.read.Triplog.req.TriplogRequest
import com.example.remembrall.read.Triplog.res.CreateTriplogResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import kotlin.concurrent.timer

class ChangePasswordDialog (
    context: Context, email: String
) : Dialog(context){ // 뷰를 띄워야 하므로 Dialog 클래스는 context를 인자로 받는다.
    private lateinit var binding: DialogChangePasswordBinding
    var email = email


    val client = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor()).build()
    var retrofit = Retrofit.Builder()
        .baseUrl(SERVER)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
    var userService : UserService = retrofit.create(UserService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() = with(binding) {

        binding.btnAuthRequest.setOnClickListener {
            userService.requestPasswordAuthCode(PasswordAuthCodeRequest(email)).enqueue(object : Callback<AuthResponse>{
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    val authCodeRes = response.body()
                    Log.d("SendAuthCode", response.body().toString())
                    if (authCodeRes?.success.toString() == "true"){
                        Log.d("SendAuthCode", authCodeRes?.response?.message.toString())
                        binding.llTimer.visibility = View.VISIBLE
//                        var time = 300000
//                        val context = this
//                        var timerTask : Timer?= null
//                        timerTask = timer(period = 1000) {
//                            time = time - 1000
//                            val min = time / 60000
//                            val sec = (time % 60000) / 1000
//
//
//
//                            binding.tvTimeMinute.text = "$min"	// TextView 세팅
//                            binding.tvTimeSecond.text = ":$sec"// Textview 세팅
//
//                            if(time == 0){
//                                timerTask?.cancel()
//                            }
//                        }
                    }
                    else {
                        try {
                            val body = response.errorBody()!!.string()
                            val error = Gson().fromJson(body, LoginResponse::class.java)
                            Log.e(ContentValues.TAG, "error - body : $body")
                            binding.tvError.text = error.error?.errorMessage
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Log.e("SendAuthCode", "통신 실패")
                }

            })
        }
        binding.btnChangePassword.setOnClickListener {
            val newPassword = binding.etNewPassword.text.toString().trim()
            var authCode = binding.etAuthcode.text.toString().trim()

            userService.validPasswordAuthCode(AuthCodeRequest(email, authCode)).enqueue(object: Callback<AuthResponse>{
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    val authRes = response.body()
                    if (authRes?.success.toString() == "true") {
                        userService.changePassword(newPassword)
                            .enqueue(object : Callback<AuthResponse> {
                                override fun onResponse(
                                    call: Call<AuthResponse>,
                                    response: Response<AuthResponse>
                                ) {
                                    val authCodeRes = response.body()
                                    Log.d("changePassword", authCodeRes.toString())
                                    if (authCodeRes?.success.toString() == "true") {
                                        Toast.makeText(context, "비밀번호가 변경되었습니다", Toast.LENGTH_SHORT).show()
                                        dismiss()
                                    }
                                }
                                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                                    Log.e("changePassword", "통신 실패")
                                }
                            })
                    } else {
                        try {
                            val body = response.errorBody()!!.string()
                            val error = Gson().fromJson(body, LoginResponse::class.java)
                            Log.e(ContentValues.TAG, "error - body : $body")
                            binding.tvError.text = error.error?.errorMessage
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Log.e("validPasswordAuthCode", "통신 실패")
                }
            })
        }
        binding.btnClose.setOnClickListener {
            dismiss()
            setOnCancelListener {
                Toast.makeText(context,"비밀번호 변경을 취소했어요", Toast.LENGTH_SHORT).show()
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
