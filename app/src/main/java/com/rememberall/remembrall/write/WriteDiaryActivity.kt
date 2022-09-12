package com.rememberall.remembrall.write

import android.Manifest
import android.app.Activity
import android.content.ContentValues
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rememberall.remembrall.BuildConfig.SERVER
import com.rememberall.remembrall.PreferenceUtil
import com.rememberall.remembrall.R
import com.rememberall.remembrall.databinding.ActivityWriteDiaryBinding
import com.rememberall.remembrall.login.userinfo.SharedManager
import com.rememberall.remembrall.map.MapSearchActivity
import com.rememberall.remembrall.read.ReadDiaryListRecyclerViewData
import com.rememberall.remembrall.read.Triplog.TriplogService
import com.rememberall.remembrall.read.Triplog.res.GetTriplogListResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    private lateinit var readDiaryListRecyclerViewData: ArrayList<ReadDiaryListRecyclerViewData>
    private lateinit var questionRecyclerViewAdapter: QuestionRecyclerViewAdapter
    private lateinit var touchHelper: ItemTouchHelper
    private var idx=1
    private lateinit var imageFile: File
    private lateinit var imagePath: String
    private var pos=0
    private lateinit var date: LocalDate
    private var diaryId: Long=1
    private lateinit var question: String
    private var questionId: Long=1
    private var selectDiary=false

    private var placeName = ""
    private var x : Double ?= null
    private var y : Double ?= null

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            placeName = result.data?.getStringExtra("placeName")!!.toString()
            x =  result.data?.getDoubleExtra("x",0.0)
            y =  result.data?.getDoubleExtra("y", 0.0)
            Log.e("placeName", placeName)
            writeDiaryRecyclerViewData.add(WriteDiaryRecyclerViewData(placeName, "", "", MultipartBody.Part.createFormData("file", ""),x!!,y!!))
            writeDiaryRecyclerViewAdapter.notifyItemInserted(writeDiaryRecyclerViewData.size)
        }
    }

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
        selectDiary=false

        var retrofit = Retrofit.Builder()
            .baseUrl(SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var triplogService : TriplogService = retrofit.create(TriplogService::class.java)
        val sharedManager : SharedManager by lazy { SharedManager(this@WriteDiaryActivity) }
        var authToken = sharedManager.getCurrentUser().accessToken

        readDiaryListRecyclerViewData = arrayListOf()
        triplogService.getTripLogList(authToken!!).enqueue(object :
            Callback<GetTriplogListResponse> {
            override fun onResponse(
                call: Call<GetTriplogListResponse>,
                response: Response<GetTriplogListResponse>
            ) {
                Log.e("CreateTripLog", response.body().toString())

                if(response.body()?.success.toString() == "true"){
                    var diaryList = response.body()?.response!!
                    if(diaryList != null){
                        for (diary in diaryList){
                            val title=diary!!.title.toString()
                            val imgUrl=diary!!.tripLogImgUrl.toString()
                            val triplogId=diary!!.triplogId!!.toLong()

                            val tripStartDate=diary!!.tripStartDate
                            val tripEndDate=diary!!.tripEndDate
                            val startDate=tripStartDate.toString().split('-')
                            val sDate=startDate[0].toString().split('0')
                            val endDate=tripEndDate.toString().split('-')
                            val eDate=startDate[0].toString().split('0')
                            val tripDate=sDate[1]+'.'+startDate[1]+'.'+startDate[2]+" ~ "+eDate[1]+'.'+endDate[1]+'.'+endDate[2]

                            readDiaryListRecyclerViewData.add(ReadDiaryListRecyclerViewData(title, imgUrl, triplogId, tripDate))
                        }

                    }
                }
            }
            override fun onFailure(call: Call<GetTriplogListResponse>, t: Throwable) {
                Toast.makeText(this@WriteDiaryActivity,"일기장 불러오기 실패", Toast.LENGTH_SHORT).show()
            }

        })

        // 일기장 선택
        binding.consWritediarySelect.setOnClickListener {
            var diaryListDialog = DiaryListDialog(this@WriteDiaryActivity, readDiaryListRecyclerViewData)
            diaryListDialog.show()
            diaryListDialog.readDiaryListRecyclerViewAdapter.setItemClickListener(object: SelectDiaryListRecyclerViewAdapter.OnItemClickListener{
                override fun diaryOnClick(v: View, position: Int) {
                    val diaryTitle=readDiaryListRecyclerViewData[position].name
                    diaryId=readDiaryListRecyclerViewData[position].triplogId
                    Log.e("diary", "$diaryId + $diaryTitle")
                    binding.tvDiaryTitle.text = diaryTitle
                    selectDiary=true
                    diaryListDialog.dismiss()
                }
            })
        }

        // 질문 선택
        binding.tvWritediaryQuestion.setOnClickListener{
            var questionListDialog = QuestionListDialog(this@WriteDiaryActivity, questionRecyclerViewData)
            questionListDialog.show()
            questionListDialog.questionRecyclerViewAdapter.setItemClickListener(object: QuestionRecyclerViewAdapter.OnItemClickListener{
                override fun onClick(v: View, position: Int) {
                    val question=questionRecyclerViewData[position].questionName
                    val id=questionRecyclerViewData[position].id
                    Log.e("question", "$id + $question")
                    binding.tvWritediaryQuestion.text = question
                    questionListDialog.dismiss()
                }
            })
        }

//        var str = binding.tvWritediaryDate.text.toString()
//        var content=SpannableString(str)
//        content.setSpan(UnderlineSpan(), 0, str.length, 0)
//        binding.tvWritediaryDate.text=content

        initalize()
        initReadDiaryRecyclerView()
        binding.recyclerviewWritediary.addItemDecoration(WriteDividerItemDecoration(binding.recyclerviewWritediary.context, R.drawable.creatediary_line_divider, 0, 0))

        //장소 추가
        binding.linearWritediaryAddplace.setOnClickListener{
            val intent_map = Intent(this@WriteDiaryActivity, MapSearchActivity::class.java)
            resultLauncher.launch(intent_map)

            binding.linearWritediaryEdit.visibility=View.VISIBLE
        }

        binding.linearWritediaryEdit.setOnClickListener{
            if(binding.tvWritediaryEdit.text.toString()=="경로 수정하기") {
                val size = writeDiaryRecyclerViewData.size - 1
                //edit 버튼 눌렀을 때
                Log.d("수정할 때 list 사이즈", "${writeDiaryRecyclerViewData.size}")

                binding.tvWritediaryEdit.text="수정 완료"
                binding.linearWritediaryAddplace.visibility = View.GONE

                for (i: Int in 0..size) {
                    binding.recyclerviewWritediary[i].findViewById<LinearLayout>(R.id.linear_addplace_edit).visibility =
                        View.VISIBLE
                    binding.recyclerviewWritediary[i].findViewById<LinearLayout>(R.id.linear_addplace_drop).visibility =
                        View.GONE
                }
            }
            else{
                val size = writeDiaryRecyclerViewData.size - 1

                Log.d("완료 후 list 사이즈", "${writeDiaryRecyclerViewData.size}")

                binding.linearWritediaryAddplace.visibility=View.VISIBLE
                binding.tvWritediaryEdit.text="경로 수정하기"

                if(writeDiaryRecyclerViewData.size==0){
                    binding.linearWritediaryEdit.visibility=View.GONE
                }

                for(i: Int in 0..size){
                    binding.recyclerviewWritediary[i].findViewById<LinearLayout>(R.id.linear_addplace_edit).visibility =
                        View.GONE
                    binding.recyclerviewWritediary[i].findViewById<LinearLayout>(R.id.linear_addplace_drop).visibility =
                        View.VISIBLE
                }
            }
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
            WriteDiaryService.getRetrofitRefreshQuestion(authToken!!).enqueue(object: Callback<GetQuestionResponse>{
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
            WriteDiaryService.getRetrofitAllQuestion(authToken!!).enqueue(object: Callback<GetAllQuestionResponse>{
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
                    var questionListDialog = QuestionListDialog(this@WriteDiaryActivity, questionRecyclerViewData)
                    questionListDialog.show()
                    questionListDialog.questionRecyclerViewAdapter.setItemClickListener(object: QuestionRecyclerViewAdapter.OnItemClickListener{
                        override fun onClick(v: View, position: Int) {
                            val question=questionRecyclerViewData[position].questionName
                            val id=questionRecyclerViewData[position].id
                            Log.e("question", "$id + $question")
                            binding.tvWritediaryQuestion.text = question
                            questionListDialog.dismiss()
                        }
                    })
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
                var name=binding.recyclerviewWritediary[i].findViewById<TextView>(R.id.tv_addplace_place).text.toString()
                var address=writeDiaryRecyclerViewData[i].place
                var longitude= writeDiaryRecyclerViewData[i].x
                var latitude=writeDiaryRecyclerViewData[i].y
                var comment=binding.recyclerviewWritediary[i].findViewById<EditText>(R.id.et_addplace_coment).text.toString()
//                placeInfo=WriteDiaryRequest.PlaceLogList.PlaceInfo(i, name, address, longitude, latitude)
                Log.e("placeLogList", "${name}  ${address}  ${longitude}  ${latitude}  ${comment}")
                placeLogList.add(JSONObject("{\"placeInfo\":{\"name\":\"${name}\",\"address\":\"${address}\",\"longitude\":${longitude},\"latitude\":${latitude}},\"comment\":\"${comment}\",\"imgName\":\"${writeDiaryRecyclerViewData[i].image}\"}"))
                imgList.add(writeDiaryRecyclerViewData[i].imgFile)
            }
//            var writeDiaryRequest=WriteDiaryRequest(date,
//                weather,questionId, answer, placeLogList)
            val jsonObject=JSONObject("{\"date\":\"${date}\", \"weatherInfo\":{\"weather\": \"맑음\",\"degree\" : 25},\"questionId\":\"${questionId}\",\"answer\":\"${answer}\", \"placeLogList\":${placeLogList}}")
            val mediaType = "application/json".toMediaType()
            val jsonBody=jsonObject.toString().toRequestBody(mediaType)

            if(selectDiary==false){
                Toast.makeText(this@WriteDiaryActivity, "일기장을 선택해주세요",Toast.LENGTH_SHORT ).show()
            }
            else {
                WriteDiaryService.getRetrofitSaveDiary(authToken!!, diaryId, jsonBody, imgList)
                    .enqueue(object : Callback<WriteDiaryResponse> {
                        override fun onResponse(
                            call: Call<WriteDiaryResponse>,
                            response: Response<WriteDiaryResponse>
                        ) {

                            if (response.isSuccessful) {
                                Log.e("question", response.toString())
                                Log.e("question", response.body().toString())

                                finish()
                            } else {
                                try {
                                    val body = response.errorBody()!!.string()

//                                    val gson=GsonBuilder().create()
//                                    val error=gson.fromJson(response.errorBody().string())
                                    //에러 Toast
                                    Toast.makeText(this@WriteDiaryActivity,"이미 작성한 날짜입니다", Toast.LENGTH_SHORT).show()
//                                    val error=JSONObject(body)
                                    Log.e(ContentValues.TAG, "body : $body")
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                        }

                        override fun onFailure(call: Call<WriteDiaryResponse>, t: Throwable) {
                            Log.e("TAG", "실패원인: {$t}")
                        }
                    })
            }
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
            override fun deleteViewOnClck(v: View, position: Int) {
                Log.d("삭제 위치", "${position}")
                binding.recyclerviewWritediary[position].findViewById<LinearLayout>(R.id.linear_addplace_edit).visibility =
                    View.GONE
                binding.recyclerviewWritediary[position].findViewById<LinearLayout>(R.id.linear_addplace_drop).visibility =
                    View.VISIBLE
                binding.recyclerviewWritediary[position].findViewById<View?>(R.id.linear_addplace_bottom).visibility=View.VISIBLE
                binding.recyclerviewWritediary[position].findViewById<ImageView>(R.id.imageview_addplace_drop).setImageResource(R.drawable.ic_drop_up)
                binding.recyclerviewWritediary[position].findViewById<ImageView>(R.id.imageview_addpicture).setImageResource(R.drawable.ic_image)

                writeDiaryRecyclerViewData.removeAt(position)
                writeDiaryRecyclerViewAdapter.notifyItemRemoved(position)
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
        recyclerViewWriteDiary.layoutManager=LinearLayoutManager(this@WriteDiaryActivity, LinearLayoutManager.VERTICAL, false)
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

        var mon: Int=1
        var yea: Int=1
        var da: Int=1
        datePicker.init(date[0].toInt(), date[1].toInt()-1, date[2].toInt()){
                view, year, month, day ->
            mon = month + 1
            da=day
            yea=year
        }
        binding.linearWritediaryOk.setOnClickListener {
            var m=mon.toString()
            var d=da.toString()
            if(mon<10){
                m="0${mon}"
            }
            if(da<10)
                d="0${da}"
            binding.tvWritediaryDate.text="$yea-$m-$d"
            binding.consWritediaryDatepicker.visibility=View.GONE
            binding.btnWritediaryComplete.visibility=View.VISIBLE
        }
        binding.linearWritediaryCancel.setOnClickListener {
            binding.consWritediaryDatepicker.visibility=View.GONE
            binding.btnWritediaryComplete.visibility=View.VISIBLE
        }

    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.writediary_toolbar_menu, menu)
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {	//뒤로가기 버튼이 작동하도록
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 200) {
            placeName = data?.getStringExtra("placeName")!!
            x = data?.getDoubleExtra("x", 0.0)!!
            y = data?.getDoubleExtra("y", 0.0)!!
        }
    }


}