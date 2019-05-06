package cn.animekid.animeswallpaper.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.adapter.ImageAdapter
import cn.animekid.animeswallpaper.api.Requester
import cn.animekid.animeswallpaper.data.DataParcelable
import cn.animekid.animeswallpaper.data.ImageDataBean
import cn.animekid.animeswallpaper.ui.DetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class BaseFFragment : Fragment() {

    var imageList = ArrayList<ImageDataBean.Data>()
    var adapter : ImageAdapter? = null
    var imageGrid : GridView? = null
    var errorview : LinearLayout? = null

    fun loadingMore(api: Call<ImageDataBean>, page: Int) {
        api.enqueue(object: Callback<ImageDataBean> {
            override fun onResponse(call: Call<ImageDataBean>?, response: Response<ImageDataBean>?) {
                this@BaseFFragment.imageGrid!!.visibility = View.VISIBLE
                this@BaseFFragment.errorview!!.visibility = View.GONE
                this@BaseFFragment.imageList.addAll(response!!.body()!!.data)
                this@BaseFFragment.adapter!!.notifyDataSetChanged()
            }
            override fun onFailure(call: Call<ImageDataBean>, t: Throwable) {
                Log.e("failed", t.message)
                if (page == 1) {
                    this@BaseFFragment.imageGrid!!.visibility = View.GONE
                    this@BaseFFragment.errorview!!.visibility = View.VISIBLE
                } else {
                    Toast.makeText(view!!.context, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}