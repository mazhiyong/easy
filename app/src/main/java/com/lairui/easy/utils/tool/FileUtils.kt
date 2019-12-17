package com.lairui.easy.utils.tool

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log

import com.lairui.easy.basic.BasicApplication
import com.lairui.easy.basic.MbsConstans

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {

    var SDPATH = MbsConstans.PHOTO_PATH

    fun saveBitmap(bm: Bitmap, picName: String): String {
        var filePath = ""
        try {
            if (!isFileExist("")) {
                val tempf = createSDDir("")
            }
            filePath = SDPATH + File.separator + picName + ".png"
            val f = File(SDPATH, "$picName.png")
            if (f.exists()) {
                f.delete()
            }
            val out = FileOutputStream(f)
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return filePath
    }

    @Throws(IOException::class)
    fun createSDDir(dirName: String): File {
        val dir = File(SDPATH + dirName)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {

            LogUtil.i("show", "createSDDir:" + dir.absolutePath)
            LogUtil.i("show", "createSDDir:" + dir.mkdir())
        }
        return dir
    }

    fun isFileExist(fileName: String): Boolean {
        val file = File(SDPATH + fileName)
        file.isFile
        return file.exists()
    }

    fun delFile(fileName: String) {
        val file = File(SDPATH + fileName)
        if (file.isFile) {
            file.delete()
        }
        file.exists()
    }

    fun deleteDir(path: String) {

        try {
            val dir = File(path)
            if (dir == null || !dir.exists() || !dir.isDirectory)
                return

            for (file in dir.listFiles()) {
                if (file.isFile) {
                    file.delete()
                    //				updateFileFromdatabase(mContext,file);
                    updateMedia(file.absolutePath)
                } else if (file.isDirectory) {
                    deleteDir(path)
                }
            }
            dir.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.i("FileUtils删除缓存文件异常$path", "没有给予权限捕获异常防止崩溃，不影响其他正常操作")
        }

    }


    fun updateMedia(path: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//当大于等于Android 4.4时
            MediaScannerConnection.scanFile(BasicApplication.context, arrayOf(path), null) { path, uri ->
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = uri
                BasicApplication.context?.sendBroadcast(mediaScanIntent)
            }

        } else {//Andrtoid4.4以下版本
            BasicApplication.context?.sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(File(path).parentFile)))
        }

    }


    //删除文件后更新数据库  通知媒体库更新文件夹
    fun updateFileFromdatabase(context: Context, file: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val paths = arrayOf(Environment.getExternalStorageDirectory().toString())
            MediaScannerConnection.scanFile(context, paths, null, null)
            MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null) { path, uri -> }
        } else {
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())))
        }
    }


    fun fileIsExists(path: String): Boolean {
        try {
            val f = File(path)
            if (!f.exists()) {
                return false
            }
        } catch (e: Exception) {

            return false
        }

        return true
    }

    /**
     * 获取指定文件大小
     * @return
     * @throws Exception 　　
     */
    @Throws(Exception::class)
    fun getFileSize(file: File): Long {
        var size: Long = 0
        if (file.exists()) {
            var fis: FileInputStream? = null
            fis = FileInputStream(file)
            size = fis.available().toLong()
        } else {
            file.createNewFile()
            Log.e("获取文件大小", "文件不存在!")
        }
        return size
    }
}
