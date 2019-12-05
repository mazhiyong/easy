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

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 审核失败后，自己手动提交审核
 */
class IdCardMyActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.name_edit)
    lateinit var mNameEdit: EditText
    @BindView(R.id.id_num_edit)
    lateinit var mIdNumEdit: EditText

    private var mRequestTag = ""


    override val contentView: Int
        get() = R.layout.activity_idcard_my

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.id_card_check)
        mLeftBackLay!!.visibility = View.GONE
        getAuthInfoAction()
    }


    /**
     * 最近一次认证信息
     */
    private fun getAuthInfoAction() {

        mRequestTag = MethodUrl.laseAuthInfo
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.laseAuthInfo, map)
    }

    private fun butAction() {
        if (UtilTools.isEmpty(mNameEdit!!, "姓名")) {
            showToastMsg("姓名不能为空")
            return
        }
        if (UtilTools.isEmpty(mIdNumEdit!!, "身份证号")) {
            showToastMsg("身份证号不能为空")
            return
        }

        val intent = Intent(this@IdCardMyActivity, IdCardMyPicActivity::class.java)
        intent.putExtra("idname", mNameEdit!!.text.toString() + "")
        intent.putExtra("idno", mIdNumEdit!!.text.toString() + "")
        startActivity(intent)
    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> butAction()
        }
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {//
        val intent: Intent
        when (mType) {
            MethodUrl.laseAuthInfo//{idname=刘英超, idno=410725199103263616}
            -> {
                val name = tData["idname"]!!.toString() + ""
                val idNum = tData["idno"]!!.toString() + ""
                mNameEdit!!.setText(name)
                mIdNumEdit!!.setText(idNum)
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.laseAuthInfo -> getAuthInfoAction()
                }
            }
        }

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }

}
