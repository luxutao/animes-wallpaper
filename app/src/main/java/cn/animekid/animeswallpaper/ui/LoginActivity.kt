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
import cn.animekid.animeswallpaper.data.BasicResponse
import cn.animekid.animeswallpaper.data.UserInfo
import cn.animekid.animeswallpaper.data.UserInfoData
import cn.animekid.animeswallpaper.utils.ToolsHelper
import cn.animekid.animeswallpaper.utils.database
import kotlinx.android.synthetic.main.login.*
import org.jetbrains.anko.db.RowParser
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class LoginActivity: BaseAAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            if (ToolsHelper.isEmailValid(user_email) != true) {
                Toast.makeText(this, "请输入一个正确的邮箱!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Requester.AuthService().authLogin(email = user_email, password = user_password).enqueue(object: Callback<BasicResponse> {
                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                    Log.d("userinfo", response.body()!!.toString())
                    val u = response.body()!!

                    if (u.code == 200) {
                        Requester.AuthService().getUserinfo(token = u.data).enqueue(object: Callback<UserInfo> {
                            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                                val userInfoData = response.body()!!
                                val userinfo = ContentValues()
                                userinfo.put("userid", userInfoData.data.userid)
                                userinfo.put("token", u.data)
                                userinfo.put("name", userInfoData.data.name)
                                userinfo.put("create_time", userInfoData.data.create_time)
                                userinfo.put("email", userInfoData.data.email)
                                userinfo.put("sex", userInfoData.data.sex)
                                userinfo.put("avatar", userInfoData.data.avatar)
                                this@LoginActivity.database.use {
                                    insert("anime_users","avatar",userinfo)
                                }
                                finish()
                            }

                            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                                Toast.makeText(this@LoginActivity, "验证失败了!", Toast.LENGTH_LONG).show()
                            }
                        })

                    } else {
                        Toast.makeText(this@LoginActivity, "该账号不存在！", Toast.LENGTH_SHORT).show()
                    }


                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
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

    override fun getLayoutId(): Int {
        return  R.layout.login
    }

}

