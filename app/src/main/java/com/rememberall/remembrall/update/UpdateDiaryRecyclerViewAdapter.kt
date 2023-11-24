package com.rememberall.remembrall.update

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rememberall.remembrall.R
import com.rememberall.remembrall.write.ItemMoveCallbackListener
import com.rememberall.remembrall.write.WriteDiaryRecyclerViewAdapter
import com.rememberall.remembrall.write.WriteDiaryRecyclerViewData
import java.util.*
import kotlin.collections.ArrayList

class UpdateDiaryRecyclerViewAdapter(
    private val context: Context,
    private val datalist: ArrayList<UpdateDiaryRecyclerViewData>,
    private val imageUri: ArrayList<Uri>
): RecyclerView.Adapter<UpdateDiaryRecyclerViewAdapter.ViewHolder>(), ItemMoveCallbackListener.OnItemMoveListener{
    private lateinit var updateDiaryRecyclerViewAdapter: UpdateDiaryRecyclerViewAdapter
    private lateinit var dragListener: OnStartDragListener

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val place: TextView = itemView.findViewById(R.id.tv_addplace_place)
        val addimage: ConstraintLayout = itemView.findViewById(R.id.cons_addplace_image)
        val image: ImageView = itemView.findViewById(R.id.imageview_addpicture)
        val coment: EditText = itemView.findViewById(R.id.et_addplace_coment)
        val drop: LinearLayout =itemView.findViewById(R.id.linear_addplace_drop)
        val placehide: LinearLayout =itemView.findViewById(R.id.linear_addplace_place)

        val editTool: LinearLayout =itemView.findViewById(R.id.linear_addplace_edit)
        val delete: ImageView =itemView.findViewById(R.id.imageview_addplace_delete)
        val transfer: ImageView =itemView.findViewById(R.id.imageview_addplace_transfer)

        init {
            addimage.setOnClickListener{
                itemClickListener.imageViewOnClick(it, adapterPosition)
            }
            drop.setOnClickListener{
                itemClickListener.dropViewOnClck(it, adapterPosition)
            }
            placehide.setOnClickListener{
                itemClickListener.dropViewOnClck(it, adapterPosition)
            }
            editTool.setOnClickListener{
                itemClickListener.dropViewOnClck(it, adapterPosition)
            }
            delete.setOnClickListener {
                itemClickListener.deleteViewOnClck(it, adapterPosition)
            }
            transfer.setOnClickListener {
                itemClickListener.transferViewOnClck(it, adapterPosition)
            }
        }

        fun bind(data: UpdateDiaryRecyclerViewData, uri: Uri){
            Glide.with(context)
                .load(uri)
                .fitCenter()
                .apply(RequestOptions().override(500, 500))
                .into(image)

            place.text=data.name
            coment.setText(data.comment)
//            if(data.image==""){
//                image.adjustViewBounds=false
//            }
//            else {
//                image.adjustViewBounds = true
//            }

        }
    }

    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_place,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int =datalist.size

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datalist[position], imageUri[position])
        holder.itemView.findViewById<ImageView>(R.id.imageview_addplace_transfer).setOnTouchListener{ view, event->
            if (event.action == MotionEvent.ACTION_DOWN) {
                dragListener.onStartDrag(holder)
            }
            return@setOnTouchListener false
        }
//        holder.itemView.setOnClickListener{
//            itemClickListener.onClick(it, position)
//            Log.d("holder", "클릭됨")
//        }
    }
    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        //        fun onClick(v: View, position: Int)
        fun imageViewOnClick(v: View, position: Int)
        fun dropViewOnClck(v: View, position: Int)
        fun deleteViewOnClck(v: View, position: Int)
        fun transferViewOnClck(v: View, position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener


    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    fun startDrag(listener: OnStartDragListener) {
        this.dragListener = listener
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(datalist, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemSwiped(position: Int) {
        datalist.removeAt(position)
        notifyItemRemoved(position)
    }
}