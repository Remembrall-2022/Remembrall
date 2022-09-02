package com.example.remembrall

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.remembrall.databinding.ActivityMainBinding
import com.example.remembrall.read.ReadDiaryListFragment
import com.example.remembrall.read.ReadTodayDiaryFragment
import com.example.remembrall.write.ReadDiaryFragment
import com.example.remembrall.map.MapSearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    var context: Context?=null
    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate(savedInstanceState)
        prefs.setString("writediary","false")


        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.i("Permission", "위치 권한 허가 필요")
            } else {
                ActivityCompat.requestPermissions(
                       this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1222)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomnavigationMain.run{setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottomnavigation_main_write -> {
                    Log.d("writediary", "${prefs.getString("writediary","작성 안됨")}")
                    var writeDiaryFragment = ReadDiaryFragment()
                    var readTodayDiaryFragment = ReadTodayDiaryFragment()
                    if(prefs.getString("writediary","작성 안됨")=="false")
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.framelayout_main, writeDiaryFragment).commit()
                    else
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.framelayout_main, readTodayDiaryFragment).commit()
                }
                R.id.bottomnavigation_main_read -> {
                    var readDiaryFragment = ReadDiaryListFragment()
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

    }
}