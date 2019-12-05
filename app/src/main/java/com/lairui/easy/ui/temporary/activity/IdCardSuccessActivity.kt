package com.lairui.easy.ui.temporary.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message

import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.SPUtils
import com.jaeger.library.StatusBarUtil


import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.MainActivity

/**
 * 安装证书   界面
 */
class IdCardSuccessActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.my_image)
    lateinit var mMyImage: ImageView
    @BindView(R.id.but_checkl)
    lateinit var mInstallBut: Button
    @BindView(R.id.zan_no_anzhuang)
    lateinit var mNoAnzhuangTv: TextView

    @BindView(R.id.anzhuang_text_tv)
    lateinit var mAnZhuangTextTv: TextView
    @BindView(R.id.dianzi_zhang_lay)
    lateinit var mDianziZhangLay: LinearLayout


    private var mVerifyType: String? = ""
    private var mAuthCode: String? = ""
    private var mOpType = 0

    private var mRequestTag = ""

    override val contentView: Int
        get() = R.layout.activity_idcard_success


    @SuppressLint("HandlerLeak")
    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            when (msg.what) {
                1 -> enterNextPage()
                2 -> {
                    showToastMsg("人脸验证授权失败")
                    mInstallBut!!.isEnabled = true
                }
            }
        }
    }

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)


        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mVerifyType = bundle.getString("verify_type")
            mAuthCode = bundle.getString("authcode")
            mOpType = bundle.getInt(MbsConstans.FaceType.FACE_KEY)
        }
        when (mOpType) {
            MbsConstans.FaceType.FACE_AUTH//直接一路认证过来的操作
            -> {
                mTitleText!!.text = resources.getString(R.string.id_card_check)
                mAnZhuangTextTv!!.text = resources.getString(R.string.check_success)
            }
            MbsConstans.FaceType.FACE_INSTALL//认证后第一时间并没有安装证书   更多设置里面点击安装证书进来进行的操作
            -> {
                mTitleText!!.text = resources.getString(R.string.install_cer)
                mAnZhuangTextTv!!.text = resources.getString(R.string.anzuang_card_text)
            }
        }

        getUserInfoAction()
    }

    /**
     * 获取用户基本信息
     */
    fun getUserInfoAction() {
        mRequestTag = MethodUrl.userInfo
        val map = HashMap<String, Any>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.userInfo, map)
    }

    /**
     * 安装证书进行的操作
     */
    private fun submitInstall() {

        mRequestTag = MethodUrl.installCerSubmit
        val map = HashMap<String, Any>()
        map["authcode"] = mAuthCode!!
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.installCerSubmit, map)
    }

    @OnClick(R.id.back_img, R.id.but_checkl, R.id.left_back_lay, R.id.zan_no_anzhuang)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.zan_no_anzhuang -> when (mOpType) {
                MbsConstans.FaceType.FACE_AUTH//直接一路认证过来的操作
                -> finish()
                MbsConstans.FaceType.FACE_INSTALL//认证后第一时间并没有安装证书   更多设置里面点击安装证书进来进行的操作
                -> finish()
            }
            R.id.but_checkl -> {
                mInstallBut!!.isEnabled = false
                when (mOpType) {
                    MbsConstans.FaceType.FACE_AUTH//直接一路认证过来的操作
                    ->

                        if (MbsConstans.USER_MAP != null) {
                            val firmKind = MbsConstans.USER_MAP!!["firm_kind"]!!.toString() + ""
                            if (firmKind == "0") {
                                //submitInstall();

                                mInstallBut!!.isEnabled = true
                                intent = Intent(this@IdCardSuccessActivity, QiyeCaActivity::class.java)
                                startActivity(intent)
                            } else {
                                mInstallBut!!.isEnabled = true
                                intent = Intent(this@IdCardSuccessActivity, QiyeCaActivity::class.java)
                                startActivity(intent)
                            }
                        } else {
                            mInstallBut!!.isEnabled = true
                        }
                    MbsConstans.FaceType.FACE_INSTALL//认证后第一时间并没有安装证书   更多设置里面点击安装证书进来进行的操作
                    ->
                        //if (mVerifyType.equals("FACE")){//如果是人脸识别的话，需要再次验证一下人脸
                        //netWorkWarranty();


                        if (MbsConstans.USER_MAP != null) {
                            val firmKind = MbsConstans.USER_MAP!!["firm_kind"]!!.toString() + ""
                            if (firmKind == "0") {
                                /* PermissionsUtils.requsetRunPermission(IdCardSuccessActivity.this, new RePermissionResultBack() {
                                    @Override
                                    public void requestSuccess() {
                                        netWorkWarranty();
                                    }

                                    @Override
                                    public void requestFailer() {
                                        toast(R.string.failure);
                                    }
                                },com.yanzhenjie.permission.runtime.Permission.Group.CAMERA,com.yanzhenjie.permission.runtime.Permission.Group.STORAGE);*/

                                mInstallBut!!.isEnabled = true
                                intent = Intent(this@IdCardSuccessActivity, QiyeCaActivity::class.java)
                                startActivity(intent)
                            } else {
                                mInstallBut!!.isEnabled = true
                                intent = Intent(this@IdCardSuccessActivity, QiyeCaActivity::class.java)
                                startActivity(intent)
                            }
                        } else {
                            mInstallBut!!.isEnabled = true
                        }
                }/* }else if (mVerifyType.equals("SMS")){//如果是人工验证的话    需要输入身份证号   后续操作短信验证码
                            intent = new Intent(IdCardSuccessActivity.this,IdCardEditActivity.class);
                            intent.putExtra(MbsConstans.CodeType.CODE_KEY, MbsConstans.CodeType.CODE_INSTALL);
                            //startActivity(intent);
                            startActivityForResult(intent,1);
                        }*/
            }
        }
    }


    /**
     * 联网授权
     */
    private fun netWorkWarranty() {

       /* val uuid = ConUtil.getUUIDString(this)

        Thread(Runnable {
            val manager = Manager(this@IdCardSuccessActivity)
            val licenseManager = LivenessLicenseManager(this@IdCardSuccessActivity)
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
                        mInstallBut!!.isEnabled = true
                    }
                }
                MbsConstans.FaceType.FACE_INSTALL -> {
                    bundle = data!!.extras
                    if (bundle != null) {
                        mAuthCode = bundle.getString("authcode")
                        submitInstall()
                    } else {
                        mInstallBut!!.isEnabled = true
                    }
                }
                else -> mInstallBut!!.isEnabled = true
            }

        } else if (requestCode == PAGE_INTO_LIVENESS) {//人脸识别返回来的数据
            if (resultCode == Activity.RESULT_OK) {
                bundle = data!!.extras
                bundle!!.putInt(MbsConstans.FaceType.FACE_KEY, MbsConstans.FaceType.FACE_INSTALL)
                intent = Intent(this@IdCardSuccessActivity, ResultActivity::class.java)
                intent.putExtras(bundle)
                //设置返回数据
                startActivityForResult(intent, 1)
            } else {
                mInstallBut!!.isEnabled = true
            }
        }
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
            MethodUrl.userInfo//用户信息 //{auth=1, firm_kind=0, head_pic=default, name=刘英超, tel=151****3298, idno=4107****3616, cmpl_info=0}
            -> {
                MbsConstans.USER_MAP = tData
                SPUtils.put(this@IdCardSuccessActivity, MbsConstans.SharedInfoConstans.LOGIN_INFO, JSONUtil.instance.objectToJson(MbsConstans.USER_MAP!!))
            }
            MethodUrl.installCerSubmit -> {
                showToastMsg("安装证书成功")
                mInstallBut!!.isEnabled = true
                when (mOpType) {
                    MbsConstans.FaceType.FACE_AUTH//直接一路认证过来的操作
                    -> {
                        intent = Intent(this@IdCardSuccessActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    MbsConstans.FaceType.FACE_INSTALL//认证后第一时间并没有安装证书   更多设置里面点击安装证书进来进行的操作
                    -> {
                        intent = Intent()
                        intent.action = MbsConstans.BroadcastReceiverAction.ZHENGSHU_UPDATE
                        sendBroadcast(intent)
                        finish()
                    }
                }
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.installCerSubmit -> submitInstall()
                    MethodUrl.userInfo -> getUserInfoAction()
                }
            }
        }//showUpdateDialog();

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        mInstallBut!!.isEnabled = true
        dealFailInfo(map, mType)
    }

    override fun onDestroy() {
        super.onDestroy()
        when (mOpType) {
            MbsConstans.FaceType.FACE_AUTH//直接一路认证过来的操作
            -> {
                val intent = Intent(this@IdCardSuccessActivity, MainActivity::class.java)
                startActivity(intent)
            }
            MbsConstans.FaceType.FACE_INSTALL//认证后第一时间并没有安装证书   更多设置里面点击安装证书进来进行的操作
            -> {
            }
        }
    }


    protected fun toast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {

        private val PAGE_INTO_LIVENESS = 101
    }

}
