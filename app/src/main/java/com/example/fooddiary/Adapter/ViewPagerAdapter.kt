package com.example.fooddiary.Adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.fooddiary.R
import kotlinx.android.synthetic.main.item_photo_viewpager.view.*
import java.net.URI

class ViewPagerAdapter(private val list: ArrayList<String>): PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val view = inflater.inflate(R.layout.item_photo_viewpager, container, false)

        view.ivItem.setClipToOutline(true);
        Glide.with(container.context).load(list[position]).into(view.ivItem)

        container.addView(view)
        return view
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