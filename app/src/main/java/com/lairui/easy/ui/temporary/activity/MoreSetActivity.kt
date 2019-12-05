package com.lairui.easy.ui.temporary.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mywidget.dialog.ZhangDialog
import com.lairui.easy.utils.imageload.GlideCacheUtil
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.imageload.GlideUtils
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.ui.module.activity.LoginPhoneInfoActivity
import com.lairui.easy.ui.module.activity.ModifyLoginPassActivity

class MoreSetActivity : BasicActivity(), RequestView {


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
    @BindView(R.id.login_phone_lay)
    lateinit var mLoginPhoneLay: CardView
    @BindView(R.id.reset_loginpass_lay)
    lateinit var mResetLoginpassLay: CardView
    @BindView(R.id.modify_loginpass_lay)
    lateinit var mModifyLoginPassLay: CardView
    @BindView(R.id.modify_pass_lay)
    lateinit var mModifyPassLay: CardView
    @BindView(R.id.finger_login_lay)
    lateinit var mFingerLoginLay: CardView
    @BindView(R.id.shoushi_login_lay)
    lateinit var mShoushiLoginLay: CardView
    @BindView(R.id.safe_lay)
    lateinit var mSafeLay: CardView
    @BindView(R.id.dianzi_zhang_lay)
    lateinit var mDianziZhangLay: CardView
    @BindView(R.id.recevie_msg_lay)
    lateinit var mRecevieMsgLay: CardView
    @BindView(R.id.clear_cache_lay)
    lateinit var mClearCacheLay: CardView
    @BindView(R.id.about_us_lay)
    lateinit var mAboutUsLay: CardView
    @BindView(R.id.my_phone_tv)
    lateinit var mMyPhoneTv: TextView
    @BindView(R.id.is_install_tv)
    lateinit var mIsInstallTv: TextView
    @BindView(R.id.app_version_tv)
    lateinit var mAppVersionTv: TextView
    @BindView(R.id.cash_size_tv)
    lateinit var mCashSizeTv: TextView
    @BindView(R.id.dianzi_qz_line)
    lateinit var mDianziQzLine: View
    @BindView(R.id.modify_trade_pass_tv)
    lateinit var mModifyTradePassTv: TextView


    private var mRequestTag = ""

    private var mZhangUrl = ""

    private lateinit var mInstallMap: MutableMap<String, Any>
    private lateinit var mTradeStateMap: MutableMap<String, Any>

    override val contentView: Int
        get() = R.layout.activity_more_set

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == MbsConstans.BroadcastReceiverAction.ZHENGSHU_UPDATE) {
                isInstallCer()
            }
        }
    }


    private lateinit var mZDialog: ZhangDialog

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText!!.text = resources.getString(R.string.set_title)

        if (MbsConstans.USER_MAP != null) {
            mMyPhoneTv!!.text = MbsConstans.USER_MAP!!["tel"]!!.toString() + ""
        }
        mAppVersionTv!!.text = MbsConstans.UpdateAppConstans.VERSION_APP_NAME

        val intentFilter = IntentFilter()
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.ZHENGSHU_UPDATE)
        registerReceiver(mBroadcastReceiver, intentFilter)

        mCashSizeTv!!.text = GlideCacheUtil.instance.getCacheSize(this)

        isInstallCer()
        tradePassState()
    }

    override fun onResume() {
        super.onResume()
        if (mIsRefresh) {
            tradePassState()
            mIsRefresh = false
        }
    }

    private fun getMsgCodeAction() {

        mRequestTag = MethodUrl.resetPassCode
        val map = HashMap<String, Any>()
        val mPhone = SPUtils[this, MbsConstans.SharedInfoConstans.LOGIN_ACCOUNT, ""]!!.toString() + ""

        if (UtilTools.empty(mPhone)) {
            showToastMsg(resources.getString(R.string.toast_login_again))
            closeAllActivity()
            MbsConstans.USER_MAP = null
            MbsConstans.REFRESH_TOKEN = ""
            MbsConstans.ACCESS_TOKEN = ""
            SPUtils.put(this@MoreSetActivity, MbsConstans.SharedInfoConstans.LOGIN_OUT, true)
            val intent = Intent(this@MoreSetActivity, LoginActivity::class.java)
            startActivity(intent)
            return
        }

        map["tel"] = mPhone + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.resetPassCode, map)
    }


    @OnClick(R.id.back_img, R.id.login_phone_lay, R.id.reset_loginpass_lay, R.id.modify_pass_lay, R.id.finger_login_lay, R.id.modify_loginpass_lay, R.id.shoushi_login_lay, R.id.safe_lay, R.id.recevie_msg_lay, R.id.clear_cache_lay, R.id.about_us_lay, R.id.left_back_lay, R.id.dianzi_zhang_lay)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.login_phone_lay -> {
                intent = Intent(this@MoreSetActivity, LoginPhoneInfoActivity::class.java)
                startActivity(intent)
            }
            R.id.modify_loginpass_lay -> {
                intent = Intent(this@MoreSetActivity, ModifyLoginPassActivity::class.java)
                startActivity(intent)
            }
            R.id.reset_loginpass_lay -> getMsgCodeAction()
            R.id.modify_pass_lay -> if (mTradeStateMap == null || mTradeStateMap!!.isEmpty()) {
                tradePassState()
            } else {
                intent = Intent(this@MoreSetActivity, ModifyOrderPassActivity::class.java)
                val tradeState = mTradeStateMap!!["trd_pwd_state"]!!.toString() + ""
                if (tradeState == "0") {//交易密码状态（0：未设置，1：已设置）
                    intent.putExtra("TYPE", "1")//1设置新交易密码   2修改交易密码
                } else {
                    intent.putExtra("TYPE", "2")//1设置新交易密码   2修改交易密码
                }
                startActivity(intent)
            }
            R.id.finger_login_lay -> {
                intent = Intent(this@MoreSetActivity, FingermentActivity::class.java)
                startActivity(intent)
            }
            R.id.shoushi_login_lay -> {
                intent = Intent(this@MoreSetActivity, PatternActivity::class.java)
                startActivity(intent)
            }
            R.id.safe_lay//安装证书
            -> {
                val ss = mInstallMap!!["state"]!!.toString() + ""
                if (ss == "0") {
                    intent = Intent(this@MoreSetActivity, IdCardSuccessActivity::class.java)
                    intent.putExtra("verify_type", mInstallMap!!["verify_type"]!!.toString() + "")
                    intent.putExtra(MbsConstans.FaceType.FACE_KEY, MbsConstans.FaceType.FACE_INSTALL)
                    startActivity(intent)
                }
            }
            R.id.dianzi_zhang_lay ->
                //                showZhangDialog();
                dzQzImage()
            R.id.recevie_msg_lay//开启消息通知服务
            -> {
                intent = Intent(this@MoreSetActivity, ServicesRemindActivity::class.java)
                startActivity(intent)
            }
            R.id.clear_cache_lay -> {
                //clearCache();
                GlideCacheUtil.instance.clearImageAllCache(this)
                showToastMsg("清除成功")
                //mCashSizeTv.setText(GlideCacheUtil.getmContext().getCacheSize(this));
                mCashSizeTv!!.text = "0.0Byte"
                val mPhone = SPUtils[this, MbsConstans.SharedInfoConstans.LOGIN_ACCOUNT, ""]!!.toString() + ""
                val mConfig = SPUtils[this, MbsConstans.SharedInfoConstans.NAME_CODE_DATA, ""]!!.toString() + ""
                SPUtils.clear(this)
                SPUtils.put(this, MbsConstans.SharedInfoConstans.LOGIN_ACCOUNT, mPhone)
                SPUtils.put(this, MbsConstans.SharedInfoConstans.NAME_CODE_DATA, mConfig)
            }
            R.id.about_us_lay -> {
                intent = Intent(this@MoreSetActivity, AboutUsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun clearCache() {

        mRequestTag = MethodUrl.clearCache
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.clearCache, map)
    }

    /**
     * 是否设置交易密码
     */
    private fun tradePassState() {
        mRequestTag = MethodUrl.isTradePass
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.isTradePass, map)
    }

    /**
     * 是否安装证书信息
     */
    private fun isInstallCer() {
        mRequestTag = MethodUrl.isInstallCer
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.isInstallCer, map)
    }

    /**
     * 电子签章信息
     */
    private fun dzQzImage() {
        mRequestTag = MethodUrl.qzImage
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.qzImage, map)
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        val intent: Intent
        when (mType) {
            //交易密码状态
            MethodUrl.isTradePass -> {
                mTradeStateMap = tData
                val tradeState = tData["trd_pwd_state"]!!.toString() + ""
                if (tradeState == "0") {//交易密码状态（0：未设置，1：已设置）
                    mModifyTradePassTv!!.text = resources.getString(R.string.ser_order_pass_title)
                } else {
                    mModifyTradePassTv!!.text = resources.getString(R.string.modify_order_pass_title)
                }
            }
            MethodUrl.qzImage -> {
                mZhangUrl = tData["remotepath"]!!.toString() + ""
                showZhangDialog()
            }
            MethodUrl.resetPassCode -> {
                showToastMsg("获取验证码成功")
                intent = Intent(this@MoreSetActivity, CodeMsgActivity::class.java)
                intent.putExtra("DATA", tData as Serializable)
                intent.putExtra(MbsConstans.CodeType.CODE_KEY, MbsConstans.CodeType.CODE_RESET_LOGIN_PASS)
                intent.putExtra("showPhone", MbsConstans.USER_MAP!!["tel"]!!.toString() + "")
                startActivity(intent)
            }
            MethodUrl.isInstallCer //{verify_type=FACE, state=0}
            -> {
                mInstallMap = tData
                val ss = mInstallMap!!["state"]!!.toString() + ""
                if (ss == "0") {
                    mIsInstallTv!!.text = resources.getString(R.string.no_setip)
                    mDianziZhangLay!!.visibility = View.GONE
                    mDianziQzLine!!.visibility = View.GONE
                } else {
                    mIsInstallTv!!.text = resources.getString(R.string.has_setup)
                    mDianziZhangLay!!.visibility = View.VISIBLE
                    mDianziQzLine!!.visibility = View.VISIBLE
                }
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                for (mTag in mRequestTagList) {
                    when (mTag) {
                        MethodUrl.isTradePass -> tradePassState()
                        MethodUrl.qzImage -> dzQzImage()
                        MethodUrl.isInstallCer -> isInstallCer()
                        MethodUrl.resetPassCode -> getMsgCodeAction()
                    }
                }
                mRequestTagList = ArrayList()
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }

    private fun showZhangDialog() {
        mZDialog = ZhangDialog(this, true)
        val onClickListener = View.OnClickListener { v ->
            when (v.id) {
                R.id.cancel -> mZDialog!!.dismiss()
                R.id.confirm -> mZDialog!!.dismiss()
                R.id.tv_right -> mZDialog!!.dismiss()
            }
        }
        mZDialog!!.setCanceledOnTouchOutside(false)
        mZDialog!!.setCancelable(true)
        mZDialog!!.onClickListener = onClickListener
        mZDialog!!.initValue("电子印章", "")
        mZDialog!!.show()


        /* if (UtilTools.empty(loadImgUrl)){
            return;
        }*/
        if (mZhangUrl.contains("http")) {
        } else {
            mZhangUrl = MbsConstans.PIC_URL + mZhangUrl
        }

        //GlideUtils.loadImage(MoreSetActivity.this,"http://pic15.nipic.com/20110628/1369025_192645024000_2.jpg",mZDialog.mContentTv);
        mZDialog!!.mContentTv?.let { GlideUtils.loadImage(this@MoreSetActivity, mZhangUrl, it) }
        mZDialog!!.tv_cancel?.isEnabled = true
        mZDialog!!.tv_cancel?.visibility = View.VISIBLE
        mZDialog!!.tv_exit?.visibility = View.GONE
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }
}
