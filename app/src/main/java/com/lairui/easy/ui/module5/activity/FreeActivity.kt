package com.lairui.easy.ui.module5.activity

import android.content.Intent
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
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
import com.lairui.easy.mywidget.dialog.UpdateDialog
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.TextViewUtils
import com.lairui.easy.utils.tool.UtilTools
import kotlinx.android.synthetic.main.activity_free.*
import kotlinx.android.synthetic.main.activity_news_item.*
import kotlinx.android.synthetic.main.activity_news_item.contentTv
import java.util.*

/**
 * ,免费体验
 */
class FreeActivity : BasicActivity(), RequestView {
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout



    private var mRequestTag = ""



    private var mMap: MutableMap<String, Any>? = null

    override val contentView: Int
        get() = R.layout.activity_free


    private var mUpdateDialog: UpdateDialog? = null


    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText.text = "免费配资"

       /* contentTv.text = "1. 赠送 2800元实盘资金（完全免费）;\n" +
                "2. 您交88元履约金（结束时如无亏损全额返还，如亏损则扣除亏损剩余返还）;\n" +
                "3. 总共 2888元实盘资金（由您配资，盈利全归您）\n" +
                "4. 盈利全归您, 亏损6个点以内，超过自动平掉,亏损将从履约金中扣除，超出履约金亏损部分由易随配承担;\n" +
                "5. 免费配资资金仅限使用 3个交易日，第3个交易日只能卖出不能买入，如第3个交易日未卖出股票，系统将在14:30后执行自动卖出指令，不保证卖出价格；\n" +
                "6. 亏损6%自动止损。"

        val  content= contentTv.text.toString()
        val textViewUtils = TextViewUtils()
        textViewUtils.init(content,contentTv)
        textViewUtils.setTextColor(content.indexOf("2800元实盘资金") , content.indexOf("（完全免费）"), ContextCompat.getColor(this, R.color.font_c))
        textViewUtils.setTextColor(content.indexOf("88元履约金") , content.indexOf("（结束时如无亏损全额返还"), ContextCompat.getColor(this, R.color.font_c))
        textViewUtils.setTextColor(content.indexOf("2888元实盘资金") , content.indexOf("（由您配资，盈利全归您）"), ContextCompat.getColor(this, R.color.font_c))
        textViewUtils.build()
*/
        getInfoAction()

    }

    private fun getInfoAction() {
        mRequestTag = MethodUrl.PEIZI_FREEMONEY_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] =  MethodUrl.PEIZI_FREEMONEY_INFO
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@FreeActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap,  MethodUrl.PEIZI_FREEMONEY_INFO, map)
    }

    private fun sumbitAction() {
        mRequestTag = MethodUrl.PEIZI_FREEMONEY_APPLY
        val map = HashMap<String, Any>()
        map["nozzle"] =  MethodUrl.PEIZI_FREEMONEY_APPLY
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@FreeActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap,  MethodUrl.PEIZI_FREEMONEY_APPLY, map)
    }


    @OnClick(R.id.back_img, R.id.left_back_lay,R.id.freeTv)
    fun onViewClicked(view: View) {
        var intent: Intent? = null
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.freeTv ->{
                freeTv.isEnabled = false
                sumbitAction()
            }

        }
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



    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        when (mType) {

            MethodUrl.PEIZI_FREEMONEY_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    val mapData = tData["data"] as MutableMap<String,Any>
                    if (!UtilTools.empty(mapData)){
                        tipsTv.text = mapData["tips"].toString()
                        contentTv.movementMethod = LinkMovementMethod.getInstance()
                        contentTv.text = Html.fromHtml(mapData["content"].toString())
                        freeTv.isEnabled = mapData["is_status"].toString() == "0"
                    }

                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@FreeActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.PEIZI_FREEMONEY_APPLY -> when (tData["code"].toString() + "") {
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")

                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@FreeActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }


        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
        when(mType){
            MethodUrl.PEIZI_FREEMONEY_APPLY -> freeTv.isEnabled = true
        }
    }




}
