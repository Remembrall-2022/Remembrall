package com.rememberall.remembrall.update

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rememberall.remembrall.CommonResponse
import com.rememberall.remembrall.MainActivity
import com.rememberall.remembrall.PreferenceUtil
import com.rememberall.remembrall.R
import com.rememberall.remembrall.databinding.ActivityUpdateDiaryBinding
import com.rememberall.remembrall.login.userinfo.SharedManager
import com.rememberall.remembrall.map.MapSearchActivity
import com.rememberall.remembrall.read.*
import com.rememberall.remembrall.write.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class UpdateDiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateDiaryBinding
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
    private var triplogId: Long=1
    private var datelogId: Long=1
    private lateinit var title: String
    private lateinit var question: String
    private var questionId: Long=1
    private var imageUri= arrayListOf<Uri>()

    private lateinit var formdata: MultipartBody.Part

    private var placeName = ""
    private var x : Double ?= null
    private var y : Double ?= null

    var tvQuestion : TextView ?= null

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
                writeDiaryRecyclerViewData[pos].imgFile=
                    MultipartBody.Part.createFormData("file", renameFile.name, requestBody)
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
                    .into(binding.recyclerviewUpdatediary[pos].findViewById(R.id.imageview_addpicture))

                binding.recyclerviewUpdatediary[pos].findViewById<ImageView>(R.id.imageview_addpicture).adjustViewBounds=true
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUpdateDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarUpdatediary)	//
        val tb=supportActionBar!!
        tb.setDisplayShowTitleEnabled(false)
        tb.setDisplayHomeAsUpEnabled(true)

        triplogId=intent.getLongExtra("triplogId", 1)
        datelogId= intent.getLongExtra("datelogId", 1)
        title= intent.getStringExtra("title").toString()

        val text= SpannableString(binding.tvUpdatediaryDate.text.toString())
        text.setSpan(UnderlineSpan(), 0, text.length,0)
        binding.tvUpdatediaryDate.text=text

        binding.tvUpdateTitle.text=title

        initalize()
        initReadDiaryRecyclerView()
        binding.recyclerviewUpdatediary.addItemDecoration(WriteDividerItemDecoration(binding.recyclerviewUpdatediary.context, R.drawable.creatediary_line_divider, 0, 0))

        //일기 정보 불러오기
        ReadDiary()

        //장소 추가
        binding.linearUpdatediaryAddplace.setOnClickListener{
            val intent_map = Intent(this@UpdateDiaryActivity, MapSearchActivity::class.java)
            resultLauncher.launch(intent_map)
        }

        clickRecyclerViewItem()
        binding.linearUpdatediaryDate.setOnClickListener {
            datePick()
        }
        //앨범에서 사진 불러오기
        linkApi()
    }

    private fun ReadDiary(){
        val sharedManager : SharedManager by lazy { SharedManager(this@UpdateDiaryActivity) }
        var authToken = sharedManager.getCurrentUser().accessToken
        ReadDiaryService.getRetrofitReadDateDiary(authToken!!, triplogId, datelogId).enqueue(object: Callback<ReadDiaryResponse>{
            override fun onResponse(
                call: Call<ReadDiaryResponse>,
                response: Response<ReadDiaryResponse>
            ) {
                if(response.isSuccessful){
//                    Log.e("diary", response.toString())
                    Log.e("diary", response.body().toString())

                    var data= response.body()
                    var date= data?.date
                    var question=data?.question?.questionName
                    var answer=data?.answer
                    var placeLogList=data?.placeLogList

                    binding.tvUpdatediaryDate.text=date
                    binding.tvUpdatediaryQuestion.text=question
                    binding.edUpdatediaryAnswer.setText(answer)

                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            if (placeLogList != null) {
                                for (placeLog in placeLogList) {
                                    var placeLogId = placeLog.placeLogId
                                    var placeLogIndex = placeLog.placeLogIndex
                                    var place = placeLog.place
                                    var id = place.id
                                    var name = place.name
                                    var address = place.address
                                    var longitude = place.longitude
                                    var latitude = place.latitude
                                    var userLogImg = placeLog.userLogImg
                                    var imgUrl = userLogImg.imgUrl
                                    var userLogImgId = userLogImg.userLogImgId
                                    var comment = placeLog.comment

                                    val now = Date()
                                    val time: String =
                                        SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH).format(
                                            now
                                        )

                                    var file = File(cacheDir, "${time}.jpg")
                                    file.createNewFile()
                                    var uri = Uri.fromFile(file)

                                    Log.e("imgUrl", "$imgUrl")
                                    val inputStream = URL(imgUrl).openStream()
                                    val outputStream = FileOutputStream(file)
                                    inputStream.copyTo(outputStream)

                                    imageUri.add(uri)
//                            val body = RequestBody.create(MultipartBody.FORM,"")
//                            val emptyPart = MultipartBody.Part.createFormData("file","",body)
                                    val requestBody =
                                        file?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                                    var image =
                                        MultipartBody.Part.createFormData(
                                            "multipartFiles",
                                            file.name,
                                            requestBody
                                        )
                                    Log.e("imgFile", "$image")

                                    writeDiaryRecyclerViewData.add(
                                        WriteDiaryRecyclerViewData(
                                            address,
                                            file.name,
                                            comment,
                                            image,
                                            longitude,
                                            latitude
                                        )
                                    )
                                }
                            }
                        }
                        withContext(Dispatchers.Main) {
                            binding.recyclerviewUpdatediary.layoutManager=LinearLayoutManager(this@UpdateDiaryActivity, LinearLayoutManager.VERTICAL, false)
                            writeDiaryRecyclerViewAdapter=WriteDiaryRecyclerViewAdapter(this@UpdateDiaryActivity, writeDiaryRecyclerViewData)

                            for (idx in 0..writeDiaryRecyclerViewData.size){
                                Glide.with(this@UpdateDiaryActivity)
                                    .load(imageUri[idx])
                                    .fitCenter()
                                    .apply(RequestOptions().override(500,500))
                                    .into(binding.recyclerviewUpdatediary[idx].findViewById(R.id.imageview_addpicture))
                            }

                            writeDiaryRecyclerViewAdapter.notifyItemInserted(
                                writeDiaryRecyclerViewData.size
                            )
                        }
                    }
                }else {
                    try {
                        val body = response.errorBody()!!.string()
                        Log.e(ContentValues.TAG, "body : $body")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<ReadDiaryResponse>, t: Throwable) {
                Log.e("TAG", "실패원인: {$t}")
            }
        })
    }

    private fun linkApi(){
        binding.imgUpdatediaryRefresh.setOnClickListener {
            val sharedManager : SharedManager by lazy { SharedManager(this@UpdateDiaryActivity) }
            var authToken = sharedManager.getCurrentUser().accessToken
            WriteDiaryService.getRetrofitRefreshQuestion(authToken!!).enqueue(object:
                Callback<GetQuestionResponse> {
                override fun onResponse(
                    call: Call<GetQuestionResponse>,
                    response: Response<GetQuestionResponse>
                ) {
                    Log.e("question", response.toString())
                    Log.e("question", response.body().toString())

                    questionId=response.body()!!.id
                    val questionName=response.body()!!.questionName
                    binding.tvUpdatediaryQuestion.text=questionName
                }
                override fun onFailure(call: Call<GetQuestionResponse>, t: Throwable) {
                    Log.e("TAG", "실패원인: {$t}")
                }
            })
        }

        binding.imgUpdatediaryMore.setOnClickListener {
            val sharedManager : SharedManager by lazy { SharedManager(this@UpdateDiaryActivity) }
            var authToken = sharedManager.getCurrentUser().accessToken
            WriteDiaryService.getRetrofitAllQuestion(authToken!!).enqueue(object:
                Callback<List<GetQuestionResponse>> {
                override fun onResponse(
                    call: Call<List<GetQuestionResponse>>,
                    response: Response<List<GetQuestionResponse>>
                ) {
                    Log.e("question", response.toString())
                    Log.e("question", response.body().toString())
                    for(i in 0..response.body()!!.size-1){
                        question=response.body()!![i].questionName
                        questionId=response.body()!![i].id

//                        Log.e("question api", "$questionId + $question")
                        questionRecyclerViewData.add(QuestionRecyclerViewData(question, questionId))
                    }
//                    questionRecyclerViewAdapter.notifyItemInserted(questionRecyclerViewData.size)
//                    val mDialogView=LayoutInflater.from(this@WriteDiaryActivity).inflate(R.layout.dialog_question_list, null)
//                    val mBuilder = AlertDialog.Builder(this@WriteDiaryActivity)
//                        .setView(mDialogView)
//                        .setTitle("질문 리스트")
                    QuestionListDialog(this@UpdateDiaryActivity, questionRecyclerViewData).show()
//                    mBuilder.show()
                }

                override fun onFailure(call: Call<List<GetQuestionResponse>>, t: Throwable) {
                    Log.e("TAG", "실패원인: {$t}")
                }
            })
        }

        //일기 작성 완료
        binding.btnUpdatediaryComplete.setOnClickListener{
            val sharedManager : SharedManager by lazy { SharedManager(this@UpdateDiaryActivity) }
            var authToken = sharedManager.getCurrentUser().accessToken
            var date=binding.tvUpdatediaryDate.text.toString()
            var answer=binding.edUpdatediaryAnswer.text.toString()
//            var weather=WriteDiaryRequest.Weather("맑음", 25)
//            lateinit var placeInfo: WriteDiaryRequest.PlaceLogList.PlaceInfo
            var placeLogList: ArrayList<JSONObject> = arrayListOf()
            var imgList: ArrayList<MultipartBody.Part> = arrayListOf()
            Log.e("size", "${writeDiaryRecyclerViewData.size}")
            for(i in 0..(writeDiaryRecyclerViewData.size-1)){
                Log.e("for", "${i}")
                var name=binding.recyclerviewUpdatediary[i].findViewById<TextView>(R.id.tv_addplace_place).text.toString()
                var address="주소"
                var longitude=142.42324
                var latitude=32.23
                var comment=binding.recyclerviewUpdatediary[i].findViewById<EditText>(R.id.et_addplace_coment).text.toString()
//                placeInfo=WriteDiaryRequest.PlaceLogList.PlaceInfo(i, name, address, longitude, latitude)
                placeLogList.add(JSONObject("{\"placeInfo\":{\"placeId\":${i+1},\"name\":\"${name}\",\"address\":\"${address}\",\"longitude\":${longitude},\"latitude\":${latitude}},\"comment\":\"${comment}\",\"imgName\":\"${writeDiaryRecyclerViewData[i].image}\"}"))
                imgList.add(writeDiaryRecyclerViewData[i].imgFile)
            }
//            var writeDiaryRequest=WriteDiaryRequest(date,
//                weather,questionId, answer, placeLogList)
            val jsonObject= JSONObject("{\"date\":\"${date}\", \"weatherInfo\":{\"weather\": \"맑음\",\"degree\" : 25},\"questionId\":\"${questionId}\",\"answer\":\"${answer}\", \"placeLogList\":${placeLogList}}")
            val mediaType = "application/json".toMediaType()
            val jsonBody=jsonObject.toString().toRequestBody(mediaType)

            WriteDiaryService.getRetrofitSaveDiary(authToken!!, 13, jsonBody, imgList).enqueue(object:
                Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
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

                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
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
                if(binding.recyclerviewUpdatediary[position].findViewById<View?>(R.id.linear_addplace_bottom).visibility== View.VISIBLE){
                    binding.recyclerviewUpdatediary[position].findViewById<View?>(R.id.linear_addplace_bottom).visibility=
                        View.GONE
                    binding.recyclerviewUpdatediary[position].findViewById<ImageView>(R.id.imageview_addplace_drop).setImageResource(R.drawable.ic_drop_down)
                }
                else{
                    binding.recyclerviewUpdatediary[position].findViewById<View?>(R.id.linear_addplace_bottom).visibility=
                        View.VISIBLE
                    binding.recyclerviewUpdatediary[position].findViewById<ImageView>(R.id.imageview_addplace_drop).setImageResource(R.drawable.ic_drop_up)
                }
            }
            override fun deleteViewOnClck(v: View, position: Int) {
                Log.d("삭제 위치", "${position}")
                writeDiaryRecyclerViewData.removeAt(position)
                writeDiaryRecyclerViewAdapter.notifyItemRemoved(position)

                binding.recyclerviewUpdatediary[position].findViewById<LinearLayout>(R.id.linear_addplace_edit).visibility =
                    View.GONE
                binding.recyclerviewUpdatediary[position].findViewById<LinearLayout>(R.id.linear_addplace_drop).visibility =
                    View.VISIBLE
                binding.recyclerviewUpdatediary[position].findViewById<View?>(R.id.linear_addplace_bottom).visibility=
                    View.VISIBLE
                binding.recyclerviewUpdatediary[position].findViewById<ImageView>(R.id.imageview_addplace_drop).setImageResource(R.drawable.ic_drop_up)

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
        binding.tvUpdatediaryDate.text="$year-$m-$d"
    }

    private fun initReadDiaryRecyclerView() {
        val recyclerViewWriteDiary=binding.recyclerviewUpdatediary

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
        val readPermission= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if(writePermission== PackageManager.PERMISSION_DENIED || readPermission== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                WriteDiaryActivity.REQ_GALLERY
            )
        }else{
            val intent= Intent(Intent.ACTION_PICK)
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
        val datePicker=binding.datePickerUpdatediary
        binding.consUpdatediaryDatepicker.visibility= View.VISIBLE
        binding.btnUpdatediaryComplete.visibility= View.GONE
        val today= Calendar.getInstance()
        var date=binding.tvUpdatediaryDate.text.toString().split("-")
        datePicker.init(date[0].toInt(), date[1].toInt()-1, date[2].toInt()){
                view, year, month, day ->
            val month = month + 1

            binding.tvUpdatediaryOk.setOnClickListener {
                var m=month.toString()
                var d=day.toString()
                if(month<10){
                    m="0${month}"
                }
                if(day<10)
                    d="0${day}"
                binding.tvUpdatediaryDate.text="$year-$m-$d"
                binding.consUpdatediaryDatepicker.visibility= View.GONE
                binding.btnUpdatediaryComplete.visibility= View.VISIBLE
            }
            binding.tvUpdatediaryCancel.setOnClickListener {
                binding.consUpdatediaryDatepicker.visibility= View.GONE
                binding.btnUpdatediaryComplete.visibility= View.VISIBLE
            }
        }

    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.readdiary_toolbar_menu, menu)
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
}