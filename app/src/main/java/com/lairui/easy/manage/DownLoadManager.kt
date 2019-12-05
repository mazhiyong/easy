package com.lairui.easy.manage

import android.util.Log

import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.bean.MessageEvent

import org.greenrobot.eventbus.EventBus

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.HashMap

import okhttp3.ResponseBody

object DownLoadManager {

    private val TAG = "DownLoadManager"

    //文件类型
    private var fileSuffix = ""

    private val APK_CONTENTTYPE = "application/vnd.android.package-archive"
    private val PNG_CONTENTTYPE = "image/png"
    private val JPG_CONTENTTYPE = "image/jpg"
    private val MP4_CONTENTTYPE = "video/mp4"

    private var sFutureStudioIconFile: File? = null
    private var sFileSize: Long = 0

    var PAUSE = false


    fun setsFutureStudioIconFile(sFutureStudioIconFile: File) {
        DownLoadManager.sFutureStudioIconFile = sFutureStudioIconFile
    }

    fun getsFutureStudioIconFile(): File? {

        return sFutureStudioIconFile
    }


    fun writeFile(body: ResponseBody, name: String): Boolean {


        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        var fileSizeDownloaded: Long


        val type = body.contentType()!!.toString()
        if (type == APK_CONTENTTYPE) {
            fileSuffix = ".apk"
        } else if (type == PNG_CONTENTTYPE) {
            fileSuffix = ".png"
        } else if (type == JPG_CONTENTTYPE) {
            fileSuffix = ".jpg"
        } else if (type == MP4_CONTENTTYPE) {
            fileSuffix = ".mp4"
        }

        val sPath = MbsConstans.BASE_PATH + File.separator + name + fileSuffix
        try {
            sFutureStudioIconFile = File(sPath)
            setsFutureStudioIconFile(sFutureStudioIconFile!!)

            try {
                val fileReader = ByteArray(1014 * 4)
                //需要下载的文件总长度
                if (sFileSize == 0L) {
                    sFileSize = body.contentLength()
                }
                //已经下载的文件长度
                fileSizeDownloaded = sFutureStudioIconFile!!.length()

                inputStream = body.byteStream()

                outputStream = FileOutputStream(sFutureStudioIconFile, true)

                if (fileSizeDownloaded < sFileSize) {
                    while (true) {
                        //是否暂停
                        if (PAUSE) {
                            break
                        } else {
                            val read = inputStream!!.read(fileReader)
                            if (read == -1) {
                                break
                            }
                            outputStream.write(fileReader, 0, read)
                            fileSizeDownloaded += read.toLong()
                            Log.d("show", "当前大小: " + fileSizeDownloaded + "   /总大小：" + sFileSize + "  /" + fileSizeDownloaded * 100 / sFileSize + "%")

                            //Eventbus  发送事件
                            val event = MessageEvent()
                            event.type = MbsConstans.MessageEventType.DOWN_LOAD
                            val map = HashMap<Any, Any>()
                            map["size"] = fileSizeDownloaded
                            map["max"] = sFileSize
                            map["progress"] = (fileSizeDownloaded * 100 / sFileSize).toString() + "%"
                            event.message = map

                            EventBus.getDefault().post(event)

                            outputStream.flush()

                        }
                    }
                }
                return true
            } catch (e: IOException) {
                return false
            } finally {
                inputStream?.close()

                outputStream?.close()
            }
        } catch (e: IOException) {
            return false
        }

    }

}
