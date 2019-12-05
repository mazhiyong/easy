package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mywidget.view.CustomerKeyboard
import com.lairui.easy.mywidget.view.PasswordEditText
import com.lairui.easy.basic.MbsConstans
import com.jaeger.library.StatusBarUtil

import butterknife.BindView
import butterknife.OnClick

/**
 * 更换手机号  界面
 */
class NewOrderPassActivity : BasicActivity(), CustomerKeyboard.CustomerKeyboardClickListener, PasswordEditText.PasswordFullListener {

    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.pass_tip)
    lateinit var mPassTip: TextView
    @BindView(R.id.right_img)
    lateinit var mRightImg: ImageView
    @BindView(R.id.top_layout)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.but_next)
    lateinit var mButNext: Button
    @BindView(R.id.password_edit_text)
    lateinit var mPasswordEditText: PasswordEditText
    @BindView(R.id.custom_key_board)
    lateinit var mCustomKeyBoard: CustomerKeyboard

    private var mNewPass = ""

    private var mOnePass: String? = ""
    private var mType = 0

    override val contentView: Int
        get() = R.layout.activity_new_order_pass

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)


        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mType = bundle.getInt("type")
            if (mType == 2) {
                mOnePass = bundle.getString("newPass")
                mPassTip!!.text = resources.getString(R.string.order_sure_pass)
                mButNext!!.text = resources.getString(R.string.but_finish)
            }
        }

        mTitleText!!.text = resources.getString(R.string.modify_order_pass_title)
        mCustomKeyBoard!!.setOnCustomerKeyboardClickListener(this)
        mPasswordEditText!!.isEnabled = false
        mPasswordEditText!!.setOnPasswordFullListener(this)
        mButNext!!.isEnabled = false
    }

    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> if (mType == 2) {
                if (mNewPass == mOnePass) {
                    showToastMsg("两次输入密码相同")
                } else {
                    showToastMsg("两次输入密码不同")
                }
            } else {
                intent = Intent(this@NewOrderPassActivity, NewOrderPassActivity::class.java)
                intent.putExtra("newPass", mNewPass)
                intent.putExtra("type", 2)
                startActivity(intent)
            }
        }
    }

    override fun click(number: String) {
        mPasswordEditText!!.addPassword(number)
    }

    override fun delete() {
        mPasswordEditText!!.deleteLastPassword()
        mButNext!!.isEnabled = false
    }

    override fun passwordFull(password: String) {
        //Toast.makeText(this,"密码输入完毕->"+password,Toast.LENGTH_LONG).show();
        mButNext!!.isEnabled = true
        mNewPass = password
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
