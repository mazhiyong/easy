package com.lairui.easy.ui.temporary.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Message

import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.SwipeMenuAdapter
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.db.IndexData
import com.lairui.easy.mywidget.dialog.MySelectDialog
import com.lairui.easy.listener.MyClickableSpan
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.utils.tool.DataHolder
import com.lairui.easy.utils.tool.FileUtils
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil
import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

/**
 * 申请额度 界面
 */
class ApplyAmountActivity : BasicActivity(), RequestView, SelectBackListener {

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
    @BindView(R.id.qixian_lay)
    lateinit var mQixianLay: CardView
    @BindView(R.id.zhouqi_value_tv)
    lateinit var mZhouqiValueTv: TextView
    @BindView(R.id.zhongqi_lay)
    lateinit var mZhongqiLay: CardView
    @BindView(R.id.lilv_value_tv)
    lateinit var mLilvValueTv: TextView
    @BindView(R.id.lilv_type_lay)
    lateinit var mLilvTypeLay: CardView
    @BindView(R.id.daikuan_use_value_tv)
    lateinit var mDaikuanUseValueTv: TextView
    @BindView(R.id.daikuan_use_lay)
    lateinit var mDaikuanUseLay: CardView
    @BindView(R.id.daikuan_kind_value_tv)
    lateinit var mDaikuanKindValueTv: TextView
    @BindView(R.id.daikuan_kind_lay)
    lateinit var mDaikuanKindLay: CardView
    @BindView(R.id.pay_type_value_tv)
    lateinit var mPayTypeValueTv: TextView
    @BindView(R.id.pay_type_lay)
    lateinit var mPayTypeLay: CardView
    @BindView(R.id.fujian_lay)
    lateinit var mFujianLay: CardView
    @BindView(R.id.add_same_people_lay)
    lateinit var mAddSamePeopleLay: CardView
    @BindView(R.id.other_zhognqi_line)
    lateinit var mOtherZhognqiLine: View
    @BindView(R.id.other_zhouqi_eddit)
    lateinit var mOtherZhouqiEddit: EditText
    @BindView(R.id.other_zhonqi_lay)
    lateinit var mOtherZhonqiLay: CardView
    @BindView(R.id.same_people_list)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.scrollView_content)
    lateinit var mNestedScrollView: NestedScrollView
    @BindView(R.id.but_submit)
    lateinit var mButSubmit: Button
    @BindView(R.id.has_upload_tv)
    lateinit var mHasUploadTv: TextView
    @BindView(R.id.add_file_tv)
    lateinit var mAddFileTv: TextView
    @BindView(R.id.file_num_tv)
    lateinit var mFileNumTv: TextView
    @BindView(R.id.add_fujian_title)
    lateinit var mAddFujianTitleTv: TextView
    @BindView(R.id.xieyi_tv)
    lateinit var mXieyiTv: TextView
    @BindView(R.id.cb_xieyi)
    lateinit var mXieyiCheckBox: CheckBox
    @BindView(R.id.xieyi_lay)
    lateinit var mXieyiLay: LinearLayout
    @BindView(R.id.add_gtpeople_Lay)
    lateinit var mAddGtpeopleLay: LinearLayout

    @BindView(R.id.has_fujian_lay)
    lateinit var mHasFujianLay: CardView
    @BindView(R.id.bulu_lay)
    lateinit var mBuLuLay: LinearLayout
    @BindView(R.id.bulu_divide_view)
    lateinit var mBuluDivideView: View
    @BindView(R.id.has_upload_tv2)
    lateinit var mHasUploadTv2: TextView
    @BindView(R.id.add_file_tv2)
    lateinit var mAddFileTv2: TextView
    @BindView(R.id.file_num_tv2)
    lateinit var mFileNumTv2: TextView
    @BindView(R.id.add_fujian_lay)
    lateinit var mAddFujinaLay: LinearLayout


    private lateinit var mPayZhouQiMap: MutableMap<String, Any>
    private lateinit var mLilvMap: MutableMap<String, Any>
    private lateinit var mDaikuanUseMap: MutableMap<String, Any>
    private lateinit var mDaikuanZhonglMap: MutableMap<String, Any>
    private lateinit var mJieKuanQxianMap: MutableMap<String, Any>

    private lateinit var mHuanKuanTypeMap: MutableMap<String, Any>

    private val mAuthCode = ""

    private lateinit var mHezuoMap: MutableMap<String, Any>
    private lateinit var mConfigMap: MutableMap<String, Any>

    private var mIsNOPeople = ""

    private var mPeopleList: MutableList<MutableMap<String, Any>>? = ArrayList()

    private lateinit var mSwipeMenuAdapter: SwipeMenuAdapter
    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter

    private var mRequestTag = ""

    private lateinit var mDefaultMap: MutableMap<String, Any>

    private var mIsModify: Int = 0

    private var mPreId: String? = ""

    private lateinit var mIndexData: IndexData

    private var mKind = ""

    override val contentView: Int
        get() = R.layout.activity_apply_amount


    private var mFileList: MutableList<MutableMap<String, Any>>? = ArrayList()
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
                        mConfigMap!!["contList"] = list
                        mFileList!!.clear()
                        for (map in list) {
                            val resultMap = HashMap<String, Any>()
                            resultMap["connpk"] = map["connpk"]!!.toString() + ""
                            resultMap["name"] = map["name"]!!.toString() + ""
                            var files = map["resultData"] as List<MutableMap<String, Any>>?
                            if (files != null) {
                                resultMap["files"] = files
                                num = num + files.size
                            } else {
                                files = ArrayList()
                                resultMap["files"] = files
                            }
                            mFileList!!.add(resultMap)
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
                    LogUtil.i("打印log日志", "###################################################" + mFileList!!)
                }
            }
        }
    }


    private lateinit var mPayZhouqiDialog: MySelectDialog
    private lateinit var mLilvDialog: MySelectDialog
    private lateinit var mDaiKuanUseDialog: MySelectDialog
    private lateinit var mDaiKuanKindDialog: MySelectDialog
    private lateinit var mJieKuanLimitDialog: MySelectDialog
    private lateinit var mHkTypeDialog: MySelectDialog

    private var isCheck = false


    @SuppressLint("HandlerLeak")
     var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            when (msg.what) {
                1 -> enterNextPage()
                2 -> showToastMsg("人脸验证授权失败")
            }
        }
    }


    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mHezuoMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
            if (bundle.containsKey("TYPE")) {
                mIsModify = bundle.getInt("TYPE")
                mPreId = bundle.getString("precreid")

            }
        }
        mIndexData = IndexData.instance
        mTitleText!!.text = resources.getString(R.string.get_my_num)
        mXieyiLay!!.visibility = View.GONE
        UtilTools.setMoneyEdit(mMoneyEdit!!, 0.0)

        val filter = IntentFilter()
        filter.addAction(MbsConstans.BroadcastReceiverAction.FILE_TIP_ACTION)
        registerReceiver(receiver, filter)

        mAddFileTv!!.visibility = View.VISIBLE
        mHasUploadTv!!.visibility = View.GONE
        mFileNumTv!!.visibility = View.GONE

        mOtherZhonqiLay!!.visibility = View.GONE
        mOtherZhognqiLine!!.visibility = View.GONE
        initDialog()


        val manager = LinearLayoutManager(this)
        manager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = manager

        if (MbsConstans.USER_MAP != null) {
            mKind = MbsConstans.USER_MAP!!["firm_kind"]!!.toString() + ""
        }
        if (mKind == "1") {
            mAddGtpeopleLay!!.visibility = View.GONE
        } else {
            mAddGtpeopleLay!!.visibility = View.VISIBLE
        }

        if (mIsModify == 1) {
            getModifyAction()
            mBuLuLay!!.visibility = View.VISIBLE
            mBuluDivideView!!.visibility = View.VISIBLE
            mAddFujianTitleTv!!.text = resources.getString(R.string.upload_bulu)
        } else {
            getConfigAction()// 查询授信申请配置
            mBuLuLay!!.visibility = View.GONE
            mBuluDivideView!!.visibility = View.GONE
            mAddFujianTitleTv!!.text = resources.getString(R.string.upload_fuji)
        }

        mDaikuanKindLay!!.isEnabled = false
        if (MbsConstans.USER_MAP != null) {
            val kind = MbsConstans.USER_MAP!!["firm_kind"]!!.toString() + ""
            if (kind == "1") {//客户类型（0：车主，1：物流公司）
                mDaikuanZhonglMap = SelectDataUtil.getMap("101010", SelectDataUtil.getNameCodeByType("loanType"))
            } else if (kind == "0") {
                mDaikuanZhonglMap = SelectDataUtil.getMap("202010", SelectDataUtil.getNameCodeByType("loanType"))
            } else {
                mDaikuanKindLay!!.isEnabled = true
            }
            mDaikuanKindValueTv!!.text = mDaikuanZhonglMap!!["name"].toString() + ""
        }
        mLilvMap = SelectDataUtil.getMap("1", SelectDataUtil.lilvType)
    }

    override fun viewEnable() {
        //mButSubmit.setEnabled(true);
    }

    /**
     * 得到预授信详情  修改申请授信信息  比如驳回了要修改
     */
    private fun getModifyAction() {

        mRequestTag = MethodUrl.reqShouxinDetail
        val map = HashMap<String, String>()
        map["precreid"] = mPreId!! + ""//预授信申请ID
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.reqShouxinDetail, map)
    }

    /**
     * 查询授信申请配置
     */
    private fun getConfigAction() {

        mRequestTag = MethodUrl.creConfig
        val map = HashMap<String, String>()
        map["patncode"] = mHezuoMap!!["patncode"]!!.toString() + ""
        map["zifangbho"] = mHezuoMap!!["zifangbho"]!!.toString() + ""
        //        map.put("patncode", "CSHEZUOF");
        //        map.put("zifangbho", "BLOF1212");
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.creConfig, map)
    }


    private fun submitDataAction() {

        if (UtilTools.isEmpty(mMoneyEdit!!, "金额")) {
            showToastMsg("请填写金额")
            mButSubmit!!.isEnabled = true
            return
        }

        if (mJieKuanQxianMap == null || mJieKuanQxianMap!!.isEmpty()) {
            UtilTools.isEmpty(mQixianValueTv!!, "借款期限")
            showToastMsg("请选择借款期限")
            mButSubmit!!.isEnabled = true
            return
        }
        if (mHuanKuanTypeMap == null || mHuanKuanTypeMap!!.isEmpty()) {
            UtilTools.isEmpty(mPayTypeValueTv!!, "还款方式")
            showToastMsg("请选择还款方式")
            mButSubmit!!.isEnabled = true
            return
        }
        if (mPayZhouQiMap == null || mPayZhouQiMap!!.isEmpty()) {
            UtilTools.isEmpty(mZhouqiValueTv!!, "还款周期")
            showToastMsg("请选择还款周期")
            mButSubmit!!.isEnabled = true
            return
        } else {
            if (mPayZhouQiMap!!["code"]!!.toString() + "" == "19") {
                if (UtilTools.isEmpty(mOtherZhouqiEddit!!, "还款周期")) {
                    showToastMsg("请输入还款周期内容")
                    mButSubmit!!.isEnabled = true
                    return
                }
            }
        }

        if (mLilvMap == null || mLilvMap!!.isEmpty()) {
            UtilTools.isEmpty(mLilvValueTv!!, "利率")
            showToastMsg("请选择利率")
            mButSubmit!!.isEnabled = true
        }
        if (mDaikuanZhonglMap == null || mDaikuanZhonglMap!!.isEmpty()) {
            UtilTools.isEmpty(mDaikuanKindValueTv!!, "贷款种类")
            showToastMsg("请选择贷款种类")
            mButSubmit!!.isEnabled = true
            return
        }
        if (mDaikuanUseMap == null || mDaikuanUseMap!!.isEmpty()) {
            UtilTools.isEmpty(mDaikuanUseValueTv!!, "贷款用途")
            showToastMsg("请选择贷款用途")
            mButSubmit!!.isEnabled = true
            return
        }


        if (mIsNOPeople == "1") {//共同借款人校验（0：没有共借人 1：可以有共借人）
            if (mPeopleList == null || mPeopleList!!.size != 1) {
                showToastMsg("共同借款人必须添加一个")
                mButSubmit!!.isEnabled = true
                return
            }
        } else {
            if (mPeopleList == null) {
                mPeopleList = ArrayList()
            }
        }



        mRequestTag = MethodUrl.applySubmit
        val map = HashMap<String, Any>()
        map["patncode"] = mHezuoMap!!["patncode"]!!.toString() + ""
        map["zifangbho"] = mHezuoMap!!["zifangbho"]!!.toString() + ""
        map["precreid"] = mConfigMap!!["precreid"]!!.toString() + ""//预授信申请ID precreid
        map["creditmoney"] = mMoneyEdit!!.text.toString() + ""//申请金额
        map["singlelimit"] = mJieKuanQxianMap!!["code"]!!.toString() + ""//申请期限
        map["interestaccmode"] = mPayZhouQiMap!!["code"]!!.toString() + ""//还款周期

        var otherZhouqi = ""
        if (mPayZhouQiMap!!["code"]!!.toString() + "" == "19") {
            otherZhouqi = mOtherZhouqiEddit!!.text.toString() + ""
            if (UtilTools.isEmpty(mOtherZhouqiEddit!!, "还款周期")) {
                showToastMsg("请输入还款周期内容")
                mButSubmit!!.isEnabled = true
                return
            } else {
                map["interestaccnm"] = otherZhouqi//还款周期-其他
            }
        }
        map["lvtype"] = mLilvMap!!["code"]!!.toString() + ""//利率方式 0：浮动 1：固定
        map["reqloantp"] = mDaikuanZhonglMap!!["code"]!!.toString() + ""//贷款种类 Y01：个人综合消费贷款 Y02：个人经营性贷款 Y03：个人信用贷款
        map["loanuse"] = mDaikuanUseMap!!["code"]!!.toString() + ""//贷款用途 0：个人经营 1：个人授信额度服务 2：个人综合消费 3：商品交易 4：资金周转
        map["hktype"] = mHuanKuanTypeMap!!["code"]!!.toString() + ""//还款方式

        if (mIsModify == 1) {

            map["gtList"] = mPeopleList!!//共同借款人列表
            map["modflag"] = "1"//新增修改标识 （0：新增 1：修改）
            if (mFileList == null && mFileList!!.size <= 0) {
                mFileList = ArrayList()
                map["contList"] = mFileList!!//附件列表
            } else {
                map["contList"] = mFileList!!//附件列表
            }
        } else {
            map["modflag"] = "0"//新增修改标识 （0：新增 1：修改）
            map["gtList"] = mPeopleList!!//共同借款人列表
            val fileConfigList = mConfigMap!!["contList"] as List<MutableMap<String, Any>>?
            for (map1 in fileConfigList!!) {
                val sign = map1["isreq"]!!.toString() + ""//是否必传(0:否1是)
                val connpk = map1["connpk"]!!.toString() + ""

                if (sign == "1") {
                    if (mFileList == null || mFileList!!.size <= 0) {
                        mButSubmit!!.isEnabled = true
                        showToastMsg("请上传必传的附件")
                        return
                    } else {
                        for (map2 in mFileList!!) {
                            val code = map2["connpk"]!!.toString() + ""
                            val files = map2["files"] as List<MutableMap<String, Any>>?
                            if (code == connpk) {
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
            map["contList"] = mFileList!!//附件列表
        }

        LogUtil.i("打印log日志", "申请额度提交参数$map")

        if (!mXieyiCheckBox!!.isChecked) {
            showToastMsg(resources.getString(R.string.xieyi_tips))
            return
        }

        if (isCheck) {//是否已经人脸认证

        } else {
            PermissionsUtils.requsetRunPermission(this@ApplyAmountActivity, object : RePermissionResultBack {
                override fun requestSuccess() {
                    netWorkWarranty()
                }

                override fun requestFailer() {
                    toast(R.string.failure)
                    mButSubmit!!.isEnabled = true
                }
            },com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)


            mButSubmit!!.isEnabled = true
            return
        }

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.applySubmit, map)
    }


    private fun initFujianView() {
        val mList = mConfigMap!!["contList"] as List<MutableMap<String, Any>>?
        if (mList!!.size == 0) {
            mAddFujinaLay!!.visibility = View.GONE
        } else {
            mAddFujinaLay!!.visibility = View.VISIBLE
        }
    }


    private fun initModifyValue() {

        mMoneyEdit!!.setText(UtilTools.getShuziMoney(mDefaultMap!!["creditmoney"]!!.toString() + ""))
        //mJieKuanQxianMap = SelectDataUtil.getMap(mDefaultMap.get("singlelimit")+"",SelectDataUtil.jieKuanLimit());
        mJieKuanQxianMap = SelectDataUtil.getMap(mDefaultMap!!["singlelimit"]!!.toString() + "", SelectDataUtil.getNameCodeByType("loanLimit"))
        mQixianValueTv!!.text = mJieKuanQxianMap!!["name"]!!.toString() + ""//借款期限


        //mPayZhouQiMap  = SelectDataUtil.getMap(mDefaultMap.get("interestaccmode")+"",SelectDataUtil.getHkZhouqi());
        mPayZhouQiMap = SelectDataUtil.getMap(mDefaultMap!!["interestaccmode"]!!.toString() + "", SelectDataUtil.getNameCodeByType("repayCycle"))
        mZhouqiValueTv!!.text = mPayZhouQiMap!!["name"]!!.toString() + ""
        if (mPayZhouQiMap!!["code"]!!.toString() + "" == "19") {
            mOtherZhouqiEddit!!.setText(mDefaultMap!!["interestaccnm"]!!.toString() + "")
            mOtherZhonqiLay!!.visibility = View.VISIBLE
            mOtherZhognqiLine!!.visibility = View.VISIBLE
        } else {
            mOtherZhonqiLay!!.visibility = View.GONE
            mOtherZhognqiLine!!.visibility = View.GONE
        }

        mLilvMap = SelectDataUtil.getMap(mDefaultMap!!["lvtype"]!!.toString() + "", SelectDataUtil.lilvType)
        mLilvValueTv!!.text = mLilvMap!!["name"]!!.toString() + ""

        //mDaikuanZhonglMap = SelectDataUtil.getMap(mDefaultMap.get("reqloantp")+"",SelectDataUtil.getDaikuanType());
        mDaikuanZhonglMap = SelectDataUtil.getMap(mDefaultMap!!["reqloantp"]!!.toString() + "", SelectDataUtil.getNameCodeByType("loanType"))
        mDaikuanKindValueTv!!.text = mDaikuanZhonglMap!!["name"]!!.toString() + ""

        //mDaikuanUseMap = SelectDataUtil.getMap(mDefaultMap.get("loanuse")+"",SelectDataUtil.getDaikuanUse());
        mDaikuanUseMap = SelectDataUtil.getMap(mDefaultMap!!["loanuse"]!!.toString() + "", SelectDataUtil.getNameCodeByType("loanUse"))
        mDaikuanUseValueTv!!.text = mDaikuanUseMap!!["name"]!!.toString() + ""

        //mHuanKuanTypeMap = SelectDataUtil.getMap(mDefaultMap.get("hktype")+"",SelectDataUtil.getHkType());
        mHuanKuanTypeMap = SelectDataUtil.getMap(mDefaultMap!!["hktype"]!!.toString() + "", SelectDataUtil.getNameCodeByType("repayWay"))
        mPayTypeValueTv!!.text = mHuanKuanTypeMap!!["name"]!!.toString() + ""


        if (mDefaultMap!!["gtList"] != null) {
            mPeopleList = ArrayList((mDefaultMap!!["gtList"] as List<MutableMap<String, Any>>?)!!)
        }

        responseData()

        val mFileTypeList = mDefaultMap!!["contList"] as List<MutableMap<String, Any>>?

        val mHasFile = mDefaultMap!!["existFileList"] as List<MutableMap<String, Any>>?
        var num = 0
        if (mHasFile != null) {
            for (fileMap in mHasFile) {
                val files = fileMap["files"] as List<MutableMap<String, Any>>?
                for (map in files!!) {
                    val timeList = map["optFiles"] as List<MutableMap<String, Any>>?
                    num = num + timeList!!.size
                }
            }
        }

        if (num != 0) {
            mAddFileTv2!!.visibility = View.GONE
            mHasUploadTv2!!.visibility = View.VISIBLE
            mFileNumTv2!!.visibility = View.VISIBLE

        } else {
            mAddFileTv2!!.visibility = View.GONE
            mHasUploadTv2!!.visibility = View.VISIBLE
            mFileNumTv2!!.visibility = View.VISIBLE

        }
        mFileNumTv2!!.text = num.toString() + "个"


    }


    override fun showProgress() {

        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

        //{smstoken=sms_token@3948602038bb8ecf912e0ede4a577ebd, send_tel=151****3298}
        val intent: Intent
        when (mType) {
            MethodUrl.reqShouxinDetail -> {

                mDefaultMap = tData
                mConfigMap = tData
                mConfigMap!!["precreid"] = mPreId!!
                mIsNOPeople = mConfigMap!!["needgt"]!!.toString() + ""
                mHezuoMap = HashMap()
                mHezuoMap!!["patncode"] = mDefaultMap!!["patncode"]!!.toString() + ""
                mHezuoMap!!["zifangbho"] = mDefaultMap!!["zifangbho"]!!.toString() + ""
                initModifyValue()
                initFujianView()
            }
            MethodUrl.applySubmit -> {
                isCheck = false
                mButSubmit!!.isEnabled = true
                showToastMsg("申请成功")

                if (mIsModify == 1) {//是驳回修改的
                    val intent1 = Intent()
                    intent1.action = MbsConstans.BroadcastReceiverAction.DAIBAN_INFO_UPDATE
                    sendBroadcast(intent1)
                }

                intent = Intent(this@ApplyAmountActivity, SubmitResultActivity::class.java)
                intent.putExtra(MbsConstans.ResultType.RESULT_KEY, MbsConstans.ResultType.RESULT_APPLY_MONEY)
                startActivity(intent)
                finish()
            }
            MethodUrl.creConfig -> {
                mConfigMap = tData
                initFujianView()
                mIsNOPeople = mConfigMap!!["needgt"]!!.toString() + ""
                initXieyiView()
                mButSubmit!!.isEnabled = true
                if (mIsModify == 1) {
                    getModifyAction()
                }
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.creConfig -> getConfigAction()
                    MethodUrl.applySubmit -> submitDataAction()
                    MethodUrl.reqShouxinDetail -> getModifyAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.applySubmit -> {
                isCheck = false
                mButSubmit!!.isEnabled = true
            }
            MethodUrl.creConfig -> mButSubmit!!.isEnabled = false
        }
        dealFailInfo(map, mType)
    }


    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {

        val value = map["name"]!!.toString() + ""
        when (type) {
            100 -> {
                mPayZhouQiMap = map
                mZhouqiValueTv!!.text = value
                if (mPayZhouQiMap!!["code"]!!.toString() + "" == "19") {
                    mOtherZhonqiLay!!.visibility = View.VISIBLE
                    mOtherZhognqiLine!!.visibility = View.VISIBLE
                } else {
                    mOtherZhonqiLay!!.visibility = View.GONE
                    mOtherZhognqiLine!!.visibility = View.GONE
                    mOtherZhouqiEddit!!.setError(null, null)
                }
                mZhouqiValueTv!!.setError(null, null)
            }
            101 -> {
                mLilvMap = map
                mLilvValueTv!!.text = value
                mLilvValueTv!!.setError(null, null)
            }
            102 -> {
                mDaikuanUseMap = map
                mDaikuanUseValueTv!!.text = value
                mDaikuanUseValueTv!!.setError(null, null)
            }
            103 -> {
                mDaikuanZhonglMap = map
                mDaikuanKindValueTv!!.text = value
                mDaikuanKindValueTv!!.setError(null, null)
            }
            104 -> {
                mJieKuanQxianMap = map
                mQixianValueTv!!.text = value
                mQixianValueTv!!.setError(null, null)
            }
            105 -> {
                mHuanKuanTypeMap = map
                mPayTypeValueTv!!.text = value
                mPayTypeValueTv!!.setError(null, null)
            }
        }
    }


    @OnClick(R.id.qixian_lay, R.id.zhongqi_lay, R.id.lilv_type_lay, R.id.daikuan_use_lay, R.id.daikuan_kind_lay, R.id.has_fujian_lay, R.id.pay_type_lay, R.id.back_img, R.id.fujian_lay, R.id.add_same_people_lay, R.id.but_submit, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.qixian_lay -> mJieKuanLimitDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.zhongqi_lay -> showPayZhouqiDialog()
            R.id.lilv_type_lay -> showLilvDialog()
            R.id.daikuan_use_lay -> showDaikuanUseDialog()
            R.id.daikuan_kind_lay -> showDaikuanKindDialog()
            R.id.pay_type_lay -> showHuanKuanDialog()
            R.id.has_fujian_lay -> if (mIsModify == 1 && mDefaultMap != null) {
                var mHasFile = mDefaultMap!!["existFileList"] as List<MutableMap<String, Any>>?
                if (mHasFile == null) {
                    mHasFile = ArrayList()
                }
                intent = Intent(this@ApplyAmountActivity, ModifyFileActivity::class.java)
                //intent.putExtra("DATA",(Serializable) mHasFile);
                DataHolder.instance!!.save("fileList", mHasFile)
                startActivity(intent)
            } else {
            }
            R.id.fujian_lay -> PermissionsUtils.requsetRunPermission(this@ApplyAmountActivity, object : RePermissionResultBack {
                override fun requestSuccess() {
                    if (mConfigMap != null) {
                        val intent = Intent(this@ApplyAmountActivity, AddFileActivity::class.java)
                        intent.putExtra("DATA", mConfigMap as Serializable?)
                        intent.putExtra("TYPE", "1")
                        startActivityForResult(intent, 300)
                    }
                }

                override fun requestFailer() {

                }
            }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)
            R.id.add_same_people_lay -> {
                intent = Intent(this@ApplyAmountActivity, AddSamePeopleActivity::class.java)
                intent.putExtra("DATA", mHezuoMap as Serializable?)
                startActivityForResult(intent, 200)
            }
            R.id.but_submit -> {
                /*
                netWorkWarranty();
*/
                mButSubmit!!.isEnabled = false
                submitDataAction()
            }
        }/*intent = new Intent(ApplyAmountActivity.this, AddFileActivity.class);
                intent.putExtra("DATA", (Serializable) mConfigMap);
                intent.putExtra("TYPE","1");
                startActivityForResult(intent, 300);*/
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                // case PictureConfig.CHOOSE_REQUEST:
                // 图片、视频、音频选择结果回调
                // List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                //adapter.setList(selectList);
                // adapter.notifyDataSetChanged();
                //     break;
                200 -> {
                    val bundle = data!!.extras
                    if (bundle != null) {
                        val gtfirmname = bundle.getString("name")
                        val gtjkzjno = bundle.getString("idno")
                        val gtjkrel = bundle.getString("guanxi")
                        val gtcustid = bundle.getString("custid")
                        val gtjkrelnm = bundle.getString("other")
                        val map = HashMap<String, Any>()
                        map["gtfirmname"] = gtfirmname!!
                        map["gtjkzjno"] = gtjkzjno!!
                        map["gtjkrel"] = gtjkrel!!
                        map["gtcustid"] = gtcustid!!
                        if (gtjkrel == "3") {
                            map["gtjkrelnm"] = gtjkrelnm!!//其它
                        }
                        mPeopleList!!.add(map)
                        responseData()

                        if (mPeopleList != null && mPeopleList!!.size == 1) {
                            mAddSamePeopleLay!!.visibility = View.GONE
                        }
                    }
                }
                300 -> {

                    val bundle2 = data!!.extras
                    if (bundle2 != null) {
                        val list = bundle2.getSerializable("resultList") as List<MutableMap<String, Any>>
                    }
                }
            }
        }


        if (requestCode == 1) {
            when (resultCode) {
                //
                MbsConstans.FaceType.FACE_CHECK_APPLY -> {
                    val bundle = data!!.extras
                    if (bundle == null) {
                        mButSubmit!!.isEnabled = true
                        isCheck = false
                    } else {
                        mButSubmit!!.isEnabled = false
                        isCheck = true
                        submitDataAction()
                    }
                }
                else -> mButSubmit!!.isEnabled = true
            }

        } else if (requestCode == PAGE_INTO_LIVENESS) {//人脸识别返回来的数据
            if (resultCode == Activity.RESULT_OK) {
                val bundle = data!!.extras
                bundle!!.putInt(MbsConstans.FaceType.FACE_KEY, MbsConstans.FaceType.FACE_CHECK_APPLY)
                val intent = Intent(this@ApplyAmountActivity, ResultActivity::class.java)
                intent.putExtras(bundle)
                //设置返回数据
                startActivityForResult(intent, 1)
            } else {
                mButSubmit!!.isEnabled = true
            }
        }
    }


    private fun responseData() {

        if (mPeopleList != null && mPeopleList!!.size == 1) {
            mAddSamePeopleLay!!.visibility = View.GONE
        }

        mSwipeMenuAdapter = SwipeMenuAdapter(this)
        mPeopleList?.let { mSwipeMenuAdapter!!.setDataList(it) }

        mSwipeMenuAdapter!!.setOnDelListener(object : SwipeMenuAdapter.onSwipeListener {
            override fun onDel(pos: Int) {
                //Toast.makeText(ApplyAmountActivity.this, "删除:" + pos, Toast.LENGTH_SHORT).show();

                /*//RecyclerView关于notifyItemRemoved的那点小事 参考：http://blog.csdn.net/jdsjlzx/article/details/52131528
                mSwipeMenuAdapter.getDataList().remove(pos);
                mSwipeMenuAdapter.notifyItemRemoved(pos);//推荐用这个
                if(pos != (mSwipeMenuAdapter.getDataList().size())){ // 如果移除的是最后一个，忽略 注意：这里的mDataAdapter.getDataList()不需要-1，因为上面已经-1了
                    mSwipeMenuAdapter.notifyItemRangeChanged(pos, mSwipeMenuAdapter.getDataList().size() - pos);
                }
                mSwipeMenuAdapter.notifyDataSetChanged();*/

                mPeopleList!!.removeAt(pos)
                responseData()

                if (mPeopleList!!.size <= 0) {
                    mAddSamePeopleLay!!.visibility = View.VISIBLE
                }

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
        mRefreshListView!!.adapter = mLRecyclerViewAdapter
        mRefreshListView!!.itemAnimator = DefaultItemAnimator()
        mRefreshListView!!.setHasFixedSize(true)
        mRefreshListView!!.isNestedScrollingEnabled = false

        mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        mRefreshListView!!.setPullRefreshEnabled(false)
        mRefreshListView!!.setLoadMoreEnabled(false)

        mRefreshListView!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
        mRefreshListView!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

        mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
    }


    private fun initDialog() {
        //List<Map<String, Object>> list1 = SelectDataUtil.getHkZhouqi();
        val list1 = SelectDataUtil.getNameCodeByType("repayCycle")
        mPayZhouqiDialog = MySelectDialog(this, true, list1, "还款周期", 100)
        mPayZhouqiDialog!!.selectBackListener = this

        val list2 = SelectDataUtil.lilvType
        mLilvDialog = MySelectDialog(this, true, list2, "利率", 101)
        mLilvDialog!!.selectBackListener = this

        //List<Map<String, Object>> list3 = SelectDataUtil.getDaikuanUse();
        val list3 = SelectDataUtil.getNameCodeByType("loanUse")
        mDaiKuanUseDialog = MySelectDialog(this, true, list3, "贷款用途", 102)
        mDaiKuanUseDialog!!.selectBackListener = this

        //List<Map<String, Object>> list4 = SelectDataUtil.getDaikuanType();
        val list4 = SelectDataUtil.getNameCodeByType("loanType")
        mDaiKuanKindDialog = MySelectDialog(this, true, list4, "贷款种类", 103)
        mDaiKuanKindDialog!!.selectBackListener = this

        //List<Map<String, Object>> list5 = SelectDataUtil.jieKuanLimit();
        val list5 = SelectDataUtil.getNameCodeByType("loanLimit")
        mJieKuanLimitDialog = MySelectDialog(this, true, list5, "借款期限", 104)
        mJieKuanLimitDialog!!.selectBackListener = this

        //List<Map<String, Object>> list6 = SelectDataUtil.getHkType();
        val list6 = SelectDataUtil.getNameCodeByType("repayWay")
        mHkTypeDialog = MySelectDialog(this, true, list6, "还款方式", 105)
        mHkTypeDialog!!.selectBackListener = this
    }


    private fun showPayZhouqiDialog() {
        mPayZhouqiDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
    }

    private fun showLilvDialog() {

        mLilvDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
    }

    private fun showDaikuanUseDialog() {

        mDaiKuanUseDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
    }

    private fun showDaikuanKindDialog() {

        mDaiKuanKindDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
    }

    private fun showHuanKuanDialog() {
        mHkTypeDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
    }


    private fun initXieyiView() {
        mXieyiLay!!.visibility = View.VISIBLE
        mXieyiCheckBox!!.isChecked = true
        var xiyiStr = "已阅读并同意签署"
        if (mConfigMap != null) {
            val xieyiList = mConfigMap!!["signConts"] as List<MutableMap<String, Any>>?
            if (xieyiList != null && xieyiList.size > 0) {
                for (i in xieyiList.indices) {
                    val map = xieyiList[i]
                    val str = map["pdfname"]!!.toString() + ""
                    if (i == xieyiList.size - 1) {
                        xiyiStr = "$xiyiStr《$str》"
                    } else {
                        xiyiStr = "$xiyiStr《$str》、"
                    }
                }
                val sp = SpannableString(xiyiStr)

                for (map in xieyiList) {
                    val str = map["pdfname"]!!.toString() + ""
                    setClickableSpan(sp, View.OnClickListener {
                        val intent = Intent(this@ApplyAmountActivity, PDFLookActivity::class.java)
                        intent.putExtra("id", map["pdfurl"]!!.toString() + "")
                        startActivity(intent)
                    }, xiyiStr, "《$str》")
                }
                mXieyiTv!!.text = sp
            } else {
                mXieyiTv!!.text = xiyiStr
            }

        }
        //添加点击事件时，必须设置
        mXieyiTv!!.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setClickableSpan(sp: SpannableString, l: View.OnClickListener, str: String, span: String): SpannableString {
        sp.setSpan(MyClickableSpan(-0xe36e16, l), str.indexOf(span), str.indexOf(span) + span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sp
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

    private fun enterNextPage() {
        //startActivityForResult(Intent(this, LivenessActivity::class.java), PAGE_INTO_LIVENESS)
    }

    /**
     * 联网授权
     */
    private fun netWorkWarranty() {

       /* val uuid = ConUtil.getUUIDString(this)

        Thread(Runnable {
            val manager = Manager(this@ApplyAmountActivity)
            val licenseManager = LivenessLicenseManager(this@ApplyAmountActivity)
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

    companion object {
        private val PAGE_INTO_LIVENESS = 101
    }
}
