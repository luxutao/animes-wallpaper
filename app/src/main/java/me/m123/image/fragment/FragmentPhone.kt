package me.m123.image.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.m123.image.R

class FragmentPhone: BaseFFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_image_grid, container, false)
        this.album_id = 7
        this.initUI(view)
        this.loadingMore(10)
        return view
    }

    companion object {
        fun newInstance(): FragmentPhone = FragmentPhone()
    }

}