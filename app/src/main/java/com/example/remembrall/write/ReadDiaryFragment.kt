package com.example.remembrall.write

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.remembrall.MainActivity
import com.example.remembrall.databinding.FragmentReadDiaryBinding

class ReadDiaryFragment : Fragment() {
    private lateinit var binding: FragmentReadDiaryBinding

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

        Log.e("ReadDiary _write", "${binding.linearReaddiaryWrite.visibility}")
        Log.e("ReadDiary read", "${binding.linearReaddiaryRead.visibility}")
        binding.linearylayoutReaddiaryWrite.setOnClickListener{
            val intent = Intent(mainActivity, WriteDiaryActivity::class.java)
            startActivity(intent)
        }
    }


}