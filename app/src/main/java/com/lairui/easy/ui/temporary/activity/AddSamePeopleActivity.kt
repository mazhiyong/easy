package com.lairui.easy.ui.temporary.activity

import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView

import android.text.method.NumberKeyListener
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mywidget.dialog.MySelectDialog
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.tool.IdCardUtil
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 添加共同借款人   界面
 */
class AddSamePeopleActivity : BasicActivity(), RequestView, SelectBackListener {

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
    @BindView(R.id.name_tv)
    lateinit var mNameTv: TextView
    @BindView(R.id.guanxi_lay)
    lateinit var mGuanxiLay: CardView
    @BindView(R.id.other_line)
    lateinit var mOtherLine: View
    @BindView(R.id.other_valut_edit)
    lateinit var mOtherValutEdit: EditText
    @BindView(R.id.other_lay)
    lateinit var mOtherLay: CardView
    @BindView(R.id.idcard_valut_edit)
    lateinit var mIdcardValutEdit: EditText
    @BindView(R.id.really_name_value_edit)
    lateinit var mReallyNameValueEdit: EditText
    @BindView(R.id.but_next)
    lateinit var mButNext: Button


    private var mGxDialog: MySelectDialog? = null

    private var mGxMap: MutableMap<String, Any>? = null

    private var mRequestTag = ""

    private var mIdno = ""
    private var mName = ""

    private var mHezuoMap: MutableMap<String, Any>? = null
    internal var IDCARD = charArrayOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'x', 'X', 'y', 'Y')


    override val contentView: Int
        get() = R.layout.activity_add_sp

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mHezuoMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }

        mTitleText!!.text = resources.getString(R.string.add_people_title)

        //List<Map<String, Object>> list = SelectDataUtil.guanxiPeople();
        val list = SelectDataUtil.getNameCodeByType("unionRel")
        mGxDialog = MySelectDialog(this, true, list, "选择关系", 30)
        mGxDialog!!.selectBackListener = this

        mOtherLay!!.visibility = View.GONE
        mOtherLine!!.visibility = View.GONE


        mIdcardValutEdit!!.keyListener = object : NumberKeyListener() {
            override fun getAcceptedChars(): CharArray {
                return IDCARD
            }

            override fun getInputType(): Int {
                return android.text.InputType.TYPE_CLASS_PHONE
            }
        }

    }


    private fun submitAction() {

        mName = mReallyNameValueEdit!!.text.toString() + ""
        mIdno = mIdcardValutEdit!!.text.toString() + ""

        if (mGxMap == null || mGxMap!!.isEmpty()) {
            UtilTools.isEmpty(mNameTv!!, "共同借款人关系")
            showToastMsg("请选择共同借款人关系")
            return
        }

        val code = mGxMap!!["code"]!!.toString() + ""
        if (code == "3") {
            if (UtilTools.isEmpty(mOtherValutEdit!!, "其它")) {
                showToastMsg("其它不能为空")
                return
            }
        }


        if (UtilTools.isEmpty(mIdcardValutEdit!!, "身份证号")) {
            showToastMsg("身份证号不能为空")
            return
        }


        val idCardUtil = IdCardUtil(mIdno)
        val correct = idCardUtil.isCorrect
        val msg = idCardUtil.getErrMsg()
        if (0 == correct) {// 符合规范

        } else {
            showToastMsg(msg)
            return
        }


        if (UtilTools.isEmpty(mReallyNameValueEdit!!, "真实姓名")) {
            showToastMsg("真实姓名不能为空")
            return
        }



        mRequestTag = MethodUrl.addPeople
        val map = HashMap<String, Any>()


        LogUtil.i("添加共同借款人信息", map)

        map["name"] = mName
        map["idno"] = mIdno
        map["patncode"] = mHezuoMap!!["patncode"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.addPeople, map)
    }

    private fun check() {

    }


    @OnClick(R.id.back_img, R.id.guanxi_lay, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.guanxi_lay -> mGxDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.but_next -> submitAction()
        }
    }

    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            30 -> {
                mGxMap = map
                val code = mGxMap!!["code"].toString() + ""
                if (code == "3") {
                    mOtherLay!!.visibility = View.VISIBLE
                    mOtherLine!!.visibility = View.VISIBLE
                } else {
                    mOtherLay!!.visibility = View.GONE
                    mOtherLine!!.visibility = View.GONE
                }

                mNameTv!!.text = mGxMap!!["name"]!!.toString() + ""
                mNameTv!!.setError(null, null)
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
            MethodUrl.addPeople//{custid=null}
            -> {

                val intent = Intent()
                intent.putExtra("name", mReallyNameValueEdit!!.text.toString() + "")
                intent.putExtra("idno", mIdcardValutEdit!!.text.toString() + "")
                intent.putExtra("guanxi", mGxMap!!["code"]!!.toString() + "")
                intent.putExtra("custid", tData["custid"]!!.toString() + "")
                var otherStr = ""
                val code = mGxMap!!["code"]!!.toString() + ""
                if (code == "3") {
                    otherStr = mOtherValutEdit!!.text.toString() + ""
                }
                intent.putExtra("other", otherStr)
                setResult(Activity.RESULT_OK, intent)
                finish()
                showToastMsg("添加借款人成功")
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.addPeople -> submitAction()
                }
            }
        }

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }
}
