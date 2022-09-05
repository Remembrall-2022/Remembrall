package com.example.remembrall.write

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
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
import com.example.remembrall.databinding.ActivityWriteDiaryBinding
import com.example.remembrall.login.res.LoginResponse
import com.example.remembrall.login.userinfo.SharedManager
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class WriteDiaryActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityWriteDiaryBinding
    private lateinit var writeDiaryRecyclerViewData: ArrayList<WriteDiaryRecyclerViewData>
    private lateinit var writeDiaryRecyclerViewAdapter: WriteDiaryRecyclerViewAdapter
    private lateinit var questionRecyclerViewData: ArrayList<QuestionRecyclerViewData>
    private lateinit var questionRecyclerViewAdapter: QuestionRecyclerViewAdapter
    private lateinit var touchHelper: ItemTouchHelper
    private var idx=1
    private lateinit var imageFile: File
    private lateinit var imagePath: String
    private var pos=0
    private lateinit var date: LocalDate

    private lateinit var question: String
    private var questionId: Long=1
    private lateinit var formdata: MultipartBody.Part
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

//                val src=File("test.jpg")

//                @RequiresApi(Build.VERSION_CODES.O)
//                date= LocalDate.now()
                val now = Date()
                val time: String = SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH).format(now)
                val renameFile= File(imageFile.parent,"${time}.jpg")
                val requestBody=imageFile.asRequestBody("img/*".toMediaTypeOrNull())

                writeDiaryRecyclerViewData[pos].image=renameFile.name
                writeDiaryRecyclerViewData[pos].imgFile=MultipartBody.Part.createFormData("file", renameFile.name, requestBody)
                imagePath = getRealPathFromURI(it)
                Log.d("imageFile", "${imageFile}")
//                Log.d("src", "${src}")
                Log.d("renameFile", "${renameFile}")
                Log.e("file name", "${renameFile.name}")
//                Log.d("tag", "imagePath: ${imagePath}")
                Log.d("tag", "imageUri: ${imageUri}")

                Glide.with(this)
                    .load(imageUri)
                    .fitCenter()
                    .apply(RequestOptions().override(500,500))
                    .into(binding.recyclerviewWritediary[pos].findViewById(R.id.imageview_addpicture))

                binding.recyclerviewWritediary[pos].findViewById<ImageView>(R.id.imageview_addpicture).adjustViewBounds=true
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

        question= intent.getStringExtra("question").toString()
        questionId=intent.getLongExtra("questionId",1)
        binding.tvWritediaryQuestion.text=question

        initalize()
        initReadDiaryRecyclerView()
        binding.recyclerviewWritediary.addItemDecoration(WriteDividerItemDecoration(binding.recyclerviewWritediary.context, R.drawable.creatediary_line_divider, 0, 0))

        //장소 추가
        binding.linearWritediaryAddplace.setOnClickListener{
            writeDiaryRecyclerViewData.add(WriteDiaryRecyclerViewData("장소${idx}", "", "", MultipartBody.Part.createFormData("file", "")))
            writeDiaryRecyclerViewAdapter.notifyItemInserted(writeDiaryRecyclerViewData.size)
            idx++
        }

        clickRecyclerViewItem()
        binding.linearWritediaryDate.setOnClickListener {
            datePick()
        }
        //앨범에서 사진 불러오기
        linkApi()
    }

    private fun linkApi(){
        binding.imgWritediaryRefresh.setOnClickListener {
            val sharedManager : SharedManager by lazy { SharedManager(this@WriteDiaryActivity) }
            var authToken = sharedManager.getCurrentUser().accessToken
            WriteDiaryService.getRetrofitRefreshQuestion(authToken).enqueue(object: Callback<GetQuestionResponse>{
                override fun onResponse(
                    call: Call<GetQuestionResponse>,
                    response: Response<GetQuestionResponse>
                ) {
                    Log.e("question", response.toString())
                    Log.e("question", response.body().toString())

                    questionId=response.body()!!.response.id
                    val questionName=response.body()!!.response.questionName
                    binding.tvWritediaryQuestion.text=questionName
                }
                override fun onFailure(call: Call<GetQuestionResponse>, t: Throwable) {
                    Log.e("TAG", "실패원인: {$t}")
                }
            })
        }

        binding.imgWritediaryMore.setOnClickListener {
            val sharedManager : SharedManager by lazy { SharedManager(this@WriteDiaryActivity) }
            var authToken = sharedManager.getCurrentUser().accessToken
            WriteDiaryService.getRetrofitAllQuestion(authToken).enqueue(object: Callback<GetAllQuestionResponse>{
                override fun onResponse(
                    call: Call<GetAllQuestionResponse>,
                    response: Response<GetAllQuestionResponse>
                ) {
                    Log.e("question", response.toString())
                    Log.e("question", response.body().toString())
                    for(i in 0..response.body()!!.response.size-1){
                        question=response.body()!!.response[i].questionName
                        questionId=response.body()!!.response[i].id

//                        Log.e("question api", "$questionId + $question")
                        questionRecyclerViewData.add(QuestionRecyclerViewData(question, questionId))
                    }
//                    questionRecyclerViewAdapter.notifyItemInserted(questionRecyclerViewData.size)
//                    val mDialogView=LayoutInflater.from(this@WriteDiaryActivity).inflate(R.layout.dialog_question_list, null)
//                    val mBuilder = AlertDialog.Builder(this@WriteDiaryActivity)
//                        .setView(mDialogView)
//                        .setTitle("질문 리스트")
                    QuestionListDialog(this@WriteDiaryActivity, questionRecyclerViewData).show()
//                    mBuilder.show()
                }

                override fun onFailure(call: Call<GetAllQuestionResponse>, t: Throwable) {
                    Log.e("TAG", "실패원인: {$t}")
                }
            })
        }

        //일기 작성 완료
        binding.btnWritediaryComplete.setOnClickListener{
            val sharedManager : SharedManager by lazy { SharedManager(this@WriteDiaryActivity) }
            var authToken = sharedManager.getCurrentUser().accessToken
            var date=binding.tvWritediaryDate.text.toString()
            var answer=binding.edWritediaryAnswer.text.toString()
//            var weather=WriteDiaryRequest.Weather("맑음", 25)
//            lateinit var placeInfo: WriteDiaryRequest.PlaceLogList.PlaceInfo
            var placeLogList: ArrayList<JSONObject> = arrayListOf()
            var imgList: ArrayList<MultipartBody.Part> = arrayListOf()
            Log.e("size", "${writeDiaryRecyclerViewData.size}")
            for(i in 0..(writeDiaryRecyclerViewData.size-1)){
                Log.e("for", "${i}")
                var name=binding.recyclerviewWritediary[i].findViewById<TextView>(R.id.tv_addplace_place).text.toString()
                var address="주소"
                var longitude=142.42324
                var latitude=32.23
                var comment=binding.recyclerviewWritediary[i].findViewById<EditText>(R.id.et_addplace_coment).text.toString()
//                placeInfo=WriteDiaryRequest.PlaceLogList.PlaceInfo(i, name, address, longitude, latitude)
                placeLogList.add(JSONObject("{\"placeInfo\":{\"placeId\":${i+1},\"name\":\"${name}\",\"address\":\"${address}\",\"longitude\":${longitude},\"latitude\":${latitude}},\"comment\":\"${comment}\",\"imgName\":\"${writeDiaryRecyclerViewData[i].image}\"}"))
                imgList.add(writeDiaryRecyclerViewData[i].imgFile)
            }
//            var writeDiaryRequest=WriteDiaryRequest(date,
//                weather,questionId, answer, placeLogList)
            val jsonObject=JSONObject("{\"date\":\"${date}\", \"weatherInfo\":{\"weather\": \"맑음\",\"degree\" : 25},\"questionId\":\"${questionId}\",\"answer\":\"${answer}\", \"placeLogList\":${placeLogList}}")
            val mediaType = "application/json".toMediaType()
            val jsonBody=jsonObject.toString().toRequestBody(mediaType)

            WriteDiaryService.getRetrofitSaveDiary(authToken, 13, jsonBody, imgList).enqueue(object: Callback<WriteDiaryResponse>{
                override fun onResponse(
                    call: Call<WriteDiaryResponse>,
                    response: Response<WriteDiaryResponse>
                ) {

                    if(response.isSuccessful){
                        Log.e("question", response.toString())
                        Log.e("question", response.body().toString())
                    }else {
                        try {
                            val body = response.errorBody()!!.string()
                            Log.e(ContentValues.TAG, "body : $body")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    MainActivity.prefs.setString("writediary","true")
                    Log.d("writediary", MainActivity.prefs.getString("writediary","작성 실패"))
                    finish()
                }

                override fun onFailure(call: Call<WriteDiaryResponse>, t: Throwable) {
                    Log.e("TAG", "실패원인: {$t}")
                }
            })
        }
    }

    private fun clickRecyclerViewItem(){
        //리사이클러뷰 아이템 클릭
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
    }

    private fun initalize() {
        writeDiaryRecyclerViewData = arrayListOf()
        questionRecyclerViewData= arrayListOf()
        var today=Calendar.getInstance()
        var year=today.get(Calendar.YEAR)
        var month= today.get(Calendar.MONTH)+1
        var day=today.get(Calendar.DAY_OF_MONTH)

        var m=month.toString()
        var d=day.toString()
        if(month<10){
            m="0${month}"
        }
        if(day<10)
            d="0${day}"
        binding.tvWritediaryDate.text="$year-$m-$d"
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

    private fun datePick() {
        val datePicker=binding.datePickerWritediary
        binding.consWritediaryDatepicker.visibility=View.VISIBLE
        binding.btnWritediaryComplete.visibility=View.GONE
        val today= Calendar.getInstance()
        var date=binding.tvWritediaryDate.text.toString().split("-")
        datePicker.init(date[0].toInt(), date[1].toInt()-1, date[2].toInt()){
                view, year, month, day ->
            val month = month + 1
            
            binding.tvWritediaryOk.setOnClickListener {
                var m=month.toString()
                var d=day.toString()
                if(month<10){
                    m="0${month}"
                }
                if(day<10)
                    d="0${day}"
                binding.tvWritediaryDate.text="$year-$m-$d"
                binding.consWritediaryDatepicker.visibility=View.GONE
                binding.btnWritediaryComplete.visibility=View.VISIBLE
            }
            binding.tvWritediaryCancel.setOnClickListener {
                binding.consWritediaryDatepicker.visibility=View.GONE
                binding.btnWritediaryComplete.visibility=View.VISIBLE
            }
        }

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