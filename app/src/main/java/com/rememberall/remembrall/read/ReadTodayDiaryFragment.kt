package com.rememberall.remembrall.read

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rememberall.remembrall.MainActivity
import com.rememberall.remembrall.R
import com.rememberall.remembrall.databinding.FragmentReadTodayDiaryBinding
import com.rememberall.remembrall.write.WriteDiaryActivity

class ReadTodayDiaryFragment : Fragment() {
    private lateinit var binding: FragmentReadTodayDiaryBinding
    lateinit var mainActivity: MainActivity

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
        binding.floatingTodaydiary.setOnClickListener{
            val intent= Intent(mainActivity,WriteDiaryActivity::class.java)
            startActivity(intent)
        }
    }
}