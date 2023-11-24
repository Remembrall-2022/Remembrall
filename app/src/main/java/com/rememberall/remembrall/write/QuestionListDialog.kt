package com.rememberall.remembrall.write

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.rememberall.remembrall.databinding.DialogQuestionListBinding
import java.util.ArrayList

class QuestionListDialog (
    context: Context,
    questionData: ArrayList<QuestionRecyclerViewData>
) : Dialog(context){
    private lateinit var binding: DialogQuestionListBinding
    private lateinit var questionRecyclerViewData: ArrayList<QuestionRecyclerViewData>
    lateinit var questionRecyclerViewAdapter: QuestionRecyclerViewAdapter
    private var list=ArrayList<QuestionRecyclerViewData>()
    var selectedQuestion = ""

    init{
        questionRecyclerViewData= arrayListOf()
        questionRecyclerViewData.addAll(questionData)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogQuestionListBinding.inflate(layoutInflater)
        initViews()
        setContentView(binding.root)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(window?.attributes)
        layoutParams.width = (context.resources.displayMetrics.widthPixels * 0.9).toInt()
        layoutParams.height = (context.resources.displayMetrics.heightPixels * 0.8).toInt()
        window?.attributes = layoutParams
    }

    private fun initViews(){
        var recyclerViewQuestion=binding.recyclerviewQuestion
        recyclerViewQuestion.layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        questionRecyclerViewAdapter=QuestionRecyclerViewAdapter(context, questionRecyclerViewData)
        questionRecyclerViewAdapter.notifyDataSetChanged()
        recyclerViewQuestion.adapter=questionRecyclerViewAdapter

        binding.btnClose.setOnClickListener {
            cancel()
        }

        Log.e("questionData", "${questionRecyclerViewAdapter.itemCount}")
    }

}