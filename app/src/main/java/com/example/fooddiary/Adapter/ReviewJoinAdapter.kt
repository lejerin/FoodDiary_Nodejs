package com.example.fooddiary.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddiary.Model.Join
import com.example.fooddiary.R


class ReviewJoinAdapter(getList: MutableList<Join>) : RecyclerView.Adapter<ReviewJoinAdapter.PhotoViewHolder>() {

    private var joinList :  MutableList<Join> = getList
    private lateinit var context: Context

    //ClickListener
    interface OnItemClickListener {
        fun onClick(position: Int)
    }
    private lateinit var itemClickListener : OnItemClickListener

    fun setReviewItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {

        context = parent!!.context

        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.row_item_review,parent,false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {

        holder!!.imageView.setClipToOutline(true);
        Glide.with(context).load(joinList!![position].uri1).into(holder!!.imageView)

        holder!!.title.text = joinList!![position].locationname
        holder!!.address.text = joinList!![position].address

        holder!!.num.text = "+" + joinList!![position].num

        val best = joinList!![position].ranknum1.toInt()
        val soso = joinList!![position].ranknum2.toInt()
        val bad = joinList!![position].ranknum3.toInt()
        val many = mutableMapOf<Int,Int>()
        many.put(bad, 3)
        many.put(soso, 2)
        many.put(best, 1)

        when(many.get(Math.max(Math.max(best, soso), bad))){
            1 -> {
                holder!!.rank1_img.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                holder!!.rank1.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
            2 -> {
                holder!!.rank2_img.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                holder!!.rank2.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
            3 -> {
                holder!!.rank3_img.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                holder!!.rank3.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
        }

        holder!!.rank1.text = "최고(" + best + ")"
        holder!!.rank2.text = "만족(" + soso + ")"
        holder!!.rank3.text = "별로(" + bad + ")"

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(position)
        }

    }


    override fun getItemCount(): Int {
        return joinList!!.size
    }


    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView = view.findViewById(R.id.review_rv_img) as ImageView
        var title = view.findViewById(R.id.review_rv_title) as TextView
        var address = view.findViewById(R.id.review_rv_address) as TextView
        var rank1 = view.findViewById(R.id.rv_review_rank1) as TextView
        var rank2 = view.findViewById(R.id.rv_review_rank2) as TextView
        var rank3 = view.findViewById(R.id.rv_review_rank3) as TextView
        var rank1_img = view.findViewById(R.id.rv_review_rank1_img) as ImageView
        var rank2_img = view.findViewById(R.id.rv_review_rank2_img) as ImageView
        var rank3_img = view.findViewById(R.id.rv_review_rank3_img) as ImageView
        var num = view.findViewById(R.id.review_rv_num_text) as Button
    }
}

