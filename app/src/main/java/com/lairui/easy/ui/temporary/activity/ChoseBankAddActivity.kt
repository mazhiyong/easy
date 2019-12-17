package com.lairui.easy.ui.temporary.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mywidget.dialog.AddressSelectDialog2
import com.lairui.easy.listener.SelectBackListener
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.ui.module.activity.MainActivity

/**
 * 选择银行网点   界面
 */
class ChoseBankAddActivity : BasicActivity(), RequestView, SelectBackListener {

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
    @BindView(R.id.address_value_tv)
    lateinit var mAddressValueTv: TextView
    @BindView(R.id.choose_city_lay)
    lateinit var mChooseCityLay: CardView
    @BindView(R.id.bank_dian_value_tv)
    lateinit var mBankDianValueTv: TextView
    @BindView(R.id.bank_dian_lay)
    lateinit var mBankDianLay: CardView
    @BindView(R.id.but_sure)
    lateinit var mButSure: Button

    private lateinit var mAddressSelectDialog2: AddressSelectDialog2
    private var mRequestTag = ""
    private lateinit var mAddressMap: MutableMap<String, Any>

    private var mBankId: String? = ""

    private var mWangDianMap: MutableMap<String, Any>? = null

    override val contentView: Int
        get() = R.layout.activity_chose_bankadd

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)


        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mBankId = bundle.getString("bankid")
        }
        mTitleText!!.text = resources.getString(R.string.chose_bank_address)
        mAddressSelectDialog2 = AddressSelectDialog2(this, true, "选择地址", 10)
        mAddressSelectDialog2!!.selectBackListener = this

    }

    /**
     */
    private fun submitInstall() {

        mRequestTag = MethodUrl.submitUserInfo

        val map = HashMap<String, Any>()

        map["opnbnkid"] = ""
        map["citycode"] = ""

        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.installCerSubmit, map)
    }


    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.submitUserInfo -> {

                showToastMsg("提交成功")
                backTo(MainActivity::class.java, true)
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.submitUserInfo -> submitInstall()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }


    private fun sureButAction() {
        if (mAddressMap == null || mAddressMap!!.isEmpty()) {
            UtilTools.isEmpty(mAddressValueTv!!, "城市")
            return
        }

        if (mWangDianMap == null || mWangDianMap!!.isEmpty()) {
            UtilTools.isEmpty(mBankDianValueTv!!, "银行网点")
            return
        }

        val intent = Intent()
        intent.putExtra("DATA", mWangDianMap as Serializable?)
        intent.putExtra("DATA2", mAddressMap as Serializable?)
        setResult(Activity.RESULT_OK, intent)

        finish()
    }

    @OnClick(R.id.choose_city_lay, R.id.bank_dian_lay, R.id.back_img, R.id.but_sure, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.but_sure -> sureButAction()
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.choose_city_lay -> mAddressSelectDialog2!!.showAtLocation(Gravity.BOTTOM, 0, 0)
            R.id.bank_dian_lay -> if (mAddressMap == null || mAddressMap!!.isEmpty()) {
                showToastMsg("请选择城市")
            } else {
                intent = Intent(this@ChoseBankAddActivity, BankWdActivity::class.java)
                intent.putExtra("bankid", mBankId)
                intent.putExtra("citycode", mAddressMap!!["citycode"]!!.toString() + "")
                startActivityForResult(intent, 111)
            }
        }
    }

    override fun onSelectBackListener(map: MutableMap<String, Any>, type: Int) {
        when (type) {
            10 -> {
                mAddressValueTv!!.setError(null, null)
                if (mAddressMap == null) {
                    mAddressMap = map
                } else {
                    LogUtil.i("show", (mAddressMap === map).toString() + "  " + (mAddressMap == map))
                    if (mAddressMap == map) {

                    } else {
                        mAddressMap = map
                        mWangDianMap = null
                        mBankDianValueTv!!.text = ""
                    }
                }
                mAddressValueTv!!.text = mAddressMap!!["name"]!!.toString() + ""
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val bundle: Bundle?
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                111 -> {
                    bundle = data!!.extras
                    if (bundle != null) {//{"opnbnkwdnm":"南洋商业银行（中国）有限公司北京分行","opnbnkwdcd":"503100000015"}
                        mWangDianMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
                        mBankDianValueTv!!.text = mWangDianMap!!["opnbnkwdnm"]!!.toString() + ""
                        mBankDianValueTv!!.setError(null, null)
                    }
                }
            }
        }
    }

}
