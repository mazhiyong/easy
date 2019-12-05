package com.lairui.easy.utils.tool

import android.os.Build
import android.text.TextUtils
import android.widget.Toast
import com.lairui.easy.basic.BasicApplication

/**
 * Toast 工具类
 */
object ToastUtils {
    private var lastToast: Toast? = null
    @JvmStatic
    @JvmOverloads
    fun showToast(resourceId: Int, duration: Int = Toast.LENGTH_SHORT) {
        showToast(BasicApplication.context!!.getResources().getString(resourceId), duration)
    }

    @JvmStatic
    @JvmOverloads
    fun showToast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
        if (TextUtils.isEmpty(message)) return
        // 9.0 以上直接用调用即可防止重复的显示的问题，且如果复用 Toast 会出现无法再出弹出对话框问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Toast.makeText(BasicApplication.context, message, duration).show()
        } else {
            if (lastToast != null) {
                lastToast!!.setText(message)
            } else {
                lastToast = Toast.makeText(BasicApplication.context, message, duration)
            }
            lastToast!!.show()
        }
    }
}