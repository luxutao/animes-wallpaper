package cn.animekid.animeswallpaper.ui

import android.content.Intent
import android.graphics.Matrix
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.api.Requester
import cn.animekid.animeswallpaper.data.DataParcelable
import cn.animekid.animeswallpaper.data.ResponseDataBean
import cn.animekid.animeswallpaper.data.UserInfoBean
import cn.animekid.animeswallpaper.utils.SaveImage
import cn.animekid.animeswallpaper.utils.ToolsHelper
import cn.animekid.animeswallpaper.utils.database
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_detail)
        setSupportActionBar(this.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar))
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val imageSource = this.findViewById<ImageView>(R.id.imageSource)
        val imagebean = intent.extras.getParcelable<DataParcelable>("imagebean")
        this.findViewById<TextView>(R.id.image_date).text = imagebean.image_date.toString()
        this.findViewById<TextView>(R.id.image_id).text = imagebean.image_id.toString()
        this.findViewById<TextView>(R.id.image_date_gmt).text = imagebean.image_date_gmt.toString()
        this.findViewById<TextView>(R.id.image_extension).text = imagebean.image_extension.toString()
        this.findViewById<TextView>(R.id.image_height).text = imagebean.image_height.toString()
        this.findViewById<TextView>(R.id.image_likes).text = imagebean.image_likes.toString()
        this.findViewById<TextView>(R.id.image_name).text = imagebean.image_name.toString()
        this.findViewById<TextView>(R.id.image_size).text = imagebean.image_size.toString()
        this.findViewById<TextView>(R.id.image_width).text = imagebean.image_width.toString()
        Glide.with(this)
                .load(imagebean.image_source)
                .apply(bitmapTransform(BlurTransformation(10, 1)))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_image_loading))
                .apply(RequestOptions.errorOf(R.drawable.ic_image_loading_error))
                .into(imageSource)

        // 判断是横屏或者竖屏,动态调整imageview
        val code = intent.extras.getString("type", "0")
        if (code == "1") {
            val params = imageSource.getLayoutParams()
            params.height = 1200
            imageSource.setLayoutParams(params)
        }

        // 设置图片的旋转
        imageSource.setScaleType(ImageView.ScaleType.MATRIX)  //设置为矩阵模式
        val matrix: Matrix = Matrix()           //创建一个单位矩阵
        matrix.setTranslate(-50F, -50F)          //平移x和y各100单位
        matrix.preRotate(15F)                   //顺时针旋转30度
        imageSource.setImageMatrix(matrix)       //设置并应用矩阵

        // 下载图片按钮
        this.findViewById<FloatingActionButton>(R.id.imageDownload).setOnClickListener {
            val itemdata = this.database.use {
                select("anime_users","userid","token","name","create_time","email","sex","avatar").exec {
                    val itemlist: List<UserInfoBean.Data> = parseList(classParser<UserInfoBean.Data>())
                    return@exec itemlist
                }
            }
            if (itemdata.count() > 0) {
                Toast.makeText(this, "图片正在保存，请稍后~", Toast.LENGTH_SHORT).show()
                launch(UI) {
                    val job = async(CommonPool) {
                        SaveImage(this@DetailActivity).savePicture(imagebean.image_source)
                    }
                    val text = job.await()
                    Toast.makeText(this@DetailActivity, text, Toast.LENGTH_SHORT).show()
                }

                Requester.apiService().addLikes(token = ToolsHelper.getToken(this@DetailActivity), addid = imagebean.image_id).enqueue(object : Callback<ResponseDataBean> {
                    override fun onResponse(call: Call<ResponseDataBean>?, response: Response<ResponseDataBean>?) {
                    }

                    override fun onFailure(call: Call<ResponseDataBean>, t: Throwable) {
                    }
                })
            } else {
                Toast.makeText(this, "正在跳转到登录", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
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

