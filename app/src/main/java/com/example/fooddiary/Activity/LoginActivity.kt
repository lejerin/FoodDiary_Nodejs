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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LoginActivity : AppCompatActivity() , View.OnClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_btn.setOnClickListener(this)
        register_text.setOnClickListener(this)

        try {
            val info = packageManager.getPackageInfo(
                "com.example.fooddiary",
                PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }

    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.login_btn ->{
                startLogin()
                isLoginValid(login_mail_text.text.toString(), login_pw_text.text.toString())
            }
            R.id.register_text ->{
                val registerIntent = Intent(this, RegisterActivity::class.java)
                startActivity(registerIntent)
            }


        }
    }


    fun isLoginValid(email: String, password: String) {
        if(TextUtils.isEmpty(email))
            errorLoginResponse(0)
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            errorLoginResponse(1)
        else if(password.length <= 6)
            errorLoginResponse(2)
        else{
            //입력은 정상, 서버에 확인필요
            getUserAuth(email,password)
        }

    }

    fun errorLoginResponse(code: Int) {

            when(code){
                0 -> sendMessage("Email must not be null")
                1 -> sendMessage("Wrong email address")
                2 -> sendMessage("Password must be greater than 6")
                3 -> sendMessage("서버 오류 입니다")
                4 -> sendMessage("로그인 실패입니다")
            }
            finishLogin()
        }

    private fun getUserAuth(email: String, password: String) {
        System.out.println("시도")

        RetrofitClient.getService().getEmailUser("\""  + email + "\"").enqueue(object :
            Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                errorLoginResponse(3)
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    System.out.println(data)
                    //비밀번호 비교
                    if (data != null) {
                        if((data.password).equals(password))
                            successAuth(data.name, email)
                        else
                            errorLoginResponse(4)
                    }
                }else{
                    errorLoginResponse(3)
                }

            }

        })

    }

    fun successAuth(name: String, email: String) {
        finishLogin()
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.putExtra("name", name)
        mainIntent.putExtra("email",email)
        startActivity(mainIntent)
        finish()

    }

    fun startLogin() {
        login_progress.visibility = View.VISIBLE
        login_btn.visibility = View.INVISIBLE
    }

    fun finishLogin() {
        login_progress.visibility = View.INVISIBLE
        login_btn.visibility = View.VISIBLE
    }

    fun sendMessage(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}