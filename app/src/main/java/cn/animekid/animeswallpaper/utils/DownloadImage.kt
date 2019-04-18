package cn.animekid.animeswallpaper.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream
import java.util.*


class SaveImage (private val context: Context) {

    //Glide保存图片
    fun savePicture(url: String): String {
        val selfStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (selfStatus != PackageManager.PERMISSION_GRANTED) {
            return "没有权限,请打开设置开启权限"
        }
        val fileName = UUID.randomUUID().toString()

        val bitmap = Glide.with(context).asBitmap().load(url).submit().get()
        return writeFileToSD(fileName, bitmap)

    }

    //往SD卡写入文件的方法
    @Throws(Exception::class)
    fun writeFileToSD(filename: String, mBitmap: Bitmap): String {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            val filePath = Environment.getExternalStorageDirectory().getCanonicalPath() +
                    "/Pictures/" + filename + ".jpg"
            val file = File(filePath)
            if (!file.exists()) {
                file.parentFile.mkdirs()
            }
            val output = FileOutputStream(filePath)
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)

            output.flush()
            output.close()
            return filePath
        } else {
            return "SD卡不存在或者不可读写"
        }
    }

}

