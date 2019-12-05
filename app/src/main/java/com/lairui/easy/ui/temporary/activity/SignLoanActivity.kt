package com.lairui.easy.ui.temporary.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message

import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.SignHeTongAdapter
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil


import java.util.ArrayList
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 合同签署界面
 */
class SignLoanActivity : BasicActivity(), RequestView, ReLoadingData {
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.tv_loaner)
    lateinit var mLoanerText: TextView
    @BindView(R.id.tv_jiekuan_timelitmit)
    lateinit var mTimeLitmitText: TextView
    @BindView(R.id.tv_shouxin_money)
    lateinit var mShouxinMoneyText: TextView
    @BindView(R.id.tv_rate_year)
    lateinit var mRateYearText: TextView
    @BindView(R.id.tv_shouxin_date)
    lateinit var mShouxinDate: TextView
    @BindView(R.id.rcv_conlist)
    lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.bt_read_agree)
    lateinit var mButton: Button
    @BindView(R.id.lilv_type_tv)
    lateinit var mLilvTypeTv: TextView
    @BindView(R.id.huankuan_type_tv)
    lateinit var mHuankuanTypeTv: TextView
    @BindView(R.id.danbao_type_tv)
    lateinit var mDanbaoTypeTv: TextView

    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView

    private var mRequestTag = ""

    private lateinit var mDataMap: MutableMap<String, Any>
    private lateinit var mDefaultMap: MutableMap<String, Any>


    private lateinit var mSignHeTongAdapter: SignHeTongAdapter
    private val mHeTongList = ArrayList<MutableMap<String, Any>>()
    private var mStatus: String? = ""

    override val contentView: Int
        get() = R.layout.activity_sign_loan


    private var isCheck = false


    @SuppressLint("HandlerLeak")
     var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            when (msg.what) {
                1 -> enterNextPage()
                2 -> {
                    showToastMsg("人脸验证授权失败")
                    mButton!!.isEnabled = true
                }
            }
        }
    }

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.hetong_sign)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mDataMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
            mStatus = bundle.getString("status")
        }

        LogUtil.i("打印日志", mDataMap.toString() + "    mstatus" + mStatus)
        if (mStatus == "0") {//未签署
            //tvStatus = "去签署";
            signInfoAction()
            mButton!!.visibility = View.VISIBLE

        } else if (mStatus == "1") {//已签署
            //tvStatus = "处理中";
            hasSignInfoAction()
            mButton!!.visibility = View.GONE
        }

        mContent?.let { mPageView!!.setContentView(it) }
        //mPageView.showEmpty();
        mPageView!!.showLoading()
        mPageView!!.reLoadingData = this

        mRecyclerView!!.isNestedScrollingEnabled = false
    }

    /**
     * 去签署 要获取的信息
     */
    private fun signInfoAction() {

        mRequestTag = MethodUrl.signDetail
        val map = HashMap<String, String>()
        map["flowdate"] = mDataMap!!["flowdate"]!!.toString() + ""//业务日期
        map["flowid"] = mDataMap!!["flowid"]!!.toString() + ""//业务流水
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.signDetail, map)
    }

    /**
     * 已经签署  要获取的信息
     */
    private fun hasSignInfoAction() {

        mRequestTag = MethodUrl.shouxinDetail
        val map = HashMap<String, String>()
        map["creditfile"] = mDataMap!!["creditfile"]!!.toString() + ""//授信合同编号
        map["flowdate"] = mDataMap!!["flowdate"]!!.toString() + ""//业务日期
        map["flowid"] = mDataMap!!["flowid"]!!.toString() + ""//业务流水
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.shouxinDetail, map)
    }

    /**
     * 签署提交的信息
     */
    private fun submitInfoAction() {

        mRequestTag = MethodUrl.signSubmit
        val map = HashMap<String, Any>()
        map["flowdate"] = mDataMap!!["flowdate"]!!.toString() + ""//业务日期
        map["flowid"] = mDataMap!!["flowid"]!!.toString() + ""//业务流水
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.signSubmit, map)
    }

    /**
     * 未签署合同详情信息
     */
    private fun initDefaultValue() {
        if (mDefaultMap == null || mDefaultMap!!.isEmpty()) {
            return
        }
        val danwei = mDefaultMap!!["singleunit"]!!.toString() + ""
        //Map<String, Object> qixianDW = SelectDataUtil.getMap(danwei, SelectDataUtil.getQixianDw());
        val qixianDW = SelectDataUtil.getMap(danwei, SelectDataUtil.getNameCodeByType("limitUnit"))

        mLoanerText!!.text = mDefaultMap!!["zifangnme"]!!.toString() + ""//出借人
        mTimeLitmitText!!.text = mDefaultMap!!["singlelimit"].toString() + "" + qixianDW["name"]//借款期限
        mShouxinMoneyText!!.text = UtilTools.getRMBMoney(mDefaultMap!!["creditmoney"]!!.toString() + "")//授信额度
        mRateYearText!!.text = UtilTools.getlilv(mDefaultMap!!["daiklilv"]!!.toString() + "")//年化利率

        mLilvTypeTv!!.text = mDefaultMap!!["lvtypenm"]!!.toString() + ""//利率方式
        mHuankuanTypeTv!!.text = mDefaultMap!!["hktypenm"]!!.toString() + ""//还款方式
        //Map<String, Object> danbaoMap = SelectDataUtil.getMap(mDefaultMap.get("assutype")+"", SelectDataUtil.getDanbaoType());
        val danbaoMap = SelectDataUtil.getMap(mDefaultMap!!["assutype"]!!.toString() + "", SelectDataUtil.getNameCodeByType("assuType"))
        mDanbaoTypeTv!!.text = danbaoMap["name"]!!.toString() + ""//担保类型

        val showDate = UtilTools.getStringFromSting2(mDefaultMap!!["enddate"]!!.toString() + "", "yyyyMMdd", "yyyy-MM-dd")
        mShouxinDate!!.text = showDate//授信截止日

        val hLists = mDefaultMap!!["contList"] as List<MutableMap<String, Any>>?
        if (hLists != null) {
            mHeTongList.clear()
            mHeTongList.addAll(hLists)
        }
        if (mSignHeTongAdapter == null) {
            val manager = LinearLayoutManager(this)
            manager.orientation = RecyclerView.VERTICAL
            mRecyclerView!!.layoutManager = manager
            mSignHeTongAdapter = SignHeTongAdapter(this, mHeTongList)
            mRecyclerView!!.adapter = mSignHeTongAdapter

            mSignHeTongAdapter!!.onMyItemClickListener = object : OnMyItemClickListener {
                override fun OnMyItemClickListener(view: View, position: Int) {
                    val map = mSignHeTongAdapter!!.datas?.get(position)
                    val intent = Intent(this@SignLoanActivity, PDFLookActivity::class.java)
                    intent.putExtra("id", map?.get("pdfurl")!!.toString() + "")
                    startActivity(intent)
                }
            }

        } else {
            mSignHeTongAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * 已经签署合同详情信息
     */
    private fun initHasDefaultValue() {
        if (mDefaultMap == null || mDefaultMap!!.isEmpty()) {
            return
        }

        val danwei = mDefaultMap!!["singleunit"]!!.toString() + ""
        //Map<String, Object> qixianDW = SelectDataUtil.getMap(danwei, SelectDataUtil.getQixianDw());
        val qixianDW = SelectDataUtil.getMap(danwei, SelectDataUtil.getNameCodeByType("limitUnit"))

        mLoanerText!!.text = mDefaultMap!!["zifangnme"]!!.toString() + ""//出借人
        mTimeLitmitText!!.text = mDefaultMap!!["singlelimit"].toString() + "" + qixianDW["name"]//借款期限 singleunit
        mShouxinMoneyText!!.text = UtilTools.getRMBMoney(mDefaultMap!!["creditmoney"]!!.toString() + "")//授信额度
        mRateYearText!!.text = UtilTools.getlilv(mDefaultMap!!["daiklilv"]!!.toString() + "")//年化利率
        mShouxinDate!!.text = UtilTools.getStringFromSting2(mDefaultMap!!["enddate"]!!.toString() + "", "yyyyMMdd", "yyyy年MM月dd日")//授信截止日


        mLilvTypeTv!!.text = mDefaultMap!!["lvtypenm"]!!.toString() + ""//利率方式
        mHuankuanTypeTv!!.text = mDefaultMap!!["hktypenm"]!!.toString() + ""//还款方式
        //Map<String, Object> danbaoMap = SelectDataUtil.getMap(mDefaultMap.get("assutype")+"", SelectDataUtil.getDanbaoType());
        val danbaoMap = SelectDataUtil.getMap(mDefaultMap!!["assutype"]!!.toString() + "", SelectDataUtil.getNameCodeByType("assuType"))

        mDanbaoTypeTv!!.text = danbaoMap["name"]!!.toString() + ""//担保类型


        val hLists = mDefaultMap!!["contList"] as List<MutableMap<String, Any>>?
        if (hLists != null) {
            mHeTongList.clear()
            mHeTongList.addAll(hLists)
        }
        if (mSignHeTongAdapter == null) {
            val manager = LinearLayoutManager(this)
            manager.orientation = RecyclerView.VERTICAL
            mRecyclerView!!.layoutManager = manager
            mSignHeTongAdapter = SignHeTongAdapter(this, mHeTongList)
            mRecyclerView!!.adapter = mSignHeTongAdapter

            mSignHeTongAdapter!!.onMyItemClickListener = object : OnMyItemClickListener {
                override fun OnMyItemClickListener(view: View, position: Int) {
                    val map = mSignHeTongAdapter!!.datas?.get(position)
                    val intent = Intent(this@SignLoanActivity, PDFLookActivity::class.java)
                    intent.putExtra("id", map?.get("pdfurl")!!.toString() + "")
                    startActivity(intent)
                }
            }

        } else {
            mSignHeTongAdapter!!.notifyDataSetChanged()
        }

    }

    @OnClick(R.id.back_img, R.id.bt_read_agree, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.bt_read_agree -> {
                mButton!!.isEnabled = false

                PermissionsUtils.requsetRunPermission(this@SignLoanActivity, object : RePermissionResultBack {
                    override fun requestSuccess() {
                        netWorkWarranty()
                    }

                    override fun requestFailer() {
                        toast(R.string.failure)
                        mButton!!.isEnabled = true
                    }
                }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)
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
            MethodUrl.shouxinDetail -> {

                mDefaultMap = tData
                initHasDefaultValue()
                mPageView!!.showContent()
            }
            MethodUrl.signDetail//
            -> {
                mDefaultMap = tData
                initDefaultValue()
                mPageView!!.showContent()
            }
            MethodUrl.signSubmit -> {
                mButton!!.isEnabled = true
                val intent = Intent()
                intent.action = MbsConstans.BroadcastReceiverAction.DAIBAN_INFO_UPDATE
                sendBroadcast(intent)
                finish()
                showToastMsg("签署成功")
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.signDetail -> signInfoAction()
                    MethodUrl.signSubmit -> submitInfoAction()
                    MethodUrl.shouxinDetail -> hasSignInfoAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.signDetail -> mPageView!!.showNetworkError()
            MethodUrl.shouxinDetail -> mPageView!!.showNetworkError()
        }
        mButton!!.isEnabled = true
        dealFailInfo(map, mType)
    }

    private fun enterNextPage() {
        //startActivityForResult(Intent(this, LivenessActivity::class.java), PAGE_INTO_LIVENESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data)
        var intent: Intent
        val bundle: Bundle?
        if (requestCode == 1) {
            when (resultCode) {
                //通过短信验证码
                MbsConstans.FaceType.FACE_SIGN_HETONG -> {
                    bundle = data!!.extras
                    if (bundle == null) {
                        isCheck = false
                        mButton!!.isEnabled = true
                    } else {
                        mButton!!.isEnabled = false
                        isCheck = true
                        submitInfoAction()
                    }
                }
                else -> mButton!!.isEnabled = true
            }

        } else if (requestCode == PAGE_INTO_LIVENESS) {//人脸识别返回来的数据
            if (resultCode == Activity.RESULT_OK) {
                bundle = data!!.extras
                bundle!!.putInt(MbsConstans.FaceType.FACE_KEY, MbsConstans.FaceType.FACE_SIGN_HETONG)
                intent = Intent(this@SignLoanActivity, ResultActivity::class.java)
                intent.putExtras(bundle)
                //设置返回数据
                startActivityForResult(intent, 1)
            } else {
                mButton!!.isEnabled = true
            }
        }
    }

    /**
     * 联网授权
     */
    private fun netWorkWarranty() {

      /*  val uuid = ConUtil.getUUIDString(this)

        Thread(Runnable {
            val manager = Manager(this@SignLoanActivity)
            val licenseManager = LivenessLicenseManager(this@SignLoanActivity)
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

    override fun reLoadingData() {
        if (mStatus == "0") {//未签署
            //tvStatus = "去签署";
            signInfoAction()
            mButton!!.visibility = View.VISIBLE
        } else if (mStatus == "1") {//已签署
            //tvStatus = "处理中";
            hasSignInfoAction()
            mButton!!.visibility = View.GONE
        }
        mPageView!!.showLoading()
    }

    companion object {
        private val PAGE_INTO_LIVENESS = 101
    }
}