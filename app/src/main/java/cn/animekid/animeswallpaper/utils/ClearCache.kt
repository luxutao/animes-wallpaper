package cn.animekid.animeswallpaper.utils

import java.io.File
import java.io.FileInputStream
import java.math.BigDecimal

class ClearCache {


    /**
     * 获取指定文件夹的大小
     *
     * @param f
     * @return
     * @throws Exception
     */
    public fun getFileSizes(f: File): Long {
        var size: Long = 0
        val flist = f.listFiles() //文件夹目录下的所有文件
        if (flist == null) {    //4.2的模拟器空指针。
            return 0
        }else {
            for (i in flist) {
                if (i.isDirectory()) {    //判断是否父目录下还有子目录
                    size = size + getFileSizes(i)
                } else {
                    size = size + getFileSize(i)
                }
            }
        }

        return size
    }


    /**
     * 获取指定文件的大小
     *
     * @return
     * @throws Exception
     */
    private fun getFileSize(file: File): Int {

        var size: Int = 0
        if (file.exists()) {
            val fis = FileInputStream(file)
            size = fis.available()
            fis.close()
        }
        return size
    }

    /**
     * 格式化单位
     * @param size
     */
    fun getFormatSize(size: Double): String {
        val megaByte: Double = size / 1024 / 1024
        val result2: BigDecimal = BigDecimal(megaByte.toString())
        return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "MB"
    }

    /**
     * 删除目录下文件
     *
     * @param path
     */
    fun clearCache(path: String): Boolean {
        var flag: Boolean = false
        val file: File = File(path)
        if (!file.exists()) {
            return flag
        }
        if (!file.isDirectory) {
            return flag
        }
        val tempList = file.listFiles()
        for (i in tempList) {
            if (i.isFile) {
                i.delete()
            }
            if (i.isDirectory()) {
                clearCache(i.absolutePath)
                flag = true
            }
        }
        return flag
    }
}