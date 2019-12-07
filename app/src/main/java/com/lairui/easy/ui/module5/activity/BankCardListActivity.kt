package com.lairui.easy.ui.module5.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.mywidget.dialog.SureOrNoDialog
import com.lairui.easy.ui.temporary.adapter.BankCardAdapter2
import com.lairui.easy.ui.temporary.adapter.BankCardChildAdapter
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module5.adapter.BankCardListAdapter

/**
 * 银行卡   界面
 */
class BankCardListActivity : BasicActivity(), RequestView, BankCardListAdapter.OnCheckedBankCardListener, ReLoadingData {

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
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.bottom_lay)
    lateinit var mBottomLay: LinearLayout
    @BindView(R.id.bind_tv_lay)
    lateinit var mBindTvLay: LinearLayout

    private var mRequestTag = ""

    //    private BankCardAdapter mBankCardAdapter;
    private  var mBankCardAdapter: BankCardListAdapter? = null
    private  var mLRecyclerViewAdapter: LRecyclerViewAdapter? = null
    private var mDataList: MutableList<MutableMap<String, Any>>? = ArrayList()
    private val mPage = 1


    private var mSelectMap: MutableMap<String, Any> = HashMap()
    private var mYueMap: MutableMap<String, Any> = HashMap()

    private var type = -1

    private var mUnBindCard: MutableMap<String, Any> = HashMap()


    private lateinit var mCardConfig: MutableMap<String, Any>


    override val contentView: Int
        get() = R.layout.activity_bank_card_list



    private lateinit var mPatncodeMap: MutableMap<String, Any>
    private lateinit var mMoneyMap: MutableMap<String, Any>
    private lateinit var mBindMap: MutableMap<String, Any>
    private lateinit var mBankCardChildAdapter: BankCardChildAdapter

    override fun init() {
        ////getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText.text = resources.getString(R.string.bank_card_title)

        initView()
        //responseData();
        showProgressDialog()

        //cardConfig()
        //bankCardAction()

        /**
         * 设置是否仅仅跟踪左侧边缘的滑动返回
         * 因为银行卡里面有嵌套 viewpager  所以要边界返回
         */
        //mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true);
    }


    /**
     * 查询资金托管配置
     */
    private fun cardConfig() {
        mRequestTag = MethodUrl.supervisionConfig
        val map = HashMap<String, String>()
        map["accsn"] = "A"
        val mHeadermap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeadermap, MethodUrl.supervisionConfig, map)
    }

    private fun initView() {
        mContent?.let { mPageView!!.setContentView(it) }
        mPageView.showLoading()
        mPageView.reLoadingData = this
        val manager = LinearLayoutManager(this@BankCardListActivity)
        manager.orientation = RecyclerView.VERTICAL
        manager.isSmoothScrollbarEnabled = true
        manager.initialPrefetchItemCount = 4
        mRefreshListView.setItemViewCacheSize(20)
        //mRefreshListView.setDrawingCacheEnabled(true);
        //mRefreshListView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRefreshListView.layoutManager = manager
        mRefreshListView.setHasFixedSize(true)

        mRefreshListView.setOnRefreshListener { bankCardAction() }

        mRefreshListView.setOnLoadMoreListener {
            //responseData();
        }

        mRefreshListView.setOnNetWorkErrorListener {
            showProgressDialog()
            bankCardAction()
        }

        for (index in 1..3){
            val map = HashMap<String,Any>()
            map["opnbnknm"]="中国工商银行"
            map["accid"]="121087886680987"
            map["logopath"]="121087886680987"

            mDataList!!.add(map)
        }

        responseData()
    }

    /**
     * 银行卡列表
     */
    private fun bankCardAction() {
        mRequestTag = MethodUrl.bankCardList
        val map = HashMap<String, String>()
        map["isdefault"] = "" //isdefault 是否默认卡（0：否，1：是）
        map["accsn"] = "" //accsn 业务类型(1:提现账户;A充值卡<快捷支付>;)
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.bankCardList, map)
    }

    /**
     * 解绑  提现  充值卡都可以解绑
     */
    private fun unBindCard() {
        mRequestTag = MethodUrl.unbindCard
        val map = HashMap<String, Any>()
        map["accid"] = mUnBindCard["accid"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.unbindCard, map)
    }

    override fun onResume() {
        super.onResume()
      /*  if (mIsRefresh) {
            bankCardAction()
        }
        mIsRefresh = false*/
    }




    //绑定的银行卡信息查询
    private fun bindCardInfo() {

        mRequestTag = MethodUrl.erleiMoney
        val map = HashMap<String, String>()
        map["patncode"] = mBindMap!!["patncode"]!!.toString() + ""
        map["crdno"] = mBindMap!!["accid"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToRes(mHeaderMap, MethodUrl.bindList, map)
    }





    private fun responseData() {

        if (mBankCardAdapter == null) {
            mBankCardAdapter = BankCardListAdapter(this@BankCardListActivity)
            mDataList?.let { mBankCardAdapter!!.addAll(it) }
            // mBankCardAdapter.setOnChangeBankCardListener(this);

            /*AnimationAdapter adapter = new ScaleInAnimationAdapter(mDataAdapter);
            adapter.setFirstOnly(false);
            adapter.setDuration(500);
            adapter.setInterpolator(new OvershootInterpolator(.5f));*/

            mLRecyclerViewAdapter = LRecyclerViewAdapter(mBankCardAdapter)

            //            SampleHeader headerView = new SampleHeader(BankCardActivity.this, R.layout.item_bank_bind);
            //            mLRecyclerViewAdapter.addHeaderView(headerView);

            mRefreshListView.adapter = mLRecyclerViewAdapter
            mRefreshListView.itemAnimator = DefaultItemAnimator()
            mRefreshListView.setHasFixedSize(true)
            mRefreshListView.isNestedScrollingEnabled = false

            mRefreshListView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            mRefreshListView.setPullRefreshEnabled(true)
            mRefreshListView.setLoadMoreEnabled(false)


            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position ->
                val item = mBankCardAdapter!!.dataList[position]
                /* Intent intent = new Intent(getActivity(), ShowDetailPictrue.class);
                    intent.putExtra("jsonData",item.get("url")+"");
                    startActivity(intent);*/
            }



        } else {
            if (mPage == 1) {
                mBankCardAdapter!!.clear()
            }
            mDataList?.let { mBankCardAdapter!!.addAll(it) }
            mBankCardAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
        }
        mBankCardAdapter!!.setOnBankCardListener(this)
        /*   //设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.black, android.R.color.white);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineScalePulseOut); //设置下拉刷新Progress的样式
        //mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);  //设置下拉刷新箭头
        //设置头部加载颜色
        mRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.red ,android.R.color.white);
//设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.red ,android.R.color.white);*/

        mRefreshListView.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        if (mDataList!!.size < 10) {
            mRefreshListView.setNoMore(true)
        } else {
            mRefreshListView.setNoMore(false)
        }

        mRefreshListView.refreshComplete(10)
        mBankCardAdapter!!.notifyDataSetChanged()
        /*if (mBankCardAdapter.getDataList().size() <= 0){
            mPageView.showEmpty();
        }else {*/
        mPageView.showContent()
        //        }
        mRefreshListView.post {
            mBankCardAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
        }
    }

    @OnClick(R.id.back_img, R.id.left_back_lay, R.id.bind_tv_lay)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.bind_tv_lay -> {
                intent = Intent(this@BankCardListActivity,BankCardAddActivity::class.java)
                //intent.putExtra("backtype", "10")
                startActivity(intent)
            }
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
        }
    }

    override fun showProgress() {
        //showProgressDialog();
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

        val intent: Intent
        when (mType) {


            MethodUrl.bindList -> {
                var mBindList = JSONUtil.instance.jsonToList(tData["result"]!!.toString() + "")

                if (mBindList == null || mBindList.size == 0) {
                    mBindList = ArrayList()
                    mBindMap["bindShow"] = "0"
                    //mBindMap.put("bindCard",mBindList);
                    showToastMsg(resources.getString(R.string.bind_card_no))
                } else {
                    mBindMap["bindCard"] = mBindList
                    mBindMap!!["bindShow"] = "1"
                    mBankCardChildAdapter?.notifyDataSetChanged()
                }
            }
            MethodUrl.erleiMoney -> {
                mMoneyMap!!["money"] = tData["acctbal"]!!.toString() + ""
                mMoneyMap!!["isShow"] = "1"
                mBankCardChildAdapter?.notifyDataSetChanged()
            }
            MethodUrl.bankCardList//
            -> {
                type = 0
                val result = tData["result"]!!.toString() + ""
                if (UtilTools.empty(result)) {
                    mDataList = JSONUtil.instance.jsonToList(result)
                    responseData()
                } else {
                    mDataList = JSONUtil.instance.jsonToList(result)
                    if (mDataList != null && mDataList!!.size > 0) {
                        for (map in mDataList!!) {
                            map.put("isShow", "0")
                        }

                    }
                    responseData()
                }
                mRefreshListView!!.refreshComplete(10)
            }
            MethodUrl.unbindCard -> {
                showToastMsg(tData["result"]!!.toString() + "")
                bankCardAction()
            }
        }/*mRefreshListView.post(new Runnable(){
                    @Override
                    public void run() {
                        //mLRecyclerViewAdapter.notifyItemChanged(mLRecyclerViewAdapter.getAdapterPosition(false,(int)mMoneyMap.get("indexPos")) , "jdsjlzx");
                    }
                });*/
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.bankCardList//
            -> if (mBankCardAdapter != null) {
                if (mBankCardAdapter!!.dataList.size <= 0) {
                    mPageView!!.showNetworkError()
                } else {
                    mPageView!!.showContent()
                }
                mRefreshListView!!.refreshComplete(10)
                mRefreshListView!!.setOnNetWorkErrorListener {
                    showProgressDialog()
                    bankCardAction()
                }
            } else {
                mPageView!!.showNetworkError()
            }
        }
        dealFailInfo(map, mType)
    }




    override fun reLoadingData() {
        showProgressDialog()
        bankCardAction()
    }


    companion object {
        var mViews: List<View> = ArrayList()
    }

    override fun onButClickListener(type: String, map: MutableMap<String, Any>, bankCardChildAdapter: BankCardListAdapter) {
        when (type) {
            "1" -> {

            }
            "2" -> {

            }
            "3" -> {
            }
            "4" -> {

            }
            "5"//更改
            -> {
                val intent = Intent(this@BankCardListActivity,BankCardAddActivity::class.java)
                startActivity(intent)
            }
            "6"//删除
            -> {
                val sureOrNoDialog = SureOrNoDialog(this@BankCardListActivity, true)
                sureOrNoDialog.initValue("提示", "解除绑定后，将无法使用该银行卡办理业务,确定要解除绑定吗？")
                sureOrNoDialog.onClickListener = View.OnClickListener { v ->
                    when (v.id) {
                        R.id.cancel -> sureOrNoDialog.dismiss()
                        R.id.confirm -> {
                            showProgressDialog()
                            mUnBindCard = map
                            unBindCard()
                            sureOrNoDialog.dismiss()
                        }
                    }
                }
                sureOrNoDialog.show()
                sureOrNoDialog.setCanceledOnTouchOutside(false)
                sureOrNoDialog.setCancelable(true)
            }
        }/* mRefreshListView.post(new Runnable(){
                    @Override
                    public void run() {
                       // mLRecyclerViewAdapter.notifyItemChanged(mLRecyclerViewAdapter.getAdapterPosition(false,(int)mMoneyMap.get("indexPos")) , "jdsjlzx");
                    }
                });*/

    }
}
