package com.lairui.easy.ui.module.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick
import com.jaeger.library.StatusBarUtil
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.KindSelectDialog
import com.lairui.easy.utils.tool.UtilTools
import kotlinx.android.synthetic.main.activity_regist.*
import java.util.HashMap

/**
 * 协议 界面
 */
class XieYiDetialActivity : BasicActivity(), RequestView {
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.divide_line)
    lateinit var divideLine: View

    @BindView(R.id.tvContent)
    lateinit var tvContent: TextView
    private var mRequestTag = ""
    private val mDialog: KindSelectDialog? = null
    private var typeData: String? = null
    override val contentView: Int
        get() = R.layout.activity_xieyi_detials

    override fun init() { //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        val intent = intent
        if (intent != null) {
            val bundle = intent.extras
            if (bundle != null) {
                typeData = bundle.getSerializable("TYPE") as String
                when(typeData){
                    "0" ->{
                        mTitleText.text = "注册协议"
                        getRegistInfoAction()
                    }
                    "1" ->{
                        mTitleText.text = "合格投资人申明"
                        getRisktInfoAction()
                    }
                    "2" ->{
                        mTitleText.text = "风险揭示书"
                        getStatementInfoAction()
                    }
                    "3" ->{
                        mTitleText.text = "策略协议"
                        getCelueInfoAction()
                    }
                }
            }
        }
        divideLine.visibility = View.GONE

    }

    //获取注册协议
    private fun getRegistInfoAction() {
        mRequestTag = MethodUrl.RIGIST_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.RIGIST_INFO
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.RIGIST_INFO, map)
    }

    private fun getRisktInfoAction() {
        mRequestTag = MethodUrl.RISK_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.RISK_INFO
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.RISK_INFO, map)
    }

    private fun getStatementInfoAction() {
        mRequestTag = MethodUrl.STATEMENT_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.STATEMENT_INFO
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.STATEMENT_INFO, map)
    }

    private fun getCelueInfoAction() {
        mRequestTag = MethodUrl.PEIZI_INFO
        val map = HashMap<String, Any>()
        map["nozzle"] = MethodUrl.PEIZI_INFO
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.PEIZI_INFO, map)
    }




    @OnClick(R.id.back_img, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent? = null
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
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
            MethodUrl.RIGIST_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    val map = tData["data"] as String
                    if (!UtilTools.empty(map)) {
                        tvContent.movementMethod = LinkMovementMethod.getInstance()
                        tvContent.text = Html.fromHtml(map)
                    }
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@XieYiDetialActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.RISK_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    val map = tData["data"] as String
                    if (!UtilTools.empty(map)) {
                        tvContent.movementMethod = LinkMovementMethod.getInstance()
                        tvContent.text = Html.fromHtml(map)
                    }
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@XieYiDetialActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            MethodUrl.STATEMENT_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    val map = tData["data"] as String
                    if (!UtilTools.empty(map)) {
                        tvContent.movementMethod = LinkMovementMethod.getInstance()
                        tvContent.text = Html.fromHtml(map)
                    }
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@XieYiDetialActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            MethodUrl.PEIZI_INFO -> when (tData["code"].toString() + "") {
                "1" -> {
                    val map = tData["data"] as String
                    if (!UtilTools.empty(map)) {
                        tvContent.movementMethod = LinkMovementMethod.getInstance()
                        tvContent.text = Html.fromHtml(map)
                    }
                }
                "0" -> showToastMsg(tData["msg"].toString() + "")
                "-1" -> {
                    closeAllActivity()
                    val intent = Intent(this@XieYiDetialActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }
    /**---------------------------------------------------------------------以下代码申请权限---------------------------------------------
     * Request permissions.
     */
    /**
     * activity销毁前触发的方法
     */
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
    }


}