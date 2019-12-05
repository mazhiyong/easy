package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.db.FaPiaoData
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.DateSelectDialog
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick

/**
 * 添加发票信息
 */
class InvoiceAddActivity : BasicActivity(), RequestView, SelectBackListener {

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
    @BindView(R.id.code_edit)
    lateinit var mCodeEdit: EditText
    @BindView(R.id.number_edit)
    lateinit var mNumberEdit: EditText
    @BindView(R.id.date_tv)
    lateinit var mDateTv: TextView
    @BindView(R.id.textcode_edit)
    lateinit var mTextcodeEdit: EditText
    @BindView(R.id.money_edit)
    lateinit var mMoneyEdit: EditText


    // private Map<String, Object> mMoneyMap;
    private lateinit var mHezuoMap: MutableMap<String, Any>
    private lateinit var mConfigMap: MutableMap<String, Any>
    private lateinit var mQixianMap: MutableMap<String, Any>


    private lateinit var mParamMap: MutableMap<String, Any>
    private val mType = ""

    private val mPeopleList = ArrayList<MutableMap<String, Any>>()


    private var mRequestTag = ""


    private lateinit var mySelectDialog: DateSelectDialog

    override val contentView: Int
        get() = R.layout.activity_invoice_add

    var mSelectStartTime = ""

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.invoice_check)


        mySelectDialog = DateSelectDialog(this, true, "选择日期", 21)
        mySelectDialog!!.selectBackListener = this
    }


    /**
     */
    private fun getConfigAction() {

        mRequestTag = MethodUrl.jiekuanConfig
        val map = HashMap<String, String>()
        map["patncode"] = mHezuoMap!!["patncode"]!!.toString() + ""
        map["zifangbho"] = mHezuoMap["zifangbho"]!!.toString() + ""
        map["creditno"] = mHezuoMap["creditno"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.jiekuanConfig, map)
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
            MethodUrl.jiekuanHetong -> {
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
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.jiekuanConfig -> getConfigAction()
                    MethodUrl.jiekuanHetong -> {
                    }
                    MethodUrl.jiekuanSubmit -> {
                    }
                }
            }
        }
    }


    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.jiekuanHetong -> {
            }
            MethodUrl.jiekuanSubmit -> {
            }
            MethodUrl.jiekuanConfig -> finish()
        }

        dealFailInfo(map, mType)
    }

    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        val value = map["name"]!!.toString() + ""
        when (type) {
            21 -> {
                mSelectStartTime = map["date"]!!.toString() + ""
                val startShow = UtilTools.getStringFromSting2(mSelectStartTime, "yyyyMMdd", "yyyy-MM-dd")
                mDateTv!!.text = startShow
            }
            10 -> {
            }
        }
    }

    @OnClick(R.id.back_img, R.id.left_back_lay, R.id.gmf_lay, R.id.but_submit)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.gmf_lay -> showDateDialog()
            R.id.but_submit -> insertData()
        }
    }

    private fun showDateDialog() {
        mySelectDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
    }

    private fun insertData() {
        if (UtilTools.isEmpty(mCodeEdit!!, getString(R.string.invoice_daima_hint))) {
            //showToastMsg("发票代码不能为空");
            return
        }
        if (UtilTools.isEmpty(mNumberEdit!!, getString(R.string.invoice_haoma_hint))) {
            //showToastMsg("发票号码不能为空");
            return
        }

        if (UtilTools.empty(mDateTv!!.text.toString().trim { it <= ' ' })) {
            showToastMsg(getString(R.string.invoice_date_hint))
            return
        }


        if (UtilTools.isEmpty(mTextcodeEdit!!, getString(R.string.invoice_check_6))) {
            //showToastMsg("发票校验码不能为空");
            return
        }
        if (UtilTools.isEmpty(mMoneyEdit!!, getString(R.string.invoice_money_hint))) {
            //showToastMsg("发票金额不能为空");
            return
        }


        if (mCodeEdit!!.text.toString().length != 10 && mCodeEdit!!.text.toString().length != 12) {
            showToastMsg("当前发票代码格式不正确，请查验!")
            return
        }

        if (mNumberEdit!!.text.toString().length != 8) {
            showToastMsg("当前发票号码格式不正确，请查验!")
            return
        }

        if (mTextcodeEdit!!.text.toString().length != 6) {
            showToastMsg("当前发票校验码格式不正确，请查验!")
            return
        }


        //将发票信息录入数据库
        val fp_code = mCodeEdit!!.text.toString()
        val fp_number = mNumberEdit!!.text.toString()
        val fp_money = mMoneyEdit!!.text.toString()
        val fp_date = mDateTv!!.text.toString()


        if (FaPiaoData.instance.dataExist(fp_code, fp_number)) {
            showToastMsg("当前发票信息已存在,请换一张试试")
        } else {
            showToastMsg("发票信息录入成功")
            FaPiaoData.instance.insertDB(fp_code, fp_number, fp_money, fp_date)
            val intent = Intent(this, InvoiceListActivity::class.java)
            startActivity(intent)
            finish()
        }


    }


    override fun finish() {
        super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
