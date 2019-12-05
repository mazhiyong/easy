package com.lairui.easy.ui.temporary.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView

import com.flyco.dialog.utils.CornerUtils
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.CompanySelectDialog
import com.lairui.easy.mywidget.dialog.DateSelectDialog
import com.lairui.easy.mywidget.dialog.MySelectDialog
import com.lairui.easy.mywidget.dialog.PopuTipView
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.utils.tool.FileUtils
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig

import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick

/**
 * 添加完善贸易信息合同
 */
class HeTongAddActivity : BasicActivity(), RequestView, SelectBackListener {

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

    @BindView(R.id.has_upload_tv)
    lateinit var mHasUploadTv: TextView
    @BindView(R.id.add_file_tv)
    lateinit var mAddFileTv: TextView
    @BindView(R.id.file_num_tv)
    lateinit var mFileNumTv: TextView
    @BindView(R.id.hetong_code_edit)
    lateinit var mHetongCodeEdit: EditText//合同编号
    @BindView(R.id.gmf_value_tv)
    lateinit var mGmfValueTv: TextView//购买方
    @BindView(R.id.gmf_lay)
    lateinit var mGmfLay: CardView//购买方
    @BindView(R.id.qd_date_value_tv)
    lateinit var mQdDateValueTv: TextView//签订日期
    @BindView(R.id.qd_date)
    lateinit var mQdDate: CardView//签订日期
    @BindView(R.id.money_edit)
    lateinit var mMoneyEdit: EditText//金额
    @BindView(R.id.js_type_value_tv)
    lateinit var mJsTypeValueTv: TextView//结算方式
    @BindView(R.id.js_type_lay)
    lateinit var mJsTypeLay: CardView//结算方式
    @BindView(R.id.js_zhouqi_edit)
    lateinit var mJsZhouqiEdit: EditText//结算周期
    @BindView(R.id.wu_checkbox)
    lateinit var mWuCheckbox: CheckBox//无
    @BindView(R.id.wu_lay)
    lateinit var mWuLay: LinearLayout//无
    @BindView(R.id.myue_checkbox)
    lateinit var mMyueCheckbox: CheckBox//每月
    @BindView(R.id.myue_lay)
    lateinit var mMyueLay: LinearLayout//每月
    @BindView(R.id.myue_edit)
    lateinit var mMyueEdit: EditText//每月
    @BindView(R.id.zy_edit)
    lateinit var mZyEdit: EditText//摘要
    @BindView(R.id.count_tv)
    lateinit var mCountTv: TextView//字数限制 摘要
    @BindView(R.id.cjr_value_tv)
    lateinit var mCjrValueTv: TextView//出借人
    @BindView(R.id.cjr_lay)
    lateinit var mCjrLay: CardView//出借人
    @BindView(R.id.but_submit)
    lateinit var mButSubmit: Button//下一步
    @BindView(R.id.tip_wu_view)
    lateinit var mTipWuView: ImageView
    @BindView(R.id.tip_ri_view)
    lateinit var mTipRiView: ImageView
    @BindView(R.id.danwei_lay)
    lateinit var mDanweiLay: LinearLayout
    @BindView(R.id.danwei_value_tv)
    lateinit var mDanweiValueTv: TextView
    @BindView(R.id.jiesuan_date_line)
    lateinit var mJiesuanDateLine: View
    @BindView(R.id.jiesuan_date_value)
    lateinit var mJiesuanDateValue: TextView
    @BindView(R.id.jiesuan_date_lay)
    lateinit var mJiesuanDateLay: CardView
    @BindView(R.id.zhouqiJs_lay)
    lateinit var mZhouqiJsLay: LinearLayout


    // private Map<String, Object> mMoneyMap;
    private lateinit var mHezuoMap: MutableMap<String, Any>
    private var mConfigMap: MutableMap<String, Any> = HashMap()
    private lateinit var mQixianMap: MutableMap<String, Any>


    private lateinit var mParamMap: MutableMap<String, Any>
    private val mType = ""

    private val mPeopleList = ArrayList<MutableMap<String, Any>>()


    private var mRequestTag = ""

    private lateinit var mQdDateDialog: DateSelectDialog
    private lateinit var mJsTypeDialog: MySelectDialog
    private lateinit var mJsZhouqiDwDialog: MySelectDialog

    private lateinit var mJsDateDialog: DateSelectDialog

    private lateinit var mCompanySelectDialog: CompanySelectDialog

    private lateinit var mJsTypeMap: MutableMap<String, Any>
    private lateinit var mJsZhouqiDwMap: MutableMap<String, Any>
    private lateinit var mPayCompanyMap: MutableMap<String, Any>

    private var mQdDateStr = "" //签订日期
    private var mZhouqiStr = "" //定期结算日


    private var mPayCompayName: String? = ""
    private var mPaycustid: String? = ""

    private var mPayLayPress = false

    override val contentView: Int
        get() = R.layout.activity_hetong_add


    private val mFileList = ArrayList<MutableMap<String, Any>>()
    private var mFileNum = 0
    //广播监听
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val b = intent.extras
            if (MbsConstans.BroadcastReceiverAction.FILE_TIP_ACTION == action) {

                if (b != null) {
                    val list = b.getSerializable("DATA") as List<MutableMap<String, Any>>
                    var num = 0
                    if (list != null) {
                        mConfigMap["contList"] = list
                        mFileList.clear()
                        for (map in list) {
                            val resultMap = HashMap<String, Any>()
                            resultMap["filetype"] = map["filetype"]!!.toString() + ""
                            resultMap["name"] = map["name"]!!.toString() + ""
                            var files = map["resultData"] as List<MutableMap<String, Any>>?
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
                    LogUtil.i("打印log日志", "上传文件列表$mFileList")
                }
            } else if (action == MbsConstans.BroadcastReceiverAction.JIE_HUAN_UPDATE) {
                finish()
            }
        }
    }


    private lateinit var popView: View
    private lateinit var mPopupWindow: PopupWindow

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.hetong_title)
        val filter = IntentFilter()
        filter.addAction(MbsConstans.BroadcastReceiverAction.FILE_TIP_ACTION)
        registerReceiver(receiver, filter)
        initDialog()
        mAddFileTv!!.visibility = View.VISIBLE
        mHasUploadTv!!.visibility = View.GONE
        mFileNumTv!!.visibility = View.GONE

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mPayCompayName = bundle.getString("payfirmname")
            mPaycustid = bundle.getString("paycustid")
        }

        mWuCheckbox!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mMyueCheckbox!!.isChecked = false
                mMyueEdit!!.isEnabled = false
            } else {
                if (!mMyueCheckbox!!.isChecked) {
                    mWuCheckbox!!.isChecked = true
                    mMyueEdit!!.isEnabled = false
                }
            }
        }

        mMyueCheckbox!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mWuCheckbox!!.isChecked = false
                mMyueEdit!!.isEnabled = true
            } else {
                if (!mWuCheckbox!!.isChecked) {
                    mMyueCheckbox!!.isChecked = true
                    mMyueEdit!!.isEnabled = true
                } else {
                    mMyueEdit!!.isEnabled = false
                }
            }
        }

        UtilTools.setMoneyEdit(mMoneyEdit!!, 0.0)

        mJiesuanDateLine!!.visibility = View.GONE
        mJiesuanDateLay!!.visibility = View.GONE

        getCompanyList()

        initContList()
    }


    private fun initDialog() {
        //签订日期
        mQdDateDialog = DateSelectDialog(this, true, "选择日期", 2001)
        mQdDateDialog!!.selectBackListener = this

        //结算日期选择   定期方式
        mJsDateDialog = DateSelectDialog(this, true, "选择日期", 2002)
        mJsDateDialog!!.selectBackListener = this

        val list = ArrayList<MutableMap<String, Any>>()//结算方式 0周期结算 1定期结算
        var map: MutableMap<String, Any> = HashMap()
        map["name"] = "周期结算"
        map["code"] = "0"
        list.add(map)
        map = HashMap()
        map["name"] = "定期结算"
        map["code"] = "1"
        list.add(map)
        mJsTypeDialog = MySelectDialog(this, true, list, "结算方式", 11)
        mJsTypeDialog!!.selectBackListener = this

        val jsZhouqiDwList = ArrayList<MutableMap<String, Any>>()//期限单位(1: 年, 2: 月, 3: 日)
        var zhouqiMap: MutableMap<String, Any> = HashMap()
        zhouqiMap["name"] = "年"
        zhouqiMap["code"] = "1"
        jsZhouqiDwList.add(zhouqiMap)
        zhouqiMap = HashMap()
        zhouqiMap["name"] = "月"
        zhouqiMap["code"] = "2"
        jsZhouqiDwList.add(zhouqiMap)
        zhouqiMap = HashMap()
        zhouqiMap["name"] = "日"
        zhouqiMap["code"] = "3"
        jsZhouqiDwList.add(zhouqiMap)
        mJsZhouqiDwDialog = MySelectDialog(this, true, jsZhouqiDwList, "结算周期单位", 12)
        mJsZhouqiDwDialog!!.selectBackListener = this
    }


    /**
     * 查询分子公司信息
     */
    private fun getCompanyList() {
        mRequestTag = MethodUrl.childfirm
        val map = HashMap<String, String>()
        map["firmname"] = mPayCompayName!!
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.childfirm, map)
    }


    private fun addHetong() {

        mRequestTag = MethodUrl.addHetongInfo

        val htCode = mHetongCodeEdit!!.text.toString() + ""//合同编号
        if (UtilTools.isEmpty(mHetongCodeEdit!!, getString(R.string.hetong_code))) {
            mButSubmit!!.isEnabled = true
            return
        }

        val gmf = ""//购买方
        if (UtilTools.isEmpty(mGmfValueTv!!, getString(R.string.pay_people))) {
            mButSubmit!!.isEnabled = true
            return
        }

        //签订日期
        if (UtilTools.isEmpty(mQdDateValueTv!!, getString(R.string.qian_date))) {
            mButSubmit!!.isEnabled = true
            return
        }

        val money = mMoneyEdit!!.text.toString() + ""//金额
        if (UtilTools.isEmpty(mMoneyEdit!!, getString(R.string.jine))) {
            mButSubmit!!.isEnabled = true
            return
        }


        if (UtilTools.isEmpty(mJsTypeValueTv!!, getString(R.string.js_type))) {
            mButSubmit!!.isEnabled = true
            return
        }


        /* String zhaiyao = mZyEdit.getText() + "";//摘要
        if (UtilTools.isEmpty(mZyEdit, getString(R.string.trade_des))) {
            mButSubmit.setEnabled(true);
            return;
        }*/

        /* String cjr = "";//出借人
        if (UtilTools.isEmpty(mCjrValueTv, getString(R.string.borrow_pay_man))) {
            mButSubmit.setEnabled(true);
            return;
        }*/

        /*  String isJsDate = "0";
        if (mWuCheckbox.isChecked()) {
            isJsDate = "0";
        } else {
            isJsDate = "1";
        }
*/

        var settdateflg = ""
        var jsType = ""//结算方式
        if (mJsTypeValueTv!!.text.toString().trim { it <= ' ' } == "周期结算") {
            jsType = "0"  //周期结算

            //结算周期
            if (UtilTools.isEmpty(mJsZhouqiEdit!!, getString(R.string.js_zhouqi))) {
                mButSubmit!!.isEnabled = true
                return
            }

            if (!mWuCheckbox!!.isChecked && !mMyueCheckbox!!.isChecked) {
                showToastMsg("请选择有无结算日")
                mButSubmit!!.isEnabled = true
                return
            }

            //无结算日
            if (mWuCheckbox!!.isChecked) {
                settdateflg = "0"
            }

            //有结算日
            if (mMyueCheckbox!!.isChecked) {

                settdateflg = "1"
                if (UtilTools.isEmpty(mMyueEdit!!, getString(R.string.js_date))) {
                    mButSubmit!!.isEnabled = true
                    return
                }
            }

        } else {
            jsType = "1" //定期结算

            if (UtilTools.isEmpty(mJiesuanDateValue!!, getString(R.string.jiesuan_date))) {
                mButSubmit!!.isEnabled = true
                return
            }

        }


        val jsZhouqi = mJsZhouqiEdit!!.text.toString() + ""//结算周期
        var jsZhouqiDanWei = ""//结算周期单位
        if (mDanweiValueTv!!.text.toString().trim { it <= ' ' } == "年") {
            jsZhouqiDanWei = "1"
        } else if (mDanweiValueTv!!.text.toString().trim { it <= ' ' } == "月") {
            jsZhouqiDanWei = "2"
        } else {
            jsZhouqiDanWei = "3"
        }

        val jsDate = mMyueEdit!!.text.toString() + ""//结算日

        val beizhu = mZyEdit!!.text.toString() + ""
        mParamMap = HashMap()
        mParamMap!!["contno"] = htCode//合同编号
        mParamMap!!["contmny"] = money//金额
        mParamMap!!["contsgndt"] = mQdDateStr//合同签订日期
        mParamMap!!["settype"] = jsType//结算方式
        mParamMap!!["settcycle"] = jsZhouqi//结算周期
        mParamMap!!["settunit"] = jsZhouqiDanWei//期限单位(1: 年, 2: 月, 3: 日)
        mParamMap!!["settdateflg"] = settdateflg//是否有结算日(0：无 1：有)
        mParamMap!!["settdate"] = jsDate//结算日
        mParamMap!!["dqsettdate"] = mZhouqiStr//定期结算日期
        mParamMap!!["memo"] = beizhu//备注
        mParamMap!!["paycustid"] = mPaycustid!!//付款方客户号
        mParamMap!!["payfirmname"] = mPayCompayName!!//付款方名称

        mParamMap!!["contList"] = ""//附件列表

        val fileConfigList = mConfigMap["contList"] as List<MutableMap<String, Any>>?
        if (fileConfigList != null) {
            for (map1 in fileConfigList) {
                val sign = map1["isreq"]!!.toString() + ""//是否必传(0:否1是)
                val filetype = map1["filetype"]!!.toString() + ""

                if (sign == "1") {
                    if (mFileList == null || mFileList.size <= 0) {
                        showToastMsg("请上传必传的附件")
                        return
                    } else {
                        for (map2 in mFileList) {
                            val code = map2["filetype"]!!.toString() + ""
                            val files = map2["files"] as List<MutableMap<String, Any>>?
                            if (code == filetype) {
                                if (files == null || files.size <= 0) {
                                    showToastMsg("请上传必传的附件")
                                    return
                                }

                            }
                        }
                    }
                } else {

                }
            }
        }


        mParamMap!!["contList"] = mFileList
        LogUtil.i("打印log日志", "提交借款申请的参数" + mParamMap!!)
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.addHetongInfo, mParamMap!!)

    }

    private fun submitDataAction() {
        LogUtil.i("打印log日志", "提交借款申请的参数" + mParamMap!!)
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.jiekuanSubmit, mParamMap!!)
    }

    private fun initContList() {

        mConfigMap = HashMap()

        val mContList = ArrayList<MutableMap<String, Any>>()

        var map: MutableMap<String, Any> = HashMap()
        map["filetype"] = "01"
        map["name"] = "贸易合同"
        mContList.add(map)

        map = HashMap()
        map["filetype"] = "02"
        map["name"] = "发票"
        mContList.add(map)

        map = HashMap()
        map["filetype"] = "03"
        map["name"] = "物流单据"
        mContList.add(map)

        map = HashMap()
        map["filetype"] = "04"
        map["name"] = "收货单"
        mContList.add(map)

        map = HashMap()
        map["filetype"] = "05"
        map["name"] = "校验单"
        mContList.add(map)

        mConfigMap["contList"] = mContList
    }


    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

        val intent: Intent
        when (mType) {
            MethodUrl.childfirm -> {

                val list = tData["firmlist"] as List<MutableMap<String, Any>>?
                if (list != null && list.size > 0) {
                    mCompanySelectDialog = CompanySelectDialog(this@HeTongAddActivity, true, list, 10)
                    mCompanySelectDialog!!.selectBackListener = this
                    if (mPayLayPress) {
                        mCompanySelectDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
                    }
                } else {
                    if (mPayLayPress) {
                        showToastMsg("暂无付款方")
                    }
                }
            }
            MethodUrl.jiekuanSubmit -> {
                showToastMsg("借款申请成功")
                intent = Intent(this, ResultMoneyActivity::class.java)
                intent.putExtra(MbsConstans.ResultType.RESULT_KEY, MbsConstans.ResultType.RESULT_JIEKUAN)
                intent.putExtra("DATA", mParamMap as Serializable?)
                startActivity(intent)
                finish()
            }
            MethodUrl.jiekuanConfig -> {
            }

            MethodUrl.addHetongInfo -> {
                showToastMsg("添加合同成功")
                intent = Intent()
                intent.action = MbsConstans.BroadcastReceiverAction.HTONGUPDATE
                sendBroadcast(intent)
                finish()
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.childfirm -> getCompanyList()
                    MethodUrl.jiekuanHetong -> {
                    }
                    MethodUrl.jiekuanSubmit -> submitDataAction()
                    MethodUrl.addHetongInfo -> addHetong()
                }//addHetong();
            }
        }
    }


    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.jiekuanHetong -> {
            }
            MethodUrl.jiekuanSubmit -> {
            }
            MethodUrl.jiekuanConfig -> {
            }
        }

        dealFailInfo(map, mType)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST ->{
                    // 图片、视频、音频选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                }
                400 -> {
                    val bundle = data!!.extras
                    if (bundle != null) {
                        val map = bundle.getSerializable("DATA") as MutableMap<String, Any>
                        mPeopleList.add(map)
                    }
                }
                300 -> {
                    val bundle2 = data!!.extras
                    if (bundle2 != null) {
                        val list = bundle2.getSerializable("resultList") as List<MutableMap<String, Any>>
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


    }


    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        val value = map["name"]!!.toString() + ""
        when (type) {
            10 -> {
                mPayCompanyMap = map
                mGmfValueTv!!.text = mPayCompanyMap!!["firmname"]!!.toString() + ""
            }
            11 -> {
                mJsTypeMap = map
                mJsTypeValueTv!!.text = mJsTypeMap!!["name"]!!.toString() + ""
                val code = mJsTypeMap!!["code"]!!.toString() + ""
                if (code == "0") {
                    mZhouqiJsLay!!.visibility = View.VISIBLE
                    mJiesuanDateLay!!.visibility = View.GONE
                    mJiesuanDateLine!!.visibility = View.GONE
                } else if (code == "1") {
                    mZhouqiJsLay!!.visibility = View.GONE
                    mJiesuanDateLine!!.visibility = View.VISIBLE
                    mJiesuanDateLay!!.visibility = View.VISIBLE
                }
            }
            12 -> {
                mJsZhouqiDwMap = map
                mDanweiValueTv!!.text = mJsZhouqiDwMap!!["name"]!!.toString() + ""
            }
            2001 -> {
                mQdDateStr = map["date"]!!.toString() + ""
                mQdDateValueTv!!.text = map["year"].toString() + "-" + map["month"] + "-" + map["day"] + ""
                mQdDateValueTv!!.setError(null, null)
            }
            2002 -> {
                mZhouqiStr = map["date"]!!.toString() + ""
                mJiesuanDateValue!!.text = map["year"].toString() + "-" + map["month"] + "-" + map["day"] + ""
                mJiesuanDateValue!!.setError(null, null)
            }
        }
    }

    @OnClick(R.id.back_img, R.id.fujian_lay, R.id.left_back_lay, R.id.gmf_lay, R.id.tip_wu_view, R.id.tip_ri_view, R.id.qd_date, R.id.js_type_lay, R.id.wu_lay, R.id.myue_lay, R.id.cjr_lay, R.id.but_submit, R.id.danwei_lay, R.id.jiesuan_date_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.jiesuan_date_lay -> mJsDateDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.danwei_lay -> mJsZhouqiDwDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.fujian_lay ->

                PermissionsUtils.requsetRunPermission(this@HeTongAddActivity, object : RePermissionResultBack {
                    override fun requestSuccess() {
                        val intent = Intent(this@HeTongAddActivity, AddFileActivity::class.java)
                        intent.putExtra("DATA", mConfigMap as Serializable)
                        intent.putExtra("TYPE", "2")
                        startActivityForResult(intent, 300)
                    }

                    override fun requestFailer() {
                        showToastMsg(R.string.failure)
                    }
                }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)

            R.id.gmf_lay//选择购买方
            -> if (mCompanySelectDialog != null) {
                mCompanySelectDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            } else {
                mPayLayPress = true
                getCompanyList()
            }
            R.id.qd_date//选择签订日期
            -> mQdDateDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.js_type_lay//选择结算方式
            -> mJsTypeDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.wu_lay//无
            -> {
            }
            R.id.myue_lay//每月
            -> {
            }
            R.id.cjr_lay//选择出借人
            -> {
            }
            R.id.but_submit -> {
                Log.i("show", "0000000")
                addHetong()
            }
            R.id.tip_wu_view -> {


                val s2 = "付款截止日=当月的发票日期+" + "\n" +
                        "结算周期（按月计算，每30天" + "\n" +
                        "算一个月）后的当月最后一天"

                val mp2 = PopuTipView(this@HeTongAddActivity, s2, R.layout.popu_lay_top)
                mTipWuView?.let { mp2.show(it, 1) }
            }
            R.id.tip_ri_view -> {

                /*View inflate2 = View.inflate(HeTongAddActivity.this, R.layout.popup_bubble_text, null);
                TextView mTextView2 = inflate2.findViewById(R.id.tv_bubble);
                mTextView2.setText("1、若开票日期在当月结算日之前"+"\n"+
                        "（不含结算日），则付款截止日=当"+ "\n" +
                        "月结算日+结算周期（按月计算）"+ "\n" +
                        "2、若开票日期在当月结算日之后"+ "\n" +
                        "（含结算日），则付款截止日=下一"+ "\n" +
                        "月结算日+结算周期（按月计算）");*/
                val s = "1、若开票日期在当月结算日之前" + "\n" +
                        "（不含结算日），则付款截止日=当" + "\n" +
                        "月结算日+结算周期（按月计算）" + "\n" +
                        "2、若开票日期在当月结算日之后" + "\n" +
                        "（含结算日），则付款截止日=下一" + "\n" +
                        "月结算日+结算周期（按月计算）"

                val mp = PopuTipView(this@HeTongAddActivity, s, R.layout.popu_lay_top)
                mTipRiView?.let { mp.show(it, 2) }
            }
        }// finish();
        /* View inflate = View.inflate(HeTongAddActivity.this, R.layout.popup_bubble_text, null);
                TextView mTextView = inflate.findViewById(R.id.tv_bubble);
                mTextView.setText("付款截止日=当月的发票日期+" + "\n" +
                        "结算周期（按月计算，每30天" + "\n" +
                        "算一个月）后的当月最后一天" );



                new EasyDialog(HeTongAddActivity.this)
                        .setLayout(inflate)
                        .setBackgroundColor(HeTongAddActivity.this.getResources().getColor(R.color.black_light))
                        .setLocationByAttachedView(mTipWuView)
                        .setGravity(EasyDialog.GRAVITY_TOP)
                        .setTouchOutsideDismiss(true)
                        .setMatchParent(false)
                        .setMarginLeftAndRight(10, 10)
                        .show();*//* new BubblePopup(HeTongAddActivity.this, inflate)
                        .anchorView(mTipWuView)
                        .bubbleColor(Color.parseColor("#A6000000"))
                        .gravity(Gravity.TOP)
                        .triangleWidth(5)
                        .triangleHeight(5)
                        .showAnim(null)
                        .dismissAnim(null)
                        .show();*//*   new BubblePopup(HeTongAddActivity.this, inflate2)
                        .anchorView(mTipRiView)
                        .bubbleColor(Color.parseColor("#A6000000"))
                        .gravity(Gravity.TOP)
                        .triangleWidth(5)
                        .triangleHeight(5)
                        .showAnim(null)
                        .dismissAnim(null)
                        .show();*/
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

    private fun initPopupWindow(view: View) {//城市列表的显示
        popView = View.inflate(this, R.layout.popu_lay, null)
        popView!!.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)


        val mTextView2 = popView!!.findViewById<TextView>(R.id.tv_bubble)
        mTextView2.text = "从事本行业的开始时间"


        val mLlContent = popView!!.findViewById<View>(com.flyco.dialog.R.id.ll_content) as LinearLayout

        val mLayoutParams = mLlContent.layoutParams as RelativeLayout.LayoutParams
        mLlContent.setBackgroundDrawable(CornerUtils.cornerDrawable(Color.parseColor("#A6000000"), 15f))

        //mLayoutParams.setMargins(10, 0, 10, 0);
        mLlContent.layoutParams = mLayoutParams


        //让mOnCreateView充满父控件,防止ViewHelper.setXY导致点击事件无效


        /* popView = LayoutInflater.from(this).inflate(R.layout.popup_bubble_text,null);
        TextView mTextView2 = popView.findViewById(R.id.tv_bubble);
        mTextView2.setText("从事本行业的开始时间" );
        mTextView2.setTextColor(ContextCompat.getColor(this,R.color.black));*/


        mPopupWindow = PopupWindow(popView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        if (mPopupWindow != null && mPopupWindow!!.isShowing == false) {
            val screenWidth = UtilTools.getScreenWidth(this)
            val screenHeight = UtilTools.getScreenHeight(this)
            //mPopupWindow.setWidth(screenWidth/2);
            // mPopupWindow.setHeight((int)(screenHeight/1.5));
            //设置background后在外点击才会消失
            //mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_corners));
            //mPopupWindow.setOutsideTouchable(true);// 设置可允许在外点击消失
            //自定义动画
            // mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
            //mPopupWindow.setAnimationStyle(android.R.style.Animation_Activity);//使用系统动画
            mPopupWindow!!.update()
            mPopupWindow!!.isTouchable = true
            mPopupWindow!!.isFocusable = true
            //popView.requestFocus();//pop设置不setBackgroundDrawable情况，把焦点给popView，添加popView.setOnKeyListener。可实现点击外部不消失，点击反键才消失
            //			mPopupWindow.showAtLocation(mCityTv, Gravity.TOP|Gravity.RIGHT, 0, 0); //设置layout在PopupWindow中显示的位置
            // mPopupWindow.showAsDropDown(mCityLay);
            val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            popView!!.measure(w, h)
            val height = popView!!.measuredHeight
            val width = popView!!.measuredWidth
            println("measure width=$width height=$height")
            val location = IntArray(2)
            view.getLocationOnScreen(location)

            mPopupWindow!!.showAtLocation(view, Gravity.TOP or Gravity.LEFT, location[0] - width + view.width, location[1] - height)

            //mPopupWindow.showAtLocation(view,Gravity.TOP,location[0],location[1]-height);
        }

        // mLoadingView.cancleView();
    }

}
