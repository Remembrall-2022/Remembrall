package com.example.remembrall.read

data class ViewPagerData (
    val title: String,
    val date:String,
    val question: String,
    val answer: String,
    val placeInfo: List<ReadDiaryRecyclerViewData>
)