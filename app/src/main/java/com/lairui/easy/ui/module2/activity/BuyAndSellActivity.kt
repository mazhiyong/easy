package com.lairui.easy.ui.module2.activity

import android.content.Intent

import androidx.core.content.ContextCompat
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.RegexUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.utils.tool.TextViewUtils
import kotlinx.android.synthetic.main.activity_buyandsell.*

/**
 * 买入  卖出
 */
class BuyAndSellActivity : BasicActivity(), RequestView {


    private var mPhone = ""

    private var mRequestTag = ""
    override val contentView: Int
        get() = R.layout.activity_buyandsell

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, resources.getDimension(R.dimen.title_item_height).toInt() + UtilTools.getStatusHeight2(this))
        title_bar_view.layoutParams = layoutParams
        title_bar_view.setPadding(0, UtilTools.getStatusHeight2(this), 0, 0)

        mTabLayout.addTab(mTabLayout.newTab().setText("买入"))
        mTabLayout.addTab(mTabLayout.newTab().setText("卖出"))

        tlTradeTab.addTab(tlTradeTab.newTab().setText("持仓"))
        tlTradeTab.addTab(tlTradeTab.newTab().setText("撤单"))
        tlTradeTab.addTab(tlTradeTab.newTab().setText("委托"))
        tlTradeTab.addTab(tlTradeTab.newTab().setText("成交"))



    }


    private fun getMsgCodeAction() {

        mRequestTag = MethodUrl.resetPassCode
        val map = HashMap<String, Any>()
        map["tel"] = mPhone
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.resetPassCode, map)
    }


    @OnClick(R.id.back_img, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()

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
            MethodUrl.resetPassCode -> {
               /* showToastMsg("获取验证码成功")
                intent = Intent(this@AddMoneyActivity, CodeMsgActivity::class.java)
                intent.putExtra(MbsConstans.CodeType.CODE_KEY, MbsConstans.CodeType.CODE_RESET_LOGIN_PASS)
                intent.putExtra("DATA", tData as Serializable)
                intent.putExtra("phonenum", mPhone + "")
                intent.putExtra("showPhone", UtilTools.getPhoneXing(mPhone))
                startActivity(intent)*/
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.resetPassCode -> getMsgCodeAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }
}
