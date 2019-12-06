package com.lairui.easy.ui.module3.fragment

import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.androidkun.xtablayout.XTabLayout
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.jaeger.library.StatusBarUtil
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicFragment
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.presenter.RequestPresenterImp
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.KindSelectDialog
import com.lairui.easy.mywidget.view.LoadingWindow
import com.lairui.easy.ui.module2.adapter.HangqingListAdapter
import com.lairui.easy.ui.module3.activity.PayActivity
import com.lairui.easy.ui.module3.adapter.SelectMoneyAdapter
import com.lairui.easy.utils.tool.*
import java.util.*

class CeLueFragment : BasicFragment(), RequestView, ReLoadingData, SelectBackListener {
    @BindView(R.id.tab_layout)
    lateinit var mTabLayout: XTabLayout
    @BindView(R.id.title_bar_view)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.tipTv)
    lateinit var mTipTv: TextView
    @BindView(R.id.shenQingTv)
    lateinit var mShenQingTv: TextView
    @BindView(R.id.peiziLay)
    lateinit var mPeiziLay: ScrollView
    @BindView(R.id.tiyanLay)
    lateinit var mTiyanLay: LinearLayout
    @BindView(R.id.dayRv)
    lateinit var mDayRv: RecyclerView
    @BindView(R.id.selectTimeLay)
    lateinit var mSelectTimeLay: LinearLayout
    @BindView(R.id.selectTimeLay2)
    lateinit var mSelectTimeLay2: LinearLayout
    @BindView(R.id.inputMoneyEt)
    lateinit var mInputMoneyEt: EditText
    @BindView(R.id.lixiLay)
    lateinit var mLixiLay: LinearLayout
    @BindView(R.id.freeXiLay)
    lateinit var mFreeXiLay: CardView




    private var mDialog: KindSelectDialog? = null
    private var mDialog2: KindSelectDialog? = null


    private lateinit var mLoadingWindow: LoadingWindow
    private var mSelectMoneyAdapter: SelectMoneyAdapter? = null
    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private var mHangqingAdapter: HangqingListAdapter? = null
    private var mRequestTag = ""

    private val mDataList = ArrayList<MutableMap<String, Any>>()

    private var mStartTime = ""
    private var mEndTime = ""
    private var mJieKuanStatus = ""

    private lateinit var mAnimUtil: AnimUtil

    private val listUp: MutableList<Map<String, Any>> = ArrayList()
    private var mDatas = ArrayList<MutableMap<String, Any>>()

    private lateinit var popView: View
    private lateinit var mConditionDialog: PopupWindow
    private var bright = false

    override val layoutId: Int
        get() = R.layout.fragment_celue



    override fun init() {

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, activity!!.resources.getDimension(R.dimen.title_item_height).toInt() + UtilTools.getStatusHeight2(activity!!))
        mTitleBarView.layoutParams = layoutParams
        mTitleBarView.setPadding(0, UtilTools.getStatusHeight2(activity!!), 0, 0)

        mTipTv.text="1.非交易日不收取管理费用;\n" +
                "2.申请即表示已阅读并同意《策略协议》;\n" +
                "3.每个合约至少2个交易日,首次申请将扣取2个交易日的管理费"

        val textViewUtils = TextViewUtils()
        val s = mTipTv.text.toString()
        textViewUtils.init(s, mTipTv)
        textViewUtils.setTextColor(s.indexOf("《"), s.indexOf("》"), ContextCompat.getColor(activity!!, R.color.font_c))
        textViewUtils.setTextClick(s.indexOf("《"), s.indexOf("》"), object : TextViewUtils.ClickCallBack {
            override fun onClick() {
                Toast.makeText(activity, "策略协议", Toast.LENGTH_LONG).show()
            }

        })
        textViewUtils.build()


        mTabLayout.addTab(mTabLayout.newTab().setText("按天配资"))
        mTabLayout.addTab(mTabLayout.newTab().setText("按月配资"))
        mTabLayout.addTab(mTabLayout.newTab().setText("免息配资"))
        mTabLayout.addTab(mTabLayout.newTab().setText("免费体验"))

        mTabLayout.addOnTabSelectedListener(object :XTabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: XTabLayout.Tab?) {
                when(mTabLayout.selectedTabPosition){
                    0 ->{
                        mPeiziLay.visibility =View.VISIBLE
                        mTiyanLay.visibility = View.GONE
                        mDayRv.visibility =View.VISIBLE
                        mLixiLay.visibility = View.VISIBLE
                        mFreeXiLay.visibility = View.GONE
                    }
                    1 ->{
                        mPeiziLay.visibility =View.VISIBLE
                        mTiyanLay.visibility = View.GONE
                        mDayRv.visibility =View.GONE
                        mLixiLay.visibility = View.VISIBLE
                        mFreeXiLay.visibility = View.GONE
                    }
                    2 ->{
                        mPeiziLay.visibility =View.VISIBLE
                        mTiyanLay.visibility = View.GONE
                        mDayRv.visibility =View.VISIBLE
                        mLixiLay.visibility = View.GONE
                        mFreeXiLay.visibility = View.VISIBLE
                    }
                    3 ->{
                        mPeiziLay.visibility =View.GONE
                        mTiyanLay.visibility = View.VISIBLE
                        mDayRv.visibility =View.GONE
                        mInputMoneyEt.visibility =View.GONE
                        mLixiLay.visibility = View.GONE
                        mFreeXiLay.visibility = View.GONE
                    }
                }
            }

            override fun onTabReselected(tab: XTabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: XTabLayout.Tab?) {

            }


        })


        mAnimUtil = AnimUtil()
        mLoadingWindow = LoadingWindow(activity!!, R.style.Dialog)

        mLoadingWindow.showView()
        //borrowListAction()



        for (index in 1..5){
            val map = HashMap<String,Any>()
            map["item"] = "8倍\n"+"8000元"
            mDatas.add(map)
        }
        mSelectMoneyAdapter = SelectMoneyAdapter(activity!!,mDatas)
        mDayRv.adapter = mSelectMoneyAdapter


        val mDataList: List<MutableMap<String, Any>> = SelectDataUtil.education
        mDialog = KindSelectDialog(activity!!, true, mDataList, 20)
        val mDataList2: List<MutableMap<String, Any>> = SelectDataUtil.baoliType
        mDialog2 = KindSelectDialog(activity!!, true, mDataList2, 21)
        mDialog!!.selectBackListener
        mDialog2!!.selectBackListener



        setBarTextColor()
    }

    fun setBarTextColor() {
        StatusBarUtil.setLightMode(activity!!)
    }

    private fun borrowListAction() {
        mRequestPresenterImp = RequestPresenterImp(this, activity!!)
        mRequestTag = MethodUrl.borrowList
        val map = HashMap<String, String>()
        map["loansqid"] = ""
        map["startdate"] = mStartTime
        map["enddate"] = mEndTime
        map["loanstate"] = mJieKuanStatus
        map["creditfile"] = ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MethodUrl.borrowList, map)
    }

    @OnClick(R.id.selectTimeLay, R.id.selectTimeLay2,R.id.shenQingTv)
    fun onViewClicked(view: View) {
        var intent: Intent? = null
        when (view.id) {
            R.id.selectTimeLay -> mDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.selectTimeLay2 -> mDialog2!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.shenQingTv ->{
                intent = Intent(activity,PayActivity::class.java)
                startActivity(intent)
            }
        }
    }





    override fun showProgress() {

    }

    override fun disimissProgress() {

    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        mLoadingWindow.cancleView()
        val intent: Intent
        when (mType) {
            MethodUrl.borrowList//
            -> {
                val result = tData["result"]!!.toString() + ""
                if (UtilTools.empty(result)) {
                    val list = JSONUtil.instance.jsonToList(result)

                } else {
                    val list = JSONUtil.instance.jsonToList(result)
                    if (list != null) {
                        mDataList.clear()
                        mDataList.addAll(list)

                    }
                }

            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.borrowList -> borrowListAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {

        }


        mLoadingWindow.cancleView()
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        mLoadingWindow.showView()
        borrowListAction()
    }



    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            21 -> {

            }
            22 -> {

            }
        }
    }
}
