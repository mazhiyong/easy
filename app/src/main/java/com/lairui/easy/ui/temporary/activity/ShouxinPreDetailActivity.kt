package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.recyclerview.ProgressStyle
import com.lairui.easy.R
import com.lairui.easy.ui.temporary.adapter.SwipeMenuSxAdapter
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.tool.DataHolder
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.util.ArrayList
import java.util.HashMap

import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

/**
 * 我的额度  -- 点击列表  -- 授信详情界面  此处为预授信
 */
class ShouxinPreDetailActivity : BasicActivity(), RequestView {
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.right_text_tv)
    lateinit var mRightTextTv: TextView
    @BindView(R.id.right_img)
    lateinit var mRightImg: ImageView
    @BindView(R.id.right_lay)
    lateinit var mRightLay: LinearLayout
    @BindView(R.id.divide_line)
    lateinit var mDivideLine: View
    @BindView(R.id.shouxin_edu_tv)
    lateinit var mShouxinEduTv: TextView
    @BindView(R.id.jiekuan_qixian_tv)
    lateinit var mJiekuanQixianTv: TextView
    @BindView(R.id.huankuan_zhouqi_tv)
    lateinit var mHuankuanZhouqiTv: TextView
    @BindView(R.id.lilv_type_tv)
    lateinit var mLilvTypeTv: TextView
    @BindView(R.id.daikuan_zhonglei_tv)
    lateinit var mDaikuanZhongleiTv: TextView
    @BindView(R.id.daikuan_yongtu_tv)
    lateinit var mDaikuanYongtuTv: TextView
    @BindView(R.id.huankuan_fangshi_tv)
    lateinit var mHuankuanFangshiTv: TextView
    @BindView(R.id.shengqing_date_tv)
    lateinit var mShengqingDateTv: TextView
    @BindView(R.id.zhuangtai_tv)
    lateinit var mZhuangtaiTv: TextView
    @BindView(R.id.same_people_list)
    lateinit var mSamePeopleList: LRecyclerView
    @BindView(R.id.fujian_lay)
    lateinit var mFujianLay: CardView
    @BindView(R.id.other_zhouqi_tv)
    lateinit var mOtherZhouqiTv: TextView
    @BindView(R.id.other_zhouqi_lay)
    lateinit var mOtherZhouqiLay: CardView
    @BindView(R.id.same_people_lay)
    lateinit var mSamePeopleLay: LinearLayout


    private var mRequestTag = ""

    private lateinit var mDataMap: MutableMap<String, Any>

    private var mPeopleList: List<MutableMap<String, Any>>? = ArrayList()

    private lateinit var mSwipeMenuAdapter: SwipeMenuSxAdapter
    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter


    private lateinit var mPayZhouQiMap: MutableMap<String, Any>
    private lateinit var mLilvMap: MutableMap<String, Any>
    private lateinit var mDaikuanUseMap: MutableMap<String, Any>
    private lateinit var mDaikuanZhonglMap: MutableMap<String, Any>
    private lateinit var mJieKuanQxianMap: MutableMap<String, Any>

    private lateinit var mHuanKuanTypeMap: MutableMap<String, Any>
    private lateinit var mDefaultMap: MutableMap<String, Any>


    override val contentView: Int
        get() = R.layout.activity_pre_shouxin_detail

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mDataMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }

        mTitleText!!.text = resources.getString(R.string.my_larger_num)
        getModifyAction()
    }

    /**
     * 得到预授信详情  修改申请授信信息  比如驳回了要修改
     */
    private fun getModifyAction() {

        mRequestTag = MethodUrl.reqShouxinDetail
        val map = HashMap<String, String>()
        map["precreid"] = mDataMap!!["precreid"]!!.toString() + ""//预授信申请ID
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.reqShouxinDetail, map)
    }

    private fun initModifyValue() {


        mShouxinEduTv!!.text = UtilTools.getRMBMoney(mDefaultMap!!["creditmoney"]!!.toString() + "")
        //mJieKuanQxianMap = SelectDataUtil.getMap(mDefaultMap.get("singlelimit") + "", SelectDataUtil.jieKuanLimit());
        mJieKuanQxianMap = SelectDataUtil.getMap(mDefaultMap!!["singlelimit"]!!.toString() + "", SelectDataUtil.getNameCodeByType("loanLimit"))
        mJiekuanQixianTv!!.text = mJieKuanQxianMap!!["name"]!!.toString() + ""//借款期限

        //mPayZhouQiMap = SelectDataUtil.getMap(mDefaultMap.get("interestaccmode") + "", SelectDataUtil.getHkZhouqi());
        mPayZhouQiMap = SelectDataUtil.getMap(mDefaultMap!!["interestaccmode"]!!.toString() + "", SelectDataUtil.getNameCodeByType("repayCycle"))
        mHuankuanZhouqiTv!!.text = mPayZhouQiMap!!["name"]!!.toString() + ""
        if (mPayZhouQiMap!!["code"]!!.toString() + "" == "19") {
            mOtherZhouqiTv!!.text = mDefaultMap!!["interestaccnm"]!!.toString() + ""
            mOtherZhouqiLay!!.visibility = View.VISIBLE
        } else {
            mOtherZhouqiLay!!.visibility = View.GONE
        }

        mLilvMap = SelectDataUtil.getMap(mDefaultMap!!["lvtype"]!!.toString() + "", SelectDataUtil.lilvType)
        mLilvTypeTv!!.text = mLilvMap!!["name"]!!.toString() + ""

        //mDaikuanZhonglMap = SelectDataUtil.getMap(mDefaultMap.get("reqloantp") + "", SelectDataUtil.getDaikuanType());
        mDaikuanZhonglMap = SelectDataUtil.getMap(mDefaultMap!!["reqloantp"]!!.toString() + "", SelectDataUtil.getNameCodeByType("loanType"))
        mDaikuanZhongleiTv!!.text = mDaikuanZhonglMap!!["name"]!!.toString() + ""

        //mDaikuanUseMap = SelectDataUtil.getMap(mDefaultMap.get("loanuse") + "", SelectDataUtil.getDaikuanUse());
        mDaikuanUseMap = SelectDataUtil.getMap(mDefaultMap!!["loanuse"]!!.toString() + "", SelectDataUtil.getNameCodeByType("loanUse"))
        mDaikuanYongtuTv!!.text = mDaikuanUseMap!!["name"]!!.toString() + ""

        //mHuanKuanTypeMap = SelectDataUtil.getMap(mDefaultMap.get("hktype") + "", SelectDataUtil.getHkType());
        mHuanKuanTypeMap = SelectDataUtil.getMap(mDefaultMap!!["hktype"]!!.toString() + "", SelectDataUtil.getNameCodeByType("repayWay"))
        mHuankuanFangshiTv!!.text = mHuanKuanTypeMap!!["name"]!!.toString() + ""

        val mStatus = mDefaultMap!!["creditstate"]!!.toString() + ""
        if (mStatus == "14") {
            mZhuangtaiTv!!.text = "已生效"
        } else {
            mZhuangtaiTv!!.text = "审核中"
        }

        val time = mDefaultMap!!["sqdate"]!!.toString() + ""
        val showTime = UtilTools.getStringFromSting2(time, "yyyyMMdd", "yyyy-MM-dd")
        mShengqingDateTv!!.text = showTime

        mPeopleList = ArrayList((mDefaultMap!!["gtList"] as List<MutableMap<String, Any>>?)!!)
        if (mPeopleList == null || mPeopleList!!.size == 0) {
            mSamePeopleLay!!.visibility = View.GONE
        } else {
            mSamePeopleLay!!.visibility = View.VISIBLE
        }

        responseData()

        val mFileTypeList = mDefaultMap!!["contList"] as List<MutableMap<String, Any>>?

        val mHasFile = mDefaultMap!!["existFileList"] as List<MutableMap<String, Any>>?
        var num = 0
        if (mHasFile != null) {
            for (fileMap in mHasFile) {
                val files = fileMap["files"] as List<MutableMap<String, Any>>?
                for (map in files!!) {
                    val timeList = map["optFiles"] as List<MutableMap<String, Any>>?
                    num = num + timeList!!.size
                }
            }
        }

        /*if (num != 0){
            mAddFileTv2.setVisibility(View.GONE);
            mHasUploadTv2.setVisibility(View.VISIBLE);
            mFileNumTv2.setVisibility(View.VISIBLE);

        }else {
            mAddFileTv2.setVisibility(View.VISIBLE);
            mHasUploadTv2.setVisibility(View.GONE);
            mFileNumTv2.setVisibility(View.GONE);

        }
        mFileNumTv2.setText(num+"个");*/


    }

    private fun responseData() {


        val manager = LinearLayoutManager(this)
        manager.orientation = RecyclerView.VERTICAL
        mSamePeopleList!!.layoutManager = manager
        mSwipeMenuAdapter = SwipeMenuSxAdapter(this)
        mSwipeMenuAdapter!!.isSwipeEnable = false
        mPeopleList?.let { mSwipeMenuAdapter!!.setDataList(it) }

        /* mSwipeMenuAdapter.setOnDelListener(new SwipeMenuAdapter.onSwipeListener() {
            @Override
            public void onDel(int pos) {
                Toast.makeText(ShouxinPreDetailActivity.this, "删除:" + pos, Toast.LENGTH_SHORT).show();

                mPeopleList.remove(pos);
                responseData();
                //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
            }

            @Override
            public void onTop(int pos) {//置顶功能有bug，后续解决

            }
        });*/

        val adapter = ScaleInAnimationAdapter(mSwipeMenuAdapter)
        adapter.setFirstOnly(false)
        adapter.setDuration(500)
        adapter.setInterpolator(OvershootInterpolator(.5f))


        mLRecyclerViewAdapter = LRecyclerViewAdapter(adapter)
        //SampleHeader headerView = new SampleHeader(getActivity(), R.layout.fragment_home_head_view);
        //mLRecyclerViewAdapter.addHeaderView(headerView);
        mSamePeopleList!!.adapter = mLRecyclerViewAdapter
        mSamePeopleList!!.itemAnimator = DefaultItemAnimator()
        //mSamePeopleList.setHasFixedSize(true);
        mSamePeopleList!!.isNestedScrollingEnabled = false

        mSamePeopleList!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
        mSamePeopleList!!.setPullRefreshEnabled(false)
        mSamePeopleList!!.setLoadMoreEnabled(false)

        mSamePeopleList!!.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
        mSamePeopleList!!.setArrowImageView(R.drawable.ic_pulltorefresh_arrow)

        mSamePeopleList!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
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
            MethodUrl.reqShouxinDetail//
            -> {
                mDefaultMap = tData
                initModifyValue()
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.reqShouxinDetail -> getModifyAction()
                }
            }
        }

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }


    @OnClick(R.id.back_img, R.id.left_back_lay, R.id.fujian_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.fujian_lay -> {
                val mHasFile = mDefaultMap!!["existFileList"] as List<MutableMap<String, Any>>?
                intent = Intent(this@ShouxinPreDetailActivity, ModifyFileActivity::class.java)
                //intent.putExtra("DATA",(Serializable) mHasFile);
                DataHolder.instance!!.save("fileList", mHasFile!!)
                startActivity(intent)
            }
        }
    }


}
