package cn.animekid.animeswallpaper.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

/**
 * Created by Administrator on 2016/3/17.
 * 实现跑马灯效果的TextView
 */
class MarqueeText : TextView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    //返回textview是否处在选中的状态
    //而只有选中的textview才能够实现跑马灯效果
    override fun isFocused(): Boolean {
        return true
    }
}