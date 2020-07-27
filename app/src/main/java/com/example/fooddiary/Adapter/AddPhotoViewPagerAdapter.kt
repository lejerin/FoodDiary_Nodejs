package com.example.fooddiary.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.example.fooddiary.Activity.ViewPagerActivity
import com.example.fooddiary.R
import kotlinx.android.synthetic.main.item_photo_viewpager.view.*

import java.net.URI

class AddPhotoViewPagerAdapter(private val list: ArrayList<Uri>): PagerAdapter() {

    private lateinit var context: Context

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        context = container!!.context

        val inflater = LayoutInflater.from(container.context)
        val view = inflater.inflate(R.layout.item_photo_viewpager, container, false)

        view.ivItem.setClipToOutline(true);
        view.ivItem.setImageURI(list[position])

        view.ivItem.setOnClickListener {
            val viewPagerIntent = Intent(context, ViewPagerActivity::class.java)
            viewPagerIntent.putExtra("pos",position)
            viewPagerIntent.putExtra("add",true)
            viewPagerIntent.putExtra("uri1",list[0].toString())
            if(list.size > 1)
                viewPagerIntent.putExtra("uri2",list[1].toString())
            if(list.size > 2)
                viewPagerIntent.putExtra("uri3",list[2].toString())
            if(list.size > 3)
                viewPagerIntent.putExtra("uri4",list[3].toString())

            context.startActivity(viewPagerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

        }

        container.addView(view)
        return view
    }

    fun removeItem(position: Int) {
        if (position > -1 && position < list.size) {
            list.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View?)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun getCount(): Int {
        return list.size
    }
}