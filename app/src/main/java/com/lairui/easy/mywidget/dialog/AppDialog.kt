package com.lairui.easy.mywidget.dialog

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.flyco.animation.BaseAnimatorSet
import com.flyco.animation.BounceEnter.BounceEnter
import com.flyco.animation.ZoomExit.ZoomOutExit
import com.flyco.dialog.utils.CornerUtils
import com.lairui.easy.R

class AppDialog : BaseDialog {
    lateinit var tv_cancel: TextView
    lateinit var tv_exit: TextView

    private var mTitleTv: TextView? = null
    private var mContentTv: TextView? = null

    private var mTitle: String? = null
    private var mContent: String? = null
    private var mleft: String? = null
    private var mright: String? = null

    var onClickListener: View.OnClickListener? = null

    fun setClickListener(clickListener: View.OnClickListener) {
        onClickListener = clickListener
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, isb: Boolean) : super(context, isb)

    override fun onCreateView(): View {
        val bas_in: BaseAnimatorSet
        val bas_out: BaseAnimatorSet
        bas_in = BounceEnter()
        bas_out = ZoomOutExit()
        showAnim(bas_in)
        dismissAnim(bas_out)//
        widthScale(0.7f)
        val inflate = View.inflate(mContext, R.layout.app_dialog, null)
        inflate.setBackgroundDrawable(
                CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(10f).toFloat()))
        tv_cancel = inflate.findViewById(R.id.cancel)
        tv_exit = inflate.findViewById(R.id.confirm)
        mTitleTv = inflate.findViewById(R.id.tv_title)
        mContentTv = inflate.findViewById(R.id.tv_message)

        return inflate
    }

    override fun setUiBeforShow() {
        tv_cancel.setOnClickListener(onClickListener)
        tv_exit.setOnClickListener(onClickListener)

        mTitleTv!!.text = mTitle
        mContentTv!!.text = mContent

        tv_cancel.text = mleft
        tv_exit.text = mright

    }

    fun initValue(title: String, content: String, left: String, right: String) {
        mTitle = title
        mContent = content
        mleft = left
        mright = right
    }
}