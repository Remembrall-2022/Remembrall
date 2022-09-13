package com.rememberall.remembrall.read

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rememberall.remembrall.R
class ReadDiaryListRecyclerViewAdapter (
    private val context: Context,
    private val datalist: ArrayList<ReadDiaryListRecyclerViewData>
    ): RecyclerView.Adapter<ReadDiaryListRecyclerViewAdapter.ViewHolder>(){
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
    private lateinit var longClickListener: OnItemLongClickListener

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = itemView.findViewById(R.id.tv_readdiary_name)
//        val heart: ImageView=itemView.findViewById(R.id.img_adddiary_heart)
        val diary: ImageView=itemView.findViewById(R.id.img_adddiary_diary)
        val date:TextView=itemView.findViewById(R.id.tv_diarylist_date)

        init{
            diary.setOnClickListener{
                itemClickListener.diaryOnClick(it, adapterPosition)
            }
            diary.setOnLongClickListener {
                longClickListener.diaryLongClick(it, adapterPosition)
                false
            }
//            heart.setOnClickListener {
//                itemClickListener.heartOnClick(it, adapterPosition)
//            }
        }
        fun bind(data: ReadDiaryListRecyclerViewData){
            name.text=data.name
            date.text=data.date
            Glide.with(this.itemView)
                .load(data.imgUrl) // 불러올 이미지 url
                .fitCenter()
                .placeholder(R.drawable.ic_image) // 이미지 로딩 시작하기 전 표시할 이미지
                .error(R.drawable.ic_image) // 로딩 에러 발생 시 표시할 이미지
                .fallback(R.drawable.ic_image) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                .apply(RequestOptions().override(500,500))
                .into(diary) // 이미지를 넣을 뷰
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
//        fun diaryLongOnClick(v: View, position: Int)
//        fun heartOnClick(v: View, position: Int)
    }

    interface OnItemLongClickListener{
        fun diaryLongClick(v : View, position : Int)
    }

    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    fun setItemLongClickListener(onLongClickListener: OnItemLongClickListener){
        this.longClickListener = onLongClickListener
    }


}