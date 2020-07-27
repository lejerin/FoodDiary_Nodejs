package com.example.fooddiary.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddiary.Activity.DetailPostActivity
import com.example.fooddiary.Activity.MainActivity
import com.example.fooddiary.Model.Time
import com.example.fooddiary.R
import java.text.SimpleDateFormat
import java.util.*


class PhotoAdapter(adapter: Int ,photoList: MutableList<Time>) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    private var photoList :  MutableList<Time> = photoList
    private lateinit var context: Context
    private val adapterNum = adapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {

        context = parent!!.context

        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.row_item_grid_photo,parent,false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {

        var photoUri = photoList!![position].uri1
        holder!!.imageView.setClipToOutline(true);
        Glide.with(context).load(photoUri).into(holder!!.imageView)

        holder!!.imageView.setOnClickListener {
            val detailPostIntent = Intent(context, DetailPostActivity::class.java)
            detailPostIntent.putExtra("id", photoList!![position].id)
            detailPostIntent.putExtra("uri1", photoUri)
            var output: String = SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA).format(StringtoDate(photoList!![position].date))
            detailPostIntent.putExtra("date", output)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context as MainActivity, holder!!.imageView, "profile")
            context.startActivity(detailPostIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), options.toBundle())


        }
    }

    fun StringtoDate(str: String, dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(str)
    }

    override fun getItemCount(): Int {
        return photoList!!.size
    }


    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView = view.findViewById(R.id.grid_photo) as ImageView

    }
}

