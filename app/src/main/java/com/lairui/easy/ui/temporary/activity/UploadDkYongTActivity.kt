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
import androidx.cardview.widget.CardView
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.MyClickableSpan
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
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig





import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 上传贷款用途 界面
 */
class UploadDkYongTActivity : BasicActivity(), RequestView {

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

    @BindView(R.id.has_fujian_content_lay)
    lateinit var mHasFujianContentLay: LinearLayout
    @BindView(R.id.has_fujian_lay)
    lateinit var mHasFujianLay: CardView
    @BindView(R.id.jiekuan_money_tv)
    lateinit var mJiekuanMoneyTv: TextView
    @BindView(R.id.chujieren_tv)
    lateinit var mChujierenTv: TextView
    @BindView(R.id.jiekuan_qixian_tv)
    lateinit var mJiekuanQixianTv: TextView
    @BindView(R.id.nian_lilv_tv)
    lateinit var mNianLilvTv: TextView
    @BindView(R.id.jiekuan_giveday_tv)
    lateinit var mJiekuanGivedayTv: TextView
    @BindView(R.id.has_upload_tv)
    lateinit var mHasUploadTv: TextView
    @BindView(R.id.add_file_tv)
    lateinit var mAddFileTv: TextView
    @BindView(R.id.file_num_tv)
    lateinit var mFileNumTv: TextView
    @BindView(R.id.fujian_lay)
    lateinit var mFujianLay: CardView


    @BindView(R.id.has_upload_tv2)
    lateinit var mHasUploadTv2: TextView
    @BindView(R.id.add_file_tv2)
    lateinit var mAddFileTv2: TextView
    @BindView(R.id.file_num_tv2)
    lateinit var mFileNumTv2: TextView

    @BindView(R.id.but_submit)
    lateinit var mSubmitBut: Button
    @BindView(R.id.bulu_content_lay)
    lateinit var mBuluContentLay: LinearLayout


    private lateinit var mDataMap: MutableMap<String, Any>
    private var mConfigMap: MutableMap<String, Any>? = null


    private var mRequestTag = ""

    private lateinit var mDefaultMap: MutableMap<String, Any>

    private var mSign = "1"//标记  请求的是详情  还是提交，，，因为两个请求的url是一样的，提交方式不一样，所以需要标记一下

    override val contentView: Int
        get() = R.layout.activity_upload_daikyongt


    private var isCheck = false


    @SuppressLint("HandlerLeak")
    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            when (msg.what) {
                1 -> enterNextPage()
                2 -> {
                    showToastMsg("人脸验证授权失败")
                    mSubmitBut!!.isEnabled = true
                }
            }
        }
    }


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
                        mDefaultMap!!["contList"] = list
                        mFileList.clear()
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
                            mFileList.add(resultMap)
                        }
                    }

                    if (num != 0) {
                        mAddFileTv.visibility = View.GONE
                        mHasUploadTv.visibility = View.VISIBLE
                        mFileNumTv.visibility = View.VISIBLE

                    } else {
                        mAddFileTv.visibility = View.VISIBLE
                        mHasUploadTv.visibility = View.GONE
                        mFileNumTv.visibility = View.GONE

                    }
                    mFileNum = num
                    mFileNumTv.text = num.toString() + "个"

                    LogUtil.i("show", "UploadDkYongTActivity########################$mFileList")
                }
            }
        }
    }

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mDataMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }
        mTitleText.text = resources.getString(R.string.upload_daikuan_yongt)

        val filter = IntentFilter()
        filter.addAction(MbsConstans.BroadcastReceiverAction.FILE_TIP_ACTION)
        registerReceiver(receiver, filter)

        mAddFileTv.visibility = View.VISIBLE
        mHasUploadTv.visibility = View.GONE
        mFileNumTv.visibility = View.GONE
        getModifyAction()

        mSubmitBut.isEnabled = false
    }


    /**
     * 获取借款信息  贷后详情
     */
    private fun getModifyAction() {
        mSign = "1"

        mRequestTag = MethodUrl.daihouDetail
        val map = HashMap<String, String>()
        map["loansqid"] = mDataMap["loansqid"]!!.toString() + ""//借款申请编号
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestGetToMap(mHeaderMap, MethodUrl.daihouDetail, map)
    }

    /**
     * 修改后  提交贷后  附件上传
     */
    private fun submitAction() {
        mSign = "2"

        mRequestTag = MethodUrl.daihouFujianSubmit
        val map = HashMap<String, Any>()
        map["loansqid"] = mDataMap!!["loansqid"]!!.toString() + ""
        map["contList"] = mFileList
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.daihouFujianSubmit, map)
    }


    private fun initModifyValue() {
        val status = mDefaultMap!!["austat"]!!.toString() + ""
        when (status) {
            "0" -> {
                mSubmitBut!!.text = "审核中"
                mSubmitBut!!.isEnabled = false
                mBuluContentLay!!.visibility = View.GONE
            }
            else -> {
                mBuluContentLay!!.visibility = View.VISIBLE
                initFujianView()
                mSubmitBut!!.text = "提交"
                mSubmitBut!!.isEnabled = true
            }
        }
        mJiekuanMoneyTv!!.text = UtilTools.getRMBMoney(mDefaultMap!!["loanmoney"]!!.toString() + "")//借款金额
        mChujierenTv!!.text = mDefaultMap!!["zifangnme"]!!.toString() + ""//出借人

        val dw = mDefaultMap!!["limitunit"]!!.toString() + ""
        //Map<String,Object> danweiMap = SelectDataUtil.getMap(dw,SelectDataUtil.getQixianDw());
        val danweiMap = SelectDataUtil.getMap(dw, SelectDataUtil.getNameCodeByType("limitUnit"))
        mJiekuanQixianTv!!.text = mDefaultMap!!["loanlimit"].toString() + "" + danweiMap["name"]//借款期限
        mNianLilvTv!!.text = UtilTools.getlilv(mDefaultMap!!["loanlilv"]!!.toString() + "")//年化利率

        var time = mDefaultMap!!["loandate"]!!.toString() + ""
        time = UtilTools.getStringFromSting2(time, "yyyyMMdd", "yyyy年MM月dd日")
        mJiekuanGivedayTv!!.text = time//借款发放日

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

        if (num == 0) {
            mHasFujianContentLay!!.visibility = View.GONE
        } else {
            mHasFujianContentLay!!.visibility = View.VISIBLE
        }

        //        if (num != 0){
        mAddFileTv2!!.visibility = View.GONE
        mHasUploadTv2!!.visibility = View.VISIBLE
        mFileNumTv2!!.visibility = View.VISIBLE

        //        }else {
        //            mAddFileTv2.setVisibility(View.VISIBLE);
        //            mHasUploadTv2.setVisibility(View.GONE);
        //            mFileNumTv2.setVisibility(View.GONE);
        //        }
        mFileNumTv2!!.text = num.toString() + "个"


    }

    private fun initFujianView() {
        val mList = mDefaultMap!!["contList"] as List<MutableMap<String, Any>>?
        if (mList!!.size == 0) {
            mBuluContentLay!!.visibility = View.GONE
        } else {
            mBuluContentLay!!.visibility = View.VISIBLE
        }
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
            MethodUrl.daihouDetail -> if (mSign == "1") {
                mDefaultMap = tData
                initModifyValue()
            } else if (mSign == "2") {
                showToastMsg("提交成功")
                finish()
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.daihouDetail -> if (mSign == "1") {
                        getModifyAction()
                    } else if (mSign == "2") {
                        submitAction()
                    }
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }


    @OnClick(R.id.back_img, R.id.fujian_lay, R.id.but_submit, R.id.has_fujian_lay, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.has_fujian_lay -> {
                val mHasFile = mDefaultMap!!["existFileList"] as List<MutableMap<String, Any>>?
                intent = Intent(this@UploadDkYongTActivity, ModifyFileActivity::class.java)
                //intent.putExtra("DATA",(Serializable) mHasFile);
                DataHolder.instance!!.save("fileList", mHasFile!!)
                startActivity(intent)
            }
            R.id.fujian_lay -> PermissionsUtils.requsetRunPermission(this@UploadDkYongTActivity, object : RePermissionResultBack {
                override fun requestSuccess() {

                    val intent = Intent(this@UploadDkYongTActivity, AddFileActivity::class.java)
                    intent.putExtra("DATA", mDefaultMap as Serializable?)
                    intent.putExtra("TYPE", "1")
                    startActivityForResult(intent, 300)
                }

                override fun requestFailer() {
                    toast(R.string.failure)
                }
            }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)
            R.id.but_submit -> {
                mSubmitBut!!.isEnabled = false
                if (mFileList == null || mFileList.size <= 0) {
                    showToastMsg("请上传附件后提交")
                    mSubmitBut!!.isEnabled = true
                } else {
                    var num = 0
                    for (fileMap in mFileList) {
                        val files = fileMap["files"] as List<MutableMap<String, Any>>?
                        if (files != null) {
                            num = num + files.size
                        }
                    }
                    if (num > 0) {

                        PermissionsUtils.requsetRunPermission(this@UploadDkYongTActivity, object : RePermissionResultBack {
                            override fun requestSuccess() {
                                netWorkWarranty()
                            }

                            override fun requestFailer() {
                                toast(R.string.failure)
                            }
                        }, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)
                    } else {
                        showToastMsg("请上传附件后提交")
                        mSubmitBut!!.isEnabled = true
                    }
                }
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
            val manager = Manager(this@UploadDkYongTActivity)
            val licenseManager = LivenessLicenseManager(this@UploadDkYongTActivity)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST ->{
                    // 图片、视频、音频选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                }
                400 -> {

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

        var intent: Intent
        val bundle: Bundle?
        if (requestCode == 1) {
            when (resultCode) {
                //通过短信验证码
                MbsConstans.FaceType.FACE_UPLOAD_USE -> {
                    bundle = data!!.extras
                    if (bundle == null) {
                        isCheck = false
                        mSubmitBut.isEnabled = true
                    } else {
                        mSubmitBut.isEnabled = false
                        isCheck = true
                        submitAction()
                    }
                }
                else -> mSubmitBut.isEnabled = true
            }

        } else if (requestCode == PAGE_INTO_LIVENESS) {//人脸识别返回来的数据
            if (resultCode == Activity.RESULT_OK) {
                bundle = data!!.extras
                bundle!!.putInt(MbsConstans.FaceType.FACE_KEY, MbsConstans.FaceType.FACE_UPLOAD_USE)
                intent = Intent(this@UploadDkYongTActivity, ResultActivity::class.java)
                intent.putExtras(bundle)
                //设置返回数据
                startActivityForResult(intent, 1)
            } else {
                mSubmitBut.isEnabled = true
            }
        }

    }


    private fun initXieyiView() {
        var xiyiStr = "已阅读并同意签署"
        if (mConfigMap != null) {
            val xieyiList = mConfigMap!!["signConts"] as List<MutableMap<String, Any>>?
            for (map in xieyiList!!) {
                val str = map["pdfname"]!!.toString() + ""
                xiyiStr = "$xiyiStr《$str》、"
            }
            val sp = SpannableString(xiyiStr)

            for (map in xieyiList) {
                val str = map["pdfname"]!!.toString() + ""
                setClickableSpan(sp, View.OnClickListener {
                    /*Intent intent = new Intent(ApplyAmountActivity.this,PDFLookActivity.class);
                        startActivity(intent);*/
                }, xiyiStr, "《$str》")
            }
            //mXieyiTv.setText(sp);
        }
        //添加点击事件时，必须设置
        //mXieyiTv.setMovementMethod(LinkMovementMethod.getmContext());
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


    protected fun toast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val PAGE_INTO_LIVENESS = 101
    }
}
