package com.example.remembrall.map

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.remembrall.MainActivity
import com.example.remembrall.R
import com.example.remembrall.databinding.FragmentMapSearchBinding
import com.example.remembrall.map.Gallery.TourRecommendApi
import com.example.remembrall.map.Gallery.TourRecommendResponse
import com.example.remembrall.map.MapSearch.KakaoMapApi
import com.example.remembrall.map.MapSearch.ResultSearchKeyword
import com.example.remembrall.map.MapSearch.RvMapSearch
import com.example.remembrall.map.MapSearch.RvMapSearchAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.w3c.dom.Element
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory


class MapSearchFragment() : Fragment() {
    var mapView: MapView ?= null
    var uLatitude: Double ?= null
    var uLongitude: Double ?= null
    lateinit var rvMapSearchAdapter : RvMapSearchAdapter
    var binding: FragmentMapSearchBinding ?= null

    // Context를 액티비티로 형변환해서 할당
    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    // 검색결과 recyclerView
    var mapSearchItemList = ArrayList<RvMapSearch>()

    companion object {
        // TODO : url key에서 들고오기
        const val BASE_URL = "https://dapi.kakao.com/"
//        const val GALLERY_API_URL = "http://apis.data.go.kr/B551011/PhotoGalleryService"
        const val API_KEY = "KakaoAK 6bc1728a7e229d952ece08fa28b0bdab"   // REST API 키
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMapSearchBinding.inflate(inflater, container, false)

        // map view 연결
        val mapView = MapView(mainActivity)
        binding!!.clKakaoMapView.addView(mapView)
        binding!!.clKakaoMapView.clipToOutline=true

        startTracking()
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!), 7, true) // 중심점 변경 + 줌 레벨 변경

        // TODO : java.lang.RuntimeException: DaumMap does not support that two or more net.daum.mf.map.api.MapView objects exists at the same time

        // 현 위치에 마커 찍기
        val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!)
        val marker = MapPOIItem()
        marker.itemName = "현 위치"
        marker.mapPoint =uNowPosition
        marker.markerType = MapPOIItem.MarkerType.BluePin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin

        mapView.addPOIItem(marker)
        mapView.zoomIn(true) // 줌 인
        mapView.zoomOut(true) // 줌 아웃

        val rv = binding!!.bottomsheetMapSearchView.rvMapSearch
        Log.e(TAG, rv.toString())
        rv?.layoutManager = LinearLayoutManager(mainActivity)
        rvMapSearchAdapter = RvMapSearchAdapter(mainActivity)
        rv?.adapter = rvMapSearchAdapter

        var thread = NetworkThread()
        thread.start()

        val client = OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
                    .setLevel(HttpLoggingInterceptor.Level.HEADERS)
            )
            .build()

        val parser = TikXml.Builder().exceptionOnUnreadXml(false).build()

        val retrofit_recommend = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(getString(R.string.TOUR_APY_URL))
            .addConverterFactory(TikXmlConverterFactory.create(parser))
            .client(client)
            .build()

        val recommendPlace = retrofit_recommend.create(TourRecommendApi::class.java)

//        recommendPlace.getTourList("AND", "AppTest", getString(R.string.TOUR_API_DECODING_KEY),uLongitude.toString(), uLatitude.toString(), "20000")
//            .enqueue(object : Callback<TourRecommendResponse>{
//                override fun onResponse(
//                    call: Call<TourRecommendResponse>,
//                    response: Response<TourRecommendResponse>
//                ) {
//                    val responseRecommendPlace = response.body()?.body?.items
//                    Log.e("responseRecommendPlace", response.toString())
////                    for (item in responseRecommendPlace){
////                        val searchItem = RvMapSearch(
////                            "이름",
////                            "카테고리",
////                            "주소",
////                            "url",
////                            2.2,
////                            2.2)
////                        mapSearchItemList.add(searchItem)
////                    }
////
////
//
//
//                }
//                override fun onFailure(call: Call<TourRecommendResponse>, t: Throwable) {
//                   Log.e(tag, "관광지 추천 실패")
//                }
//
//            })


        rvMapSearchAdapter.setDataList(mapSearchItemList)

        val bottomSheet : View = binding!!.llBottomsheet
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        // 드래그해도 팝업이 종료되지 않도록
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_COLLAPSED-> {
//                        Toast.makeText(mainActivity,"최소화",Toast.LENGTH_LONG).show()
                    }
                    BottomSheetBehavior.STATE_DRAGGING-> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED-> {
//                        Toast.makeText(mainActivity,"확장",Toast.LENGTH_LONG).show()
                    }
                    BottomSheetBehavior.STATE_HIDDEN-> {
                    }
                    BottomSheetBehavior.STATE_SETTLING-> {
//                        Toast.makeText(mainActivity, "중간", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        // 검색 결과 처리 함수
        fun addItemsAndMarkers(searchResult: ResultSearchKeyword?){
            if(!searchResult?.documents.isNullOrEmpty()){
                mapSearchItemList.clear()
                mapView.removeAllPOIItems()
                for (document in searchResult!!.documents){
                    // 결과 리사이클러 뷰 추가
                    val searchItem = RvMapSearch(
                        document.place_name,
                        document.category_group_name,
                        document.road_address_name,
                        document.place_url,
                        document.x.toDouble(),
                        document.y.toDouble())
                    mapSearchItemList.add(searchItem)
                    // 지도 마커 추가
                    val point = MapPOIItem()
                    point.apply {
                        itemName = document.place_name
                        mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(), document.x.toDouble())
                        markerType = MapPOIItem.MarkerType.BluePin
                        selectedMarkerType = MapPOIItem.MarkerType.RedPin
                    }
                    mapView.addPOIItem(point)
                }
                rvMapSearchAdapter.notifyDataSetChanged()
                Log.e("searchResult", mapSearchItemList[0].place_name)
                mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(mapSearchItemList[0].y, mapSearchItemList[0].x), 7, true) // 중심점 변경 + 줌 레벨 변경
            }
            else{
                Toast.makeText(mainActivity, "검색 결과가 없습니다",Toast.LENGTH_SHORT).show()
            }
        }

        // 키워드 검색 함수
        fun searchKeyword(keyword: String){
            val retrofit_map = Retrofit.Builder()   // Retrofit 구성
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit_map.create(KakaoMapApi::class.java)   // 통신 인터페이스를 객체로 생성
            val call = api.getSearchKeyword(API_KEY, keyword)   // 검색 조건 입력
            // API 서버에 요청
            call.enqueue(object: Callback<ResultSearchKeyword> {
                override fun onResponse(
                    call: Call<ResultSearchKeyword>,
                    response: Response<ResultSearchKeyword>
                ) {
                    // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                    Log.d("Test", "Raw: ${response.raw()}")
                    Log.d("Test", "Body: ${response.body()}")
                    addItemsAndMarkers(response.body())
                }

                override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                    // 통신 실패
                    Log.w("MainActivity", "통신 실패: ${t.message}")
                }
            })
        }

        // 키워드 검색
        binding!!.searchViewMapSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchKeyword(query!!)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return binding!!.root
    }

    //현재 유저 위치에 대한 정보
    @SuppressLint("MissingPermission") // 나중에 user 권한 받을 수 있으면 받기
    private fun startTracking() {
        mapView?.currentLocationTrackingMode =MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading  //이 부분

        val lm: LocationManager = mainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val userNowLocation: Location? = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        //위도 , 경도
        uLatitude = userNowLocation?.latitude
        uLongitude = userNowLocation?.longitude
    }

    // 위치추적 중지
    private fun stopTracking() {
        mapView?.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    inner class NetworkThread: Thread(){
        override fun run() {
            try {
                // 접속할 페이지의 주소
                var site = getString(R.string.TOUR_APY_URL)+"/B551011/KorService/locationBasedList"+"?MobileOS=AND&MobileApp=AppTest&serviceKey="+getString(R.string.TOUR_API_DECODING_KEY)+"&mapX="+uLongitude.toString()+"&mapY="+uLatitude+"&radius=20000"
                var url = URL(site)
                var conn = url.openConnection()
                var input = conn.getInputStream()

                var factory = DocumentBuilderFactory.newInstance()
                var builder = factory.newDocumentBuilder()
                // doc: xml문서를 모두 읽어와서 분석을 끝냄
                var doc = builder.parse(input)

                // root: xml 문서의 모든 데이터들을 갖고 있는 객체
                var root = doc.documentElement

                // xml 문서에서 태그 이름이 item인 태그들이 item_node_list에 리스트로 담김
                var item_node_list = root.getElementsByTagName("item")

                // item_node_list에 들어있는 태그 객체 수만큼 반복함
                for(i in 0 until item_node_list.length){
                    // i번째 태그 객체를 item_element에 넣음
                    var item_element = item_node_list.item(i) as Element

                    // item태그 객체에서 원하는 데이터를 태그이름을 이용해서 데이터를 가져옴
                    // xml 문서는 태그 이름으로 데이터를 가져오면 무조건 리스트로 나옴
                    var addr_list = item_element.getElementsByTagName("addr1")
                    var firstImage_list = item_element.getElementsByTagName("firstImage")
                    var mapX_list = item_element.getElementsByTagName("mapX")
                    var mapY_list = item_element.getElementsByTagName("mapY")
                    var title_list = item_element.getElementsByTagName("title")


                    var addr_node = addr_list.item(0) as Element
                    var firstImage_node = firstImage_list.item(0) as Element
                    var mapX_node = mapX_list.item(0) as Element
                    var mapY_node = mapY_list.item(0) as Element
                    var title_node = title_list.item(0) as Element

                    // 태그 사이에 있는 문자열을 가지고 오는 작업
                    var addr = addr_node.textContent
                    var firstImage = firstImage_node.textContent
                    var mapX = mapX_node.textContent
                    var mapY = mapY_node.textContent
                    var title = title_node.textContent

                    Log.e("TourAPI", title)
                    // Ui에 데이터를 출력해주는 부분
                    val searchItem = RvMapSearch(
                            title,
                            "카테고리",
                            addr,
                            firstImage,
                            mapX.toDouble(),
                            mapY.toDouble())
                    mapSearchItemList.add(searchItem)
                    rvMapSearchAdapter.notifyDataSetChanged()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
}
}