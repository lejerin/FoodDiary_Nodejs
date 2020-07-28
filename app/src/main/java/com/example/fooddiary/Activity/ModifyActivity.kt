package com.example.fooddiary.Activity

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.fooddiary.Adapter.ViewPagerAdapter
import com.example.fooddiary.Helper.MyApplication
import com.example.fooddiary.Model.Photo
import com.example.fooddiary.Model.Time
import com.example.fooddiary.Model.TimeCount
import com.example.fooddiary.Network.RetrofitClient
import com.example.fooddiary.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_add_post.add_date_text
import kotlinx.android.synthetic.main.activity_add_post.circleAnimIndicator
import kotlinx.android.synthetic.main.activity_add_post.viewpager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ModifyActivity : AppCompatActivity() {

    private val PReqCode = 2
    private val REQUESCODE = 3
    private var savePhotoNum = 0
    private val photoList = mutableListOf<String>()
    val stringList = mutableListOf<Boolean>()
    private val photoViewPagerAdapter: ViewPagerAdapter = ViewPagerAdapter(photoList as ArrayList<String>, stringList as ArrayList<Boolean>)
    private var selectIndicatorNum = 0
    private val REQUESCODE_MAP = 60
    private var selectEmotionLayout : ConstraintLayout? = null
    private var selectEmotionImg: ImageView? = null
    private var selectEmotionText: TextView? = null
    private var selectDate = ""
    private var selectTimeRadio = 0
    //저장 데이터
    private var locationText : String = ""
    private var addressText : String? = null
    private var location_x : Double? = null
    private var location_y : Double? = null

    private var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        //날짜, 사진, 내용, 위치, 상세주소, 시간대, 평가
        id = intent.getStringExtra("id")

        add_date_text.text = intent.getStringExtra("date")
        editTextTextPersonName.setText(intent.getStringExtra("text"))
        locationText = intent.getStringExtra("name")
        location_title_text.setText(locationText)
        addressText = intent.getStringExtra("address")
        add_address_text.text = addressText

        stringList.add(true)
        photoList.add(intent.getStringExtra("uri1"))
        if(intent.getStringExtra("uri2") != null){
            photoList.add(intent.getStringExtra("uri2"))
            stringList.add(true)
        }
        if(intent.getStringExtra("uri3") != null){
            photoList.add(intent.getStringExtra("uri3"))
            stringList.add(true)
        }
        if(intent.getStringExtra("uri4") != null){
            photoList.add(intent.getStringExtra("uri4"))
            stringList.add(true)
        }

        //뷰페이저
        viewpager.adapter = photoViewPagerAdapter


        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                circleAnimIndicator.selectDot(position);
                selectIndicatorNum = position
            }

        })

        viewpager.adapter!!.notifyDataSetChanged()
        initIndicator()

        best_emotion_layout.setOnClickListener {
            chageEmotionImg(best_emotion_layout)
        }
        good_emotion_layout.setOnClickListener {
            chageEmotionImg(good_emotion_layout)
        }
        bad_emotion_layout.setOnClickListener {
            chageEmotionImg(bad_emotion_layout)
        }

        initTime(intent.getIntExtra("time",1))
        initTaste(intent.getIntExtra("rank",1))



        add_photo_btn.visibility = View.INVISIBLE
        add_photo_more_btn.visibility = View.VISIBLE
        remove_photo_btn.visibility = View.VISIBLE

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.radioButton1 -> selectTimeRadio = 1
                R.id.radioButton2 -> selectTimeRadio = 2
                R.id.radioButton3 -> selectTimeRadio = 3
                R.id.radioButton4 -> selectTimeRadio = 4
                R.id.radioButton5 -> selectTimeRadio = 5
            }
        }

        map_search_btn.setOnClickListener {
            val mapSearchIntent = Intent(this, MapSearchActivity::class.java)
            startActivityForResult(mapSearchIntent, REQUESCODE_MAP)
        }

        back_btn.setOnClickListener {
            finish()
        }

        select_date_btn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                var monthtext = (monthOfYear + 1).toString()
                if(monthtext.length == 1){
                    monthtext = "0" + monthtext
                }
                var daytext = dayOfMonth.toString()
                if(daytext.length == 1){
                    daytext = "0" + daytext
                }
                selectDate = "" + year + "-" + monthtext + "-" + daytext
                add_date_text.text = "" + year + "년 " + (monthOfYear + 1)  + "월 " + dayOfMonth + "일"

            }, year, month, day)

            dpd.show()
        }

        add_photo_btn.setOnClickListener {
            System.out.println("사진 추가 클릭")
            checkAndRequestForPermission()
        }

        add_photo_more_btn.setOnClickListener {
            System.out.println("사진 추가 클릭")
            if(photoList.size < 4){
                checkAndRequestForPermission()
            }else{
                Toast.makeText(this, "사진은 최대 4장까지 추가 가능합니다", Toast.LENGTH_SHORT).show()
            }

        }

        remove_photo_btn.setOnClickListener {
            photoViewPagerAdapter.removeItem(selectIndicatorNum);
            viewpager.setAdapter(photoViewPagerAdapter);
            circleAnimIndicator.removeDotPanel()
            initIndicator()
            if(photoList.size == 0){
                add_photo_more_btn.visibility = View.INVISIBLE
                remove_photo_btn.visibility = View.INVISIBLE
                add_photo_btn.visibility = View.VISIBLE
            }
            selectIndicatorNum = 0

        }

        save_post_btn.setOnClickListener {
            //사진 저장
            if(checkAllInput()){
                //로딩화면 시작
                setLoadingView(true)


                locationText = location_title_text.text.toString()
                savePhoto(Uri.parse(photoList[0]))
            }
        }

    }

    private fun checkAllInput(): Boolean {
        var checkNum = false

        //사진 한 장 이상인지
        if(photoList.size == 0){
            warning_photo_text.visibility = View.VISIBLE
            checkNum = true
        }else{
            warning_photo_text.visibility = View.INVISIBLE
        }
        //위치 입력되어있는지
        if(location_title_text.text.toString().equals("")){
            warning_location_text.visibility = View.VISIBLE
            checkNum = true
        }else{
            warning_location_text.visibility = View.INVISIBLE
        }

        //시간간 입력어있는지
        if(selectTimeRadio == 0){
            warning_time_text.visibility = View.VISIBLE
            checkNum = true
        }else{
            warning_time_text.visibility = View.INVISIBLE
        }

        //맛 입력되어있는지
        if(selectEmotionText == null){
            warning_taste_text.visibility = View.VISIBLE
            checkNum = true
        }else{
            warning_taste_text.visibility = View.INVISIBLE
        }

        if(checkNum){
            return false
        }
        return true

    }

    private fun setLoadingView(isShow : Boolean){
        if(isShow){
            save_text.text = "이미지 저장중"
            loadingLayout.visibility = View.VISIBLE
        }else{
            loadingLayout.visibility = View.INVISIBLE
        }

    }


    private fun initIndicator() {
        //원사이의 간격
        circleAnimIndicator.setItemMargin(15);
        //애니메이션 속도
        circleAnimIndicator.setAnimDuration(300);
        //indecator 생성
        circleAnimIndicator.createDotPanel(photoList.size, R.drawable.viewpage_indicator_off , R.drawable.viewpager_indicator_on);
    }


    //갤러리 사용자 권한 확인
    private fun checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PReqCode)
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        //galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(galleryIntent, REQUESCODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUESCODE && data != null) {
            photoList.add(data.data!!.toString())
            stringList.add(false)
            photoViewPagerAdapter.notifyDataSetChanged()

            circleAnimIndicator.removeDotPanel()
            initIndicator()
            circleAnimIndicator.selectDot(selectIndicatorNum);
            add_photo_more_btn.visibility = View.VISIBLE
            remove_photo_btn.visibility = View.VISIBLE
            add_photo_btn.visibility = View.GONE

        }
        if(resultCode == Activity.RESULT_OK && requestCode == REQUESCODE_MAP && data != null){
            locationText = data.getStringExtra("name")
            addressText =  data.getStringExtra("roadAddress")
            add_address_text.text = addressText
            add_address_text.visibility = View.VISIBLE
            location_title_text.setText(locationText)
            location_x = data.getStringExtra("x").toDouble()
            location_y = data.getStringExtra("y").toDouble()
        }
    }

    private fun initTime(num: Int){

        System.out.println("" + num + "시간대")
        selectTimeRadio = num
        when(num){
            1 -> radioButton1.isChecked = true
            2 -> radioButton2.isChecked = true
            3 -> radioButton3.isChecked = true
            4 -> radioButton4.isChecked = true
            5 -> radioButton5.isChecked = true

        }

    }

    private fun initTaste(num: Int){
        when(num){
            1 -> {
                best_emotion_layout.performClick()
            }
            2 -> {
                good_emotion_layout.performClick()
            }
            3 -> {
                bad_emotion_layout.performClick()
            }
        }
    }

    private fun chageEmotionImg(a: ConstraintLayout){
        if(selectEmotionLayout != null){
            selectEmotionLayout!!.setBackgroundResource(R.drawable.emotion_background)
            selectEmotionText!!.setTextColor(
                ContextCompat.getColor(this,
                R.color.brightGray))
            selectEmotionImg!!.setColorFilter(
                ContextCompat.getColor(this,
                R.color.brightGray));
        }
        selectEmotionLayout = a
        if(a == best_emotion_layout){
            selectEmotionImg = best_emotion
            selectEmotionText = best_emotion_text
        }
        else if(a == good_emotion_layout) {
            selectEmotionImg = good_emotion
            selectEmotionText = good_emotion_text
        }
        else{
            selectEmotionImg = bad_emotion
            selectEmotionText = bad_emotion_text
        }


        selectEmotionLayout!!.setBackgroundResource(R.drawable.emotion_background_selected)
        selectEmotionImg!!.setColorFilter(
            ContextCompat.getColor(this,
            R.color.colorPrimary));
        selectEmotionText!!.setTextColor(
            ContextCompat.getColor(this,
            R.color.colorPrimary))
    }


    private fun savePhoto(pickedImgUri : Uri){

        if(!stringList[savePhotoNum]){
            val storageReference = FirebaseStorage.getInstance().reference.child("post_images")
            val imageFilePath =
                storageReference.child(
                    MyApplication.prefs.getString("email", "no email") +
                            pickedImgUri.lastPathSegment!!)
            imageFilePath.putFile(pickedImgUri).addOnSuccessListener {
                imageFilePath.downloadUrl.addOnSuccessListener { uri ->
                    val imageDownloadLink = uri.toString()
                    photoList[savePhotoNum] = imageDownloadLink
                    //사진을 제외한 날짜, 날짜, 사용자 이메일 등 상세 텍스트,
                    //time에 이미 등록되어있는지 확인해야함
                    savePhotoNum += 1
                    System.out.println("저장 완료")
                    if(savePhotoNum == photoList.size){
                        save_text.text = "데이터 저장중"
                        uploadDBTime()
                    }else{
                        savePhoto(Uri.parse(photoList[savePhotoNum]))
                    }


                }.addOnFailureListener { e ->
                    //사진 업로드 실패
                }
            }
        }else{
            savePhotoNum += 1
            if(savePhotoNum == photoList.size){
                save_text.text = "데이터 저장중"
                uploadDBTime()
            }else{
                savePhoto(Uri.parse(photoList[savePhotoNum]))
            }
        }


    }


    //서버에 시간 게시글 저장 time
    private fun uploadDBTime(){
        System.out.println("시도" + selectDate)

        //첫 번째 사진 바뀌었으면 사진만 업데이트
        //날짜 바뀌었으면 날짜 업데이트
        if(!stringList[0] || !selectDate.equals("")){

            updateTime()

        }else{
            uploadDBPhotoUri()
        }

    }

    private fun updateTime(){

        System.out.println("시간 업데이트")

        val transformat = SimpleDateFormat("yyyy년 M월 d일")
        val date1 = transformat.parse(add_date_text.text.toString())
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date1)

        RetrofitClient.getService().putTimeUpdate("\""  + id + "\"" , "\""  + dateString + "\"" , "\""  + photoList[0] + "\"" ).enqueue(object :
            Callback<Time> {
            override fun onFailure(call: Call<Time>, t: Throwable) {
                System.out.println("서버 오류입니다" + t.message)
                //  iRegisterView.onCreateError("서버 오류입니다")
            }

            override fun onResponse(call: Call<Time>, response: Response<Time>) {
                if (response.isSuccessful) {
                    val data = response.body()

                    if (data != null) {
                        //    iRegisterView.onCreateSuccess(data.name, email)
                        //사진 저장해야함

                        //상세내용 변경함
                        uploadDBPhotoUri()


                    }
                }else{
                    //    iRegisterView.onCreateError("생성 실패하였습니다")
                }

            }

        })

    }


    //서버에 사진 게시글 내용 저장 photo
    private fun uploadDBPhotoUri(){

        //랭크 숫자로 변환
        var rankNum = 1
        when(selectEmotionText!!.text.toString()){
            "만족" -> rankNum = 2
            "별로" -> rankNum = 3
        }

        //몇개있는지 계산해야함 COUNT

        var photoRequest = RetrofitClient.getService().putPhotoUpdate(id,
            Photo(id, photoList[0], null,
            null, null, editTextTextPersonName.text.toString(), locationText, addressText, location_x, location_y, selectTimeRadio, rankNum)
        )

        when(photoList.size) {
            2 -> photoRequest = RetrofitClient.getService().putPhotoUpdate(id,
                Photo(id, photoList[0], photoList[1],
                null, null, editTextTextPersonName.text.toString(), locationText, addressText, location_x, location_y, selectTimeRadio, rankNum)
            )
            3 ->  photoRequest = RetrofitClient.getService().putPhotoUpdate(id,
                Photo(id, photoList[0], photoList[1],
                    photoList[2], null, editTextTextPersonName.text.toString(), locationText, addressText, location_x, location_y, selectTimeRadio, rankNum)
            )
            4 ->  photoRequest = RetrofitClient.getService().putPhotoUpdate(id,
                Photo(id, photoList[0], photoList[1],
                    photoList[2], photoList[3], editTextTextPersonName.text.toString(), locationText, addressText, location_x, location_y, selectTimeRadio, rankNum)
            )
        }

        photoRequest.enqueue(object :
            Callback<Photo> {
            override fun onFailure(call: Call<Photo>, t: Throwable) {
                System.out.println("서버 오류입니다" + t.message)
                //  iRegisterView.onCreateError("서버 오류입니다")
            }

            override fun onResponse(call: Call<Photo>, response: Response<Photo>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        //    iRegisterView.onCreateSuccess(data.name, email)
                        //사진 저장해야함
                        System.out.println("저장 완료" + data)
                        setLoadingView(false)
                        setResult(RESULT_OK, intent);
                        finish();



                    }
                }else{
                    //    iRegisterView.onCreateError("생성 실패하였습니다")
                }

            }

        })
    }






}