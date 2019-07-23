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
import cn.animekid.animeswallpaper.data.BasicResponse
import cn.animekid.animeswallpaper.data.UserInfo
import cn.animekid.animeswallpaper.data.UserInfoData
import cn.animekid.animeswallpaper.utils.ToolsHelper
import cn.animekid.animeswallpaper.utils.database
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.profile.*
import org.jetbrains.anko.db.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity: BaseAAppCompatActivity() {

    private lateinit var _userinfo: UserInfoData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        line_sex.setOnClickListener {
            val sex_array = arrayOf("男","女")
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("提示").setSingleChoiceItems(sex_array, 0) { t_dialog, which ->
                Requester.AuthService().changeProfile(token = ToolsHelper.getToken(this@ProfileActivity), email = this._userinfo.email, name = "", sex = sex_array[which]).enqueue(object: Callback<BasicResponse> {
                    override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                        user_sex.text = sex_array[which]
                        this@ProfileActivity._userinfo.sex = sex_array[which]
                        this@ProfileActivity.database.use {
                            update("anime_users","sex" to sex_array[which]).whereArgs("userid="+this@ProfileActivity._userinfo.userid).exec()
                        }
                        t_dialog.dismiss()
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                        Log.d("Error",t.message)
                    }
                })
            }
            dialog.create().show()
        }

        line_name.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val newview = View.inflate(this, R.layout.change_profile_name, null)
            dialog.setTitle("提示").setView(newview)
            dialog.setPositiveButton("确认", DialogInterface.OnClickListener { t_dialog, which ->
                val newName = newview.findViewById<EditText>(R.id.new_name).text.toString()
                if (TextUtils.isEmpty(newName)) { return@OnClickListener }
                Requester.AuthService().changeProfile(token = ToolsHelper.getToken(this@ProfileActivity), email = this._userinfo.email, name = newName, sex = "").enqueue(object: Callback<BasicResponse> {
                    override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                        user_name.text = newName
                        this@ProfileActivity._userinfo.name = newName
                        this@ProfileActivity.database.use {
                            update("anime_users","name" to newName).whereArgs("userid="+this@ProfileActivity._userinfo.userid).exec()
                        }
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                        Log.d("logoutError",t.message)
                    }
                })

            })
            dialog.setNegativeButton("取消", null)
            dialog.create().show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (this.UserInfoList.count() > 0) {
            this._userinfo = this.UserInfoList.first()
            user_name.text = this._userinfo.name
            user_sex.text = this._userinfo.sex
            if (this._userinfo.avatar != "F") {
                Glide.with(this).load(this._userinfo.avatar).into(user_avatar)
            }
            Log.d("tag", _userinfo.toString())
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.profile
    }

}