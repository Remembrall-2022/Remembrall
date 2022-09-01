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
import androidx.recyclerview.widget.RecyclerView
import com.example.remembrall.MainActivity
import com.example.remembrall.R
import com.example.remembrall.databinding.FragmentMapSearchBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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
        const val BASE_URL = "https://dapi.kakao.com/"
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


        val searchItem = RvMapSearch(
            "이름",
            "카테고리",
            "주소",
            "url",
            2.2,
            2.2)
        mapSearchItemList.add(searchItem)
        rvMapSearchAdapter.setDataList(mapSearchItemList)

        val bottomSheet : View = binding!!.llBottomsheet
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        // 드래그해도 팝업이 종료되지 않도록
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_COLLAPSED-> {
                        Toast.makeText(mainActivity,"최소화",Toast.LENGTH_LONG).show()
                    }
                    BottomSheetBehavior.STATE_DRAGGING-> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED-> {
                        Toast.makeText(mainActivity,"확장",Toast.LENGTH_LONG).show()
                    }
                    BottomSheetBehavior.STATE_HIDDEN-> {
                    }
                    BottomSheetBehavior.STATE_SETTLING-> {
                        Toast.makeText(mainActivity, "중간", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        // 검색 결과 처리 함수
        fun addItemsAndMarkers(searchResult: ResultSearchKeyword?){
            if(!searchResult?.documents.isNullOrEmpty()){
                Log.e("실행됐나?","ㅇㅇ")
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
            val retrofit = Retrofit.Builder()   // Retrofit 구성
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(KakaoMapApi::class.java)   // 통신 인터페이스를 객체로 생성
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

}