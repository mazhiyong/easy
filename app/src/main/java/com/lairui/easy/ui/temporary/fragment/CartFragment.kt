package com.lairui.easy.ui.temporary.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Message

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager

import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.CartAdapter
import com.lairui.easy.basic.BasicFragment
import com.lairui.easy.mywidget.view.MyRefreshHeader
import com.lairui.easy.mywidget.view.PageView

import java.util.ArrayList
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

class CartFragment : BasicFragment() {
    @BindView(R.id.refresh_list_view)
    lateinit var mRecyclerView: LRecyclerView
    @BindView(R.id.content)
    lateinit var content: LinearLayout
    @BindView(R.id.page_view)
    lateinit var pageView: PageView
    @BindView(R.id.back_img)
    lateinit var backImg: ImageView
    @BindView(R.id.title_text)
    lateinit var titleText: TextView
    @BindView(R.id.all_checkbox)
    lateinit var allCheckbox: CheckBox
    @BindView(R.id.cart_totaltip)
    lateinit var cartTotaltip: TextView
    @BindView(R.id.cart_totalprice)
    lateinit var cartTotalprice: TextView
    @BindView(R.id.cart_btn_pay)
    lateinit var cartBtnPay: Button
    @BindView(R.id.right_img)
    lateinit var rightImg: ImageView
    @BindView(R.id.right_lay)
    lateinit var rightLay: LinearLayout
    @BindView(R.id.bottom_lay)
    lateinit var bottomLay: LinearLayout


    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private lateinit var mDataAdapter: CartAdapter

    private val mJianPanShow = false


    override val layoutId: Int
        get() = R.layout.fragment_cart


    var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            getData()
            responseData()
        }
    }
   var list: MutableList<MutableMap<String, Any>>? = ArrayList()


    private val mLScrollListener = object : LRecyclerView.LScrollListener {
        override fun onScrollUp() {

        }

        override fun onScrollDown() {

        }

        override fun onScrolled(distanceX: Int, distanceY: Int) {

        }

        override fun onScrollStateChanged(state: Int) {

        }
    }

    override fun init() {
        initView()
    }


    private fun initView() {

        mRecyclerView!!.setRefreshHeader(activity?.let { MyRefreshHeader(it) })

        mRecyclerView!!.setLScrollListener(mLScrollListener)
        mRecyclerView!!.setOnRefreshListener { requestData() }
        //        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineScalePulseOut); //设置下拉刷新Progress的样式
        //        //mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);  //设置下拉刷新箭头
        //        //设置头部加载颜色
        //        mRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.red ,R.color.white);
        ////设置底部加载颜色
        //        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.red ,android.R.color.white);
        mRecyclerView!!.setOnLoadMoreListener { requestData() }

        mRecyclerView!!.setOnNetWorkErrorListener { requestData() }
        handler.sendEmptyMessageDelayed(1, 0)


    }

    private fun requestData() {


        handler.sendEmptyMessageDelayed(1, 1000)

    }


    private fun responseData() {

        /*if (map == null){
            mPageView.showNetworkError();
            return;
        }else {
            mPageView.showContent();
        }*/


        if (mDataAdapter == null) {
            mDataAdapter = CartAdapter(activity!!)
            mDataAdapter!!.addAll(list!!)

            val adapter = ScaleInAnimationAdapter(mDataAdapter)
            adapter.setFirstOnly(false)
            adapter.setDuration(500)
            adapter.setInterpolator(OvershootInterpolator(.5f))

            mLRecyclerViewAdapter = LRecyclerViewAdapter(adapter)
            mRecyclerView!!.adapter = mLRecyclerViewAdapter
            mRecyclerView!!.itemAnimator = DefaultItemAnimator()
            mRecyclerView!!.setHasFixedSize(true)
            mRecyclerView!!.isNestedScrollingEnabled = false
            mRecyclerView!!.layoutManager = LinearLayoutManager(activity)

            mRecyclerView!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRecyclerView!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

            mRecyclerView!!.setLoadMoreEnabled(false)

            /* mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Map<String, Object> item = mDataAdapter.getDataList().get(position);
                }

            });*/

        } else {
            mDataAdapter!!.clear()
            mDataAdapter!!.addAll(list!!)
            mDataAdapter!!.notifyDataSetChanged()
            mLRecyclerViewAdapter!!.notifyDataSetChanged()//必须调用此方法
        }
        /*  //设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.black ,android.R.color.white);*/


        if (list == null || list!!.size < 10) {
            mRecyclerView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        } else {
            mRecyclerView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        }

        mRecyclerView!!.refreshComplete(10)
        mDataAdapter!!.notifyDataSetChanged()
    }


    @OnClick(R.id.back_img, R.id.cart_btn_pay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> {
            }
            R.id.cart_btn_pay -> {
            }
        }// intent = new Intent(CartActivity.this,WriteOrderActivity.class);
        // startActivity(intent);
    }


    private fun getData() {

        list!!.clear()

        var map: MutableMap<String, Any> = HashMap()
        map["name"] = "苹果111"
        map["code"] = "001"
        map["shopName"] = "天仓旗舰店"
        list!!.add(map)

        map = HashMap()
        map["name"] = "苹果222"
        map["code"] = "001"
        map["shopName"] = "天仓旗舰店"
        list!!.add(map)


        map = HashMap()
        map["name"] = "苹果333"
        map["code"] = "001"
        map["shopName"] = "天仓旗舰店"
        list!!.add(map)


        map = HashMap()
        map["name"] = "西瓜11"
        map["code"] = "002"
        map["shopName"] = "天仓体验店"
        list!!.add(map)


        map = HashMap()
        map["name"] = "西瓜22"
        map["code"] = "002"
        map["shopName"] = "天仓体验店"
        list!!.add(map)


        map = HashMap()
        map["name"] = "西瓜33"
        map["code"] = "002"
        map["shopName"] = "天仓体验店"
        list!!.add(map)


        map = HashMap()
        map["name"] = "孙悟空111"
        map["code"] = "003"
        map["shopName"] = "天仓优惠店"
        list!!.add(map)


        map = HashMap()
        map["name"] = "孙悟空222"
        map["code"] = "003"
        map["shopName"] = "天仓优惠店"
        list!!.add(map)


    }

    /**
     * @descriptoin 请求前加载progress
     * @author dc
     * @date 2017/2/16 11:00
     */
    override fun showProgress() {

    }

    /**
     * @descriptoin 请求结束之后隐藏progress
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun disimissProgress() {

    }

    /**
     * @param tData 数据类型
     * @param mType
     * @descriptoin 请求数据成功
     * @author dc
     * @date 2017/2/16 11:01
     */
    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

    }

    /**
     * @param map
     * @param mType
     * @descriptoin 请求数据错误
     * @date 2017/2/16 11:01
     */
    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

    }
}
