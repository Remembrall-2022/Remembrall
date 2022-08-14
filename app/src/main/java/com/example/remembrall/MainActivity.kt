package com.example.remembrall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.remembrall.databinding.ActivityMainBinding
import com.example.remembrall.map.MapSearchFragment
import com.example.remembrall.mypage.MyPageFragment
import com.example.remembrall.read.ReadDiaryFragment
import com.example.remembrall.write.WriteDiaryFragment


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
    }
}