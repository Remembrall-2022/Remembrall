package com.example.remembrall.read.Triplog

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import com.example.remembrall.R
import com.example.remembrall.databinding.DialogTriplogCreateBinding
import com.example.remembrall.login.LoginActivity
import com.example.remembrall.login.userinfo.SharedManager
import com.example.remembrall.read.Triplog.req.TriplogRequest
import com.example.remembrall.read.Triplog.res.CreateTriplogResponse
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class TriplogCreateDialog(
    context: Context
) : Dialog(context){ // 뷰를 띄워야 하므로 Dialog 클래스는 context를 인자로 받는다.
    private lateinit var binding: DialogTriplogCreateBinding
//    val db = Firebase.firestore
    // TODO : url key에서 들고오기
    var retrofit = Retrofit.Builder()
        .baseUrl("http://ec2-13-124-98-176.ap-northeast-2.compute.amazonaws.com:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var triplogService : TriplogService = retrofit.create(TriplogService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogTriplogCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() = with(binding) {

        binding.llCreateTriplogStartDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(context, DatePickerDialog.OnDateSetListener { datePicker, y, m, d->
                binding.tvStartDate.text = String.format("%d-%02d-%02d", y, m, d)
                Toast.makeText(context, "$y-$m-$d", Toast.LENGTH_SHORT).show()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show()
        }
        binding.llCreateTriplogEndDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(context, DatePickerDialog.OnDateSetListener { datePicker, y, m, d->
                binding.tvEndDate.text = String.format("%d-%02d-%02d", y, m, d)
                Toast.makeText(context, String.format("%d-%02d-%02d", y, m, d), Toast.LENGTH_SHORT).show()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show()
        }

        binding.btnTriplogCreate.setOnClickListener {
            val title = binding.etTriplogCreateName.text.toString().trim()
            val startDate = binding.tvStartDate.text.toString()
            val endDate = binding.tvEndDate.text.toString()
            val sharedManager : SharedManager by lazy { SharedManager(context) }
            var authToken = sharedManager.getCurrentUser().accessToken
            triplogService.createTripLog(authToken, TriplogRequest(title, startDate, endDate)).enqueue(object : Callback<CreateTriplogResponse>{
                override fun onResponse(
                    call: Call<CreateTriplogResponse>,
                    response: Response<CreateTriplogResponse>
                ) {
                    Log.e("CreateTripLog", response.body().toString())
                    if(response.body()?.success.toString() == "true"){
                        Toast.makeText(context,"글을 업로드 했어요",Toast.LENGTH_SHORT).show()
                    }

                    dismiss()
                }
                override fun onFailure(call: Call<CreateTriplogResponse>, t: Throwable) {
                    Toast.makeText(context,"일기장 생성 실패",Toast.LENGTH_SHORT).show()
                }

            })

            dismiss()
        }
        binding.btnClose.setOnClickListener {
            cancel()
        }
        setOnCancelListener {
            Toast.makeText(context,"일기장 생성을 취소했어요",Toast.LENGTH_SHORT).show()
        }
    }
}
