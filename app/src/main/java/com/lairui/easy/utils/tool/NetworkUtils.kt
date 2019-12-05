package com.lairui.easy.utils.tool

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * 网络使用工具类
 */
object NetworkUtils {

    /**
     * 判断是不是wifi网络状态
     *
     * @param paramContext
     * @return
     */
    fun isWifi(paramContext: Context): Boolean {
        return "2" == getNetType(paramContext)[0]
    }

    /**
     * 判断是不是2/3G网络状态
     *
     * @param paramContext
     * @return
     */
    fun isMobile(paramContext: Context): Boolean {
        return "1" == getNetType(paramContext)[0]
    }

    /**
     * 网络是否可用
     *
     * @param paramContext
     * @return
     */
    fun isNetAvailable(paramContext: Context): Boolean {
        return if ("1" == getNetType(paramContext)[0] || "2" == getNetType(paramContext)[0]) {
            true
        } else false
    }

    /**
     * 获取当前网络状态 返回2代表wifi,1代表2G/3G
     *
     * @param paramContext
     * @return
     */
    fun getNetType(paramContext: Context): Array<String> {
        val arrayOfString = arrayOf("Unknown", "Unknown")
        val localPackageManager = paramContext.packageManager
        if (localPackageManager.checkPermission("android.permission.ACCESS_NETWORK_STATE", paramContext.packageName) != 0) {
            arrayOfString[0] = "Unknown"
            return arrayOfString
        }

        val localConnectivityManager = paramContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (localConnectivityManager == null) {
            arrayOfString[0] = "Unknown"
            return arrayOfString
        }

        val localNetworkInfo1 = localConnectivityManager.getNetworkInfo(1)
        if (localNetworkInfo1 != null && localNetworkInfo1.state == NetworkInfo.State.CONNECTED) {
            arrayOfString[0] = "2"
            return arrayOfString
        }

        val localNetworkInfo2 = localConnectivityManager.getNetworkInfo(0)
        if (localNetworkInfo2 != null && localNetworkInfo2.state == NetworkInfo.State.CONNECTED) {
            arrayOfString[0] = "1"
            arrayOfString[1] = localNetworkInfo2.subtypeName
            return arrayOfString
        }

        return arrayOfString
    }
}