package com.lairui.easy.mywidget.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView

import com.flyco.animation.BaseAnimatorSet
import com.flyco.animation.SlideEnter.SlideBottomEnter
import com.flyco.animation.SlideExit.SlideBottomExit
import com.lairui.easy.R
import com.lairui.easy.mywidget.view.CustomerKeyboard
import com.lairui.easy.mywidget.view.PasswordEditText

/**
 * 交易密码弹框
 */
class TradePassDialog : BaseDialog, CustomerKeyboard.CustomerKeyboardClickListener, PasswordEditText.PasswordFullListener {


    override var mContext: Context

    var mPasswordEditText: PasswordEditText? = null
    var mCustomerKeyboard: CustomerKeyboard? = null
    var mForgetPassTv: TextView? = null

    var passFullListener: PassFullListener? = null


    constructor(context: Context) : super(context) {
        mContext = context
    }

    constructor(context: Context, isPopupStyle: Boolean) : super(context, isPopupStyle) {
        mContext = context
    }

    override fun onCreateView(): View {
        val view = View.inflate(mContext, R.layout.dialog_trade_pass_layout, null)

        mPasswordEditText = view.findViewById(R.id.password_edit_text)
        mCustomerKeyboard = view.findViewById(R.id.custom_key_board)
        mForgetPassTv = view.findViewById(R.id.forget_trade_pass)

        mCustomerKeyboard?.setOnCustomerKeyboardClickListener(this)
        mPasswordEditText?.isEnabled = false
        mPasswordEditText?.setOnPasswordFullListener(this)
        init()
        return view
    }

    @SuppressLint("WrongConstant")
    private fun init() {

    }

    override fun setUiBeforShow() {

        val bas_in: BaseAnimatorSet
        val bas_out: BaseAnimatorSet
        bas_in = SlideBottomEnter()
        bas_in.duration(400)
        bas_out = SlideBottomExit()
        bas_out.duration(200)

        widthScale(1f)
        dimEnabled(true)
        // baseDialog.heightScale(1f);
        showAnim(bas_in)
        dismissAnim(bas_out)
    }

    override fun click(number: String) {
        mPasswordEditText?.addPassword(number)
    }

    override fun delete() {
        mPasswordEditText?.deleteLastPassword()
    }

    override fun passwordFull(password: String) {
        // Toast.makeText(this,"密码输入完毕->"+password,Toast.LENGTH_LONG).show();
        if (passFullListener != null) {
            passFullListener!!.onPassFullListener(password)
        }
    }

    interface PassFullListener {
        fun onPassFullListener(pass: String)
    }

}
