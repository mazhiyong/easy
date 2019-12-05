package com.lairui.easy.ui.temporary.activity

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.jaeger.library.StatusBarUtil

import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick

class IntoChiFinishActivity : BasicActivity(), RequestView, ReLoadingData {

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
    @BindView(R.id.my_image)
    lateinit var mMyImage: ImageView
    @BindView(R.id.submit_result_tv)
    lateinit var mSubmitResultTv: TextView
    @BindView(R.id.submit_tip_tv)
    lateinit var mSubmitTipTv: TextView
    @BindView(R.id.title_tv1)
    lateinit var mTitleTv1: TextView
    @BindView(R.id.value_tv1)
    lateinit var mValueTv1: TextView
    @BindView(R.id.title_lay1)
    lateinit var mTitleLay1: LinearLayout
    @BindView(R.id.title_tv2)
    lateinit var mTitleTv2: TextView
    @BindView(R.id.value_tv2)
    lateinit var mValueTv2: TextView
    @BindView(R.id.title_lay2)
    lateinit var mTitleLay2: LinearLayout
    @BindView(R.id.line2)
    lateinit var mLine2: View
    @BindView(R.id.title_tv3)
    lateinit var mTitleTv3: TextView
    @BindView(R.id.value_tv3)
    lateinit var mValueTv3: TextView
    @BindView(R.id.title_lay3)
    lateinit var mTitleLay3: LinearLayout
    @BindView(R.id.line3)
    lateinit var mLine3: View
    @BindView(R.id.but_back)
    lateinit var mButBack: Button

    override val contentView: Int
        get() = R.layout.activity_into_chi_finish

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

    @OnClick(R.id.left_back_lay, R.id.but_back)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.left_back_lay -> finish()
            R.id.but_back -> finish()
        }
    }
}
