package com.lairui.easy.api

import android.os.Environment
import android.text.TextUtils
import android.util.Log

/**
 * Created by heavyrain lee on 2017/11/24.
 */
interface Config {
    companion object {
        // 仅仅是host，没有http开头
        // 不可配置为127.0.0.1 或者 192.168.0.1
        //String IM_SERVER_HOST = "wildfirechat.cn";
        //String IM_SERVER_HOST = "8y2cdj.natappfree.cc";
        //String IM_SERVER_HOST = "39.97.238.99";
        //String IM_SERVER_HOST = "47.75.185.156";
        const val IM_SERVER_HOST = "47.52.155.199"
        //int IM_SERVER_PORT = 80;
        const val IM_SERVER_PORT = 88
        //正式商用时，建议用https，确保token安全
        //String APP_SERVER_ADDRESS = "http://wildfirechat.cn:8888";
        //String APP_SERVER_ADDRESS = "http://39.97.238.99:8888";
        //String APP_SERVER_ADDRESS = "http://47.75.185.156:8888";
        const val APP_SERVER_ADDRESS = "http://47.52.155.199:8888"
        const val ICE_ADDRESS = "turn:turn.wildfirechat.cn:3478"
        const val ICE_USERNAME = "wfchat"
        const val ICE_PASSWORD = "wfchat"
        const val DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND = 120
        val VIDEO_SAVE_DIR = Environment.getExternalStorageDirectory().path + "/wfc/video"
        val AUDIO_SAVE_DIR = Environment.getExternalStorageDirectory().path + "/wfc/audio"
        val PHOTO_SAVE_DIR = Environment.getExternalStorageDirectory().path + "/wfc/photo"
        val FILE_SAVE_DIR = Environment.getExternalStorageDirectory().path + "/wfc/file"


        fun validateConfig() {
            check(!(TextUtils.isEmpty(IM_SERVER_HOST)
                    || IM_SERVER_HOST.startsWith("http")
                    || TextUtils.isEmpty(APP_SERVER_ADDRESS)
                    || !APP_SERVER_ADDRESS.startsWith("http")
                    || IM_SERVER_HOST == "192.168.0.1" || IM_SERVER_HOST == "127.0.0.1" || APP_SERVER_ADDRESS.contains("192.168.0.1")
                    || APP_SERVER_ADDRESS.contains("127.0.0.1"))) { "im server host config error" }
            if (IM_SERVER_PORT != 80) {
                Log.w("wfc config", "如果IM_SERVER_PORT配置为非80端口，无法使用第三方文件存储")
            }
        }
    }
}