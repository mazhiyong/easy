package com.lairui.easy.utils.tool

import android.content.Context
import android.net.ConnectivityManager

object NetUtil {

    val isNetworkConnected: Boolean
        get() {
            if (AppContextUtil.instance != null) {
                val mConnectivityManager = AppContextUtil.instance
                        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mNetworkInfo = mConnectivityManager.activeNetworkInfo
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable
                }
            }
            return false
        }

    val isWifiConnected: Boolean
        get() {
            if (AppContextUtil.instance != null) {
                val mConnectivityManager = AppContextUtil.instance
                        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mWiFiNetworkInfo = mConnectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                if (mWiFiNetworkInfo != null) {
                    return mWiFiNetworkInfo.isAvailable
                }
            }
            return false
        }

    val isMobileConnected: Boolean
        get() {
            if (AppContextUtil.instance != null) {
                val mConnectivityManager = AppContextUtil.instance
                        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mMobileNetworkInfo = mConnectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                if (mMobileNetworkInfo != null) {
                    return mMobileNetworkInfo.isAvailable
                }
            }
            return false
        }

    val connectedType: Int
        get() {
            if (AppContextUtil.instance != null) {
                val mConnectivityManager = AppContextUtil.instance
                        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mNetworkInfo = mConnectivityManager.activeNetworkInfo
                if (mNetworkInfo != null && mNetworkInfo.isAvailable) {
                    return mNetworkInfo.type
                }
            }
            return -1
        }
}
