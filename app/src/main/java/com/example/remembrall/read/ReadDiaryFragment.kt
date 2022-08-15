package com.example.remebrall.read

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.remebrall.R
import com.example.remebrall.databinding.FragmentReadDiaryBinding

class ReadDiaryFragment : Fragment() {
    private lateinit var binding: FragmentReadDiaryBinding
    private lateinit var readDiaryRecyclerViewData: ArrayList<ReadDiaryRecyclerViewData>
//    private var recyclerView = binding.recyclerviewReaddiary

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReadDiaryBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initalize()
        initReadDiaryRecyclerView()
        binding.recyclerviewReaddiary.addItemDecoration(DividerItemDecoration(binding.recyclerviewReaddiary.context, R.drawable.readdiary_line_divider, 0,0))
    }

    private fun initalize(){
        readDiaryRecyclerViewData= arrayListOf()
        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData("일기장1"))
        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData("일기장2"))
        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData("일기장3"))
        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData("일기장4"))
        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData("일기장5"))
        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData("일기장6"))
        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData("일기장7"))
        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData("일기장7"))
        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData("일기장7"))
        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData("일기장7"))
        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData("일기장7"))
        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData("일기장7"))
        readDiaryRecyclerViewData.add(ReadDiaryRecyclerViewData("일기장7"))
    }

    private fun initReadDiaryRecyclerView() {
        binding.recyclerviewReaddiary.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
            adapter = ReadDiaryRecyclerViewAdapter(
                requireContext(),
                readDiaryRecyclerViewData
            )
        }
    }

    }
