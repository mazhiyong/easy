@file:Suppress("UNREACHABLE_CODE")

package com.lairui.easy.utils.secret

import android.text.TextUtils

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

object MD5 {
    private var instance: MD5? = null

    fun getInstance(): MD5? {
        if (instance == null) {
            instance = MD5
        }
        return instance
    }

    /*
      字符串加密
     */
    fun md5String(string: String): String {
        if (TextUtils.isEmpty(string)) {
            return ""
        }
        var md5: MessageDigest? = null
        try {
            md5 = MessageDigest.getInstance("MD5")
            val bytes = md5!!.digest(string.toByteArray())
            val result = StringBuilder()
            for (b in bytes) {
                var temp = Integer.toHexString((b and 0xff.toByte()).toInt())
                if (temp.length == 1) {
                    temp = "0$temp"
                }
                result.append(temp)
            }
            return result.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }


    fun md5File(file: File?): String {
        if (file == null || !file.isFile || !file.exists()) {
            return ""
        }
        var `in`: FileInputStream? = null
        var result = ""
        val buffer = ByteArray(8192)
        var len: Int
        try {
            val md5 = MessageDigest.getInstance("MD5")
            `in` = FileInputStream(file)

            do {
                if (`in`.read(buffer) != -1){
                    len = `in`.read(buffer)
                    md5.update(buffer, 0, len)
                }

            }while (true)

            val bytes = md5.digest()

            for (b in bytes) {
                var temp = Integer.toHexString((b and 0xff.toByte()).toInt())
                if (temp.length == 1) {
                    temp = "0$temp"
                }
                result += temp
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (null != `in`) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return result
    }
}
