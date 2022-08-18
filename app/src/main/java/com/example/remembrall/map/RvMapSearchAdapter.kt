package com.example.remembrall.map
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//
//
//class RvMapSearchAdapter(val itemList: ArrayList<RvMapSearch>): RecyclerView.Adapter<ListAdapter.ViewHolder>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_map_search, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun getItemCount(): Int {
//        return itemList.size
//    }
//
//    override fun onBindViewHolder(holder: ListAdapter.ViewHolder, position: Int) {
//        holder.name.text = itemList[position].name
//        holder.road.text = itemList[position].road
//        holder.address.text = itemList[position].address
//        // 아이템 클릭 이벤트
//        holder.itemView.setOnClickListener {
//            itemClickListener.onClick(it, position)
//        }
//    }
//
//    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
//        val name: TextView = itemView.findViewById(R.id.tv_list_name)
//        val road: TextView = itemView.findViewById(R.id.tv_list_road)
//        val address: TextView = itemView.findViewById(R.id.tv_list_address)
//    }
//
//    interface OnItemClickListener {
//        fun onClick(v: View, position: Int)
//    }
//
//    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
//        this.itemClickListener = onItemClickListener
//    }
//
//    private lateinit var itemClickListener : OnItemClickListener
//}