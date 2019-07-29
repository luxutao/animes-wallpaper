package cn.animekid.animeswallpaper.utils

import android.content.Context
import android.util.Base64
import cn.animekid.animeswallpaper.data.UserInfoData
import org.jetbrains.anko.db.*
import java.util.regex.Pattern

object ToolsHelper {


    /**
     * 检查是否是有效的邮箱
     *
     * @param email
     * @return boolean
     */
    fun isEmailValid(email: String): Boolean {
        var isValid = false

        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"

        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        if (matcher.matches()) {
            isValid = true
        }
        return isValid
    }

    /**
     * 获取ticket
     *
     * @param path
     * @return string
     */
    fun getTicket(path: String): String {
        val enstr = "LRnS4t"
        val timestamp = System.currentTimeMillis() / 1000
        val token = Base64.encodeToString((path+"-"+timestamp.toString()+"-"+enstr).toByteArray(), Base64.DEFAULT)
        return token
    }

    fun getToken(ctx: Context): String {
        val userinfo = ctx.database.use {
            select("anime_users","userid","token","name","create_time","email","sex","avatar").exec {
                val userinfo: UserInfoData = parseSingle(classParser())
                return@exec userinfo
            }
        }
        return userinfo.token
    }
}