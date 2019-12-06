package com.lairui.easy.ui.module1.activity

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
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.KindSelectDialog
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.utils.tool.SPUtils.get
import com.lairui.easy.utils.tool.UtilTools.Companion.empty
import java.util.*

/**
 * 通知详情 界面
 */
class NoticeDetialActivity : BasicActivity(), RequestView {
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
    @BindView(R.id.tvTitle)
    lateinit var tvTitle: TextView
    @BindView(R.id.tvTime)
    lateinit var tvTime: TextView
    @BindView(R.id.tvContent)
    lateinit var tvContent: TextView
    private var mRequestTag = ""
    private val mDialog: KindSelectDialog? = null
    private var mapData: Map<String, Any>? = null
    override val contentView: Int
        get() = R.layout.activity_notice_detials

    override fun init() { //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        val intent = intent
        if (intent != null) {
            val bundle = intent.extras
            if (bundle != null) {
                mapData = bundle.getSerializable("DATA") as Map<String, Any>
            }
        }
        mTitleText!!.text = "公告详情"
        mTitleText!!.setCompoundDrawables(null, null, null, null)
        divideLine!!.visibility = View.GONE
        traderListItemAction()
    }

    //获取公告列表详情
    private fun traderListItemAction() {
       /* mRequestTag = MethodUrl.NOTICE_LIST_ITEM
        val map: MutableMap<String, Any> = HashMap()
        if (empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = get(this@NoticeDetialActivity, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString()
        }
        map["token"] = MbsConstans.ACCESS_TOKEN
        map["id"] = mapData!!["id"].toString() + ""
        val mHeaderMap: Map<String, String> = HashMap()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.NOTICE_LIST_ITEM, map)*/
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
          /*  MethodUrl.NOTICE_LIST_ITEM -> when (tData["code"].toString() + "") {
                "0" -> {
                    val map = tData["data"] as Map<String, Any>?
                    if (!empty(map)) {
                        tvTitle!!.text = map!!["title"].toString() + ""
                        tvTime!!.text = map["time"].toString() + ""
                        tvContent!!.movementMethod = LinkMovementMethod.getInstance()
                        tvContent!!.text = Html.fromHtml(map["content"].toString() + "")
                    }
                }
                "-1" -> showToastMsg(tData["msg"].toString() + "")
                "1" -> {
                    closeAllActivity()
                    val intent = Intent(this@NoticeDetialActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }*/
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