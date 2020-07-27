package com.example.fooddiary.Activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.fooddiary.Adapter.AddPhotoViewPagerAdapter
import com.example.fooddiary.Adapter.ViewPagerAdapter
import com.example.fooddiary.Adapter.ViewPagerDetailAdapter
import com.example.fooddiary.R
import kotlinx.android.synthetic.main.activity_detail_post.*
import kotlinx.android.synthetic.main.activity_detail_post.circleAnimIndicator
import kotlinx.android.synthetic.main.activity_detail_post.viewpager
import kotlinx.android.synthetic.main.activity_view_pager.*

class ViewPagerActivity : AppCompatActivity() {

    val photoList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)


        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                circleAnimIndicator.selectDot(position)
            }

        })

        photoList.add(intent.getStringExtra("uri1"))
        if (intent.getStringExtra("uri2") != null) {
            photoList.add(intent.getStringExtra("uri2"))
        }
        if (intent.getStringExtra("uri3") != null) {
            photoList.add(intent.getStringExtra("uri3"))
        }
        if (intent.getStringExtra("uri4") != null) {
            photoList.add(intent.getStringExtra("uri4"))
        }

       if(intent.getBooleanExtra("add",true)){
           val photoViewPagerAdapter: ViewPagerDetailAdapter = ViewPagerDetailAdapter(true, photoList as ArrayList<String>)
           viewpager.adapter = photoViewPagerAdapter
       }else{
           val photoViewPagerAdapter: ViewPagerDetailAdapter = ViewPagerDetailAdapter(false, photoList as ArrayList<String>)
           viewpager.adapter = photoViewPagerAdapter
       }

        //원사이의 간격
        circleAnimIndicator.setItemMargin(15);
        //애니메이션 속도
        circleAnimIndicator.setAnimDuration(300);
        //indecator 생성
        circleAnimIndicator.createDotPanel(photoList.size, R.drawable.viewpage_indicator_off , R.drawable.viewpager_indicator_on);

        System.out.println(intent.getIntExtra("pos",0))
        viewpager.setCurrentItem(intent.getIntExtra("pos",0), false)

    }
}