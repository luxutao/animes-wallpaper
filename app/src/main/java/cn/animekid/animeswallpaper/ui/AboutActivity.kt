package cn.animekid.animeswallpaper.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.api.Requester
import cn.animekid.animeswallpaper.data.BasicResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AboutActivity: BaseAAppCompatActivity() {

    private lateinit var CheckUpdate: LinearLayout
    private lateinit var HelpFeedback: LinearLayout
    private lateinit var License: TextView
    private lateinit var NowVersion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.initUI()
        val packetInfo = this.packageManager.getPackageInfo(this.packageName, 0)
        this.NowVersion.text = packetInfo.versionName

        this.CheckUpdate.setOnClickListener {
            Requester.PublicService().checkUpdate(app_version = packetInfo.versionName).enqueue(object: Callback<BasicResponse> {
                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                    if (response.body()!!.data == "True") {
                        val dialog = AlertDialog.Builder(this@AboutActivity)
                        dialog.setTitle("提示")
                        dialog.setMessage("找到新版本了！请扫描分享二维码下载最新版本。")
                        dialog.setPositiveButton("确认", null)
                        dialog.setNegativeButton("取消", null)
                        dialog.create().show()
                    }
                    else {
                        Toast.makeText(this@AboutActivity, "已经是最新版本了哦", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Toast.makeText(this@AboutActivity, "连接服务器错误", Toast.LENGTH_SHORT).show()
                }
            })
        }

        this.HelpFeedback.setOnClickListener {
            val intent = Intent(this, FeedbackActivity::class.java)
            startActivity(intent)
        }

        this.License.setOnClickListener {
            val intent = Intent(this, LicenseActivity::class.java)
            startActivity(intent)
        }

    }

    fun initUI() {
        this.CheckUpdate = this.findViewById(R.id.check_update)
        this.License = this.findViewById(R.id.about_license)
        this.NowVersion = this.findViewById(R.id.now_version)
        this.HelpFeedback = this.findViewById(R.id.help_feedback)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_about
    }
}