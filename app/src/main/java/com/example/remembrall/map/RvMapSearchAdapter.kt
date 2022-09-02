package com.example.remembrall.map

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remembrall.*


class RvMapSearchAdapter(val context: Context): RecyclerView.Adapter<RvMapSearchAdapter.ViewHolder>() {
    var itemList = ArrayList<RvMapSearch>()

    fun setDataList(mapSearchResultList : ArrayList<RvMapSearch>){
        this.itemList = mapSearchResultList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvMapSearchAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_map_search,
            parent,
            false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RvMapSearchAdapter.ViewHolder, position: Int) {
        holder.name.text = itemList[position].place_name
        holder.category.text = itemList[position].category
        holder.address.text = itemList[position].address
        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_place_name_map_search)
        val category: TextView = itemView.findViewById(R.id.tv_category_map_search)
        val address: TextView = itemView.findViewById(R.id.tv_address_map_search)
    }


    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener
}