package cn.animekid.animeswallpaper.fragment

import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import android.widget.*
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.adapter.ImageAdapter
import cn.animekid.animeswallpaper.api.Requester
import cn.animekid.animeswallpaper.data.ImageList
import cn.animekid.animeswallpaper.data.ImageListData
import cn.animekid.animeswallpaper.utils.ImageDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class BaseFFragment : Fragment() {

    var imageList: ArrayList<ImageListData> = arrayListOf()
    var wallTag: String = ""
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
            this.loadingMore(1)
        }

        this.imageGrid.setOnScrollListener(object: AbsListView.OnScrollListener{
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) { }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                val length : Int = imageList.size
                val page : Int = length / 10 + 1
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && length%10 == 0
                        && length-1 == view!!.lastVisiblePosition){
                    this@BaseFFragment.loadingMore(page)
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
                this.loadingMore(1)
                this.adapter.notifyDataSetChanged()
                this.swipere.isRefreshing = false
            }, 2000)
        }
    }

    fun loadingMore(page: Int) {
        Requester.ImageService().getWallpaper(page = page, tag = this.wallTag).enqueue(object: Callback<ImageList> {
            override fun onResponse(call: Call<ImageList>?, response: Response<ImageList>?) {
                this@BaseFFragment.imageGrid.visibility = View.VISIBLE
                this@BaseFFragment.errorview.visibility = View.GONE
                this@BaseFFragment.imageList.addAll(response!!.body()!!.data)
                this@BaseFFragment.adapter.notifyDataSetChanged()
            }
            override fun onFailure(call: Call<ImageList>, t: Throwable) {
                Log.e("failed", t.message)
                if (page == 1) {
                    this@BaseFFragment.imageGrid.visibility = View.GONE
                    this@BaseFFragment.errorview.visibility = View.VISIBLE
                } else {
                    Toast.makeText(view!!.context, "加载失败,请查看网络情况", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}