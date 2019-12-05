package com.lairui.easy.utils.tool

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object AppContextUtil {
    private var sContext: Context? = null

    val instance: Context
        get() {
            if (sContext == null) {
                throw NullPointerException("the context is null,please init AppContextUtil in Application first.")
            }
            return sContext as Context
        }

    fun init(context: Context) {
        sContext = context
    }
}
