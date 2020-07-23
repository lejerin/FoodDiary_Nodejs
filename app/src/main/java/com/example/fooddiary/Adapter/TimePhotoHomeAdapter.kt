package com.example.fooddiary.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddiary.Model.Time
import com.example.fooddiary.R


class TimePhotoHomeAdapter(
    timeList: MutableList<String>,
    photoList: HashMap<String, MutableList<Time>>
) : RecyclerView.Adapter<TimePhotoHomeAdapter.TimePhotoViewHolder>() {

    private var timeList : MutableList<String> = timeList
    private var photoList : HashMap<String,MutableList<Time>> = photoList
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimePhotoViewHolder {
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.row_item_home_time,parent,false)

        context = parent!!.context
        return TimePhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimePhotoViewHolder, position: Int) {

        var timeData = timeList!![position]

        val result = timeData.substring(timeData.lastIndexOf("-") + 1)
        val result2 = result.substring(result.lastIndexOf("/") + 1)
        holder!!.textTitle.text =  result2

        if(photoList.get(timeData) != null){
            var photoAdapter = PhotoAdapter(0, photoList.get(timeData)!!)
            val gridLayoutManager = GridLayoutManager(context, 3)

            holder!!.rv_photo.adapter = photoAdapter
            holder!!.rv_photo.layoutManager = gridLayoutManager
        }



    }

    override fun getItemCount(): Int {
        return timeList!!.size
    }


    class TimePhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textTitle = view.findViewById(R.id.time_title) as Button
        var rv_photo = view.findViewById(R.id.rv_time_photo) as RecyclerView


    }
}

