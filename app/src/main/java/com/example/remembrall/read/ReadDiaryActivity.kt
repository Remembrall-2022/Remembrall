package com.example.remembrall.read

import android.content.ContentValues
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
import com.example.remembrall.login.res.LoginResponse
import com.example.remembrall.login.userinfo.SharedManager
import com.example.remembrall.write.WriteDividerItemDecoration
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ReadDiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadDiaryBinding
    private lateinit var readDiaryRecyclerViewData: ArrayList<ReadDiaryRecyclerViewData>
    private lateinit var readDiaryRecyclerViewAdapter: ReadDiaryRecyclerViewAdapter
    private var triplogId: Long=1
    private lateinit var datelogId:LongArray
    private lateinit var title: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityReadDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarReaddiary)
        val tb=supportActionBar!!
        tb.setDisplayShowTitleEnabled(false)
        tb.setDisplayHomeAsUpEnabled(true)

        triplogId=intent.getLongExtra("triplogId", 1)
        datelogId= intent.getLongArrayExtra("datelogId")!!
        title= intent.getStringExtra("title").toString()
        binding.tvReaddiaryTitle.text=title
        initialize();
        initReadDiaryRecyclerView();
        binding.recyclerviewReaddiary.addItemDecoration(WriteDividerItemDecoration(binding.recyclerviewReaddiary.context, R.drawable.creatediary_line_divider, 0,0))
        ReadDiaryApi();
    }

    private fun initialize(){
        readDiaryRecyclerViewData= arrayListOf()
    }

    private fun initReadDiaryRecyclerView() {
        val recyclerViewDiary = binding.recyclerviewReaddiary
        recyclerViewDiary.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        readDiaryRecyclerViewAdapter= ReadDiaryRecyclerViewAdapter(this, readDiaryRecyclerViewData)
        recyclerViewDiary.adapter = readDiaryRecyclerViewAdapter
    }

    private fun ReadDiaryApi(){
        val sharedManager : SharedManager by lazy { SharedManager(this@ReadDiaryActivity) }
        var authToken = sharedManager.getCurrentUser().accessToken
        ReadDiaryService.getRetrofitReadDateDiary(authToken, triplogId, datelogId[0]).enqueue(object: Callback<ReadDiaryResponse>{
            override fun onResponse(
                call: Call<ReadDiaryResponse>,
                response: Response<ReadDiaryResponse>
            ) {
                if(response.isSuccessful){
                    Log.e("question", response.toString())
                    Log.e("question", response.body().toString())

                    binding.tvReaddiaryDate.text=response.body()!!.response.date
                    binding.tvReaddiaryQuestion.text=response.body()!!.response.question.questionName
                    binding.tvReaddiaryAnswer.text=response.body()!!.response.answer
                    for(i in 0..response.body()!!.response.placeLogList.size-1){
                        val place=response.body()!!.response.placeLogList[i].place.name
                        val image=response.body()!!.response.placeLogList[i].userLogImg.imgUrl
                        val content=response.body()!!.response.placeLogList[i].comment
                        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData(place, image, content))
                    }
                    readDiaryRecyclerViewAdapter.notifyItemInserted(readDiaryRecyclerViewData.size)
                }else {
                    try {
                        val body = response.errorBody()!!.string()
                        Log.e(ContentValues.TAG, "body : $body")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<ReadDiaryResponse>, t: Throwable) {
                Log.e("TAG", "실패원인: {$t}")
            }
        })
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