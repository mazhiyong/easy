package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.utils.tool.TextViewUtils
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.HashMap

import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick

/**
 * 发票   界面
 */
class InvoiceActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.login_phone_tv)
    lateinit var mLoginPhoneTv: TextView

    @BindView(R.id.shoudong_tv)
    lateinit var mShouDongTv: TextView

    private var mRequestTag = ""


    override val contentView: Int
        get() = R.layout.activity_invoice

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mLoginPhoneTv!!.text = MbsConstans.USER_MAP!!["tel"]!!.toString() + ""
        mTitleText!!.text = resources.getString(R.string.invoice_title)


        val content = mShouDongTv!!.text.toString().trim { it <= ' ' }
        val textViewUtils = TextViewUtils()
        textViewUtils.init(content, mShouDongTv!!)
        textViewUtils.setTextColor(content.indexOf("？") + 1, content.length, ContextCompat.getColor(this, R.color.font_c))
        textViewUtils.build()
    }

    private fun getMsgCodeAction() {

        mRequestTag = MethodUrl.changePhoneMsgCode
        val map = HashMap<String, Any>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.changePhoneMsgCode, map)
    }


    @OnClick(R.id.back_img, R.id.but_checkl, R.id.left_back_lay, R.id.shoudong_tv)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_checkl -> PermissionsUtils.requsetRunPermission(this@InvoiceActivity, object : RePermissionResultBack {
                override fun requestSuccess() {
                    val intent = Intent(this@InvoiceActivity, TestScanActivity::class.java)
                    intent.putExtra("type", "2")
                    startActivity(intent)
                }

                override fun requestFailer() {

                }
            }, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA)
            R.id.shoudong_tv -> {
                intent = Intent(this@InvoiceActivity, InvoiceAddActivity::class.java)
                startActivity(intent)
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
            MethodUrl.changePhoneMsgCode -> {
                showToastMsg("获取验证码成功")
                intent = Intent(this@InvoiceActivity, CodeMsgActivity::class.java)
                intent.putExtra("DATA", tData as Serializable)
                intent.putExtra(MbsConstans.CodeType.CODE_KEY, MbsConstans.CodeType.CODE_PHONE_CHANGE)
                intent.putExtra("showPhone", MbsConstans.USER_MAP!!["tel"]!!.toString() + "")
                startActivity(intent)
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.changePhoneMsgCode -> getMsgCodeAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }
}
