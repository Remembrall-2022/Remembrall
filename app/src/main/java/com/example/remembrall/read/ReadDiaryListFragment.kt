package com.example.remembrall.read

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
import com.example.remembrall.read.Triplog.TriplogCreateDialog
import com.example.remembrall.read.Triplog.TriplogService
import com.example.remembrall.read.Triplog.req.TriplogRequest
import com.example.remembrall.read.Triplog.res.CreateTriplogResponse
import com.example.remembrall.read.Triplog.res.GetTriplogListResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
            TriplogCreateDialog(mainActivity).show()
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
                            readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData(diary!!.title.toString()))
                            readDiaryListRecyclerViewAdapter.notifyDataSetChanged()
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
//                binding.recyclerviewReaddiarylist[position]
                val intent = Intent(mainActivity, ReadDiaryActivity::class.java)
                startActivity(intent)
            }

            override fun heartOnClick(v: View, position: Int) { //북마크
                binding.recyclerviewReaddiarylist[position].findViewById<ImageView>(R.id.img_adddiary_heart).setImageResource(R.drawable.ic_bookmark_fill)
//                binding.recyclerviewReaddiarylist[position].findViewById<ImageView>(R.id.img_adddiary_heart).setImageResource(R.drawable.ic_bookmark_heart)
            }
        })
    }

    }
