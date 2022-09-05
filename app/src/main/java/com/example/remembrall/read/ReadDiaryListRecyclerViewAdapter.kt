package com.example.remembrall.read

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.remembrall.R
class ReadDiaryListRecyclerViewAdapter (
    private val context: Context,
    private val datalist: ArrayList<ReadDiaryListRecyclerViewData>
    ): RecyclerView.Adapter<ReadDiaryListRecyclerViewAdapter.ViewHolder>(){
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = itemView.findViewById(R.id.tv_readdiary_name)
        val heart: ImageView=itemView.findViewById(R.id.img_adddiary_heart)
        val diary: ConstraintLayout=itemView.findViewById(R.id.constraintlayout_readdiary)

        init{
            diary.setOnClickListener{
                itemClickListener.diaryOnClick(it, adapterPosition)
            }
            heart.setOnClickListener {
                itemClickListener.heartOnClick(it, adapterPosition)
            }
        }
        fun bind(data: ReadDiaryListRecyclerViewData){
            name.text=data.name
        }
    }

    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_readdiarylist,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int =datalist.size

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datalist[position])
    }

    interface OnItemClickListener {
        fun diaryOnClick(v: View, position: Int)
        fun heartOnClick(v: View, position: Int)
    }

    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }


}