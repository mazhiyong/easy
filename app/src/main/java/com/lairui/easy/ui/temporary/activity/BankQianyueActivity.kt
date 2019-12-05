package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.cardview.widget.CardView
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import java.io.Serializable
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import de.hdodenhof.circleimageview.CircleImageView

/**
 * 开户绑卡成功后签约网银
 */
class BankQianyueActivity : BasicActivity(), RequestView {

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
    @BindView(R.id.card_num_tv)
    lateinit var mCardNumTv: TextView
    @BindView(R.id.xieyi_checkbox)
    lateinit var mXieyiCheckbox: CheckBox
    @BindView(R.id.xieyi_tv)
    lateinit var mXieyiTv: TextView
    @BindView(R.id.xiyi_lay)
    lateinit var mXiyiLay: LinearLayout
    @BindView(R.id.but_next)
    lateinit var mButNext: Button
    @BindView(R.id.tiaoguo_tv)
    lateinit var mTiaoGuoTv: TextView
    @BindView(R.id.bank_name_tv)
    lateinit var mBankNameTv: TextView
    @BindView(R.id.bank_image_view)
    lateinit var mCircleImageView: CircleImageView
    @BindView(R.id.bank_lay)
    lateinit var mBankLay: CardView


    private var mRequestTag = ""

    private var mHeZuoMap: MutableMap<String, Any>? = HashMap()
    private var mAccid = ""


    override val contentView: Int
        get() = R.layout.activity_bank_qianyue

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        //        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.white), 60);

        mTitleText!!.text = resources.getString(R.string.but_open_wy)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mHeZuoMap = bundle.getSerializable("DATA") as MutableMap<String, Any>
            if (mHeZuoMap!!.containsKey("accid")) {
                mAccid = mHeZuoMap!!["accid"].toString() + ""
            } else if (mHeZuoMap!!.containsKey("crdno")) {
                mAccid = mHeZuoMap!!["crdno"].toString() + ""
            }
            mCardNumTv!!.text = UtilTools.getIDXing(mAccid)

            var bankName = mHeZuoMap!!["bankname"].toString() + ""
            if (UtilTools.empty(bankName)) {
                bankName = ""
            }
            mBankNameTv!!.text = bankName
        }

        Glide.with(this)
                .load(mHeZuoMap!!["logopath"]!!.toString() + "")
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        val bd = resource as BitmapDrawable
                        val bm = bd.bitmap

                        Palette.from(bm).maximumColorCount(10).generate(object : Palette.PaletteAsyncListener {
                            override fun onGenerated(palette: Palette?) {
                                val list = palette?.swatches
                                var colorSize = 0
                                lateinit var maxSwatch: Palette.Swatch
                                for (i in list!!.indices) {
                                    val swatch = list[i]
                                    if (swatch != null) {
                                        val population = swatch.population
                                        if (colorSize < population) {
                                            colorSize = population
                                            maxSwatch = swatch
                                        }
                                    }
                                }
                                if (maxSwatch != null) {
                                    mBankLay!!.setCardBackgroundColor(maxSwatch.rgb)
                                    mBankLay!!.background.alpha = (0.8 * 255).toInt()

                                }
                            }
                        })
                        mCircleImageView!!.setImageBitmap(bm)
                    }
                })

        mXieyiCheckbox!!.setOnCheckedChangeListener { buttonView, isChecked ->
            mButNext!!.isEnabled = isChecked
        }

        mButNext!!.isEnabled = mXieyiCheckbox!!.isChecked
        initXieyi()
    }


    /**
     * 服务协议的显示信息
     */
    private fun initXieyi() {

        val tip = "同意《电子银行客户服务条款》"
        var dian = tip.length
        var end = tip.length
        if (tip.contains("《") && tip.contains("》")) {
            dian = tip.indexOf("《")
            end = tip.indexOf("》") + 1
        } else {
            dian = tip.length
        }

        /* 用来标识在 Span 范围内的文本前后输入新的字符时是否把它们也应用这个效果
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括)
        Spanned.SPAN_INCLUSIVE_EXCLUSIVE(前面包括，后面不包括)
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE(前面不包括，后面包括)
        Spanned.SPAN_INCLUSIVE_INCLUSIVE(前后都包括)*/
        val ss = SpannableString(tip)
        ss.setSpan(TextSpanClick(false), dian, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.data_col)), 0, dian, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue1)), dian, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.data_col)), end, tip.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mXieyiTv!!.text = ss
        //添加点击事件时，必须设置
        mXieyiTv!!.movementMethod = LinkMovementMethod.getInstance()

    }


    /**
     * 二类户网银签约
     * {prestate=10, iscmp=1, patnList=[{patncode=NSHCSHZF, vaccid=1831216000013914, secstatus=2,
     * accid=6235559020000001270, zifangnme=上海支行, zifangbho=NCBK6621}]}
     */
    private fun qianyueAction() {
        if (mHeZuoMap == null) {
            mButNext!!.isEnabled = true
            return
        }

        mRequestTag = MethodUrl.erLeihuQianYue

        val map = HashMap<String, Any>()
        map["patncode"] = mHeZuoMap!!["patncode"]!!.toString() + ""
        map["crdno"] = mAccid
        val mHeadermap = HashMap<String, String>()
        LogUtil.i("签约网银参数", map)
        mRequestPresenterImp!!.requestPostToMap(mHeadermap, MethodUrl.erLeihuQianYue, map)
    }


    private fun getMsgCodeAction() {

        mRequestTag = MethodUrl.normalSms
        val map = HashMap<String, Any>()
        //开通网银
        map["busi"] = "COMMON"

        val mHeaderMap = HashMap<String, String>()
        //mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.resetPassCode, map);
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.normalSms, map)
    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay, R.id.tiaoguo_tv)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> {
                mButNext!!.isEnabled = false
                if (!mXieyiCheckbox!!.isChecked) {
                    showToastMsg("请先阅读协议")
                    mButNext!!.isEnabled = true
                    return
                }
                getMsgCodeAction()
            }
            R.id.tiaoguo_tv -> finish()
        }//qianyueAction();
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
            MethodUrl.normalSms -> {
                mButNext!!.isEnabled = true
                showToastMsg("获取验证码成功")
                intent = Intent(this@BankQianyueActivity, CodeMsgActivity::class.java)
                intent.putExtra("DATA", tData as Serializable)
                intent.putExtra(MbsConstans.CodeType.CODE_KEY, MbsConstans.CodeType.CODE_WANGYIN)
                intent.putExtra("showPhone", MbsConstans.USER_MAP!!["tel"]!!.toString() + "")
                startActivityForResult(intent, 1)
            }
            MethodUrl.erLeihuQianYue//二类户网银签约
            -> {
                showToastMsg(tData["result"]!!.toString() + "")
                intent = Intent()
                intent.action = MbsConstans.BroadcastReceiverAction.QIAN_YUE_WY
                sendBroadcast(intent)
                finish()
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.erLeihuQianYue -> qianyueAction()
                    MethodUrl.normalSms -> getMsgCodeAction()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        mButNext!!.isEnabled = true
        dealFailInfo(map, mType)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data)
        val intent: Intent
        val bundle: Bundle
        if (requestCode == 1) {
            when (resultCode) {
                //通过短信验证码  安装证书
                MbsConstans.CodeType.CODE_WANGYIN -> qianyueAction()
            }

        }
    }


    private inner class TextSpanClick(private val status: Boolean) : ClickableSpan() {

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false//取消下划线false
        }

        override fun onClick(v: View) {
            val intent = Intent(this@BankQianyueActivity, HtmlActivity::class.java)
            intent.putExtra("id", MbsConstans.XIEYI_URL + "H5/static/html/wyxy.html")
            intent.putExtra("title", "网银协议")
            //intent.putExtra("id",MbsConstans.XIEYI_URL+"H5/static/html/khxy.html?name="+MbsConstans.USER_MAP.get("name"));

            startActivity(intent)
        }
    }

}
