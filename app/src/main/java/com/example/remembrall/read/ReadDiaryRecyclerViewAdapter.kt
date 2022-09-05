package com.example.remembrall.read

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.remembrall.R

class ReadDiaryRecyclerViewAdapter (
    private val context: Context,
    private val datalist: ArrayList<ReadDiaryRecyclerViewData>
): RecyclerView.Adapter<ReadDiaryRecyclerViewAdapter.ViewHolder>(){
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val place: TextView =itemView.findViewById(R.id.tv_itemread_place)
        val imageView: ImageView = itemView.findViewById(R.id.image_readpicture)
        val content: TextView=itemView.findViewById(R.id.tv_itemread_coment)
        val imageLayout: LinearLayout = itemView.findViewById(R.id.linear_itemread_image)

        fun bind(data: ReadDiaryRecyclerViewData) {
            place.text=data.place
            content.text=data.content

            if(data.image==""){
                imageLayout.visibility=View.GONE
            }
            else{
                Glide.with(this.itemView)
                    .load(data.image) // 불러올 이미지 url
                    .fitCenter()
//                    .placeholder(R.drawable.ic_image) // 이미지 로딩 시작하기 전 표시할 이미지
//                    .error(R.drawable.ic_image) // 로딩 에러 발생 시 표시할 이미지
//                    .fallback(R.drawable.ic_image) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                    .apply(RequestOptions().override(500,500))
                    .into(imageView) // 이미지를 넣을 뷰
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_readdiary,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int =datalist.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datalist[position])
    }
}