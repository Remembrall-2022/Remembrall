package com.example.remembrall.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.remembrall.databinding.FragmentWriteDiaryBinding

class WriteDiaryFragment : Fragment() {
    private lateinit var binding: FragmentWriteDiaryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentWriteDiaryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


}