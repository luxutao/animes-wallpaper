package cn.animekid.animeswallpaper.fragment

import android.content.Intent
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
import cn.animekid.animeswallpaper.data.DataParcelable
import cn.animekid.animeswallpaper.utils.ImageDialog

class FragmentPhone: BaseFFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_image_grid, container, false)
        this.wallTag = "phone"
        this.initUI(view)
        this.loadingMore(1)
        return view
    }

    companion object {
        fun newInstance(): FragmentPhone = FragmentPhone()
    }

}