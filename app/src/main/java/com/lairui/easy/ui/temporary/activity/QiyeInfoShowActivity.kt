package com.lairui.easy.ui.temporary.activity

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
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil


import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 企业基本信息展示  界面
 */
class QiyeInfoShowActivity : BasicActivity(), RequestView, ReLoadingData {


    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.right_text_tv)
    lateinit var mRightTextTv: TextView
    @BindView(R.id.right_img)
    lateinit var mRightImg: ImageView
    @BindView(R.id.right_lay)
    lateinit var mRightLay: LinearLayout
    @BindView(R.id.qiye_name_tv)
    lateinit var mQiyeNameTv: TextView
    @BindView(R.id.qiye_daima_tv)
    lateinit var mQiyeDaimaTv: TextView
    @BindView(R.id.zhuce_hao_tv)
    lateinit var mZhuceHaoTv: TextView
    @BindView(R.id.qiye_fading_name_tv)
    lateinit var mQiyeFadingNameTv: TextView
    @BindView(R.id.qiye_zhuce_money_tv)
    lateinit var mQiyeZhuceMoneyTv: TextView
    @BindView(R.id.qiye_really_money_tv)
    lateinit var mQiyeReallyMoneyTv: TextView
    @BindView(R.id.qiye_jingying_status_tv)
    lateinit var mQiyeJingyingStatusTv: TextView
    @BindView(R.id.qiye_kind_tv)
    lateinit var mQiyeKindTv: TextView
    @BindView(R.id.qiye_hangye_tv)
    lateinit var mQiyeHangyeTv: TextView
    @BindView(R.id.qiye_open_date_tv)
    lateinit var mQiyeOpenDateTv: TextView
    @BindView(R.id.qiye_jingying_qixian_tv)
    lateinit var mQiyeJingyingQixianTv: TextView
    @BindView(R.id.qiye_address_tv)
    lateinit var mQiyeAddressTv: TextView
    @BindView(R.id.qiye_dengji_tv)
    lateinit var mQiyeDengjiTv: TextView
    @BindView(R.id.qiye_all_pro_tv)
    lateinit var mQiyeAllProTv: TextView
    @BindView(R.id.qiye_normal_pro_tv)
    lateinit var mQiyeNormalProTv: TextView
    @BindView(R.id.qiye_jingying_fanwei_tv)
    lateinit var mQiyeJingyingFanweiTv: TextView
    @BindView(R.id.btn_submit)
    lateinit var mBtnSubmit: Button
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout

    private var mRequestTag = ""

    private var mBackType: String? = ""

    override val contentView: Int
        get() = R.layout.activity_qiye_info_show

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.qiye_info)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mBackType = bundle.getString("backtype")
        }

        if (mBackType == "2") {
            mBtnSubmit!!.text = "返回"
        }

        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.reLoadingData = this
        mPageView!!.showLoading()
        getCompanyInfo()
    }


    private fun getCompanyInfo() {
        mRequestTag = MethodUrl.companyInfo
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.companyInfo, map)
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.companyInfo ->

                initValueTv(tData)
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.companyInfo -> {
                    }
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        mPageView!!.showNetworkError()
        dealFailInfo(map, mType)
    }


    //{
    //	"custid": "1910715000014349",
    //	"addr": "湖南省怀化市鹤城区锦溪南路岳麓欧城一期第17栋1904号",
    //	"busipermitem": "日用百货、五金交电、厨具、办公用品、服装、家用电器、电子产品、宠物用品、宠物食品、宠物药品、保健品的批发及零售。（依法须经批准的项目，经相关部门批准后方可开展经营活动）",
    //	"district": "431202",
    //	"eptype": null,
    //	"genebusiitem": "日用百货、五金交电、厨具、办公用品、服装、家用电器、电子产品、宠物用品、宠物食品、宠物药品、保健品的批发及零售。（依法须经批准的项目，经相关部门批准后方可开展经营活动）",
    //	"opendate": "20160601",
    //	"operateplace": "百货零售",
    //	"opercap": "在营（开业）",
    //	"opteend": null,
    //	"optescope": "日用百货、五金交电、厨具、办公用品、服装、家用电器、电子产品、宠物用品、宠物食品、宠物药品、保健品的批发及零售。（依法须经批准的项目，经相关部门批准后方可开展经营活动）",
    //	"optestart": "20160601",
    //	"paidmny": null,
    //	"registmny": "200.000000",
    //	"registno": "431200000079989",
    //	"regorg": "怀化市工商行政管理局鹤城分局",
    //	"regorgcode": "431202",
    //	"regorgprov": "湖南省"
    //}
    private fun initValueTv(tData: MutableMap<String, Any>) {

        //企业名称
        var firmname = tData["firmname"]!!.toString() + ""
        if (UtilTools.empty(firmname)) {
            firmname = ""
        }
        //统一社会信用代码
        var zuzhjgdm = tData["zuzhjgdm"]!!.toString() + ""
        if (UtilTools.empty(zuzhjgdm)) {
            zuzhjgdm = ""
        }
        //注册号
        var registno = tData["registno"]!!.toString() + ""
        if (UtilTools.empty(registno)) {
            registno = ""
        }
        //法定代表人名称
        var farnname = tData["farnname"]!!.toString() + ""
        if (UtilTools.empty(farnname)) {
            farnname = ""
        }
        //注册资本
        var registmny = tData["registmny"]!!.toString() + ""
        if (UtilTools.empty(registmny)) {
            registmny = ""
        }
        //实收资本
        var paidmny = tData["paidmny"]!!.toString() + ""
        if (UtilTools.empty(paidmny)) {
            paidmny = ""
        }
        //经营状态
        var opercap = tData["opercap"]!!.toString() + ""
        if (UtilTools.empty(opercap)) {
            opercap = ""
        }
        //企业类型
        var eptype = tData["eptype"]!!.toString() + ""
        if (UtilTools.empty(eptype)) {
            eptype = ""
        }

        //所属行业
        var operateplace = tData["operateplace"]!!.toString() + ""
        if (UtilTools.empty(operateplace)) {
            operateplace = ""
        }
        //开业日期
        var opendate = tData["opendate"]!!.toString() + ""
        if (UtilTools.empty(opendate)) {
            opendate = ""
        }
        //经营期限
        /*String eptype = tData.get("eptype")+"";
        if (UtilTools.empty(eptype)){
            eptype = "";
        }*/
        //企业地址
        var addr = tData["addr"]!!.toString() + ""
        if (UtilTools.empty(addr)) {
            addr = ""
        }
        //登记机关
        var regorg = tData["regorg"]!!.toString() + ""
        if (UtilTools.empty(regorg)) {
            regorg = ""
        }
        //许可经营项目
        var busipermitem = tData["busipermitem"]!!.toString() + ""
        if (UtilTools.empty(busipermitem)) {
            busipermitem = ""
        }
        //一般经营项目
        var genebusiitem = tData["genebusiitem"]!!.toString() + ""
        if (UtilTools.empty(genebusiitem)) {
            genebusiitem = ""
        }
        //经营范围
        var optescope = tData["optescope"]!!.toString() + ""
        if (UtilTools.empty(optescope)) {
            optescope = ""
        }

        mQiyeNameTv!!.text = firmname//企业名称
        mQiyeDaimaTv!!.text = zuzhjgdm//统一社会信用代码
        mZhuceHaoTv!!.text = registno//注册号
        mQiyeFadingNameTv!!.text = farnname//法定代表人名称
        mQiyeZhuceMoneyTv!!.text = registmny//注册资本
        mQiyeReallyMoneyTv!!.text = paidmny//实收资本
        mQiyeJingyingStatusTv!!.text = opercap//经营状态 -------------
        mQiyeKindTv!!.text = eptype//企业类型  --------
        mQiyeHangyeTv!!.text = operateplace//所属行业   -----------
        mQiyeOpenDateTv!!.text = opendate//开业日期
        mQiyeJingyingQixianTv!!.text = ""//经营期限  ------------------
        mQiyeAddressTv!!.text = addr//企业地址
        mQiyeDengjiTv!!.text = regorg//登记机关
        mQiyeAllProTv!!.text = busipermitem//许可经营项目
        mQiyeNormalProTv!!.text = genebusiitem//一般经营项目
        mQiyeJingyingFanweiTv!!.text = optescope//经营范围


        mPageView!!.showContent()

    }


    @OnClick(R.id.left_back_lay, R.id.btn_submit)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.left_back_lay -> finish()
            R.id.btn_submit -> if (mBackType == "2") {
                finish()
            } else {
                intent = Intent(this@QiyeInfoShowActivity, QiyeCardInfoActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun reLoadingData() {
        getCompanyInfo()
    }
}
