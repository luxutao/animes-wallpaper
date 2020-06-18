package me.m123.image.fragment

import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import android.widget.*
import me.m123.image.R
import me.m123.image.adapter.ImageAdapter
import me.m123.image.api.Requester
import me.m123.image.data.ImageList
import me.m123.image.data.ImageListData
import me.m123.image.utils.ImageDialog
import me.m123.image.utils.ToolsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class BaseFFragment : Fragment() {

    var imageList: ArrayList<ImageListData> = arrayListOf()
    var album_id: Int = 0
    lateinit var adapter: ImageAdapter
    lateinit var imageGrid: GridView
    lateinit var errorview: LinearLayout
    lateinit var reloadImageList: Button
    lateinit var swipere: SwipeRefreshLayout

    fun initUI(view: View) {
        this.imageGrid = view.findViewById(R.id.image_list)
        this.errorview = view.findViewById(R.id.not_data)
        this.reloadImageList = view.findViewById(R.id.reload_image_list)
        this.swipere = view.findViewById(R.id.refresh)
        this.adapter = ImageAdapter(view.context, this.imageList)
        this.imageGrid.adapter = this.adapter

        reloadImageList.setOnClickListener {
            this.loadingMore(10)
        }

        this.imageGrid.setOnScrollListener(object: AbsListView.OnScrollListener{
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) { }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                val length : Int = imageList.size
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && length%10 == 0
                        && length-1 == view!!.lastVisiblePosition){
                    this@BaseFFragment.loadingMore(length + 10)
                }
            }
        })
        this.imageGrid.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val index = parent.getItemIdAtPosition(position)
            val bean = imageList.get(index.toInt())
            ImageDialog(view.context).showLoading(bean)
        }

        this.swipere.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light)

        this.swipere.setOnRefreshListener {
            //设置2秒的时间来执行以下事件
            Handler().postDelayed(Runnable {
                this.imageList.clear()
                this.loadingMore(10)
                this.adapter.notifyDataSetChanged()
                this.swipere.isRefreshing = false
            }, 2000)
        }
    }

    fun loadingMore(offset: Int) {
        Requester.ImageService().getWallpaper(offset = offset, album_id = this.album_id, token = ToolsHelper.getToken(this@BaseFFragment.context!!)).enqueue(object: Callback<ImageList> {
            override fun onResponse(call: Call<ImageList>?, response: Response<ImageList>?) {
                this@BaseFFragment.imageGrid.visibility = View.VISIBLE
                this@BaseFFragment.errorview.visibility = View.GONE
                this@BaseFFragment.imageList.addAll(response!!.body()!!.results)
                this@BaseFFragment.adapter.notifyDataSetChanged()
            }
            override fun onFailure(call: Call<ImageList>, t: Throwable) {
                Log.e("failed", t.message)
                if (offset == 10) {
                    this@BaseFFragment.imageGrid.visibility = View.GONE
                    this@BaseFFragment.errorview.visibility = View.VISIBLE
                } else {
                    Toast.makeText(view!!.context, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}