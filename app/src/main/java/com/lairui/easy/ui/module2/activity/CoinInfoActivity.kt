package com.lairui.easy.ui.module2.activity

import android.view.View
import androidx.core.content.ContextCompat
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.depthview.DepthBuySellData
import com.lairui.easy.mywidget.kview.KViewListener
import com.lairui.easy.mywidget.kview.Quotes
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.UtilTools
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jaeger.library.StatusBarUtil
import com.lairui.easy.utils.tool.SelectDataUtil
import com.zhangke.websocket.SocketListener
import com.zhangke.websocket.WebSocketHandler.getDefault
import com.zhangke.websocket.response.ErrorResponse
import kotlinx.android.synthetic.main.activity_coin_info.*
import kotlinx.android.synthetic.main.title_leftbut_bar.*
import org.java_websocket.framing.Framedata
import java.lang.reflect.Type
import java.nio.ByteBuffer
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CoinInfoActivity : BasicActivity(), RequestView {

    private var handler = android.os.Handler()
    private var dataList = ArrayList<Quotes>()
    private var kLineData = ArrayList<List<String>>()

    private var period:String = "60"

    //不支持滑动返回
    override fun isSupportSwipeBack(): Boolean {
        return false
    }



    override val contentView: Int
        get() = R.layout.activity_coin_info

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        title_text.setText("行情")
        divide_line.visibility = View.GONE

        val timeChoose = resources.getStringArray(R.array.timeChoose)
        val klineTimeParams = resources.getStringArray(R.array.klineTimeParams)

        val mKLineData = SelectDataUtil.kLineParams
        for (item in mKLineData){
            tabLayout.addTab(tabLayout.newTab().setText(item.get("name") as String))
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
        akv_btn_showCandle.setOnClickListener {
            akv_kv_kview.visibility = View.VISIBLE
            depthMapView.visibility = View.GONE

            val showTimSharing = akv_kv_kview.isShowTimSharing
            akv_kv_kview.isShowTimSharing = !showTimSharing
            //设置数据
            updateDataAndUI(dataList)

            if (!showTimSharing) {
                akv_btn_showCandle.text = "切换到K线图"
            } else {
                akv_btn_showCandle.text = "切换到分时图"
            }

        }
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


        getDefault().addListener(object : SocketListener{
            override fun onConnectFailed(e: Throwable?) {
                LogUtil.i("show","ws ConnectFailed &&&")
                getDefault().reconnect()
            }

            override fun onSendDataError(errorResponse: ErrorResponse?) {
                LogUtil.i("show"," ws errorResponse:"+errorResponse!!.errorCode)
            }

            override fun onConnected() {
                LogUtil.i("show","ws onConnected &&&")
            }

            override fun <T : Any?> onMessage(message: String?, data: T) {
                LogUtil.i("show","Kline Message:"+message)

            }

            override fun <T : Any?> onMessage(bytes: ByteBuffer?, data: T) {

            }

            override fun onDisconnect() {
                LogUtil.i("show","ws onDisconnect &&&")
                getDefault().reconnect()

            }

            override fun onPong(framedata: Framedata?) {

            }

            override fun onPing(framedata: Framedata?) {

            }

        })


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
            //轮询 获取K线数据
            getKLineAction()

            //websocket获取K线数据
            val map = HashMap<String,String>()
            map.put("area","USDT")
            map.put("method","k_coinChart")
            map.put("range",period)
            map.put("symbol","BTC")
            getDefault().send(JSONUtil.instance.objectToJson(map))
            handler.postDelayed(this, 60 * 1000)


        }
    }


    private fun getKLineAction() {
        val map = HashMap<String, String>()
        map["symbol"] = "btc_usdt"
        map["period"] = period
        val headerMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToMap(headerMap, MethodUrl.K_LINE, map)

    }




    override fun showProgress() {

    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.K_LINE -> {
                val str = tData["data"].toString()
                kLineData = JSONUtil.instance.jsonToListStr2(str) as ArrayList<List<String>>
                dataList.clear()
                for (item in kLineData) {
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
                    akv_ll_container.visibility = View.INVISIBLE
                }

                override fun needLoadMore() {
                    //滑动加载更多?
                    showProgressDialog()
                    getKLineAction()
                }
            })

        }


    }

    private fun showContanier(preQuotes: Quotes, currentQuotes: Quotes) {
        akv_ll_container.visibility = View.VISIBLE
        val digits = 4
        var isPositive: Boolean
        var precent: String
        var dis = (currentQuotes.c - preQuotes.c) / currentQuotes.c * 100
        isPositive = dis >= 0
        precent = UtilTools.formatBySubString(dis, 2)
        precent += "%"

        akv_tv_h.text = UtilTools.numFormat(currentQuotes.h, digits)
        akv_tv_o.text = UtilTools.numFormat(currentQuotes.o, digits)
        akv_tv_l.text = UtilTools.numFormat(currentQuotes.l, digits)
        akv_tv_c.text = UtilTools.numFormat(currentQuotes.c, digits)
        akv_tv_amount.text = UtilTools.numFormat(currentQuotes.getAmount(currentQuotes), 2)

        akv_tv_p.text = precent
        if (isPositive) {
            akv_tv_h.setTextColor(resources.getColor(R.color.color_timeSharing_callBackRed))
            akv_tv_o.setTextColor(resources.getColor(R.color.color_timeSharing_callBackRed))
            akv_tv_l.setTextColor(resources.getColor(R.color.color_timeSharing_callBackRed))
            akv_tv_c.setTextColor(resources.getColor(R.color.color_timeSharing_callBackRed))
            akv_tv_p.setTextColor(resources.getColor(R.color.color_timeSharing_callBackRed))
            akv_tv_amount.setTextColor(resources.getColor(R.color.color_timeSharing_callBackRed))
        } else {
            akv_tv_h.setTextColor(resources.getColor(R.color.color_timeSharing_callBackGreen))
            akv_tv_o.setTextColor(resources.getColor(R.color.color_timeSharing_callBackGreen))
            akv_tv_l.setTextColor(resources.getColor(R.color.color_timeSharing_callBackGreen))
            akv_tv_c.setTextColor(resources.getColor(R.color.color_timeSharing_callBackGreen))
            akv_tv_p.setTextColor(resources.getColor(R.color.color_timeSharing_callBackGreen))
            akv_tv_amount.setTextColor(resources.getColor(R.color.color_timeSharing_callBackGreen))

        }


    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)

    }


}