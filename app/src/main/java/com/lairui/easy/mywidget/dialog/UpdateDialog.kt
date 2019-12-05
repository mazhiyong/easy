package com.lairui.easy.mywidget.dialog

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import com.flyco.animation.BaseAnimatorSet
import com.flyco.animation.BounceEnter.BounceEnter
import com.flyco.animation.ZoomExit.ZoomOutExit
import com.flyco.dialog.utils.CornerUtils

import com.lairui.easy.R

class UpdateDialog : BaseDialog {
    var tv_cancel: Button? = null
    var tv_exit: Button? = null

    private var mTitleTv: TextView? = null
    private var mContentTv: TextView? = null

    private var mTitle: String? = null
    private var mContent: String? = null

    var progressLay: LinearLayout? = null
    var prgText: TextView? = null
    var progressBar: ProgressBar? = null

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
        val inflate = View.inflate(mContext, R.layout.update_dialog, null)
        inflate.setBackgroundDrawable(
                CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(10f).toFloat()))
        tv_cancel = inflate.findViewById(R.id.cancel)
        tv_exit = inflate.findViewById(R.id.confirm)
        mTitleTv = inflate.findViewById(R.id.tv_one)
        mContentTv = inflate.findViewById(R.id.tv_two)

        progressBar = inflate.findViewById(R.id.progress)
        prgText = inflate.findViewById(R.id.progress_text)
        progressLay = inflate.findViewById(R.id.progress_lay)


        return inflate
    }

    override fun setUiBeforShow() {
        tv_cancel!!.setOnClickListener(onClickListener)
        tv_exit!!.setOnClickListener(onClickListener)

        mTitleTv!!.text = mTitle
        mContentTv!!.text = mContent


    }

    fun initValue(title: String, content: String) {
        mTitle = title
        mContent = content
    }

    fun update(max: String, size: String, progress: String) {
        progressBar!!.max = Integer.parseInt(max)
        progressBar!!.progress = Integer.parseInt(size)
        prgText!!.text = progress
        if (max == size) {
            dismiss()
        }

    }
}