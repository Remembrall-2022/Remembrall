package com.example.remebrall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.remebrall.databinding.ActivityMainBinding
import com.example.remebrall.mypage.MyPageFragment
import com.example.remebrall.read.ReadDiaryFragment
import com.example.remebrall.write.WriteDiaryFragment

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
                    var mypageFragment = MyPageFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout_main, mypageFragment).commit()
                }
            }
            true
        }
            selectedItemId = R.id.bottomnavigation_main_write
        }
    }
}