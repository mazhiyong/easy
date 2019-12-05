package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.jaeger.library.StatusBarUtil

import butterknife.BindView
import butterknife.OnClick

/**
 * 开启消息通知（服务提醒）
 */
class ServicesRemindActivity : BasicActivity() {
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.switch_service_remind)
    lateinit var mRemindServiceSwitch: Switch
    @BindView(R.id.ll_layout)
    lateinit var mLayout: LinearLayout
    @BindView(R.id.bt_clear_all)
    lateinit var mClearButton: Button

    override val contentView: Int
        get() = R.layout.activity_services_remind

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.text = resources.getString(R.string.service_remind)
    }

    @OnClick(R.id.bt_clear_all, R.id.back_img, R.id.switch_service_remind, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.switch_service_remind -> if (mRemindServiceSwitch!!.isChecked) {
                mLayout!!.visibility = View.VISIBLE
            } else {
                mLayout!!.visibility = View.INVISIBLE
            }
            R.id.bt_clear_all -> {
                Toast.makeText(this, "清空成功", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ServicesRemindInfoActivity::class.java))
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
