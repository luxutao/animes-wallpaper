package me.m123.image.ui

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import me.m123.image.R
import me.m123.image.api.Requester
import me.m123.image.utils.ToolsHelper
import kotlinx.android.synthetic.main.register.*
import me.m123.image.data.BaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity: BaseAAppCompatActivity() {

    private var _Captcha: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        send_captcha.setOnClickListener {
            val user_email = email.text.toString()
            if (TextUtils.isEmpty(user_email) || ToolsHelper.isEmailValid(user_email) != true) {
                Toast.makeText(this, "邮箱不能为空或者邮箱格式不正确!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "验证码已发送,请注意查收!", Toast.LENGTH_SHORT).show()
            Requester.AuthService().sendCaptcha(email = user_email, username = "").enqueue(object: Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    val c = response.body()!!
                    _Captcha = c.result
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Log.d("send_captcha", t.message)
                    Toast.makeText(this@RegisterActivity, "验证码发送失败,请稍后再试.", Toast.LENGTH_SHORT).show()
                }
            })
        }

        register.setOnClickListener {
            val user_email = email.text.toString()
            val user_captcha = captcha.text.toString()
            val user_password = password.text.toString()
            if (TextUtils.isEmpty(user_email) || TextUtils.isEmpty(user_password) || TextUtils.isEmpty(user_captcha)) {
                Toast.makeText(this, "邮箱,密码,验证码不能为空!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (user_captcha != _Captcha) {
                Toast.makeText(this, "验证码不正确!请登录邮箱查看正确的验证码!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ToolsHelper.isEmailValid(user_email) != true) {
                Toast.makeText(this, "请输入一个正确的邮箱!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Requester.AuthService().authRegister(email = user_email, password = user_password, username = "").enqueue(object: Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    Log.d("userinfo", response.body()!!.toString())
                    val res = response.body()!!
                    if (res.result == "创建成功") {
                        Toast.makeText(this@RegisterActivity, "注册成功啦!感谢你的支持,赶快去登录吧!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    Toast.makeText(this@RegisterActivity, res.result, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Log.d("login_error", t.message)
                    Toast.makeText(this@RegisterActivity, "注册错误,请检查网络是否正常!", Toast.LENGTH_LONG).show()
                }
            })
        }

        login.setOnClickListener {
            finish()
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.register
    }
}