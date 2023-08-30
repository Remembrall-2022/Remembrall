package com.rememberall.remembrall.map.MapSearch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rememberall.remembrall.*


class RvMapSearchAdapter(val context: Context): RecyclerView.Adapter<RvMapSearchAdapter.ViewHolder>() {
    var itemList = ArrayList<RvMapSearch>()

    fun setDataList(mapSearchResultList : ArrayList<RvMapSearch>){
        this.itemList = mapSearchResultList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_map_search,
            parent,
            false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = itemList[position].place_name
        holder.category.text = itemList[position].category
        holder.address.text = itemList[position].address
        if(itemList[position].category == ""){
            holder.category.visibility = View.GONE
        }
        if(itemList[position].place_url!=null && itemList[position].place_url!=""){
            Glide.with(context)
                .load(itemList[position].place_url)
                .centerCrop()
                .into(holder.image)
            holder.image.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        else{
            holder.image.visibility = View.GONE
        }

        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }

        holder.btnSelect.setOnClickListener {
            itemClickListener.btnOnClick(it, position)
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_place_name_map_search)
        val category: TextView = itemView.findViewById(R.id.tv_category_map_search)
        val address: TextView = itemView.findViewById(R.id.tv_address_map_search)
        val image: ImageView = itemView.findViewById(R.id.iv_image_map_search)
        val btnSelect : Button = itemView.findViewById(R.id.btn_select)
    }


    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
        fun btnOnClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener
}