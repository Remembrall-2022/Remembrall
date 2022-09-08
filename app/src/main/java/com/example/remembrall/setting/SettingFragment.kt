package com.example.remembrall.setting

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.remembrall.BuildConfig.SERVER
import com.example.remembrall.MainActivity
import com.example.remembrall.R
import com.example.remembrall.databinding.FragmentSettingBinding
import com.example.remembrall.dongdong.DongDongFragment
import com.example.remembrall.login.SplashActivity
import com.example.remembrall.login.UserService
import com.example.remembrall.login.res.UserInfoResponse
import com.example.remembrall.login.userinfo.SharedManager
import com.kakao.sdk.user.UserApiClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SettingFragment : Fragment() {
    var binding : FragmentSettingBinding?= null
    lateinit var mainActivity: MainActivity
    var email : String ?= null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor()).build()

        // 레트로핏 객체 생성.
        var retrofit = Retrofit.Builder()
            .baseUrl(SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // user 서비스 올리기
        var userService: UserService = retrofit.create(UserService::class.java)

        val sharedManager : SharedManager by lazy { SharedManager(mainActivity) }
        var authToken = sharedManager.getCurrentUser().accessToken

        fun getUserInfo(){
            userService.userInfo(authToken!!)
                .enqueue(object : Callback<UserInfoResponse>{
                    override fun onResponse(
                        call: Call<UserInfoResponse>,
                        response: Response<UserInfoResponse>
                    ) {
                        if(response.body()?.success.toString() == "true"){
                            var userInfo = response.body()!!.response!!
                            var name = userInfo.name
                            email = userInfo.email
                            binding!!.userName.text = name.toString()
                            binding!!.userEmail.text = email.toString()
                            if(userInfo.authType == "KAKAO"){
                                binding!!.btnChangePassword.visibility = View.GONE
                            }
                        }
                    }

                    override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                        Log.e("UserInfo", "요청 실패")
                    }

                })
        }

        getUserInfo()

        binding!!.btnLogout.setOnClickListener {
            sharedManager.logoutCurrentUser()
            Log.d(TAG, "로그아웃 성공")
            // 토큰 정보 보기
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    Log.e(TAG, "토큰 정보 보기 실패", error)
                }
                else if (tokenInfo != null) {
                    Log.i(TAG, "토큰 정보 보기 성공" +
                            "\n회원번호: ${tokenInfo.id}" +
                            "\n만료시간: ${tokenInfo.expiresIn} 초")
                    // 로그아웃
                    UserApiClient.instance.logout { error ->
                        if (error != null) {
                            Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                        }
                        else {
                            Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                        }
                    }
                }
            }
            var intent =  Intent(mainActivity, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        binding!!.btnChangeName.setOnClickListener {
            val changeNameDialog = ChangeNameDialog(mainActivity)
            changeNameDialog.show()
            changeNameDialog.setOnClickListener(object : ChangeNameDialog.OnDialogClickListener{
                override fun onClicked(name: String) {
                    binding!!.userName.text = name
                }
            })
        }

        binding!!.btnChangePassword.setOnClickListener {
            ChangePasswordDialog(mainActivity, email!!).show()
        }

        binding!!.btnSignout.setOnClickListener {
            SignOutDialog(mainActivity).show()
        }

        binding!!.llBack.setOnClickListener {
            var dongdongFragment = DongDongFragment()
            mainActivity.replaceFragment(dongdongFragment)
        }
        return binding!!.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
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