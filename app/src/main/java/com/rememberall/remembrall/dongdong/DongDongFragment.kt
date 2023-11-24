package com.rememberall.remembrall.dongdong

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.rememberall.remembrall.BuildConfig.SERVER
import com.rememberall.remembrall.MainActivity
import com.rememberall.remembrall.databinding.FragmentDongDongBinding
import com.rememberall.remembrall.dongdong.res.DongDongResponse
import com.rememberall.remembrall.user.userinfo.SharedManager
import com.rememberall.remembrall.setting.SettingFragment
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DongDongFragment : Fragment() {
    var binding: FragmentDongDongBinding?= null

    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDongDongBinding.inflate(inflater, container, false)

        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor()).build()

        // 레트로핏 객체 생성.
        var retrofit = Retrofit.Builder()
            .baseUrl(SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // dongdong 서비스 올리기
        var dongdongService: DongDongService = retrofit.create(DongDongService::class.java)

        val sharedManager : SharedManager by lazy { SharedManager(mainActivity) }
        var authToken = sharedManager.getCurrentUser().accessToken

        Log.e("AuthToken", authToken!!)
        dongdongService.getDongDong(authToken!!).enqueue(object : Callback<DongDongResponse> {
            override fun onResponse(
                call: Call<DongDongResponse>,
                response: Response<DongDongResponse>
            ) {
                if(response.body()?.success.toString() == "true"){
                    var dongdongInfo = response.body()!!.response!!
                    var exp = dongdongInfo.exp
                    var maxExp = dongdongInfo.maxExp
                    var dongdongImg = dongdongInfo.dongdongImgUrl
                    var level = dongdongInfo.level.toString()
                    binding!!.level.text = "Lv "+level
                    binding!!.progressBar.progress = exp!!
                    binding!!.progressBar.max = maxExp!!
                    Glide.with(mainActivity)
                        .load(dongdongImg)
                        .into(binding!!.ivDongdong)
                }
            }

            override fun onFailure(call: Call<DongDongResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

        binding!!.ivSetting.setOnClickListener {
            var settingFragment = SettingFragment()
            mainActivity.replaceFragment(settingFragment)
        }

//        binding!!.btnAttendance.setOnClickListener {
//        }
        return binding!!.root
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