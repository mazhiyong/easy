package com.lairui.easy.ui.module4.activity

import android.content.Intent
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick
import com.jaeger.library.StatusBarUtil
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.AppDialog
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.TextViewUtils
import com.lairui.easy.utils.tool.UtilTools
import kotlinx.android.synthetic.main.activity_celue_item.*
import java.util.*

/**
 * 策略详情 操盘中
 */
class CeLueItemCurrentActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.titleTv)
    lateinit var mTitleTv: TextView
    @BindView(R.id.timeTv)
    lateinit var mTimeTv: TextView
    @BindView(R.id.typeTv)
    lateinit var mTypeTv: TextView
    @BindView(R.id.celueMoneyTv)
    lateinit var mCelueMoneyTv: TextView
    @BindView(R.id.caopanMoneyTv)
    lateinit var mCaopanMoneyTv: TextView
    @BindView(R.id.addMoneyTv)
    lateinit var mAddMoneyTv: TextView
    @BindView(R.id.extendMoneyTv)
    lateinit var mExtendMoneyTv: TextView
    @BindView(R.id.kuisunTv)
    lateinit var mKuisunTv: TextView
    @BindView(R.id.pingcangTv)
    lateinit var mPingcangTv: TextView
    @BindView(R.id.lixiTv)
    lateinit var mLixiTv: TextView
    @BindView(R.id.zhouqiTv)
    lateinit var mZhouqiTv: TextView
    @BindView(R.id.tiquBt)
    lateinit var mTiquBt: Button
    @BindView(R.id.stopBt)
    lateinit var mStopBt: Button
    @BindView(R.id.extendRecordLay)
    lateinit var mExtendRecordLay: RelativeLayout
    @BindView(R.id.addMoneyRecordLay)
    lateinit var mAddMoneyRecordLay: RelativeLayout
    @BindView(R.id.lixiRecordLay)
    lateinit var mLixiRecordLay: RelativeLayout
    @BindView(R.id.shouyiRecordLay)
    lateinit var mShouyiRecordLay: RelativeLayout



    private var mark = ""
    private var status = ""
    override val contentView: Int
        get() = R.layout.activity_celue_item

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText.text = "策略详情"
        val bundel = intent.extras
        if (bundel == null){
            finish()
        }else{
            mark = bundel.getString("mark")
            status =  bundel.getString("status")
            if (status == "1"){
                //操盘中详情
                typeTv.text = "操盘中"
                typeTv.setTextColor(ContextCompat.getColor(this, R.color.font_c))
                addMoneyTv.visibility = View.VISIBLE
                extendMoneyTv.visibility = View.VISIBLE
                dealLay.visibility = View.VISIBLE
                jiesuanLay.visibility = View.GONE
                liushuiRecordLay.visibility = View.GONE

                detitalAction()
            }else{
                //已结算详情
                typeTv.text = "已结清"
                typeTv.setTextColor(ContextCompat.getColor(this,R.color.black99))
                addMoneyTv.visibility = View.GONE
                extendMoneyTv.visibility = View.GONE
                dealLay.visibility = View.GONE
                jiesuanLay.visibility = View.VISIBLE
                liushuiRecordLay.visibility = View.VISIBLE

                historyDetitalAction()
            }
        }

        myDashBoard.cgangePer(0.35f)

    }


    private fun detitalAction() {

        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.DETAILED_IFFO
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@CeLueItemCurrentActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.DETAILED_IFFO, map)
    }

    private fun historyDetitalAction() {

        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.HISTORY_DETAILED_IFFO
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@CeLueItemCurrentActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.HISTORY_DETAILED_IFFO, map)
    }

    private fun stopAction() {

        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.PEIZI_JIESUAN
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@CeLueItemCurrentActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.PEIZI_JIESUAN, map)
    }

    @OnClick(R.id.back_img, R.id.left_back_lay,R.id.addMoneyTv,R.id.extendMoneyTv,R.id.tiquBt,R.id.stopBt,R.id.addMoneyRecordLay,R.id.extendRecordLay,R.id.lixiRecordLay,R.id.shouyiRecordLay,R.id.liushuiRecordLay)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.left_back_lay, R.id.back_img -> finish()
            R.id.addMoneyTv -> { //追加保证金
                intent = Intent(this@CeLueItemCurrentActivity,AddMoneyActivity::class.java)
                intent.putExtra("mark",mark)
                startActivity(intent)
            }
            R.id.extendMoneyTv -> { //扩大配资
                intent = Intent(this@CeLueItemCurrentActivity,ExtendMoneyActivity::class.java)
                intent.putExtra("mark",mark)
                startActivity(intent)
            }
            R.id.tiquBt -> {
                intent = Intent(this@CeLueItemCurrentActivity,TixuMoneyActivity::class.java)
                intent.putExtra("mark",mark)
                startActivity(intent)
            }
            R.id.stopBt -> {
                val dialog = AppDialog(this@CeLueItemCurrentActivity,true)
                dialog.initValue("终止操盘须知","请确保账户内已经全部清仓完 否则我们将有权把您的股票进行平仓处理","取消","确定")
                dialog.setClickListener(View.OnClickListener { v ->
                    when (v.id) {
                        R.id.cancel -> dialog.dismiss()
                        R.id.confirm -> {
                            stopAction()
                            dialog.dismiss()
                        }
                    }
                })

                dialog.show()
            }
            R.id.addMoneyRecordLay-> {
                intent = Intent(this@CeLueItemCurrentActivity,RecordListActivity::class.java)
                intent.putExtra("TYPE","2")
                intent.putExtra("mark",mark)
                startActivity(intent)
            }
            R.id.extendRecordLay-> {
                intent = Intent(this@CeLueItemCurrentActivity,RecordListActivity::class.java)
                intent.putExtra("TYPE","3")
                intent.putExtra("mark",mark)
                startActivity(intent)
            }
            R.id.lixiRecordLay-> {
                intent = Intent(this@CeLueItemCurrentActivity,RecordListActivity::class.java)
                intent.putExtra("TYPE","5")
                intent.putExtra("mark",mark)
                startActivity(intent)
            }
            R.id.shouyiRecordLay-> {
                intent = Intent(this@CeLueItemCurrentActivity,RecordListActivity::class.java)
                intent.putExtra("TYPE","4")
                intent.putExtra("mark",mark)
                startActivity(intent)
            }
            R.id.liushuiRecordLay-> {
                intent = Intent(this@CeLueItemCurrentActivity,RecordListActivity::class.java)
                intent.putExtra("TYPE","6")
                intent.putExtra("mark",mark)
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
        when (mType) {
            MethodUrl.DETAILED_IFFO -> when (tData["code"].toString()) {
                "1" -> {
                    if (tData["data"].toString().isNotEmpty()){
                        val mapData = tData["data"] as MutableMap<String,Any>
                        titleTv.text = mapData["name"].toString()
                        timeTv.text = mapData["time"].toString()
                        celueMoneyTv.text = mapData["bond"].toString()+"元"
                        caopanMoneyTv.text = mapData["total"].toString()+"元"
                        kuisunTv.text = mapData["warning"].toString()+"元"
                        pingcangTv.text = mapData["close"].toString()+"元"
                        lixiTv.text = mapData["manage"].toString()+"元"
                        if (mapData["type"].toString() == "1"){
                            zhouqiTv.text = mapData["day"].toString()+"天"
                        }else{
                            zhouqiTv.text = mapData["day"].toString()+"月"
                        }
                        shizhiTv.text = mapData["market"].toString()+"元"
                        biliTv.text = mapData["depot_ratio"].toString()
                        yingkuiMoneyTv.text = mapData["profit_loss"].toString()+"元"
                        yingkuiLvTv.text = mapData["ratio"].toString()

                        kuisunRedTv.text = "亏损警戒线: "+  mapData["warning"].toString()
                        pingcangTvGreendTv.text = "亏损平仓线: "+mapData["close"].toString()
                    }

                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@CeLueItemCurrentActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            MethodUrl.HISTORY_DETAILED_IFFO-> when (tData["code"].toString()) {
                "1" -> {
                    if (tData["data"].toString().isNotEmpty()){
                        val mapData = tData["data"] as MutableMap<String,Any>
                        titleTv.text = mapData["name"].toString()
                        timeTv.text = mapData["time"].toString()
                        celueMoneyTv.text = mapData["bond"].toString()+" 元"
                        caopanMoneyTv.text = mapData["total"].toString()+" 元"
                        kuisunTv.text = mapData["warning"].toString()+" 元"
                        pingcangTv.text = mapData["close"].toString()+" 元"
                        lixiTv.text = mapData["manage"].toString()+" 元"
                        if (mapData["type"].toString() == "1"){
                            zhouqiTv.text = mapData["day"].toString()+"天"
                        }else{
                            zhouqiTv.text = mapData["day"].toString()+"月"
                        }
                        shizhiTv.text = mapData["market"].toString()+" 元"
                        biliTv.text = mapData["depot_ratio"].toString()
                        yingkuiMoneyTv.text = mapData["profit_loss"].toString()+" 元"
                        yingkuiLvTv.text = mapData["ratio"].toString()
                        dongjieMoneyTv.text = mapData["frozen"].toString()+" 元"
                        koujianMoneyTv.text = mapData["deduction"].toString()+" 元"
                        jiesuanMoneyTv.text = mapData["thaw"].toString()+" 元"

                        kuisunRedTv.text = "亏损警戒线: "+  mapData["warning"].toString()
                        pingcangTvGreendTv.text = "亏损平仓线: "+mapData["close"].toString()


                    }

                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@CeLueItemCurrentActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            MethodUrl.PEIZI_JIESUAN -> when (tData["code"].toString()) {
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")
                    intent = Intent()
                    intent.action = MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE
                    sendBroadcast(intent)
                }
                "0" -> {
                    showToastMsg(tData["msg"].toString() + "")
                }
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@CeLueItemCurrentActivity, LoginActivity::class.java)
                    startActivity(intent)
                }

            }


        }

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }


}
