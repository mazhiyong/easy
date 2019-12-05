package com.lairui.easy.mywidget.dialog

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

import com.flyco.animation.BaseAnimatorSet
import com.flyco.animation.BounceEnter.BounceEnter
import com.flyco.animation.ZoomExit.ZoomOutExit
import com.flyco.dialog.utils.CornerUtils
import com.lairui.easy.R

class ImageCodeDialog : BaseDialog {
    var tv_cancel: Button? = null
    var tv_exit: Button? = null
    var tv_right: ImageView? = null

    var mEditText: EditText? = null
    private var mTitleTv: TextView? = null
    var mContentTv: ImageView? = null

    private var mTitle: String? = null
    private var mContent: String? = null

    var onClickListener: View.OnClickListener? = null

    constructor(context: Context) : super(context) {}
    constructor(context: Context, isb: Boolean) : super(context, isb) {}

    override fun onCreateView(): View {
        val bas_in: BaseAnimatorSet
        val bas_out: BaseAnimatorSet
        bas_in = BounceEnter()
        bas_out = ZoomOutExit()
        showAnim(bas_in)
        dismissAnim(bas_out)//
        widthScale(0.85f)
        val inflate = View.inflate(mContext, R.layout.dialog_image_code, null)
        inflate.setBackgroundDrawable(
                CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(10f).toFloat()))
        tv_cancel = inflate.findViewById(R.id.cancel)
        tv_exit = inflate.findViewById(R.id.confirm)
        tv_right = inflate.findViewById(R.id.tv_right)
        mTitleTv = inflate.findViewById(R.id.tv_one)
        mContentTv = inflate.findViewById(R.id.tv_two)
        mEditText = inflate.findViewById(R.id.code_edit)


        return inflate
    }

    override fun setUiBeforShow() {
        tv_cancel!!.setOnClickListener(onClickListener)
        tv_exit!!.setOnClickListener(onClickListener)
        tv_right!!.setOnClickListener(onClickListener)
        mTitleTv!!.text = mTitle
    }

    fun initValue(title: String, content: String) {
        mTitle = title
        mContent = content
    }
}