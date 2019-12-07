package com.lairui.easy.mywidget.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.flyco.animation.BaseAnimatorSet
import com.flyco.animation.BounceEnter.BounceEnter
import com.flyco.animation.ZoomExit.ZoomOutExit
import com.flyco.dialog.utils.CornerUtils
import com.lairui.easy.R

class SimpleTipMsgDialog : BaseDialog{

    var cancelIv: ImageView? = null
    var mTitleTv: TextView? = null
    var mDealTv: TextView? = null
    var mTitle: String = ""
    var mDeal: String = ""

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
        //dismissAnim(bas_out);//
        widthScale(0.8f)
        val inflate = View.inflate(mContext, R.layout.dialog_simple_msg_tip, null)
        inflate.setBackgroundDrawable(CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(10f).toFloat()))
        cancelIv = inflate.findViewById(R.id.cancelIv)
        mTitleTv = inflate.findViewById(R.id.tipTv)
        mDealTv = inflate.findViewById(R.id.dealTv)


        return inflate
    }

    @SuppressLint("SetTextI18n")
    override fun setUiBeforShow() {
        mTitleTv!!.text = mTitle
        mDealTv!!.text = mDeal
        cancelIv!!.setOnClickListener(onClickListener)
        mDealTv!!.setOnClickListener(onClickListener)
    }


    fun initValue(title: String,deal:String) {
        mTitle = title
        mDeal = deal
    }
}