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
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 身份证认证初始界面   界面
 */
class IdCardCheckActivity : BasicActivity(), RequestView {

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
    lateinit var mButCheck: Button

    private var mRequestTag = ""

    private lateinit var mAuthTimesMap: MutableMap<String, Any>
    override val contentView: Int
        get() = R.layout.activity_idcard_check


    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        //mBackImg.setVisibility(View.GONE);
        //mLeftBackLay.setVisibility(View.GONE);
        mTitleText!!.text = resources.getString(R.string.id_card_check)

    }

    /**
     * 获取用户认证信息
     */
    private fun getAuthInfoAction() {
        mRequestTag = MethodUrl.userAuthInfo

        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.userAuthInfo, map)
    }

    override fun onResume() {
        super.onResume()
        getAuthInfoAction()
    }

    @OnClick(R.id.back_img, R.id.but_checkl, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_checkl -> if (mAuthTimesMap == null || mAuthTimesMap!!.isEmpty()) {
                getAuthInfoAction()
            } else {
                val authTimes = Integer.valueOf(mAuthTimesMap!!["auth_times"]!!.toString() + "")
                if (authTimes >= 3) {
                    intent = Intent(this@IdCardCheckActivity, IdCardMyActivity::class.java)
                    startActivity(intent)
                } else {
                    intent = Intent(this@IdCardCheckActivity, IdCardPicActivity::class.java)
                    startActivity(intent)
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
            MethodUrl.userAuthInfo -> {
                mAuthTimesMap = tData
                val authTimes = Integer.valueOf(tData["auth_times"]!!.toString() + "")
                mButCheck!!.text = "开始认证($authTimes/3)"
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.userAuthInfo -> getAuthInfoAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }
}
