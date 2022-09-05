package com.example.remembrall.write

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.remembrall.R
import com.example.remembrall.databinding.DialogQuestionListBinding
import java.util.ArrayList

class QuestionListDialog (
    context: Context,
    questionData: ArrayList<QuestionRecyclerViewData>
) : Dialog(context){
    private lateinit var binding: DialogQuestionListBinding
    private lateinit var questionRecyclerViewData: ArrayList<QuestionRecyclerViewData>
    private lateinit var questionRecyclerViewAdapter: QuestionRecyclerViewAdapter
    private var list=ArrayList<QuestionRecyclerViewData>()

    init{
        questionRecyclerViewData= arrayListOf()
        questionRecyclerViewData.addAll(questionData)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogQuestionListBinding.inflate(layoutInflater)
//        Log.e("list", "${list}")
        initViews()
        setContentView(binding.root)

        clickRecyclerViewItem()
    }

    private fun initViews(){
        var recyclerViewQuestion=binding.recyclerviewQuestion
        recyclerViewQuestion.layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        questionRecyclerViewAdapter=QuestionRecyclerViewAdapter(context, questionRecyclerViewData)
        questionRecyclerViewAdapter.notifyDataSetChanged()
        recyclerViewQuestion.adapter=questionRecyclerViewAdapter

        Log.e("questionData", "${questionRecyclerViewAdapter.itemCount}")
    }

    private fun clickRecyclerViewItem(){
        questionRecyclerViewAdapter.setItemClickListener(object: QuestionRecyclerViewAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val question=questionRecyclerViewData[position].questionName
                val id=questionRecyclerViewData[position].id
                Log.e("question", "$id + $question")
            }
        })
    }
}