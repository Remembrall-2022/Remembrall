package com.rememberall.remembrall.read.Triplog

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.rememberall.remembrall.BuildConfig.SERVER
import com.rememberall.remembrall.databinding.DialogTriplogCreateBinding
import com.rememberall.remembrall.login.res.LoginResponse
import com.rememberall.remembrall.login.userinfo.SharedManager
import com.rememberall.remembrall.read.ReadDiaryListRecyclerViewData
import com.rememberall.remembrall.read.Triplog.req.TriplogRequest
import com.rememberall.remembrall.read.Triplog.res.CreateTriplogResponse
import com.rememberall.remembrall.read.Triplog.res.GetTriplogListResponse
import com.google.gson.Gson
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class CreateTriplogDialog(
    context: Context
) : Dialog(context){ // 뷰를 띄워야 하므로 Dialog 클래스는 context를 인자로 받는다.
    private lateinit var binding: DialogTriplogCreateBinding
    private lateinit var onClickListener: CreateTriplogDialog.OnDialogClickListener
    private lateinit var readDiaryListRecyclerViewData: ArrayList<ReadDiaryListRecyclerViewData>
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
            triplogService.createTripLog(authToken!!, TriplogRequest(title, startDate, endDate)).enqueue(object : Callback<CreateTriplogResponse>{
                override fun onResponse(
                    call: Call<CreateTriplogResponse>,
                    response: Response<CreateTriplogResponse>
                ) {
                    Log.e("CreateTripLog", response.body().toString())
                    if(response.body()?.success.toString() == "true"){
                        Toast.makeText(context,"일기장 생성 완료",Toast.LENGTH_SHORT).show()

                        val sharedManager : SharedManager by lazy { SharedManager(context) }
                        var authToken = sharedManager.getCurrentUser().accessToken
                        readDiaryListRecyclerViewData= arrayListOf()
                        triplogService.getTripLogList(authToken!!).enqueue(object :
                            Callback<List<GetTriplogListResponse>> {
                            override fun onResponse(
                                call: Call<List<GetTriplogListResponse>>,
                                response: Response<List<GetTriplogListResponse>>
                            ) {
                                Log.e("CreateTripLog", response.body().toString())
                                if(response.body()!=null){
                                    var diaryList = response.body()
                                    if(diaryList != null){
                                        for (diary in diaryList){
                                            val title=diary!!.title.toString()
                                            val imgUrl=diary!!.tripLogImgUrl.toString()
                                            val triplogId=diary!!.triplogId!!.toLong()

                                            val tripStartDate=diary!!.tripStartDate
                                            val tripEndDate=diary!!.tripEndDate
                                            val startDate=tripStartDate.toString().split('-')
                                            val sDate=startDate[0].toString().split('0')
                                            val endDate=tripEndDate.toString().split('-')
                                            val eDate=startDate[0].toString().split('0')
                                            val tripDate=sDate[1]+'.'+startDate[1]+'.'+startDate[2]+" ~ "+eDate[1]+'.'+endDate[1]+'.'+endDate[2]

                                            readDiaryListRecyclerViewData.add(
                                                ReadDiaryListRecyclerViewData(title, imgUrl, triplogId, tripDate)
                                            )
                                        }
                                    }
                                }
                                onClickListener.onClicked(readDiaryListRecyclerViewData)
                                dismiss()
                            }
                            override fun onFailure(call: Call<List<GetTriplogListResponse>>, t: Throwable) {
                                Toast.makeText(context,"일기장 불러오기 실패", Toast.LENGTH_SHORT).show()
                            }
                        })
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
        fun onClicked(readDiaryRecyclerViewDataList: ArrayList<ReadDiaryListRecyclerViewData>)
    }
}
