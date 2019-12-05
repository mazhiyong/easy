package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.SelectDataUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick

/**
 * 我的额度  -- 点击列表  -- 授信详情界面
 */
class ShouxinDetailActivity : BasicActivity(), RequestView {

    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.more_info_lay)
    lateinit var mMoreInfoLay: LinearLayout
    @BindView(R.id.more_info_but)
    lateinit var mMoreInfoBut: ImageView
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.shouxin_edu_tv)
    lateinit var mShouxinEduTv: TextView
    @BindView(R.id.shouxin_hetong_tv)
    lateinit var mShouxinHetongTv: TextView
    @BindView(R.id.hetong_name_tv)
    lateinit var mHetongNameTv: TextView
    @BindView(R.id.chujieren_tv)
    lateinit var mChujierenTv: TextView
    @BindView(R.id.jiekuan_ren_tv)
    lateinit var mJiekuanRenTv: TextView
    @BindView(R.id.shouxin_yue_tv)
    lateinit var mShouxinYueTv: TextView
    @BindView(R.id.nian_lilv_tv)
    lateinit var mNianLilvTv: TextView
    @BindView(R.id.danbi_jiekuan_tv)
    lateinit var mDanbiJiekuanTv: TextView
    @BindView(R.id.huankuan_type_tv)
    lateinit var mHuankuanTypeTv: TextView
    @BindView(R.id.danbao_type_tv)
    lateinit var mDanbaoTypeTv: TextView
    @BindView(R.id.danbao_ren_tv)
    lateinit var mDanbaoRenTv: TextView
    @BindView(R.id.hasno_zhuisuo_tv)
    lateinit var mHasnoZhuisuoTv: TextView
    @BindView(R.id.baoli_yewu_tv)
    lateinit var mBaoliYewuTv: TextView
    @BindView(R.id.chuzhuang_type_tv)
    lateinit var mChuzhuangTypeTv: TextView
    @BindView(R.id.shouxin_stop_tv)
    lateinit var mShouxinStopTv: TextView
    @BindView(R.id.jiekuan_xinxi_lay)
    lateinit var mJiekuanXinxiLay: CardView
    @BindView(R.id.fujian_lay)
    lateinit var mFujianLay: CardView
    @BindView(R.id.hetong_lay)
    lateinit var mHetongLay: CardView
    @BindView(R.id.divide_jiekuan)
    lateinit var mDivideJiekuan: View
    @BindView(R.id.divide_jiekuan2)
    lateinit var mDivideJiekuan2: View

    private var mRequestTag = ""

    private lateinit var mDataMap: MutableMap<String, Any>

    override val contentView: Int
        get() = R.layout.activity_shouxin_detail

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mDataMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }

        mTitleText!!.text = resources.getString(R.string.my_larger_num)

        mMoreInfoLay!!.visibility = View.GONE

        detailAction()
    }

    private fun detailAction() {

        mRequestTag = MethodUrl.shouxinDetail
        val map = HashMap<String, String>()
        map["creditfile"] = mDataMap!!["creditfile"]!!.toString() + ""
        map["flowdate"] = mDataMap!!["flowdate"]!!.toString() + ""
        map["flowid"] = mDataMap!!["flowid"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.shouxinDetail, map)
    }

    private fun initValue() {

        val status = mDataMap!!["creditstate"]!!.toString() + ""
        when (status) {
            "0" -> {
            }
            "1" -> {
            }
            "2" -> {
            }
            "3" -> {
                //viewHolder.mStatusTv.setText("终止");
                mJiekuanXinxiLay!!.visibility = View.GONE
                mDivideJiekuan!!.visibility = View.GONE
                mDivideJiekuan2!!.visibility = View.GONE
            }
            "4" -> {
            }
        }//viewHolder.mStatusTv.setText("未开通");
        //viewHolder.mStatusTv.setText("已开通");
        //viewHolder.mStatusTv.setText("暂停");
        //viewHolder.mStatusTv.setText("已签署");


        //Map<String,Object> hkType = SelectDataUtil.getMap(mDataMap.get("hktypenm")+"",SelectDataUtil.getHkType());
        val hkType = SelectDataUtil.getMap(mDataMap!!["hktypenm"]!!.toString() + "", SelectDataUtil.getNameCodeByType("repayWay"))
        //Map<String,Object> danboType = SelectDataUtil.getMap(mDataMap.get("assutype")+"",SelectDataUtil.getDanbaoType());
        val danboType = SelectDataUtil.getMap(mDataMap!!["assutype"]!!.toString() + "", SelectDataUtil.getNameCodeByType("assuType"))
        val hasZhuisuo = SelectDataUtil.getMap(mDataMap!!["blzhsoqn"]!!.toString() + "", SelectDataUtil.hasZhuisuo)
        val baoliType = SelectDataUtil.getMap(mDataMap!!["blyewufs"]!!.toString() + "", SelectDataUtil.baoliType)
        val chuzhuangType = SelectDataUtil.getMap(mDataMap!!["vrtacct"]!!.toString() + "", SelectDataUtil.chuzhangType)

        //Map<String,Object> danwei = SelectDataUtil.getMap(mDataMap.get("singleunit")+"",SelectDataUtil.getQixianDw());
        val danwei = SelectDataUtil.getMap(mDataMap!!["singleunit"]!!.toString() + "", SelectDataUtil.getNameCodeByType("limitUnit"))

        mShouxinEduTv!!.text = UtilTools.getRMBMoney(mDataMap!!["creditmoney"]!!.toString() + "")
        mShouxinHetongTv!!.text = mDataMap!!["creditfile"]!!.toString() + ""
        mHetongNameTv!!.text = if (UtilTools.empty(mDataMap!!["creditfilename"]!!.toString() + "")) "" else mDataMap!!["creditfilename"]!!.toString() + ""
        mChujierenTv!!.text = mDataMap!!["zifangnme"]!!.toString() + ""
        mJiekuanRenTv!!.text = ""//借款人--------------------------
        mShouxinYueTv!!.text = UtilTools.getRMBMoney(mDataMap!!["leftmoney"]!!.toString() + "")
        mNianLilvTv!!.text = UtilTools.getlilv(mDataMap!!["daiklilv"]!!.toString() + "")
        mDanbiJiekuanTv!!.text = mDataMap!!["singlelimit"].toString() + "" + danwei["name"]
        //mHuankuanTypeTv.setText(hkType.get("name")+"");
        mHuankuanTypeTv!!.text = if (UtilTools.empty(mDataMap!!["hktypenm"]!!.toString() + "")) "" else mDataMap!!["hktypenm"]!!.toString() + ""
        mDanbaoTypeTv!!.text = danboType["name"]!!.toString() + ""
        mDanbaoRenTv!!.text = ""
        mHasnoZhuisuoTv!!.text = hasZhuisuo["name"]!!.toString() + ""
        mBaoliYewuTv!!.text = baoliType["name"]!!.toString() + ""
        mChuzhuangTypeTv!!.text = chuzhuangType["name"]!!.toString() + ""


        val time = UtilTools.getStringFromSting2(mDataMap!!["enddate"]!!.toString() + "", "yyyyMMdd", "yyyy-MM-dd")
        mShouxinStopTv!!.text = time

    }


    @OnClick(R.id.back_img, R.id.more_info_but, R.id.jiekuan_xinxi_lay, R.id.fujian_lay, R.id.hetong_lay, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.more_info_but -> if (mMoreInfoLay!!.isShown) {
                mMoreInfoLay!!.visibility = View.GONE
                mMoreInfoBut!!.setImageResource(R.drawable.android_list_down)
            } else {
                mMoreInfoLay!!.visibility = View.VISIBLE
                mMoreInfoBut!!.setImageResource(R.drawable.android_list_up)
            }
            R.id.jiekuan_xinxi_lay -> {
                intent = Intent(this@ShouxinDetailActivity, ShouxinJkListActivity::class.java)
                intent.putExtra("DATA", mDataMap as Serializable?)
                startActivity(intent)
            }
            R.id.fujian_lay -> {
            }
            R.id.hetong_lay -> {
                intent = Intent(this@ShouxinDetailActivity, HeTongShowActivity::class.java)
                val mHetongList = mDataMap!!["contList"] as List<MutableMap<String, Any>>?
                intent.putExtra("DATA", mHetongList as Serializable?)
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
        val intent: Intent
        when (mType) {
            MethodUrl.shouxinDetail//
            -> {
                mDataMap = tData
                initValue()
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.shouxinDetail -> detailAction()
                }
            }
        }

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }
}
