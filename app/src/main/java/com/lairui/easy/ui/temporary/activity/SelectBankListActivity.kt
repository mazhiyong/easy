package com.lairui.easy.ui.temporary.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message

import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.SelectBankAdapter
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.OnMyItemClickListener
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil


import java.util.ArrayList
import java.util.HashMap

import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick

/**
 * 选择要绑定的银行卡的界面   界面
 */
class SelectBankListActivity : BasicActivity(), RequestView, ReLoadingData {

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
    @BindView(R.id.right_text_tv)
    lateinit var mRightTextTv: TextView
    @BindView(R.id.top_layout)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.right_lay)
    lateinit var mRightLay: LinearLayout
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.but_next)
    lateinit var mButNext: Button


    private var mRequestTag = ""

    private lateinit var mSelectBankAdapter: SelectBankAdapter
    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private var mDataList: MutableList<MutableMap<String, Any>>? = ArrayList()
    private var mPage = 1

    private lateinit var mBooleanList: MutableList<Boolean>

    private var mPatncode: String? = ""

    private var mViewType: String? = ""

    private lateinit var mSelectBankMap: MutableMap<String, Any>


    override val contentView: Int
        get() = R.layout.activity_select_bank_list
    @SuppressLint("HandlerLeak")
    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            when (msg.what) {
                1 -> enterNextPage()
                2 -> {
                    showToastMsg("人脸验证授权失败")
                    mButNext!!.isEnabled = true
                }
            }
        }
    }

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText!!.text = resources.getString(R.string.chose_bank)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mViewType = bundle.getString("TYPE")
            if (mViewType == "1") {//从  我的---银行卡  进来的
                mPatncode = bundle.getString("patncode")
            } else {
                mPatncode = bundle.getString("patncode")
                mDataList = bundle.run { getSerializable("DATA") } as MutableList<MutableMap<String, Any>>
                if (mDataList == null) {
                    mDataList = ArrayList()
                }
            }
        }
        mRightImg!!.visibility = View.VISIBLE
        mRightImg!!.setImageResource(R.drawable.shuaixuan)
        mRightTextTv!!.visibility = View.VISIBLE
        mRightLay!!.visibility = View.GONE

        mButNext!!.isEnabled = false

        initView()
    }


    private fun initView() {
        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.showLoading()
        mPageView!!.reLoadingData = this
        val manager = LinearLayoutManager(this@SelectBankListActivity)
        manager.orientation = RecyclerView.VERTICAL
        mRefreshListView!!.layoutManager = manager

        mRefreshListView!!.setOnRefreshListener {
            mPage = 1
            erLeiHuList()
        }

        mRefreshListView!!.setOnLoadMoreListener { mPage++ }

        showProgressDialog()
        erLeiHuList()
    }


    //二类户查询列表
    private fun erLeiHuList() {

        mRequestTag = MethodUrl.erleiHuList
        val map = HashMap<String, String>()
        map["patncode"] = mPatncode!! + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.erleiHuList, map)
    }

    //二类户绑定
    private fun erLeiHuBind() {
        showProgress()

        mRequestTag = MethodUrl.erleiHuBind
        val map = HashMap<String, Any>()
        map["patncode"] = mPatncode!! + ""
        map["crdno"] = mSelectBankMap!!["crdno"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.erleiHuBind, map)
    }


    private fun responseData() {

        mBooleanList = ArrayList()
        for (i in mDataList!!.indices) {
            mBooleanList!!.add(false)
        }
        if (mSelectBankAdapter == null) {
            mSelectBankAdapter = SelectBankAdapter(this@SelectBankListActivity)
            mSelectBankAdapter!!.booleanList = mBooleanList
            mSelectBankAdapter!!.addAll(mDataList!!)

            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(.5f));*/

            mLRecyclerViewAdapter = LRecyclerViewAdapter(mSelectBankAdapter)


            mRefreshListView!!.adapter = mLRecyclerViewAdapter
            mRefreshListView!!.itemAnimator = DefaultItemAnimator()
            mRefreshListView!!.setHasFixedSize(true)
            mRefreshListView!!.isNestedScrollingEnabled = false

            mRefreshListView!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            mRefreshListView!!.setPullRefreshEnabled(true)
            mRefreshListView!!.setLoadMoreEnabled(false)


            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position ->
                val item = mSelectBankAdapter!!.dataList[position]
                /* Intent intent = new Intent(getActivity(), ShowDetailPictrue.class);
                    intent.putExtra("jsonData",item.get("url")+"");
                    startActivity(intent);*/
            }

            mSelectBankAdapter!!.onMyItemClickListener = object : OnMyItemClickListener {
                override fun OnMyItemClickListener(view: View, position: Int) {
                    val item = mSelectBankAdapter!!.dataList[position]

                    if (mSelectBankAdapter != null) {
                        val list = mSelectBankAdapter!!.booleanList
                        if (list != null && list.size > 0) {
                            var b = false
                            for (i in list.indices) {
                                b = list[i]
                                if (b) {
                                    mButNext!!.isEnabled = true
                                    break
                                }
                            }
                            if (b) {
                                mButNext!!.isEnabled = true
                            } else {
                                mButNext!!.isEnabled = false
                            }
                        } else {
                            mButNext!!.isEnabled = false
                        }
                    } else {
                        mButNext!!.isEnabled = false
                    }

                    mRefreshListView!!.post {
                        mSelectBankAdapter!!.notifyDataSetChanged()
                        mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
                    }
                }
            }


        } else {
            mSelectBankAdapter!!.clear()
            mSelectBankAdapter!!.addAll(mDataList!!)
            mSelectBankAdapter!!.booleanList = mBooleanList
            mSelectBankAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
        }

        mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        if (mDataList!!.size < 10) {
            mRefreshListView!!.setNoMore(true)
        } else {
            mRefreshListView!!.setNoMore(false)
        }

        mRefreshListView!!.refreshComplete(10)
        mSelectBankAdapter!!.notifyDataSetChanged()
        if (mSelectBankAdapter!!.dataList.size <= 0) {
            mPageView!!.showEmpty()
        } else {
            mPageView!!.showContent()
        }

    }


    @OnClick(R.id.back_img, R.id.right_lay, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.right_lay -> {
            }
            R.id.but_next -> {

                lateinit var bankMap: MutableMap<String, Any>
                if (mSelectBankAdapter != null) {
                    val list = mSelectBankAdapter!!.booleanList
                    if (list != null && list.size > 0) {
                        for (i in list.indices) {
                            val b = list[i]
                            if (b) {
                                bankMap = mSelectBankAdapter!!.dataList[i]
                                break
                            }
                        }
                        if (bankMap == null || bankMap.isEmpty()) {
                            showToastMsg("请选择银行卡")
                        } else {
                            mButNext!!.isEnabled = false
                            PermissionsUtils.requsetRunPermission(this@SelectBankListActivity, object : RePermissionResultBack {
                                override fun requestSuccess() {
                                    netWorkWarranty()
                                }

                                override fun requestFailer() {
                                    toast(R.string.failure)
                                    mButNext!!.isEnabled = true
                                }
                            }, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE, com.yanzhenjie.permission.runtime.Permission.Group.CAMERA)
                        }
                    } else {
                    }
                } else {
                    showToastMsg("暂无可用银行卡")
                }
            }
        }//butPressCheck();
    }

    private fun butPressCheck() {
        lateinit var bankMap: MutableMap<String, Any>
        if (mSelectBankAdapter != null) {
            val list = mSelectBankAdapter!!.booleanList
            if (list != null && list.size > 0) {
                for (i in list.indices) {
                    val b = list[i]
                    if (b) {
                        bankMap = mSelectBankAdapter!!.dataList[i]
                        break
                    }
                }
                if (bankMap == null || bankMap.isEmpty()) {
                    showToastMsg("请选择银行卡")
                } else {
                    mSelectBankMap = bankMap
                    showProgressDialog()
                    erLeiHuBind()
                }
            } else {

            }
        } else {
            showToastMsg("暂无可用银行卡")
        }
    }


    override fun showProgress() {
        //showProgressDialog();
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.erleiHuBind -> {
                showToastMsg(tData["result"]!!.toString() + "")
                if (mViewType == "1") {//从  我的---银行卡  进来的
                    backTo(BankCardActivity::class.java, true)
                } else {
                    val intent = Intent(this@SelectBankListActivity, BankBindSuccessActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            MethodUrl.erleiHuList//
            -> {
                val result = tData["result"]!!.toString() + ""
                if (UtilTools.empty(result)) {
                    val list = JSONUtil.instance.jsonToList(result)
                    responseData()

                } else {
                    val list = JSONUtil.instance.jsonToList(result)
                    if (list != null) {
                        mDataList!!.clear()
                        mDataList!!.addAll(list)
                    }
                    responseData()
                }
                mRefreshListView!!.refreshComplete(10)
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.erleiHuList -> erLeiHuList()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
        when (mType) {
            MethodUrl.erleiHuBind -> {
                mButNext!!.isEnabled = true
                finish()
            }
            MethodUrl.erleiHuList//
            -> if (mSelectBankAdapter != null) {
                if (mSelectBankAdapter!!.dataList.size <= 0) {
                    mPageView!!.showNetworkError()
                } else {
                    mPageView!!.showContent()
                }
                mRefreshListView!!.refreshComplete(10)
                mRefreshListView!!.setOnNetWorkErrorListener { erLeiHuList() }
            } else {
                mPageView!!.showNetworkError()
            }
        }

    }

    override fun reLoadingData() {
        showProgressDialog()
        erLeiHuList()
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
                //
                MbsConstans.FaceType.FACE_CHECK_BANK_BIND -> {

                    bundle = data!!.extras
                    if (bundle == null) {
                        mButNext!!.isEnabled = true
                    } else {
                        butPressCheck()
                    }
                }
                else -> mButNext!!.isEnabled = true
            }

        } else if (requestCode == PAGE_INTO_LIVENESS) {//人脸识别返回来的数据
            if (resultCode == Activity.RESULT_OK) {
                bundle = data!!.extras
                bundle!!.putInt(MbsConstans.FaceType.FACE_KEY, MbsConstans.FaceType.FACE_CHECK_BANK_BIND)
                intent = Intent(this@SelectBankListActivity, ResultActivity::class.java)
                intent.putExtras(bundle)
                //设置返回数据
                startActivityForResult(intent, 1)
            } else {
                mButNext!!.isEnabled = true
            }
        }
    }


    /**
     * 联网授权
     */
    private fun netWorkWarranty() {

       /* val uuid = ConUtil.getUUIDString(this)

        Thread(Runnable {
            val manager = Manager(this@SelectBankListActivity)
            val licenseManager = LivenessLicenseManager(this@SelectBankListActivity)
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


        /**
         * -----------------------------------------------------------人脸识别代码
         */
        private val PAGE_INTO_LIVENESS = 101
    }

}
