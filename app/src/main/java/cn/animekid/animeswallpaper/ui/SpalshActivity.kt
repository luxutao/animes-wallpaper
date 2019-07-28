package cn.animekid.animeswallpaper.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import cn.animekid.animeswallpaper.R
import kotlinx.coroutines.experimental.launch
import java.lang.Thread.sleep

class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)
        val mAnimation = AlphaAnimation(0.1f, 1.0f)
        val splash = this.findViewById<ImageView>(R.id.splash_anim)
        mAnimation.duration = 3000
        splash.animation = mAnimation
        mAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
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