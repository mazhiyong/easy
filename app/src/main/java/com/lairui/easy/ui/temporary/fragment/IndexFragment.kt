package com.lairui.easy.ui.temporary.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager

import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.AdapterView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.github.why168.LoopViewPagerLayout
import com.github.why168.loader.OnDefaultImageViewLoader
import com.github.why168.modle.BannerInfo
import com.github.why168.modle.IndicatorLocation
import com.github.why168.modle.LoopStyle
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.activity.TestActivity
import com.lairui.easy.ui.temporary.adapter.GoodsAdapter
import com.lairui.easy.ui.temporary.adapter.HomeGridViewAdapter
import com.lairui.easy.basic.BasicFragment
import com.lairui.easy.mywidget.view.IndexGridView
import com.lairui.easy.mywidget.view.SampleHeader
import com.lairui.easy.mywidget.viewpager.BannerViewPager
import com.lairui.easy.mywidget.viewpager.ViewFactory
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.ArrayList
import java.util.HashMap
import java.util.Random

import butterknife.BindView
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

@SuppressLint("ValidFragment")
class IndexFragment : BasicFragment, View.OnClickListener {

    @BindView(R.id.but_test)
    lateinit var mButTest: Button
    @BindView(R.id.refresh_list_view)
    lateinit var mRefreshListView: LRecyclerView
    @BindView(R.id.cart_view)
    lateinit var mCartView: ImageView
    @BindView(R.id.list_cart_num)
    lateinit  var mListCartNum: TextView
    @BindView(R.id.title_bar_view)
    lateinit var mTitleBarView: LinearLayout

    //    private Button testBut;
    //    private View rootView;
    //    private LRecyclerView mRecyclerView;
    //    private ImageView cart_btn;
    //    private TextView mCartNumTv;

    //头部布局文件
    private lateinit var mFlBanner: BannerViewPager
    private lateinit var mLoopViewPagerLayout: LoopViewPagerLayout
    private lateinit var mIndexGridView: IndexGridView

    var isRefresh = false
    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private lateinit var mDataAdapter: GoodsAdapter
    private val imageUrls = arrayOf("http://m.360buyimg.com/mobilecms/s720x350_jfs/t3058/134/987833711/92043/510bc5da/57c3e41dN557e42e4.jpg!q70.jpg", "http://m.360buyimg.com/mobilecms/s720x350_jfs/t3151/236/1017904659/76060/7a50f772/57c41ed5Nd132bb3f.jpg!q70.jpg")
    // private String[] imageUrls = {"http://pic7.nipic.com/20100525/4796759_105030008376_2.jpg"};
    private val views = ArrayList<ImageView>()
    private val infos = ArrayList<String>()

    private var mPage = 1


    override val layoutId: Int
        get() = R.layout.fragment_goods


    var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            list!!.clear()
            for (i in 0..9) {
                val map = HashMap<String, Any>()
                list!!.add(map)
            }

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


    private lateinit var mCartAnim: Animation
    //动画时间
    private val AnimationDuration = 1000
    //正在执行的动画数量
    private var number = 0
    //是否完成清理
    private var isClean = false
    private lateinit var animation_viewGroup: FrameLayout
    private val animHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {
                    //用来清除动画后留下的垃圾
                    try {
                        animation_viewGroup!!.removeAllViews()
                    } catch (e: Exception) {

                    }

                    isClean = false
                }
                else -> {
                }
            }
        }
    }


    constructor() {
        // Required empty public constructor
    }

    constructor(cart_btn: ImageView, mCartNumTv: TextView) {
        this.mCartView = cart_btn
        this.mListCartNum = mCartNumTv
    }

    override fun init() {
        initView()
    }


    /*  @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.fragment_goods, (ViewGroup) getActivity().findViewById(R.id.food_manager_page), false);
        initView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup p = (ViewGroup) rootView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return rootView;
    }*/

    private fun initView() {

        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, activity!!.resources.getDimension(R.dimen.title_item_height).toInt() + UtilTools.getStatusHeight2(activity!!))
        mTitleBarView!!.layoutParams = layoutParams
        mTitleBarView!!.setPadding(0, UtilTools.getStatusHeight2(activity!!), 0, 0)

        animation_viewGroup = createAnimLayout()
        mCartAnim = AnimationUtils.loadAnimation(activity, R.anim.anim_collect)

        //cart_btn = rootView.findViewById(R.id.cart_view);
        //mCartNumTv = (TextView) rootView.findViewById(R.id.list_cart_num);
        mButTest!!.setOnClickListener(this)
        val manager = GridLayoutManager(activity, 2)
        mRefreshListView!!.layoutManager = manager
        mRefreshListView!!.setLScrollListener(mLScrollListener)
        mRefreshListView!!.setOnRefreshListener {
            mPage = 1

            requestData()
        }

        mRefreshListView!!.setOnLoadMoreListener {
            mPage++
            requestData()
        }

        mRefreshListView!!.setOnNetWorkErrorListener { requestData() }
        handler.sendEmptyMessageDelayed(1, 0)
        setBarTextColor()
    }

    fun setBarTextColor() {
        StatusBarUtil.setLightMode(activity!!)
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
        if (mPage == 1 && mDataAdapter != null) {
            mDataAdapter!!.clear()
        }


        if (mDataAdapter == null) {
            mDataAdapter = GoodsAdapter(activity!!)
            mDataAdapter!!.addAll(list!!)

            val adapter = ScaleInAnimationAdapter(mDataAdapter)
            adapter.setFirstOnly(false)
            adapter.setDuration(500)
            adapter.setInterpolator(OvershootInterpolator(.5f))


            mLRecyclerViewAdapter = LRecyclerViewAdapter(adapter)
            val headerView = SampleHeader(activity!!, R.layout.fragment_home_head_view)
            mLRecyclerViewAdapter!!.addHeaderView(headerView)
            mRefreshListView!!.adapter = mLRecyclerViewAdapter
            mRefreshListView!!.itemAnimator = DefaultItemAnimator()
            mRefreshListView!!.setHasFixedSize(true)
            mRefreshListView!!.isNestedScrollingEnabled = false


            //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            mRefreshListView!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
            mRefreshListView!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)


            mFlBanner = headerView.findViewById<View>(R.id.fl_banner) as BannerViewPager
            mLoopViewPagerLayout = headerView.findViewById<View>(R.id.mLoopViewPagerLayout) as LoopViewPagerLayout
            mIndexGridView = headerView.findViewById(R.id.gird_view)
            // RecyclerViewUtils.setHeaderView(mRecyclerView, new SampleHeader(getActivity()));

            mLRecyclerViewAdapter!!.setOnItemClickListener { view, position ->
                val item = mDataAdapter!!.dataList[position]
                /* Intent intent = new Intent(getActivity(), ShowDetailPictrue.class);
                    intent.putExtra("jsonData",item.get("url")+"");
                    startActivity(intent);*/
            }

            initViewPager()
            initViewPager2()

            initGridView()

        } else {
            mDataAdapter!!.addAll(list!!)
            mDataAdapter!!.notifyDataSetChanged()
        }
        //设置底部加载颜色
        mRefreshListView!!.setFooterViewColor(R.color.colorAccent, R.color.black, android.R.color.white)
        if (list == null || list!!.size < 10) {
            mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        } else {
            mRefreshListView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        }

        mRefreshListView!!.refreshComplete(10)
        mDataAdapter!!.notifyDataSetChanged()


        /*mDataAdapter.SetOnSetHolderClickListener(new HolderClickListener(){
            @Override
            public void onHolderClick(Drawable drawable, int[] start_location, Map<String,Object> m) {
                // TODO Auto-generated method stub
                doAnim(drawable,start_location);
                //addCartAction(m);
            }

        });*/
    }

    private fun initViewPager() {
        for (i in imageUrls.indices) {
            infos.add(imageUrls[i])
        }
        // 将最后一个ImageView添加进来
        activity?.let { ViewFactory.getImageView(it, infos[infos.size - 1]) }?.let { views.add(it) }
        for (i in infos.indices) {
            activity?.let { ViewFactory.getImageView(it, infos[i]) }?.let { views.add(it) }
        }
        // 将第一个ImageView添加进来
        activity?.let { ViewFactory.getImageView(it, infos[0]) }?.let { views.add(it) }
        // 在加载数据前设置是否循环

        mFlBanner?.setData(views, infos, object : BannerViewPager.OnItemClickListener {
            override fun onItemClick(postion: Int) {
                Toast.makeText(activity, postion.toString() + "", Toast.LENGTH_SHORT).show()
            }
        })


        //开始轮播
        mFlBanner!!.isWhee = true
        // 设置轮播时间，默认3000ms
        mFlBanner!!.setScrollTime(4000)
        //设置圆点指示图标组居中显示，默认靠右
        mFlBanner!!.setIndicatorCenter()
    }


    private fun initViewPager2() {
        mLoopViewPagerLayout!!.loop_ms = 2000//轮播的速度(毫秒)
        mLoopViewPagerLayout!!.setLoop_duration(1000)//滑动的速率(毫秒)
        mLoopViewPagerLayout!!.setLoop_style(LoopStyle.Empty)//轮播的样式-默认empty
        mLoopViewPagerLayout!!.setIndicatorLocation(IndicatorLocation.Center)//指示器位置-中Center
        // mLoopViewPagerLayout.setNormalBackground(R.drawable.normal_background);//默认指示器颜色
        // mLoopViewPagerLayout.setSelectedBackground(R.drawable.selected_background);//选中指示器颜色

        mLoopViewPagerLayout!!.initializeData(activity)//初始化数据
        val bannerInfos = ArrayList<BannerInfo<*>>()
        bannerInfos.add(BannerInfo(imageUrls[0], "第一张图片"))
        bannerInfos.add(BannerInfo(imageUrls[1], "第二张图片"))
        bannerInfos.add(BannerInfo(imageUrls[0], "第三张图片"))
        bannerInfos.add(BannerInfo(imageUrls[1], "第四张图片"))
        bannerInfos.add(BannerInfo(imageUrls[0], "第五张图片"))
        mLoopViewPagerLayout!!.setOnLoadImageViewListener(object : OnDefaultImageViewLoader() {
            override fun onLoadImageView(imageView: ImageView, parameter: Any) {
                Glide.with(activity!!)
                        .load(parameter)
                        .into(imageView)
            }

            override fun createImageView(context: Context): ImageView {
                val imageView = ImageView(context)
                imageView.scaleType = ImageView.ScaleType.FIT_XY
                return imageView
            }
        })//设置图片加载&自定义图片监听
        mLoopViewPagerLayout!!.setOnBannerItemClickListener { index, banner -> }//设置监听
        mLoopViewPagerLayout!!.setLoopData(bannerInfos)//设置数据

    }

    private fun initGridView() {

        val gridList = ArrayList<MutableMap<String, Any>>()
        var gridMap: MutableMap<String, Any> = HashMap()
        gridMap["url"] = "http://m.360buyimg.com/mobilecms/s80x80_jfs/t2941/340/2547425887/3566/4dc2eaf4/57b3b3e5N53d03f9f.png"
        gridMap["name"] = "购物"
        gridMap["code"] = "0001"
        gridList.add(gridMap)

        gridMap = HashMap()
        gridMap["url"] = "http://m.360buyimg.com/mobilecms/s80x80_jfs/t2941/340/2547425887/3566/4dc2eaf4/57b3b3e5N53d03f9f.png"
        gridMap["name"] = "教育"
        gridMap["code"] = "0002"
        gridList.add(gridMap)

        gridMap = HashMap()
        gridMap["url"] = "http://m.360buyimg.com/mobilecms/s80x80_jfs/t2941/340/2547425887/3566/4dc2eaf4/57b3b3e5N53d03f9f.png"
        gridMap["name"] = "救援"
        gridMap["code"] = "0003"
        gridList.add(gridMap)

        gridMap = HashMap()
        gridMap["url"] = "http://m.360buyimg.com/mobilecms/s80x80_jfs/t2941/340/2547425887/3566/4dc2eaf4/57b3b3e5N53d03f9f.png"
        gridMap["name"] = "智能家具"
        gridMap["code"] = "0004"
        gridList.add(gridMap)

        gridMap = HashMap()
        gridMap["url"] = "http://m.360buyimg.com/mobilecms/s80x80_jfs/t2941/340/2547425887/3566/4dc2eaf4/57b3b3e5N53d03f9f.png"
        gridMap["name"] = "便民服务"
        gridMap["code"] = "0005"
        gridList.add(gridMap)

        gridMap = HashMap()
        gridMap["url"] = "http://m.360buyimg.com/mobilecms/s80x80_jfs/t2941/340/2547425887/3566/4dc2eaf4/57b3b3e5N53d03f9f.png"
        gridMap["name"] = "理财"
        gridMap["code"] = "0006"
        gridList.add(gridMap)

        gridMap = HashMap()
        gridMap["url"] = "http://m.360buyimg.com/mobilecms/s80x80_jfs/t2941/340/2547425887/3566/4dc2eaf4/57b3b3e5N53d03f9f.png"
        gridMap["name"] = "微创业"
        gridMap["code"] = "0007"
        gridList.add(gridMap)

        gridMap = HashMap()
        gridMap["url"] = "http://m.360buyimg.com/mobilecms/s80x80_jfs/t2941/340/2547425887/3566/4dc2eaf4/57b3b3e5N53d03f9f.png"
        gridMap["name"] = "慈善"
        gridMap["code"] = "0008"
        gridList.add(gridMap)

        gridMap = HashMap()
        gridMap["url"] = "http://m.360buyimg.com/mobilecms/s80x80_jfs/t2941/340/2547425887/3566/4dc2eaf4/57b3b3e5N53d03f9f.png"
        gridMap["name"] = "吃喝玩乐"
        gridMap["code"] = "0009"
        gridList.add(gridMap)

        gridMap = HashMap()
        gridMap["url"] = "http://m.360buyimg.com/mobilecms/s80x80_jfs/t2941/340/2547425887/3566/4dc2eaf4/57b3b3e5N53d03f9f.png"
        gridMap["name"] = "医疗"
        gridMap["code"] = "0010"
        gridList.add(gridMap)

        val gridViewAdapter = HomeGridViewAdapter(activity!!, gridList)
        mIndexGridView!!.adapter = gridViewAdapter
        mIndexGridView!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l -> val map = adapterView.adapter.getItem(i) as MutableMap<String, Any> }

    }


    override fun onClick(view: View) {
        val intent = Intent(activity, TestActivity::class.java)
        startActivity(intent)
    }

    private fun doAnim(drawable: Drawable, start_location: IntArray) {
        if (!isClean) {
            setAnim(drawable, start_location)
        } else {
            try {
                animation_viewGroup!!.removeAllViews()
                isClean = false
                setAnim(drawable, start_location)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isClean = true
            }
        }
    }

    /**
     * @param
     * @return void
     * @throws
     * @Description: 创建动画层
     */
    private fun createAnimLayout(): FrameLayout {
        val rootView = activity!!.window.decorView as ViewGroup
        val animLayout = FrameLayout(activity!!)
        val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        animLayout.layoutParams = lp
        animLayout.setBackgroundResource(android.R.color.transparent)
        rootView.addView(animLayout)
        return animLayout

    }

    /**
     * @param vg       动画运行的层 这里是frameLayout
     * @param view     要运行动画的View
     * @param location 动画的起始位置
     * @return
     */
    @Deprecated("将要执行动画的view 添加到动画层")
    private fun addViewToAnimLayout(vg: ViewGroup, view: View, location: IntArray): View {
        val x = location[0]
        val y = location[1]
        vg.addView(view)
        val lp = FrameLayout.LayoutParams(
                dip2px(activity!!, 60f), dip2px(activity!!, 60f))
        lp.leftMargin = x
        lp.topMargin = y
        view.setPadding(5, 5, 5, 5)
        view.layoutParams = lp

        return view
    }

    /**
     * dip，dp转化成px 用来处理不同分辨路的屏幕
     *
     * @param context
     * @param dpValue
     * @return
     */
    private fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 动画效果设置
     *
     * @param drawable       将要加入购物车的商品
     * @param start_location 起始位置
     */
    private fun setAnim(drawable: Drawable, start_location: IntArray) {
        val iview = ImageView(activity)
        iview.setImageDrawable(drawable)
        val view = this.addViewToAnimLayout(animation_viewGroup!!, iview, start_location)
        view.alpha = 0.6f

        val end_location = IntArray(2)// 这是用来存储动画结束位置的X、Y坐标
        val ra = Random()
        mCartView.getLocationInWindow(end_location)
        // 计算位移
        val endX = 0 - start_location[0] + mCartView.left// 动画位移的X坐标
        val endY = end_location[1] - start_location[1]// 动画位移的y坐标
        val translateAnimationX = TranslateAnimation(0f,
                endX.toFloat(), 0f, 0f)
        translateAnimationX.interpolator = LinearInterpolator()
        translateAnimationX.repeatCount = 0// 动画重复执行的次数
        //translateAnimationX.setFillAfter(true);

        val translateAnimationY = TranslateAnimation(0f,
                0f, 0f, endY.toFloat())
        translateAnimationY.interpolator = AccelerateInterpolator()
        translateAnimationY.repeatCount = 0// 动画重复执行的次数
        // translateAnimationX.setFillAfter(true);

        val mRotateAnimation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)

        val mScaleAnimation = ScaleAnimation(1.0f, 0.3f, 1.0f, 0.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        // mScaleAnimation.setFillAfter(true);


        val set = AnimationSet(false)
        set.fillAfter = false
        set.addAnimation(mRotateAnimation)
        set.addAnimation(mScaleAnimation)
        set.addAnimation(translateAnimationY)
        set.addAnimation(translateAnimationX)
        set.duration = AnimationDuration.toLong()// 动画的执行时间


        /* view.startAnimation(set);
        // 动画监听事件
        set.setAnimationListener(new Animation.AnimationListener() {
            // 动画的开始
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            // 动画的结束
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }
        });*/


        set.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {
                // TODO Auto-generated method stub
                number++
            }

            override fun onAnimationEnd(animation: Animation) {
                // TODO Auto-generated method stub

                number--
                if (number == 0) {
                    isClean = true
                    animHandler.sendEmptyMessage(0)
                }
                view.visibility = View.GONE
                mListCartNum.startAnimation(mCartAnim)
                mCartView.startAnimation(mCartAnim)
            }

            override fun onAnimationRepeat(animation: Animation) {
                // TODO Auto-generated method stub

            }

        })
        view.startAnimation(set)

    }

    /**
     * 内存过低时及时处理动画产生的未处理冗余
     */
    override fun onLowMemory() {
        // TODO Auto-generated method stub
        isClean = true
        try {
            animation_viewGroup!!.removeAllViews()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        isClean = false
        super.onLowMemory()
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
