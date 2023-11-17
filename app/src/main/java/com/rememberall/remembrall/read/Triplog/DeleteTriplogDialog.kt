package com.rememberall.remembrall.read.Triplog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.rememberall.remembrall.BuildConfig.SERVER
import com.rememberall.remembrall.databinding.DialogDeleteTriplogBinding
import com.rememberall.remembrall.user.userinfo.SharedManager
import com.rememberall.remembrall.read.Triplog.res.DeleteTriplogResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DeleteTriplogDialog(
    context: Context,
    triplogId: Long,
    triplogTitle: String
) : Dialog(context){ // 뷰를 띄워야 하므로 Dialog 클래스는 context를 인자로 받는다.
    private lateinit var binding: DialogDeleteTriplogBinding
    private lateinit var onClickListener: DeleteTriplogDialog.OnDialogClickListener
    private var triplogId = triplogId
    private var triplogTitle = triplogTitle
    var retrofit = Retrofit.Builder()
        .baseUrl(SERVER)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var triplogService : TriplogService = retrofit.create(TriplogService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogDeleteTriplogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() = with(binding) {
        binding.tvDeleteMessage.text = triplogTitle+" : 정말 삭제하시겠습니까?"
        binding.btnTriplogDelete.setOnClickListener {
            val sharedManager : SharedManager by lazy { SharedManager(context) }
            var authToken = sharedManager.getCurrentUser().accessToken
            triplogService.deleteTripLog(authToken!!, triplogId).enqueue(object :
                Callback<DeleteTriplogResponse> {
                override fun onResponse(
                    call: Call<DeleteTriplogResponse>,
                    response: Response<DeleteTriplogResponse>
                ) {
                    Log.e("DeleteTripLog", response.body().toString())
                    if(response.body()?.success.toString() == "true"){
                        Toast.makeText(context, response.body()?.response?.message, Toast.LENGTH_SHORT).show()
                        onClickListener.onClicked()
                        dismiss()
                    }
                }
                override fun onFailure(call: Call<DeleteTriplogResponse>, t: Throwable) {
                    Toast.makeText(context,"일기장 삭제 실패", Toast.LENGTH_SHORT).show()
                }
            })
        }
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
