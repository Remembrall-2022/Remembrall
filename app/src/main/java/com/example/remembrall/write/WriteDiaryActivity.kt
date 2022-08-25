package com.example.remembrall.write

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.inflate
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.remembrall.MainActivity
import com.example.remembrall.PreferenceUtil
import com.example.remembrall.R
import com.example.remembrall.databinding.ActivityLoginBinding.inflate
import com.example.remembrall.databinding.ActivityWriteDiaryBinding
import com.example.remembrall.read.ReadTodayDiaryFragment
import java.io.File
import java.time.LocalDate

class WriteDiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteDiaryBinding
    private lateinit var writeDiaryRecyclerViewData: ArrayList<WriteDiaryRecyclerViewData>
    private lateinit var writeDiaryRecyclerViewAdapter: WriteDiaryRecyclerViewAdapter
    private lateinit var touchHelper: ItemTouchHelper
    private var idx=1
    private lateinit var imageFile: File
    private lateinit var imagePath: String
    private var pos=0
    private lateinit var date: LocalDate
    private lateinit var inflater: LayoutInflater
    // Context를 액티비티로 형변환해서 할당

    companion object{
        lateinit var prefs: PreferenceUtil
        const val REQ_GALLERY=1
    }
    val imageResult=registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
            result->
        if(result.resultCode==RESULT_OK){
            val imageUri=result.data?.data
            imageUri?.let{
                imageFile= File(getRealPathFromURI(it))

                val src=File("test.jpg")

                @RequiresApi(Build.VERSION_CODES.O)
                date= LocalDate.now()
                val renameFile= File(imageFile.parent,"${date}-${pos}.jpg")
                imagePath = getRealPathFromURI(it)
                Log.d("imageFile", "${imageFile}")
                Log.d("src", "${src}")
                Log.d("renameFile", "${renameFile}")
//                Log.d("tag", "imagePath: ${imagePath}")
                Log.d("tag", "imageUri: ${imageUri}")

                Glide.with(this)
                    .load(imageUri)
                    .fitCenter()
                    .apply(RequestOptions().override(500,500))
                    .into(binding.recyclerviewWritediary[pos].findViewById(R.id.imageview_addpicture))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate(savedInstanceState)
        binding=ActivityWriteDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarWritediary)	//
        val tb=supportActionBar!!
        tb.setDisplayShowTitleEnabled(false)
        tb.setDisplayHomeAsUpEnabled(true)

        initalize()
        initReadDiaryRecyclerView()
        binding.recyclerviewWritediary.addItemDecoration(WriteDividerItemDecoration(binding.recyclerviewWritediary.context, R.drawable.creatediary_line_divider, 0, 0))

        //장소 추가
        binding.linearWritediaryAddplace.setOnClickListener{
            writeDiaryRecyclerViewData.add(WriteDiaryRecyclerViewData("장소${idx}", "", ""))
            writeDiaryRecyclerViewAdapter.notifyItemInserted(writeDiaryRecyclerViewData.size)
            idx++
        }

        binding.btnWritediaryComplete.setOnClickListener{
            MainActivity.prefs.setString("writediary","true")
            Log.d("writediary", MainActivity.prefs.getString("writediary","작성 실패"))
            finish()
//            val readDiaryView=View.inflate(this@WriteDiaryActivity, R.layout.fragment_read_diary, null)

//            Log.e("mainView","${mainView.findViewById<View?>(R.id.bottomnavigation_main).visibility}")
//            if(MainActivity.prefs.getString("writediary","작성 실패")=="true"){
//                Log.e("readDiaryView write b", "${readDiaryView.findViewById<View?>(R.id.linear_readdiary_write).visibility}")
//                Log.e("readDiaryView read b", "${readDiaryView.findViewById<View?>(R.id.linear_readdiary_read).visibility}")
//                readDiaryView.findViewById<View?>(R.id.linear_readdiary_write).visibility=View.GONE
//                Log.e("readDiaryView write a", "${readDiaryView.findViewById<View?>(R.id.linear_readdiary_write).visibility}")
//                readDiaryView.findViewById<View?>(R.id.linear_readdiary_read).visibility=View.VISIBLE

//                Log.e("readDiaryView read a", "${readDiaryView.findViewById<View?>(R.id.linear_readdiary_read).visibility}")
//            }
//            if(MainActivity.prefs.getString("writediary","작성 안됨")=="true")
//                supportFragmentManager.beginTransaction()
//                    .replace(mainView.findViewById<FrameLayout>(R.id.framelayout_main), ReadTodayDiaryFragment()).commit()

        }

//      리사이클러뷰 아이템 클릭
        writeDiaryRecyclerViewAdapter.setItemClickListener(object: WriteDiaryRecyclerViewAdapter.OnItemClickListener{
            override fun imageViewOnClick(v: View, position: Int) {
                pos=position
                selectGallery()
            }
            override fun dropViewOnClck(v: View, position: Int) {
                if(binding.recyclerviewWritediary[position].findViewById<View?>(R.id.linear_addplace_bottom).visibility==View.VISIBLE){
                    binding.recyclerviewWritediary[position].findViewById<View?>(R.id.linear_addplace_bottom).visibility=View.GONE
                    binding.recyclerviewWritediary[position].findViewById<ImageView>(R.id.imageview_addplace_drop).setImageResource(R.drawable.ic_drop_down)
                }
                else{
                    binding.recyclerviewWritediary[position].findViewById<View?>(R.id.linear_addplace_bottom).visibility=View.VISIBLE
                    binding.recyclerviewWritediary[position].findViewById<ImageView>(R.id.imageview_addplace_drop).setImageResource(R.drawable.ic_drop_up)
                }
            }
            override fun editViewOnClck(v: View, position: Int) {
                binding.recyclerviewWritediary[position].findViewById<TextView>(R.id.tv_addplace_place).text="수정됨"
            }
            override fun deleteViewOnClck(v: View, position: Int) {
                Log.d("삭제 위치", "${position}")
                writeDiaryRecyclerViewData.removeAt(position)
                writeDiaryRecyclerViewAdapter.notifyItemRemoved(position)

                binding.recyclerviewWritediary[position].findViewById<LinearLayout>(R.id.linear_addplace_edit).visibility =
                    View.GONE
                binding.recyclerviewWritediary[position].findViewById<LinearLayout>(R.id.linear_addplace_drop).visibility =
                    View.VISIBLE
                binding.recyclerviewWritediary[position].findViewById<View?>(R.id.linear_addplace_bottom).visibility=View.VISIBLE
                binding.recyclerviewWritediary[position].findViewById<ImageView>(R.id.imageview_addplace_drop).setImageResource(R.drawable.ic_drop_up)

                Log.d("리스트 삭제 후 사이즈", "${writeDiaryRecyclerViewData.size}")
                Toast.makeText(binding.root.context, "삭제되었습니다", Toast.LENGTH_SHORT).show()
            }
            override fun transferViewOnClck(v: View, position: Int) {

            }
//            override fun onClick(v: View, position: Int) {
//                Toast.makeText(v.context, "${writeDiaryRecyclerViewData[position].place}", Toast.LENGTH_SHORT).show()
//            }
        })
        //앨범에서 사진 불러오기

    }
    private fun initalize() {
        writeDiaryRecyclerViewData = arrayListOf()
//        writeDiaryRecyclerViewData.add(WriteDiaryRecyclerViewData("장소1", "", ""))
    }

    private fun initReadDiaryRecyclerView() {
        val recyclerViewWriteDiary=binding.recyclerviewWritediary
        recyclerViewWriteDiary.layoutManager=LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        writeDiaryRecyclerViewAdapter=WriteDiaryRecyclerViewAdapter(this, writeDiaryRecyclerViewData)

        val callback = ItemMoveCallbackListener(writeDiaryRecyclerViewAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerViewWriteDiary)

        recyclerViewWriteDiary.adapter=writeDiaryRecyclerViewAdapter

        writeDiaryRecyclerViewAdapter.startDrag(object : WriteDiaryRecyclerViewAdapter.OnStartDragListener {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                touchHelper.startDrag(viewHolder)
            }
        })
    }

    fun getRealPathFromURI(uri: Uri): String{
        val buildName= Build.MANUFACTURER
        if(buildName.equals("Xiaomi")){
            return uri.path!!
        }
        var columnIndex=0
        val proj=arrayOf(MediaStore.Images.Media.DATA)
        val cursor=contentResolver.query(uri, proj, null, null, null)
        if(cursor!!.moveToFirst()){
            columnIndex=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        val result=cursor.getString(columnIndex)
        cursor.close()
        return result

    }
//
    private fun selectGallery(){
        val writePermission= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission=ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if(writePermission==PackageManager.PERMISSION_DENIED || readPermission==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), REQ_GALLERY)
        }else{
            val intent=Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            imageResult.launch(intent)
        }
    }

    fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        touchHelper.startDrag(viewHolder)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.writediary_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {	//뒤로가기 버튼이 작동하도록
        when (item.itemId) {
            R.id.home -> {
                finish()
            }
            R.id.toolbar_writediary_edit -> {
                val size=writeDiaryRecyclerViewData.size-1
                //edit 버튼 눌렀을 때
                Log.d("수정할 때 list 사이즈", "${writeDiaryRecyclerViewData.size}")
                    binding.toolbarWritediary.menu.findItem(R.id.toolbar_writediary_edit)
                        .setVisible(false)
                    binding.toolbarWritediary.menu.findItem(R.id.toolbar_writediary_complete)
                        .setVisible(true)
                    binding.linearWritediaryAddplace.visibility = View.GONE

                    for(i: Int in 0..size){
                    binding.recyclerviewWritediary[i].findViewById<LinearLayout>(R.id.linear_addplace_edit).visibility =
                        View.VISIBLE
                    binding.recyclerviewWritediary[i].findViewById<LinearLayout>(R.id.linear_addplace_drop).visibility =
                        View.GONE
                    }
            }
            //완료 버튼 눌렀을 떄
            R.id.toolbar_writediary_complete -> {
                val size=writeDiaryRecyclerViewData.size-1
                Log.d("완료 후 list 사이즈", "${writeDiaryRecyclerViewData.size}")

                binding.toolbarWritediary.menu.findItem(R.id.toolbar_writediary_edit).setVisible(true)
                binding.toolbarWritediary.menu.findItem(R.id.toolbar_writediary_complete).setVisible(false)
                binding.linearWritediaryAddplace.visibility=View.VISIBLE

                for(i: Int in 0..size){
                    binding.recyclerviewWritediary[i].findViewById<LinearLayout>(R.id.linear_addplace_edit).visibility =
                        View.GONE
                    binding.recyclerviewWritediary[i].findViewById<LinearLayout>(R.id.linear_addplace_drop).visibility =
                        View.VISIBLE
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }
}