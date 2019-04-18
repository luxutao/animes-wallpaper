package cn.animekid.animeswallpaper.ui

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.api.Requester
import cn.animekid.animeswallpaper.data.ResponseDataBean
import cn.animekid.animeswallpaper.data.UserAuthBean
import cn.animekid.animeswallpaper.utils.ToolsHelper
import cn.animekid.animeswallpaper.utils.database
import kotlinx.android.synthetic.main.login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        setSupportActionBar(this.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar))
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        forgot_password.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener {
            val user_email = email.text.toString()
            val user_password = password.text.toString()
            if (TextUtils.isEmpty(user_email) || TextUtils.isEmpty(user_password)) {
                Toast.makeText(this, "邮箱或密码不能为空!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ToolsHelper().isEmailValid(user_email) != true) {
                Toast.makeText(this, "请输入一个正确的邮箱!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Requester.apiService().authLogin(email = user_email, password = user_password).enqueue(object: Callback<UserAuthBean> {
                override fun onResponse(call: Call<UserAuthBean>, response: Response<UserAuthBean>) {
                    Log.d("userinfo", response.body()!!.toString())
                    val u = response.body()!!
                    val userinfo = ContentValues()
                    userinfo.put("userid", u.data.userid)
                    userinfo.put("token", u.data.token)
                    userinfo.put("name", u.data.name)
                    userinfo.put("create_time", u.data.create_time)
                    userinfo.put("email", u.data.email)
                    userinfo.put("sex", u.data.sex)
                    userinfo.put("avatar", u.data.avatar)
                    this@LoginActivity.database.use {
                        insert("anime_users","avatar",userinfo)
                    }
                    finish()
                }

                override fun onFailure(call: Call<UserAuthBean>, t: Throwable) {
                    Log.d("login_error", t.message)
                    Toast.makeText(this@LoginActivity, "登录错误,请检查账号密码是否正确!", Toast.LENGTH_LONG).show()
                }
            })
        }

        register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
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

