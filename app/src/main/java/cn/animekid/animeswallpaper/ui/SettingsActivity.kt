package cn.animekid.animeswallpaper.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.api.Requester
import cn.animekid.animeswallpaper.data.ResponseDataBean
import cn.animekid.animeswallpaper.data.UserInfoBean
import cn.animekid.animeswallpaper.utils.ClearCache
import cn.animekid.animeswallpaper.utils.ToolsHelper
import cn.animekid.animeswallpaper.utils.database
import kotlinx.android.synthetic.main.settings.*
import org.jetbrains.anko.db.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity: AppCompatActivity() {

    private var _userinfo:UserInfoBean.Data? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        setSupportActionBar(this.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar))
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        clear_cache.setOnClickListener {
            val cacheSize = ClearCache().getFileSizes(this.cacheDir).toDouble()
            val formatSize = ClearCache().getFormatSize(cacheSize)
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("提示")
            dialog.setMessage("你确定要清除"+formatSize+"缓存吗？")
            dialog.setPositiveButton("确认", DialogInterface.OnClickListener { dialog, which ->
                val clearr = ClearCache().clearCache(this.cacheDir.toString())
                if (clearr) {
                    Toast.makeText(this, "清除成功", Toast.LENGTH_SHORT).show()
                }
            })
            dialog.setNegativeButton("取消", null)
            dialog.create().show()
        }

        this.onResume()
        if (_userinfo != null) {
            delete_account.visibility = View.VISIBLE
        }
        delete_account.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("警告")
            dialog.setMessage("确认删除当前账号吗？")
            dialog.setPositiveButton("确认", DialogInterface.OnClickListener { dialog, which ->
                Requester.AuthService().delUser(token = ToolsHelper.getToken(this@SettingsActivity), authid = this._userinfo!!.userid).enqueue(object: Callback<ResponseDataBean> {
                    override fun onResponse(call: Call<ResponseDataBean>, response: Response<ResponseDataBean>) {
                        this@SettingsActivity.database.use {
                            delete("anime_users")
                        }
                        Log.d("logoutSuccess","success")
                        _userinfo = null
                        delete_account.visibility = View.GONE
                        Toast.makeText(this@SettingsActivity, "删除账号成功", Toast.LENGTH_SHORT).show()
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