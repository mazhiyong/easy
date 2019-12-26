package com.lairui.easy.ui.module5.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import butterknife.BindView
import butterknife.Unbinder
import com.jaeger.library.StatusBarUtil
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicFragment
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.SureOrNoDialog
import com.lairui.easy.mywidget.pulltozoomview.PullToZoomScrollViewEx
import com.lairui.easy.mywidget.view.LoadingWindow
import com.lairui.easy.mywidget.view.TipsToast.Companion.showToastMsg
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.ui.module1.activity.NoticeListActivity
import com.lairui.easy.ui.module4.activity.RecordListActivity
import com.lairui.easy.ui.module5.activity.*
import com.lairui.easy.utils.imageload.GlideUtils
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.UtilTools
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_circle_view2.*
import java.util.*

class PersonFragment : BasicFragment(), View.OnClickListener, RequestView {

    lateinit var headView: View
    lateinit var zoomView: View
    lateinit var contentView: View

    @BindView(R.id.person_scroll_view)
    lateinit var mPersonScrollView: PullToZoomScrollViewEx

    lateinit var mRenZhengLay: CardView
    lateinit var mBankCardLay: CardView
    lateinit var mYaoQingLay: CardView
    lateinit var mTradeLay: CardView
    lateinit var mAccountLay: CardView
    lateinit var mHelpLay: CardView
    lateinit var mMessageLay: CardView
    lateinit var mNoticeLay: CardView
    @BindView(R.id.back_view)
    lateinit var mBackView: ImageView
    @BindView(R.id.titleText)
    lateinit var mTitleText: TextView
    @BindView(R.id.personal_more_button)
    lateinit var mPersonalMoreButton: ImageView

    @BindView(R.id.index_top_layout_person)
    lateinit var mIndexTopLayoutPerson: LinearLayout
    lateinit var unbinder: Unbinder


    private lateinit var mZoomImage: ImageView
    private lateinit var mRoundImageView: CircleImageView
    private lateinit var mUserName: TextView
    private lateinit var mPeiziMoneyTv: TextView
    private lateinit var mDongjieMoneyTv: TextView
    private lateinit var mBaozhengMoneyTv: TextView
    private lateinit var mAviableMoneyTv: TextView
    private lateinit var mAccountTotal: TextView
    private lateinit var mBankMoneyTv: TextView
    private lateinit var mBankCardTv: TextView
    private lateinit var mShowYueLay: RelativeLayout
    private lateinit var mToggleButton: ToggleButton
    private lateinit var mShowMoneyTv: TextView
    private lateinit var mChongZhiTv: TextView
    private lateinit var mTixianTv: TextView
    private lateinit var mKefuIv: ImageView
    private lateinit var mExitTv: TextView


    private var mRequestTag = ""
    private lateinit var mLoadingWindow: LoadingWindow


    private lateinit var mBankInfo: MutableMap<String, Any>
    private lateinit var mMoneyInfo: MutableMap<String, Any>
    private lateinit var mShareMap: MutableMap<String, Any>

    override val layoutId: Int

        get() = R.layout.fragment_person

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE) {
                getUserInfoAction()
            } else if (action == MbsConstans.BroadcastReceiverAction.MONEY_UPDATE) {
                //
            }
        }
    }

    override fun init() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE)
        activity!!.registerReceiver(mBroadcastReceiver, intentFilter)

        mLoadingWindow = LoadingWindow(activity!!, R.style.Dialog)
        val layoutParams = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, activity!!.resources.getDimension(R.dimen.title_item_height).toInt() + UtilTools.getStatusHeight2(activity!!))
        mIndexTopLayoutPerson.layoutParams = layoutParams
        mIndexTopLayoutPerson.setPadding(0, UtilTools.getStatusHeight2(activity!!), 0, 0)
        mTitleText.text = resources.getString(R.string.bottom_person)

        //StatusBarUtil.setColor(getActivity(), ContextCompat.getColor(getActivity(), R.color.transparent), 0);
        headView = LayoutInflater.from(activity).inflate(R.layout.profile_head_view, null, false)
        zoomView = LayoutInflater.from(activity).inflate(R.layout.profile_zoom_view, null, false)
        contentView = LayoutInflater.from(activity).inflate(R.layout.profile_content_view, null, false)
        mZoomImage = zoomView.findViewById<View>(R.id.iv_zoom) as ImageView
        //  Glide.with(this).load(R.drawable.per_bg).into(mZoomImage);
        //Glide.with(this).load(R.drawable.per_bg).into(mZoomImage);
        mToggleButton = headView.findViewById(R.id.togglePwd)
        mChongZhiTv = headView.findViewById(R.id.chongzhiTv)
        mChongZhiTv.setOnClickListener(this)
        mTixianTv = headView.findViewById(R.id.tixianTv)
        mTixianTv.setOnClickListener(this)
        mShowYueLay = headView.findViewById(R.id.show_yue_lay)
        mShowYueLay.setOnClickListener(this)
        mKefuIv = headView.findViewById(R.id.headKefuIv)
        mKefuIv.setOnClickListener(this)



        mPersonScrollView.headerView = headView
        mPersonScrollView.zoomView = zoomView
        mPersonScrollView.setScrollContentView(contentView)

        mRenZhengLay = contentView.findViewById(R.id.renZhengLay)
        mRenZhengLay.setOnClickListener(this)
        mBankCardLay = contentView.findViewById(R.id.bankCardLay)
        mBankCardLay.setOnClickListener(this)
        mYaoQingLay = contentView.findViewById(R.id.yaoQingLay)
        mYaoQingLay.setOnClickListener(this)
        mTradeLay = contentView.findViewById(R.id.trade_Lay)
        mTradeLay.setOnClickListener(this)
        mAccountLay = contentView.findViewById(R.id.account_Lay)
        mAccountLay.setOnClickListener(this)
        mHelpLay = contentView.findViewById(R.id.activity_Lay)
        mHelpLay.setOnClickListener(this)
        mMessageLay = contentView.findViewById(R.id.free_Lay)
        mMessageLay.setOnClickListener(this)
        mNoticeLay = contentView.findViewById(R.id.notice_Lay)
        mNoticeLay.setOnClickListener(this)
        mExitTv = contentView.findViewById(R.id.exitTv)
        mExitTv.setOnClickListener(this)



        mPersonalMoreButton.setOnClickListener(this)


        mUserName = mPersonScrollView.findViewById<View>(R.id.userAccount_Tv) as TextView
        mAccountTotal = mPersonScrollView.findViewById<View>(R.id.account_total_tv) as TextView
        mPeiziMoneyTv = mPersonScrollView.findViewById<View>(R.id.peiziMoneyTv) as TextView
        mDongjieMoneyTv = mPersonScrollView.findViewById<View>(R.id.dongjieMoneyTv) as TextView
        mBaozhengMoneyTv = mPersonScrollView.findViewById<View>(R.id.baozhengMoneyTv) as TextView
        mAviableMoneyTv = mPersonScrollView.findViewById<View>(R.id.avaiableMoneyTv) as TextView

        mRoundImageView = mPersonScrollView.findViewById<View>(R.id.headImageIv) as CircleImageView
        mRoundImageView.setOnClickListener(this)



        mToggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (UtilTools.empty(MbsConstans.USER_MAP)) {
                    val s = SPUtils!![activity!!, MbsConstans.SharedInfoConstans.LOGIN_INFO, ""].toString()
                    MbsConstans.USER_MAP = JSONUtil.instance.jsonMap(s)
                }
                mAccountTotal.text = MbsConstans.USER_MAP!!["total"] as String
                mPeiziMoneyTv.text = MbsConstans.USER_MAP!!["with_capital"] as String
                mDongjieMoneyTv.text = MbsConstans.USER_MAP!!["frozen"] as String
                mBaozhengMoneyTv.text = MbsConstans.USER_MAP!!["bond"] as String
                mAviableMoneyTv.text = MbsConstans.USER_MAP!!["account"] as String

            } else {
                mAccountTotal.text = "*******"
                mPeiziMoneyTv.text = "***"
                mDongjieMoneyTv.text = "***"
                mBaozhengMoneyTv.text = "***"
                mAviableMoneyTv.text = "***"
            }
        }
        setBarTextColor()

        getUserInfoAction()

    }

    fun setBarTextColor() {
        StatusBarUtil.setLightMode(activity!!)
    }


    override fun onResume() {
        super.onResume()
        getUserInfoAction()
    }

    /**
     * 获取用户信息
     */
    private fun getUserInfoAction() {
        mRequestTag = MethodUrl.ACCOUNT_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.ACCOUNT_INFO
         if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[activity!!, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.ACCOUNT_INFO, map)
    }

    /**
     * 获取分享内容
     */
    fun getShareData() {
        mRequestTag = MethodUrl.shareUrl
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToMap(mHeaderMap, MethodUrl.shareUrl, map)
    }

    /**
     * 获取默认银行卡信息
     */
    private fun getCardInfoAction() {
        mRequestTag = MethodUrl.bankCard
        val map = HashMap<String, String>()
        map["isdefault"] = "1"
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.bankCard, map)
    }

    /**
     * 查询余额
     */
    private fun getCardMoney() {
        mRequestTag = MethodUrl.allZichan
        val map = HashMap<String, String>()
        map["qry_type"] = "acct"
        map["accid"] = ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToMap(mHeaderMap, MethodUrl.allZichan, map)
    }

    private fun initHeadPic() {
        var headUrl = MbsConstans.USER_MAP!!["head_pic"]!!.toString() + ""
        if (headUrl.contains("http")) {
        } else {
            headUrl = MbsConstans.PIC_URL + headUrl
        }
        GlideUtils.loadImage2(activity!!, headUrl, mRoundImageView, R.drawable.head)

    }

    private var sureOrNoDialog: SureOrNoDialog? = null
    override fun onClick(view: View) {

        var intent: Intent
        when (view.id) {
            R.id.show_yue_lay -> mToggleButton.performClick()
            R.id.headKefuIv->{
                intent = Intent(activity,ContacKefuActivity::class.java)
                startActivity(intent)
            }
            R.id.chongzhiTv ->{
                intent = Intent(activity,PayWayActivity::class.java)
                startActivity(intent)
            }
            R.id.tixianTv ->{
                intent = Intent(activity,TixianActivity::class.java)
                startActivity(intent)
            }
            R.id.renZhengLay ->{
                intent = Intent(activity,RenZhengActivity::class.java)
                startActivity(intent)
            }
            R.id.bankCardLay ->{
                intent = Intent(activity,BankCardListActivity::class.java)
                startActivity(intent)
            }
            R.id.yaoQingLay ->{
                intent = Intent(activity,YaoqingActivity::class.java)
                startActivity(intent)
            }
            R.id.trade_Lay ->{
                intent = Intent(activity,RecordListActivity::class.java)
                intent.putExtra("TYPE","1")
                startActivity(intent)
            }
            R.id.account_Lay ->{
                intent = Intent(activity,AccountActivity::class.java)
                startActivity(intent)
            }

            R.id.notice_Lay ->{
                intent = Intent(activity,NoticeListActivity::class.java)
                startActivity(intent)
            }

            R.id.activity_Lay ->{
                intent = Intent(activity,HuoDongActivity::class.java)
                startActivity(intent)
            }

            R.id.free_Lay ->{
                intent = Intent(activity,FreeActivity::class.java)
                startActivity(intent)
            }

            R.id.exitTv ->{
                sureOrNoDialog = SureOrNoDialog(activity!!, true)
                sureOrNoDialog!!.initValue("提示", "确定要退出登录吗？")
                sureOrNoDialog!!.onClickListener = View.OnClickListener { v ->
                    when (v.id) {
                        R.id.cancel -> sureOrNoDialog!!.dismiss()
                        R.id.confirm -> {
                            /*ChatManagerHolder.gChatManager.disconnect(true);
                                        SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
                                        sp.edit().clear().apply();*/
                            activity!!.finish()
                            MbsConstans.USER_MAP = null
                            MbsConstans.RONGYUN_MAP = null
                            MbsConstans.ACCESS_TOKEN = ""
                            SPUtils.put(activity!!, MbsConstans.SharedInfoConstans.LOGIN_OUT, true)
                            SPUtils.put(activity!!, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "")
                            val intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
                sureOrNoDialog!!.show()
                sureOrNoDialog!!.setCanceledOnTouchOutside(false)
                sureOrNoDialog!!.setCancelable(true)
            }
        }

    }


    override fun showProgress() {
        mLoadingWindow!!.showView()
    }

    override fun disimissProgress() {
        mLoadingWindow!!.cancleView()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        when (mType) {

            MethodUrl.ACCOUNT_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    MbsConstans.USER_MAP = tData["data"] as MutableMap<String, Any>?
                    mUserName.text = MbsConstans.USER_MAP!!["phone"] as String
                   /* mAccountTotal.text = MbsConstans.USER_MAP!!["total"] as String
                    mPeiziMoneyTv.text = MbsConstans.USER_MAP!!["with_capital"] as String
                    mDongjieMoneyTv.text = MbsConstans.USER_MAP!!["frozen"] as String
                    mBaozhengMoneyTv.text = MbsConstans.USER_MAP!!["bond"] as String
                    mAviableMoneyTv.text = MbsConstans.USER_MAP!!["account"] as String*/
                    SPUtils.put(activity!!, MbsConstans.SharedInfoConstans.LOGIN_INFO, JSONUtil.instance.objectToJson(MbsConstans.USER_MAP!!))
                   // initHeadPic()
                    if (MbsConstans.USER_MAP!!["type"].toString() == "0"){ //线上用户
                        mMessageLay.visibility = View.VISIBLE
                    }else{//线下用户
                        mMessageLay.visibility = View.GONE
                    }

                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    activity!!.finish()
                    val intent = Intent(activity!!, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.userInfo//用户信息 //{auth=1, firm_kind=0, head_pic=default, name=刘英超, tel=151****3298, idno=4107****3616, cmpl_info=0}
            -> {
                MbsConstans.USER_MAP = tData
                SPUtils.put(activity!!, MbsConstans.SharedInfoConstans.LOGIN_INFO, JSONUtil.instance.objectToJson(MbsConstans.USER_MAP!!))
                mUserName.text = MbsConstans.USER_MAP!!["name"]!!.toString() + ""
                initHeadPic()
            }
            MethodUrl.bankCard -> {
                val result = tData["result"]!!.toString() + ""
                if (UtilTools.empty(result)) {
                } else {
                    val list = JSONUtil.instance.jsonToList(result)
                    if (list != null && list.size > 0) {
                        mBankInfo = list[0]
                        mBankCardTv.text = "银行账户(" + mBankInfo!!["accid"] + ")余额"
                    } else {

                    }
                }
            }

            MethodUrl.allZichan -> {
                val yue = UtilTools.getMoney(tData["use_amt"]!!.toString() + "")
                mBankMoneyTv.text = yue
                mShowMoneyTv.text = "隐藏余额"
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.liveSubmit -> getUserInfoAction()
                    MethodUrl.bankCard -> getCardInfoAction()
                    MethodUrl.allZichan -> getCardMoney()
                    MethodUrl.shareUrl -> getShareData()
                }
            }
        }
    }


    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            userVisibleHint = false
        } else {
            userVisibleHint = true


        }
    }


    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(mBroadcastReceiver)
    }
}
