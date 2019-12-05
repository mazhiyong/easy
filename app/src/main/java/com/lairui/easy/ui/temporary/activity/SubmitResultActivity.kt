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
import com.lairui.easy.ui.module.activity.LoginActivity

/**
 * 提交结果   界面  申请额度   更换手机号
 */
class SubmitResultActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.submit_result_tv)
    lateinit var mSubmitResultTv: TextView
    @BindView(R.id.submit_tip_tv)
    lateinit var mSubmitTipTv: TextView
    @BindView(R.id.but_back)
    lateinit var mButBack: Button


    private var mOpType = 0

    override val contentView: Int
        get() = R.layout.activity_submit_result

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intentBroast = Intent()
        intentBroast.action = MbsConstans.BroadcastReceiverAction.SHOUXIN_UPDATE
        sendBroadcast(intentBroast)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mOpType = bundle.getInt(MbsConstans.ResultType.RESULT_KEY)
        }

        when (mOpType) {
            MbsConstans.ResultType.RESULT_APPLY_MONEY -> {
                mTitleText!!.text = resources.getString(R.string.get_my_num)
                mMyImage!!.setImageResource(R.drawable.wait)
                mButBack!!.text = resources.getString(R.string.but_back)
                mSubmitResultTv!!.text = resources.getString(R.string.submit_success)
                mSubmitTipTv!!.text = resources.getString(R.string.applay_wait_tip)
            }
            MbsConstans.ResultType.RESULT_PHONE_CHANGE -> {
                mTitleText!!.text = resources.getString(R.string.change_phone_num)
                mMyImage!!.setImageResource(R.drawable.finish)
                mButBack!!.text = resources.getString(R.string.but_sure)
                mSubmitResultTv!!.text = resources.getString(R.string.change_phone_success)
                mSubmitTipTv!!.text = resources.getString(R.string.new_phone_login)
            }
        }
    }

    /**
     * 网络连接请求
     */
    private fun submitInstall() {

        val map = HashMap<String, Any>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.installCerSubmit, map)
    }

    @OnClick(R.id.back_img, R.id.but_back, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.left_back_lay, R.id.back_img -> when (mOpType) {
                MbsConstans.ResultType.RESULT_APPLY_MONEY -> finish()
                MbsConstans.ResultType.RESULT_PHONE_CHANGE -> {
                    closeAllActivity()
                    intent = Intent(this@SubmitResultActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.but_back -> when (mOpType) {
                MbsConstans.ResultType.RESULT_APPLY_MONEY -> finish()
                MbsConstans.ResultType.RESULT_PHONE_CHANGE -> {
                    closeAllActivity()
                    intent = Intent(this@SubmitResultActivity, LoginActivity::class.java)
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

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
