package com.lairui.easy.mywidget.dialog

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.TextView

import com.flyco.animation.BaseAnimatorSet
import com.flyco.animation.BounceEnter.BounceEnter
import com.flyco.animation.ZoomExit.ZoomOutExit
import com.flyco.dialog.utils.CornerUtils
import com.lairui.easy.R

class ZZTipMsgDialog : BaseDialog {

    var tv_cancel: Button? = null
    var tv_sure: Button? = null
    var mNameTv: TextView? = null
    var mAccountTv: TextView? = null
    var mBankTv: TextView? = null
    var mBankNumTv: TextView? = null
    var mMap: MutableMap<String, Any>? = null

    var onClickListener: View.OnClickListener? = null

    constructor(context: Context) : super(context) {}
    constructor(context: Context, isb: Boolean) : super(context, isb) {}

    override fun onCreateView(): View {
        val bas_in: BaseAnimatorSet
        val bas_out: BaseAnimatorSet
        bas_in = BounceEnter()
        bas_out = ZoomOutExit()
        showAnim(bas_in)
        //dismissAnim(bas_out);//
        widthScale(0.85f)
        val inflate = View.inflate(mContext, R.layout.dialog_zz_msg_tip, null)
        inflate.setBackgroundDrawable(CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(10f).toFloat()))
        tv_cancel = inflate.findViewById(R.id.cancel)
        tv_sure = inflate.findViewById(R.id.sure)
        mNameTv = inflate.findViewById(R.id.tv_name)
        mAccountTv = inflate.findViewById(R.id.tv_account)
        mBankTv = inflate.findViewById(R.id.tv_bank)
        mBankNumTv = inflate.findViewById(R.id.tv_bank_num)


        return inflate
    }

    override fun setUiBeforShow() {
        //mNameTv.setText(mMap.get("orgname")+"");
        mAccountTv!!.text = mMap?.get("accid")!!.toString() + ""
        mBankTv!!.text = mMap!!["opnbnkwdnm"]!!.toString() + ""
        mBankNumTv!!.text = "302100011552"
        tv_cancel!!.setOnClickListener(onClickListener)
        tv_sure!!.setOnClickListener(onClickListener)
    }


    fun initValue(map: MutableMap<String, Any>) {
        mMap = map
    }
}