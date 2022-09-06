package com.example.remembrall.write

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.remembrall.databinding.DialogDiaryListBinding
import com.example.remembrall.databinding.DialogQuestionListBinding
import com.example.remembrall.login.UserService
import com.example.remembrall.login.userinfo.SharedManager
import com.example.remembrall.read.ReadDiaryListRecyclerViewAdapter
import com.example.remembrall.read.ReadDiaryListRecyclerViewData
import com.example.remembrall.read.Triplog.TriplogService
import com.example.remembrall.read.Triplog.res.GetTriplogListResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

class DiaryListDialog (
    context: Context,
    selectDiaryListRecyclerViewData: ArrayList<ReadDiaryListRecyclerViewData>
) : Dialog(context){
    private lateinit var binding: DialogDiaryListBinding
    private lateinit var diaryListRecyclerViewData: ArrayList<ReadDiaryListRecyclerViewData>
    lateinit var readDiaryListRecyclerViewAdapter: SelectDiaryListRecyclerViewAdapter
    private var list=ArrayList<ReadDiaryListRecyclerViewData>()


    init{
        diaryListRecyclerViewData = arrayListOf()
        diaryListRecyclerViewData.addAll(selectDiaryListRecyclerViewData)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogDiaryListBinding.inflate(layoutInflater)
        initViews()
        setContentView(binding.root)
    }

    private fun initViews(){
        readDiaryListRecyclerViewAdapter = SelectDiaryListRecyclerViewAdapter(context, diaryListRecyclerViewData)
        readDiaryListRecyclerViewAdapter.notifyDataSetChanged()
        var recyclerViewDiary=binding.recyclerViewDiary
        recyclerViewDiary.adapter = readDiaryListRecyclerViewAdapter
        recyclerViewDiary.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)

        binding.btnClose.setOnClickListener {
            cancel()
        }
        Log.e("questionData", "${readDiaryListRecyclerViewAdapter.itemCount}")
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