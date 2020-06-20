package me.m123.image.ui

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import me.m123.image.R
import me.m123.image.api.Requester
import me.m123.image.utils.ToolsHelper
import kotlinx.android.synthetic.main.forget_password.*
import me.m123.image.data.BaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ForgetPasswordActivity: BaseAAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        forget.setOnClickListener {
            val input_username = username.text.toString()
            if (TextUtils.isEmpty(input_username)) {
                Toast.makeText(this, "用户名不能为空!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            username.isEnabled = false
            Requester.AuthService().forgetPassword(username = input_username).enqueue(object: Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    val c = response.body()!!
                    if (c.code == 200) {
                        Toast.makeText(this@ForgetPasswordActivity, "重置链接已发送,请注意查收!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@ForgetPasswordActivity, c.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Log.d("send_captcha", t.message)
                    Toast.makeText(this@ForgetPasswordActivity, "发送失败,请稍后再试.", Toast.LENGTH_SHORT).show()
                }
            })
        }

        login.setOnClickListener { finish() }
    }

    override fun getLayoutId(): Int {
        return R.layout.forget_password
    }

}