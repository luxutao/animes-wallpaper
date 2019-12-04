package cn.animekid.animeswallpaper.utils

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.api.Requester
import cn.animekid.animeswallpaper.data.BasicResponse
import cn.animekid.animeswallpaper.data.ImageListData
import cn.animekid.animeswallpaper.data.UserInfoData
import cn.animekid.animeswallpaper.ui.LoginActivity
import com.bm.library.PhotoView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.Transition
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

class ImageDialog(var mContext: Context) : AlertDialog(mContext) {

    private var isLoading: Boolean = false

    fun showLoading(imagebean: ImageListData): AlertDialog {

        val dialog = AlertDialog.Builder(mContext,R.style.Dialog_Fullscreen).create()
//        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window.decorView.setPadding(0,0,0,0)
        dialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        val layoutInflater = LayoutInflater.from(mContext)
        val dialog_layout = layoutInflater.inflate(R.layout.image_dialog, null)
        val BigImageView = dialog_layout.findViewById<PhotoView>(R.id.big_image)
        val BigImageDownload = dialog_layout.findViewById<FloatingActionButton>(R.id.big_image_download)
        val BigImageClose = dialog_layout.findViewById<ImageButton>(R.id.big_image_close)
        BigImageView.enable()
        if (imagebean.image_width < imagebean.image_height) {
            BigImageView.scaleType = ImageView.ScaleType.FIT_XY
        }

        Glide.with(mContext)
                .load(imagebean.image_source)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .apply(RequestOptions().placeholder(R.color.colorBlackT))
                .apply(RequestOptions().error(R.color.colorBlackT))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(object: DrawableImageViewTarget(BigImageView) {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        super.onResourceReady(resource, transition)
                        this@ImageDialog.isLoading = true
                    }
                })
        dialog.setView(dialog_layout)
        Handler().postDelayed({
            if (this@ImageDialog.isLoading == false) {
                Toast.makeText(mContext, "网络连接较慢，请耐心等待。", Toast.LENGTH_LONG).show()
            }
        }, 2000)

        BigImageClose.setOnClickListener { dialog.dismiss() }

        BigImageDownload.setOnClickListener {
            val itemdata = mContext.database.use {
                select("anime_users","userid","token","name","create_time","modify_time","email","sex","avatar").exec {
                    val itemlist: List<UserInfoData> = parseList(classParser<UserInfoData>())
                    return@exec itemlist
                }
            }
            if (itemdata.count() > 0) {
                Toast.makeText(mContext, "图片正在保存，请稍后~", Toast.LENGTH_SHORT).show()
                launch(UI) {
                    val job = async(CommonPool) {
                        SaveImage(mContext).savePicture(imagebean.image_source)
                    }
                    val text = job.await()
                    Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show()
                }

                Requester.ImageService().addLikes(token = ToolsHelper.getToken(mContext), addid = imagebean.image_id).enqueue(object : Callback<BasicResponse> {
                    override fun onResponse(call: Call<BasicResponse>?, response: Response<BasicResponse>?) {
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    }
                })
                dialog.dismiss()
            } else {
                val intent = Intent(mContext, LoginActivity::class.java)
                mContext.startActivity(intent)
            }
        }

        dialog.show()
        return dialog
    }

}