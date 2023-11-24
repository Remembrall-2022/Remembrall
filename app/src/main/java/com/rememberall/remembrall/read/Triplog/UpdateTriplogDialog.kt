package com.rememberall.remembrall.read.Triplog

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.rememberall.remembrall.BuildConfig.SERVER
import com.rememberall.remembrall.databinding.DialogTriplogCreateBinding
import com.rememberall.remembrall.user.res.Error
import com.rememberall.remembrall.user.userinfo.SharedManager
import com.rememberall.remembrall.read.ReadDiaryListRecyclerViewData
import com.rememberall.remembrall.read.Triplog.req.TriplogRequest
import com.rememberall.remembrall.read.Triplog.res.CreateTriplogResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.Calendar

class UpdateTriplogDialog(
    context: Context,
    triplogId: Long,
    triplogRequest: TriplogRequest
) : Dialog(context){ // 뷰를 띄워야 하므로 Dialog 클래스는 context를 인자로 받는다.
    private lateinit var binding: DialogTriplogCreateBinding
    private lateinit var onClickListener: UpdateTriplogDialog.OnDialogClickListener
    private lateinit var readDiaryListRecyclerViewData: ArrayList<ReadDiaryListRecyclerViewData>
    private var triplogRequest = triplogRequest
    private var triplogId = triplogId

    var retrofit = Retrofit.Builder()
        .baseUrl(SERVER)
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

        binding.etTriplogCreateName.setText(triplogRequest.title)
        binding.tvStartDate.text = triplogRequest.tripStartDate
        binding.tvEndDate.text = triplogRequest.tripEndDate

        binding.llCreateTriplogStartDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(context, DatePickerDialog.OnDateSetListener { datePicker, y, m, d->
                binding.tvStartDate.text = String.format("%d-%02d-%02d", y, m+1, d)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show()
        }
        binding.llCreateTriplogEndDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(context, DatePickerDialog.OnDateSetListener { datePicker, y, m, d->
                binding.tvEndDate.text = String.format("%d-%02d-%02d", y, m+1, d)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show()
        }

        binding.btnTriplogCreate.setOnClickListener {
            val title = binding.etTriplogCreateName.text.toString().trim()
            val startDate = binding.tvStartDate.text.toString()
            val endDate = binding.tvEndDate.text.toString()
            val sharedManager : SharedManager by lazy { SharedManager(context) }
            var authToken = sharedManager.getCurrentUser().accessToken
            triplogService.updateTripLog(authToken!!, triplogId, TriplogRequest(title, startDate, endDate)).enqueue(object : Callback<CreateTriplogResponse>{
                override fun onResponse(
                    call: Call<CreateTriplogResponse>,
                    response: Response<CreateTriplogResponse>
                ) {
                    Log.e("UpdateTripLog", response.body().toString())
                    if(response.body()?.success.toString() == "true"){
                        Toast.makeText(context,"일기장 수정 완료",Toast.LENGTH_SHORT).show()
                        onClickListener.onClicked()
                        dismiss()
                    }
                    else {
                        try {
                            val body = response.errorBody()!!.string()
                            val error = Gson().fromJson(body, Error::class.java)
                            Log.e(ContentValues.TAG, "error - body : $body")
                            binding.tvError.text = error?.errorMessage
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                }
                override fun onFailure(call: Call<CreateTriplogResponse>, t: Throwable) {
                    Toast.makeText(context,"일기장 생성 실패",Toast.LENGTH_SHORT).show()
                }

            })

        }
        binding.btnClose.setOnClickListener {
            cancel()
        }
        setOnCancelListener {
            Toast.makeText(context,"일기장 생성을 취소했어요",Toast.LENGTH_SHORT).show()
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
