package cn.animekid.animeswallpaper.ui

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.api.Requester
import cn.animekid.animeswallpaper.data.ResponseDataBean
import cn.animekid.animeswallpaper.data.UserInfoBean
import cn.animekid.animeswallpaper.utils.ToolsHelper
import cn.animekid.animeswallpaper.utils.database
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.profile.*
import org.jetbrains.anko.db.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity: AppCompatActivity() {

    private var _userinfo:UserInfoBean.Data? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        setSupportActionBar(this.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar))
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        line_sex.setOnClickListener {
            val sexarry = arrayOf("男","女")
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("提示").setSingleChoiceItems(sexarry, 0, DialogInterface.OnClickListener { dialog, which ->
                Log.d("tag", sexarry[which])
                Requester.apiService().changeProfile(token = ToolsHelper.getToken(this@ProfileActivity), email = this._userinfo!!.email, name = "", sex = sexarry[which]).enqueue(object: Callback<ResponseDataBean> {
                    override fun onResponse(call: Call<ResponseDataBean>, response: Response<ResponseDataBean>) {
                        user_sex.text = sexarry[which]
                        this@ProfileActivity._userinfo!!.sex = sexarry[which]
                        this@ProfileActivity.database.use {
                            update("anime_users","sex" to sexarry[which]).whereArgs("userid="+this@ProfileActivity._userinfo!!.userid).exec()
                        }
                        dialog.dismiss()
                    }

                    override fun onFailure(call: Call<ResponseDataBean>, t: Throwable) {
                        Log.d("logoutError",t.message)
                    }
                })
            })
            dialog.create().show()
        }

        line_name.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val newview = View.inflate(this, R.layout.change_profile_name, null)
            dialog.setTitle("提示").setView(newview)
            dialog.setPositiveButton("确认", DialogInterface.OnClickListener { dialog, which ->
                val newName = newview.findViewById<EditText>(R.id.new_name).text.toString()
                if (TextUtils.isEmpty(newName)) { return@OnClickListener }
                Requester.apiService().changeProfile(token = ToolsHelper.getToken(this@ProfileActivity), email = this._userinfo!!.email, name = newName, sex = "").enqueue(object: Callback<ResponseDataBean> {
                    override fun onResponse(call: Call<ResponseDataBean>, response: Response<ResponseDataBean>) {
                        user_name.text = newName
                        this@ProfileActivity._userinfo!!.name = newName
                        this@ProfileActivity.database.use {
                            update("anime_users","name" to newName).whereArgs("userid="+this@ProfileActivity._userinfo!!.userid).exec()
                        }
                    }

                    override fun onFailure(call: Call<ResponseDataBean>, t: Throwable) {
                        Log.d("logoutError",t.message)
                    }
                })

            })
            dialog.setNegativeButton("取消", null)
            dialog.create().show()
        }
    }

    override fun onResume() {
        super.onResume()
        val userinfo = this.getData(classParser<UserInfoBean.Data>())
        if (userinfo.count() > 0) {
            _userinfo = userinfo.first()
            user_name.text = this._userinfo!!.name
            user_sex.text = this._userinfo!!.sex
            if (this._userinfo!!.avatar != "F") {
                Glide.with(this).load(this._userinfo!!.avatar).into(user_avatar)
            }
            Log.d("tag", _userinfo.toString())
        }
    }

    private fun getData(parser: RowParser<UserInfoBean.Data>): List<UserInfoBean.Data>{
        val itemdata = this.database.use {
            select("anime_users","userid","token","name","create_time","email","sex","avatar").exec {
                val itemlist: List<UserInfoBean.Data> = parseList(parser)
                return@exec itemlist
            }
        }
        return itemdata
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