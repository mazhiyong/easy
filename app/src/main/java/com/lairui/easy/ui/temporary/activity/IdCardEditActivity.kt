package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 手动输入身份证界面   界面
 */
class IdCardEditActivity : BasicActivity(), RequestView {


    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.idcard_edit_tip)
    lateinit var mIdcardEditTip: TextView
    @BindView(R.id.idcard_edit)
    lateinit var mIdCardEdit: EditText
    @BindView(R.id.but_next)
    lateinit var mButNext: Button
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView

    private var mOpType: String? = ""

    private var mRequestTag = ""
    private var mIdNum = ""

    override val contentView: Int
        get() = R.layout.activity_idcard_edit

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mOpType = bundle.getString(MbsConstans.CodeType.CODE_KEY)
        }

        mTitleText!!.text = resources.getString(R.string.id_card_check2)
    }


    private fun getMsgCodeAction() {

        mRequestTag = MethodUrl.installCode
        val map = HashMap<String, Any>()
        map["idno"] = mIdNum
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.installCode, map)
    }

    @OnClick(R.id.back_img, R.id.left_back_lay, R.id.but_next)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> {
                mIdNum = mIdCardEdit!!.text.toString() + ""
                if (UtilTools.isEmpty(mIdCardEdit!!, "身份证号")) {
                    showToastMsg("身份证号不能为空")
                    return
                }
                getMsgCodeAction()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data)

        var intent: Intent
        if (requestCode == 1) {
            when (resultCode) {
                //通过短信验证码  安装证书
                MbsConstans.CodeType.CODE_INSTALL -> {
                    var authCode: String? = ""
                    val bundle = data!!.extras
                    if (bundle != null) {
                        authCode = bundle.getString("authcode")
                    }
                    intent = Intent()
                    intent.putExtra("authcode", authCode)
                    setResult(MbsConstans.CodeType.CODE_INSTALL, intent)
                    finish()
                }
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
            MethodUrl.installCode -> {
                showToastMsg("获取验证码成功")
                intent = Intent(this@IdCardEditActivity, CodeMsgActivity::class.java)
                intent.putExtra("DATA", tData as Serializable)
                intent.putExtra(MbsConstans.CodeType.CODE_KEY, MbsConstans.CodeType.CODE_INSTALL)
                intent.putExtra("idno", mIdNum)
                startActivityForResult(intent, 1)
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.installCode -> getMsgCodeAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }
}
