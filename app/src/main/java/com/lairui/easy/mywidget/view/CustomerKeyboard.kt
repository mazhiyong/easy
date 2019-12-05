package com.lairui.easy.mywidget.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R

/**
 * Email 240336124@qq.com
 * Created by Darren on 2016/12/24.
 * Version 1.0
 * Description: 自定义键盘
 */
class CustomerKeyboard @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    // 设置点击回掉监听
    private var mListener: CustomerKeyboardClickListener? = null

    init {
        // 直接加载布局
        View.inflate(context, R.layout.ui_customer_keyboard, this)
        setItemClickListener(this)
    }

    /**
     * 设置子View的ClickListener
     */
    private fun setItemClickListener(view: View) {
        if (view is ViewGroup) {
            val childCount = view.childCount
            for (i in 0 until childCount) {
                //不断的递归给里面所有的View设置OnClickListener
                val childView = view.getChildAt(i)
                setItemClickListener(childView)
            }
        } else {
            view.setOnClickListener(this)
        }
    }

    override fun onClick(v: View) {
        if (v is TextView) {
            // 点击的是数字
            val number = v.text.toString().trim { it <= ' ' }
            if (mListener != null) {
                mListener!!.click(number)
            }
        }

        if (v is ImageView) {
            // 点击的是删除
            if (mListener != null) {
                mListener!!.delete()
            }
        }
    }

    fun setOnCustomerKeyboardClickListener(listener: CustomerKeyboardClickListener) {
        this.mListener = listener
    }

    /**
     * 点击键盘的回调监听
     */
    interface CustomerKeyboardClickListener {
        fun click(number: String)
        fun delete()
    }
}
