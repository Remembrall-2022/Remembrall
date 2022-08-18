package com.example.remembrall

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.remebrall.read.ReadDiaryFragment
import com.example.remembrall.databinding.ActivityMainBinding
import com.example.remembrall.map.MapSearchFragment
import com.example.remembrall.mypage.MyPageFragment
import com.example.remembrall.write.WriteDiaryFragment
import com.kakao.sdk.user.UserApiClient


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomnavigationMain.run{setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottomnavigation_main_write -> {
                    var writeDiaryFragment = WriteDiaryFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout_main, writeDiaryFragment).commit()
                }
                R.id.bottomnavigation_main_read -> {
                    var readDiaryFragment = ReadDiaryFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout_main, readDiaryFragment).commit()
                }
                R.id.bottomnavigation_main_mypage -> {
//                    var mypageFragment = MyPageFragment()
                    var mapSearchFragment = MapSearchFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout_main, mapSearchFragment).commit()
                }
            }
            true
        }
            selectedItemId = R.id.bottomnavigation_main_write
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.i("Permission", "위치 권한 허가 필요")
            } else {
                ActivityCompat.requestPermissions(
                       this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1222)
            }
        }
    }
}