package cn.animekid.animeswallpaper.utils

import android.content.Context
import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.api.Requester
import cn.animekid.animeswallpaper.data.*
import cn.animekid.animeswallpaper.ui.LoginActivity
import com.bm.library.PhotoView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.db.parseList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ImageDialog(var mContext: Context) : AlertDialog(mContext) {

    fun showLoading(imagebean: ImageListData): AlertDialog {

        val dialog = AlertDialog.Builder(mContext).create()
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutInflater = LayoutInflater.from(mContext)
        val dialog_layout = layoutInflater.inflate(R.layout.image_dialog, null)
        val BigImageView = dialog_layout.findViewById<PhotoView>(R.id.big_image)
        val BigImageDownload = dialog_layout.findViewById<FloatingActionButton>(R.id.big_image_download)
        val BigImageClose = dialog_layout.findViewById<ImageButton>(R.id.big_image_close)
        BigImageView.enable()

        Glide.with(mContext)
                .load(imagebean.image_source)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_image_loading))
                .apply(RequestOptions.errorOf(R.drawable.ic_image_loading_error))
                .into(BigImageView)
        dialog.setView(dialog_layout)

        BigImageClose.setOnClickListener { dialog.dismiss() }

        BigImageDownload.setOnClickListener {
            val itemdata = mContext.database.use {
                select("anime_users","userid","token","name","create_time","email","sex","avatar").exec {
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