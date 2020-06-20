package me.m123.image.ui

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import me.m123.image.R
import me.m123.image.api.Requester
import me.m123.image.utils.ToolsHelper
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_profile.*
import me.m123.image.data.BaseResponse
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity: BaseAAppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        line_sex.setOnClickListener {
            val sex_array = arrayOf("男","女")
            val dialog = AlertDialog.Builder(this)
            var json = JSONObject()
            dialog.setTitle("提示").setSingleChoiceItems(sex_array, sex_array.asList().indexOf(this.UserInfo.gender)) { t_dialog, which ->
                json.put("gender", sex_array[which])
                Requester.UserService().updateUserInfo(this.UserInfo.id, RequestBody.create(this.JSON, json.toString()), token = ToolsHelper.getToken(this@ProfileActivity)).enqueue(object: Callback<BaseResponse> {
                    override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                        user_sex.text = sex_array[which]
                        this@ProfileActivity.UserInfo.gender = sex_array[which]
                        this@ProfileActivity.updateData("gender", sex_array[which], this@ProfileActivity.UserInfo.id)
                        t_dialog.dismiss()
                    }

                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
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
                var json = JSONObject()
                json.put("last_name", newName)
                if (TextUtils.isEmpty(newName)) { return@OnClickListener }
                Requester.UserService().updateUserInfo(this.UserInfo.id, RequestBody.create(this.JSON, json.toString()), token = ToolsHelper.getToken(this@ProfileActivity)).enqueue(object: Callback<BaseResponse> {
                    override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                        user_name.text = newName
                        this@ProfileActivity.UserInfo.last_name = newName
                        this@ProfileActivity.updateData("last_name", newName, this@ProfileActivity.UserInfo.id)
                    }

                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
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
        if (this.UserInfo.id != 0) {
            user_name.text = this.UserInfo.last_name
            user_sex.text = this.UserInfo.gender
            Glide.with(this).load(this.UserInfo.avatar).into(user_avatar)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_profile
    }

}