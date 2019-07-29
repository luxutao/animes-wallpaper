package cn.animekid.animeswallpaper.ui

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.LinearLayout
import android.widget.Toast
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.utils.ClearCache

class SettingsActivity: BaseAAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val clear_cache: LinearLayout = this.findViewById(R.id.clear_cache)

        clear_cache.setOnClickListener {
            val cacheSize = ClearCache().getFileSizes(this.cacheDir).toDouble()
            val formatSize = ClearCache().getFormatSize(cacheSize)
            AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你确定要清除"+formatSize+"缓存吗？")
                .setPositiveButton("确认") { t_dialog, which ->
                    val clearr = ClearCache().clearCache(this.cacheDir.toString())
                    if (clearr) {
                        Toast.makeText(this, "清除成功", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("取消", null)
                .create().show()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_settings
    }
}