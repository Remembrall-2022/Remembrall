package com.rememberall.remembrall.read

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rememberall.remembrall.GlobalApplication
import com.rememberall.remembrall.MainActivity
import com.rememberall.remembrall.R
import com.rememberall.remembrall.databinding.FragmentReadTodayDiaryBinding
import com.rememberall.remembrall.user.userinfo.SharedManager
import com.rememberall.remembrall.write.WriteDiaryActivity
import com.rememberall.remembrall.write.WriteDiaryRecyclerViewData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ReadTodayDiaryFragment : Fragment() {
    private lateinit var binding: FragmentReadTodayDiaryBinding
    lateinit var mainActivity: MainActivity
    private lateinit var readDiaryRecyclerViewAdapter: ReadDiaryRecyclerViewAdapter
    private lateinit var readDiaryRecyclerViewData: ArrayList<ReadDiaryRecyclerViewData>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentReadTodayDiaryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()

        val sharedManager : SharedManager by lazy { SharedManager(mainActivity) }
        var authToken = sharedManager.getCurrentUser().accessToken

        ReadDiaryService.getRetrofitTodayDiary(authToken!!).enqueue(object:
            Callback<ReadDiaryResponse> {
            override fun onResponse(
                call: Call<ReadDiaryResponse>,
                response: Response<ReadDiaryResponse>
            ) {
                if(response.isSuccessful){
//                    Log.e("diary", response.toString())
                    Log.e("diary", response.body().toString())

                    var data= response.body()
                    var date= data?.date
                    var question=data?.question?.questionName
                    var answer=data?.answer
                    var placeLogList=data?.placeLogList

                    binding.tvTodaydiaryDate.text=date
                    binding.tvTodaydiaryQuestion.text=question
                    binding.tvTodaydiaryAnswer.setText(answer)

                    var list= arrayListOf<ReadDiaryRecyclerViewData>()

                    if (placeLogList != null) {
                        for (placeLog in placeLogList) {
                            var place = placeLog.place
                            var name = place.name
                            var userLogImg = placeLog.userLogImg
                            var imgUrl = userLogImg.imgUrl
                            var content = placeLog.comment

                            list.add(ReadDiaryRecyclerViewData(name, imgUrl, content))
                        }
                    }

                    readDiaryRecyclerViewAdapter= ReadDiaryRecyclerViewAdapter(mainActivity, list)

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

            }
        })
        binding.floatingTodaydiary.setOnClickListener{
            val intent= Intent(mainActivity,WriteDiaryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initialize() {
        readDiaryRecyclerViewData = arrayListOf()
    }
}
