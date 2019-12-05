package com.lairui.easy.ui.temporary.activity

/**
 * 开始指纹登录   界面
 */

import android.content.Intent
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.jaeger.library.StatusBarUtil

import butterknife.BindView
import butterknife.OnClick

class FingermentActivity : BasicActivity() {
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.switch_finger_login)
    lateinit var mSwitch: Switch
    override val contentView: Int
        get() = R.layout.activity_fingerment

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText!!.text = resources.getString(R.string.set_title)
    }

    @OnClick(R.id.back_img, R.id.switch_finger_login, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.switch_finger_login -> if (mSwitch!!.isChecked) {
                //startActivity(new Intent(FingermentActivity.this,FingerRecognieActivity.class));
                startActivity(Intent(this@FingermentActivity, FingerCheckActivity::class.java))
            }
        }
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
