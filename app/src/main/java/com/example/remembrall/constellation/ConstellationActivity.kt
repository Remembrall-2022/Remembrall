package com.example.remembrall.constellation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import com.example.remembrall.R
import com.example.remembrall.databinding.ActivityConstellationBinding
import com.ssomai.android.scalablelayout.ScalableLayout
import java.lang.Math.sin
import java.lang.Math.sqrt
import kotlin.math.pow

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

        // TODO : display, size deprecated
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
        val constellationWidth = xList.maxOrNull()?.minus(xList.minOrNull()!!)
        val constellationHeight = yList.maxOrNull()?.minus(yList.minOrNull()!!)
        returnList.add(yList.minOrNull()!!) // 시작 좌표 계산을 위한 min(y)
        returnList.add(xList.minOrNull()!!)  // 시작 좌표 계산을 위한 min(x)

        if (constellationWidth != null) {
            if (constellationWidth > constellationHeight!!){
                returnList.add(constellationWidth) // 정사각형 가로
            }
            else {
                returnList.add(constellationHeight) // 정사각형 가로
            }
        }
        return returnList // min(y), min(x), square width
    }

    // 별자리지도 그리기
    fun drawStar(constellationMapWidth: Int, touristDestinationList: ArrayList<TouristDestination>, mappingRatio: ArrayList<Double>){
        var xList = ArrayList<Float>()
        var yList = ArrayList<Float>()

        for (i in 0..(touristDestinationList.size-1)){
            var iv_item = ImageView(this)
            iv.add(iv_item)
            iv[i].layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            iv[i].setImageResource(R.drawable.star)

            val ratioX = 50f+((touristDestinationList[i].x - mappingRatio[1]) / mappingRatio[2]).toFloat()*300f
            val ratioY = ((1-(touristDestinationList[i].y - mappingRatio[0]) / mappingRatio[2]).toFloat())*300f

            xList.add(ratioX)
            yList.add(ratioY)

            Log.e("ratioY:", (ratioY).toInt().toString())
            Log.e("ratioX:", (ratioX).toInt().toString())

            mapConstellation!!.addView(iv[i], ratioX, ratioY, 30f, 30f)
        }
        for (i in 0..(touristDestinationList.size-2)){
            val angle = angleOf(xList[i], yList[i], xList[i+1], yList[i+1])
            val length = getDistance(xList[i], xList[i], xList[i+1], yList[i+1])
            var iv_line = ImageView(this)
            iv_line.setImageResource(R.drawable.square)
            iv_line.scaleType = ImageView.ScaleType.FIT_XY
            iv_line.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//                iv_line.rotation = angle
            iv_line.setImageBitmap(rotateImage(BitmapFactory.decodeResource(getResources(), R.drawable.square), angle))
            Log.e("angle", angle.toString())
            Log.e("length", length.toString())
            mapConstellation!!.addView(iv_line, xList[i]+15f, yList[i]+15f, length-15f, 5f)
        }

    }

    //회전 실행
    fun rotateImage(src : Bitmap , degree : Float):Bitmap {
        var matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(src,0,0,src.getWidth(),src.getHeight(),matrix,true)
    }

    // 각도 구하기
    fun angleOf(p1x : Float, p1y : Float, p2x : Float , p2y : Float) : Float{
        val deltaY = p1y - p2y
        val deltaX = p2x - p1x
        val result = Math.toDegrees(Math.atan2(deltaY.toDouble(), deltaX.toDouble()))
        if (result<0){
            return (360+result).toFloat()
        }
        return result.toFloat()
    }
    // 거리 구하기
    fun getDistance(p1x : Float, p1y : Float, p2x : Float , p2y : Float): Float {
        val deltaY = (p1y - p2y).pow(2)
        val deltaX = (p2x - p1x).pow(2)
        return sqrt((deltaY + deltaX).toDouble()).toFloat()
    }
}
