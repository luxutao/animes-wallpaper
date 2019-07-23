package cn.animekid.animeswallpaper.ui

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.api.Requester
import cn.animekid.animeswallpaper.data.BasicResponse
import cn.animekid.animeswallpaper.data.UserInfoData
import cn.animekid.animeswallpaper.utils.ClearCache
import cn.animekid.animeswallpaper.utils.ToolsHelper
import cn.animekid.animeswallpaper.utils.database
import kotlinx.android.synthetic.main.settings.*
import org.jetbrains.anko.db.delete
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity: BaseAAppCompatActivity() {

    private lateinit var _userinfo: UserInfoData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        clear_cache.setOnClickListener {
            val cacheSize = ClearCache().getFileSizes(this.cacheDir).toDouble()
            val formatSize = ClearCache().getFormatSize(cacheSize)
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("提示")
            dialog.setMessage("你确定要清除"+formatSize+"缓存吗？")
            dialog.setPositiveButton("确认") { t_dialog, which ->
                val clearr = ClearCache().clearCache(this.cacheDir.toString())
                if (clearr) {
                    Toast.makeText(this, "清除成功", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.setNegativeButton("取消", null)
            dialog.create().show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (this.UserInfoList.count() > 0) {
            this._userinfo = this.UserInfoList.first()
            Log.d("tag", this._userinfo.toString())
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.settings
    }
}