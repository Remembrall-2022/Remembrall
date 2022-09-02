package com.example.remembrall.map.Gallery

data class GalleryResponse(
    val body: Body,
    val header: Header
)

data class Body(
    val items: Items,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class Header(
    val resultCode: String,
    val resultMsg: String
)

data class Items(
    val item: Item
)

data class Item(
    val galContentId: String,
    val galContentTypeId: String,
    val galCreatedtime: String,
    val galModifiedtime: String,
    val galPhotographer: String,
    val galPhotographyLocation: String,
    val galPhotographyMonth: String,
    val galSearchKeyword: String,
    val galTitle: String,
    val galViewCount: String,
    val galWebImageUrl: String
)