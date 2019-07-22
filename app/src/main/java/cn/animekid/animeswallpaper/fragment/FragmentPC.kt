package cn.animekid.animeswallpaper.fragment

import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.adapter.ImageAdapter
import cn.animekid.animeswallpaper.api.Requester
import cn.animekid.animeswallpaper.utils.ImageDialog

class FragmentPC: BaseFFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pc, container, false)
        this.adapter = ImageAdapter(view.context, this.imageList)
        this.imageGrid = view.findViewById(R.id.imagePCList)
        this.errorview = view.findViewById(R.id.noInternet)
        val reloadImageList = view.findViewById<Button>(R.id.reloadImageList)
        val swiperereshlayout = view.findViewById<SwipeRefreshLayout>(R.id.swiperereshlayout)
        this.imageGrid!!.adapter = adapter
        this.loadingMore(Requester.ImageService().getAnimepc(page = 1), 1)

        reloadImageList.setOnClickListener {
            this.loadingMore(Requester.ImageService().getAnimepc(page = 1), 1)
        }

        this.imageGrid!!.setOnScrollListener(object: AbsListView.OnScrollListener{
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) { }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                val length : Int = imageList.size
                val page : Int = length / 10 + 1
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && length%10 == 0
                        && length-1 == view!!.lastVisiblePosition){
                    this@FragmentPC.loadingMore(Requester.ImageService().getAnimepc(page = page), page)
                }
            }
        })
        this.imageGrid!!.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val index = parent.getItemIdAtPosition(position)
            val bean = imageList.get(index.toInt())
            ImageDialog(view.context).showLoading(bean)
        }

        swiperereshlayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light)

        swiperereshlayout.setOnRefreshListener {
            //设置2秒的时间来执行以下事件
            Handler().postDelayed(Runnable {
                this.imageList.clear()
                this.loadingMore(Requester.ImageService().getAnimepc(page = 1), 1)
                this.adapter?.notifyDataSetChanged()
                swiperereshlayout.isRefreshing = false
            }, 2000)
        }

        return view

    }
    companion object {
        fun newInstance(): FragmentPC = FragmentPC()
    }

}