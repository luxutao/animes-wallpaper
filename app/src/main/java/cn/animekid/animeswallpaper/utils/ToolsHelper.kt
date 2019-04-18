package cn.animekid.animeswallpaper.utils

import android.util.Base64
import java.util.regex.Pattern

class ToolsHelper {


    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public fun isEmailValid(email: String): Boolean {
        var isValid = false

        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"

        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        if (matcher.matches()) {
            isValid = true
        }
        return isValid
    }

    public fun getToken(path: String): String {
        val enstr = "LRnS4t"
        val timestamp = System.currentTimeMillis() / 1000
        val token = Base64.encodeToString((path+"-"+timestamp.toString()+"-"+enstr).toByteArray(), Base64.DEFAULT)
        return token
    }
}