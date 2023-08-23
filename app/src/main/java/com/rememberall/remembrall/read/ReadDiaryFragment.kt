package com.rememberall.remembrall.read

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rememberall.remembrall.GlobalApplication
import com.rememberall.remembrall.MainActivity
import com.rememberall.remembrall.databinding.FragmentReadDiaryBinding
import com.rememberall.remembrall.login.userinfo.SharedManager
import com.rememberall.remembrall.write.GetQuestionResponse
import com.rememberall.remembrall.write.WriteDiaryActivity
import com.rememberall.remembrall.write.WriteDiaryService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate
import java.util.*

class ReadDiaryFragment : Fragment() {
    private lateinit var binding: FragmentReadDiaryBinding
    private lateinit var question: String
    private var id: Long=1
    // Context를 액티비티로 형변환해서 할당
    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    //나중에 여행지 추천해주는 것도 넣기
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentReadDiaryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedManager : SharedManager by lazy { SharedManager(mainActivity) }
        var authToken = sharedManager.getCurrentUser().accessToken

        val calendar= Calendar.getInstance()
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        val today=GlobalApplication.prefs.getString("today","")
        if(today!=day.toString()) { //날짜가 다름
            WriteDiaryService.getRetrofitRefreshQuestion(authToken!!)
                .enqueue(object : Callback<GetQuestionResponse> {
                    override fun onResponse(
                        call: Call<GetQuestionResponse>,
                        response: Response<GetQuestionResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.e("call question", response.toString())
                            Log.e("call question", response.body().toString())

                            question = response.body()!!.response.questionName
                            id = response.body()!!.response.id
                            binding.textviewCreatediaryQuestion.text = question

                            GlobalApplication.prefs.setString("today", day.toString())
                            GlobalApplication.prefs.setString("today_question", question)
                            GlobalApplication.prefs.setString("today_questionId", id.toString())

                        } else {
                            try {
                                val body = response.errorBody()!!.string()
                                Log.e(ContentValues.TAG, "body : $body")
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetQuestionResponse>, t: Throwable) {
                        TODO("Not yet implemented")
                    }
                })
        }
        else{
            binding.textviewCreatediaryQuestion.text = GlobalApplication.prefs.getString("today_question", "오늘은 점심으로 무엇을 드셨나요?")

        }

        Log.e("ReadDiary _write", "${binding.linearReaddiaryWrite.visibility}")
        Log.e("ReadDiary read", "${binding.linearReaddiaryRead.visibility}")
        binding.linearylayoutReaddiaryWrite.setOnClickListener{
            val intent = Intent(mainActivity, WriteDiaryActivity::class.java)
            val todayQuestion=GlobalApplication.prefs.getString("today_question", "")
            val todayQuestionId=GlobalApplication.prefs.getString("today_questionId", "")
            intent.putExtra("question", todayQuestion)
            intent.putExtra("questionId", todayQuestionId.toLong())
            startActivity(intent)
        }
    }

}