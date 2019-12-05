package com.lairui.easy.mywidget.view

import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.lairui.easy.R
import com.lairui.easy.basic.BasicApplication

/**
 * 自定义提示Toast
 *
 */
class TipsToast(context: Context) : Toast(context) {

    fun setIcon(iconResId: Int) {
        if (view == null) {
            throw RuntimeException("This Toast was not created with Toast.makeText()")
        }
        val iv = view.findViewById<View>(R.id.tips_icon) as ImageView
                ?: throw RuntimeException("This Toast was not created with Toast.makeText()")
        iv.setImageResource(iconResId)
    }

    override fun setText(s: CharSequence) {
        if (view == null) {
            throw RuntimeException("This Toast was not created with Toast.makeText()")
        }
        val tv = view.findViewById<View>(R.id.tips_msg) as TextView
                ?: throw RuntimeException("This Toast was not created with Toast.makeText()")
        tv.text = s
    }

    private fun showTips(iconResId: Int, msgResId: Int) {
        val tipsToast = TipsToast.makeText(msgResId, Toast.LENGTH_SHORT)
        tipsToast.show()
        tipsToast.setIcon(iconResId)
        tipsToast.setText(msgResId)
    }

    companion object {
        private val mHandler = Handler(Looper.getMainLooper())

        fun makeText(text: CharSequence, duration: Int): TipsToast {
            val result = BasicApplication.context?.let { TipsToast(it) }

            val inflate = BasicApplication.context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val v = inflate.inflate(R.layout.view_tips, null)
            val tv = v.findViewById<View>(R.id.tips_msg) as TextView
            tv.text = text

            result!!.view = v
            // setGravity方法用于设置位置，此处为垂直居中
            result.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            result.duration = duration

            return result
        }

        @Throws(Resources.NotFoundException::class)
        fun makeText(resId: Int, duration: Int): TipsToast {
            return makeText(BasicApplication.context!!.getResources().getText(resId), duration)
        }


        /**
         * 提示消息
         * @param resId
         */
        fun showToastMsg(resId: Int) {
            mHandler.post {
                //Toast.makeText(BasicActivity.this, resId, Toast.LENGTH_SHORT).show();
                showTips(resId)
            }
        }

        /**
         * 提示消息
         */
        fun showToastMsg(msg: String) {
            mHandler.post {
                //Toast.makeText(BasicActivity.this, msg, Toast.LENGTH_SHORT).show();
                showTips(msg)
            }
        }

        private fun showTips(msgResId: Int) {
            val tipsToast = TipsToast.makeText(msgResId,Toast.LENGTH_SHORT)
            tipsToast.show()
            //tipsToast.setIcon(iconResId);
            tipsToast.setText(msgResId)
        }

        private fun showTips(msgResId: String) {
            val tipsToast = TipsToast.makeText(msgResId,Toast.LENGTH_SHORT)
            tipsToast.show()
            //tipsToast.setIcon(iconResId);
            tipsToast.setText(msgResId)
        }
    }

}
