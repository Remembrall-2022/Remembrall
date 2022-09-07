package com.example.remembrall.dongdong

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.remembrall.databinding.DialogAttendanceBinding
import com.example.remembrall.databinding.DialogDeleteTriplogBinding
import com.example.remembrall.dongdong.res.AttendanceResponse
import com.example.remembrall.login.userinfo.SharedManager
import com.example.remembrall.read.Triplog.DeleteTriplogDialog
import com.example.remembrall.read.Triplog.TriplogService
import com.example.remembrall.read.Triplog.res.DeleteTriplogResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AttendanceDialog (
    context: Context
) : Dialog(context){ // 뷰를 띄워야 하므로 Dialog 클래스는 context를 인자로 받는다.
    private lateinit var binding: DialogAttendanceBinding
    private lateinit var onClickListener: AttendanceDialog.OnDialogClickListener
    // TODO : url key에서 들고오기
    var retrofit = Retrofit.Builder()
        .baseUrl("http://ec2-13-124-98-176.ap-northeast-2.compute.amazonaws.com:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var dongdongService : DongDongService = retrofit.create(DongDongService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() = with(binding) {
        val sharedManager : SharedManager by lazy { SharedManager(context) }
        var authToken = sharedManager.getCurrentUser().accessToken
        dongdongService.getAttendance(authToken!!).enqueue(object : Callback<AttendanceResponse>{
            override fun onResponse(
                call: Call<AttendanceResponse>,
                response: Response<AttendanceResponse>
            ) {
                if(response.body()?.success.toString() == "true"){
//                    if(response.body()?.response.message

                }
            }

            override fun onFailure(call: Call<AttendanceResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

        onClickListener.onClicked()
        dismiss()
        binding.btnClose.setOnClickListener {
            cancel()
        }
        setOnCancelListener {
            Toast.makeText(context,"일기장 삭제를 취소했어요", Toast.LENGTH_SHORT).show()
        }
    }

    fun setOnClickListener(listener: OnDialogClickListener)
    {
        onClickListener = listener
    }

    interface OnDialogClickListener
    {
        fun onClicked()
    }
}
