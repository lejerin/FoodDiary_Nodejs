package com.example.fooddiary.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fooddiary.Activity.MainActivity
import com.example.fooddiary.Adapter.ReviewJoinAdapter
import com.example.fooddiary.Helper.MyApplication
import com.example.fooddiary.fragment.ReviewDetailFragment

import com.example.fooddiary.Model.Join
import com.example.fooddiary.Network.RetrofitClient
import com.example.fooddiary.R
import kotlinx.android.synthetic.main.fragment_home.no_data_in_recyclerview
import kotlinx.android.synthetic.main.fragment_review.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.String
import kotlin.Throwable


class ReviewFragment : Fragment() {

    //중복없는 날짜만
    val joinList = mutableListOf<Join>()
    val dupleList : HashMap<String, Join> = hashMapOf()

    private var findOrder = "DESC"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_review, container, false)

        getJoinData()

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        var reviewAdapter = ReviewJoinAdapter(joinList)
        reviewAdapter.setReviewItemClickListener(object : ReviewJoinAdapter.OnItemClickListener{
            override fun onClick(position: Int) {

                val reviewDetailFragment = ReviewDetailFragment()
                val bundle = Bundle()
                bundle.putString("address", joinList.get(position).address)
                bundle.putString("name", joinList.get(position).locationname)
                reviewDetailFragment.arguments = bundle

                val transaction = getFragmentManager()!!.beginTransaction()
                transaction.add(R.id.frame_layout, reviewDetailFragment)
                transaction.addToBackStack("review_detail")
                transaction.commit();

            }
        })
        rv_review.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        rv_review.adapter = reviewAdapter
        rv_review.layoutManager = LinearLayoutManager(context)


    }

    fun setOrder(order: String){
        findOrder = order
        getJoinData()
    }

     fun getJoinData(){
         joinList.clear()
         dupleList.clear()

        RetrofitClient.getService().getLocationJoin("\""  +
                MyApplication.prefs.getString("email", "no email") + "\"", findOrder).enqueue(object :
            Callback<List<Join>> {
            override fun onFailure(call: Call<List<Join>>, t: Throwable) {
                System.out.println("서버 오류입니다 "+ t.message)
            }

            override fun onResponse(call: Call<List<Join>>, response: Response<List<Join>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    System.out.println(data)
                    //목록 받아오기
                    if (data != null) {

                        //중복 처리
                        for(i in 0..data.size-1){
                            if(dupleList.containsKey(data[i].address)){
                                when(data[i].ranknum){
                                    1-> dupleList.get(data[i].address)!!.ranknum1 += 1
                                    2-> dupleList.get(data[i].address)!!.ranknum2 += 1
                                    3-> dupleList.get(data[i].address)!!.ranknum3 += 1
                                }
                            }else{
                                dupleList.put(data[i].address!!, data[i])
                                when(data[i].ranknum){
                                    1-> dupleList.get(data[i].address)!!.ranknum1 += 1
                                    2-> dupleList.get(data[i].address)!!.ranknum2 += 1
                                    3-> dupleList.get(data[i].address)!!.ranknum3 += 1
                                }
                            }
                            dupleList.get(data[i].address)!!.num += 1
                        }

                        joinList.addAll(dupleList.values)


                        rv_review.adapter?.notifyDataSetChanged()
                    }
                }else{
                    System.out.println("서버 오류입니다 time 없음")
                    rv_review.adapter?.notifyDataSetChanged()
                    no_data_in_recyclerview.visibility = View.VISIBLE
                }

            }

        })
    }
}