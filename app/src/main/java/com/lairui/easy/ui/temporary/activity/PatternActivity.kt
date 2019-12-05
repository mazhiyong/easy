package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SPUtils
import com.jaeger.library.StatusBarUtil

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

/**
 * 开始手势登录 界面
 */
class PatternActivity : BasicActivity() {
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.switch_shoushi_login)
    lateinit var mSwitchLogin: Switch
    @BindView(R.id.switch_shoushi_show)
    lateinit var mSwitchShow: Switch
    @BindView(R.id.iv_shoushi_update)
    lateinit var iv_Shoushi: ImageView
    @BindView(R.id.ll)
    lateinit var ll: LinearLayout
    @BindView(R.id.ll_shoushi_update)
    lateinit var mLlShoushiUpdate: LinearLayout

    override val contentView: Int
        get() = R.layout.activity_pattern

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText!!.text = resources.getString(R.string.shoushi_login)
    }

    @OnClick(R.id.back_img, R.id.switch_shoushi_login, R.id.switch_shoushi_show, R.id.iv_shoushi_update, R.id.ll_shoushi_update, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.switch_shoushi_login -> if (mSwitchLogin!!.isChecked) {
                ll!!.visibility = View.VISIBLE
            } else {
                ll!!.visibility = View.INVISIBLE
            }
            R.id.switch_shoushi_show -> {
            }
            R.id.ll_shoushi_update -> {
                if (mSwitchShow!!.isChecked) {
                    SPUtils.put(this@PatternActivity, MbsConstans.SharedInfoConstans.SHOW_SHOUSHI, "ture")
                } else {
                    SPUtils.put(this@PatternActivity, MbsConstans.SharedInfoConstans.SHOW_SHOUSHI, "false")
                }
                val intent = Intent(this, DrawPatternActivity::class.java)
                startActivity(intent)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this)
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
