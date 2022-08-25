package com.example.remembrall.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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


class MapSearchFragment : Fragment() {
    var mapView: MapView ?= null
    var uLatitude: Double ?= null
    var uLongitude: Double ?= null

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 6bc1728a7e229d952ece08fa28b0bdab"   // REST API 키
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapSearchFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
        }
    }

    // Context를 액티비티로 형변환해서 할당
    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var binding = FragmentMapSearchBinding.inflate(inflater, container, false)
        // 팝업 생성 시 전체화면으로 띄우기

            val mapView = MapView(mainActivity) // map view 연결
        binding.clKakaoMapView.addView(mapView)

        startTracking()
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!), 7, true); // 중심점 변경 + 줌 레벨 변경

        // 현 위치에 마커 찍기
        val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!)
        val marker = MapPOIItem()
        marker.itemName = "현 위치"
        marker.mapPoint =uNowPosition
        marker.markerType = MapPOIItem.MarkerType.BluePin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin

        mapView?.addPOIItem(marker)
        mapView.zoomIn(true); // 줌 인
        mapView.zoomOut(true); // 줌 아웃

        binding.searchViewMapSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchKeyword(query!!)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        val bottomSheet : View = binding.bottomsheetMapSearchView
        val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        // 드래그해도 팝업이 종료되지 않도록
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
//                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
//                }
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
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


        return binding.root
    }

    // 키워드 검색 함수
    private fun searchKeyword(keyword: String) {
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
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("MainActivity", "통신 실패: ${t.message}")
            }
        })

    }

    @SuppressLint("MissingPermission") // 나중에 user 권한 받을 수 있으면 받기
    private fun startTracking() {
        mapView?.currentLocationTrackingMode =MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading  //이 부분

        val lm: LocationManager = mainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val userNowLocation: Location? = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        //위도 , 경도
        uLatitude = userNowLocation?.latitude
        uLongitude = userNowLocation?.longitude

        Log.e("실행됐나?", "ㅇㅇ")
    }

    // 위치추적 중지
    private fun stopTracking() {
        mapView?.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }


}