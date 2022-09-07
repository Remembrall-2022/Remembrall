package com.example.remembrall.read

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import com.example.remembrall.MainActivity
import com.example.remembrall.R
import com.example.remembrall.databinding.FragmentReadDiaryListBinding
import com.example.remembrall.login.userinfo.SharedManager
import com.example.remembrall.read.Triplog.DeleteTriplogDialog
import com.example.remembrall.read.Triplog.TriplogCreateDialog
import com.example.remembrall.read.Triplog.TriplogService
import com.example.remembrall.read.Triplog.req.TriplogRequest
import com.example.remembrall.read.Triplog.res.CreateTriplogResponse
import com.example.remembrall.read.Triplog.res.GetTriplogListResponse
import com.example.remembrall.write.ReadDiaryFragment
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReadDiaryListFragment : Fragment() {
    private lateinit var binding: FragmentReadDiaryListBinding
    private lateinit var readDiaryRecyclerViewData: ArrayList<ReadDiaryListRecyclerViewData>
    private lateinit var readDiaryListRecyclerViewAdapter: ReadDiaryListRecyclerViewAdapter
    private var pos=0

    lateinit var mainActivity: MainActivity

    val client = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
                .setLevel(HttpLoggingInterceptor.Level.BODY)
                .setLevel(HttpLoggingInterceptor.Level.HEADERS)
        )
        .build()

    var retrofit = Retrofit.Builder()
        .baseUrl("http://ec2-13-124-98-176.ap-northeast-2.compute.amazonaws.com:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
    var triplogService : TriplogService = retrofit.create(TriplogService::class.java)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReadDiaryListBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initalize()
        initReadDiaryRecyclerView()
        binding.recyclerviewReaddiarylist.addItemDecoration(
            DividerItemDecoration(binding.recyclerviewReaddiarylist.context,
                R.drawable.readdiary_line_divider, 0,0))
        clickRecyclerView()
        binding.floatingReaddiarylist.setOnClickListener {
            val triplogCreateDialog = TriplogCreateDialog(mainActivity)
            triplogCreateDialog.show()
            triplogCreateDialog.setOnClickListener(object : TriplogCreateDialog.OnDialogClickListener{
                override fun onClicked(readDiaryRecyclerViewDataList: ArrayList<ReadDiaryListRecyclerViewData>) {
                    readDiaryRecyclerViewData = readDiaryRecyclerViewDataList
                    readDiaryListRecyclerViewAdapter.notifyItemInserted(readDiaryRecyclerViewData.size)
                    var readDiaryFragment = ReadDiaryListFragment()
                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout_main, readDiaryFragment).commit()
                }
            })
        }
    }

    private fun httpLoggingInterceptor(): HttpLoggingInterceptor? {
        val interceptor = HttpLoggingInterceptor { message ->
            Log.e(
                "HttpLogging:",
                message + ""
            )
        }
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    }


    private fun initalize(){
        val sharedManager : SharedManager by lazy { SharedManager(mainActivity) }
        var authToken = sharedManager.getCurrentUser().accessToken
        readDiaryRecyclerViewData= arrayListOf()
        triplogService.getTripLogList(authToken).enqueue(object :
            Callback<GetTriplogListResponse> {
            override fun onResponse(
                call: Call<GetTriplogListResponse>,
                response: Response<GetTriplogListResponse>
            ) {
                Log.e("CreateTripLog", response.body().toString())

                if(response.body()?.success.toString() == "true"){
                    var diaryList = response.body()?.response!!
                    if(diaryList != null){
                        for (diary in diaryList){
                            val title=diary!!.title.toString()
                            val imgUrl=diary!!.tripLogImgUrl.toString()
                            val triplogId=diary!!.triplogId!!.toLong()

                            val tripStartDate=diary!!.tripStartDate
                            val tripEndDate=diary!!.tripEndDate
                            val startDate=tripStartDate.toString().split('-')
                            val sDate=startDate[0].toString().split('0')
                            val endDate=tripEndDate.toString().split('-')
                            val eDate=startDate[0].toString().split('0')
                            val tripDate=sDate[1]+'.'+startDate[1]+'.'+startDate[2]+" ~ "+eDate[1]+'.'+endDate[1]+'.'+endDate[2]

                            readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData(title, imgUrl, triplogId, tripDate))
                        }
                        readDiaryListRecyclerViewAdapter.notifyItemInserted(readDiaryRecyclerViewData.size)
                        binding.llNoDiary.visibility = View.GONE
                        if(diaryList.size == 0){
                            binding.llNoDiary.visibility = View.VISIBLE
                        }
                    }
                }
            }
            override fun onFailure(call: Call<GetTriplogListResponse>, t: Throwable) {
                Toast.makeText(context,"일기장 불러오기 실패", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun initReadDiaryRecyclerView() {
        val recyclerViewDiaryList = binding.recyclerviewReaddiarylist
        recyclerViewDiaryList.layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        readDiaryListRecyclerViewAdapter= ReadDiaryListRecyclerViewAdapter(mainActivity, readDiaryRecyclerViewData)
        recyclerViewDiaryList.adapter = readDiaryListRecyclerViewAdapter

    }

    private fun clickRecyclerView(){
        readDiaryListRecyclerViewAdapter.setItemClickListener(object: ReadDiaryListRecyclerViewAdapter.OnItemClickListener{
            override fun diaryOnClick(v: View, position: Int) {  //일기 불러오기
                Log.e("tag", "일기장 클릭")
                val sharedManager : SharedManager by lazy { SharedManager(mainActivity) }
                var authToken = sharedManager.getCurrentUser().accessToken

                ReadDiaryService.getRetrofitReadTripLog(authToken, readDiaryRecyclerViewData[position].triplogId).enqueue(object: Callback<ReadTripLogResponse>{
                    override fun onResponse(
                        call: Call<ReadTripLogResponse>,
                        response: Response<ReadTripLogResponse>
                    ) {
                        if(response.isSuccessful){
                            Log.e("diaryInfo", response.toString())
                            Log.e("diaryInfo", response.body().toString())

                            val triplogId=response.body()!!.response.triplogId
                            val datelogId=response.body()!!.response.placeLogIdList
                            if(datelogId.size==0){ //일기가 없을 때
                                Toast.makeText(mainActivity, "아직 작성한 일기가 없어요!\n오늘의 일기를 작성해보면 어떨까요?",Toast.LENGTH_LONG).show()
                                // 빈 탭을 하나 넣어서 일기쓰러가는 버튼 넣기
                            }
                            else{
                                val title=response.body()!!.response.title
                                val intent = Intent(mainActivity, ReadDiaryActivity::class.java)
                                var array= datelogId.toLongArray()
                                intent.putExtra("triplogId", triplogId)
                                intent.putExtra("datelogId", array)
                                intent.putExtra("title", title)
                                startActivity(intent)
                            }
                        }else {
                            try {
                                val body = response.errorBody()!!.string()
                                Log.e(ContentValues.TAG, "body : $body")
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ReadTripLogResponse>, t: Throwable) {

                    }
                })
//                binding.recyclerviewReaddiarylist[position]

            }

//            override fun heartOnClick(v: View, position: Int) { //북마크
//                binding.recyclerviewReaddiarylist[position].findViewById<ImageView>(R.id.img_adddiary_heart).setImageResource(R.drawable.ic_bookmark_fill)
//                binding.recyclerviewReaddiarylist[position].findViewById<ImageView>(R.id.img_adddiary_heart).setImageResource(R.drawable.ic_bookmark_heart)
//            }
        })
        readDiaryListRecyclerViewAdapter.setItemLongClickListener(object : ReadDiaryListRecyclerViewAdapter.OnItemLongClickListener{
            override fun diaryLongClick(v: View, position: Int) {
                val deleteTriplogDialog = DeleteTriplogDialog(mainActivity, readDiaryRecyclerViewData[position].triplogId, readDiaryRecyclerViewData[position].name)
                deleteTriplogDialog.show()
                deleteTriplogDialog.setOnClickListener(object : DeleteTriplogDialog.OnDialogClickListener{
                    override fun onClicked() {
                        var readDiaryFragment = ReadDiaryListFragment()
                        mainActivity.supportFragmentManager.beginTransaction()
                            .replace(R.id.framelayout_main, readDiaryFragment).commit()
                    }
                })
            }
        })
    }

    }
