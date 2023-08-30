package com.rememberall.remembrall.write

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.rememberall.remembrall.databinding.DialogDiaryListBinding
import com.rememberall.remembrall.read.ReadDiaryListRecyclerViewData
import okhttp3.logging.HttpLoggingInterceptor
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