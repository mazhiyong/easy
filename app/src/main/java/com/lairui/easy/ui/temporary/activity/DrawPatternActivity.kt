package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.ShoushiPatternCallBack
import com.lairui.easy.mywidget.view.ShouShiPatternView
import com.lairui.easy.mywidget.view.ShouShiPatternViewSmall
import com.lairui.easy.utils.secret.MD5
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SPUtils
import com.jaeger.library.StatusBarUtil

import butterknife.BindView
import butterknife.OnClick

/**
 * 手势解锁（创建）界面
 */
class DrawPatternActivity : BasicActivity(), ShoushiPatternCallBack {
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.shoushi_big)
    lateinit var mPatternViewBig: ShouShiPatternView
    @BindView(R.id.shoushi_small)
    lateinit var mPatternViewSmall: ShouShiPatternViewSmall
    @BindView(R.id.tv_tip)
    lateinit var mTextView: TextView
    @BindView(R.id.bt_set_sucess)
    lateinit var mButton: Button

    lateinit var first: String
    override val contentView: Int
        get() = R.layout.activity_draw_pattern

    override fun isSupportSwipeBack(): Boolean {
        return false
    }

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText!!.setText(R.string.set_handpause)
        ShouShiPatternView.setCallBack(this)
    }

    override fun finsh(i: Int, str: List<Int>) {
        mPatternViewSmall!!.cellSeleced(str)
        mPatternViewSmall!!.invalidate()
        Log.i("show", "密码的长度：" + str.toString().length)
        when (i) {
            0 -> {
                mTextView!!.text = "请再次绘制手势密码图案"
                mButton!!.visibility = View.INVISIBLE
                first = str.toString()
            }
            1 -> if (first != null) {
                if (first == str.toString()) {
                    mPatternViewSmall!!.cellSeleced(str)
                    mTextView!!.text = "手势密码创建成功"
                    mButton!!.visibility = View.VISIBLE
                    //对手势密码进行MD5加密
                    val reslult = MD5.getInstance()!!.md5String(str.toString())
                    Log.i("show", "MD5加密后的结果：$reslult")
                    //sharedPreferences 保存加密后的结果
                    SPUtils.put(this@DrawPatternActivity, MbsConstans.SharedInfoConstans.SHOUSHI_CODE, reslult)


                } else {
                    mButton!!.visibility = View.INVISIBLE
                    mTextView!!.text = "手势密码创建失败，请重新创建"

                }

            }
            2 -> {
                mTextView!!.text = "请重新绘制图案，至少连接5个点"
                mButton!!.visibility = View.INVISIBLE
            }
        }
    }


    @OnClick(R.id.back_img, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
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
