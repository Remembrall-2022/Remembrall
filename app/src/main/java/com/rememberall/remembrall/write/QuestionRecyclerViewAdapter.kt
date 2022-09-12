package com.rememberall.remembrall.write

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.rememberall.remembrall.R

class QuestionRecyclerViewAdapter(
    private val context: Context,
    private val datalist: ArrayList<QuestionRecyclerViewData>
): RecyclerView.Adapter<QuestionRecyclerViewAdapter.ViewHolder>(){
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val question=itemView.findViewById<TextView>(R.id.tv_questionlist)
        val questionList=itemView.findViewById<ConstraintLayout>(R.id.cons_questionlist)

        init{
            questionList.setOnClickListener{
                itemClickListener.onClick(it,adapterPosition)
            }
        }
        fun bind(data: QuestionRecyclerViewData){
            question.text=data.questionName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_questionlist,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int =datalist.size

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: QuestionRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bind(datalist[position])
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

}