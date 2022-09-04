package com.example.remembrall.write

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.example.remembrall.R
import com.example.remembrall.read.ReadDiaryListRecyclerViewAdapter

class QuestionRecyclerViewAdapter(
    private val context: Context,
    private val datalist: ArrayList<QuestionRecyclerViewData>
): RecyclerView.Adapter<QuestionRecyclerViewAdapter.ViewHolder>(){
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : AdapterView.OnItemClickListener

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        fun bind(data: QuestionRecyclerViewData){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dialog_question_list,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int =datalist.size

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: QuestionRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bind(datalist[position])
    }
}