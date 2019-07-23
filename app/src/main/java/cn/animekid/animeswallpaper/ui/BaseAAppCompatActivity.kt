package cn.animekid.animeswallpaper.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import cn.animekid.animeswallpaper.R
import cn.animekid.animeswallpaper.data.UserInfoData
import cn.animekid.animeswallpaper.utils.database
import org.jetbrains.anko.db.RowParser
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select

abstract class BaseAAppCompatActivity: AppCompatActivity() {

    var UserInfoList: List<UserInfoData> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.getData()
        setContentView(this.getLayoutId())
        setSupportActionBar(this.findViewById(R.id.toolbar))
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun getData(){
        val itemdata = this.database.use {
            select("anime_users","userid","token","name","create_time","email","sex","avatar").exec {
                val itemlist: List<UserInfoData> = parseList(classParser())
                return@exec itemlist
            }
        }
        this.UserInfoList = itemdata
    }

    abstract fun getLayoutId(): Int


    // 监听导航栏按钮
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
            }
        }
        return true
    }
}