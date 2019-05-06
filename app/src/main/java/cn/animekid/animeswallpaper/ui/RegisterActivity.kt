package cn.animekid.animeswallpaper.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.api.Requester
import cn.animekid.animeswallpaper.data.ResponseDataBean
import cn.animekid.animeswallpaper.utils.ToolsHelper
import kotlinx.android.synthetic.main.register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity: AppCompatActivity() {

    private var _Captcha: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
        setSupportActionBar(this.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar))
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        send_captcha.setOnClickListener {
            val user_email = email.text.toString()
            if (TextUtils.isEmpty(user_email) || ToolsHelper.isEmailValid(user_email) != true) {
                Toast.makeText(this, "邮箱不能为空或者邮箱格式不正确!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "验证码已发送,请注意查收!", Toast.LENGTH_SHORT).show()
            Requester.apiService().sendCaptcha(email = user_email).enqueue(object: Callback<ResponseDataBean> {
                override fun onResponse(call: Call<ResponseDataBean>, response: Response<ResponseDataBean>) {
                    val c = response.body()!!
                    if (c.code == 200) {
                        _Captcha = c.data
                    }
                }

                override fun onFailure(call: Call<ResponseDataBean>, t: Throwable) {
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
            Requester.apiService().authRegister(email = user_email, password = user_password).enqueue(object: Callback<ResponseDataBean> {
                override fun onResponse(call: Call<ResponseDataBean>, response: Response<ResponseDataBean>) {
                    Log.d("userinfo", response.body()!!.toString())
                    val res = response.body()!!
                    if (res.code == 200) {
                        Toast.makeText(this@RegisterActivity, "注册成功啦!感谢你的支持,赶快去登录吧!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    if (res.code == 405) {
                        Toast.makeText(this@RegisterActivity, "该邮箱已被注册,何不尝试一下找回密码?", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseDataBean>, t: Throwable) {
                    Log.d("login_error", t.message)
                    Toast.makeText(this@RegisterActivity, "注册错误,请检查网络是否正常!", Toast.LENGTH_LONG).show()
                }
            })
        }

        login.setOnClickListener {
            finish()
        }

    }

    // 监听导航栏按钮
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
            }
        }
        return true
    }

}