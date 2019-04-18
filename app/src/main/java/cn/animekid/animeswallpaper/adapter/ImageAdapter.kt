package cn.animekid.animeswallpaper.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.data.ImageDataBean
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import java.util.*


class ImageAdapter(private val _context: Context, private val _list: ArrayList<ImageDataBean.Data>) : BaseAdapter() {

    override fun getCount(): Int {
        return _list.size
    }

    override fun getItem(position: Int): Any {
        return _list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val holder: ImageViewHolder
        val v: View
        if (convertView == null) {
            v = View.inflate(_context, R.layout.image_item, null)
            holder = ImageViewHolder(v)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as ImageViewHolder
        }

        Glide.with(v)
                .load(_list[position].image_thumb)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_image_loading))
                .apply(RequestOptions.errorOf(R.drawable.ic_image_loading_error))
                .transition(withCrossFade())
                .into(holder.image)
        holder.count.setText(String.format(_context.getString(R.string.image_likes),  _list[position].image_likes))
        return v
    }

    class ImageViewHolder(var viewItem: View) {
        var image: ImageView = viewItem.findViewById(R.id.imageItem) as ImageView
        var count: TextView = viewItem.findViewById(R.id.downloadCount) as TextView

    }
}