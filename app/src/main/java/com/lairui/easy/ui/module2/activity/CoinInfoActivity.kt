package com.lairui.easy.ui.module2.activity

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import butterknife.OnClick
import com.github.mikephil.charting.stockChart.charts.CoupleChartGestureListener.CoupleClick
import com.github.mikephil.charting.stockChart.dataManage.KLineDataManage
import com.github.mikephil.charting.stockChart.dataManage.TimeDataManage
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.jaeger.library.StatusBarUtil
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.bean.StockInfoBean
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.kview.KViewListener
import com.lairui.easy.mywidget.kview.Quotes
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.ui.module2.adapter.BuyAndSellAdapter
import com.lairui.easy.utils.tool.*
import kotlinx.android.synthetic.main.activity_coin_info.*
import kotlinx.android.synthetic.main.title_leftbut_bar.*
import org.json.JSONObject
import java.lang.reflect.Type
import java.text.SimpleDateFormat


class CoinInfoActivity : BasicActivity(), RequestView {

    private var handler = android.os.Handler()
    private var dataList = ArrayList<Quotes>()
    private var kLineData = ArrayList<List<String>>()

    private var mapData :MutableMap<String,Any> ? = null
    private var stockInfoBean :StockInfoBean? = null
    private var buyData: MutableList<MutableMap<String,Any>> =  java.util.ArrayList()
    private var sellData: MutableList<MutableMap<String,Any>> =java.util.ArrayList()

    private var mSelladapter: BuyAndSellAdapter? = null
    private var mBuyadapter: BuyAndSellAdapter? = null

    private var mRequestTag:Int = 0
    private var mIsFirst:Boolean = true

    private var dateformat: SimpleDateFormat? = null

    private val kTimeData = TimeDataManage()
    private var kLineDatas : KLineDataManage? = null


    private var indexType = 1

    //不支持滑动返回
    override fun isSupportSwipeBack(): Boolean {
        return false
    }



    override val contentView: Int
        get() = R.layout.activity_coin_info

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        kLineDatas = KLineDataManage(this)
        timeChart.initChart(true)
        kLineChart.initChart(true)
        kLineChart.getGestureListenerBar().setCoupleClick(CoupleClick {
            loadIndexData(if (indexType < 5) ++indexType else 1)
        })



        divide_line.visibility = View.GONE
        right_img.visibility = View.VISIBLE
        right_text_tv.visibility = View.VISIBLE
        right_img.setBackgroundResource(R.drawable.zixuan_selected)

        dateformat =  SimpleDateFormat("yyyyMMddHHmmss")

        val bundle = intent.extras
        if (bundle == null){
            finish()
        }else{
            mapData = bundle.getSerializable("DATA") as MutableMap<String,Any> ?
            title_text.text = mapData!!["name"].toString()
            mRequestTag = 0
            getTimeMinuteDataActin()
            //getKLineMinuteDataActin("m1")
            getKLineMonthAction()
            getDetialDataAction()
            queryConcernAction()
        }




        val mKLineData = SelectDataUtil.kLineParams
        for (item in mKLineData){
            tabLayout.addTab(tabLayout.newTab().setText(item.get("name") as String))
        }

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0 -> {
                        mRequestTag = 0
                        getTimeMinuteDataActin() //1分时图
                        timeChart.visibility = View.VISIBLE
                        kLineChart.visibility =View.GONE

                    }
                    /*1 -> {
                        mRequestTag = 1 //五日分时图
                        getKLineWeekAction()

                    }*/
                    1 -> {
                        mRequestTag = 2 //日K
                        getKLineDayAction()
                        timeChart.visibility = View.GONE
                        kLineChart.visibility =View.VISIBLE
                    }
                    2 -> {
                        mRequestTag = 3 //周K
                        getKLineWeekAction()
                        timeChart.visibility = View.GONE
                        kLineChart.visibility =View.VISIBLE
                    }
                    3 -> {
                        mRequestTag = 4//月K
                        getKLineMonthAction()
                        timeChart.visibility = View.GONE
                        kLineChart.visibility =View.VISIBLE
                    }
                }
            }

        })


        spinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0 ->{
                        if (mIsFirst){
                            mIsFirst = false
                            return
                        }
                        mRequestTag = 5 //1分钟 K
                        getKLineMinuteDataActin("m1")
                        timeChart.visibility = View.GONE
                        kLineChart.visibility =View.VISIBLE
                    }
                    1 ->{
                        mRequestTag = 6 //5分钟 K
                        getKLineMinuteDataActin("m5")
                        timeChart.visibility = View.GONE
                        kLineChart.visibility =View.VISIBLE
                    }
                    2 ->{
                        //mRequestTag = 7 //10分钟 K
                        //getKLineMinuteDataActin("m10")
                        mRequestTag = 8 //15分钟 K
                        getKLineMinuteDataActin("m15")
                        timeChart.visibility = View.GONE
                        kLineChart.visibility =View.VISIBLE
                    }
                    3 ->{
                        mRequestTag = 9 //30分钟 K
                        getKLineMinuteDataActin("m30")
                        timeChart.visibility = View.GONE
                        kLineChart.visibility =View.VISIBLE
                    }
                    4 ->{
                        mRequestTag = 10 //60分钟 K
                        getKLineMinuteDataActin("m60")
                        timeChart.visibility = View.GONE
                        kLineChart.visibility =View.VISIBLE
                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }








        /*tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tabLayout.selectedTabPosition){
                    0 -> period = klineTimeParams[0]
                    1 -> period = klineTimeParams[1]
                    2 -> period = klineTimeParams[2]
                    3 -> period = klineTimeParams[3]
                    4 -> period = klineTimeParams[4]
                    5 -> period = klineTimeParams[5]
                    6 -> period = klineTimeParams[6]
                }
                getKLineAction()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }


        })
*/


        //匿名内部类格式
       /* akv_btn_showCandle.setOnClickListener ( object :View.OnClickListener{
            override fun onClick(v: View?) {

            }
        }
        )*/
       // Lambda 若只有一个重写方法,且方法只有一个参数,参数有用,可以简写为以下格式
      /*  akv_btn_showCandle.setOnClickListener {
            v:View -> //设置数据
        }*/
        //分时图/K线图切换
        //Lambda 若只有一个重写方法,且方法只有一个参数,参数无用,可以简写为以下格式

        //副图显示切换
        akv_btn_showMinnor.setOnClickListener {
            akv_kv_kview.visibility = View.VISIBLE
            depthMapView.visibility = View.GONE

            val showMinor = akv_kv_kview.isShowMinor
            akv_kv_kview.isShowMinor = !showMinor

            if (!showMinor) {
                akv_btn_showMinnor.text = "显示副图"
            } else {
                akv_btn_showMinnor.text = "隐藏副图"
            }


        }

        //深度图
/*
        akv_btn_showDepth.setOnClickListener {

            akv_kv_kview.visibility = View.GONE
            depthMapView.visibility = View.VISIBLE

            var buy =
                    "[{\"amount\":\"516494.4900\",\"price\":\"0.000148\"},{\"amount\":\"10099.9999\",\"price\":\"0.000145\"},{\"amount\":\"28865.9722\",\"price\":\"0.000144\"},{\"amount\":\"1019275.5244\",\"price\":\"0.000143\"},{\"amount\":\"1243819.0139\",\"price\":\"0.000142\"},{\"amount\":\"2467081.9859\",\"price\":\"0.000141\"},{\"amount\":\"685328.5708\",\"price\":\"0.000140\"},{\"amount\":\"2384900.7191\",\"price\":\"0.000139\"},{\"amount\":\"210871.7389\",\"price\":\"0.000138\"},{\"amount\":\"17296.3502\",\"price\":\"0.000137\"},{\"amount\":\"123081.6173\",\"price\":\"0.000136\"},{\"amount\":\"861877.0363\",\"price\":\"0.000135\"},{\"amount\":\"598669.4028\",\"price\":\"0.000134\"},{\"amount\":\"887218.7968\",\"price\":\"0.000133\"},{\"amount\":\"2684262.8786\",\"price\":\"0.000132\"},{\"amount\":\"3122257.2515\",\"price\":\"0.000131\"},{\"amount\":\"612505.3840\",\"price\":\"0.000130\"},{\"amount\":\"71791.4728\",\"price\":\"0.000129\"},{\"amount\":\"650141.4061\",\"price\":\"0.000128\"},{\"amount\":\"454568.5038\",\"price\":\"0.000127\"},{\"amount\":\"3887016.6663\",\"price\":\"0.000126\"},{\"amount\":\"247112.8000\",\"price\":\"0.000125\"},{\"amount\":\"1572039.5160\",\"price\":\"0.000124\"},{\"amount\":\"898388.6176\",\"price\":\"0.000123\"},{\"amount\":\"30499.1802\",\"price\":\"0.000122\"},{\"amount\":\"203665.2889\",\"price\":\"0.000121\"},{\"amount\":\"482505.8332\",\"price\":\"0.000120\"},{\"amount\":\"19102.5209\",\"price\":\"0.000119\"},{\"amount\":\"1039101.6948\",\"price\":\"0.000118\"},{\"amount\":\"125602.5640\",\"price\":\"0.000117\"},{\"amount\":\"87967.2412\",\"price\":\"0.000116\"},{\"amount\":\"966166.0868\",\"price\":\"0.000115\"},{\"amount\":\"334629.8244\",\"price\":\"0.000114\"},{\"amount\":\"115053.9821\",\"price\":\"0.000113\"},{\"amount\":\"239043.7499\",\"price\":\"0.000112\"},{\"amount\":\"2481802.7026\",\"price\":\"0.000111\"},{\"amount\":\"396694.5453\",\"price\":\"0.000110\"},{\"amount\":\"8042.2018\",\"price\":\"0.000109\"},{\"amount\":\"45685.1850\",\"price\":\"0.000108\"},{\"amount\":\"276085.9811\",\"price\":\"0.000107\"},{\"amount\":\"1110069.8113\",\"price\":\"0.000106\"},{\"amount\":\"153936.1904\",\"price\":\"0.000105\"},{\"amount\":\"12069.2307\",\"price\":\"0.000104\"},{\"amount\":\"2097548.5436\",\"price\":\"0.000103\"},{\"amount\":\"1641847.0588\",\"price\":\"0.000102\"},{\"amount\":\"729310.8909\",\"price\":\"0.000101\"},{\"amount\":\"1708698.0000\",\"price\":\"0.000100\"},{\"amount\":\"2200.0000\",\"price\":\"0.000096\"},{\"amount\":\"99672.6315\",\"price\":\"0.000095\"},{\"amount\":\"606000.0000\",\"price\":\"0.000094\"}]"
            var sell =
                    "[{\"amount\":\"18998.3492\",\"price\":\"0.000149\"},{\"amount\":\"579375.2636\",\"price\":\"0.000150\"},{\"amount\":\"10000.0000\",\"price\":\"0.000151\"},{\"amount\":\"23938.1907\",\"price\":\"0.000152\"},{\"amount\":\"39437.0592\",\"price\":\"0.000153\"},{\"amount\":\"70086.8975\",\"price\":\"0.000154\"},{\"amount\":\"119481.8449\",\"price\":\"0.000155\"},{\"amount\":\"31000.0000\",\"price\":\"0.000156\"},{\"amount\":\"398481.1022\",\"price\":\"0.000157\"},{\"amount\":\"1020826.4943\",\"price\":\"0.000158\"},{\"amount\":\"19700.0000\",\"price\":\"0.000159\"},{\"amount\":\"1540570.1143\",\"price\":\"0.000160\"},{\"amount\":\"510000.0000\",\"price\":\"0.000161\"},{\"amount\":\"42297.9030\",\"price\":\"0.000162\"},{\"amount\":\"905817.4543\",\"price\":\"0.000163\"},{\"amount\":\"320918.8569\",\"price\":\"0.000164\"},{\"amount\":\"85984.6779\",\"price\":\"0.000165\"},{\"amount\":\"849310.8825\",\"price\":\"0.000166\"},{\"amount\":\"337482.8245\",\"price\":\"0.000167\"},{\"amount\":\"1058834.3325\",\"price\":\"0.000168\"},{\"amount\":\"189803.0301\",\"price\":\"0.000169\"},{\"amount\":\"1010000.0000\",\"price\":\"0.000170\"},{\"amount\":\"553919.1734\",\"price\":\"0.000171\"},{\"amount\":\"103177.0000\",\"price\":\"0.000172\"},{\"amount\":\"69936.9942\",\"price\":\"0.000173\"},{\"amount\":\"112300.0984\",\"price\":\"0.000174\"},{\"amount\":\"89297.2116\",\"price\":\"0.000175\"},{\"amount\":\"224360.1818\",\"price\":\"0.000176\"},{\"amount\":\"539730.2447\",\"price\":\"0.000177\"},{\"amount\":\"30000.0000\",\"price\":\"0.000178\"},{\"amount\":\"25469.0000\",\"price\":\"0.000179\"},{\"amount\":\"1146947.3660\",\"price\":\"0.000180\"},{\"amount\":\"1000000.0000\",\"price\":\"0.000181\"},{\"amount\":\"523562.9441\",\"price\":\"0.000183\"},{\"amount\":\"2000.0000\",\"price\":\"0.000184\"},{\"amount\":\"876996.7708\",\"price\":\"0.000185\"},{\"amount\":\"558687.6729\",\"price\":\"0.000186\"},{\"amount\":\"570457.3663\",\"price\":\"0.000187\"},{\"amount\":\"1130000.0000\",\"price\":\"0.000188\"},{\"amount\":\"397615.3157\",\"price\":\"0.000190\"},{\"amount\":\"300000.0000\",\"price\":\"0.000191\"},{\"amount\":\"100000.0000\",\"price\":\"0.000192\"},{\"amount\":\"130077.7202\",\"price\":\"0.000193\"},{\"amount\":\"6257.9403\",\"price\":\"0.000194\"},{\"amount\":\"273440.9221\",\"price\":\"0.000195\"},{\"amount\":\"649800.0000\",\"price\":\"0.000196\"},{\"amount\":\"167367.9134\",\"price\":\"0.000197\"},{\"amount\":\"1109832.5267\",\"price\":\"0.000198\"},{\"amount\":\"30570.7676\",\"price\":\"0.000199\"},{\"amount\":\"3486373.7985\",\"price\":\"0.000200\"}]"

            var buyList: MutableList<DepthBuySellData>? =
                    parseGSON(buy, object : TypeToken<ArrayList<DepthBuySellData>>() {}.type)
            var sellList: MutableList<DepthBuySellData>? =
                    parseGSON(sell, object : TypeToken<ArrayList<DepthBuySellData>>() {}.type)

            if (buyList?.isNotEmpty()!! && sellList?.isEmpty()!!) {
                sellList.add(0, DepthBuySellData("0", "0"))
            } else if (buyList.isEmpty() && sellList?.isNotEmpty()!!) {
                buyList.add(0, DepthBuySellData("0", "0"))
            }
            depthMapView.setData(buyList, sellList, "BTC", 6, 4)
        }
*/


    }

    //查询自选
    private fun queryConcernAction() {
        val map = java.util.HashMap<String, Any>()
        map["nozzle"] = MethodUrl.QUERY_CONCERN
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@CoinInfoActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["code"] = mapData!!["code"].toString()
        val mHeaderMap = java.util.HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.QUERY_CONCERN, map)
    }

    //取消自选
    private fun cancelConcernAction() {
        val map = java.util.HashMap<String, Any>()
        map["nozzle"] = MethodUrl.CANCEL_CONCERN
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@CoinInfoActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["code"] = mapData!!["code"].toString()
        val mHeaderMap = java.util.HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CANCEL_CONCERN, map)
    }

    //添加自选
    private fun addConcernAction() {
        val map = java.util.HashMap<String, Any>()
        map["nozzle"] = MethodUrl.ADD_CONCERN
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[this@CoinInfoActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""].toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["code"] = mapData!!["code"].toString()
        val mHeaderMap = java.util.HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.ADD_CONCERN, map)
    }




    //获取详情
    private fun getDetialDataAction() {
        val map = HashMap<String, String>()
        map["q"] = mapData!!["code"].toString()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MbsConstans.DETIAL_SERVER_URL, map)
    }

    //获取1分 分时图
    private fun getTimeMinuteDataActin() {
        val map = HashMap<String, String>()
        map["code"] = mapData!!["code"].toString()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MbsConstans.TIME_MINUTE_SERVER_URL, map)
    }

    //分钟K线图
    private fun  getKLineMinuteDataActin(param:String) {
        val map = HashMap<String, String>()
        map["_var"] = param+"_today"
        map["param"] = mapData!!["code"].toString()+","+param+",,320"
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MbsConstans.KLINE_MINUTE_SERVER_URL, map)
    }

    //日K线图
    private fun  getKLineDayAction() {
        val map = HashMap<String, String>()
        map["param"] = mapData!!["code"].toString()+",day,,,320,"
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MbsConstans.KLINE_DAY_SERVER_URL, map)
    }


    //周K线图
    private fun  getKLineWeekAction() {
        val map = HashMap<String, String>()
        map["param"] = mapData!!["code"].toString()+",week,,,320,"
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MbsConstans.KLINE_WEEK_SERVER_URL, map)
    }


    //月K线图
    private fun  getKLineMonthAction() {
        val map = HashMap<String, String>()
        map["param"] = mapData!!["code"].toString()+",month,,,320,"
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToRes(mHeaderMap, MbsConstans.KLINE_MONTHS_SERVER_URL, map)
    }







    @OnClick(R.id.back_img, R.id.left_back_lay,R.id.right_lay)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.left_back_lay, R.id.back_img -> finish()
            R.id.right_lay -> {
                if (right_img.isSelected){
                    //取消
                    cancelConcernAction()
                }else{
                    //添加
                    addConcernAction()
                }

            }

        }
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


    //根据 Type 解析
    fun <T> parseGSON(jsonString: String, typeOfT: Type): T? {
        var t: T? = null
        try {
            t = Gson().fromJson(jsonString, typeOfT)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return t
    }



    private val cnyRunnable = object : Runnable {
        override fun run() {
            /*//轮询 获取K线数据
            getKLineAction()

            //websocket获取K线数据
            val map = HashMap<String,String>()
            map.put("area","USDT")
            map.put("method","k_coinChart")
            map.put("range",period)
            map.put("symbol","BTC")
            getDefault().send(JSONUtil.instance.objectToJson(map))*/

            if (UtilTools.empty(mapData)){
                return
            }
            getDetialDataAction()

/*
            when(mRequestTag){
                0 ->{
                    getTimeMinuteDataActin() //1分时图
                }
                1 ->{
                    getKLineWeekAction()
                }
                2 ->{
                    getKLineDayAction()
                }
                3 ->{
                    getKLineWeekAction()
                }
                4 ->{
                    getKLineMonthAction()
                }
                5 ->{
                getKLineMinuteDataActin("m1")
                }
                6 ->{
                getKLineMinuteDataActin("m5")
                }
                7 ->{

                }
                8 ->{
                getKLineMinuteDataActin("m15")
                }
                9 ->{
                getKLineMinuteDataActin("m30")
                }
                10 ->{
                getKLineMinuteDataActin("m60")
                }

            }
*/


            handler.postDelayed(this, 5 * 1000)


        }
    }




    override fun showProgress() {

    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }



    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        when (mType) {

            MbsConstans.KLINE_MINUTE_SERVER_URL ->{
                var  result = tData["result"]!!.toString() + ""
                result = result.substring(result.indexOf("=")+1)
                val mapInfo = JSONUtil.instance.jsonMap(result)
                if (mapInfo != null){
                    if (UtilTools.empty(mapInfo["data"].toString())){
                        //akv_kv_kview.loadMoreNoData()
                        return
                    }
                    val dataMap = mapInfo["data"] as MutableMap<String,Any>
                    val  dataMap1 = dataMap[mapData!!["code"].toString()] as MutableMap<String,Any>
                    var prec :Double = 0.0
                    if (!UtilTools.empty(dataMap1["prec"].toString())){
                        prec = dataMap1["prec"].toString().replace("\"","").toDouble()
                    }
                    var resultStr:String? = null
                    when(mRequestTag){
                        0 ->  {
                            resultStr = dataMap1["m1"] .toString().replace("{","").replace("},","")
                            akv_kv_kview.isShowTimSharing = true
                        }
                        5 ->  {
                            resultStr = dataMap1["m1"] .toString().replace("{","").replace("},","")
                            akv_kv_kview.isShowTimSharing = false
                        }
                        6 ->  {
                            resultStr = dataMap1["m5"] .toString().replace("{","").replace("},","")
                            akv_kv_kview.isShowTimSharing = false
                        }
                        7 ->  {
                            //resultStr = dataMap1["m10"] .toString().replace("{","").replace("},","")
                            //akv_kv_kview.isShowTimSharing = false
                        }
                        8 ->  {
                            resultStr = dataMap1["m15"] .toString().replace("{","").replace("},","")
                            akv_kv_kview.isShowTimSharing = false
                        }
                        9 ->  {
                            resultStr = dataMap1["m30"] .toString().replace("{","").replace("},","")
                            akv_kv_kview.isShowTimSharing = false
                        }
                        10 ->  {
                            resultStr = dataMap1["m60"] .toString().replace("{","").replace("},","")
                            akv_kv_kview.isShowTimSharing = false
                        }

                    }
                    if (UtilTools.empty(resultStr)){
                        return
                    }
                    val data = JSONUtil.instance.jsonToListStr2(resultStr!!)
                    if (data != null) {
                       /* dataList.clear()
                        for (item in data){
                            val timeStr =item[0]
                            val time = dateformat!!.parse(timeStr+"00").time
                            //开收高低


                            val quotes = Quotes(item[1].toDouble(), item[3].toDouble(), item[4].toDouble(), item[2].toDouble(), time,item[5].toDouble())
                            dataList.add(quotes)
                        }

                          //设置数据
                        LogUtil.i("show","$$$$:"+dataList.size)
                        updateDataAndUI(dataList)
*/
                        LogUtil.i("show","K线格式化后数据:"+data )
                        //上证指数代码000001.IDX.SH
                        val listDataMap = HashMap<String,Any>()
                        listDataMap["data"] = data
                        val obc = JSONObject(JSONUtil.instance.toJson(listDataMap))

                        //上证指数代码000001.IDX.SH
                        kLineDatas!!.parseKlineData(obc, "000001.IDX.SH", true,prec)
                        kLineChart.setDataToChart(kLineDatas)


                    }


                }
            }

            MbsConstans.KLINE_DAY_SERVER_URL ->{
                var  result = tData["result"]!!.toString() + ""
                LogUtil.i("show","日K线原始数据:"+result)
                result = result.substring(result.indexOf("=")+1)

                val jsonObjet = JsonParser().parse(result).asJsonObject


                //val mapInfo = JSONUtil.instance.jsonMap(result)
                if (jsonObjet != null){
                    if (UtilTools.empty(jsonObjet["data"])){
                        akv_kv_kview.loadMoreNoData()
                        return
                    }
                    val dataMap = jsonObjet["data"] as JsonObject
                    val  dataMap1 = dataMap[mapData!!["code"].toString()] as JsonObject
                    akv_kv_kview.isShowTimSharing = false
                    var prec :Double = 0.0
                    if (!UtilTools.empty(dataMap1["prec"].toString())){
                        prec = dataMap1["prec"].toString().replace("\"","").toDouble()
                    }
                    if (UtilTools.empty(dataMap1["day"])){
                        return
                    }

                    val dataListAny = JSONUtil.instance.jsonToListAny(dataMap1["day"].toString().trim())
                    LogUtil.i("show","dataListAny:"+dataListAny!!.size)
                    val data = ArrayList<List<String>>()
                    for (item in dataListAny){
                        val list = ArrayList<String>()
                        list.add(item[0].toString())
                        list.add(item[1].toString())
                        list.add(item[2].toString())
                        list.add(item[3].toString())
                        list.add(item[4].toString())
                        list.add(item[5].toString())
                        data.add(list)
                    }

                    if (data != null) {
                      /*  dataList.clear()
                        for (item in data){
                            val timeStr =item[0]
                            val time = dateformat!!.parse(timeStr+"00").time
                            //开收高低
                            val quotes = Quotes(item[1].toDouble(), item[3].toDouble(), item[4].toDouble(), item[2].toDouble(), time,item[5].toDouble())
                            dataList.add(quotes)
                        }
                        //设置数据
                        LogUtil.i("show","$$$$:"+dataList.size)
                        updateDataAndUI(dataList)*/


                        LogUtil.i("show","日K线格式化后数据:"+data )
                        //上证指数代码000001.IDX.SH
                        val listDataMap = HashMap<String,Any>()
                        listDataMap["data"] = data
                        val obc = JSONObject(JSONUtil.instance.toJson(listDataMap))

                        //上证指数代码000001.IDX.SH
                        kLineDatas!!.parseKlineData(obc, "000001.IDX.SH", true,prec)
                        kLineChart.setDataToChart(kLineDatas)
                    }
                }


            }

            MbsConstans.KLINE_WEEK_SERVER_URL ->{
                var  result = tData["result"]!!.toString() + ""
                LogUtil.i("show","周K线原始数据:"+result)
                result = result.substring(result.indexOf("=")+1)
                val mapInfo = JSONUtil.instance.jsonMap(result)
                if (mapInfo != null){
                    if (UtilTools.empty(mapInfo["data"])){
                        akv_kv_kview.loadMoreNoData()
                        return
                    }
                    val dataMap = mapInfo["data"] as MutableMap<String,Any>
                    val  dataMap1 = dataMap[mapData!!["code"].toString()] as MutableMap<String,Any>
                   /* when(mRequestTag){
                        1->{ //分时图
                            akv_kv_kview.isShowTimSharing = true
                        }
                        3->{ //K线图
                            akv_kv_kview.isShowTimSharing = false
                        }
                    }*/
                    var prec :Double = 0.0
                    if (!UtilTools.empty(dataMap1["prec"].toString())){
                        prec = dataMap1["prec"].toString().replace("\"","").toDouble()
                    }
                    if (UtilTools.empty(dataMap1["week"].toString())){
                        return
                    }
                    val dataListAny = JSONUtil.instance.jsonToListAny(dataMap1["week"].toString().trim())
                    LogUtil.i("show","dataListAny:"+dataListAny!!.size)
                    val data = ArrayList<List<String>>()
                    for (item in dataListAny){
                        val list = ArrayList<String>()
                        list.add(item[0].toString())
                        list.add(item[1].toString())
                        list.add(item[2].toString())
                        list.add(item[3].toString())
                        list.add(item[4].toString())
                        list.add(item[5].toString())
                        data.add(list)
                    }
                    if (data != null) {
                    /*    dataList.clear()
                        for (item in data){
                            val timeStr =item[0]
                            val time = dateformat!!.parse(timeStr+"00").time
                            //开收高低
                            val quotes = Quotes(item[1].toDouble(), item[3].toDouble(), item[4].toDouble(), item[2].toDouble(), time,item[5].toDouble())
                            dataList.add(quotes)
                        }
                        //设置数据
                        updateDataAndUI(dataList)*/


                        LogUtil.i("show","周K线格式化后数据:"+data )
                        //上证指数代码000001.IDX.SH
                        val listDataMap = HashMap<String,Any>()
                        listDataMap["data"] = data
                        val obc = JSONObject(JSONUtil.instance.toJson(listDataMap))

                        //上证指数代码000001.IDX.SH
                        kLineDatas!!.parseKlineData(obc, "000001.IDX.SH", true,prec)
                        kLineChart.setDataToChart(kLineDatas)
                    }

                }


            }

            MbsConstans.KLINE_MONTHS_SERVER_URL ->{
                var  result = tData["result"]!!.toString() + ""
                LogUtil.i("show","月K线原始数据:"+result)
                result = result.substring(result.indexOf("=")+1)
                val mapInfo = JSONUtil.instance.jsonMap(result)
                if (mapInfo != null){
                    if (UtilTools.empty(mapInfo["data"])){
                        akv_kv_kview.loadMoreNoData()
                        return
                    }
                    val dataMap = mapInfo["data"] as MutableMap<String,Any>
                    val  dataMap1 = dataMap[mapData!!["code"].toString()] as MutableMap<String,Any>
                    akv_kv_kview.isShowTimSharing = false
                    var prec :Double = 0.0
                    if (!UtilTools.empty(dataMap1["prec"].toString())){
                        prec = dataMap1["prec"].toString().replace("\"","").toDouble()
                    }

                    if (UtilTools.empty(dataMap1["month"].toString())){
                        return
                    }
                    val dataListAny = JSONUtil.instance.jsonToListAny(dataMap1["month"].toString().trim())
                    LogUtil.i("show","dataListAny:"+dataListAny!!.size)
                    val data = ArrayList<List<String>>()
                    for (item in dataListAny){
                        val list = ArrayList<String>()
                        list.add(item[0].toString())
                        list.add(item[1].toString())
                        list.add(item[2].toString())
                        list.add(item[3].toString())
                        list.add(item[4].toString())
                        list.add(item[5].toString())
                        data.add(list)
                    }
                    if (data != null) {
                       /* dataList.clear()
                        for (item in data){
                            val timeStr =item[0]
                            val time = dateformat!!.parse(timeStr+"00").time
                            //开收高低
                            val quotes = Quotes(item[1].toDouble(), item[3].toDouble(), item[4].toDouble(), item[2].toDouble(), time,item[5].toDouble())
                            dataList.add(quotes)
                        }
                        //设置数据
                        LogUtil.i("show","$$$$:"+dataList.size)
                        updateDataAndUI(dataList)*/

                        LogUtil.i("show","月线格式化后数据:"+data )
                        //上证指数代码000001.IDX.SH
                        val listDataMap = HashMap<String,Any>()
                        listDataMap["data"] = data
                        val obc = JSONObject(JSONUtil.instance.toJson(listDataMap))

                        //上证指数代码000001.IDX.SH
                        kLineDatas!!.parseKlineData(obc, "000001.IDX.SH", true,prec)
                        kLineChart.setDataToChart(kLineDatas)
                    }


                }


            }



            MbsConstans.TIME_MINUTE_SERVER_URL ->{
                var  result = tData["result"]!!.toString() + ""
                result = result.substring(result.indexOf("=")+1)
                //val mapInfo = JSONUtil.instance.jsonMap(result)
                val jsonObjet = JsonParser().parse(result).asJsonObject

                if (jsonObjet != null){
                    val dataMap = jsonObjet["data"] as JsonObject
                    val  dataMap1 = dataMap[mapData!!["code"].toString()] as JsonObject
                    val  dataMap2 = dataMap1["data"] as JsonObject
                    val  qtMap = dataMap1["qt"] as JsonObject

                    val dataCode = JSONUtil.instance.jsonToListStr(qtMap[mapData!!["code"].toString()].toString())
                    val  date = JSONUtil.instance.jsonMap(dataMap2.toString())!!["date"].toString()

                    val data = JSONUtil.instance.jsonToListStr(dataMap2["data"].toString())

                    LogUtil.i("show","分时图原始数据数量:"+data!!.size )

                    if (data != null) {

                        val listData = ArrayList<List<Any>>()
                        var sumPrice = 0.0
                        for (i in data.indices){
                           val array = data[i].split(" ").toTypedArray()
                            val listChild = ArrayList<Any>()
                            val timeStr = date+array[0].replace("\"","")
                            val time = dateformat!!.parse(timeStr+"00").time
                            sumPrice += array[1].toDouble()
                            listChild.add(time.toString())  //时间
                            listChild.add(array[1]) //当前价
                            listChild.add((sumPrice/(i+1)).toString())//平均价
                            if (i == 0){
                                 //成交量
                                listChild.add(array[2])
                            }else{
                                val array2 = data[i-1].split(" ").toTypedArray()
                                //成交量
                                listChild.add((array[2].toDouble() - array2[2].toDouble()).toString())
                            }
                            listChild.add(dataCode!![4]) //左收
                            listData.add(listChild)

                        }

                        LogUtil.i("show","格式化后数据:"+JSONUtil.instance.toJson(listData)   )
                        //上证指数代码000001.IDX.SH
                        val listDataMap = HashMap<String,Any>()
                        listDataMap["data"] = JSONUtil.instance.toJson(listData)

                        val obc = JSONObject(JSONUtil.instance.toJson(listDataMap))

                        kTimeData.parseTimeData(obc, "000001.IDX.SH", 0.0)
                        timeChart.setDataToChart(kTimeData)


                    }


                }

            }

            MbsConstans.DETIAL_SERVER_URL-> {
                val result = tData["result"]!!.toString() + ""
                handInfoData(result)
                if(!UtilTools.empty(stockInfoBean)){
                    topPriceTv.text = stockInfoBean!!.stockCurrentPrice
                    if (stockInfoBean!!.stockRiseAndFallAmplitude.contains("-")){
                        topAmountTv.text = stockInfoBean!!.stockRiseAndFallAmount
                        topRatioTv.text = stockInfoBean!!.stockRiseAndFallAmplitude+"%"
                    }else{
                        topAmountTv.text ="+"+ stockInfoBean!!.stockRiseAndFallAmount
                        topRatioTv.text ="+" +stockInfoBean!!.stockRiseAndFallAmplitude+"%"
                    }


                    tv1.text = stockInfoBean!!.stockHighestPrice
                    tv2.text = stockInfoBean!!.stockTodayPrice
                    tv3.text = stockInfoBean!!.stockRiseTop
                    tv4.text = stockInfoBean!!.stockAveragePrice
                    tv5.text = stockInfoBean!!.stockLowestPrice
                    tv6.text = stockInfoBean!!.stockYesterdayPrice
                    tv7.text = stockInfoBean!!.stockRiseTop
                    tv8.text = stockInfoBean!!.stockAmplitude+"%"
                    tv9.text = stockInfoBean!!.stockLiangbi
                    tv10.text = stockInfoBean!!.stockPriceEarningsRatio
                    tv11.text = stockInfoBean!!.stockTransactionVolume+"手"
                    tv12.text = stockInfoBean!!.stockTotalMarketValue+"亿"
                    tv13.text = stockInfoBean!!.stockEntrustRatio+"%"
                    tv14.text = stockInfoBean!!.stockTurnoverRate+"%"
                    tv15.text = stockInfoBean!!.stockTransactionAccount+"万"
                    tv16.text = stockInfoBean!!.stockCirculationMarketValue+"亿"

                }

                if ( buyData.size >0){
                    if (mBuyadapter == null){
                        mBuyadapter = BuyAndSellAdapter(this@CoinInfoActivity)
                    }
                    mBuyadapter!!.clear()
                    mBuyadapter!!.addAll(buyData)
                    rvBuy.adapter = mBuyadapter
                }

                if ( sellData.size >0){
                    if (mSelladapter == null){
                        mSelladapter = BuyAndSellAdapter(this@CoinInfoActivity)
                    }
                    mSelladapter!!.clear()
                    mSelladapter!!.addAll(sellData)
                    rvSell.adapter = mSelladapter
                }


            }

            MethodUrl.QUERY_CONCERN -> when (tData["code"].toString() + "") {
                "1" -> {
                    if (tData["data"].toString() == "0"){ //加入
                        right_img.isSelected = false
                        right_img.setBackgroundResource(R.drawable.zixuan_unselected)
                    }else{//已加入
                        right_img.isSelected = true
                        right_img.setBackgroundResource(R.drawable.zixuan_selected)
                    }
                }
                "0" -> TipsToast.showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@CoinInfoActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.CANCEL_CONCERN -> when (tData["code"].toString() + "") {
                "1" -> {
                    TipsToast.showToastMsg(tData["msg"].toString() + "")
                    right_img.isSelected = false
                    right_img.setBackgroundResource(R.drawable.zixuan_unselected)
                }
                "0" -> TipsToast.showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@CoinInfoActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.ADD_CONCERN -> when (tData["code"].toString() + "") {
                "1" -> {
                    TipsToast.showToastMsg(tData["msg"].toString() + "")
                    right_img.isSelected = true
                    right_img.setBackgroundResource(R.drawable.zixuan_selected)
                }
                "0" -> TipsToast.showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@CoinInfoActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }







            MethodUrl.K_LINE -> {
                val str = tData["data"].toString()
                kLineData = JSONUtil.instance.jsonToListStr2(str) as ArrayList<List<String>>
                dataList.clear()
                for (item in kLineData) {
                    //val quotes = Quotes(item[1].toDouble(), item[2].toDouble(), item[3].toDouble(), item[4].toDouble(), item[0].toLong() * 1000)
                    val quotes = Quotes(item[1].toDouble(), item[2].toDouble(), item[3].toDouble(), item[4].toDouble(), item[0].toLong() * 1000)

                    dataList.add(quotes)
                    //akv_kv_kview.pushKViewData(quotes, 0)

                }

                //设置数据
                updateDataAndUI(dataList)

            }
        }
    }



    //拓展属性
    /* var Quotes.a:Double
        set(value) {
        }
        get() = a*/

    //拓展方法
    fun Quotes.getAmount(curQuotes: Quotes): Double {
        for (item in kLineData) {
            if (
                    curQuotes.o == item[1].toDouble()
                    && curQuotes.h == item[2].toDouble()
                    && curQuotes.l == item[3].toDouble()
                    && curQuotes.c == item[4].toDouble()
                    && curQuotes.t == item[0].toLong() * 1000
            ) {
                return item[5].toDouble()
            }
        }
        return 0.00
    }


    private fun updateDataAndUI(dataList: ArrayList<Quotes>) {
        if (dataList.size > 0) {

            akv_kv_kview.setKViewData(dataList, object : KViewListener.MasterTouchListener {
                override fun onLongTouch(preQuotes: Quotes, currentQuotes: Quotes) {
                    //长按
                    showContanier(preQuotes, currentQuotes)
                }

                override fun onUnLongTouch() {

                }

                override fun needLoadMore() {
                    //滑动加载更多?
                    //showProgressDialog()
                    //getKLineAction()
                }
            })

        }


    }

    private fun showContanier(preQuotes: Quotes, currentQuotes: Quotes) {


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
                stockInfoBean!!.stockLiangbi = split[49]
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
                map7["amount"] = stockInfoBean!!.stockSell1Amount
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


    private fun loadIndexData(type: Int) {
        indexType = type
        when (indexType) {
            1 -> kLineChart.doBarChartSwitch(indexType)
            2 -> {
                kLineDatas!!.initMACD()
                kLineChart.doBarChartSwitch(indexType)
            }
            3 -> {
                kLineDatas!!.initKDJ()
                kLineChart.doBarChartSwitch(indexType)
            }
            4 -> {
                kLineDatas!!.initBOLL()
                kLineChart.doBarChartSwitch(indexType)
            }
            5 -> {
                kLineDatas!!.initRSI()
                kLineChart.doBarChartSwitch(indexType)
            }
            else -> {
            }
        }
    }


}