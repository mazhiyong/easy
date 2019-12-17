package com.lairui.easy.ui.temporary.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message

import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.github.jdsjlzx.ItemDecoration.DividerDecoration
import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.api.ErrorHandler
import com.lairui.easy.mywidget.dialog.TipMsgDialog
import com.lairui.easy.ui.temporary.adapter.JkHetongAdapter
import com.lairui.easy.ui.temporary.adapter.ShouKuanRenAdapter
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mywidget.dialog.MySelectDialog
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.utils.tool.FileUtils
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig


import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.MainActivity
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlin.collections.MutableMap as MutableMap1

/**
 * 我要借款 界面
 */
class BorrowMoneyActivity : BasicActivity(), RequestView, SelectBackListener {

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
    @BindView(R.id.money_edit)
    lateinit var mMoneyEdit: EditText
    @BindView(R.id.qixian_value_tv)
    lateinit var mQixianValueTv: TextView
    @BindView(R.id.lixi_valut_tv)
    lateinit var mLixiValutTv: TextView
    @BindView(R.id.qixian_lay)
    lateinit var mQixianLay: CardView
    @BindView(R.id.jiekuan_yongtu_edit)
    lateinit var mJiekuanYongtuEdit: EditText
    @BindView(R.id.shoukuan_list)
    lateinit var mShoukuanList: LRecyclerView
    @BindView(R.id.add_shoukuan_people_lay)
    lateinit var mAddShoukuanPeopleLay: CardView
    @BindView(R.id.has_upload_tv)
    lateinit var mHasUploadTv: TextView
    @BindView(R.id.add_file_tv)
    lateinit var mAddFileTv: TextView
    @BindView(R.id.file_num_tv)
    lateinit var mFileNumTv: TextView
    @BindView(R.id.qixian_edit)
    lateinit var mQixianEidt: EditText
    @BindView(R.id.fujian_lay)
    lateinit var mFujianLay: CardView
    @BindView(R.id.but_submit)
    lateinit var mButSubmit: Button
    @BindView(R.id.scrollView_content)
    lateinit var mScrollViewContent: NestedScrollView
    @BindView(R.id.hetong_recycleview)
    lateinit var mHetongRceycleView: RecyclerView
    @BindView(R.id.hetong_lay)
    lateinit var mHetongLay: LinearLayout
    @BindView(R.id.shoukanren_lay)
    lateinit var mShouKuanrenLay: LinearLayout
    @BindView(R.id.but_agree)
    lateinit var mButAgree: Button

    @BindView(R.id.money_tips_tv)
    lateinit var mMoneyTipsTv: TextView
    @BindView(R.id.lilv_value_tv)
    lateinit var mLilvValueTv: TextView

    @BindView(R.id.qixian_arrow_view)
    lateinit var mQixianArrowView: ImageView

    @BindView(R.id.fujian_content_lay)
    lateinit var mFujianContentLay: LinearLayout

    @BindView(R.id.tv_add_amount)
    lateinit var mAddTv: TextView
    @BindView(R.id.danwei_tv)
    lateinit var mDanweiTv: TextView


    // private Map<String, Object> mMoneyMap;
    private lateinit var mHezuoMap: MutableMap1<String, Any>
    private lateinit var mConfigMap: MutableMap1<String, Any>
    private lateinit var mQixianMap: MutableMap1<String, Any>


    private lateinit var mParamMap: MutableMap1<String, Any>
    private var mType: String? = ""

    private var mPeopleList: MutableList<MutableMap1<String, Any>>? = ArrayList()

    private lateinit var mSwipeMenuAdapter: ShouKuanRenAdapter
    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter

    private var mRequestTag = ""
    private lateinit var mQixianDialog: MySelectDialog


    private var mQianxianMax = 0
    private var mPayCompayName: String? = ""
    private var mPaycustid = ""


    override val contentView: Int
        get() = R.layout.activity_borrow_money


    private lateinit var mHetongList: List<MutableMap1<String, Any>>
    private lateinit var mJkHetongAdapter: JkHetongAdapter


    private/* String danwei = mConfigMap.get("limitunit")+"";
        String qixianMax = mConfigMap.get("loanlimit")+"";

        String mm = mMoneyEdit.getText()+"";
        if (mQixianMap == null || UtilTools.empty(mm)){
            return 0;
        }

        double money = Double.valueOf(mm);



        double nianlilv =Double.valueOf(mConfigMap.get("daiklilv")+"") ;
        double  lixi = 0;
        double day = Double.valueOf(mQixianMap.get("code")+"");
        switch (danwei){
            case "1"://借款期限单位（1：年 2：月 3：日）
                lixi = UtilTools.mul(nianlilv,day);//年利率乘以多少年
                lixi = UtilTools.mul(lixi,money);//然后乘以本金
                break;
            case "2":
                lixi = UtilTools.divide2(nianlilv,12);//换算成月利率
                lixi = UtilTools.mul(lixi,day);//乘以多少月
                lixi = UtilTools.mul(lixi,money);//然后乘以本金
                LogUtil.i("show",lixi+"####################"+UtilTools.divide(nianlilv,100)+" "+UtilTools.divide(UtilTools.divide(nianlilv,100),12));
                break;
            case "3":
                double ll = UtilTools.divide2(nianlilv,12);//换算成月利率
                ll = UtilTools.divide2(ll,30);//换算成日利率
                lixi = UtilTools.mul(ll,day);//然后乘以多少天
                lixi = UtilTools.mul(lixi,money);//然后乘以本金
                break;
        }

         return lixi; */ val lixi: Double
        get() = 0.0


    private val mFileList = ArrayList<MutableMap1<String, Any>>()
    private var mFileNum = 0
    //广播监听
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val b = intent.extras
            if (MbsConstans.BroadcastReceiverAction.FILE_TIP_ACTION == action) {

                if (b != null) {
                    val list = b.getSerializable("DATA") as List<MutableMap1<String, Any>>
                    var num = 0
                    if (list != null) {
                        mConfigMap!!["contList"] = list
                        mFileList.clear()
                        for (map in list) {
                            val resultMap = HashMap<String, Any>()
                            resultMap["filetype"] = map["filetype"]!!.toString() + ""
                            resultMap["name"] = map["name"]!!.toString() + ""
                            var files = map["resultData"] as List<MutableMap1<String, Any>>?
                            if (files != null) {
                                resultMap["files"] = files
                                num = num + files.size
                            } else {
                                files = ArrayList()
                                resultMap["files"] = files
                            }
                            mFileList.add(resultMap)
                        }
                    }

                    if (num != 0) {
                        mAddFileTv!!.visibility = View.GONE
                        mHasUploadTv!!.visibility = View.VISIBLE
                        mFileNumTv!!.visibility = View.VISIBLE

                    } else {
                        mAddFileTv!!.visibility = View.VISIBLE
                        mHasUploadTv!!.visibility = View.GONE
                        mFileNumTv!!.visibility = View.GONE

                    }
                    mFileNum = num
                    mFileNumTv!!.text = num.toString() + "个"
                    LogUtil.i("show", "上传文件列表$mFileList")
                }
            } else if (action == MbsConstans.BroadcastReceiverAction.JIE_HUAN_UPDATE) {
                finish()
            }
        }
    }

    /**
     * -----------------------------------  人脸识别  ------------------------------------------------
     */


    private var isCheck = false


    @SuppressLint("HandlerLeak")
    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            when (msg.what) {
                1 -> enterNextPage()
                2 -> {
                    showToastMsg("人脸验证授权失败")
                    mButSubmit!!.isEnabled = true
                }
            }
        }
    }


    private lateinit var mTotalMoneyDialog: TipMsgDialog

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            //mMoneyMap = (Map<String, Object>) bundle.getSerializable("MONEY");

            if (bundle.containsKey("TYPE")) {
                mConfigMap = bundle.getSerializable("CONFIG") as MutableMap1<String, Any>
                mHetongList = bundle.getSerializable("HETONG") as List<MutableMap1<String, Any>>
                mParamMap = bundle.getSerializable("PARAM") as MutableMap1<String, Any>
                mType = bundle.getString("TYPE")
            } else {
                mHezuoMap = bundle.getSerializable("DATA") as MutableMap1<String, Any>
            }

            mPayCompayName = bundle.getString("payfirmname")
            mPaycustid = bundle.getString("paycustid", "")
        }
        mTitleText!!.text = resources.getString(R.string.tikuan_title)
        UtilTools.setMoneyEdit(mMoneyEdit!!, 0.0)

        //List<Map<String, Object>> list = SelectDataUtil.jieKuanLimit();
        val list = SelectDataUtil.getNameCodeByType("loanLimit")
        mQixianDialog = MySelectDialog(this, true, list, "选择期限", 10)
        mQixianDialog!!.selectBackListener = this

        val filter = IntentFilter()
        filter.addAction(MbsConstans.BroadcastReceiverAction.FILE_TIP_ACTION)
        filter.addAction(MbsConstans.BroadcastReceiverAction.JIE_HUAN_UPDATE)

        registerReceiver(receiver, filter)

        mAddFileTv!!.visibility = View.VISIBLE
        mHasUploadTv!!.visibility = View.GONE
        mFileNumTv!!.visibility = View.GONE


        val manager = LinearLayoutManager(this)
        manager.orientation = RecyclerView.VERTICAL
        mShoukuanList!!.layoutManager = manager


        if (mType == "1") {

            mMoneyTipsTv!!.text = "最多可借款:" + UtilTools.getRMBMoney(mConfigMap!!["maxamt"]!!.toString() + "")
            mLilvValueTv!!.text = resources.getString(R.string.borrow_lilu) + UtilTools.getlilv(mConfigMap!!["daiklilv"]!!.toString() + "")


            val danwei = mParamMap!!["limitunit"]!!.toString() + ""
            //Map<String,Object> danWeiMap = SelectDataUtil.getMap(danwei,SelectDataUtil.getQixianDw());
            val danWeiMap = SelectDataUtil.getMap(danwei, SelectDataUtil.getNameCodeByType("limitUnit"))

            //mParamMap.put("limitunit", danwei);//借款期限单位
            mMoneyEdit!!.setText(mParamMap!!["reqmoney"]!!.toString() + "")
            mQixianValueTv!!.text = mParamMap!!["loanlimit"].toString() + "" + danWeiMap["name"]
            mJiekuanYongtuEdit!!.setText(mParamMap!!["loanuse"]!!.toString() + "")

            mPeopleList = mParamMap!!["stitems"] as MutableList<MutableMap1<String, Any>>?
            if (mPeopleList == null || mPeopleList!!.size == 0) {
                mShouKuanrenLay!!.visibility = View.GONE
            } else {
                mShouKuanrenLay!!.visibility = View.VISIBLE
            }
            responseData()

            val fileList = mParamMap!!["contList"] as List<MutableMap1<String, Any>>?

            var num = 0
            for (mm in fileList!!) {
                val fileMap = mm["files"] as List<MutableMap1<String, Any>>?
                if (fileMap != null) {
                    num = num + fileMap.size
                }
            }
            if (num != 0) {
                mFujianContentLay!!.visibility = View.VISIBLE
                mAddFileTv!!.visibility = View.GONE
                mHasUploadTv!!.visibility = View.VISIBLE
                mFileNumTv!!.visibility = View.VISIBLE
            } else {
                mFujianContentLay!!.visibility = View.GONE
                mAddFileTv!!.visibility = View.VISIBLE
                mHasUploadTv!!.visibility = View.GONE
                mFileNumTv!!.visibility = View.GONE

            }
            mFileNumTv!!.text = num.toString() + "个"
            LogUtil.i("show", "上传文件列表$mFileList")

            initHetongList()

            mMoneyEdit!!.isEnabled = false
            mQixianLay!!.isEnabled = false
            mJiekuanYongtuEdit!!.isEnabled = false
            mShoukuanList!!.isEnabled = false
            mAddShoukuanPeopleLay!!.isEnabled = false
            mFujianLay!!.isEnabled = false
            mQixianEidt!!.visibility = View.GONE
            mButSubmit!!.visibility = View.GONE
            mAddShoukuanPeopleLay!!.visibility = View.GONE
            mHetongLay!!.visibility = View.VISIBLE
            mButAgree!!.visibility = View.VISIBLE
            mQixianArrowView!!.visibility = View.GONE
        } else {
            getConfigAction()
        }

        //mQixianArrowView.setVisibility(View.GONE);
        //mJiekuanYongtuEdit.setEnabled(false);

    }

    override fun onResume() {
        super.onResume()
        if (mIsRefresh) {
            getConfigAction()
            mIsRefresh = false
        }
    }


    override fun viewEnable() {
        mButSubmit!!.isEnabled = true
        mButAgree!!.isEnabled = true
    }

    /**
     * 查询借款申请配置
     */
    private fun getConfigAction() {

        mRequestTag = MethodUrl.jiekuanConfig
        val map = HashMap<String, String>()
        map["patncode"] = mHezuoMap!!["patncode"]!!.toString() + ""
        map["zifangbho"] = mHezuoMap!!["zifangbho"]!!.toString() + ""
        map["creditno"] = mHezuoMap!!["creditno"]!!.toString() + ""
        map["paycustid"] = mPaycustid

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.jiekuanConfig, map)
    }


    private fun createHetong() {

        if (UtilTools.isEmpty(mMoneyEdit!!, "金额")) {
            showToastMsg("金额不能为空")
            mButSubmit!!.isEnabled = true
            return
        }


        val maxMStr = mConfigMap!!["maxamt"]!!.toString() + ""
        var maxMoney = java.lang.Double.valueOf(maxMStr)
        maxMoney = UtilTools.divide(maxMoney, 100.0)
        val editMoney = java.lang.Double.valueOf(mMoneyEdit!!.text.toString() + "")
        if (editMoney > maxMoney) {
            showToastMsg("输入金额不能大于" + UtilTools.fromDouble(maxMoney))
            mButSubmit!!.isEnabled = true
            return
        } else if (editMoney == 0.0) {
            showToastMsg("输入金额不能为 0")
            mButSubmit!!.isEnabled = true
            return
        }


        val dw = mConfigMap!!["limitunit"]!!.toString() + ""//借款期限单位（1：年 2：月 3：日）
        when (dw) {
            "1"//借款期限单位（1：年 2：月 3：日）
                , "2" -> if (mQixianMap == null || mQixianMap!!.isEmpty()) {
                UtilTools.isEmpty(mQixianValueTv!!, "期限")
                showToastMsg("期限不能为空")
                mButSubmit!!.isEnabled = true
                return
            }
            "3" -> if (UtilTools.isEmpty(mQixianEidt!!, "期限")) {
                showToastMsg("期限不能为空")
                mButSubmit!!.isEnabled = true
                return
            }
        }
        if (UtilTools.isEmpty(mJiekuanYongtuEdit!!, "用途")) {
            showToastMsg("用途不能为空")
            mButSubmit!!.isEnabled = true
            return
        }



        mRequestTag = MethodUrl.jiekuanHetong
        mParamMap = HashMap()
        mParamMap!!["patncode"] = mHezuoMap!!["patncode"]!!.toString() + ""
        mParamMap!!["zifangbho"] = mHezuoMap!!["zifangbho"]!!.toString() + ""
        mParamMap!!["reqmoney"] = mMoneyEdit!!.text.toString() + ""//借款金额（单位：元

        val danwei = mConfigMap!!["limitunit"]!!.toString() + ""
        var qixian = ""
        when (danwei) {
            "1"//借款期限单位（1：年 2：月 3：日）
            -> qixian = mQixianMap!!["code"]!!.toString() + ""
            "2" -> qixian = mQixianMap!!["code"]!!.toString() + ""
            "3" -> qixian = mQixianEidt!!.text.toString() + ""
        }

        val qixianStr = mConfigMap!!["loanlimit"]!!.toString() + ""

        mParamMap!!["loanlimit"] = qixian//借款期限
        mParamMap!!["limitunit"] = danwei//借款期限单位

        mParamMap!!["loanuse"] = mJiekuanYongtuEdit!!.text.toString() + ""//借款用途
        mParamMap!!["stitems"] = mPeopleList!!//受托支付信息
        //mParamMap.put("loansqid", mConfigMap.get("loansqid")+"");// 借款编号

        mParamMap!!["loancode"] = mHezuoMap!!["creditcd"]!!.toString() + ""// 借款品种

        mParamMap!!["paycustid"] = mPaycustid

        val fileConfigList = mConfigMap!!["contList"] as List<MutableMap1<String, Any>>?
        for (map1 in fileConfigList!!) {
            val sign = map1["isreq"]!!.toString() + ""//是否必传(0:否1是)
            val filetype = map1["filetype"]!!.toString() + ""

            if (sign == "1") {
                if (mFileList == null || mFileList.size <= 0) {
                    mButSubmit!!.isEnabled = true
                    showToastMsg("请上传必传的附件")
                    return
                } else {
                    for (map2 in mFileList) {
                        val code = map2["filetype"]!!.toString() + ""
                        val files = map2["files"] as List<MutableMap1<String, Any>>?
                        if (code == filetype) {
                            if (files == null || files.size <= 0) {
                                mButSubmit!!.isEnabled = true
                                showToastMsg("请上传必传的附件")
                                return
                            }

                        }
                    }
                }
            } else {

            }
        }

        //map.put("contList", mFileList);//附件列表

        LogUtil.i("show", "提交借款申请的参数" + mParamMap!!)
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.jiekuanHetong, mParamMap!!)
    }

    private fun submitDataAction() {


        /*if (UtilTools.isEmpty(mMoneyEdit,"金额")){
            showToastMsg("金额不能为空");
            return;
        }
        String dw = mConfigMap.get("limitunit")+"";
        switch (dw){
            case "1"://借款期限单位（1：年 2：月 3：日）
            case "2":
                if (mQixianMap == null || mQixianMap.isEmpty()){
                    UtilTools.isEmpty(mQixianValueTv,"期限");
                    return;
                }
                break;
            case "3":
                if (UtilTools.isEmpty(mQixianEidt,"期限")){
                    showToastMsg("期限不能为空");
                    return;
                }
                break;
        }
        if (UtilTools.isEmpty(mMoneyEdit,"金额")){
            showToastMsg("金额不能为空");
            return;
        }*/



        mRequestTag = MethodUrl.jiekuanSubmit
        /* Map<String, Object> map = new HashMap<>();
        map.put("patncode", mHezuoMap.get("patncode")+"");
        map.put("zifangbho", mHezuoMap.get("zifangbho")+"");
        map.put("reqmoney", mMoneyEdit.getText()+"");//借款金额（单位：元

        String danwei = mConfigMap.get("limitunit")+"";
        String qixian = "";
        List<Map<String, Object>> list;
        switch (danwei){
            case "1"://借款期限单位（1：年 2：月 3：日）
                qixian = mQixianMap.get("code")+"";
                break;
            case "2":
                qixian = mQixianMap.get("code")+"";
                break;
            case "3":
                qixian = mQixianEidt.getText()+"";
                break;
        }
        map.put("loanlimit", qixian);//借款期限
        map.put("limitunit", danwei);//借款期限单位

        map.put("loanuse", mJiekuanYongtuEdit.getText()+"");//借款用途
        map.put("stitems", mPeopleList);//受托支付信息
        map.put("loansqid", mConfigMap.get("loansqid")+"");// 借款编号


        List<Map<String, Object>> fileConfigList = (List<Map<String, Object>>) mConfigMap.get("conts");
        for (Map map1 : fileConfigList) {
            String sign = map1.get("isreq") + "";//是否必传(0:否1是)
            String filetype = map1.get("filetype") + "";

            if (sign.equals("1")) {
                if (mFileList == null || mFileList.size() <= 0) {
                    showToastMsg("请上传附件");
                    return;
                } else {
                    for (Map map2 : mFileList) {
                        String code = map2.get("filetype") + "";
                        List<Map<String, Object>> files = (List<Map<String, Object>>) map2.get("files");
                        if (code.equals(filetype)) {
                            if (files == null || files.size() <= 0) {
                                showToastMsg("请上传附件");
                                return;
                            }

                        }
                    }
                }
            }
        }

        map.put("contList", mFileList);//附件列表*/

        LogUtil.i("show", "提交借款申请的参数" + mParamMap!!)
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.jiekuanSubmit, mParamMap!!)
    }

    private fun initHetongList() {
        if (mHetongList == null) {
            mHetongList = ArrayList()
        }
        mJkHetongAdapter = JkHetongAdapter(this, mHetongList!!)
        val manager = LinearLayoutManager(this)
        manager.orientation = RecyclerView.VERTICAL
        mHetongRceycleView!!.layoutManager = manager
        mHetongRceycleView!!.adapter = mJkHetongAdapter
    }

    private fun initFujianView() {
        val mList = mConfigMap!!["contList"] as List<MutableMap1<String, Any>>?
        if (mList!!.size == 0) {
            mFujianContentLay!!.visibility = View.GONE
        } else {
            mFujianContentLay!!.visibility = View.VISIBLE
        }
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap1<String, Any>, mType: String) {

        val intent: Intent
        when (mType) {
            MethodUrl.jiekuanHetong -> {
                mHetongList = (tData["conts"] as List<MutableMap1<String, Any>>?)!!

                mParamMap!!["loansqid"] = tData["loansqid"]!!.toString() + ""// 借款编号

                intent = Intent(this@BorrowMoneyActivity, BorrowMoneyActivity::class.java)
                intent.putExtra("HETONG", mHetongList as Serializable?)
                intent.putExtra("CONFIG", mConfigMap as Serializable?)
                mParamMap!!["contList"] = mFileList
                intent.putExtra("PARAM", mParamMap as Serializable?)
                intent.putExtra("TYPE", "1")
                startActivity(intent)
                mButSubmit!!.isEnabled = true
            }
            MethodUrl.jiekuanSubmit -> {
                showToastMsg("借款申请成功")
                backTo(MainActivity::class.java, false)
                intent = Intent(this, ResultMoneyActivity::class.java)
                intent.putExtra(MbsConstans.ResultType.RESULT_KEY, MbsConstans.ResultType.RESULT_JIEKUAN)
                intent.putExtra("DATA", mParamMap as Serializable?)
                startActivity(intent)
                finish()
            }
            MethodUrl.jiekuanConfig -> {
                mConfigMap = tData
                val danwei = mConfigMap!!["limitunit"]!!.toString() + "" //借款期限单位（1：年 2：月 3：日）
                val qixianStr = mConfigMap!!["loanlimit"]!!.toString() + ""

                mQianxianMax = Integer.valueOf(mConfigMap!!["loanlimit"]!!.toString() + "")

                val maxMStr = mConfigMap!!["maxamt"]!!.toString() + ""
                var maxMoney = java.lang.Double.valueOf(maxMStr)
                maxMoney = UtilTools.divide(maxMoney, 100.0)

                UtilTools.setMoneyEdit(mMoneyEdit!!, maxMoney)

                mMoneyTipsTv!!.text = "最多可借款:" + UtilTools.getRMBMoney(maxMStr)
                mLilvValueTv!!.text = resources.getString(R.string.borrow_lilu) + UtilTools.getlilv(mConfigMap!!["daiklilv"]!!.toString() + "")

                //Map<String,Object> qixianMap = SelectDataUtil.getMap(danwei,SelectDataUtil.getQixianDw());
                //mQixianValueTv.setText(qixianStr+""+qixianMap.get("name")+"");

                initFujianView()

                val list: List<MutableMap1<String, Any>>
                when (danwei) {
                    "1"//借款期限单位（1：年 2：月 3：日）
                    -> {
                        list = SelectDataUtil.getMaxQixian(mQianxianMax, danwei)
                        mQixianDialog = MySelectDialog(this, true, list, "选择期限", 11)
                        mQixianDialog!!.selectBackListener = this
                        mQixianEidt!!.visibility = View.GONE
                        mQixianLay!!.isEnabled = true
                        mQixianArrowView!!.visibility = View.VISIBLE
                        mDanweiTv!!.visibility = View.GONE
                    }
                    "2" -> {
                        list = SelectDataUtil.getMaxQixian(mQianxianMax, danwei)
                        mQixianDialog = MySelectDialog(this, true, list, "选择期限", 10)
                        mQixianDialog!!.selectBackListener = this
                        mQixianEidt!!.visibility = View.GONE
                        mQixianLay!!.isEnabled = true
                        mQixianArrowView!!.visibility = View.VISIBLE
                        mDanweiTv!!.visibility = View.GONE
                    }
                    "3" -> {
                        mQixianEidt!!.visibility = View.VISIBLE
                        mQixianValueTv!!.visibility = View.GONE
                        mQixianLay!!.isEnabled = false
                        mQixianArrowView!!.visibility = View.GONE
                        mDanweiTv!!.visibility = View.VISIBLE
                    }
                }

                val yongtu = mConfigMap!!["usage"]!!.toString() + ""
                if (UtilTools.empty(yongtu)) {
                    mJiekuanYongtuEdit!!.setText("")
                    mJiekuanYongtuEdit!!.isEnabled = true
                } else {
                    mJiekuanYongtuEdit!!.setText(yongtu)
                    mJiekuanYongtuEdit!!.isEnabled = false
                }


                if (mHezuoMap != null) {
                    val type = mHezuoMap!!["creditcd"]!!.toString() + ""
                    when (type) {
                        "L03"//应收账款池
                        -> {
                            val total = mConfigMap!!["totalmny"]!!.toString() + ""
                            if (total == "0") {
                                showMsgDialog("当前可借额度为零，请关联相关合同，申请数据入池", true)
                                mAddTv!!.visibility = View.VISIBLE
                            }
                        }
                        "L11"//信用融资
                        -> {
                        }
                        else//未知
                        -> {
                        }
                    }
                }
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.jiekuanConfig -> getConfigAction()
                    MethodUrl.jiekuanHetong -> createHetong()
                    MethodUrl.jiekuanSubmit -> submitDataAction()
                }
            }
        }
    }


    override fun loadDataError(map: MutableMap1<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.jiekuanHetong -> mButSubmit!!.isEnabled = true
            MethodUrl.jiekuanSubmit -> mButAgree!!.isEnabled = true
            MethodUrl.jiekuanConfig -> {

                var errorCode = -1
                val errcodeStr = map["errcode"]!!.toString() + ""
                try {
                    errorCode = java.lang.Double.valueOf(errcodeStr).toInt()
                } catch (e: Exception) {
                    e.printStackTrace()
                    LogUtil.i("show", "这里出现异常了" + e.message)
                }

                if (errorCode != ErrorHandler.REFRESH_TOKEN_DATE_CODE
                        && errorCode != ErrorHandler.ACCESS_TOKEN_DATE_CODE
                        && errorCode != ErrorHandler.PHONE_NO_ACTIVE) {
                    finish()
                }
            }
        }

        dealFailInfo(map, mType)
    }


    override fun onSelectBackListener(map: MutableMap1<String, Any>, type: Int) {
        val value = map["name"]!!.toString() + ""
        when (type) {
            11 -> {
                mQixianMap = map
                mQixianValueTv!!.text = mQixianMap!!["name"]!!.toString() + ""
                mQixianValueTv!!.setError(null, null)
                mQixianEidt!!.setError(null, null)
            }
            10 -> {
                mQixianMap = map
                val d = lixi
                val ss = UtilTools.fromDouble(d)
                mQixianValueTv!!.text = mQixianMap!!["name"]!!.toString() + ""
                mLixiValutTv!!.text = ss
                mQixianValueTv!!.setError(null, null)
                mQixianEidt!!.setError(null, null)
            }
        }
    }

    @OnClick(R.id.qixian_lay, R.id.back_img, R.id.fujian_lay, R.id.add_shoukuan_people_lay, R.id.but_submit, R.id.but_agree, R.id.left_back_lay, R.id.tv_add_amount)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.but_agree -> {
                mButAgree!!.isEnabled = false
                // submitDataAction();
                PermissionsUtils.requsetRunPermission(this@BorrowMoneyActivity, object : RePermissionResultBack {
                    override fun requestSuccess() {
                        netWorkWarranty()
                    }

                    override fun requestFailer() {
                        toast(R.string.failure)
                        mButAgree!!.isEnabled = true
                    }
                }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)
            }
            R.id.qixian_lay -> mQixianDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.fujian_lay ->

                PermissionsUtils.requsetRunPermission(this@BorrowMoneyActivity, object : RePermissionResultBack {
                    override fun requestSuccess() {
                        val intent = Intent(this@BorrowMoneyActivity, AddFileActivity::class.java)
                        intent.putExtra("DATA", mConfigMap as Serializable?)
                        intent.putExtra("TYPE", "2")
                        startActivityForResult(intent, 300)
                    }

                    override fun requestFailer() {
                        toast(R.string.failure)
                    }
                }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)
            R.id.add_shoukuan_people_lay -> {
                intent = Intent(this@BorrowMoneyActivity, AddSKPeopleActivity::class.java)
                intent.putExtra("DATA", mHezuoMap as Serializable?)
                startActivityForResult(intent, 400)
            }
            R.id.but_submit -> {
                mButSubmit!!.isEnabled = false
                createHetong()
            }
            R.id.tv_add_amount //添加入池
            -> {
                intent = Intent(this@BorrowMoneyActivity, HeTongSelectActivity::class.java)
                intent.putExtra("payfirmname", mPayCompayName)
                intent.putExtra("paycustid", mPaycustid)
                intent.putExtra("DATA", mHezuoMap as Serializable?)
                startActivity(intent)
            }
        }/* intent = new Intent(BorrowMoneyActivity.this, AddFileActivity.class);
                intent.putExtra("DATA", (Serializable) mConfigMap);
                intent.putExtra("TYPE","2");
                startActivityForResult(intent, 300);*/
    }


    private fun responseData() {
        if (mPeopleList == null) {
            return
        }
        mSwipeMenuAdapter = ShouKuanRenAdapter(this)

        if (mType == "1") {
            mSwipeMenuAdapter!!.isSwipeEnable = false
        } else {
            mSwipeMenuAdapter!!.isSwipeEnable = true
        }

        mSwipeMenuAdapter!!.setDataList(mPeopleList!!)

        mSwipeMenuAdapter!!.setOnDelListener(object : ShouKuanRenAdapter.onSwipeListener {
            override fun onDel(pos: Int) {
                //Toast.makeText(BorrowMoneyActivity.this, "删除:" + pos, Toast.LENGTH_SHORT).show();

                /*//RecyclerView关于notifyItemRemoved的那点小事 参考：http://blog.csdn.net/jdsjlzx/article/details/52131528
                mSwipeMenuAdapter.getDataList().remove(pos);
                mSwipeMenuAdapter.notifyItemRemoved(pos);//推荐用这个
                if(pos != (mSwipeMenuAdapter.getDataList().size())){ // 如果移除的是最后一个，忽略 注意：这里的mDataAdapter.getDataList()不需要-1，因为上面已经-1了
                    mSwipeMenuAdapter.notifyItemRangeChanged(pos, mSwipeMenuAdapter.getDataList().size() - pos);
                }
                mSwipeMenuAdapter.notifyDataSetChanged();*/

                mPeopleList!!.removeAt(pos)
                responseData()

                //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
            }

            override fun onTop(pos: Int) {//置顶功能有bug，后续解决

            }
        })

        val adapter = ScaleInAnimationAdapter(mSwipeMenuAdapter)
        adapter.setFirstOnly(false)
        adapter.setDuration(500)
        adapter.setInterpolator(OvershootInterpolator(.5f))


        mLRecyclerViewAdapter = LRecyclerViewAdapter(adapter)
        //SampleHeader headerView = new SampleHeader(getActivity(), R.layout.fragment_home_head_view);
        //mLRecyclerViewAdapter.addHeaderView(headerView);
        mShoukuanList!!.adapter = mLRecyclerViewAdapter
        mShoukuanList!!.itemAnimator = DefaultItemAnimator()
        mShoukuanList!!.setHasFixedSize(true)
        mShoukuanList!!.isNestedScrollingEnabled = false

        mShoukuanList!!.setPullRefreshEnabled(false)
        mShoukuanList!!.setLoadMoreEnabled(false)

        val divider2 = DividerDecoration.Builder(this)
                .setHeight(2f)
                .setColorResource(R.color.divide_line)
                .build()
        mShoukuanList!!.addItemDecoration(divider2)
        mShoukuanList!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
        mShoukuanList!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

        mShoukuanList!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
    }

    override fun finish() {
        super.finish()
        if (mFileNum > 0) {
            FileUtils.deleteDir(MbsConstans.UPLOAD_PATH)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SETTING -> {
                Toast.makeText(this@BorrowMoneyActivity, R.string.message_setting_comeback, Toast.LENGTH_SHORT).show()
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                // 图片、视频、音频选择结果回调
                PictureConfig.CHOOSE_REQUEST ->{
                    val selectList = PictureSelector.obtainMultipleResult(data)
                }
                400 -> {
                    val bundle = data!!.extras
                    if (bundle != null) {
                        val map = bundle.getSerializable("DATA") as MutableMap1<String, Any>
                        mPeopleList!!.add(map)
                        responseData()
                    }
                }
                300 -> {
                    val bundle2 = data!!.extras
                    if (bundle2 != null) {
                        val list = bundle2.getSerializable("resultList") as List<MutableMap1<String, Any>>
                    }
                }
            }// 例如 LocalMedia 里面返回三种path
            // 1.media.getPath(); 为原图path
            // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
            // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
            // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
            //adapter.setList(selectList);
            // adapter.notifyDataSetChanged();
        }

        var intent: Intent
        val bundle: Bundle?
        if (requestCode == 1) {//人脸识别过来的状态码
            when (resultCode) {
                //通过短信验证码
                MbsConstans.FaceType.FACE_BORROW_MONEY -> {
                    bundle = data!!.extras
                    if (bundle == null) {
                        isCheck = false
                        mButAgree!!.isEnabled = true
                    } else {
                        mButAgree!!.isEnabled = false
                        isCheck = true
                        submitDataAction()
                    }
                }
                else -> mButAgree!!.isEnabled = true
            }

        } else if (requestCode == PAGE_INTO_LIVENESS) {//人脸识别返回来的数据
            if (resultCode == Activity.RESULT_OK) {
                bundle = data!!.extras
                bundle!!.putInt(MbsConstans.FaceType.FACE_KEY, MbsConstans.FaceType.FACE_BORROW_MONEY)
                intent = Intent(this@BorrowMoneyActivity, ResultActivity::class.java)
                intent.putExtras(bundle)
                //设置返回数据
                startActivityForResult(intent, 1)
            } else {
                mButAgree!!.isEnabled = true
            }
        }
    }

    private fun enterNextPage() {
        //startActivityForResult(Intent(this, LivenessActivity::class.java), PAGE_INTO_LIVENESS)
    }

    /**
     * 联网授权
     */
    private fun netWorkWarranty() {

       /* val uuid = ConUtil.getUUIDString(this)

        Thread(Runnable {
            val manager = Manager(this@BorrowMoneyActivity)
            val licenseManager = LivenessLicenseManager(this@BorrowMoneyActivity)
            manager.registerLicenseManager(licenseManager)
            manager.takeLicenseFromNetwork(uuid)
            if (licenseManager.checkCachedLicense() > 0) {
                //授权成功
                mHandler.sendEmptyMessage(1)
            } else {
                //授权失败
                mHandler.sendEmptyMessage(2)
            }
        }).start()*/
    }

    protected fun toast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showMsgDialog(msg: Any, isClose: Boolean) {
        mTotalMoneyDialog = TipMsgDialog(this, true)
        mTotalMoneyDialog!!.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
                dialog.dismiss()
                if (isClose) {
                    finish()
                }
                true
            } else {
                false
            }
        }
        val onClickListener = View.OnClickListener { v ->
            when (v.id) {
                R.id.cancel -> mTotalMoneyDialog!!.dismiss()
                R.id.confirm -> {
                    val intent = Intent(this@BorrowMoneyActivity, HeTongSelectActivity::class.java)
                    intent.putExtra("payfirmname", mPayCompayName)
                    intent.putExtra("paycustid", mPaycustid)
                    intent.putExtra("DATA", mHezuoMap as Serializable?)
                    startActivity(intent)
                    mTotalMoneyDialog!!.dismiss()
                }
                R.id.tv_right -> mTotalMoneyDialog!!.dismiss()
            }
        }
        mTotalMoneyDialog.setCanceledOnTouchOutside(true)
        mTotalMoneyDialog.setCancelable(true)
        mTotalMoneyDialog.onClickListener = onClickListener
        mTotalMoneyDialog.initValue("温馨提示", msg)
        mTotalMoneyDialog.show()
        mTotalMoneyDialog.tv_cancel!!.text = "取消"
        mTotalMoneyDialog.tv_exit!!.visibility = View.VISIBLE
        mTotalMoneyDialog.tv_exit!!.text = "前往"
    }

    companion object {
        private val PAGE_INTO_LIVENESS = 101

        private val REQUEST_CODE_SETTING = 10011
    }
}
