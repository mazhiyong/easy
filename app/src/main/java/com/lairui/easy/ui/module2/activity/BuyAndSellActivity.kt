package com.lairui.easy.ui.module2.activity

import android.content.Intent
import android.graphics.Rect
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import butterknife.OnClick
import com.androidkun.xtablayout.XTabLayout
import com.jaeger.library.StatusBarUtil
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.bean.StockInfoBean
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.AppDialog
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.ui.module2.adapter.*
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.UtilTools
import kotlinx.android.synthetic.main.activity_buyandsell.*


/**
 * 买入  卖出
 */
class BuyAndSellActivity : BasicActivity(), RequestView, ReLoadingData {


    private var totalMony: Double = 0.00
    private var surplusAmount = "0"

    private var mCode = ""
    private var mark = ""
    private var mRequestTag = 0


    private var stockInfoBean :StockInfoBean? = null
    private var buyData: MutableList<MutableMap<String,Any>> =  java.util.ArrayList()
    private var sellData: MutableList<MutableMap<String,Any>> =java.util.ArrayList()

    private var mSelladapter: BuyAndSellAdapter? = null
    private var mBuyadapter: BuyAndSellAdapter? = null


    private  var mChicangListAdapter: ChicangListAdapter? = null
    private  var mChedanListAdapter: ChedanListAdapter? = null
    private  var mWeituoListAdapter:WeituoListAdapter? = null
    private  var mChengjiaoListAdapter: ChengjiaoListAdapter? = null



    private var mDataList: MutableList<MutableMap<String, Any>> = ArrayList()
    private var mItemDataList: MutableList<MutableMap<String, Any>> = ArrayList()


    private var handler = android.os.Handler()

    private var mIsShow = false
    private var mIsShowLoading = false


    override val contentView: Int
        get() = R.layout.activity_buyandsell

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        content?.let { mPageView!!.setContentView(it) }
        mPageView.showLoading()
        mPageView.reLoadingData = this



       /* val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, resources.getDimension(R.dimen.title_item_height).toInt() + UtilTools.getStatusHeight2(this))
        title_bar_view.layoutParams = layoutParams
        title_bar_view.setPadding(0, UtilTools.getStatusHeight2(this), 0, 0)
*/
        val bundel = intent.extras
        if (bundel == null){
            finish()
        }else{
            mark = bundel.getString("mark")
        }



        mTabLayout.addTab(mTabLayout.newTab().setText("买入"))
        mTabLayout.addTab(mTabLayout.newTab().setText("卖出"))
        mTabLayout.addOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }
            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }
            override fun onTabSelected(tab: XTabLayout.Tab?) {
                when(tab!!.position){
                    0 ->{ //买入
                        tvBuySell.setBackgroundResource(R.drawable.btn_next)
                        tvBuySell.text = "买入"
                        guAmoutTv.text = "可买股数 --"
                        if (marktCb.isChecked && stockInfoBean != null){
                            priceEt.setText(stockInfoBean!!.stockCurrentPrice)
                        }else{
                            priceEt.setText("")
                        }


                    }
                    1 ->{ //卖出
                        tvBuySell.setBackgroundResource(R.drawable.btn_next_green)
                        tvBuySell.text = "卖出"
                        guAmoutTv.text = "可卖数量 "+surplusAmount
                        if (marktCb.isChecked && stockInfoBean != null){
                            priceEt.setText(stockInfoBean!!.stockCurrentPrice)
                        }else{
                            priceEt.setText("")
                        }
                    }
                }
            }

        })


        tlTradeTab.addTab(tlTradeTab.newTab().setText("持仓"))
        tlTradeTab.addTab(tlTradeTab.newTab().setText("撤单"))
        tlTradeTab.addTab(tlTradeTab.newTab().setText("委托"))
        tlTradeTab.addTab(tlTradeTab.newTab().setText("成交"))

        tlTradeTab.addOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }
            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }
            override fun onTabSelected(tab: XTabLayout.Tab?) {
                when(tab!!.position){
                    0 ->{ //持仓
                        chiCangLsitAction()
                    }
                    1 ->{ //撤单
                        cheDanLsitAction()
                    }
                    2 ->{ //委托
                        weiTuoLsitAction()
                    }
                    3 ->{ //成交
                        chengJiaoLsitAction()
                    }
                }
            }

        })




        inputCode.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 5 && !UtilTools.isContainsChinese(s.toString())){
                    getMsgAction(inputCode.text.toString())

                }
            }

        })


       /* if (UtilTools.empty(MbsConstans.USER_MAP)) {
            val s = SPUtils[this, MbsConstans.SharedInfoConstans.LOGIN_INFO, ""].toString()
            MbsConstans.USER_MAP = JSONUtil.instance.jsonMap(s)
            if (MbsConstans.USER_MAP == null){
                getUserInfoAction()
            }else{
                totalMony = (MbsConstans.USER_MAP!!["account"] as String).toDouble()
            }
        }*/

        //获取配资详情
        getDetialInfoAction()




        priceEt.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (mTabLayout.selectedTabPosition == 0){
                    //买入
                    if ( s.toString().isNotEmpty()){
                        if (s.toString() == "0" || s.toString() == "0."){
                            guAmoutTv.text = "可买股数 --"
                        }else{
                            guAmoutTv.text = "可买股数 "+ (totalMony/(s.toString().toDouble())).toInt()
                        }

                    }else{
                        guAmoutTv.text = "可买股数 --"
                    }
                }


            }

        })
        amountEt.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty() && s.toString().toInt()%100 != 0){
                    Toast.makeText(this@BuyAndSellActivity,"请输入100的整数倍",Toast.LENGTH_SHORT).show()
                    return
                }

                /*if (!TextUtils.isEmpty(priceEt.text) && s.toString().isNotEmpty()){
                    if (s.toString() == "0"){
                        guAmoutTv.text = "可买股数 --"
                    }else{
                        guAmoutTv.text = "可买股数 "+ (totalMony/((s.toString().toDouble())*priceEt.text.toString().toDouble())).toInt()
                    }

                }else{
                    guAmoutTv.text = "可买股数 --"
                }*/


            }

        })


        //键盘显示监听

        amountEt.viewTreeObserver.addOnGlobalLayoutListener(object :OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rect = Rect()
                this@BuyAndSellActivity.window.decorView.getWindowVisibleDisplayFrame(rect)
                val screenHeight: Int = this@BuyAndSellActivity.window.decorView.rootView.height
                Log.e("TAG", rect.bottom.toString() + "#" + screenHeight)
                val heightDifference = screenHeight - rect.bottom
                val visible = heightDifference > screenHeight / 3
                if (visible) {
                    allRb.isChecked = false
                    halfRb.isChecked = false
                    thirdRb.isChecked = false
                    mIsShow = true
                } else {
                    mIsShow = false
                }
            }
        })


        rg.setOnCheckedChangeListener(object: RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                if (mTabLayout.selectedTabPosition == 0){ //买入
                    if (TextUtils.isEmpty(priceEt.text) || priceEt.text.toString().toDouble() < 0.000001){
                        showToastMsg("请输入价格")
                        return
                    }
                    when(checkedId){
                        R.id.allRb ->{
                            val amount:Int = ((totalMony/(priceEt.text.toString().toDouble()))/100).toInt()
                            amountEt.setText((amount*100).toString())
                            //guAmoutTv.text = "可买股数 "+ totalMony/(amount*priceEt.text.toString().toDouble())

                        }
                        R.id.halfRb ->{
                            val amount:Int = ((totalMony/(priceEt.text.toString().toDouble()))/100).toInt()
                            amountEt.setText((amount/2*100).toString())
                            // guAmoutTv.text = "可买股数 "+ totalMony/(amount*priceEt.text.toString().toDouble())
                        }
                        R.id.thirdRb ->{
                            val amount:Int = ((totalMony/(priceEt.text.toString().toDouble()))/100/3).toInt()
                            amountEt.setText((amount*100).toString())
                            //guAmoutTv.text = "可买股数 "+ totalMony/(amount*priceEt.text.toString().toDouble())
                        }

                    }
                }else { //卖出

                    when (checkedId) {
                        R.id.allRb -> {
                            val amount: Int = (surplusAmount.toDouble()/100).toInt()
                            amountEt.setText((amount * 100).toString())

                        }
                        R.id.halfRb -> {
                            val amount: Int = (surplusAmount.toDouble()/100).toInt()
                            amountEt.setText((amount / 2 * 100).toString())

                        }
                        R.id.thirdRb -> {
                            val amount: Int = (surplusAmount.toDouble()/100/3).toInt()
                            amountEt.setText((amount * 100).toString())

                        }

                    }


                }
            }
        })



        chiCangLsitAction()


        refreshLayout.setOnRefreshListener {
            when(tlTradeTab!!.selectedTabPosition){
                0 ->{ //持仓
                    chiCangLsitAction()
                }
                1 ->{ //撤单
                    cheDanLsitAction()
                }
                2 ->{ //委托
                    weiTuoLsitAction()
                }
                3 ->{ //成交
                    chengJiaoLsitAction()
                }
            }

        }

        refreshLayout.setOnLoadMoreListener {
            refreshLayout.finishLoadMoreWithNoMoreData()
        }


        initData()
    }

    override fun onPause() {
        super.onPause()
        // if (getDefault().isConnect) getDefault().disConnect()
        handler.removeCallbacks(cnyRunnable)
    }

    override fun onResume() {
        super.onResume()
        //if (!getDefault().isConnect) getDefault().reconnect()
        handler.post(cnyRunnable)
    }

    private val cnyRunnable = object : Runnable {
        override fun run() {
            chiCangLsitAction()
            if (!UtilTools.empty(mCode)){
                getDetialDataAction()
            }

            handler.postDelayed(this, 10 * 1000)


        }
    }



    /**
     * 获取配资详情
     */
    private fun getDetialInfoAction() {
        mIsShowLoading = true
        val map = java.util.HashMap<String, Any>()
        map["nozzle"] = MethodUrl.DETAILED_IFFO
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@BuyAndSellActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        val mHeaderMap = java.util.HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.DETAILED_IFFO, map)
    }


    //获取详情
    private fun getDetialDataAction() {
        mIsShowLoading = false
        mRequestTag = 0
        val map = HashMap<String, String>()
        map["q"] = mCode
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MbsConstans.DETIAL_SERVER_URL, map)
    }

    //获取详情
    private fun getDetialDataAction(code:String) {
        mIsShowLoading = false
        mRequestTag = 1
        val map = HashMap<String, String>()
        map["q"] = code
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MbsConstans.DETIAL_SERVER_URL, map)
    }





    //持仓
    private fun chiCangLsitAction() {
        mIsShowLoading = false
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.CHICANG_LIST
         if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
             MbsConstans.ACCESS_TOKEN = SPUtils[this@BuyAndSellActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
         }
         map["token"] = MbsConstans.ACCESS_TOKEN
         map["mark"] = mark
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHICANG_LIST, map)
    }

    //成交
    private fun chengJiaoLsitAction() {
        mIsShowLoading = true
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.CHENGJIAO_LIST
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@BuyAndSellActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHENGJIAO_LIST, map)
    }

    //撤单
    private fun cheDanLsitAction() {
        mIsShowLoading = true
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.CHEDAN_LIST
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@BuyAndSellActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHEDAN_LIST, map)
    }

    //委托
    private fun weiTuoLsitAction() {
        mIsShowLoading = true
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.WEITUO_LIST
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@BuyAndSellActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.WEITUO_LIST, map)
    }






    private fun getMsgAction(str:String) {
        mIsShowLoading = true
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.QUERY_STOCK
       /* if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@BuyAndSellActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN*/
        map["code"] = str
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.QUERY_STOCK, map)
    }


    private fun buyAction() {
        if(TextUtils.isEmpty(priceEt.text)){
            showToastMsg("请输入价格")
            return
        }

        if(TextUtils.isEmpty(amountEt.text)){
            showToastMsg("请输入数量")
            return
        }
        mIsShowLoading = true
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.BUY_STOCK
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@BuyAndSellActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["code"] = mCode
        map["price"] = priceEt.text.toString()
        map["number"] = amountEt.text.toString()
        map["mark"] = mark
        if (marktCb.isChecked){
            map["mold"] = "2"
        }else{
            map["mold"] = "1"
        }
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.BUY_STOCK, map)
    }


    private fun sellAction() {

        if(TextUtils.isEmpty(priceEt.text)){
            showToastMsg("请输入价格")
            return
        }

        if(TextUtils.isEmpty(amountEt.text)){
            showToastMsg("请输入数量")
            return
        }
        mIsShowLoading = true
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.SELL_STOCK
         if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@BuyAndSellActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["code"] = mCode
        map["price"] = priceEt.text.toString()
        map["number"] = amountEt.text.toString()
        map["mark"] = mark
        if (marktCb.isChecked){
            map["mold"] = "2"
        }else{
            map["mold"] = "1"
        }

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.SELL_STOCK, map)
    }


    private fun cancelAction(id: String) {
        mIsShowLoading = true
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.CHEXIAO_ACTION
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@BuyAndSellActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["mark"] = mark
        map["order"] = id
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHEXIAO_ACTION, map)
    }


    @OnClick(R.id.back_img, R.id.left_back_lay,R.id.marktCb,R.id.limitCb,R.id.addPriceIv,R.id.duePriceIv,R.id.addAmountIv,R.id.dueAmountIv,R.id.tvBuySell)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.marktCb -> {
                marktCb.isChecked = true
                limitCb.isChecked = false
                if (stockInfoBean != null){
                    priceEt.setText(stockInfoBean!!.stockCurrentPrice)
                }

            }

            R.id.limitCb -> {
                marktCb.isChecked = false
                limitCb.isChecked = true
                // priceEt.setText("0.00")
            }
            R.id.addPriceIv -> {
                var currentaAmount = 0.00
                if (!TextUtils.isEmpty(priceEt.text)){
                    currentaAmount = priceEt.text.toString().toDouble()
                }
                currentaAmount += 0.01
                priceEt.setText(UtilTools.numFormat(currentaAmount.toString(),2))
            }

            R.id.duePriceIv-> {
                if (TextUtils.isEmpty(priceEt.text) || priceEt.text.toString() == "0.00" || priceEt.text.toString().toDouble() < 0){
                    priceEt.setText("0.00")
                    return
                }
                var currentaAmount = priceEt.text.toString().toDouble()
                currentaAmount -= 0.01
                priceEt.setText(UtilTools.numFormat(currentaAmount.toString(),2))
            }

            R.id.addAmountIv -> {
                var currentaAmount = 0
                if (!TextUtils.isEmpty(amountEt.text)){
                    currentaAmount = amountEt.text.toString().toInt()
                }
                currentaAmount += 100
                amountEt.setText(currentaAmount.toString())
            }

            R.id.dueAmountIv -> {
                if (TextUtils.isEmpty(amountEt.text) || amountEt.text.toString() == "0" || amountEt.text.toString().toInt() < 100){
                    amountEt.setText("0")
                    return
                }
                var currentaAmount = amountEt.text.toString().toInt()
                currentaAmount -= 100
                amountEt.setText(currentaAmount.toString())
            }

            R.id.tvBuySell -> {
                when(mTabLayout.selectedTabPosition){
                    0 -> { //买入
                        buyAction()
                    }
                    1 -> { //卖出
                       sellAction()
                    }
                }
            }
        }
    }

    override fun showProgress() {
        if (mIsShowLoading){
            showProgressDialog()
        }

    }

    override fun disimissProgress() {
        dismissProgressDialog()

    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        val intent: Intent
        when (mType) {
            MethodUrl.CHICANG_LIST -> when (tData["code"].toString() + "") {
                "1" -> {
                    refreshLayout.finishRefresh()
                    if (UtilTools.empty(tData["data"]!!.toString())) {
                        mPageView.showEmpty()
                    } else {
                        mDataList = tData["data"] as MutableList<MutableMap<String, Any>>
                        if (mDataList.size > 0){
                            mPageView.showContent()
                            for (item in mDataList){
                                if (item["short"].toString() == mCode ){
                                    surplusAmount = item ["surplus"].toString()
                                    if (mTabLayout.selectedTabPosition == 1){
                                        guAmoutTv.text = "可卖数量 "+surplusAmount
                                    }
                                }
                            }
                             var paramCode = ""
                             for (item in mDataList){
                                 paramCode = paramCode+item["short"].toString()+","
                             }
                            getDetialDataAction(paramCode)
                        }else{
                            mPageView.showEmpty()
                        }


                    }
                }

            "0" -> showToastMsg(tData["msg"].toString() + "")
            "-1" -> {
                closeAllActivity()
                val intent = Intent(this@BuyAndSellActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

            MethodUrl.DETAILED_IFFO -> when (tData["code"].toString()) {
                "1" -> {
                    if (tData["data"].toString().isNotEmpty()){
                        val mapData = tData["data"] as MutableMap<String,Any>
                        totalMony = (mapData["available"] as String).toDouble()
                    }

                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@BuyAndSellActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }



            MethodUrl.CHEDAN_LIST -> when (tData["code"].toString() + "") {
                "1" -> {
                    refreshLayout.finishRefresh()
                    if (UtilTools.empty(tData["data"]!!.toString())) {
                        mPageView.showEmpty()
                    } else {
                        mDataList = tData["data"] as MutableList<MutableMap<String, Any>>
                        if (mDataList.size > 0){
                            mPageView.showContent()
                            if (mChedanListAdapter == null) {
                                mChedanListAdapter= ChedanListAdapter(this@BuyAndSellActivity)
                            }
                            mChedanListAdapter!!.clear()
                            mChedanListAdapter!!.addAll(mDataList)
                            val view: View = LayoutInflater.from(this@BuyAndSellActivity).inflate(R.layout.item_chedan_header, rcv, false)
                            mChedanListAdapter!!.addHeaderView(view)
                            rcv.adapter = mChedanListAdapter
                            mChedanListAdapter!!.onItemClickListener = object : com.lairui.easy.listener.OnItemClickListener {
                                override fun onItemClickListener(view: View, position: Int, map: MutableMap<String, Any>) {
                                    val dialog = AppDialog(this@BuyAndSellActivity,true)
                                    dialog.initValue("提示","是否确定撤单?","取消","确定")
                                    dialog.setClickListener(View.OnClickListener { v ->
                                        when (v.id) {
                                            R.id.cancel -> dialog.dismiss()
                                            R.id.confirm -> {
                                                cancelAction(map["id"].toString())
                                                dialog.dismiss()
                                            }
                                        }
                                    })

                                    dialog.show()
                                }
                            }

                        }else{
                            mPageView.showEmpty()
                        }


                    }
                }

                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@BuyAndSellActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }


            MethodUrl.WEITUO_LIST -> when (tData["code"].toString() + "") {
                "1" -> {
                    refreshLayout.finishRefresh()
                    if (UtilTools.empty(tData["data"]!!.toString())) {
                        mPageView.showEmpty()
                    } else {
                        mDataList = tData["data"] as MutableList<MutableMap<String, Any>>
                        if (mDataList.size > 0){
                            mPageView.showContent()
                            if (mWeituoListAdapter == null) {
                                mWeituoListAdapter = WeituoListAdapter(this@BuyAndSellActivity)
                            }
                            mWeituoListAdapter!!.clear()
                            mWeituoListAdapter!!.addAll(mDataList)
                            val view: View = LayoutInflater.from(this@BuyAndSellActivity).inflate(R.layout.item_weituo_header, rcv, false)
                            mWeituoListAdapter!!.addHeaderView(view)
                            rcv.adapter = mWeituoListAdapter

                        }else{
                            mPageView.showEmpty()
                        }



                    }
                }

                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@BuyAndSellActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }


            MethodUrl.CHENGJIAO_LIST -> when (tData["code"].toString() + "") {
                "1" -> {
                    refreshLayout.finishRefresh()
                    if (UtilTools.empty(tData["data"]!!.toString())) {
                        mPageView.showEmpty()
                    } else {
                        mDataList = tData["data"] as MutableList<MutableMap<String, Any>>
                        if (mDataList.size > 0){
                            mPageView.showContent()
                            if (mChengjiaoListAdapter == null) {
                                mChengjiaoListAdapter = ChengjiaoListAdapter(this@BuyAndSellActivity)
                            }
                            mChengjiaoListAdapter!!.clear()
                            mChengjiaoListAdapter!!.addAll(mDataList)
                            val view: View = LayoutInflater.from(this@BuyAndSellActivity).inflate(R.layout.item_chengjiao_header, rcv, false)
                            mChengjiaoListAdapter!!.addHeaderView(view)
                            rcv.adapter = mChengjiaoListAdapter
                        }else{
                            mPageView.showEmpty()
                        }


                    }
                }

                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@BuyAndSellActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }




            MethodUrl.BUY_STOCK -> when (tData["code"].toString() + "") {
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")
                    chiCangLsitAction()

                    intent = Intent()
                    intent.action = MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE
                    sendBroadcast(intent)
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@BuyAndSellActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            MethodUrl.SELL_STOCK -> when (tData["code"].toString() + "") {
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")
                    chiCangLsitAction()

                    intent = Intent()
                    intent.action = MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE
                    sendBroadcast(intent)
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@BuyAndSellActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            MethodUrl.QUERY_STOCK -> when (tData["code"].toString() + "") {
                "1" -> {
                    if (UtilTools.empty(tData["data"].toString())){
                        showToastMsg( "当前股票不支持交易")
                    }else{
                        mCode = tData["data"].toString()
                        getDetialDataAction()
                        chiCangLsitAction()
                    }
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@BuyAndSellActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }



            MbsConstans.DETIAL_SERVER_URL-> {
                if (mRequestTag == 0){ //单条数据
                    val result = tData["result"]!!.toString() + ""
                    handInfoData(result)
                    if(!UtilTools.empty(stockInfoBean)){
                        fallStopTv.text = "跌停  "+stockInfoBean!!.stockFallBottom
                        riseStopTv.text = "涨停  "+stockInfoBean!!.stockRiseTop
                        nameTv.text = stockInfoBean!!.stockName+"   "+mCode
                        priceEt.setText(stockInfoBean!!.stockCurrentPrice)

                    }

                    if ( buyData.size >0  && sellData.size > 0){
                        if (mBuyadapter == null){
                            mBuyadapter = BuyAndSellAdapter(this@BuyAndSellActivity)
                        }
                        mBuyadapter!!.clear()
                        mBuyadapter!!.addAll(buyData)
                        rvBuy.adapter = mBuyadapter

                        if (mSelladapter == null){
                            mSelladapter = BuyAndSellAdapter(this@BuyAndSellActivity)
                        }
                        mSelladapter!!.clear()
                        mSelladapter!!.addAll(sellData)
                        rvSell.adapter = mSelladapter

                    }else{
                        initData()
                    }
                }else{ //列表数据
                    val result = tData["result"]!!.toString() + ""
                    val infoDataList = handInfoDataList(result)
                    if (infoDataList.isNotEmpty()) {
                        for (itemMap in mDataList) {
                            for (item in infoDataList) {
                                if (itemMap["code"].toString() == item["code"].toString()) {
                                    itemMap["current"] = item["current"].toString()
                                    itemMap["ratio"] = item["ratio"].toString()
                                }
                            }
                        }
                        if (mChicangListAdapter == null) {
                            mChicangListAdapter = ChicangListAdapter(this@BuyAndSellActivity)
                        }
                       /* if ( mChicangListAdapter!!.dataList.size == mDataList.size){
                            //列表数据无变化 整体不刷新
                            LogUtil.i("show","整体不刷新")
                            //判断当前价是否变动,变动了更新,不变动不更新
                            if (mDataList.size>0){
                                for (itemMap in mDataList) {
                                    for (item in mChicangListAdapter!!.dataList) {
                                        if (itemMap["current"].toString() == item["current"].toString()) {
                                           //价格一致不刷新
                                        }else{
                                            //价格改变局部刷新
                                            mChicangListAdapter!!.notifyDataSetChanged()
                                        }
                                    }
                                }
                            }

                        }else{
                            //列表数据更新 整体刷新
                            LogUtil.i("show","整体刷新")
                            mChicangListAdapter!!.clear()
                            mChicangListAdapter!!.addAll(mDataList)
                            val view: View = LayoutInflater.from(this@BuyAndSellActivity).inflate(R.layout.item_chichang_header, rcv, false)
                            mChicangListAdapter!!.addHeaderView(view)
                            rcv.adapter = mChicangListAdapter
                        }*/

                        mChicangListAdapter!!.clear()
                        mChicangListAdapter!!.addAll(mDataList)
                        val view: View = LayoutInflater.from(this@BuyAndSellActivity).inflate(R.layout.item_chichang_header, rcv, false)
                        mChicangListAdapter!!.addHeaderView(view)
                        rcv.adapter = mChicangListAdapter

                        mChicangListAdapter!!.onItemClickListener = object : com.lairui.easy.listener.OnItemClickListener {
                            override fun onItemClickListener(view: View, position: Int, map: MutableMap<String, Any>) {
                                inputCode.setText(map["code"].toString())
                            }

                        }
                    }
                }



            }

            MethodUrl.ACCOUNT_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    MbsConstans.USER_MAP = tData["data"] as MutableMap<String, Any>?
                    SPUtils.put(this@BuyAndSellActivity, MbsConstans.SharedInfoConstans.LOGIN_INFO, JSONUtil.instance.objectToJson(MbsConstans.USER_MAP!!))
                    totalMony = (MbsConstans.USER_MAP!!["account"] as String).toDouble()
                }
                "0" -> TipsToast.showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@BuyAndSellActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.CHEXIAO_ACTION-> when (tData["code"].toString()) {
                "1" -> {
                    showToastMsg(tData["msg"].toString() + "")
                    cheDanLsitAction()
                }
                "0" -> {
                    showToastMsg(tData["msg"].toString() + "")
                }
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@BuyAndSellActivity, LoginActivity::class.java)
                    startActivity(intent)
                }

            }


        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }

    private fun handInfoData(result: String) {
        if (!TextUtils.isEmpty(result) && result.contains("~")) {
            val stockArray = result.split(";\n").toTypedArray()
            for (stockInfo in stockArray) {
                stockInfoBean = StockInfoBean()
                val split = stockInfo.split("~").toTypedArray()
                stockInfoBean!!.stockName = split[1]
                stockInfoBean!!.stockCode = split[2]
                stockInfoBean!!.stockCurrentPrice = split[3]
                stockInfoBean!!.stockYesterdayPrice = split[4]
                stockInfoBean!!.stockTodayPrice = split[5]
                stockInfoBean!!.stockVolume = split[6]
                stockInfoBean!!.stockOuterDisk = split[7]
                stockInfoBean!!.stockInnerDisk = split[8]
                stockInfoBean!!.stockBuy1Price = split[9]
                stockInfoBean!!.stockBuy1Amount = split[10]
                stockInfoBean!!.stockBuy2Price = split[11]
                stockInfoBean!!.stockBuy2Amount = split[12]
                stockInfoBean!!.stockBuy3Price = split[13]
                stockInfoBean!!.stockBuy3Amount = split[14]
                stockInfoBean!!.stockBuy4Price = split[15]
                stockInfoBean!!.stockBuy4Amount = split[16]
                stockInfoBean!!.stockBuy5Price = split[17]
                stockInfoBean!!.stockBuy5Amount = split[18]
                stockInfoBean!!.stockSell1Price = split[19]
                stockInfoBean!!.stockSell1Amount = split[20]
                stockInfoBean!!.stockSell2Price = split[21]
                stockInfoBean!!.stockSell2Amount = split[22]
                stockInfoBean!!.stockSell3Price = split[23]
                stockInfoBean!!.stockSell3Amount = split[24]
                stockInfoBean!!.stockSell4Price = split[25]
                stockInfoBean!!.stockSell4Amount = split[26]
                stockInfoBean!!.stockSell5Price = split[27]
                stockInfoBean!!.stockSell5Amount = split[28]
                stockInfoBean!!.stockRiseAndFallAmount = split[31]
                stockInfoBean!!.stockRiseAndFallAmplitude = split[32]
                stockInfoBean!!.stockHighestPrice = split[33]
                stockInfoBean!!.stockLowestPrice = split[34]
                stockInfoBean!!.stockTransactionVolume = split[36]
                stockInfoBean!!.stockTransactionAccount = split[37]
                stockInfoBean!!.stockTurnoverRate = split[38]
                stockInfoBean!!.stockPriceEarningsRatio = split[39]
                stockInfoBean!!.stockAmplitude = split[43]
                stockInfoBean!!.stockCirculationMarketValue = split[44]
                stockInfoBean!!.stockTotalMarketValue = split[45]
                stockInfoBean!!.stockPBRatio = split[46]
                stockInfoBean!!.stockRiseTop = split[47]
                stockInfoBean!!.stockFallBottom = split[48]
                stockInfoBean!!.stockEntrustDifference = split[50]
                stockInfoBean!!.stockAveragePrice = split[51]
                val totalBuyAccount: Int = Integer.valueOf(stockInfoBean!!.stockBuy1Amount) +
                        Integer.valueOf(stockInfoBean!!.stockBuy2Amount) +
                        Integer.valueOf(stockInfoBean!!.stockBuy3Amount) +
                        Integer.valueOf(stockInfoBean!!.stockBuy4Amount) +
                        Integer.valueOf(stockInfoBean!!.stockBuy5Amount)
                val totalSellAccount: Int = Integer.valueOf(stockInfoBean!!.stockSell1Amount) +
                        Integer.valueOf(stockInfoBean!!.stockSell2Amount) +
                        Integer.valueOf(stockInfoBean!!.stockSell3Amount) +
                        Integer.valueOf(stockInfoBean!!.stockSell4Amount) +
                        Integer.valueOf(stockInfoBean!!.stockSell5Amount)
                if (totalBuyAccount > 0 && totalSellAccount > 0) {
                    stockInfoBean!!.stockEntrustRatio = ((totalBuyAccount - totalSellAccount) / (totalBuyAccount + totalSellAccount) * 100).toString()
                } else {
                    stockInfoBean!!.stockEntrustRatio = "0"
                }


            }
            if (!UtilTools.empty(stockInfoBean)){
                buyData.clear()
                sellData.clear()


                val  map1 = HashMap<String,Any>()
                map1["price"] = stockInfoBean!!.stockBuy1Price
                map1["amount"] = stockInfoBean!!.stockBuy1Amount
                map1["type"] = "买1"
                buyData.add(map1)
                val  map2 = HashMap<String,Any>()
                map2["price"] = stockInfoBean!!.stockBuy2Price
                map2["amount"] = stockInfoBean!!.stockBuy2Amount
                map2["type"] = "买2"
                buyData.add(map2)
                val  map3 = HashMap<String,Any>()
                map3["price"] = stockInfoBean!!.stockBuy3Price
                map3["amount"] = stockInfoBean!!.stockBuy3Amount
                map3["type"] = "买3"
                buyData.add(map3)
                val  map4 = HashMap<String,Any>()
                map4["price"] = stockInfoBean!!.stockBuy4Price
                map4["amount"] = stockInfoBean!!.stockBuy4Amount
                map4["type"] = "买4"
                buyData.add(map4)
                val  map5 = HashMap<String,Any>()
                map5["price"] = stockInfoBean!!.stockBuy5Price
                map5["amount"] = stockInfoBean!!.stockBuy5Amount
                map5["type"] = "买5"
                buyData.add(map5)


                val  map10 = HashMap<String,Any>()
                map10["price"] = stockInfoBean!!.stockSell5Price
                map10["amount"] = stockInfoBean!!.stockSell5Amount
                map10["type"] = "卖5"
                sellData.add(map10)

                val  map9 = HashMap<String,Any>()
                map9["price"] = stockInfoBean!!.stockSell4Price
                map9["amount"] = stockInfoBean!!.stockSell4Amount
                map9["type"] = "卖4"
                sellData.add(map9)

                val  map8 = HashMap<String,Any>()
                map8["price"] = stockInfoBean!!.stockSell3Price
                map8["amount"] = stockInfoBean!!.stockSell3Amount
                map8["type"] = "卖3"
                sellData.add(map8)

                val  map7 = HashMap<String,Any>()
                map7["price"] = stockInfoBean!!.stockSell2Price
                map7["amount"] = stockInfoBean!!.stockSell2Amount
                map7["type"] = "卖2"
                sellData.add(map7)

                val  map6 = HashMap<String,Any>()
                map6["price"] = stockInfoBean!!.stockSell1Price
                map6["amount"] = stockInfoBean!!.stockSell1Amount
                map6["type"] = "卖1"
                sellData.add(map6)






            }

        }


    }

    private fun handInfoDataList(result: String):List<MutableMap<String,Any?>> {
        val infoDataList : MutableList<MutableMap<String,Any?>> = ArrayList()
        if (!TextUtils.isEmpty(result) && result.contains("~")) {
            val stockArray = result.split(";\n").toTypedArray()
            for (stockInfo in stockArray) {
                val map : MutableMap<String,Any?> = HashMap()
                val split = stockInfo.split("~").toTypedArray()
                map["code"] =  split[2]
                map["current"] = split[3]
                map["ratio"] =  split[32]
                infoDataList.add(map)
            }

        }

        return infoDataList

    }



    override fun reLoadingData() {
        when(tlTradeTab!!.selectedTabPosition){
            0 ->{ //持仓
                chiCangLsitAction()
            }
            1 ->{ //撤单
                cheDanLsitAction()
            }
            2 ->{ //委托
                weiTuoLsitAction()
            }
            3 ->{ //成交
                chengJiaoLsitAction()
            }
        }
    }

    fun initData(){
        val  map1 = HashMap<String,Any>()
        map1["price"] = "--"
        map1["amount"] = "--"
        map1["type"] = "买1"
        buyData.add(map1)
        val  map2 = HashMap<String,Any>()
        map2["price"] = "--"
        map2["amount"] = "--"
        map2["type"] = "买2"
        buyData.add(map2)
        val  map3 = HashMap<String,Any>()
        map3["price"] = "--"
        map3["amount"] = "--"
        map3["type"] = "买3"
        buyData.add(map3)
        val  map4 = HashMap<String,Any>()
        map4["price"] = "--"
        map4["amount"] = "--"
        map4["type"] = "买4"
        buyData.add(map4)
        val  map5 = HashMap<String,Any>()
        map5["price"] = "--"
        map5["amount"] = "--"
        map5["type"] = "买5"
        buyData.add(map5)


        val  map10 = HashMap<String,Any>()
        map10["price"] = "--"
        map10["amount"] = "--"
        map10["type"] = "卖5"
        sellData.add(map10)

        val  map9 = HashMap<String,Any>()
        map9["price"] = "--"
        map9["amount"] = "--"
        map9["type"] = "卖4"
        sellData.add(map9)

        val  map8 = HashMap<String,Any>()
        map8["price"] = "--"
        map8["amount"] = "--"
        map8["type"] = "卖3"
        sellData.add(map8)

        val  map7 = HashMap<String,Any>()
        map7["price"] = "--"
        map7["amount"] = "--"
        map7["type"] = "卖2"
        sellData.add(map7)

        val  map6 = HashMap<String,Any>()
        map6["price"] = "--"
        map6["amount"] = "--"
        map6["type"] = "卖1"
        sellData.add(map6)

            if (mBuyadapter == null){
                mBuyadapter = BuyAndSellAdapter(this@BuyAndSellActivity)
            }
            mBuyadapter!!.clear()
            mBuyadapter!!.addAll(buyData)
            rvBuy.adapter = mBuyadapter


            if (mSelladapter == null){
                mSelladapter = BuyAndSellAdapter(this@BuyAndSellActivity)
            }
            mSelladapter!!.clear()
            mSelladapter!!.addAll(sellData)
            rvSell.adapter = mSelladapter

    }

}
