package com.rememberall.remembrall.map.Gallery

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
data class TourRecommendResponse(
    @Element(name = "body")
    val body: Body,
    @Element(name="header")
    val header: Header
)

@Xml(name="header")
data class Header(
    @PropertyElement(name="resultCode")
    val resultCode: Int,
    @PropertyElement(name="resultMsg")
    val resultMsg: String
)

@Xml(name = "body")
data class Body(
    @Element(name="items")
    val items: Items,
    @PropertyElement(name="numOfRows")
    val numOfRows: Int,
    @PropertyElement(name="pageNo")
    val pageNo: Int,
    @PropertyElement(name="totalCount")
    val totalCount: Int
)

@Xml
data class Items(
    @Element(name="item")
    val item: List<Item>
)

@Xml
data class Item(
    @PropertyElement(name = "addr1")
    val addr1: String?,
    @PropertyElement(name = "contentID")
    val contentid: String?,
    @PropertyElement(name = "firstimage")
    val firstimage: String?,
    @PropertyElement(name = "mapx")
    val latitude: Double?,
    @PropertyElement(name = "mapy")
    val longitude: Double?,
    @PropertyElement(name = "title")
    val title: String?
){
    constructor() : this(null,null,null,null,null,null)
}

