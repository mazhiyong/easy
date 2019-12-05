package com.lairui.easy.ui.temporary.activity

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick

/**
 * 应收账款详情界面
 */
class ShouMoneyItemActivity : BasicActivity(), RequestView, ReLoadingData {

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
    @BindView(R.id.pz_number_tv)
    lateinit var mPzNumberTv: TextView
    @BindView(R.id.pz_money_tv)
    lateinit var mPzMoneyTv: TextView
    @BindView(R.id.fukuan_fang_tv)
    lateinit var mFukuanFangTv: TextView
    @BindView(R.id.shoukuan_fang_tv)
    lateinit var mShoukuanFangTv: TextView
    @BindView(R.id.date_tv)
    lateinit var mDateTv: TextView
    @BindView(R.id.zhaiyao_tv)
    lateinit var mZhaiyaoTv: TextView
    @BindView(R.id.tv_money)
    lateinit var mMoenyTv: TextView

    override val contentView: Int
        get() = R.layout.activity_shou_money_item

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = "应收账款详情"
        val bundle = intent.extras
        if (bundle != null) {
            val map = bundle.getSerializable("DATA") as MutableMap<String, Any>
            mPzNumberTv!!.text = map["billid"]!!.toString() + ""
            mMoenyTv!!.text = UtilTools.getRMBMoney(map["vouchmny"]!!.toString() + "")
            mPzMoneyTv!!.text = UtilTools.getRMBMoney(map["vouchmny"]!!.toString() + "")
            mFukuanFangTv!!.text = map["payfirmname"]!!.toString() + ""
            mShoukuanFangTv!!.text = "暂无"
            mDateTv!!.text = map["paydate"]!!.toString() + ""
            mZhaiyaoTv!!.text = "暂无"
        }


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

    @OnClick(R.id.left_back_lay)
    fun onViewClicked() {
        finish()
    }
}
