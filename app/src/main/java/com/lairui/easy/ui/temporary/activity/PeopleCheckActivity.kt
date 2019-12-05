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

import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.MyClickableSpan
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil


import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.MainActivity

/**
 * 共同借款人 审核页面
 */
class PeopleCheckActivity : BasicActivity(), RequestView, ReLoadingData {


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
    @BindView(R.id.jkren_value_tv)
    lateinit var mJkrenValueTv: TextView
    @BindView(R.id.jkjine_value_tv)
    lateinit var mJkjineValueTv: TextView
    @BindView(R.id.jkqixian_value_tv)
    lateinit var mJkqixianValueTv: TextView
    @BindView(R.id.hkzhouqi_value_tv)
    lateinit var mHkzhouqiValueTv: TextView
    @BindView(R.id.lilvfangshi_value_tv)
    lateinit var mLilvfangshiValueTv: TextView
    @BindView(R.id.dkzhonglei_value_tv)
    lateinit var mDkzhongleiValueTv: TextView
    @BindView(R.id.dkyongtu_value_tv)
    lateinit var mDkyongtuValueTv: TextView
    @BindView(R.id.hkfangshi_value_tv)
    lateinit var mHkfangshiValueTv: TextView
    @BindView(R.id.chujieren_value_tv)
    lateinit var mChujierenValueTv: TextView
    @BindView(R.id.tongyi_value_switch)
    lateinit var mTongyiValueSwitch: Switch
    @BindView(R.id.refuse_edit_lay)
    lateinit var mResuseEditLay: RelativeLayout
    @BindView(R.id.resuse_des_edit)
    lateinit var mResuseDesEdit: EditText
    @BindView(R.id.count)
    lateinit var mCount: TextView
    @BindView(R.id.xieyi_tv)
    lateinit var mXieyiTv: TextView
    @BindView(R.id.btn_submit)
    lateinit var mBtnSubmit: Button
    @BindView(R.id.other_value_tv)
    lateinit var mOtherValueTv: TextView
    @BindView(R.id.other_lay)
    lateinit var mOtherLay: LinearLayout
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView

    @BindView(R.id.cb_xieyi)
    lateinit var mXieyiCheckBox: CheckBox
    @BindView(R.id.xiyi_lay)
    lateinit var mXieyiLay: LinearLayout

    private val MAX_COUNT = 100

    private var mRequestTag = ""

    private lateinit var mDataMap: MutableMap<String, Any>

    private lateinit var mValueMap: MutableMap<String, Any>

    private lateinit var mZhengshuMap: MutableMap<String, Any>

    private var mIsNO = "2"

    override val contentView: Int
        get() = R.layout.activity_people_check

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == MbsConstans.BroadcastReceiverAction.ZHENGSHU_UPDATE) {
                isInstallCer()
            } else if (action == MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE) {
                getUserInfoAction()
            }
        }
    }


    private val mTextWatcher = object : TextWatcher {

        private var editStart: Int = 0

        private var editEnd: Int = 0

        override fun afterTextChanged(s: Editable) {
            editStart = mResuseDesEdit!!.selectionStart
            editEnd = mResuseDesEdit!!.selectionEnd

            // 先去掉监听器，否则会出现栈溢出
            mResuseDesEdit!!.removeTextChangedListener(this)

            // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
            // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
            while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
                s.delete(editStart - 1, editEnd)
                editStart--
                editEnd--
            }
            mResuseDesEdit!!.text = s
            mResuseDesEdit!!.setSelection(editStart)

            // 恢复监听器
            mResuseDesEdit!!.addTextChangedListener(this)

            setLeftCount()
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                       after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                   count: Int) {
        }
    }

    /**
     * 获取用户输入的分享内容字数
     *
     * @return
     */
    private val inputCount: Long
        get() = calculateLength(mResuseDesEdit!!.text.toString())


    private var isCheck = false


    @SuppressLint("HandlerLeak")
    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            when (msg.what) {
                1 -> enterNextPage()
                2 -> {
                    showToastMsg("人脸验证授权失败")
                    mBtnSubmit!!.isEnabled = true
                }
            }
        }
    }

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.shenhe)
        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mDataMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }
        mXieyiLay!!.visibility = View.GONE
        val intentFilter = IntentFilter()
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.ZHENGSHU_UPDATE)
        intentFilter.addAction(MbsConstans.BroadcastReceiverAction.USER_INFO_UPDATE)
        registerReceiver(mBroadcastReceiver, intentFilter)
        isInstallCer()
        // 先去掉监听器，否则会出现栈溢出
        mResuseDesEdit!!.addTextChangedListener(mTextWatcher)
        setLeftCount()
        mResuseDesEdit!!.setSelection(mResuseDesEdit!!.length())


        mResuseEditLay!!.visibility = View.GONE
        mTongyiValueSwitch!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mIsNO = "2"
                mResuseEditLay!!.visibility = View.GONE
            } else {
                mIsNO = "4"
                mResuseEditLay!!.visibility = View.VISIBLE
            }
        }

        mContent?.let { mPageView!!.setContentView(it) }
        //mPageView.showEmpty();
        mPageView!!.showLoading()
        mPageView!!.reLoadingData = this
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }

    /**
     * 审核详情信息
     */
    private fun getInfoAction() {

        mRequestTag = MethodUrl.prePeopleCheck
        val map = HashMap<String, String>()
        map["patncode"] = mDataMap!!["patncode"]!!.toString() + ""//合作方编号
        map["zifangbho"] = mDataMap!!["zifangbho"]!!.toString() + ""//资方编号
        map["precreid"] = mDataMap!!["precreid"]!!.toString() + ""//预授信申请ID
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.prePeopleCheck, map)
    }

    /**
     * 是否安装证书
     */
    private fun isInstallCer() {

        mRequestTag = MethodUrl.isInstallCer
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.isInstallCer, map)
    }

    /**
     * 获取用户基本信息
     */
    fun getUserInfoAction() {
        mRequestTag = MethodUrl.userInfo

        val map = HashMap<String, Any>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.userInfo, map)
    }


    private fun submitAction() {
        if (MbsConstans.USER_MAP != null) {
            val ss = MbsConstans.USER_MAP!!["cmpl_info"]!!.toString() + ""
            val zhengshu = MbsConstans.USER_MAP!!["auth"]!!.toString() + ""
            if (ss == "1") {//是否完善信息（0：未完善，1：已完善

                if (zhengshu == "1") {//是否认证（0：未认证，1：已认证

                } else {
                    if (mZhengshuMap != null && !mZhengshuMap!!.isEmpty()) {
                        val state = mZhengshuMap!!["state"]!!.toString() + ""
                        val intent = Intent(this@PeopleCheckActivity, IdCardSuccessActivity::class.java)
                        intent.putExtra("verify_type", mZhengshuMap!!["verify_type"]!!.toString() + "")
                        intent.putExtra(MbsConstans.FaceType.FACE_KEY, MbsConstans.FaceType.FACE_INSTALL)
                        startActivity(intent)
                        mBtnSubmit!!.isEnabled = true
                        return
                    } else {
                        showToastMsg("请先完善认证信息")
                        mBtnSubmit!!.isEnabled = true
                        return
                    }
                }
            } else {
                val intent = Intent(this, PerfectInfoActivity::class.java)
                intent.putExtra("type", "3")
                startActivity(intent)
                showToastMsg("请先完善信息")
                mBtnSubmit!!.isEnabled = true
                return
            }
        } else {
            MainActivity.mInstance.getUserInfoAction()
            showToastMsg("获取用户信息失败,请重新获取")
            mBtnSubmit!!.isEnabled = true
            return

        }


        mRequestTag = MethodUrl.peopleCheckSure
        val map = HashMap<String, Any>()
        map["patncode"] = mDataMap!!["patncode"]!!.toString() + ""//合作方编号
        map["zifangbho"] = mDataMap!!["zifangbho"]!!.toString() + ""//资方编号
        map["precreid"] = mDataMap!!["precreid"]!!.toString() + ""//预授信申请ID
        map["state"] = mIsNO//审核状态 2：通过 4：驳回

        if (mIsNO == "2") {

        } else {
            if (UtilTools.isEmpty(mResuseDesEdit!!, "驳回理由")) {
                showToastMsg("驳回理由不能为空")
                mBtnSubmit!!.isEnabled = true
                return
            } else {
                map["reason"] = mResuseDesEdit!!.text.toString() + ""//驳回原因
            }
        }
        if (!mXieyiCheckBox!!.isChecked) {
            showToastMsg(resources.getString(R.string.xieyi_tips))
            mBtnSubmit!!.isEnabled = true
            return
        }

        if (isCheck) {

        } else {

            PermissionsUtils.requsetRunPermission(this@PeopleCheckActivity, object : RePermissionResultBack {
                override fun requestSuccess() {
                    netWorkWarranty()
                }

                override fun requestFailer() {
                    toast(R.string.failure)
                    mBtnSubmit!!.isEnabled = true
                }
            }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)


            mBtnSubmit!!.isEnabled = true
            return
        }

        LogUtil.i("驳回参数", map)
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.peopleCheckSure, map)
    }


    private fun initValue() {
        mJkrenValueTv!!.text = mValueMap!!["firmname"]!!.toString() + ""
        mJkjineValueTv!!.text = UtilTools.getRMBMoney(mValueMap!!["creditmoney"]!!.toString() + "")
        mJkqixianValueTv!!.text = mValueMap!!["singlelimit"]!!.toString() + " 月"

        //Map<String, Object> huanKuanType = SelectDataUtil.getMap(mValueMap.get("hktype") + "",SelectDataUtil.getHkType());
        val huanKuanType = SelectDataUtil.getMap(mValueMap!!["hktype"]!!.toString() + "", SelectDataUtil.getNameCodeByType("repayWay"))
        mHkfangshiValueTv!!.text = huanKuanType["name"]!!.toString() + ""

        //Map<String, Object> zhouqiMap = SelectDataUtil.getMap(mValueMap.get("interestaccmode") + "", SelectDataUtil.getHkZhouqi());
        val zhouqiMap = SelectDataUtil.getMap(mValueMap!!["interestaccmode"]!!.toString() + "", SelectDataUtil.getNameCodeByType("repayCycle"))
        mHkzhouqiValueTv!!.text = zhouqiMap["name"]!!.toString() + ""
        if (mValueMap!!["interestaccmode"]!!.toString() + "" == "19") {
            mOtherLay!!.visibility = View.VISIBLE
            mOtherValueTv!!.text = mValueMap!!["interestaccnm"]!!.toString() + ""
        } else {
            mOtherLay!!.visibility = View.GONE
        }

        val lilvMap = SelectDataUtil.getMap(mValueMap!!["lvtype"]!!.toString() + "", SelectDataUtil.lilvType)
        mLilvfangshiValueTv!!.text = lilvMap["name"]!!.toString() + ""

        //Map<String, Object> dkZhongleiMap = SelectDataUtil.getMap(mValueMap.get("reqloantp") + "",SelectDataUtil.getDaikuanType());
        val dkZhongleiMap = SelectDataUtil.getMap(mValueMap!!["reqloantp"]!!.toString() + "", SelectDataUtil.getNameCodeByType("loanType"))
        mDkzhongleiValueTv!!.text = dkZhongleiMap["name"]!!.toString() + ""

        //Map<String, Object> dkYongtuMap = SelectDataUtil.getMap(mValueMap.get("loanuse") + "", SelectDataUtil.getDaikuanUse());
        val dkYongtuMap = SelectDataUtil.getMap(mValueMap!!["loanuse"]!!.toString() + "", SelectDataUtil.getNameCodeByType("loanUse"))
        mDkyongtuValueTv!!.text = dkYongtuMap["name"]!!.toString() + ""

        mChujierenValueTv!!.text = mValueMap!!["zifangnme"]!!.toString() + ""

        initXieyiView()
    }

    private fun initXieyiView() {
        mXieyiLay!!.visibility = View.VISIBLE
        mXieyiCheckBox!!.isChecked = true
        var xiyiStr = "已阅读并同意签署"
        if (mValueMap != null) {
            val xieyiList = mValueMap!!["contList"] as List<MutableMap<String, Any>>?
            for (i in xieyiList!!.indices) {
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
                    val intent = Intent(this@PeopleCheckActivity, PDFLookActivity::class.java)
                    intent.putExtra("id", map["pdfurl"]!!.toString() + "")
                    startActivity(intent)
                }, xiyiStr, "《$str》")
            }
            mXieyiTv!!.text = sp
        }
        //添加点击事件时，必须设置
        mXieyiTv!!.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setClickableSpan(sp: SpannableString, l: View.OnClickListener, str: String, span: String): SpannableString {
        sp.setSpan(MyClickableSpan(-0xe36e16, l), str.indexOf(span), str.indexOf(span) + span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sp
    }

    @OnClick(R.id.back_img, R.id.btn_submit, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.btn_submit -> {
                mBtnSubmit!!.isEnabled = false
                submitAction()
            }
        }
    }

    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.userInfo//用户信息 //{auth=1, firm_kind=0, head_pic=default, name=刘英超, tel=151****3298, idno=4107****3616, cmpl_info=0}
            -> {
                MbsConstans.USER_MAP = tData
                SPUtils.put(this@PeopleCheckActivity, MbsConstans.SharedInfoConstans.LOGIN_INFO, JSONUtil.instance.objectToJson(MbsConstans.USER_MAP!!))
            }
            MethodUrl.peopleCheckSure -> {
                mBtnSubmit!!.isEnabled = true
                isCheck = false
                showToastMsg("审核完成")
                val intent = Intent()
                intent.action = MbsConstans.BroadcastReceiverAction.DAIBAN_INFO_UPDATE
                sendBroadcast(intent)
                finish()
            }
            MethodUrl.isInstallCer//{verify_type=FACE, state=0}
            -> {
                mZhengshuMap = tData
                getInfoAction()
            }
            MethodUrl.prePeopleCheck//
            -> {
                mPageView!!.showContent()
                mValueMap = tData
                initValue()
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.prePeopleCheck -> getInfoAction()
                    MethodUrl.isInstallCer -> isInstallCer()
                    MethodUrl.peopleCheckSure -> submitAction()
                    MethodUrl.userInfo -> getUserInfoAction()
                }
            }
        }//showUpdateDialog();

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.isInstallCer//{verify_type=FACE, state=0}
            -> mPageView!!.showNetworkError()
            MethodUrl.prePeopleCheck//
            -> mPageView!!.showNetworkError()
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
            }
            MethodUrl.peopleCheckSure -> {
                isCheck = false
                mBtnSubmit!!.isEnabled = true
            }
        }
        dealFailInfo(map, mType)
    }


    /**
     * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
     *
     * @param c
     * @return
     */
    private fun calculateLength(c: CharSequence): Long {
        var len = 0.0
        for (i in 0 until c.length) {
            val tmp = c[i].toInt()
            if (tmp > 0 && tmp < 127) {
                //len++;
                len += 0.5
            } else {
                len++
            }
        }
        return Math.round(len)
    }

    /**
     * 刷新剩余输入字数,最大值新浪微博是140个字，人人网是200个字
     */
    private fun setLeftCount() {
        mCount!!.text = (MAX_COUNT - inputCount).toString()
    }

    override fun reLoadingData() {
        getInfoAction()
    }

    private fun enterNextPage() {
       // startActivityForResult(Intent(this, LivenessActivity::class.java), PAGE_INTO_LIVENESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data)
        var intent: Intent
        val bundle: Bundle?
        if (requestCode == 1) {
            when (resultCode) {
                //通过短信验证码  安装证书
                MbsConstans.FaceType.FACE_PEOPLE_CHECK -> {
                    bundle = data!!.extras
                    if (bundle == null) {
                        isCheck = false
                        mBtnSubmit!!.isEnabled = true
                    } else {
                        mBtnSubmit!!.isEnabled = false
                        isCheck = true
                        submitAction()
                    }
                }
                else -> mBtnSubmit!!.isEnabled = true
            }

        } else if (requestCode == PAGE_INTO_LIVENESS) {//人脸识别返回来的数据
            if (resultCode == Activity.RESULT_OK) {
                bundle = data!!.extras
                bundle!!.putInt(MbsConstans.FaceType.FACE_KEY, MbsConstans.FaceType.FACE_PEOPLE_CHECK)
                intent = Intent(this@PeopleCheckActivity, ResultActivity::class.java)
                intent.putExtras(bundle)
                //设置返回数据
                startActivityForResult(intent, 1)
            } else {
                mBtnSubmit!!.isEnabled = true
            }
        }
    }

    /**
     * 联网授权
     */
    private fun netWorkWarranty() {
/*
        val uuid = ConUtil.getUUIDString(this)

        Thread(Runnable {
            val manager = Manager(this@PeopleCheckActivity)
            val licenseManager = LivenessLicenseManager(this@PeopleCheckActivity)
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
