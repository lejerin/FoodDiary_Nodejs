package com.example.fooddiary.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.fooddiary.Adapter.ViewPagerAdapter
import com.example.fooddiary.Model.Photo
import com.example.fooddiary.Network.RetrofitClient
import com.example.fooddiary.R
import kotlinx.android.synthetic.main.activity_detail_post.*
import kotlinx.android.synthetic.main.activity_detail_post.add_date_text
import kotlinx.android.synthetic.main.activity_detail_post.back_btn
import kotlinx.android.synthetic.main.activity_detail_post.circleAnimIndicator
import kotlinx.android.synthetic.main.activity_detail_post.viewpager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailPostActivity : AppCompatActivity() {

    val photoList = mutableListOf<String>()
    val stringList = mutableListOf<Boolean>()

    var id = ""
    var x = 0.0
    var y = 0.0
    lateinit var nowData : Photo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_post)

        back_btn.setOnClickListener {
            supportFinishAfterTransition()
        }

        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                circleAnimIndicator.selectDot(position);
            }

        })

        stringList.add(true)
        photoList.add(intent.getStringExtra("uri1"))
        val photoViewPagerAdapter: ViewPagerAdapter = ViewPagerAdapter(photoList as ArrayList<String>,
            stringList as ArrayList<Boolean>
        )
        viewpager.adapter = photoViewPagerAdapter


        //postnum, count로 사진이랑 날짜 갖고옴

        add_date_text.text = intent.getStringExtra("date")
        id = intent.getStringExtra("id")
         getPostnumCountPhoto(id)


        detail_post_address_text.setOnClickListener {

            val mapDetailIntent = Intent(this, MapDetailActivity::class.java)
            mapDetailIntent.putExtra("x",x)
            mapDetailIntent.putExtra("y",y)
            mapDetailIntent.putExtra("name",detail_post_location_text.text.toString())
            mapDetailIntent.putExtra("address",detail_post_address_text.text.toString())
            startActivity(mapDetailIntent)
        }

        detail_post_delete_btn.setOnClickListener {
            val builder = AlertDialog.Builder(this@DetailPostActivity)
            builder.setMessage("정말로 삭제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    setServerDeleteData()
                }
                .setNegativeButton("No") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        detail_post_modify_btn.setOnClickListener {
            val modifyActivity = Intent(this, ModifyActivity::class.java)
            modifyActivity.putExtra("id", nowData.id)
            modifyActivity.putExtra("date",add_date_text.text.toString())
            modifyActivity.putExtra("uri1",nowData.uri1)
            modifyActivity.putExtra("uri2",nowData.uri2)
            modifyActivity.putExtra("uri3",nowData.uri3)
            modifyActivity.putExtra("uri4",nowData.uri4)
            modifyActivity.putExtra("text",nowData.text)
            modifyActivity.putExtra("name",nowData.locationname)
            modifyActivity.putExtra("address",nowData.address)
            modifyActivity.putExtra("time",nowData.time)
            modifyActivity.putExtra("rank",nowData.ranknum)
            startActivityForResult(modifyActivity, 11)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 11 && resultCode == RESULT_OK){
            System.out.println("초기화 액티비티")
            photoList.clear()
            stringList.clear()
            getPostnumCountPhoto(id)
        }



    }

    private fun getPostnumCountPhoto(id: String) {


        RetrofitClient.getService().getPostnumCountPhoto("\"" + id + "\"").enqueue(object :
            Callback<Photo> {
            override fun onFailure(call: Call<Photo>, t: Throwable) {
                System.out.println("서버 오류입니다 "+ t.message)
            }

            override fun onResponse(call: Call<Photo>, response: Response<Photo>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    System.out.println(data)
                    //목록 받아오기
                    if (data != null){
                        System.out.println(data)
                        nowData = data
                        initView(data)

                    }
                }else{
                    System.out.println("없음")

                }

            }

        })
    }

    private fun setServerDeleteData(){

        RetrofitClient.getService().setDeletePhotoById("\"" + id + "\"").enqueue(object :
            Callback<Photo> {
            override fun onFailure(call: Call<Photo>, t: Throwable) {
                System.out.println("서버 오류입니다 "+ t.message)
            }

            override fun onResponse(call: Call<Photo>, response: Response<Photo>) {
                if (response.isSuccessful) {
                    System.out.println("삭제완료")
                    MainActivity.getInstancem().refreshHome(44,RESULT_OK,null)
                    finish()
                }else{
                    System.out.println("없음")

                }

            }

        })
    }


    private fun initView(photo: Photo){

        if(photo.uri2 != null){
            photoList.add(photo.uri2)
            stringList.add(true)
        }

        if(photo.uri3 != null){
            photoList.add(photo.uri3)
            stringList.add(true)
        }

        if(photo.uri4 != null){
            photoList.add(photo.uri4)
            stringList.add(true)
        }


        viewpager.adapter!!.notifyDataSetChanged()

        //원사이의 간격
        circleAnimIndicator.setItemMargin(15);
        //애니메이션 속도
        circleAnimIndicator.setAnimDuration(300);
        //indecator 생성
        circleAnimIndicator.createDotPanel(photoList.size, R.drawable.viewpage_indicator_off , R.drawable.viewpager_indicator_on);


        if(photo.latitude != null){
            x = photo.latitude.toDouble()
            y = photo.longitude!!.toDouble()
        }


        detail_post_location_text.text = photo.locationname
        if(photo.address != null){
            detail_post_address_text.text = photo.address
            detail_post_address_text.visibility = View.VISIBLE
        }
        detail_post_location_btn.visibility = View.VISIBLE
        detail_post_location_text.visibility = View.VISIBLE

        initTime(photo.time)
        initTaste(photo.ranknum)
        detail_post_text.text = photo.text


        //주소 선택하면 지도로 이동
        select_loaction_layout.setOnClickListener {

        }

    }



    private fun initTime(num: Int){

        when(num){
            1 -> detail_post_time_text.text = "아침"
            2 -> detail_post_time_text.text = "점심"
            3 -> detail_post_time_text.text = "저녁"
            4 -> detail_post_time_text.text = "야식"
            5 -> detail_post_time_text.text = "간식"

        }

        if(num > 0){
            detail_post_time_img.visibility = View.VISIBLE
            detail_post_time_text.visibility = View.VISIBLE
        }


    }

    private fun initTaste(num: Int){
        when(num){
            1 -> {
                detail_post_taste_img.setImageResource(R.drawable.laughing)
                detail_post_taste_text.text = "최고"
            }
            2 -> {
                detail_post_taste_img.setImageResource(R.drawable.happy)
                detail_post_taste_text.text = "만족"
            }
            3 -> {
                detail_post_taste_img.setImageResource(R.drawable.nervous)
                detail_post_taste_text.text = "별로"
            }
        }
        detail_post_taste_img.visibility = View.VISIBLE
        detail_post_taste_text.visibility = View.VISIBLE
    }




}