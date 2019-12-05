package com.lairui.easy.ui.temporary.activity

import android.content.Intent

import androidx.core.content.ContextCompat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mywidget.dialog.UpdateDialog
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.service.DownloadService
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.share.ShareUtil
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil
import com.mob.MobSDK

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener

/**
 * 关于我们  界面
 */
class AboutUsActivity : BasicActivity(), RequestView {
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.iv_welcome)
    lateinit var mImageViewPhone: ImageView
    @BindView(R.id.iv_about_pihuibao)
    lateinit var mImageViewAbout: ImageView
    @BindView(R.id.tv_phone_coutomer)
    lateinit var mTextView: TextView
    @BindView(R.id.head_image)
    lateinit var mHeadImage: ImageView
    @BindView(R.id.version_check_lay)
    lateinit var mVersionCheckLay: LinearLayout
    @BindView(R.id.puhuibao_jieshao_lay)
    lateinit var mJieShaoLay: LinearLayout
    @BindView(R.id.version_tv)
    lateinit var mVersionTv: TextView
    @BindView(R.id.edit_phone)
    lateinit var mEditPhone: EditText
    @BindView(R.id.edit)
    lateinit var mEdit: EditText
    @BindView(R.id.shared_lay)
    lateinit var mSharedLay: LinearLayout


    private var mRequestTag = ""
    private val mTempToken = ""
    private val mAuthCode = ""
    private val mSmsToken = ""


    private var mShareMap: MutableMap<String, Any>? = null

    override val contentView: Int
        get() = R.layout.activity_about_us


    private var mUpdateDialog: UpdateDialog? = null


    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText!!.setText(R.string.about_puhuibao)
        mVersionTv!!.text = MbsConstans.UpdateAppConstans.VERSION_APP_NAME
        //getTempToken();
        getShareData()
    }

    @OnClick(R.id.back_img, R.id.version_check_lay, R.id.iv_about_pihuibao, R.id.welcome_lay, R.id.tv_phone_coutomer, R.id.left_back_lay, R.id.head_image, R.id.puhuibao_jieshao_lay, R.id.shared_lay)
    fun onViewClicked(view: View) {
        var intent: Intent? = null
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.puhuibao_jieshao_lay -> {
                intent = Intent(this@AboutUsActivity, AboutDeRongActivity::class.java)
                startActivity(intent)
            }
            R.id.welcome_lay -> {
            }
            R.id.head_image -> {
            }
            R.id.version_check_lay ->
                //registerAction();
                getAppVersion()
            R.id.tv_phone_coutomer ->
                /*Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + mTextView.getText().toString()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                UtilTools.startTel(this, mTextView!!.text.toString())

            R.id.shared_lay //分享
            -> if (mShareMap != null) {
                val url = mShareMap!!["codeurl"]!!.toString() + ""
                LogUtil.i("AboutUsActivity", mShareMap.toString() + "--------------------------           " + url)
                ShareUtil.showShare(this@AboutUsActivity, "来自得融在线", "分享注册", url)
            } else {
            }
        }// ShareUtil.showShare(AboutUsActivity.this,"来自智能收款","销售统计表格",
        //        "https://gagayi.oss-cn-beijing.aliyuncs.com/video/pdf-signed.pdf");
        // getRegSms();
        //checkImageCode();
        // getImageCode();
    }

    /**
     * 获取分享内容
     */
    fun getShareData() {
        mRequestTag = MethodUrl.shareUrl
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.shareUrl, map)
    }


    /**
     * 获取app更新信息
     */
    fun getAppVersion() {
        mRequestTag = MethodUrl.appVersion
        val map = HashMap<String, String>()
        map["appCode"] = MbsConstans.UPDATE_CODE
        map["osType"] = "android"
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.appVersion, map)
    }

    private fun showUpdateDialog() {
        if (MbsConstans.UpdateAppConstans.VERSION_NET_CODE > MbsConstans.UpdateAppConstans.VERSION_APP_CODE) {
            mUpdateDialog = UpdateDialog(this, true)
            val onClickListener = OnClickListener { v ->
                when (v.id) {
                    R.id.cancel -> if (MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE) {
                        showToastMsg("本次升级为必须更新，请您更新！")
                    } else {
                        mUpdateDialog!!.dismiss()
                    }
                    R.id.confirm -> {
                        PermissionsUtils.requsetRunPermission(this@AboutUsActivity, object : RePermissionResultBack {
                            override fun requestSuccess() {
                                mUpdateDialog!!.progressLay!!.visibility = View.VISIBLE
                                DownloadService.downNewFile(MbsConstans.UpdateAppConstans.VERSION_NET_APK_URL, 1003,
                                        MbsConstans.UpdateAppConstans.VERSION_NET_APK_NAME, MbsConstans.UpdateAppConstans.VERSION_MD5_CODE, this@AboutUsActivity)

                            }

                            override fun requestFailer() {

                            }
                        },com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)

                        if (!MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE) {
                            mUpdateDialog!!.dismiss()
                        }
                    }
                }
            }
            mUpdateDialog!!.setCanceledOnTouchOutside(false)
            mUpdateDialog!!.setCancelable(false)
            var ss = ""
            if (MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE) {
                ss = "必须更新"
            } else {
                ss = "建议更新"
            }
            mUpdateDialog!!.onClickListener = onClickListener
            mUpdateDialog!!.initValue("检查新版本($ss)", "更新内容:\n" + MbsConstans.UpdateAppConstans.VERSION_NET_UPDATE_MSG)
            mUpdateDialog!!.show()

            if (MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE) {
                mUpdateDialog!!.setCancelable(false)
                mUpdateDialog!!.tv_cancel?.isEnabled = false
                mUpdateDialog!!.tv_cancel?.visibility = View.GONE
            } else {
                mUpdateDialog!!.setCancelable(true)
                mUpdateDialog!!.tv_cancel?.isEnabled = true
                mUpdateDialog!!.tv_cancel?.visibility = View.VISIBLE
            }
            mUpdateDialog!!.progressLay!!.visibility = View.GONE
            DownloadService.mProgressBar = mUpdateDialog!!.progressBar
            DownloadService.mTextView = mUpdateDialog!!.prgText
        } else {
            showToastMsg("已经是最新版本")
        }
    }


    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.shareUrl -> mShareMap = tData
            MethodUrl.appVersion ->

                if (tData != null && !tData.isEmpty()) {
                    //网络版本号
                    MbsConstans.UpdateAppConstans.VERSION_NET_CODE = UtilTools.getIntFromStr(tData["verCode"].toString() + "")
                    //MbsConstans.UpdateAppConstans.VERSION_NET_CODE = 3;
                    //网络下载url
                    MbsConstans.UpdateAppConstans.VERSION_NET_APK_URL = tData["downUrl"].toString() + ""
                    //MbsConstans.UpdateAppConstans.VERSION_NET_APK_URL = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
                    //网络版本名称
                    MbsConstans.UpdateAppConstans.VERSION_NET_APK_NAME = tData["verName"].toString() + ""
                    //网络MD5值
                    MbsConstans.UpdateAppConstans.VERSION_MD5_CODE = tData["md5Code"].toString() + ""
                    //本次更新内容
                    MbsConstans.UpdateAppConstans.VERSION_NET_UPDATE_MSG = tData["verUpdateMsg"].toString() + ""
                    val mustUpdate = tData["isMust"].toString() + ""
                    MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE = mustUpdate != "0"
                    showUpdateDialog()
                } else {
                    showToastMsg("已经是最新版本")
                }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData!!["refresh_token"].toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.shareUrl -> getShareData()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }


    /**---------------------------------------------------------------------以下代码申请权限---------------------------------------------
     * Request permissions.
     */


    /**
     * activity销毁前触发的方法
     */
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
    }

    /**
     * activity恢复时触发的方法
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

    }


    inner class MyPlatformActionListener : PlatformActionListener {
        override fun onComplete(platform: Platform, i: Int, hashMap: HashMap<String, Any>) {
            this@AboutUsActivity.runOnUiThread {
                val name = platform.name
                /* if (name.equals(Wechat.NAME)){

                    }else {
                        Toast.makeText(MobSDK.getContext(), "分享成功", Toast.LENGTH_SHORT).show();
                    }*/
            }
        }

        override fun onError(platform: Platform, i: Int, throwable: Throwable) {
            throwable.printStackTrace()
            val error = throwable.toString()
            Log.d("show", "onError ---->  失败" + throwable.stackTrace)
            Log.d("show", "onError ---->  失败" + throwable.message)
            Log.d("show", "onError ---->  失败$i")
            Log.d("show", "onError ---->  失败" + platform.name)
            this@AboutUsActivity.runOnUiThread {
                if (platform.name == "Wechat") {
                    if (!platform.isClientValid) {
                        Toast.makeText(MobSDK.getContext(), "目前您的微信版本过低或未安装微信，需要安装微信才能使用", Toast.LENGTH_SHORT).show()
                    } else if (throwable.message!!.contains("-6")) {
                        Toast.makeText(MobSDK.getContext(), "微信签名授权失败", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(MobSDK.getContext(), "微信分享失败，错误信息：$error", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(MobSDK.getContext(), "分享失败,错误信息：$error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onCancel(platform: Platform, i: Int) {
            this@AboutUsActivity.runOnUiThread { Toast.makeText(MobSDK.getContext(), "分享取消", Toast.LENGTH_SHORT).show() }
        }
    }

}
