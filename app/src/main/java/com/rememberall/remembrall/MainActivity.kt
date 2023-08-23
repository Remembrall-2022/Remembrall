package com.rememberall.remembrall

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.rememberall.remembrall.databinding.ActivityMainBinding
import com.rememberall.remembrall.dongdong.DongDongFragment
import com.rememberall.remembrall.read.ReadDiaryListFragment
import com.rememberall.remembrall.read.ReadDiaryFragment
import com.rememberall.remembrall.read.ReadTodayDiaryFragment
import java.util.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    var context: Context?=null
    var waitTime = 0L

    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate(savedInstanceState: Bundle?) {


//        prefs = PreferenceUtil(applicationContext)
        super.onCreate(savedInstanceState)
//        prefs.setString("writediary","false")


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
        val today=GlobalApplication.prefs.getString("today","")
        val calendar= Calendar.getInstance()
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        if(today!=day.toString()) { //날짜가 다름
            GlobalApplication.prefs.setString("today_write","false")
        }
        binding.bottomnavigationMain.run{setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottomnavigation_main_write -> {
                    Log.d("writediary", "${GlobalApplication.prefs.getString("writediary","작성 안됨")}")
                    var readDiaryFragment = ReadDiaryFragment()
                    var readTodayDiaryFragment = ReadTodayDiaryFragment()
                    if(GlobalApplication.prefs.getString("today_write","false")=="true")
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.framelayout_main, readTodayDiaryFragment).commit()
                    else
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.framelayout_main, readDiaryFragment).commit()
                }
                R.id.bottomnavigation_main_read -> {
                    var readDiaryFragment = ReadDiaryListFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout_main, readDiaryFragment).commit()
                }
                R.id.bottomnavigation_main_mypage -> {
                    var dongdongFragment = DongDongFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout_main, dongdongFragment).commit()
                }
            }
            true
        }
            selectedItemId = R.id.bottomnavigation_main_write
        }

    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() - waitTime >=1500 ) {
            waitTime = System.currentTimeMillis()
            Toast.makeText(this,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show()
        } else {
            finish() // 액티비티 종료
            exitProcess(0)
        }
    }

    fun replaceFragment(fragment : Fragment) {
        var fragmentManager = getSupportFragmentManager()
        var fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.framelayout_main, fragment).commit()
    }


    }