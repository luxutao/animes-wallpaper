package me.m123.image.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import me.m123.image.R
import me.m123.image.data.UserInfoToken
import me.m123.image.utils.database
import org.jetbrains.anko.db.*
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import okhttp3.MediaType


abstract class BaseAAppCompatActivity: AppCompatActivity() {

    val JSON = MediaType.parse("application/json; charset=utf-8")
    var UserInfo: UserInfoToken = UserInfoToken(0,"","","","","","", "", "","")

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
            select("anime_users","userid","token","username","last_name","first_name","email","gender","avatar","date_joined","last_login").exec {
                val itemlist: List<UserInfoToken> = parseList(classParser())
                return@exec itemlist
            }
        }
        if (itemdata.count() >= 1) {
            this.UserInfo = itemdata.first()
        }
    }

    fun updateData(column: String, value: Any, userid: Int) {
        this.database.use {
            update("anime_users",column to value).whereArgs("userid=" + userid).exec()
        }
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