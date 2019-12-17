package com.lairui.easy.ui.temporary.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.MyClickableSpan
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.imageload.GlideUtils
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.utils.tool.LogUtil
import com.jaeger.library.StatusBarUtil


import java.util.ArrayList
import java.util.HashMap

import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.MainActivity

/**
 * 企业申请数字证书   修改后的
 */
class QiyeCaActivity : BasicActivity(), RequestView, ReLoadingData {

    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.right_text_tv)
    lateinit var mRightTextTv: TextView
    @BindView(R.id.right_img)
    lateinit var mRightImg: ImageView
    @BindView(R.id.right_lay)
    lateinit var mRightLay: LinearLayout
    @BindView(R.id.divide_line)
    lateinit var mDivideLine: View
    @BindView(R.id.top_layout)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.my_image)
    lateinit var mMyImage: ImageView
    @BindView(R.id.xieyi_checkbox)
    lateinit var mXieyiCheckbox: CheckBox
    @BindView(R.id.soft_xieyi_tv)
    lateinit var mSoftXieyiTv: TextView
    @BindView(R.id.sushu_value_tv)
    lateinit var mSushuValueTv: TextView
    @BindView(R.id.user_name_value_tv)
    lateinit var mUserNameValueTv: TextView
    @BindView(R.id.phone_value_tv)
    lateinit var mPhoneValueTv: TextView
    @BindView(R.id.apply_date_value_tv)
    lateinit var mApplyDateValueTv: TextView
    @BindView(R.id.limit_value_tv)
    lateinit var mLimitValueTv: TextView
    @BindView(R.id.qiye_qianzhang_image)
    lateinit var mQiyeQZImage: ImageView
    @BindView(R.id.but_checkl)
    lateinit var mButCheck: Button

    private lateinit var mConfigMap: MutableMap<String, Any>
    private var mRequestTag = ""

    private var mPressBut = false
    private var mAuthCode: String? = ""

    override val contentView: Int
        get() = R.layout.activity_qiye_ca


    @SuppressLint("HandlerLeak")
     var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            when (msg.what) {
                1 -> enterNextPage()
                2 -> {
                    showToastMsg("人脸验证授权失败")
                    mButCheck!!.isEnabled = true
                }
            }
        }
    }

    //广播监听
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val b = intent.extras
            if (MbsConstans.BroadcastReceiverAction.CAPAY_SUC == action) {
                mButCheck!!.text = getString(R.string.apply)
            }
        }
    }

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        val filter = IntentFilter()
        filter.addAction(MbsConstans.BroadcastReceiverAction.CAPAY_SUC)
        registerReceiver(receiver, filter)
        mTitleText!!.text = "申请数字证书"
        checkCa()
    }

    private fun initXieyiView() {

        val list = ArrayList<MutableMap<String, Any>>()

        var map: MutableMap<String, Any> = HashMap()
        map["title"] = "数字证书申请表"
        map["content"] = "数字证书申请表"
        map["url"] = mConfigMap!!["catabpatn"]!!.toString() + ""
        list.add(map)

        map = HashMap()
        map["url"] = mConfigMap!!["caauthpath"]!!.toString() + ""
        map["title"] = "数字证书使用授权书"
        map["content"] = "数字证书使用授权书"
        list.add(map)

        var xiyiStr = "已阅读并同意签署"
        for (i in list.indices) {
            val mm = list[i]
            val str = mm["content"]!!.toString() + ""
            if (i == list.size - 1) {
                xiyiStr = "$xiyiStr《$str》"
            } else {
                xiyiStr = "$xiyiStr《$str》和"
            }
        }
        val sp = SpannableString(xiyiStr)

        for (xieyiMap in list) {
            val str = xieyiMap["content"]!!.toString() + ""
            setClickableSpan(sp, View.OnClickListener {
                val intent = Intent(this@QiyeCaActivity, PDFLookActivity::class.java)
                intent.putExtra("id", xieyiMap["url"]!!.toString() + "")
                startActivity(intent)
            }, xiyiStr, "《$str》")
        }
        mSoftXieyiTv!!.text = sp
        //添加点击事件时，必须设置
        mSoftXieyiTv!!.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun initValueView() {
        mSushuValueTv!!.text = mConfigMap!!["firmname"]!!.toString() + ""
        mUserNameValueTv!!.text = mConfigMap!!["username"]!!.toString() + ""
        mPhoneValueTv!!.text = mConfigMap!!["telephone"]!!.toString() + ""
        mApplyDateValueTv!!.text = mConfigMap!!["reqdate"]!!.toString() + ""
        mLimitValueTv!!.text = mConfigMap!!["validdate"]!!.toString() + ""
        val payStatus = mConfigMap!!["payState"]!!.toString() + ""
        if (payStatus == "1") {//证书费用支付状态（0：未支付 1：已支付）
            mButCheck!!.text = getString(R.string.apply)
        } else {
            mButCheck!!.text = getString(R.string.but_next)
        }
        loadQZImage()

        initXieyiView()

    }

    /**
     * 加载电子签章信息
     */
    private fun loadQZImage() {

        // GlideUtils.loadImage(this,"https://www.baidu.com/img/bd_logo1.png",mQiyeQZImage);

        if (mConfigMap == null || mConfigMap!!.isEmpty()) {
            return
        }
        var loadImgUrl = mConfigMap!!["remotepath"]!!.toString() + ""

        LogUtil.i("show--------------------------------------------------------", loadImgUrl)


        /* if (UtilTools.empty(loadImgUrl)){
            return;
        }*/
        if (loadImgUrl.contains("http")) {
        } else {
            loadImgUrl = MbsConstans.PIC_URL + loadImgUrl
        }
        val imgUrl = loadImgUrl

        GlideUtils.loadImage(this, imgUrl, mQiyeQZImage!!)
        //
    }


    private fun caNextAction() {
        val payStatus = mConfigMap!!["payState"]!!.toString() + ""
        if (payStatus == "1") {//证书费用支付状态（0：未支付 1：已支付）
            //  去申请证书
            PermissionsUtils.requsetRunPermission(this@QiyeCaActivity, object : RePermissionResultBack {
                override fun requestSuccess() {
                    netWorkWarranty()
                }

                override fun requestFailer() {
                    toast(R.string.failure)
                }
            }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)

        } else {//去支付费用
            val intent = Intent(this, QiyeCaPayActivity::class.java)
            startActivity(intent)
        }

        mButCheck!!.isEnabled = true
    }


    /**
     * 检测是否已支付money  匹配来账信息
     */
    private fun checkCa() {
        mRequestTag = MethodUrl.checkCa
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.checkCa, map)
    }

    /**
     * 获取证书信息
     */
    private fun caInfo() {
        mRequestTag = MethodUrl.caConfig
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.caConfig, map)
    }

    /**
     * 安装证书进行的操作
     */
    private fun submitInstall() {

        mRequestTag = MethodUrl.installCerSubmit
        val map = HashMap<String, Any>()
        map["authcode"] = mAuthCode!!
        map["remotepath"] = mConfigMap!!["remotepath"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.installCerSubmit, map)
    }


    private fun setClickableSpan(sp: SpannableString, l: View.OnClickListener, str: String, span: String): SpannableString {
        sp.setSpan(MyClickableSpan(ContextCompat.getColor(this, R.color.blue1), l), str.indexOf(span), str.indexOf(span) + span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sp
    }


    override fun reLoadingData() {

    }

    /**
     * @descriptoin 请求前加载progress
     * @author dc
     * @date 2017/2/16 11:00
     */
    override fun showProgress() {
        showProgressDialog()
    }

    /**
     * @descriptoin 请求结束之后隐藏progress
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun disimissProgress() {
        dismissProgressDialog()
    }

    /**
     * @param tData 数据类型
     * @param mType
     * @descriptoin 请求数据成功
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

        val intent: Intent
        when (mType) {
            MethodUrl.installCerSubmit -> {
                val msg = tData["result"]!!.toString() + ""
                showToastMsg(msg)
                intent = Intent()
                intent.action = MbsConstans.BroadcastReceiverAction.ZHENGSHU_UPDATE
                sendBroadcast(intent)
                backTo(MainActivity::class.java, true)
            }
            MethodUrl.caConfig -> {
                mButCheck!!.isEnabled = true
                mConfigMap = tData
                initValueView()
                if (mPressBut) {
                    caNextAction()
                    mPressBut = false
                }
            }
            MethodUrl.checkCa -> caInfo()
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.caConfig -> caInfo()
                    MethodUrl.checkCa -> checkCa()
                }
            }
        }
    }

    /**
     * @param map
     * @param mType
     * @descriptoin 请求数据错误
     * @date 2017/2/16 11:01
     */
    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        mButCheck!!.isEnabled = true
        dealFailInfo(map, mType)
    }

    @OnClick(R.id.left_back_lay, R.id.but_checkl, R.id.qiye_qianzhang_image)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.left_back_lay -> finish()
            R.id.qiye_qianzhang_image -> loadQZImage()
            R.id.but_checkl -> {
                if (!mXieyiCheckbox!!.isChecked) {
                    showToastMsg("请您先阅读并同意签署相关协议")
                    return
                }
                mPressBut = true
                mButCheck!!.isEnabled = false
                checkCa()
            }
        }
    }

    /**
     * 联网授权
     */
    private fun netWorkWarranty() {

       /* val uuid = ConUtil.getUUIDString(this)

        Thread(Runnable {
            val manager = Manager(this@QiyeCaActivity)
            val licenseManager = LivenessLicenseManager(this@QiyeCaActivity)
            manager.registerLicenseManager(licenseManager)
            manager.takeLicenseFromNetwork(uuid)
            if (licenseManager.checkCachedLicense() > 0) {
                //授权成功
                mHandler.sendEmptyMessage(1)
            } else {
                //授权失败
                mHandler.sendEmptyMessage(2)
            }
        }).start()*/
    }

    private fun enterNextPage() {
        //startActivityForResult(Intent(this, LivenessActivity::class.java), PAGE_INTO_LIVENESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data)

        var intent: Intent
        val bundle: Bundle?
        if (requestCode == 1) {
            when (resultCode) {
                //通过短信验证码  安装证书
                MbsConstans.CodeType.CODE_INSTALL -> {
                    bundle = data!!.extras
                    if (bundle != null) {
                        mAuthCode = bundle.getString("authcode")
                        submitInstall()
                    } else {
                        mButCheck!!.isEnabled = true
                    }
                }
                MbsConstans.FaceType.FACE_INSTALL -> {
                    bundle = data!!.extras
                    if (bundle != null) {
                        mAuthCode = bundle.getString("authcode")
                        submitInstall()
                    } else {
                        mButCheck!!.isEnabled = true
                    }
                }
                else -> mButCheck!!.isEnabled = true
            }

        } else if (requestCode == PAGE_INTO_LIVENESS) {//人脸识别返回来的数据
            if (resultCode == Activity.RESULT_OK) {
                bundle = data!!.extras
                bundle!!.putInt(MbsConstans.FaceType.FACE_KEY, MbsConstans.FaceType.FACE_INSTALL)
                intent = Intent(this@QiyeCaActivity, ResultActivity::class.java)
                intent.putExtras(bundle)
                //设置返回数据
                startActivityForResult(intent, 1)
            } else {
                mButCheck!!.isEnabled = true
            }
        }
    }

    protected fun toast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    companion object {

        private val PAGE_INTO_LIVENESS = 101
    }
}
