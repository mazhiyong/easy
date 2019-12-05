package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mywidget.view.CustomerKeyboard
import com.lairui.easy.mywidget.view.PasswordEditText
import com.jaeger.library.StatusBarUtil

import butterknife.BindView
import butterknife.OnClick

/**
 * 提现（输入密码）
 */
class TiXianTestActivity : BasicActivity(), CustomerKeyboard.CustomerKeyboardClickListener, PasswordEditText.PasswordFullListener {

    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.password_edit_text)
    lateinit var mPasswordEditText: PasswordEditText
    @BindView(R.id.custom_key_board)
    lateinit var mKeyboard: CustomerKeyboard

    override val contentView: Int
        get() = R.layout.activity_ti_xian_test

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, R.color.font_c), 60)
        mTitleText!!.text = resources.getString(R.string.tixian)
        mKeyboard!!.setOnCustomerKeyboardClickListener(this)
        mKeyboard!!.isEnabled = false
        mPasswordEditText!!.setOnPasswordFullListener(this)
        mPasswordEditText!!.isEnabled = false

    }

    @OnClick(R.id.back_img, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
        }
    }

    override fun click(number: String) {
        mPasswordEditText!!.addPassword(number)
    }

    override fun delete() {
        mPasswordEditText!!.deleteLastPassword()
    }

    override fun passwordFull(password: String) {
        val intent = Intent(this, StateFeedbackActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * @descriptoin 请求前加载progress
     * @author dc
     * @date 2017/2/16 11:00
     */
    override fun showProgress() {

    }

    /**
     * @descriptoin 请求结束之后隐藏progress
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun disimissProgress() {

    }

    /**
     * @param tData 数据类型
     * @param mType
     * @descriptoin 请求数据成功
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

    }

    /**
     * @param map
     * @param mType
     * @descriptoin 请求数据错误
     * @date 2017/2/16 11:01
     */
    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

    }
}
