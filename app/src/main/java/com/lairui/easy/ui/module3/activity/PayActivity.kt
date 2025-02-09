package com.lairui.easy.ui.module3.activity

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
import com.lairui.easy.manage.ActivityManager
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.ui.module.activity.MainActivity
import com.lairui.easy.ui.module.activity.XieYiDetialActivity
import com.lairui.easy.ui.module5.activity.PayWayActivity
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.TextViewUtils
import com.lairui.easy.utils.tool.UtilTools
import kotlinx.android.synthetic.main.activity_pay.*
import java.util.ArrayList

/**
 * 配置 支付
 */
class PayActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.but_back)
    lateinit var mButBack: Button


    private var mOpType = 0
    private var total = 0.0

    var type =""
    var bound =""
    var multiple = ""
    override val contentView: Int
        get() = R.layout.activity_pay

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        val  bundle = intent.extras
        if (bundle != null){
            bound = bundle["bound"].toString()
            type = bundle["type"].toString()
            multiple = bundle["multiple"].toString()
            boundTv.text ="¥ "+UtilTools.getNormalMoney(bundle["bound"].toString())
            lixiTv.text ="¥ "+UtilTools.getNormalMoney(bundle["lixi"].toString())
            total = (bundle["bound"].toString().toInt()+bundle["lixi"].toString().toFloat()).toDouble()
            payTv.text = "¥ "+UtilTools.getNormalMoney(total.toString())

            when(type){
                "0" ->{
                    timeTv.text = "1天"
                }
                "1" ->{
                    timeTv.text = "1月"
                }
                "2" ->{
                    timeTv.text = "20天"
                }
            }
        }else{
            finish()
        }

        if (UtilTools.empty(MbsConstans.USER_MAP)) {
            val s = SPUtils!![this, MbsConstans.SharedInfoConstans.LOGIN_INFO, ""].toString()
            MbsConstans.USER_MAP = JSONUtil.instance.jsonMap(s)
        }
        val chongzhiMoney  =  total - ((MbsConstans.USER_MAP!!["account"] as String).toDouble() +1000)
        if (chongzhiMoney > 0){
            tipTv.text = "您的账户余额"+MbsConstans.USER_MAP!!["account"]+"元,赠送金额1000元,还需支付"+chongzhiMoney+"元,请前往充值"

            val textViewUtils = TextViewUtils()
            val s =tipTv.text.toString()
            textViewUtils.init(s, tipTv)
            textViewUtils.setTextColor(s.indexOf("前"), s.length, ContextCompat.getColor(this, R.color.btn_blue_normal))
            textViewUtils.setTextClick(s.indexOf("前"), s.length, object : TextViewUtils.ClickCallBack {
                override fun onClick() {
                    // 充值
                    val intent = Intent(this@PayActivity, PayWayActivity::class.java)
                    startActivity(intent)
                }

            })
            textViewUtils.build()
        }


        mTitleText.text = "确认支付"
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
            R.id.left_back_lay, R.id.back_img -> finish()
            R.id.but_back ->  {
                when(type){
                    "0" ->{
                        payAction()
                    }
                    "1" ->{
                        payAction2()
                    }
                    "2" ->{
                        payAction3()
                    }
                }

                }
            }
        }

    private fun payAction() {
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.PEIZI_DAY_APPLY
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@PayActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["bond"] = bound
        map["multiple"] = multiple
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.PEIZI_DAY_APPLY, map)
    }

    private fun payAction2() {
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.PEIZI_MONTH_APPLY
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@PayActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["bond"] = bound
        map["multiple"] = multiple
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.PEIZI_MONTH_APPLY, map)
    }

    private fun payAction3() {
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.PEIZI_FREE_APPLY
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@PayActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["bond"] = bound
        map["multiple"] = multiple
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.PEIZI_FREE_APPLY, map)
    }




    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        when(mType){
            MethodUrl.PEIZI_DAY_APPLY -> when (tData["code"].toString() + "") {
                "1" -> {
                    TipsToast.showToastMsg(tData["msg"].toString() + "")
                    intent = Intent()
                    intent.action = MbsConstans.BroadcastReceiverAction.INTENT_AND_UPDATE
                    sendBroadcast(intent)
                    finish()

                  /*  val  activity = ActivityManager.instance.currentActivity() as MainActivity
                    activity.toTradeFragment()*/

                }
                "0" -> TipsToast.showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@PayActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            MethodUrl.PEIZI_MONTH_APPLY -> when (tData["code"].toString() + "") {
                "1" -> {
                    TipsToast.showToastMsg(tData["msg"].toString() + "")
                    intent = Intent()
                    intent.action = MbsConstans.BroadcastReceiverAction.INTENT_AND_UPDATE
                    sendBroadcast(intent)

                    finish()
                   /* val  activity = ActivityManager.instance.currentActivity() as MainActivity
                    activity.toTradeFragment()*/

                }
                "0" -> TipsToast.showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@PayActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.PEIZI_FREE_APPLY -> when (tData["code"].toString() + "") {
                "1" -> {
                    TipsToast.showToastMsg(tData["msg"].toString() + "")
                    intent = Intent()
                    intent.action = MbsConstans.BroadcastReceiverAction.INTENT_AND_UPDATE
                    sendBroadcast(intent)

                    finish()
                   /* val  activity = ActivityManager.instance.currentActivity() as MainActivity
                    activity.toTradeFragment()*/


                }
                "0" -> TipsToast.showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@PayActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }


        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
