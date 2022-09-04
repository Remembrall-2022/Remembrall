package com.example.remembrall.dongdong

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.remembrall.MainActivity
import com.example.remembrall.R
import com.example.remembrall.databinding.FragmentDongDongBinding
import com.example.remembrall.databinding.FragmentMapSearchBinding
import com.example.remembrall.setting.SettingFragment

class DongDongFragment : Fragment() {
    var binding: FragmentDongDongBinding?= null

    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDongDongBinding.inflate(inflater, container, false)

        binding!!.ivSetting.setOnClickListener {
            var settingFragment = SettingFragment()
            mainActivity.replaceFragment(settingFragment)
        }
        return binding!!.root
    }

    companion object {

    }
}