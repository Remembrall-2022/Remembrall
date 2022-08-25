package com.example.remembrall.read

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.remembrall.R
import com.example.remembrall.databinding.FragmentReadDiaryListBinding

class ReadDiaryListFragment : Fragment() {
    private lateinit var binding: FragmentReadDiaryListBinding
    private lateinit var readDiaryRecyclerViewData: ArrayList<ReadDiaryListRecyclerViewData>
//    private var recyclerView = binding.recyclerviewReaddiary

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
        binding.recyclerviewReaddiarylist.addItemDecoration(DividerItemDecoration(binding.recyclerviewReaddiarylist.context, R.drawable.readdiary_line_divider, 0,0))
    }

    private fun initalize(){
        readDiaryRecyclerViewData= arrayListOf()
        readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData("일기장1"))
        readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData("일기장2"))
        readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData("일기장3"))
        readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData("일기장4"))
        readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData("일기장5"))
        readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData("일기장6"))
        readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData("일기장7"))
        readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData("일기장7"))
        readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData("일기장7"))
        readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData("일기장7"))
        readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData("일기장7"))
        readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData("일기장7"))
        readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData("일기장7"))
    }

    private fun initReadDiaryRecyclerView() {
        binding.recyclerviewReaddiarylist.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
            adapter = ReadDiaryListRecyclerViewAdapter(
                requireContext(),
                readDiaryRecyclerViewData
            )
        }
    }

    }
