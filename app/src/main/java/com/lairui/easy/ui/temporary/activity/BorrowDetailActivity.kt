package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.flyco.animation.BaseAnimatorSet
import com.flyco.animation.SlideEnter.SlideBottomEnter
import com.flyco.animation.SlideExit.SlideBottomExit
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.listener.ReLoadingData
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.mywidget.view.PageView
import com.lairui.easy.utils.tool.DataHolder
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.ParseTextUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import com.lairui.easy.mywidget.dialog.BaseDialog

/**
 * 借款记录详情   界面
 */
class BorrowDetailActivity : BasicActivity(), RequestView, ReLoadingData {

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
    @BindView(R.id.borrow_info_lay)
    lateinit var mBorrowInfoLay: CardView
    @BindView(R.id.borrow_file_lay)
    lateinit var mBorrowFileLay: CardView
    @BindView(R.id.borrow_hetong_lay)
    lateinit var mBorrowHetongLay: CardView
    @BindView(R.id.borrow_pay_lay)
    lateinit var mBorrowPayLay: CardView
    @BindView(R.id.borrow_repayplan_lay)
    lateinit var mBorrowRepayplanLay: CardView
    @BindView(R.id.borrow_repayshistory_lay)
    lateinit var mBorrowRepayshistoryLay: CardView
    @BindView(R.id.borrow_card_lay)
    lateinit var mBorrowCardLay: CardView
    @BindView(R.id.money_tv)
    lateinit var mLeftMoneyTv: TextView
    @BindView(R.id.jiekuan_zonge_tv)
    lateinit var mZongMoneyTv: TextView
    @BindView(R.id.tiqian_huankuan_tv)
    lateinit var mTqHuanKuanTv: TextView
    @BindView(R.id.status_image_view)
    lateinit var mStatusImageView: ImageView
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout
    @BindView(R.id.page_view)
    lateinit var mPageView: PageView
    @BindView(R.id.bohui_yuanyin_lay)
    lateinit var mBohuiYyLay: LinearLayout
    @BindView(R.id.bohui_yuanyin_tv)
    lateinit var mBohuiYyTv: TextView
    @BindView(R.id.chexiao_sq_button)
    lateinit var mChexiaoButton: Button
    @BindView(R.id.shoutuo_pay_line)
    lateinit var mShoutuoPayLine: View
    @BindView(R.id.huankuan_plan_line)
    lateinit var mHuankuanPlanLine: View
    @BindView(R.id.huankuan_his_line)
    lateinit var mHuankuanHisLine: View
    @BindView(R.id.jiekuan_use_line)
    lateinit var mJiekuanUseLine: View
    @BindView(R.id.bottom_line)
    lateinit var mBottomLine: View

    private var mRequestTag = ""

    private lateinit var mDataMap: MutableMap<String, Any>
    private lateinit var mResultMap: MutableMap<String, Any>

    override val contentView: Int get() = R.layout.activity_borrow_detail

    private lateinit var mEditDialog: BaseDialog
    private lateinit var mDSureBut: Button
    private lateinit var mDCancleBut: Button
    private lateinit var mDEditText: EditText
    private lateinit var mDCountText: TextView
    private val MAX_COUNT = 100

    private val mTextWatcher = object : TextWatcher {

        private var editStart: Int = 0

        private var editEnd: Int = 0

        override fun afterTextChanged(s: Editable) {
            editStart = mDEditText!!.selectionStart
            editEnd = mDEditText!!.selectionEnd

            // 先去掉监听器，否则会出现栈溢出
            mDEditText!!.removeTextChangedListener(this)

            // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
            // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
            while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
                s.delete(editStart - 1, editEnd)
                editStart--
                editEnd--
            }
            mDEditText!!.text = s
            mDEditText!!.setSelection(editStart)

            // 恢复监听器
            mDEditText!!.addTextChangedListener(this)

            setLeftCount()
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                       after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                   count: Int) {
        }
    }

    /**
     * 获取用户输入的分享内容字数
     *
     * @return
     */
    private val inputCount: Long
        get() = calculateLength(mDEditText!!.text.toString())

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)


        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mDataMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
        }
        mTitleText!!.text = resources.getString(R.string.my_borrow_title)
        mContent?.let { mPageView!!.setContentView(it) }
        mPageView!!.reLoadingData = this
        mPageView!!.showLoading()
        mStatusImageView!!.visibility = View.GONE
        borrowDetail()

        //mTqHuanKuanTv.getBackground().setAlpha((int)(0.1*255));
        //mTqHuanKuanTv.setTextColor(ContextCompat.getColor(this,R.color.red3));

    }

    /**
     * 借款详情
     */
    private fun borrowDetail() {

        mRequestTag = MethodUrl.borrowDetail
        val map = HashMap<String, String>()
        map["loansqid"] = mDataMap!!["loansqid"]!!.toString() + ""
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.borrowDetail, map)
    }

    /**
     * 获取借款信息  贷后详情
     */
    private fun getModifyAction() {

        mRequestTag = MethodUrl.daihouDetail
        val map = HashMap<String, String>()
        map["loansqid"] = mDataMap!!["loansqid"]!!.toString() + ""//借款申请编号
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.daihouDetail, map)
    }

    private fun initStatus() {

        if (mResultMap != null) {
            mStatusImageView!!.visibility = View.VISIBLE

            //借款状态（1：放款中 2：还款中 3：已结清 4：已驳回）  loanstate
            val status = mResultMap!!["loanstate"]!!.toString() + ""
            Log.i("show", "status:$status")
            when (status) {
                "1" -> {
                    mStatusImageView!!.setImageResource(R.drawable.shenhe)
                    mTqHuanKuanTv!!.visibility = View.GONE
                    mLeftMoneyTv!!.visibility = View.VISIBLE
                    mZongMoneyTv!!.visibility = View.GONE
                    mBohuiYyLay!!.visibility = View.GONE
                    // mChexiaoButton.setVisibility(View.VISIBLE);
                    mChexiaoButton!!.visibility = View.GONE

                    mHuankuanPlanLine!!.visibility = View.GONE
                    mBorrowRepayplanLay!!.visibility = View.GONE

                    mHuankuanHisLine!!.visibility = View.GONE
                    mBorrowRepayshistoryLay!!.visibility = View.GONE

                    mJiekuanUseLine!!.visibility = View.GONE
                    mBorrowCardLay!!.visibility = View.GONE

                    mBottomLine!!.visibility = View.GONE
                }
                "2" -> {
                    mStatusImageView!!.setImageResource(R.drawable.huankuan)
                    mTqHuanKuanTv!!.visibility = View.VISIBLE
                    mLeftMoneyTv!!.visibility = View.VISIBLE
                    mZongMoneyTv!!.visibility = View.VISIBLE
                    mBohuiYyLay!!.visibility = View.GONE
                    mChexiaoButton!!.visibility = View.GONE
                }
                "3" -> {
                    mStatusImageView!!.setImageResource(R.drawable.jieqing)
                    mTqHuanKuanTv!!.visibility = View.GONE
                    mLeftMoneyTv!!.visibility = View.VISIBLE
                    mZongMoneyTv!!.visibility = View.VISIBLE
                    mBohuiYyLay!!.visibility = View.GONE
                    mChexiaoButton!!.visibility = View.GONE
                }
                "4" -> {
                    mStatusImageView!!.setImageResource(R.drawable.bohui)
                    mTqHuanKuanTv!!.visibility = View.GONE
                    mLeftMoneyTv!!.visibility = View.VISIBLE
                    mZongMoneyTv!!.visibility = View.GONE
                    mBohuiYyLay!!.visibility = View.VISIBLE
                    mChexiaoButton!!.visibility = View.GONE
                    if (UtilTools.empty(mResultMap!!["lnfailrsn"])) {
                        mBohuiYyTv!!.text = "驳回原因:无"
                    } else {
                        mBohuiYyTv!!.text = "驳回原因:" + mResultMap!!["lnfailrsn"]!!
                    }


                    mHuankuanPlanLine!!.visibility = View.GONE
                    mBorrowRepayplanLay!!.visibility = View.GONE

                    mHuankuanHisLine!!.visibility = View.GONE
                    mBorrowRepayshistoryLay!!.visibility = View.GONE

                    mJiekuanUseLine!!.visibility = View.GONE
                    mBorrowCardLay!!.visibility = View.GONE

                    mBottomLine!!.visibility = View.GONE
                }
                else -> {
                    mBohuiYyLay!!.visibility = View.GONE
                    mChexiaoButton!!.visibility = View.GONE
                    mTqHuanKuanTv!!.visibility = View.GONE
                }
            }//mShoutuoPayLine.setVisibility(View.GONE);

        }
    }

    @OnClick(R.id.back_img, R.id.borrow_info_lay, R.id.borrow_file_lay, R.id.borrow_hetong_lay, R.id.borrow_pay_lay, R.id.borrow_repayplan_lay, R.id.borrow_repayshistory_lay, R.id.borrow_card_lay, R.id.tiqian_huankuan_tv, R.id.left_back_lay, R.id.chexiao_sq_button)
    fun onViewClicked(view: View) {
        var intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.borrow_info_lay//借款信息
            -> {
                intent = Intent(this@BorrowDetailActivity, BorrowInfoActivity::class.java)
                //intent.putExtra("DATA",(Serializable) mDataMap);
                intent.putExtra("DATA", mResultMap as Serializable?)
                startActivity(intent)
            }
            R.id.borrow_file_lay//附件信息
            -> {
                intent = Intent(this@BorrowDetailActivity, FujianShowActivity::class.java)
                //intent.putExtra("DATA", (Serializable) ((List<Map<String, Object>>) mResultMap.get("imgList")));
                DataHolder.instance!!.save("fileList", mResultMap!!["imgList"] as List<MutableMap<String, Any>>?)
                startActivity(intent)
            }
            R.id.borrow_hetong_lay//合同信息
            -> {
                val mHetongList = mResultMap!!["contList"] as List<MutableMap<String, Any>>?
                if (mHetongList != null && mHetongList.size > 0) {
                    if (mHetongList.size == 1) {
                        val mHetongMap = mHetongList[0]
                        intent = Intent(this@BorrowDetailActivity, PDFLookActivity::class.java)
                        //intent.putExtra("DATA",(Serializable) mDataMap);
                        intent.putExtra("id", mHetongMap["pdfurl"]!!.toString() + "")
                        startActivity(intent)
                    } else {
                        intent = Intent(this@BorrowDetailActivity, HeTongShowActivity::class.java)
                        intent.putExtra("DATA", mResultMap!!["contList"] as List<MutableMap<String, Any>>? as Serializable)
                        startActivity(intent)
                    }
                } else {
                    showToastMsg("暂无合同信息")
                }
            }
            R.id.borrow_pay_lay//受托支付信息
            -> {
                intent = Intent(this@BorrowDetailActivity, PayTheInfoActivity::class.java)
                intent.putExtra("DATA", mDataMap as Serializable?)
                startActivity(intent)
            }
            R.id.borrow_repayplan_lay//还款计划
            -> {
                intent = Intent(this@BorrowDetailActivity, PayPlanActivity::class.java)
                intent.putExtra("DATA", mDataMap as Serializable?)
                startActivity(intent)
            }
            R.id.borrow_repayshistory_lay//还款记录
            -> {
                intent = Intent(this@BorrowDetailActivity, PayHistoryActivity::class.java)
                intent.putExtra("DATA", mDataMap as Serializable?)
                startActivity(intent)
            }
            R.id.borrow_card_lay//借款用途凭证
            -> getModifyAction()
            R.id.tiqian_huankuan_tv -> {
                intent = Intent(this@BorrowDetailActivity, HuankuanActivity::class.java)
                intent.putExtra("DATA", mDataMap as Serializable?)
                startActivity(intent)
            }
            R.id.chexiao_sq_button -> showDialog()
        }
    }


    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.borrowDetail//
            -> {
                mPageView!!.showContent()
                mResultMap = tData

                val all = java.lang.Double.valueOf(mResultMap!!["reqmoney"]!!.toString() + "")
                val backmoney = java.lang.Double.valueOf(mResultMap!!["backmoney"]!!.toString() + "")
                val leftMoney = UtilTools.sub(all, backmoney)
                val leftStr = UtilTools.getRMBMoney(leftMoney.toString() + "")

                val m = ParseTextUtil(this)
                val spannable = m.getDianType(leftStr)
                mLeftMoneyTv!!.text = spannable

                val money = UtilTools.getMoney(mResultMap!!["reqmoney"]!!.toString() + "")
                mZongMoneyTv!!.text = "借款总额(元) $money"
                initStatus()
            }

            MethodUrl.daihouDetail -> {
                val mHasFile = tData["existFileList"] as List<MutableMap<String, Any>>?
                if (mHasFile == null || mHasFile.size == 0) {
                    showToastMsg("暂无借款用途凭证")
                } else {
                    val intent = Intent(this@BorrowDetailActivity, ModifyFileActivity::class.java)
                    //intent.putExtra("DATA", (Serializable) mHasFile);
                    DataHolder.instance!!.save("fileList", mHasFile)
                    startActivity(intent)
                }
            }
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.borrowDetail -> borrowDetail()
                    MethodUrl.daihouDetail -> getModifyAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        when (mType) {
            MethodUrl.borrowDetail//
            -> {
                mStatusImageView!!.visibility = View.GONE

                mPageView!!.showNetworkError()
            }
        }


        dealFailInfo(map, mType)
    }

    override fun reLoadingData() {
        borrowDetail()
    }

    fun showDialog() {
        val bas_in: BaseAnimatorSet
        val bas_out: BaseAnimatorSet
        bas_in = SlideBottomEnter()
        bas_in.duration(200)
        bas_out = SlideBottomExit()
        bas_out.duration(300)
        if (mEditDialog == null) {
            mEditDialog = object : BaseDialog(this, true){
                override fun onCreateView(): View {
                    val view = View.inflate(this@BorrowDetailActivity, R.layout.dialog_edit_reson, null)
                    initDialog(view)
                    return view
                }

                override fun setUiBeforShow() {

                }
            }
            mEditDialog!!.dimEnabled(true)
            mEditDialog!!.widthScale(1f)
            mEditDialog!!.showAnim(bas_in)
            mEditDialog!!.dismissAnim(bas_out)
            mEditDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
        } else {
            mEditDialog!!.showAtLocation(Gravity.BOTTOM, 0, 0)
        }
    }

    private fun initDialog(view: View) {
        mDSureBut = view.findViewById(R.id.submit_but)
        mDCancleBut = view.findViewById(R.id.cancle_but)
        mDEditText = view.findViewById(R.id.resuse_des_edit)
        mDCountText = view.findViewById(R.id.count)
        mDEditText!!.addTextChangedListener(mTextWatcher)
        setLeftCount()
        mDEditText!!.setSelection(mDEditText!!.length())
        mDSureBut!!.setOnClickListener { mEditDialog!!.dismiss() }

        mDCancleBut!!.setOnClickListener { mEditDialog!!.dismiss() }
    }


    /**
     * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
     *
     * @param c
     * @return
     */
    private fun calculateLength(c: CharSequence): Long {
        var len = 0.0
        for (i in 0 until c.length) {
            val tmp = c[i].toInt()
            if (tmp > 0 && tmp < 127) {
                //len++;
                len += 0.5
            } else {
                len++
            }
        }
        return Math.round(len)
    }

    /**
     * 刷新剩余输入字数,最大值新浪微博是140个字，人人网是200个字
     */
    private fun setLeftCount() {
        mDCountText!!.text = (MAX_COUNT - inputCount).toString()
    }
}
