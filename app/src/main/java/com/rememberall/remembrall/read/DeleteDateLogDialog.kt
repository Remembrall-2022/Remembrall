package com.rememberall.remembrall.read

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.rememberall.remembrall.databinding.DialogDeleteDatelogBinding
import com.rememberall.remembrall.login.userinfo.SharedManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class DeleteDateLogDialog(
    context: Context,
    triplogId: Long,
    datelogId:Long,
) : Dialog(context) {
    private lateinit var binding: DialogDeleteDatelogBinding
    private var triplog: Long
    private var datelog: Long

    init {
        triplog=triplogId
        datelog=datelogId
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogDeleteDatelogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDatelogDelete.setOnClickListener {
            val sharedManager: SharedManager by lazy { SharedManager(context) }
            var authToken = sharedManager.getCurrentUser().accessToken
            ReadDiaryService.getRetrofitDeleteDiary(authToken!!, triplog, datelog).enqueue(object :
                Callback<DeleteDiaryResponse> {
                override fun onResponse(
                    call: Call<DeleteDiaryResponse>,
                    response: Response<DeleteDiaryResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.e("question", response.toString())
                        Log.e("question", response.body().toString())
                        Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        cancel()

                    } else {
                        try {
                            val body = response.errorBody()!!.string()
                            val jsonObject = JSONObject(body)
                            val errorBody =
                                jsonObject.getJSONObject("error").getString("errorMessage")
//
                            //에러 Toast
                            Toast.makeText(context, "${errorBody}", Toast.LENGTH_SHORT).show()
                            Log.e(ContentValues.TAG, "body : $body")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(
                    call: Call<DeleteDiaryResponse>,
                    t: Throwable
                ) {
                    TODO("Not yet implemented")
                }
            })
        }

        binding.btnDatelogClose.setOnClickListener {
            cancel()
        }

    }

}
