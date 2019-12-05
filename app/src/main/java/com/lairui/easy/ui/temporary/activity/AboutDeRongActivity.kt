package com.lairui.easy.ui.temporary.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mvp.view.RequestView
import com.jaeger.library.StatusBarUtil

import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick

/**
 * 得融在线介绍  界面
 */
class AboutDeRongActivity : BasicActivity(), RequestView {
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.tv)
    lateinit var mTextView: TextView

    override val contentView: Int
        get() = R.layout.activity_about_derong


    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.setText(R.string.derong_jieshao)

        mTextView!!.text = "        自2015年“互联网+能源+金融”战略写入科锐得集团《十三五规划》以来，科锐得集团从未放弃为“打造世界一流能源互联网企业”添砖加瓦梦想与实践。从最初与省公司各部门共同设计的“科锐得互联网统一运营平台”，到当下以“供应链金融”为核心的“互联网金融平台-得融在线”的完成，产融结合的先进理念与互联网思维在科锐得集团生根发芽。目前该平台已通过了我国最具权威的第三方金融认证机构CFCA的专业认证，通过了省电科院的系统安全认证，取得了省市金融局的认可、通过了省通管局的备案许可。符合国网公司“保证安全，业务主导，先行先试”的原则。"
    }

    @OnClick(R.id.back_img)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
        }
    }


    override fun showProgress() {

    }

    override fun disimissProgress() {

    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {


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

    /**
     * activity恢复时触发的方法
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

    }

}
