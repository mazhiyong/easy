package com.lairui.easy.ui.temporary.activity

import android.content.Intent

import androidx.core.content.ContextCompat

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mywidget.view.CustomerKeyboard
import com.lairui.easy.mywidget.view.PasswordEditText
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.secret.RSAUtils
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 修改密码  界面
 */
class ModifyOrderPassActivity : BasicActivity(), CustomerKeyboard.CustomerKeyboardClickListener, PasswordEditText.PasswordFullListener {

    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
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

    @BindView(R.id.order_pass_tip_tv)
    lateinit var mOrderPassTipTv: TextView

    private var mRequestTag = ""

    private var mActionType: String? = ""
    private var mBackType = ""

    private var mOldPass: String? = ""
    private var mNewPass: String? = ""
    private val mAgainPass = ""

    private var mEditPass = ""

    private var mAuthCode: String? = ""


    override val contentView: Int
        get() = R.layout.activity_modify_order_pass

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mActionType = bundle.getString("TYPE")
            mBackType = bundle.getString("BACK_TYPE", "")//返回到哪个界面   提现界面  和  设置界面
            mAuthCode = bundle.getString("authcode")//短信校验后的authcode
            when (mActionType) {
                "1"//设置新交易密码  第一次进来
                -> {
                    mTitleText!!.text = resources.getString(R.string.ser_order_pass_title)
                    mOrderPassTipTv!!.text = resources.getString(R.string.order_new_pass)
                }
                "11"//设置新交易密码  第二次进来 再确认输入一遍 和第一次输入的新的密码进行验证  最终提交
                -> {
                    mTitleText!!.text = resources.getString(R.string.ser_order_pass_title)
                    mOrderPassTipTv!!.text = resources.getString(R.string.order_sure_pass)
                    mNewPass = bundle.getString("PASS")
                }
                "2"//修改交易密码  第一次进来 输入旧的交易密码验证身份
                -> {
                    mTitleText!!.text = resources.getString(R.string.modify_order_pass_title)
                    mOrderPassTipTv!!.text = resources.getString(R.string.order_old_pass)
                }
                "21"//修改交易密码  第二次进来 输入新的交易密码
                -> {
                    mTitleText!!.text = resources.getString(R.string.modify_order_pass_title)
                    mOrderPassTipTv!!.text = resources.getString(R.string.order_new_pass)
                    mOldPass = bundle.getString("PASS")
                }
                "22"//修改交易密码  第三次进来 再次输入新的交易密码 和上一次输入新的交易密码进行验证  最终提交
                -> {
                    mTitleText!!.text = resources.getString(R.string.modify_order_pass_title)
                    mOrderPassTipTv!!.text = resources.getString(R.string.order_sure_pass)
                    mNewPass = bundle.getString("PASS")
                    mOldPass = bundle.getString("OLDPASS")
                }
                "3"//忘记交易密码  第一次进来
                -> {
                    mTitleText!!.text = resources.getString(R.string.ser_order_pass_title)
                    mOrderPassTipTv!!.text = resources.getString(R.string.order_new_pass)
                }
                "31"//忘记交易密码  第二次进来
                -> {
                    mTitleText!!.text = resources.getString(R.string.ser_order_pass_title)
                    mOrderPassTipTv!!.text = resources.getString(R.string.order_sure_pass)
                    mNewPass = bundle.getString("PASS")
                }
            }
        }
        mCustomKeyBoard!!.setOnCustomerKeyboardClickListener(this)
        mPasswordEditText!!.isEnabled = false
        mPasswordEditText!!.setOnPasswordFullListener(this)
        mButNext!!.isEnabled = false
    }


    /**
     * 检查交易密码是否正确
     */
    private fun checkPass() {
        mRequestTag = MethodUrl.checkTradePass
        val map = HashMap<String, String>()
        map["trd_pwd"] = RSAUtils.encryptContent(mEditPass, RSAUtils.publicKey)
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.checkTradePass, map)
    }

    /**
     * 设置交易密码
     */
    private fun setPass() {
        mRequestTag = MethodUrl.setTradePass
        val map = HashMap<String, String>()

        when (mActionType) {
            "11"//设置新交易密码  第二次进来 再确认输入一遍 和第一次输入的新的密码进行验证  最终提交
            -> {
            }
            "22"//修改交易密码  第三次进来 再次输入新的交易密码 和上一次输入新的交易密码进行验证  最终提交
            -> map["trd_pwd_old"] = RSAUtils.encryptContent(mOldPass!!, RSAUtils.publicKey)
            "31"//忘记交易密码  第二次进来 再次输入新的交易密码  和第一次验证判断提交   （需加上验证短信成功后的authcode）
            -> map["authcode"] = mAuthCode!!
        }
        map["trd_pwd"] = RSAUtils.encryptContent(mNewPass!!, RSAUtils.publicKey)
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPutToMap(mHeaderMap, MethodUrl.setTradePass, map)
    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next ->

                when (mActionType) {
                    "1"//设置新交易密码  第一次进来
                    -> {
                        intent = Intent(this@ModifyOrderPassActivity, ModifyOrderPassActivity::class.java)
                        intent.putExtra("TYPE", "11")
                        intent.putExtra("PASS", mEditPass)
                        intent.putExtra("BACK_TYPE", mBackType)
                        startActivity(intent)
                    }
                    "11"//设置新交易密码  第二次进来 再确认输入一遍 和第一次输入的新的密码进行验证  最终提交
                    -> if (mNewPass == mEditPass) {
                        setPass()
                    } else {
                        showToastMsg("两次输入密码不同")
                    }
                    "2"//修改交易密码  第一次进来 输入旧的交易密码验证身份
                    ->
                        //请求后台验证老密码是否正确   验证成功后设置新密码
                        //-------------------------
                        checkPass()
                    "21"//修改交易密码  第二次进来 输入新的交易密码
                    -> {
                        intent = Intent(this@ModifyOrderPassActivity, ModifyOrderPassActivity::class.java)
                        intent.putExtra("TYPE", "22")
                        intent.putExtra("PASS", mEditPass)
                        intent.putExtra("OLDPASS", mOldPass)
                        intent.putExtra("BACK_TYPE", mBackType)
                        startActivity(intent)
                    }
                    "22"//修改交易密码  第三次进来 再次输入新的交易密码 和上一次输入新的交易密码进行验证  最终提交
                    ->

                        if (mNewPass == mEditPass) {
                            setPass()
                        } else {
                            showToastMsg("两次输入密码不同")
                        }
                    "3"//忘记交易密码  第一次进来
                    -> {
                        intent = Intent(this@ModifyOrderPassActivity, ModifyOrderPassActivity::class.java)
                        intent.putExtra("TYPE", "31")
                        intent.putExtra("PASS", mEditPass)
                        intent.putExtra("BACK_TYPE", mBackType)
                        intent.putExtra("authcode", mAuthCode)
                        startActivity(intent)
                    }
                    "31"//忘记交易密码  第二次进来
                    -> if (mNewPass == mEditPass) {
                        setPass()
                    } else {
                        showToastMsg("两次输入密码不同")
                    }
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
        // Toast.makeText(this,"密码输入完毕->"+password,Toast.LENGTH_LONG).show();
        mButNext!!.isEnabled = true
        mEditPass = password
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    /**
     */
    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        val intent: Intent
        when (mType) {
            MethodUrl.setTradePass -> {
                showToastMsg(tData["result"]!!.toString() + "")
                if (mBackType == "2") {
                    intent = Intent()
                    intent.action = MbsConstans.BroadcastReceiverAction.TRADE_PASS_UPDATE
                    sendBroadcast(intent)
                    backTo(TiXianActivity::class.java, false)
                } else if (mBackType == "3") {
                    intent = Intent()
                    intent.action = MbsConstans.BroadcastReceiverAction.TRADE_PASS_UPDATE
                    sendBroadcast(intent)
                    backTo(TiXianActivity::class.java, false)
                } else {
                    backTo(MoreSetActivity::class.java, true)
                }
            }
            //老交易密码 检测
            MethodUrl.checkTradePass -> {

                val st = tData["check_rst"]!!.toString() + ""
                if (st == "1") {
                    //现在假如验证成功了
                    intent = Intent(this@ModifyOrderPassActivity, ModifyOrderPassActivity::class.java)
                    intent.putExtra("TYPE", "21")
                    intent.putExtra("PASS", mEditPass)
                    intent.putExtra("BACK_TYPE", mBackType)
                    startActivity(intent)
                } else {
                    showToastMsg("密码错误，请重新输入")
                }
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.checkTradePass -> checkPass()
                    MethodUrl.setTradePass -> setPass()
                }
            }
        }
    }

    /**
     */
    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }
}
