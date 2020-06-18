package me.m123.image.api

import android.annotation.SuppressLint
import android.util.Log
import me.m123.image.data.*
import retrofit2.Call
import me.m123.image.utils.ToolsHelper
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.http.Headers
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

        fun UserService(): UserService {
            return getService(UserService.baseUrl, UserService::class.java)
        }

    }

}

interface AuthService {

    companion object {
        val baseUrl = "https://api.123m.me/api/auth/"
    }

    @FormUrlEncoded
    @POST("login/")
    fun authLogin(@Field("username") username: String, @Field("password") password: String): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register/")
    fun authRegister(@Field("email") email: String, @Field("username") username: String, @Field("password") password: String): Call<BaseResponse>

    @FormUrlEncoded
    @POST("captcha/")
    fun sendCaptcha(@Field("email") email: String, @Field("username") username: String): Call<BaseResponse>

    @FormUrlEncoded
    @POST("forgetpassword/")
    fun forgetPassword(@Field("username") username: String, @Field("new_password") new_password: String): Call<BaseResponse>

}

interface UserService {

    companion object {
        //此类接口的基地址
        val baseUrl = "https://api.123m.me/api/user/"
    }

    @Headers("Authorization: Token bfffb66dc6a15b57e7731c28d4efc328ab7f1e6c")
    @GET("{userid}/")
    fun getUserInfo(@Path("userid") userid: Int): Call<UserInfo>

    @Headers("Authorization: Token bfffb66dc6a15b57e7731c28d4efc328ab7f1e6c")
    @FormUrlEncoded
    @PUT("{userid}/")
    fun updateUserInfo(@Path("userid") userid: Int, @Body requestBody: RequestBody): Call<BaseResponse>

    @Headers("Authorization: Token bfffb66dc6a15b57e7731c28d4efc328ab7f1e6c")
    @FormUrlEncoded
    @POST("{userid}/resetpassword/")
    fun resetPassword(@Path("userid") userid: Int, @Field("old_password") old_password: String, @Field("new_password") new_password: String): Call<BaseResponse>
}

interface ImageService {

    companion object {
        //此类接口的基地址
        val baseUrl = "https://api.123m.me/api/"
    }

//    @Headers("")
    @GET("image/")
    fun getWallpaper(@Query("offset") offset: Int, @Query("album_id") album_id: Int, @Header("Authorization") token: String): Call<ImageList>

    @Headers("Authorization: Token bfffb66dc6a15b57e7731c28d4efc328ab7f1e6c")
    @Multipart
    @POST("image/")
    fun uploadImage( @Part file: MultipartBody.Part): Call<BaseResponse>
}

interface PublicService {

    companion object {
        val baseUrl = "https://api.123m.me/api/app/"
    }

    @Headers("Authorization: Token bfffb66dc6a15b57e7731c28d4efc328ab7f1e6c")
    @GET("notice/")
    fun getAnnouncement(@Query("package_name") package_name: String): Call<BaseResponse>

    @Headers("Authorization: Token bfffb66dc6a15b57e7731c28d4efc328ab7f1e6c")
    @GET("checkupdate/")
    fun checkUpdate(@Query("package_name") package_name: String, @Query("app_version") app_version: String): Call<BaseResponse>
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