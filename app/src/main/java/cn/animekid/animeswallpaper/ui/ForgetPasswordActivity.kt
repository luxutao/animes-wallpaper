package cn.animekid.animeswallpaper.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.api.Requester
import cn.animekid.animeswallpaper.data.ResponseDataBean
import cn.animekid.animeswallpaper.utils.ToolsHelper
import kotlinx.android.synthetic.main.forget_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ForgetPasswordActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forget_password)
        setSupportActionBar(this.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar))
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        forget.setOnClickListener {
            val user_email = email.text.toString()
            if (TextUtils.isEmpty(user_email) || ToolsHelper().isEmailValid(user_email) != true) {
                Toast.makeText(this, "邮箱不能为空或者邮箱格式不正确!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "重置链接已发送,请注意查收!", Toast.LENGTH_SHORT).show()
            Requester.apiService().forgetPassword(email = user_email).enqueue(object: Callback<ResponseDataBean> {
                override fun onResponse(call: Call<ResponseDataBean>, response: Response<ResponseDataBean>) {
                    val c = response.body()!!
                    if (c.code == 200) {
                        finish()
                    }
                }

                override fun onFailure(call: Call<ResponseDataBean>, t: Throwable) {
                    Log.d("send_captcha", t.message)
                    Toast.makeText(this@ForgetPasswordActivity, "发送失败,请稍后再试.", Toast.LENGTH_SHORT).show()
                }
            })
        }

        login.setOnClickListener { finish() }
    }

}