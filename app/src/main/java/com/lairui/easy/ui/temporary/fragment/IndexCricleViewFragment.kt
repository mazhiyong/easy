package com.lairui.easy.ui.temporary.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.cardview.widget.CardView
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*

import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.mywidget.dialog.TipMsgDialog
import com.lairui.easy.mywidget.view.CircleProgress
import com.lairui.easy.ui.temporary.activity.ApplyAmountActivity
import com.lairui.easy.ui.temporary.activity.BankOpenActivity
import com.lairui.easy.ui.temporary.activity.BankTiXianModifyActivity
import com.lairui.easy.ui.temporary.activity.BorrowDetailActivity
import com.lairui.easy.ui.temporary.activity.BorrowMoneyActivity
import com.lairui.easy.ui.temporary.activity.BorrowMoneySelectActivity
import com.lairui.easy.ui.temporary.activity.ChongZhiCardAddActivity
import com.lairui.easy.ui.temporary.activity.HuankuanActivity
import com.lairui.easy.ui.temporary.activity.IdCardSuccessActivity
import com.lairui.easy.ui.module.activity.MainActivity
import com.lairui.easy.ui.temporary.activity.PeopleCheckActivity
import com.lairui.easy.ui.temporary.activity.PerfectInfoActivity
import com.lairui.easy.ui.temporary.activity.SelectBankListActivity
import com.lairui.easy.ui.temporary.activity.SignLoanActivity
import com.lairui.easy.ui.temporary.activity.WaitDoWorkActivity
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicFragment
import com.lairui.easy.mywidget.dialog.HeZuoFangDialog
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.presenter.RequestPresenterImp
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.LoadingWindow
import com.lairui.easy.mywidget.view.RoundIndicatorView
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.ParseTextUtil
import com.lairui.easy.utils.tool.UtilTools
import com.lairui.easy.mywidget.view.PullScrollView
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.math.BigDecimal
import java.util.ArrayList
import java.util.HashMap

import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.mywidget.pulltozoomview.PullToZoomScrollViewEx

@SuppressLint("ValidFragment")
class IndexCricleViewFragment : BasicFragment(), RequestView, SelectBackListener {
    @BindView(R.id.person_scroll_view)
    lateinit var mPersonScrollView: PullToZoomScrollViewEx



    lateinit var headView: View
    lateinit var zoomView: View
    lateinit var contentView: View
    private var mRequestTag = ""


    private lateinit var mLoadingWindow: LoadingWindow

    override val layoutId: Int
        get() = R.layout.fragment_circle_view

    private lateinit var mDataList: MutableList<MutableMap<String, Any>>
    private lateinit var mZhangDialog: TipMsgDialog

    override fun onResume() {
        super.onResume()

    }

    override fun init() {
        initView()
        headView = LayoutInflater.from(activity).inflate(R.layout.profile_head_view, null, false)
        zoomView = LayoutInflater.from(activity).inflate(R.layout.profile_zoom_view, null, false)
        contentView = LayoutInflater.from(activity).inflate(R.layout.profile_content_view, null, false)


        mLoadingWindow = LoadingWindow(activity!!, R.style.Dialog)

        mLoadingWindow!!.setOnDismissListener {
            //refreshLayout.setRefreshCompleted()
            println("dialog消失了监听")
            //mBtnPlease.setEnabled(true);
        }
        mLoadingWindow.showView()

    }


    private fun initView() {
        setBarTextColor()

    }

    fun setBarTextColor() {
        StatusBarUtil.setLightMode(activity!!)
    }


    @OnClick(R.id.person_scroll_view)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.person_scroll_view -> {
            }

        }
    }

    override fun showProgress() {
        //mLoadingWindow.showView();
    }

    override fun disimissProgress() {
        // mLoadingWindow.cancleView();

    }



    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        mLoadingWindow!!.cancleView()

        /*if (mRequestTagList != null && mRequestTagList.contains(mType)){
            mRequestTagList.remove(mType);
        }*/
        when (mType) {

            MethodUrl.erleiHuList
            -> {

            }

        }
    }
        override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
            mLoadingWindow!!.cancleView()

            when (mType) {
                MethodUrl.erleiHuList//二类户列表
                -> {
                }
            }
            dealFailInfo(map, mType)
        }


    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        val intent: Intent
        when (type) {
            200 -> {

            }
            210 -> {

            }
        }
    }



}
