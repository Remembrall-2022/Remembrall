package com.example.remembrall.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.remembrall.R
import com.example.remembrall.databinding.ActivityLoginBinding
import com.example.remembrall.login.req.LoginRequest
import com.example.remembrall.login.res.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var intent =  Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        // 레트로핏 객체 생성.
        var retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.SERVER))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 로그인 서비스 올리기
        var loginService: UserService = retrofit.create(UserService::class.java)

        binding.btnJoin.setOnClickListener {
//            val email = binding.etEmail.text.toString().trim()
//            val password = binding.etPassword.text.toString().trim()
            var email = "minpearl@gmail.com"
            var password = "newly"
            loginService.loginEmail(LoginRequest(email, password)).enqueue(object: Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("Login", t.toString())
                    Toast.makeText(applicationContext,"error : 로그인 실패", Toast.LENGTH_SHORT)
                }
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val login = response
                    Log.d("Login:", "login success : "+login?.toString())
                    Log.d("Login:", "login success : "+login?.body().toString())

//                    if(login?.success.toString() == "true"){
//                        var loginData = login!!.response
//                        // 현재 유저 정보 저장
////                        val currentUser = LoginData(
////                            id = loginData.id,
////                            isActive = loginData.isActive,
////                            name = loginData.name,
////                            password = loginData.password,
////                            budget = loginData.budget,
////                            iskakao = loginData.iskakao,
////                            defState = loginData.defState,
////                            defArea = loginData.defArea,
////                            defTown = loginData.defTown,
////                        )
////                        sharedManager.saveCurrentUser(currentUser)
////                        UserApplication.setUserId(loginData.id)
////                        UserApplication.setUserBudget(loginData.budget.toInt())
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//                        startActivity(intent)
//                        finish()
//                    }
//                    else{
////                        Toast.makeText(applicationContext, login?.statusMsg.toString(), Toast.LENGTH_SHORT)
//                    }
                }
            })
        }

        binding.btnBack.setOnClickListener{
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
