package com.example.remembrall.read

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.remembrall.R
import com.example.remembrall.databinding.ActivityReadDiaryBinding
import com.example.remembrall.write.WriteDividerItemDecoration

class ReadDiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadDiaryBinding
    private lateinit var readDiaryRecyclerViewData: ArrayList<ReadDiaryRecyclerViewData>
    private lateinit var readDiaryRecyclerViewAdapter: ReadDiaryRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityReadDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialize();
        initReadDiaryRecyclerView();
        binding.recyclerviewReaddiary.addItemDecoration(WriteDividerItemDecoration(binding.recyclerviewReaddiary.context, R.drawable.creatediary_line_divider, 0,0))

    }

    private fun initialize(){
        readDiaryRecyclerViewData= arrayListOf()
        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData("창덕궁", "", "처음으로 창덕궁을 봐서 너무 신기했다! 다음에는 가족들이랑 같이 오고 싶기도 하고, 오늘은 사람들이 많았는데 한적할 때의 창덕궁은 어떤 모습을 가지고 있을지 궁금하기도 하다."))
        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData("창덕궁", "https://cdn.pixabay.com/photo/2021/08/03/07/03/orange-6518675_960_720.jpg", "처음으로 창덕궁을 봐서 너무 신기했다! 다음에는 가족들이랑 같이 오고 싶기도 하고, 오늘은 사람들이 많았는데 한적할 때의 창덕궁은 어떤 모습을 가지고 있을지 궁금하기도 하다."))
        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData("창덕궁", "https://cdn.pixabay.com/photo/2021/08/03/07/03/orange-6518675_960_720.jpg", "처음으로 창덕궁을 봐서 너무 신기했다! 다음에는 가족들이랑 같이 오고 싶기도 하고, 오늘은 사람들이 많았는데 한적할 때의 창덕궁은 어떤 모습을 가지고 있을지 궁금하기도 하다."))
    }

    private fun initReadDiaryRecyclerView() {
        val recyclerViewDiary = binding.recyclerviewReaddiary
        recyclerViewDiary.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        readDiaryRecyclerViewAdapter= ReadDiaryRecyclerViewAdapter(this, readDiaryRecyclerViewData)
        recyclerViewDiary.adapter = readDiaryRecyclerViewAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {	//뒤로가기 버튼이 작동하도록
        when (item.itemId) {
            R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}