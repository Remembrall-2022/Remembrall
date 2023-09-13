package com.rememberall.remembrall.read

data class ViewPagerData (
    val date:String,
    val question: String,
    val answer: String,
    val placeInfo: List<ReadDiaryRecyclerViewData>
)