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
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import com.example.remembrall.MainActivity
import com.example.remembrall.R
import com.example.remembrall.databinding.FragmentReadDiaryListBinding
import com.example.remembrall.read.Triplog.TriplogCreateDialog

class ReadDiaryListFragment : Fragment() {
    private lateinit var binding: FragmentReadDiaryListBinding
    private lateinit var readDiaryRecyclerViewData: ArrayList<ReadDiaryListRecyclerViewData>
    private lateinit var readDiaryListRecyclerViewAdapter: ReadDiaryListRecyclerViewAdapter
    private var pos=0

    lateinit var mainActivity: MainActivity
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

    private fun initalize(){
        readDiaryRecyclerViewData= arrayListOf()
        readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData("창덕궁 나들이"))
        readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData("일기장2"))
        readDiaryRecyclerViewData.add(ReadDiaryListRecyclerViewData("일기장3"))
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
                binding.recyclerviewReaddiarylist[position].findViewById<ImageView>(R.id.img_adddiary_heart).setImageResource(R.drawable.ic_bookmark_heartfill)
//                binding.recyclerviewReaddiarylist[position].findViewById<ImageView>(R.id.img_adddiary_heart).setImageResource(R.drawable.ic_bookmark_heart)
            }
        })
    }

    }
