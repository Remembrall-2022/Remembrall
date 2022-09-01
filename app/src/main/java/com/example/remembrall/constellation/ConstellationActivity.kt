package com.example.remembrall.constellation

import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.example.remembrall.R
import com.example.remembrall.databinding.ActivityConstellationBinding
import com.example.remembrall.databinding.ActivityMainBinding
import com.ssomai.android.scalablelayout.ScalableLayout

class ConstellationActivity : AppCompatActivity() {
    private lateinit var binding : ActivityConstellationBinding
    private var constellationMapWidth = 0
    private var constellationMapHeight = 0
    private var touristDestinationList = ArrayList<TouristDestination>()
    private var mapConstellation: ScalableLayout ?= null
    private val iv = ArrayList<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityConstellationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapConstellation = binding.mapConstellation

//        binding.mapConstellation.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                constellationMapWidth = mapConstellation!!.width // ScalableLayout width 구하기
//
//                // 다 쓰고 리스너 삭제
//                binding.mapConstellation.viewTreeObserver.removeOnGlobalLayoutListener(this)
//            }
//        })

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size) // or getSize(size)
        val width = size.x
        val height = size.y

        Log.e("mapConstellation", width.toString())

        touristDestinationList.add(TouristDestination(37.537229,127.005515))
        touristDestinationList.add(TouristDestination(37.545024,127.03923))
        touristDestinationList.add(TouristDestination(37.527896, 127.036245))
        touristDestinationList.add(TouristDestination(37.541889,127.095388))


        // 별자리 지도 비율
        var mappingRatio = mappingRatio(touristDestinationList)
        
        // 별 그리기
        drawStar(width-80, touristDestinationList, mappingRatio)

    }

    // 별자리지도 비율 결정
    fun mappingRatio(touristDestinationList : ArrayList<TouristDestination>) : ArrayList<Double>{
        var xList = ArrayList<Double>()
        var yList = ArrayList<Double>()
        var returnList = ArrayList<Double>()
        for(destination in touristDestinationList){
            xList.add(destination.x)
            yList.add(destination.y)
        }

        val constellationWidth = xList.max()-xList.min()
        val constellationHeight = yList.max()-yList.min()

        returnList.add(yList.min()) // 시작 좌표 계산을 위한 min(y)
        returnList.add(xList.min())  // 시작 좌표 계산을 위한 min(x)

        if (constellationWidth>constellationHeight){
            returnList.add(constellationWidth) // 정사각형 가로
        }
        else {
            returnList.add(constellationHeight) // 정사각형 가로
        }
        return returnList // min(y), min(x), square width
    }
    fun drawStar(constellationMapWidth: Int, touristDestinationList: ArrayList<TouristDestination>, mappingRatio: ArrayList<Double>){
        for (i in 0..(touristDestinationList.size-1)){
            var iv_item = ImageView(this)
            iv.add(iv_item)
            iv[i].layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            iv[i].setImageResource(R.drawable.star)
            val ratioY = (touristDestinationList[i].y - mappingRatio[0])/mappingRatio[2]
            val ratioX = (touristDestinationList[i].x - mappingRatio[1])/mappingRatio[2]
            Log.e("ratioY:", (ratioY).toInt().toString())
            Log.e("ratioX:", (ratioX).toInt().toString())
            mapConstellation!!.addView(iv[i], 50f+(ratioX).toFloat()*300f, (1-(ratioY).toFloat())*300f, 30f, 30f)
        }
    }
}
