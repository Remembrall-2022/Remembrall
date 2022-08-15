package com.example.remebrall.read

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.remebrall.R

class ReadDiaryRecyclerViewAdapter (
    private val context: Context,
    private val datalist: ArrayList<ReadDiaryRecyclerViewData>
    ): RecyclerView.Adapter<ReadDiaryRecyclerViewAdapter.ViewHolder>(){

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
            val name: TextView = itemView.findViewById(R.id.tv_readdiary_name)

            fun bind(data: ReadDiaryRecyclerViewData){
                name.text=data.name
            }
        }

    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_readdiary,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int =datalist.size

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datalist[position])
    }
    }