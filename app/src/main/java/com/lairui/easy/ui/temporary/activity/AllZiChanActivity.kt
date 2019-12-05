package com.lairui.easy.ui.temporary.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView

import android.content.IntentFilter
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mywidget.dialog.ZhanghuListDialog
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 总资产   界面
 */
class AllZiChanActivity : BasicActivity(), RequestView, SelectBackListener {

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
    @BindView(R.id.title_bar_view)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.money_tv)
    lateinit var mMoneyTv: TextView
    @BindView(R.id.keyong_money_tv)
    lateinit var mKeyongMoneyTv: TextView
    @BindView(R.id.dongjie_money_tv)
    lateinit var mDongjieMoneyTv: TextView
    @BindView(R.id.chongzhi_lay)
    lateinit var mChongzhiLay: CardView
    @BindView(R.id.tixian_lay)
    lateinit var mTixianLay: CardView
    @BindView(R.id.hezuo_tv)
    lateinit var mHezuoTv: TextView


    private var mRequestTag = ""

    private lateinit var mZhangHuMap: MutableMap<String, Any>


    private lateinit var mZhuangHuList: MutableList<MutableMap<String,Any>>
    override val contentView: Int
        get() = R.layout.activity_all_zichan


    lateinit var mSimpleCustomPop: ZhanghuListDialog


    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == MbsConstans.BroadcastReceiverAction.MONEY_UPDATE) {
                getMoneyAction()
            }
        }
    }

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, R.color.font_c), 0);

        StatusBarUtil.setTranslucentForImageView(this, MbsConstans.ALPHA, null)

        val layoutParams = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, resources.getDimension(R.dimen.title_item_height).toInt() + UtilTools.getStatusHeight2(this))
        mTitleBarView!!.layoutParams = layoutParams
        mTitleBarView!!.setPadding(0, UtilTools.getStatusHeight2(this), 0, 0)

        val intentFilter = IntentFilter()
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.MONEY_UPDATE)
        registerReceiver(mBroadcastReceiver, intentFilter)

        //mTitleBarView.setBackgroundResource(R.color.font_c);
        mTitleText!!.text = resources.getString(R.string.all_zichan)
        mTitleText!!.setTextColor(ContextCompat.getColor(this, R.color.white))
        mBackImg!!.setImageResource(R.drawable.write_back)
        mBackText!!.text = ""
        getMoneyAction()
    }

    override fun setBarTextColor() {
        StatusBarUtil.setDarkMode(this)
    }


    /**
     * 查询账户列表
     */
    private fun zhanghuAction() {
        mRequestTag = MethodUrl.zhanghuList

        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.zhanghuList, map)
    }

    /**
     * 请求资产信息（账号资金，可用资金，冻结资金）
     */
    private fun getMoneyAction() {
        //查询类型(acct:资金账户,card:银行卡)
        mRequestTag = MethodUrl.allZichan
        val map = HashMap<String, String>()
        map["qry_type"] = "acct"
        map["accid"] = ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.allZichan, map)
    }


    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        when (mType) {
            //获取合作商的信息
            MethodUrl.zhanghuList//
            -> {
                val result = tData["result"].toString() + ""
                if (UtilTools.empty(result)) {
                } else {
                    val list = JSONUtil.instance.jsonToList(result)
                    list as MutableList<MutableMap<String,Any>>
                    if (list != null) {
                        mZhuangHuList!!.clear()
                        mZhuangHuList!!.addAll(list)
                    } else {
                    }
                }
                initList()
            }
            MethodUrl.allZichan -> {
                val money = UtilTools.getRMBMoney(tData["bal_amt"].toString() + "")
                val yue = UtilTools.getRMBMoney(tData["use_amt"].toString() + "")
                val dongjie = UtilTools.getRMBMoney(tData["frz_amt"].toString() + "")

                mMoneyTv!!.text = money
                mKeyongMoneyTv!!.text = yue
                mDongjieMoneyTv!!.text = dongjie
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"].toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.zhanghuList -> zhanghuAction()
                    MethodUrl.allZichan -> getMoneyAction()
                }
            }
        }// getMoneyAction();
    }


    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }

    private fun initList() {
        mSimpleCustomPop = ZhanghuListDialog(this@AllZiChanActivity, mZhuangHuList, 110)
        mSimpleCustomPop!!.selectBackListener = this
        //mSimpleCustomPop.initData(mZhuangHuList);
        if (mZhuangHuList != null && mZhuangHuList!!.size > 0) {
            mZhangHuMap = mZhuangHuList!![0]
            if (UtilTools.empty(mZhangHuMap!!["orgname"]!!.toString() + "")) {
                mZhangHuMap!!["orgname"] = "暂无合作方"
                mHezuoTv!!.text = "暂无合作方"
            } else {
                mHezuoTv!!.text = mZhangHuMap!!["orgname"]!!.toString() + ""
            }
            getMoneyAction()
        }
    }

    private fun showList() {

        mSimpleCustomPop!!
                //.alignCenter(true)
                .anchorView(mHezuoTv)
                .gravity(Gravity.BOTTOM)
                //.showAnim(new SlideTopEnter())
                //.dismissAnim(new SlideTopExit())
                .offset(0f, 10f)
                .dimEnabled(false)
                .show()
    }


    @OnClick(R.id.back_img, R.id.chongzhi_lay, R.id.tixian_lay, R.id.hezuo_tv, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.chongzhi_lay -> {
                intent = Intent(this, ChongzhiTestActivity::class.java)
                intent.putExtra("TYPE", 100)
                startActivity(intent)
            }
            R.id.tixian_lay -> {
                intent = Intent(this, TiXianActivity::class.java)
                intent.putExtra("TYPE", 200)
                startActivity(intent)
            }
            R.id.hezuo_tv -> showList()
        }
    }

    override  fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            110 -> {
                map as MutableMap<String,Any>
                mZhangHuMap = map
                mHezuoTv!!.text = mZhangHuMap!!["orgname"]!!.toString() + ""
                getMoneyAction()
            }
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }
}
