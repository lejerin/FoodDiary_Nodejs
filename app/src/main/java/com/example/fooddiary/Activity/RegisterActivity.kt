package com.example.fooddiary.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.fooddiary.Model.User
import com.example.fooddiary.Network.RetrofitClient
import com.example.fooddiary.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() , View.OnClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        register_btn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.register_btn ->{
                startRegister()
                isRegisterValid(register_email_text.text.toString(), register_pw_text.text.toString(),
                    register_pw2_text.text.toString(),register_name_text.text.toString())
            }
        }
    }

    fun isRegisterValid(email: String, password: String, password2: String, name: String) {

        if(TextUtils.isEmpty(email))
            errorRegisterResponse(0)
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            errorRegisterResponse(1)
        else if(password.length <= 6)
            errorRegisterResponse(2)
        else if(!password2.equals(password))
            errorRegisterResponse(3)
        else if(TextUtils.isEmpty(name))
            errorRegisterResponse(4)
        else{
            //입력은 정상, 서버에 등록필요
            createUser(email,password, name)
        }

    }

    fun errorRegisterResponse(code: Int) {

        when(code){
            0 -> sendMessage("메일을 입력해주세요")
            1 -> sendMessage("메일 주소 형식을 다시 입력해주세요")
            2 -> sendMessage("비번은 6자 이상")
            3 -> sendMessage("비번 확인과 다릅니다")
            4 -> sendMessage("닉네임을 입력해주세요")
            5 -> sendMessage("서버 오류 입니다")
            6 -> sendMessage("로그인 실패입니다")
        }
        finishRegister()
    }

    private fun createUser(email: String, password: String, name: String) {
        System.out.println("시도")

        RetrofitClient.getService().putNewUserJson(User(email,  password , name)).enqueue(object :
            Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                errorRegisterResponse(5)
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    System.out.println(data)
                    if (data != null) {
                        onCreateSuccess(data.name, email)
                    }
                }else{
                    errorRegisterResponse(5)
                }

            }

        })

    }


    fun onCreateSuccess(name: String, email: String) {
        finishRegister()

        //메인화면으로 이동
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.putExtra("name", name)
        mainIntent.putExtra("email",email)
        startActivity(mainIntent)
        finish()
    }

    fun startRegister() {
        login_progress.visibility = View.VISIBLE
        login_btn.visibility = View.INVISIBLE
    }

    fun finishRegister() {
        login_progress.visibility = View.INVISIBLE
        login_btn.visibility = View.VISIBLE
    }

    fun sendMessage(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}