package com.example.fooddiary.Activity

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.fooddiary.Adapter.AddPhotoViewPagerAdapter
import com.example.fooddiary.Helper.MyApplication
import com.example.fooddiary.Model.Photo
import com.example.fooddiary.Model.Time
import com.example.fooddiary.Model.TimeCount
import com.example.fooddiary.Network.RetrofitClient
import com.example.fooddiary.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_add_post.add_date_text
import kotlinx.android.synthetic.main.activity_add_post.select_date_btn
import kotlinx.android.synthetic.main.activity_add_post.viewpager
import kotlinx.android.synthetic.main.app_bar_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS
import kotlin.collections.ArrayList

class AddPostActivity : AppCompatActivity() {

    private val PReqCode = 2
    private val REQUESCODE = 3
    private val REQUESCODE_MAP = 60
    private val photoList = mutableListOf<Uri>()
    private val storageUriList = mutableListOf<String>()
    private val photoViewPagerAdapter: AddPhotoViewPagerAdapter = AddPhotoViewPagerAdapter(photoList as ArrayList<Uri>)

    private var savePhotoNum = 0
    private var selectDate = ""

    private var selectIndicatorNum = 0

    private var selectEmotionLayout : ConstraintLayout? = null
    private var selectEmotionImg: ImageView? = null
    private var selectEmotionText: TextView? = null

    private var selectTimeRadio = 0


    //저장 데이터
    private var locationText : String = ""
    private var addressText : String? = null
    private var location_x : Double? = null
    private var location_y : Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)


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

        map_search_btn.setOnClickListener {
            val mapSearchIntent = Intent(this, MapSearchActivity::class.java)
            startActivityForResult(mapSearchIntent, REQUESCODE_MAP)
        }

        best_emotion_layout.setOnClickListener {
            chageEmotionImg(best_emotion_layout)
        }
        good_emotion_layout.setOnClickListener {
            chageEmotionImg(good_emotion_layout)
        }
        bad_emotion_layout.setOnClickListener {
            chageEmotionImg(bad_emotion_layout)
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.radioButton1 -> selectTimeRadio = 1
                R.id.radioButton2 -> selectTimeRadio = 2
                R.id.radioButton3 -> selectTimeRadio = 3
                R.id.radioButton4 -> selectTimeRadio = 4
                R.id.radioButton5 -> selectTimeRadio = 5
            }
        }


        //오늘 날짜
        selectDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val nowYear = SimpleDateFormat("yyyy", Locale.KOREA).format(Date())
        val nowMonth = SimpleDateFormat("M", Locale.KOREA).format(Date())
        val nowDay = SimpleDateFormat("d", Locale.KOREA).format(Date())
        add_date_text.text =  "" + nowYear + "년 " + nowMonth + "월 " + nowDay + "일"

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

        }

        save_post_btn.setOnClickListener {
            //사진 저장
            if(checkAllInput()){
                //로딩화면 시작
                setLoadingView(true)

                
                locationText = location_title_text.text.toString()
                savePhoto(photoList[0])
            }
        }

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


    private fun savePhoto(pickedImgUri : Uri){
        val storageReference = FirebaseStorage.getInstance().reference.child("post_images")
        val imageFilePath =
            storageReference.child(
                MyApplication.prefs.getString("email", "no email") +
                        pickedImgUri.lastPathSegment!!)
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener {
            imageFilePath.downloadUrl.addOnSuccessListener { uri ->
                val imageDownloadLink = uri.toString()
                storageUriList.add(uri.toString())
                //사진을 제외한 날짜, 날짜, 사용자 이메일 등 상세 텍스트,
                //time에 이미 등록되어있는지 확인해야함
                savePhotoNum += 1
                System.out.println("저장 완료")
                if(savePhotoNum == photoList.size){
                    save_text.text = "데이터 저장중"
                    getPostnumTime(selectDate)
                }else{
                    savePhoto(photoList[savePhotoNum])
                }


            }.addOnFailureListener { e ->
                //사진 업로드 실패
            }
        }
    }


    //count 개수 요청함
    private fun getPostnumTime(date: String) {

        RetrofitClient.getService().getPostnumTime("\""  +
                MyApplication.prefs.getString("email", "no email") + "\"", "\"" + date + "\"").enqueue(object :
            Callback<TimeCount> {
            override fun onFailure(call: Call<TimeCount>, t: Throwable) {
                System.out.println("서버 오류입니다 "+ t.message)
            }

            override fun onResponse(call: Call<TimeCount>, response: Response<TimeCount>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    System.out.println("값" + data)
                    //목록 받아오기
                    if (data != null){

                        //있다는 뜻
                        System.out.println("있음")
                        System.out.println(data.count)
                        //uploadDBPhotoUri(MyApplication.prefs.getString("email", "no email") + date, data[0].count + 1)
                        uploadDBTime(data.count + 1)
                    }
                }else{
                    System.out.println("없음")
                    uploadDBTime(1)
                }

            }

        })
    }

    //서버에 시간 게시글 저장 time
    private fun uploadDBTime(count: Int){
        System.out.println("시도" + selectDate)
        val randomId = UUID.randomUUID().toString()
        RetrofitClient.getService().putNewTime(
            Time(randomId, MyApplication.prefs.getString("email", "no email"), selectDate, count,
            storageUriList[0])
        ).enqueue(object :
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

                        uploadDBPhotoUri(randomId)


                    }
                }else{
                    //    iRegisterView.onCreateError("생성 실패하였습니다")
                }

            }

        })

    }


    //서버에 사진 게시글 내용 저장 photo
    private fun uploadDBPhotoUri(id: String){

        //랭크 숫자로 변환
        var rankNum = 1
        when(selectEmotionText!!.text.toString()){
            "만족" -> rankNum = 2
            "별로" -> rankNum = 3
        }

        //몇개있는지 계산해야함 COUNT

        var photoRequest = RetrofitClient.getService().putNewPhoto(Photo(id, storageUriList[0], null,
            null, null, editTextTextPersonName.text.toString(), locationText, addressText, location_x, location_y, selectTimeRadio, rankNum)
        )

            when(storageUriList.size) {
                2 -> photoRequest = RetrofitClient.getService().putNewPhoto(Photo(id, storageUriList[0], storageUriList[1],
                null, null, editTextTextPersonName.text.toString(), locationText, addressText, location_x, location_y, selectTimeRadio, rankNum))
                3 ->  photoRequest = RetrofitClient.getService().putNewPhoto(Photo(id, storageUriList[0], storageUriList[1],
                    storageUriList[2], null, editTextTextPersonName.text.toString(), locationText, addressText, location_x, location_y, selectTimeRadio, rankNum))
                4 ->  photoRequest = RetrofitClient.getService().putNewPhoto(Photo(id, storageUriList[0], storageUriList[1],
                    storageUriList[2], storageUriList[3], editTextTextPersonName.text.toString(), locationText, addressText, location_x, location_y, selectTimeRadio, rankNum))
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
            photoList.add(data.data!!)
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

    private fun chageEmotionImg(a: ConstraintLayout){
        if(selectEmotionLayout != null){
            selectEmotionLayout!!.setBackgroundResource(R.drawable.emotion_background)
            selectEmotionText!!.setTextColor(ContextCompat.getColor(this,
                R.color.brightGray))
            selectEmotionImg!!.setColorFilter(ContextCompat.getColor(this,
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
        selectEmotionImg!!.setColorFilter(ContextCompat.getColor(this,
            R.color.colorPrimary));
        selectEmotionText!!.setTextColor(ContextCompat.getColor(this,
            R.color.colorPrimary))
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

        //위치 입력되어있는지
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


}