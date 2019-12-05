package com.lairui.easy.utils.tool

import android.util.Log

object LogUtil {

    var DEBUG = true
    @JvmStatic
    fun i(tag: String, `object`: Any) {  //信息太长,分段打印
        if (!DEBUG) {
            return
        }
        //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
        //  把4*1024的MAX字节打印长度改为2001字符数
        var msg = `object`.toString() + ""
        val max_str_length = 2001 - tag.length
        //大于4000时
        while (msg.length > max_str_length) {
            Log.i(tag, msg.substring(0, max_str_length))
            msg = msg.substring(max_str_length)
        }
        //剩余部分
        Log.i(tag, msg)
    }

}
