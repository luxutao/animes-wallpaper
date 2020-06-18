package me.m123.image.ui

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import me.m123.image.R
import me.m123.image.api.Requester
import me.m123.image.data.UserInfo
import me.m123.image.utils.ToolsHelper
import me.m123.image.utils.database
import kotlinx.android.synthetic.main.login.*
import me.m123.image.data.BaseResponse
import me.m123.image.data.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            Requester.AuthService().authLogin(username = user_email, password = user_password).enqueue(object: Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    Log.d("userinfo", response.body()!!.toString())
                    val u = response.body()!!

                    if (u.token.isNotEmpty()) {
                        Requester.UserService().getUserInfo(u.userid).enqueue(object: Callback<UserInfo> {
                            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                                val userInfoData = response.body()!!
                                val userinfo = ContentValues()
                                userinfo.put("userid", userInfoData.id)
                                userinfo.put("token", u.token)
                                userinfo.put("email", userInfoData.email)
                                userinfo.put("username", userInfoData.username)
                                userinfo.put("first_name", userInfoData.first_name)
                                userinfo.put("last_name", userInfoData.last_name)
                                userinfo.put("last_login", userInfoData.last_login)
                                userinfo.put("date_joined", userInfoData.date_joined)
                                userinfo.put("gender", userInfoData.gender)
                                userinfo.put("avatar", userInfoData.avatar)
                                this@LoginActivity.database.use {
                                    insert("anime_users","avatar",userinfo)
                                }
                                val intent = Intent()
                                intent.putExtra("result", "true")
                                this@LoginActivity.setResult(1, intent)
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

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
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

