package com.rememberall.remembrall.read

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rememberall.remembrall.R

class ViewPagerAdapter(
    private val context: Context,
    private val datelist: ArrayList<ViewPagerData>
    ) : RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
    inner class PagerViewHolder(itemView: ViewGroup) : RecyclerView.ViewHolder(itemView){
        val title=itemView.findViewById<TextView>(R.id.tv_readdiary_title)
        val date=itemView.findViewById<TextView>(R.id.tv_readdiary_date)
        val question=itemView.findViewById<TextView>(R.id.tv_readdiary_question)
        val answer=itemView.findViewById<TextView>(R.id.tv_readdiary_answer)
        val placeRecycler=itemView.findViewById<RecyclerView>(R.id.recyclerview_readdiary)
        val place=placeRecycler.findViewById<TextView>(R.id.tv_itemread_place)
        val image=placeRecycler.findViewById<ImageView>(R.id.image_readpicture)
        val coment=placeRecycler.findViewById<TextView>(R.id.tv_itemread_coment)

        var readDiaryRecyclerViewData: ArrayList<ReadDiaryRecyclerViewData> = arrayListOf()
        private lateinit var readDiaryRecyclerViewAdapter: ReadDiaryRecyclerViewAdapter

       fun bind(data: ViewPagerData, position: Int) {
            title.text=data.title
           date.text=data.date
           question.text=data.question
           answer.text=data.answer
           placeRecycler.layoutManager=
               LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
           Log.e("placeInfo", "${data.placeInfo}")
//           place.text=data.placeInfo[position].place
//           image.setImageURI(data.placeInfo[position].image.toUri())
//           coment.text=data.placeInfo[position].content
           Log.d("readDiaryData", "${readDiaryRecyclerViewData}")
           readDiaryRecyclerViewAdapter= ReadDiaryRecyclerViewAdapter(context, readDiaryRecyclerViewData)
           placeRecycler.adapter = readDiaryRecyclerViewAdapter
           readDiaryRecyclerViewData.addAll(data.placeInfo)
           readDiaryRecyclerViewAdapter.notifyItemInserted(readDiaryRecyclerViewData.size)
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_readdiary_recyclerview,
            parent,
            false
        )
        return PagerViewHolder(view as ViewGroup)
    }

    override fun getItemCount(): Int = datelist.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bind(datelist[position], position)
    }


}