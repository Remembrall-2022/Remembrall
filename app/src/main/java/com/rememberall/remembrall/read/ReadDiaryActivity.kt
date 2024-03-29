package com.rememberall.remembrall.read

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.PagerAdapter.POSITION_NONE
import androidx.viewpager2.widget.ViewPager2
import com.rememberall.remembrall.R
import com.rememberall.remembrall.databinding.ActivityReadDiaryBinding
import com.rememberall.remembrall.login.userinfo.SharedManager
import com.rememberall.remembrall.read.Triplog.CreateTriplogDialog
import com.rememberall.remembrall.read.Triplog.UpdateTriplogDialog
import com.rememberall.remembrall.write.DiaryListDialog
import com.rememberall.remembrall.write.SelectDiaryListRecyclerViewAdapter
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Math.abs

class ReadDiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadDiaryBinding
    private lateinit var readDiaryRecyclerViewData: ArrayList<ReadDiaryRecyclerViewData>
    private lateinit var readDiaryRecyclerViewAdapter: ReadDiaryRecyclerViewAdapter
    private lateinit var viewPagerData: ArrayList<ViewPagerData>
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private var triplogId: Long=1
    private lateinit var datelogId:LongArray
    private lateinit var title: String
    private var page=0

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
        Log.e("id", "triplodId: ${triplogId}  datelogId: ${datelogId[0]}")
        initialize();
        initReadDiaryRecyclerView();
//        binding.recyclerviewReaddiary.addItemDecoration(WriteDividerItemDecoration(binding.recyclerviewReaddiary.context, R.drawable.creatediary_line_divider, 0,0))
//        ReadDiaryApi();
        ReadAllDiary();
    }

    private fun initialize(){
        readDiaryRecyclerViewData= arrayListOf()
        viewPagerData= arrayListOf()
    }

    private fun initReadDiaryRecyclerView() {
//        val recyclerViewDiary = findViewById<RecyclerView>(R.id.recyclerview_readdiary)
//        recyclerViewDiary.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
//        readDiaryRecyclerViewAdapter= ReadDiaryRecyclerViewAdapter(this, readDiaryRecyclerViewData)
//        recyclerViewDiary.adapter = readDiaryRecyclerViewAdapter


        var viewpager2=binding.viewpagerRead
        viewPagerAdapter=ViewPagerAdapter(this@ReadDiaryActivity, viewPagerData)
        viewpager2.orientation= ViewPager2.ORIENTATION_HORIZONTAL
        viewpager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                page=position
                Log.d("ViewPager", "Page ${position+1}")
            }
        })
    }

//    private fun ReadDiaryApi(){
//        val sharedManager : SharedManager by lazy { SharedManager(this@ReadDiaryActivity) }
//        var authToken = sharedManager.getCurrentUser().accessToken
//        ReadDiaryService.getRetrofitReadDateDiary(authToken, triplogId, datelogId[0]).enqueue(object: Callback<ReadDiaryResponse>{
//            override fun onResponse(
//                call: Call<ReadDiaryResponse>,
//                response: Response<ReadDiaryResponse>
//            ) {
//                if(response.isSuccessful){
//                    Log.e("question", response.toString())
//                    Log.e("question", response.body().toString())
//
//                    val date=response.body()!!.response.date
//                    val question=response.body()!!.response.question.questionName
//                    val answer=response.body()!!.response.answer
//                    for(i in 0..response.body()!!.response.placeLogList.size-1){
//                        val place=response.body()!!.response.placeLogList[i].place.name
//                        val image=response.body()!!.response.placeLogList[i].userLogImg.imgUrl
//                        val content=response.body()!!.response.placeLogList[i].comment
//                        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData(place, image, content))
//                    }
////                    readDiaryRecyclerViewAdapter.notifyItemInserted(readDiaryRecyclerViewData.size)
//
//                    viewPagerData.add(ViewPagerData(title, date,question,answer,readDiaryRecyclerViewData))
//                    viewPagerAdapter.notifyItemInserted(viewPagerData.size)
//
//                }else {
//                    try {
//                        val body = response.errorBody()!!.string()
//                        Log.e(ContentValues.TAG, "body : $body")
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<ReadDiaryResponse>, t: Throwable) {
//                Log.e("TAG", "실패원인: {$t}")
//            }
//        })
//    }

    private fun ReadAllDiary(){
        val sharedManager : SharedManager by lazy { SharedManager(this@ReadDiaryActivity) }
        var authToken = sharedManager.getCurrentUser().accessToken
        ReadDiaryService.getRetrofitAllDiary(authToken!!, triplogId).enqueue(object: Callback<ReadAllDiaryResponse>{
            override fun onResponse(
                call: Call<ReadAllDiaryResponse>,
                response: Response<ReadAllDiaryResponse>
            ) {
                if(response.isSuccessful){
//                    Log.e("diary", response.toString())
                    Log.e("diary", response.body().toString())
                    for(i in 0..(response.body()!!.response.dateLogResponseDtoList.size-1)){
                        var list= arrayListOf<ReadDiaryRecyclerViewData>()

                        val date=response.body()!!.response.dateLogResponseDtoList[i].date
                        var question=response.body()!!.response.dateLogResponseDtoList[i].question.questionName
                        val answer=response.body()!!.response.dateLogResponseDtoList[i].answer

                        if (question == null){
                            question = ""
                        }

                        if(response.body()!!.response.dateLogResponseDtoList[i].placeLogList!=null){
                            for(j in 0..(response.body()!!.response.dateLogResponseDtoList[i].placeLogList.size-1)) {
                                val place = response.body()!!.response.dateLogResponseDtoList[i].placeLogList[j].place.name
                                val image = response.body()!!.response.dateLogResponseDtoList[i].placeLogList[j].userLogImg.imgUrl
                                val content = response.body()!!.response.dateLogResponseDtoList[i].placeLogList[j].comment
                                list.add(ReadDiaryRecyclerViewData(place, image, content))
                            }
                        }
//                        readDiaryRecyclerViewData.addAll(list)
                        viewPagerData.add(ViewPagerData(title, date ,question,answer,list))
                    }
//                    readDiaryRecyclerViewAdapter.notifyItemInserted(readDiaryRecyclerViewData.size)
                    viewPagerAdapter.notifyDataSetChanged()
                    val viewPager=binding.viewpagerRead
                    viewPager.adapter=viewPagerAdapter
                    viewPager.offscreenPageLimit=4
                    val offsetBetweenPages = resources.getDimensionPixelOffset(R.dimen.offsetBetweenPages).toFloat()
                    viewPager.setPageTransformer { page, position ->
                        val myOffset = position * -(2 * offsetBetweenPages)
                        if (position < -1) {
                            page.translationX = -myOffset
                        } else if (position <= 1) {
                            // Paging 시 Y축 Animation 배경색을 약간 연하게 처리
                            val scaleFactor = 0.95f.coerceAtLeast(1 - abs(position))
                            page.translationX = myOffset
                            page.scaleY = scaleFactor
                            page.alpha = scaleFactor
                        } else {
                            page.alpha = 0f
                            page.translationX = myOffset
                        }
                    }
                    Log.e("viewPagerAdapter", "${viewPagerData.size}")

                }else {
                    try {
                        val body = response.errorBody()!!.string()
                        Log.e(ContentValues.TAG, "body : $body")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<ReadAllDiaryResponse>, t: Throwable) {
                Log.e("TAG", "실패원인: {$t}")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.readdiary_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {	//뒤로가기 버튼이 작동하도록
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.toolbar_readdiary_delete->{
                val deleteDiaryDialog = DeleteDateLogDialog(this@ReadDiaryActivity, triplogId, datelogId[page])
                deleteDiaryDialog.setOnClickListener(object : DeleteDateLogDialog.OnDialogClickListener{
                    override fun onClicked() {
//                        viewPagerAdapter.notifyItemRemoved(page)
//                        viewPagerAdapter.notifyItemRangeChanged(page, readDiaryRecyclerViewData.size)
//                        viewPagerAdapter.notifyDataSetChanged()
                        initialize();
                        initReadDiaryRecyclerView();
                        ReadAllDiary()
                    }
                })
                deleteDiaryDialog.show()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onResume(){
        super.onResume()

    }
}