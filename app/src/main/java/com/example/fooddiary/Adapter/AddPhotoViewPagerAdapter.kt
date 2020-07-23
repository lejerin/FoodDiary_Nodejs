package com.example.fooddiary.Adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.example.fooddiary.R
import kotlinx.android.synthetic.main.item_photo_viewpager.view.*

import java.net.URI

class AddPhotoViewPagerAdapter(private val list: ArrayList<Uri>): PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val view = inflater.inflate(R.layout.item_photo_viewpager, container, false)

        view.ivItem.setClipToOutline(true);
        view.ivItem.setImageURI(list[position])

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