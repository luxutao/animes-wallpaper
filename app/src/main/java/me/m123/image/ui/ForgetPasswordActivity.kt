package me.m123.image.ui

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import me.m123.image.R
import me.m123.image.api.Requester
import me.m123.image.utils.ToolsHelper
import kotlinx.android.synthetic.main.forget_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ForgetPasswordActivity: BaseAAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        forget.setOnClickListener {
            val user_email = email.text.toString()
            if (TextUtils.isEmpty(user_email) || ToolsHelper.isEmailValid(user_email) != true) {
                Toast.makeText(this, "邮箱不能为空或者邮箱格式不正确!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "重置链接已发送,请注意查收!", Toast.LENGTH_SHORT).show()
//            Requester.AuthService().forgetPassword(email = user_email).enqueue(object: Callback<BasicResponse> {
//                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
//                    val c = response.body()!!
//                    if (c.code == 200) {
//                        finish()
//                    }
//                }
//
//                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
//                    Log.d("send_captcha", t.message)
//                    Toast.makeText(this@ForgetPasswordActivity, "发送失败,请稍后再试.", Toast.LENGTH_SHORT).show()
//                }
//            })
        }

        login.setOnClickListener { finish() }
    }

    override fun getLayoutId(): Int {
        return R.layout.forget_password
    }

}