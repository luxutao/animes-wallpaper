package me.m123.image.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import me.m123.image.R
import me.m123.image.api.Requester
import me.m123.image.data.BaseResponse
import me.m123.image.utils.ToolsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AboutActivity: BaseAAppCompatActivity() {

    private lateinit var CheckUpdate: LinearLayout
    private lateinit var HelpFeedback: LinearLayout
    private lateinit var SendMail: LinearLayout
    private lateinit var License: TextView
    private lateinit var NowVersion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.initUI()
        val packetInfo = this.packageManager.getPackageInfo(this.packageName, 0)
        this.NowVersion.text = packetInfo.versionName

        this.CheckUpdate.setOnClickListener {
            Requester.PublicService().checkUpdate(package_name = this.packageName, app_version = packetInfo.versionName, token = ToolsHelper.getToken(this@AboutActivity)).enqueue(object: Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    val result = response.body()!!
                    if (result.code == 200) {
                        Toast.makeText(this@AboutActivity, result.data.toString(), Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(this@AboutActivity, "已经是最新版本了哦", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Toast.makeText(this@AboutActivity, "连接服务器错误", Toast.LENGTH_SHORT).show()
                }
            })
        }

        this.HelpFeedback.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.setData(Uri.parse("https://www.github.com/luxutao/animes-wallpaper"))
            startActivity(Intent.createChooser(intent, "请选择浏览器"))
        }

        this.SendMail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // only email apps should handle this
                putExtra(Intent.EXTRA_EMAIL, arrayOf("luxutao@123m.me"))
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
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
        this.SendMail = this.findViewById(R.id.send_mail)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_about
    }
}