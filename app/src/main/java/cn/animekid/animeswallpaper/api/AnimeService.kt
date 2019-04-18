package cn.animekid.animeswallpaper.api

import android.annotation.SuppressLint
import android.util.Log
import retrofit2.Call
import cn.animekid.animeswallpaper.data.ImageDataBean
import cn.animekid.animeswallpaper.data.ResponseDataBean
import cn.animekid.animeswallpaper.data.UserAuthBean
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
        fun apiService(): AnimeService {
            return getService(AnimeService.baseUrl, AnimeService::class.java)
        }

    }

}


interface AnimeService {

    companion object {
        //此类接口的基地址
        val baseUrl = "https://api.animekid.cn/api/animewallpaper/"
    }

    @GET("getAnimepc")
    fun getAnimepc(@Query("token") token: String = ToolsHelper().getToken("getAnimepc"), @Query("page") page: Int): Call<ImageDataBean>

    @GET("getAnimephone")
    fun getAnimephone(@Query("token") token: String = ToolsHelper().getToken("getAnimephone"), @Query("page") page: Int): Call<ImageDataBean>

    @GET("getBing")
    fun getBing(@Query("token") token: String = ToolsHelper().getToken("getBing"), @Query("page") page: Int): Call<ImageDataBean>

    @FormUrlEncoded
    @POST("addCount")
    fun addLikes(@Query("token") token: String = ToolsHelper().getToken("addCount"), @Field("addid") addid: Int): Call<ResponseDataBean>

    @FormUrlEncoded
    @POST("login")
    fun authLogin(@Query("token") token: String = ToolsHelper().getToken("login"), @Field("email") email: String, @Field("password") password: String): Call<UserAuthBean>

    @FormUrlEncoded
    @POST("register")
    fun authRegister(@Query("token") token: String = ToolsHelper().getToken("register"), @Field("email") email: String, @Field("password") password: String): Call<ResponseDataBean>

    @FormUrlEncoded
    @POST("sendCaptcha")
    fun sendCaptcha(@Query("token") token: String = ToolsHelper().getToken("sendCaptcha"), @Field("email") email: String): Call<ResponseDataBean>

    @FormUrlEncoded
    @POST("logout")
    fun authLogout(@Query("token") token: String = ToolsHelper().getToken("logout"), @Field("authtoken") authtoken: String): Call<ResponseDataBean>

    @FormUrlEncoded
    @POST("forgetpassword")
    fun forgetPassword(@Query("token") token: String = ToolsHelper().getToken("forgetpassword"), @Field("email") email: String): Call<ResponseDataBean>

    @FormUrlEncoded
    @POST("deluser")
    fun delUser(@Query("token") token: String = ToolsHelper().getToken("deluser"), @Field("authid") authid: Int): Call<ResponseDataBean>

    @FormUrlEncoded
    @POST("changeuser")
    fun changeProfile(@Query("token") token: String = ToolsHelper().getToken("changeuser"), @Field("email") email: String, @Field("name") name: String,@Field("sex") sex: String): Call<ResponseDataBean>

    @Multipart
    @POST("uploadImage")
    fun uploadImage(@Query("token") token: String = ToolsHelper().getToken("uploadImage"), @Part file: MultipartBody.Part): Call<ResponseDataBean>
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