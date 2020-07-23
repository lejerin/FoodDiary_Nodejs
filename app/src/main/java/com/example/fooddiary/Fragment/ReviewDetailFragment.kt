package com.example.fooddiary.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fooddiary.Activity.MainActivity
import com.example.fooddiary.Adapter.ReviewPhotoAdapter
import com.example.fooddiary.Helper.MyApplication
import com.example.fooddiary.Model.Time
import com.example.fooddiary.Network.RetrofitClient
import com.example.fooddiary.R
import kotlinx.android.synthetic.main.fragment_review_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ReviewDetailFragment : Fragment(){

    //중복없는 날짜만
    val timeList = mutableListOf<String>()
    //고유번호, 대표사진, count 순서대로
    val photoList : HashMap<String,MutableList<Time>> = hashMapOf()
    lateinit var address : String
    private var findOrder = "DESC"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_review_detail, container, false)

        val a = activity as MainActivity
        a.setActionBarTitle(arguments!!.getString("name").toString())
        address = arguments!!.getString("address").toString()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var musicAdapter = ReviewPhotoAdapter(timeList,photoList)

        rv_review_detail.adapter = musicAdapter
        rv_review_detail.layoutManager = LinearLayoutManager(context)


        getUserTime()

    }

    fun setOrder(order: String){
        findOrder = order
        getUserTime()
    }

    fun getUserTime(){

        timeList.clear()
        photoList.clear()

        RetrofitClient.getService().getAddressAllJoin(
            "\""  + MyApplication.prefs.getString("email", "no email") + "\"",
            "\"" + address + "\"" ,findOrder).enqueue(object :
            Callback<List<Time>> {
            override fun onFailure(call: Call<List<Time>>, t: Throwable) {
                System.out.println("서버 오류입니다 "+ t.message)
            }

            override fun onResponse(call: Call<List<Time>>, response: Response<List<Time>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    System.out.println(data)
                    //목록 받아오기
                    if (data != null) {
                        no_data_in_recyclerview.visibility = View.GONE

                        var beforeDate = ""
                        for(i in data.indices) {
                            var output: String = SimpleDateFormat("yyyy-M-d", Locale.KOREA).format(StringtoDate(data.get(i).date))

                            if(!output.equals(beforeDate)){
                                //중복 되지 않으면 time list에 넣기
                                beforeDate = output
                                timeList.add(output)
                            }
                            if(photoList.containsKey(output)){
                                //이미 존재하면
                                photoList.get(output)!!.add(data.get(i))
                            }else{
                                photoList.put(output, mutableListOf(data.get(i)))
                            }
                        }

                        rv_review_detail.adapter?.notifyDataSetChanged()

                    }
                }else{
                    System.out.println("서버 오류입니다 time 없음")
                    rv_review_detail.adapter?.notifyDataSetChanged()
                    no_data_in_recyclerview.visibility = View.VISIBLE
                }

            }

        })
    }

    fun StringtoDate(str: String, dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(str)
    }
}