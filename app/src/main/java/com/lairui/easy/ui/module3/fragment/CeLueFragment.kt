package com.lairui.easy.ui.module3.fragment

import android.content.Intent
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
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
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.KindSelectDialog
import com.lairui.easy.mywidget.view.LoadingWindow
import com.lairui.easy.mywidget.view.TipsToast.Companion.showToastMsg
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.ui.module.activity.XieYiDetialActivity
import com.lairui.easy.ui.module3.activity.PayActivity
import com.lairui.easy.ui.module3.adapter.SelectMoneyAdapter
import com.lairui.easy.utils.tool.*
import kotlinx.android.synthetic.main.activity_news_item.*
import kotlinx.android.synthetic.main.fragment_celue.*
import java.util.*
import kotlin.collections.HashMap

class CeLueFragment : BasicFragment(), RequestView, ReLoadingData, SelectBackListener,OnMyItemClickListener {
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
    @BindView(R.id.boundEt)
    lateinit var boundEt: EditText
    @BindView(R.id.timeTv)
    lateinit var timeTv: TextView
    @BindView(R.id.totalMoneyTv)
    lateinit var totalMoneyTv: TextView
    @BindView(R.id.jingjieLineTv)
    lateinit var jingjieLineTv: TextView
    @BindView(R.id.pingcangLineTv)
    lateinit var pingcangLineTv: TextView
    @BindView(R.id.lixiTv)
    lateinit var lixiTv: TextView










    private var mDialog: KindSelectDialog? = null
    private var mDialog2: KindSelectDialog? = null


    private lateinit var mLoadingWindow: LoadingWindow
    private var mSelectMoneyAdapter: SelectMoneyAdapter? = null
    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private var mRequestTag = ""
    private val mDataList: MutableList<MutableMap<String, Any>> = ArrayList()


    private lateinit var mAnimUtil: AnimUtil

    private val listUp: MutableList<Map<String, Any>> = ArrayList()
    private lateinit  var mData : MutableMap<String, Any>


    var selectItem = 0
    var totalMoney = 0
    var lixiMoney = 0.0f

    var TYPE = 0

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

        initTextView()




        mTabLayout.addTab(mTabLayout.newTab().setText("按天配资"))
        mTabLayout.addTab(mTabLayout.newTab().setText("按月配资"))
        mTabLayout.addTab(mTabLayout.newTab().setText("免息配资"))
       // mTabLayout.addTab(mTabLayout.newTab().setText("免费体验"))

        mTabLayout.addOnTabSelectedListener(object :XTabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: XTabLayout.Tab?) {
                when(mTabLayout.selectedTabPosition){
                    0 ->{
                        mPeiziLay.visibility =View.VISIBLE
                        mTiyanLay.visibility = View.GONE
                        mDayRv.visibility =View.VISIBLE
                        mLixiLay.visibility = View.VISIBLE
                        mFreeXiLay.visibility = View.GONE
                        mTipTv.text="1.非交易日不收取管理费用;\n" +
                                "2.申请即表示已阅读并同意《策略协议》;\n" +
                                "3.每个合约至少2个交易日,首次申请将扣取2个交易日的管理费"
                        initTextView()
                        lixiTv.text ="0.00 元/交易日"
                        totalMoneyTv.text = "0.00 元"
                        jingjieLineTv.text = "0.00 元"
                        pingcangLineTv.text = "0.00 元"
                        boundEt.setText("")
                        getDayInfoAction()

                    }
                    1 ->{
                        mPeiziLay.visibility =View.VISIBLE
                        mTiyanLay.visibility = View.GONE
                        mDayRv.visibility =View.GONE
                        mLixiLay.visibility = View.VISIBLE
                        mFreeXiLay.visibility = View.GONE
                        mTipTv.text="1.非交易日不收取管理费用;\n" +
                                "2.申请即表示已阅读并同意《策略协议》;"
                        initTextView()
                        lixiTv.text ="0.00 元/月"
                        totalMoneyTv.text = "0.00 元"
                        jingjieLineTv.text = "0.00 元"
                        pingcangLineTv.text = "0.00 元"
                        boundEt.setText("")
                        getMonthInfoAction()
                    }
                    2 ->{
                        mPeiziLay.visibility =View.VISIBLE
                        mTiyanLay.visibility = View.GONE
                        mDayRv.visibility =View.VISIBLE
                        mLixiLay.visibility = View.GONE
                        mFreeXiLay.visibility = View.VISIBLE
                        mTipTv.text="1.非交易日不收取管理费用;\n" +
                                "2.申请即表示已阅读并同意《策略协议》;\n" +
                                "3.每个合约至少2个交易日,首次申请将扣取2个交易日的管理费"
                        initTextView()
                        totalMoneyTv.text = "0.00 元"
                        jingjieLineTv.text = "0.00 元"
                        pingcangLineTv.text = "0.00 元"
                        boundEt.setText("")
                       getFreeInfoAction()

                    }
                   /* 3 ->{
                        mPeiziLay.visibility =View.GONE
                        mTiyanLay.visibility = View.VISIBLE
                        mDayRv.visibility =View.GONE
                        mInputMoneyEt.visibility =View.GONE
                        mLixiLay.visibility = View.GONE
                        mFreeXiLay.visibility = View.GONE
                    }*/
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

        boundEt.addTextChangedListener(object:TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty()){
                    mShenQingTv.isEnabled = true
                    mDayRv.visibility = View.VISIBLE
                    if (mDataList.isNotEmpty()){
                        for (item in mDataList){
                            item["money"] = (item["multiple"].toString()).toInt() * (s.toString()).toInt()
                        }
                        if (mSelectMoneyAdapter == null){
                            mSelectMoneyAdapter = SelectMoneyAdapter(activity!!,mDataList)
                        }
                        mDayRv.adapter = mSelectMoneyAdapter
                        mSelectMoneyAdapter!!.notifyDataSetChanged()
                        mSelectMoneyAdapter!!.onMyItemClickListener = this@CeLueFragment
                        //计算操盘金= 保证金+保证*配资倍数
                        totalMoney = ((mDataList[selectItem]["multiple"].toString()).toInt())* ((s.toString()).toInt())+ ((s.toString()).toInt())
                        totalMoneyTv.text =UtilTools.getNormalMoney(totalMoney.toString())+"元"
                        //计算警戒线 = 保证金*预警线比例+保证金*配置倍数
                        val jingjieMoney =(mData["warning"].toString().toFloat() +(mDataList[selectItem]["multiple"].toString()).toInt()) * (s.toString()).toInt()
                        jingjieLineTv.text =UtilTools.getNormalMoney(jingjieMoney.toString())+"元"
                        //计算平仓线 = 保证金*平仓线比例+保证金*配资倍数
                        val pingcangMoney =(mData["close"].toString().toFloat() +(mDataList[selectItem]["multiple"].toString()).toInt()) * (s.toString()).toInt()
                        pingcangLineTv.text =UtilTools.getNormalMoney(pingcangMoney.toString())+"元"
                        when(mTabLayout.selectedTabPosition){
                            0 ->{
                                //计算利息/管理费 = 保证金*配资倍数*利息比例*2 (按天)
                                lixiMoney =(mDataList[selectItem]["ratio"].toString().toFloat()) *((mDataList[selectItem]["multiple"].toString()).toInt()) * (s.toString()).toInt()*2
                                lixiTv.text =UtilTools.getNormalMoney(lixiMoney.toString())+"元/交易日"
                            }
                            1 ->{
                                //计算利息/管理费 = 保证金*配资倍数*利息比例*2 (按天)
                                lixiMoney =(mDataList[selectItem]["ratio"].toString().toFloat()) *((mDataList[selectItem]["multiple"].toString()).toInt()) * (s.toString()).toInt()
                                lixiTv.text =UtilTools.getNormalMoney(lixiMoney.toString())+"元/月"
                            }
                            2 ->{

                            }
                        }


                    }


                }else{
                    mDayRv.visibility = View.GONE
                    mShenQingTv.isEnabled = false
                }
            }


        })


       /* val mDataList: List<MutableMap<String, Any>> = SelectDataUtil.education
        mDialog = KindSelectDialog(activity!!, true, mDataList, 20)
        val mDataList2: List<MutableMap<String, Any>> = SelectDataUtil.baoliType
        mDialog2 = KindSelectDialog(activity!!, true, mDataList2, 21)
        mDialog!!.selectBackListener
        mDialog2!!.selectBackListener*/

        setBarTextColor()
        getDayInfoAction()

        Objects.requireNonNull(mTabLayout.getTabAt(TYPE))!!.select()


    }

    private fun initTextView() {
        val textViewUtils = TextViewUtils()
        val s = mTipTv.text.toString()
        textViewUtils.init(s, mTipTv)
        textViewUtils.setTextColor(s.indexOf("《"), s.indexOf("》"), ContextCompat.getColor(activity!!, R.color.font_c))
        textViewUtils.setTextClick(s.indexOf("《"), s.indexOf("》"), object : TextViewUtils.ClickCallBack {
            override fun onClick() {
                // Toast.makeText(activity, "策略协议", Toast.LENGTH_LONG).show()
                val intent = Intent(activity,XieYiDetialActivity::class.java)
                intent.putExtra("TYPE","3")
                startActivity(intent)
            }

        })
        textViewUtils.build()
    }

    fun setBarTextColor() {
        StatusBarUtil.setLightMode(activity!!)
    }

    private fun getDayInfoAction() {
        mRequestTag = MethodUrl.PEIZI_DAY_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.PEIZI_DAY_INFO
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[activity!!, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.PEIZI_DAY_INFO, map)
    }

    private fun getMonthInfoAction() {
        mRequestTag = MethodUrl.PEIZI_MONTH_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.PEIZI_MONTH_INFO
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[activity!!, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.PEIZI_MONTH_INFO, map)
    }
    private fun getFreeInfoAction() {
        mRequestTag = MethodUrl.PEIZI_FREE_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.PEIZI_FREE_INFO
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[activity!!, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.PEIZI_FREE_INFO, map)
    }

    @OnClick(R.id.selectTimeLay, R.id.selectTimeLay2,R.id.shenQingTv)
    fun onViewClicked(view: View) {
        var intent: Intent? = null
        when (view.id) {
            R.id.selectTimeLay -> mDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.selectTimeLay2 -> mDialog2!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.shenQingTv ->{
                intent = Intent(activity,PayActivity::class.java)
                intent.putExtra("bound",boundEt.text.toString())
                intent.putExtra("lixi",lixiMoney.toString())
                intent.putExtra("multiple",mDataList[selectItem]["multiple"].toString())

                when(mTabLayout.selectedTabPosition){
                    0 ->{
                        intent.putExtra("type","0")
                    }

                    1 ->{
                        intent.putExtra("type","1")
                    }

                    2 ->{
                        intent.putExtra("type","2")
                        intent.putExtra("lixi","0")
                    }

                }
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
            MethodUrl.PEIZI_DAY_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    //val map = tData["data"] as String
                    if (!UtilTools.empty(tData["data"].toString())){
                        val  mapData = tData["data"] as MutableMap<String,Any>
                        if (!UtilTools.empty(mapData["data"].toString())){
                            mData = mapData["data"] as MutableMap<String,Any>
                            boundEt.hint = mData["explain"].toString()
                            timeTv.text = "自动延期，最长"+mData["delay"]+"个交易日"
                            val strList = JSONUtil.instance.jsonToListStr2(mapData["multiple"].toString()) as ArrayList<List<String>>
                            mDataList.clear()
                            for (item in strList){
                                val map = HashMap<String,Any>()
                                map["multiple"] = item[0]
                                map["ratio"] = item[1]
                                mDataList.add(map)
                            }
                        }

                    }

                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    activity!!.finish()
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }


            MethodUrl.PEIZI_MONTH_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    //val map = tData["data"] as String
                    if (!UtilTools.empty(tData["data"].toString())){
                        val  mapData = tData["data"] as MutableMap<String,Any>
                        if (!UtilTools.empty(mapData["data"].toString())){
                            mData = mapData["data"] as MutableMap<String,Any>
                            boundEt.hint = mData["explain"].toString()
                            timeTv.text = "自动延期，最长"+mData["delay"]+"个自然月"
                            val strList = JSONUtil.instance.jsonToListStr2(mapData["multiple"].toString()) as ArrayList<List<String>>
                            mDataList.clear()
                            for (item in strList){
                                val map = HashMap<String,Any>()
                                map["multiple"] = item[0]
                                map["ratio"] = item[1]
                                mDataList.add(map)
                            }
                        }

                    }

                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    activity!!.finish()
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.PEIZI_FREE_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    //val map = tData["data"] as String
                    if (!UtilTools.empty(tData["data"].toString())){
                        val  mapData = tData["data"] as MutableMap<String,Any>
                        if (!UtilTools.empty(mapData["data"].toString())){
                            mData = mapData["data"] as MutableMap<String,Any>
                            boundEt.hint = mData["explain"].toString()
                            timeTv.text = mData["delay"].toString()+"个交易日"
                            freeTipTv.movementMethod = LinkMovementMethod.getInstance()
                            freeTipTv.text = Html.fromHtml(mapData["tips"].toString())
                            val strList = JSONUtil.instance.jsonToListStr2(mapData["multiple"].toString()) as ArrayList<List<String>>
                            mDataList.clear()
                            for (item in strList){
                                val map = HashMap<String,Any>()
                                map["multiple"] = item[0]
                                map["ratio"] = item[1]
                                mDataList.add(map)
                            }
                        }

                    }

                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    activity!!.finish()
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }



        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.PEIZI_DAY_APPLY -> mShenQingTv.isEnabled = true
        }


        mLoadingWindow.cancleView()
        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        mLoadingWindow.showView()

    }



    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            21 -> {

            }
            22 -> {

            }
        }
    }

    override fun OnMyItemClickListener(view: View, position: Int) {
        selectItem = position
        if (!TextUtils.isEmpty(boundEt.text)){
            //计算操盘金= 保证金+保证*配资倍数
            totalMoney = (mDataList[selectItem]["multiple"].toString()).toInt() * (boundEt.text.toString()).toInt()+ (boundEt.text.toString()).toInt()
            totalMoneyTv.text =UtilTools.getNormalMoney(totalMoney.toString())+"元"
            //计算警戒线 = 保证金*预警线比例+保证金*配置倍数
            val jingjieMoney =(mData["warning"].toString().toFloat() +(mDataList[selectItem]["multiple"].toString()).toInt()) * (boundEt.text.toString()).toInt()
            jingjieLineTv.text =UtilTools.getNormalMoney(jingjieMoney.toString())+"元"
            //计算平仓线 = 保证金*平仓线比例+保证金*配资倍数
            val pingcangMoney =(mData["close"].toString().toFloat() +(mDataList[selectItem]["multiple"].toString()).toInt()) * (boundEt.text.toString()).toInt()
            pingcangLineTv.text =UtilTools.getNormalMoney(pingcangMoney.toString())+"元"

            when(mTabLayout.selectedTabPosition){
                0 ->{
                    //计算利息/管理费 = 保证金*配资倍数*利息比例*2 (按天)
                    lixiMoney =(mDataList[selectItem]["ratio"].toString().toFloat()) *((mDataList[selectItem]["multiple"].toString()).toInt()) * (boundEt.text.toString()).toInt()*2
                    lixiTv.text =UtilTools.getNormalMoney(lixiMoney.toString())+"元/交易日"
                }
                1 ->{
                    //计算利息/管理费 = 保证金*配资倍数*利息比例*2 (按天)
                    lixiMoney =(mDataList[selectItem]["ratio"].toString().toFloat()) *((mDataList[selectItem]["multiple"].toString()).toInt()) * (boundEt.text.toString()).toInt()
                    lixiTv.text =UtilTools.getNormalMoney(lixiMoney.toString())+"元/月"
                }
                2 ->{

                }
            }

        }


    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            userVisibleHint = false
        } else {
            userVisibleHint = true
            Objects.requireNonNull(mTabLayout.getTabAt(TYPE))!!.select()
        }
    }
}
