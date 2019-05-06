package cn.animekid.animeswallpaper.utils

import android.content.Context
import android.util.Base64
import cn.animekid.animeswallpaper.data.UserInfoBean
import org.jetbrains.anko.db.*
import java.util.regex.Pattern

object ToolsHelper {


    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
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

    fun getTicket(path: String): String {
        val enstr = "LRnS4t"
        val timestamp = System.currentTimeMillis() / 1000
        val token = Base64.encodeToString((path+"-"+timestamp.toString()+"-"+enstr).toByteArray(), Base64.DEFAULT)
        return token
    }

    fun getToken(ctx: Context): String {
        val userinfo = ctx.database.use {
            select("token").exec {
                val userinfo: UserInfoBean.Data = parseSingle(classParser<UserInfoBean.Data>())
                return@exec userinfo
            }
        }
        return userinfo.token
    }
}