package cn.animekid.animeswallpaper.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import cn.animekid.animeswallpaper.R
import kotlinx.android.synthetic.main.splash.*
import kotlinx.coroutines.experimental.launch
import java.lang.Thread.sleep

class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(R.layout.splash)
        val mAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_anime)
        splash_anim.startAnimation(mAnimation)
        mAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                //第一个动画执行完后执行第二个动画就是那个字体显示那部分
                val sAnimation = AnimationUtils.loadAnimation(this@SplashActivity, R.anim.splash_position)
                text_lin.startAnimation(sAnimation)
                val tAnimation = AnimationUtils.loadAnimation(this@SplashActivity, R.anim.text_canvas)
                text_hide_lin.startAnimation(tAnimation)
                launch {
                    sleep(1000)
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }
}