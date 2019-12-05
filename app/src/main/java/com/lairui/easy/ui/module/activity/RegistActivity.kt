package com.lairui.easy.ui.module.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick
import com.jaeger.library.StatusBarUtil
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.KindSelectDialog
import com.lairui.easy.ui.temporary.activity.CodeMsgActivity
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.TextViewUtils
import kotlinx.android.synthetic.main.activity_regist.*
import java.io.Serializable
import java.util.*

class RegistActivity : BasicActivity(), RequestView, SelectBackListener {


    @BindView(R.id.tv_zhuti)
    lateinit var mTvZhuti: TextView
    @BindView(R.id.zhuti_lay)
    lateinit var mZhutiLay: LinearLayout
    @BindView(R.id.et_code)
    lateinit var mEtCode: EditText
    @BindView(R.id.iv_code)
    lateinit var mIvCode: ImageView
    @BindView(R.id.bt_next)
    lateinit var mBtNext: Button
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.divide_line)
    lateinit var mDivideLine: View
    @BindView(R.id.arrow_view)
    lateinit var mArrowView: ImageView


    private lateinit var mDialog: KindSelectDialog
    private var mRequestTag = ""
    private var authcode = ""
    private var type = ""
    private var invcode = ""


    private lateinit var mKindMap: MutableMap<String, Any>

    private lateinit var mTimeCount:TimeCount


    override val contentView: Int
        get() = R.layout.activity_regist


    override fun init() {

        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTimeCount = TimeCount(1*60*1000,1000)
        mTitleText.text = ""
        mDivideLine.visibility = View.GONE

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            invcode = bundle.getString("result")!! + ""
        }


       // val mDataList = SelectDataUtil.getListByKeyList(SelectDataUtil.getNameCodeByType("firmKind"))

        val textViewUtils = TextViewUtils()
        val s = tipTv.text.toString()
        textViewUtils.init(s, tipTv)
        textViewUtils.setTextColor(s.indexOf("《"), s.length, ContextCompat.getColor(this, R.color.font_c))
        textViewUtils.setTextClick(s.indexOf("《注"), s.indexOf("议》"), object : TextViewUtils.ClickCallBack {
            override fun onClick() {
                Toast.makeText(this@RegistActivity, "注册协议", Toast.LENGTH_LONG).show()
            }

        })
        textViewUtils.setTextClick(s.indexOf("《合"), s.indexOf("明》"), object : TextViewUtils.ClickCallBack {
            override fun onClick() {
                Toast.makeText(this@RegistActivity, "合格投资人申明", Toast.LENGTH_LONG).show()
            }

        })
        textViewUtils.setTextClick(s.indexOf("《风"), s.indexOf("书》"), object : TextViewUtils.ClickCallBack {
            override fun onClick() {
                Toast.makeText(this@RegistActivity, "风险揭示书", Toast.LENGTH_LONG).show()
            }

        })
        textViewUtils.build()

        //获取临时Toten
        //getTempTokenAction()

    }


    /**
     * 获取全局字典配置信息
     */
    fun getNameCodeInfo() {
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.nameCode, map)
    }


    @OnClick( R.id.getCodeTv, R.id.bt_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.left_back_lay -> finish()
            R.id.getCodeTv -> {
                mTimeCount.start()
            }
            R.id.bt_next -> {
                mBtNext!!.isEnabled = false
                registAction()
            }
        }
    }

    private fun getTempTokenAction() {
        mRequestTag = MethodUrl.tempToken
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.tempToken, map)
    }


    private fun getImageCodeAction() {
        type = "0"
        mRequestTag = MethodUrl.imageCode
        val map = HashMap<String, String>()
        map["token"] = MbsConstans.TEMP_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.imageCode, map)
    }


    private fun registAction() {

        if (mKindMap == null || mKindMap.isEmpty()) {
            showToastMsg("请选择账号主体")
            mBtNext!!.isEnabled = true
            return
        }

        if (TextUtils.isEmpty(etPhone!!.text)) {
            showToastMsg("请编辑手机号码信息")
            mBtNext!!.isEnabled = true
            return
        }

        if (TextUtils.isEmpty(mEtCode!!.text)) {
            showToastMsg("请编辑验证码信息")
            mBtNext!!.isEnabled = true
            return
        }

        cheackImageCodeAction()
    }

    private fun cheackImageCodeAction() {
        type = "1"
        mRequestTag = MethodUrl.imageCode
        val map = HashMap<String, Any>()
        map["imgcode"] = mEtCode!!.text.toString()
        map["temptoken"] = MbsConstans.TEMP_TOKEN
        map["tel"] = etPhone!!.text.toString()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.imageCode, map)

    }


    /**
     * @descriptoin 请求前加载progress
     * @author dc
     * @date 2017/2/16 11:00
     */
    override fun showProgress() {
        showProgressDialog()
    }

    /**
     * @descriptoin 请求结束之后隐藏progress
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun disimissProgress() {
        dismissProgressDialog()
    }

    /**
     * @param tData 数据类型
     * @param mType
     * @descriptoin 请求数据成功
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        mBtNext!!.isEnabled = true
        val intent: Intent
        when (mType) {
            MethodUrl.nameCode -> {
                val result = tData["result"]!!.toString() + ""
                SPUtils.put(this@RegistActivity, MbsConstans.SharedInfoConstans.NAME_CODE_DATA, result)
                val mDataList = SelectDataUtil.getListByKeyList(SelectDataUtil.getNameCodeByType("firmKind"))
                if (mDataList == null || mDataList.size == 0) {
                    showToastMsg("暂无可选择的主体，请联系客服")
                } else {

                    if (mDataList.size == 1) {
                        mZhutiLay!!.isEnabled = false
                        mArrowView!!.visibility = View.GONE
                    } else {
                        mZhutiLay!!.isEnabled = true
                        mArrowView!!.visibility = View.VISIBLE
                        mDialog = KindSelectDialog(this, true, mDataList, 10)
                        mDialog!!.selectBackListener = this
                        mDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)

                    }
                }
            }
            MethodUrl.tempToken -> {
                MbsConstans.TEMP_TOKEN = tData["temp_token"]!!.toString() + ""
                getImageCodeAction()
            }
            MethodUrl.imageCode ->
                // 获取图片验证码
                if (tData.containsKey("img")) {//获取图片验证码
                    val bitmap = tData["img"] as Bitmap?
                    mIvCode!!.setImageBitmap(bitmap)
                } else {
                    authcode = tData["authcode"]!!.toString() + "" //验证图片验证码
                    getSmgCodeAction()
                }
            MethodUrl.regSms -> {
                showToastMsg(resources.getString(R.string.code_phone_tip))
                intent = Intent(this, CodeMsgActivity::class.java)
                intent.putExtra(MbsConstans.CodeType.CODE_KEY, MbsConstans.CodeType.CODE_PHONE_REGIST)
                intent.putExtra("phone", etPhone!!.text.toString())
                intent.putExtra("authcode", authcode)
                intent.putExtra("invcode", invcode)
                intent.putExtra("showPhone", etPhone!!.text.toString())
                intent.putExtra("DATA", tData as Serializable)
                intent.putExtra("kind", mKindMap!!["code"]!!.toString() + "")
                startActivity(intent)
            }
        }

    }

    private fun getSmgCodeAction() {
        mRequestTag = MethodUrl.regSms
        val map = HashMap<String, Any>()
        val mHeaderMap = HashMap<String, String>()
        map["tel"] = etPhone!!.text.toString()
        map["authcode"] = authcode
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.regSms, map)
    }


    /**
     * @param map
     * @param mType
     * @descriptoin 请求数据错误
     * @date 2017/2/16 11:01
     */
    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        mBtNext!!.isEnabled = true
        when (mType) {
            MethodUrl.imageCode -> if (type == "0") { //请求验证码失败
                mIvCode!!.setImageResource(R.drawable.default_pic)
            } else { //验证验证码失败
                getImageCodeAction()
            }
        }

        dealFailInfo(map, mType)
    }


    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {

    }


    // 倒计时内部类
   inner class TimeCount(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            getCodeTv.text = resources.getString(R.string.msg_code_again)
            getCodeTv.setTextColor(ContextCompat.getColor(this@RegistActivity,R.color.font_c))
            getCodeTv.isClickable = true
            MbsConstans.CURRENT_TIME = 0
        }

        override fun onTick(millisUntilFinished: Long) {
            //计时过程显示
            getCodeTv.isClickable = false
            getCodeTv.setTextColor(ContextCompat.getColor(this@RegistActivity,R.color.black99))
            getCodeTv.text = (millisUntilFinished / 1000 ).toString()+"秒后重发"
            MbsConstans.CURRENT_TIME = (millisUntilFinished / 1000).toInt()

        }

    }
}
