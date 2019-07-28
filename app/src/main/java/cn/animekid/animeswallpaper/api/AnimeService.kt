package cn.animekid.animeswallpaper.api

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import cn.animekid.animeswallpaper.R
import retrofit2.Call
import cn.animekid.animeswallpaper.data.BasicResponse
import cn.animekid.animeswallpaper.data.ImageList
import cn.animekid.animeswallpaper.data.UserInfo
import cn.animekid.animeswallpaper.utils.ToolsHelper
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.text.SimpleDateFormat
import java.util.*


class Requester {

    companion object {

        private fun <T> getService(baseUrl: String, service: Class<T>): T {

            val clien = OkHttpClient.Builder()
                    //自定义拦截器用于日志输出
                    .addInterceptor(LogInterceptor())
                    .build()

            val retrofit = Retrofit.Builder().baseUrl(baseUrl)
                    //格式转换
                    .addConverterFactory(GsonConverterFactory.create())
                    //正常的retrofit返回的是call，此方法用于将call转化成Rxjava的Observable或其他类型
//                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(clien)
                    .build()
            return retrofit.create(service)
        }

        //可用于多种不同种类的请求
        fun ImageService(): ImageService {
            return getService(ImageService.baseUrl, ImageService::class.java)
        }

        fun AuthService(): AuthService {
            return getService(AuthService.baseUrl, AuthService::class.java)
        }

        fun PublicService(): PublicService {
            return getService(PublicService.baseUrl, PublicService::class.java)
        }

    }

}

interface AuthService {

    companion object {
        val baseUrl = "https://api.animekid.cn/api/auth/"
    }

    @FormUrlEncoded
    @POST("login")
    fun authLogin(@Query("ticket") ticket: String = ToolsHelper.getTicket("login"), @Field("email") email: String, @Field("password") password: String): Call<BasicResponse>

    @FormUrlEncoded
    @POST("register")
    fun authRegister(@Query("ticket") ticket: String = ToolsHelper.getTicket("register"), @Field("email") email: String, @Field("password") password: String): Call<BasicResponse>

    @FormUrlEncoded
    @POST("sendCaptcha")
    fun sendCaptcha(@Query("ticket") ticket: String = ToolsHelper.getTicket("sendCaptcha"), @Field("email") email: String): Call<BasicResponse>

    @FormUrlEncoded
    @POST("logout")
    fun authLogout(@Query("token") token: String, @Field("authtoken") authtoken: String): Call<BasicResponse>

    @FormUrlEncoded
    @POST("forgetpassword")
    fun forgetPassword(@Query("ticket") ticket: String = ToolsHelper.getTicket("forgetpassword"), @Field("email") email: String): Call<BasicResponse>

    @FormUrlEncoded
    @POST("deluser")
    fun delUser(@Query("token") token: String, @Field("authid") authid: Int): Call<BasicResponse>

    @FormUrlEncoded
    @POST("changeuser")
    fun changeProfile(@Query("token") token: String, @Field("email") email: String, @Field("name") name: String,@Field("sex") sex: String): Call<BasicResponse>

    @GET("getUserinfo")
    fun getUserinfo(@Query("token") token: String): Call<UserInfo>

}


interface ImageService {

    companion object {
        //此类接口的基地址
        val baseUrl = "https://api.animekid.cn/api/animewallpaper/"
    }

    @GET("getWallpaper")
    fun getWallpaper(@Query("ticket") ticket: String = ToolsHelper.getTicket("getWallpaper"), @Query("page") page: Int, @Query("tag") tag: String): Call<ImageList>

    @FormUrlEncoded
    @POST("addCount")
    fun addLikes(@Query("token") token: String, @Field("addid") addid: Int): Call<BasicResponse>

    @Multipart
    @POST("uploadImage")
    fun uploadImage(@Query("token") token: String, @Part file: MultipartBody.Part): Call<BasicResponse>
}

interface PublicService {

    companion object {
        val baseUrl = "https://api.animekid.cn/api/public/"
    }

    @GET("checkUpdate")
    fun checkUpdate(@Query("ticket") ticket: String = ToolsHelper.getTicket("checkUpdate"), @Query("app_name") app_name: String = "ANIMEWALLPAPER", @Query("app_version") app_version: String): Call<BasicResponse>

    @FormUrlEncoded
    @POST("feedback")
    fun feedback(@Query("ticket") ticket: String = ToolsHelper.getTicket("feedback"),@Field("app_name") app_name: String = "ANIMEWALLPAPER",  @Field("email") email: String, @Field("content") content: String): Call<BasicResponse>
}


class LogInterceptor : Interceptor {

    private val tag = "Retrofit"
    @SuppressLint("SimpleDateFormat")
    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        Log.i(tag, format.format(Date()) + " Requeste " + "\nmethod:" + request.method() + "\nurl:" + request.url() + "\nbody:" + request.body().toString())

        val response = chain.proceed(request)

        //response.peekBody不会关闭流
        Log.i(tag, format.format(Date()) + " Response " + "\nsuccessful:" + response.isSuccessful + "\nbody:" + response.peekBody(1024).toString())

        return response
    }

}