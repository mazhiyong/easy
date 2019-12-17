package com.lairui.easy.ui.module4.activity

import android.content.Intent
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick
import com.jaeger.library.StatusBarUtil
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.dialog.AppDialog
import java.util.*

/**
 * 策略详情 已结算
 */
class CeLueItemFinishActivity : BasicActivity(), RequestView {

    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.right_img)
    lateinit var mRightImg: ImageView
    @BindView(R.id.top_layout)
    lateinit var mTitleBarView: LinearLayout
    @BindView(R.id.titleTv)
    lateinit var mTitleTv: TextView
    @BindView(R.id.timeTv)
    lateinit var mTimeTv: TextView
    @BindView(R.id.typeTv)
    lateinit var mTypeTv: TextView
    @BindView(R.id.celueMoneyTv)
    lateinit var mCelueMoneyTv: TextView
    @BindView(R.id.caopanMoneyTv)
    lateinit var mCaopanMoneyTv: TextView
    @BindView(R.id.addMoneyTv)
    lateinit var mAddMoneyTv: TextView
    @BindView(R.id.extendMoneyTv)
    lateinit var mExtendMoneyTv: TextView
    @BindView(R.id.kuisunTv)
    lateinit var mKuisunTv: TextView
    @BindView(R.id.pingcangTv)
    lateinit var mPingcangTv: TextView
    @BindView(R.id.lixiTv)
    lateinit var mLixiTv: TextView
    @BindView(R.id.zhouqiTv)
    lateinit var mZhouqiTv: TextView
    @BindView(R.id.tiquBt)
    lateinit var mTiquBt: Button
    @BindView(R.id.stopBt)
    lateinit var mStopBt: Button
    @BindView(R.id.extendRecordLay)
    lateinit var mExtendRecordLay: RelativeLayout
    @BindView(R.id.addMoneyRecordLay)
    lateinit var mAddMoneyRecordLay: RelativeLayout
    @BindView(R.id.lixiRecordLay)
    lateinit var mLixiRecordLay: RelativeLayout
    @BindView(R.id.shouyiRecordLay)
    lateinit var mShouyiRecordLay: RelativeLayout











    private var mOpType = 0

    override val contentView: Int
        get() = R.layout.activity_celue_item_finish

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        mTitleText.text = "策略详情"
    }

    /**
     * 网络连接请求
     */
    private fun submitInstall() {

        val map = HashMap<String, Any>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.installCerSubmit, map)
    }

    @OnClick(R.id.back_img, R.id.left_back_lay,R.id.addMoneyTv,R.id.extendMoneyTv,R.id.tiquBt,R.id.stopBt,R.id.addMoneyRecordLay,R.id.extendRecordLay,R.id.lixiRecordLay,R.id.shouyiRecordLay)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.left_back_lay, R.id.back_img -> finish()
            R.id.addMoneyTv -> {
                intent = Intent(this@CeLueItemFinishActivity,AddMoneyActivity::class.java)
                startActivity(intent)
            }
            R.id.extendMoneyTv -> {
                intent = Intent(this@CeLueItemFinishActivity,ExtendMoneyActivity::class.java)
                startActivity(intent)
            }
            R.id.tiquBt -> {
                intent = Intent(this@CeLueItemFinishActivity,TixuMoneyActivity::class.java)
                startActivity(intent)
            }
            R.id.stopBt -> {
                val dialog = AppDialog(this@CeLueItemFinishActivity,true)
                dialog.initValue("终止操盘须知","请确保账户内已经全部清仓完 否则我们将有权把您的股票进行平仓处理","取消","确定")
                dialog.setClickListener(View.OnClickListener { v ->
                    when (v.id) {
                        R.id.cancel -> dialog.dismiss()
                        R.id.confirm -> {
                        }
                    }
                })

                dialog.show()
            }
            R.id.addMoneyRecordLay-> {
                intent = Intent(this@CeLueItemFinishActivity,RecordListActivity::class.java)
                startActivity(intent)
            }
            R.id.extendRecordLay-> {
                intent = Intent(this@CeLueItemFinishActivity,RecordListActivity::class.java)
                startActivity(intent)
            }
            R.id.lixiRecordLay-> {
                intent = Intent(this@CeLueItemFinishActivity,RecordListActivity::class.java)
                startActivity(intent)
            }
            R.id.shouyiRecordLay-> {
                intent = Intent(this@CeLueItemFinishActivity,RecordListActivity::class.java)
                startActivity(intent)
            }

        }
    }


    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
