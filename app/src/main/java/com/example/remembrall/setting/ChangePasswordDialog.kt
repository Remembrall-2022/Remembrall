package com.example.remembrall.setting

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.remembrall.databinding.DialogChangeNameBinding
import com.example.remembrall.databinding.DialogChangePasswordBinding
import com.example.remembrall.databinding.DialogTriplogCreateBinding
import com.example.remembrall.login.UserService
import com.example.remembrall.login.res.UserInfoResponse
import com.example.remembrall.login.res.UserNameResponse
import com.example.remembrall.login.userinfo.SharedManager
import com.example.remembrall.read.Triplog.TriplogService
import com.example.remembrall.read.Triplog.req.TriplogRequest
import com.example.remembrall.read.Triplog.res.CreateTriplogResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class ChangePasswordDialog (
    context: Context
) : Dialog(context){ // 뷰를 띄워야 하므로 Dialog 클래스는 context를 인자로 받는다.
    private lateinit var binding: DialogChangePasswordBinding
    // TODO : url key에서 들고오기
    val client = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor()).build()
    var retrofit = Retrofit.Builder()
        .baseUrl("http://ec2-13-124-98-176.ap-northeast-2.compute.amazonaws.com:8080")
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
        binding.btnChangePassword.setOnClickListener {
            val newName = binding.etNewPassword.text.toString().trim()
            val sharedManager : SharedManager by lazy { SharedManager(context) }
            var authToken = sharedManager.getCurrentUser().accessToken

            userService.changeUserName(authToken, newName).enqueue(object :
                Callback<UserNameResponse> {
                override fun onResponse(
                    call: Call<UserNameResponse>,
                    response: Response<UserNameResponse>
                ) {
                    Log.e("Username", response.body().toString())
                    if(response.body()?.success.toString() == "true"){
                        Toast.makeText(context,"닉네임 변경 성공", Toast.LENGTH_SHORT).show()
                    }
                    dismiss()
                }
                override fun onFailure(call: Call<UserNameResponse>, t: Throwable) {
                    Toast.makeText(context,"닉네임 변경 실패", Toast.LENGTH_SHORT).show()
                }
            })

            dismiss()
        }
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        setOnCancelListener {
            Toast.makeText(context,"일기장 생성을 취소했어요", Toast.LENGTH_SHORT).show()
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
