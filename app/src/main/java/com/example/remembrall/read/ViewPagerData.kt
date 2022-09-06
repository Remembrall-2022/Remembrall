package com.example.remembrall.read

data class ViewPagerData (
    val title: String,
    val date:String,
    val queston: String,
    val answer: String,
    val placeInfo: List<ReadDiaryRecyclerViewData>
)