package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.PopuTipView
import com.jaeger.library.StatusBarUtil

import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import butterknife.BindView
import butterknife.OnClick

/**
 * 申请入池
 */
class IntoChiActivity : BasicActivity(), RequestView, ReLoadingData {


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
    @BindView(R.id.top_layout)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.total_money_tv)
    lateinit var mTotalMoneyTv: TextView
    @BindView(R.id.guanlian_lay)
    lateinit var mGuanlianLay: CardView
    @BindView(R.id.number_et)
    lateinit var mNumberEt: EditText
    @BindView(R.id.buyer_tv)
    lateinit var mBuyerTv: TextView
    @BindView(R.id.date_tv)
    lateinit var mDateTv: TextView
    @BindView(R.id.money_et)
    lateinit var mMoneyEt: EditText
    @BindView(R.id.pay_type_value_tv)
    lateinit var mPayTypeValueTv: TextView
    @BindView(R.id.pay_type_lay)
    lateinit var mPayTypeLay: CardView
    @BindView(R.id.zhouqi_value_tv)
    lateinit var mZhouqiValueTv: TextView
    @BindView(R.id.zhongqi_lay)
    lateinit var mZhongqiLay: CardView
    @BindView(R.id.other_zhognqi_line)
    lateinit var mOtherZhognqiLine: View
    @BindView(R.id.other_zhonqi_lay)
    lateinit var mOtherZhonqiLay: CardView
    @BindView(R.id.zhaiyao_et)
    lateinit var mZhaiyaoEt: EditText
    @BindView(R.id.lilv_type_lay)
    lateinit var mLilvTypeLay: CardView
    @BindView(R.id.chujieren_tv)
    lateinit var mChujierenTv: TextView
    @BindView(R.id.chujieren_lay)
    lateinit var mChujierenLay: CardView
    @BindView(R.id.same_people_list)
    lateinit var mSamePeopleList: LRecyclerView
    @BindView(R.id.add_same_people_lay)
    lateinit var mAddSamePeopleLay: CardView
    @BindView(R.id.has_upload_tv2)
    lateinit var mHasUploadTv2: TextView
    @BindView(R.id.add_file_tv2)
    lateinit var mAddFileTv2: TextView
    @BindView(R.id.file_num_tv2)
    lateinit var mFileNumTv2: TextView
    @BindView(R.id.has_fujian_lay)
    lateinit var mHasFujianLay: CardView
    @BindView(R.id.bulu_divide_view)
    lateinit var mBuluDivideView: View
    @BindView(R.id.bulu_lay)
    lateinit var mBuluLay: LinearLayout
    @BindView(R.id.add_fujian_title)
    lateinit var mAddFujianTitle: TextView
    @BindView(R.id.has_upload_tv)
    lateinit var mHasUploadTv: TextView
    @BindView(R.id.add_file_tv)
    lateinit var mAddFileTv: TextView
    @BindView(R.id.file_num_tv)
    lateinit var mFileNumTv: TextView
    @BindView(R.id.fujian_lay)
    lateinit var mFujianLay: CardView
    @BindView(R.id.cb_xieyi)
    lateinit var mCbXieyi: CheckBox
    @BindView(R.id.xieyi_tv)
    lateinit var mXieyiTv: TextView
    @BindView(R.id.xieyi_lay)
    lateinit var mXieyiLay: LinearLayout
    @BindView(R.id.but_submit)
    lateinit var mButSubmit: Button
    @BindView(R.id.scrollView_content)
    lateinit var mScrollViewContent: NestedScrollView
    @BindView(R.id.tip_wu_view)
    lateinit var mTipWuView: ImageView
    @BindView(R.id.tip_ri_view)
    lateinit var mTipRiView: ImageView


    override val contentView: Int
        get() = R.layout.activity_into_chi

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText!!.text = "申请入池"
    }


    override fun reLoadingData() {

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

    @OnClick(R.id.left_back_lay, R.id.guanlian_lay, R.id.zhongqi_lay, R.id.but_submit, R.id.tip_wu_view, R.id.tip_ri_view)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.left_back_lay -> finish()
            R.id.guanlian_lay -> {
            }
            R.id.zhongqi_lay -> {
            }
            R.id.but_submit -> startActivity(Intent(this@IntoChiActivity, IntoChiFinishActivity::class.java))
            R.id.tip_wu_view -> {

                val s = "付款截止日=当月的发票日期+" + "\n" +
                        "结算周期（按月计算，每30天" + "\n" +
                        "算一个月）后的当月最后一天"

                val mp = PopuTipView(this@IntoChiActivity, s, R.layout.popu_lay_top)
                mTipWuView?.let { mp.show(it, 1) }
            }
            R.id.tip_ri_view -> {


                val s1 = "1、若开票日期在当月结算日之前" + "\n" +
                        "（不含结算日），则付款截止日=当" + "\n" +
                        "月结算日+结算周期（按月计算）" + "\n" +
                        "2、若开票日期在当月结算日之后" + "\n" +
                        "（含结算日），则付款截止日=下一" + "\n" +
                        "月结算日+结算周期（按月计算）"

                val mp1 = PopuTipView(this@IntoChiActivity, s1, R.layout.popu_lay_top)
                mTipRiView?.let { mp1.show(it, 2) }
            }
        }/*View inflate = View.inflate(IntoChiActivity.this, R.layout.popup_bubble_text, null);
                TextView mTextView = inflate.findViewById(R.id.tv_bubble);
                mTextView.setText("付款截止日=当月的发票日期+" + "\n" +
                        "结算周期（按月计算，每30天" + "\n" +
                        "算一个月）后的当月最后一天" );

                new BubblePopup(IntoChiActivity.this, inflate)
                        .anchorView(mTipWuView)
                        .bubbleColor(Color.parseColor("#A6000000"))
                        .gravity(Gravity.TOP)
                        .triangleWidth(5)
                        .triangleHeight(5)
                        .showAnim(null)
                        .dismissAnim(null)
                        .show();*//* View inflate2 = View.inflate(IntoChiActivity.this, R.layout.popup_bubble_text, null);
                TextView mTextView2 = inflate2.findViewById(R.id.tv_bubble);
                mTextView2.setText("1、若开票日期在当月结算日之前"+"\n"+
                        "（不含结算日），则付款截止日=当"+ "\n" +
                        "月结算日+结算周期（按月计算）"+ "\n" +
                        "2、若开票日期在当月结算日之后"+ "\n" +
                        "（含结算日），则付款截止日=下一"+ "\n" +
                        "月结算日+结算周期（按月计算）");


                new BubblePopup(IntoChiActivity.this, inflate2)
                        .anchorView(mTipRiView)
                        .bubbleColor(Color.parseColor("#A6000000"))
                        .gravity(Gravity.TOP)
                        .triangleWidth(5)
                        .triangleHeight(5)
                        .showAnim(null)
                        .dismissAnim(null)
                        .show();*/
    }
}
