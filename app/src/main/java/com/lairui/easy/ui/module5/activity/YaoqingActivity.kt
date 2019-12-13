package com.lairui.easy.ui.module5.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick
import com.jaeger.library.StatusBarUtil
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.UtilTools
import kotlinx.android.synthetic.main.activity_yaoqing_money.*
import java.util.*

/**
 *邀请界面
 */
class YaoqingActivity : BasicActivity(), RequestView {

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

    @BindView(R.id.tipTv)
    lateinit var mTipTv: TextView

    private var mPhone = ""

    private var mRequestTag = ""

    private var mClipboardManager: ClipboardManager? = null
    private var clipData: ClipData? = null


    override val contentView: Int
        get() = R.layout.activity_yaoqing_money

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText.text = "邀请奖励"
        getMsgCodeAction()

        mClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }


    private fun getMsgCodeAction() {

        mRequestTag = MethodUrl.PROMOTION
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.PROMOTION
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@YaoqingActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.PROMOTION, map)
    }


    @OnClick(R.id.back_img, R.id.left_back_lay,R.id.yaoqingRecordLay,R.id.jiangliRecordLay,R.id.copyTextTv,R.id.copyLinkTv)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.yaoqingRecordLay -> {
                val intent = Intent(this@YaoqingActivity,YaoqingListActivity::class.java)
                startActivity(intent)
            }
            R.id.jiangliRecordLay -> {
                val intent = Intent(this@YaoqingActivity,JiangliListActivity::class.java)
                startActivity(intent)
            }
            R.id.copyTextTv ->{
                clipData = ClipData.newPlainText("", yaoQingCodeTv.text.toString())
                mClipboardManager!!.primaryClip = clipData
                showToastMsg("复制成功")
            }
            R.id.copyLinkTv ->{
                clipData = ClipData.newPlainText("", linkTv.text.toString())
                mClipboardManager!!.primaryClip = clipData
                showToastMsg("复制成功")
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
            MethodUrl.PROMOTION -> when (tData["code"].toString() + "") {
                "1" -> {
                    if (!UtilTools.empty(tData["data"])){
                        val mapData = tData["data"] as MutableMap<String, Any>
                        yaoQingCodeTv.text = mapData["code"].toString()
                        linkTv.text = mapData["link_url"].toString()
                        yaoqingMountTv.text = mapData["push_team"].toString()
                        jiangliMountTv.text = mapData["push_total"].toString()

                    }
                }
                "0" -> TipsToast.showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@YaoqingActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }
}
