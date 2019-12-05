package com.lairui.easy.ui.temporary.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.annotation.StringRes
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
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mywidget.view.BankCardTextWatcher
import com.jaeger.library.StatusBarUtil





import java.io.Serializable
import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import de.hdodenhof.circleimageview.CircleImageView

/**
 * 开户协议 设置密码  界面
 */
class BankOpenXieyiActivity : BasicActivity(), RequestView {

    private val TAG = "BankOpenXieyiActivity"

    @BindView(R.id.card_num_tv)
   lateinit var mCardNumTv: EditText
    @BindView(R.id.xieyi_tv)
   lateinit var mXieyiTv: TextView
    @BindView(R.id.view1)
   lateinit var mView1: TextView
    @BindView(R.id.but_next)
   lateinit var mButNext: Button
    @BindView(R.id.containerLayout)
   lateinit var mContainerLayout: LinearLayout
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
    @BindView(R.id.bank_name_tv)
   lateinit var mBankNameTv: TextView
   lateinit var mClear1: ImageView
   lateinit var mClear2: ImageView
    @BindView(R.id.xieyi_checkbox)
   lateinit var mXieyiCheckBox: CheckBox

    @BindView(R.id.bank_image_view)
   lateinit var mBankImageView: CircleImageView
    @BindView(R.id.bank_bg_lay)
   lateinit var mBankBgView: CardView
    @BindView(R.id.bank_open_toptips_tv)
   lateinit var mBankOpenTipTv: TextView


    private var mRequestTag = ""

    private var mServerRandom = ""

    private var mAccno: String? = ""
    private var mPatncode: String? = ""
    private var mOpnbnknm: String? = ""
    private var mOpnbnkid: String? = ""
    private var mLogoPath: String? = ""

    private val mClientPass = ""
    private val mClientRandom = ""

    private var mResultPass = ""

    private var mErLeiHuCard = ""
    private var mErLeiHuLogo = ""
    private var mErLeiBankName = ""

    override val contentView: Int
        get() = R.layout.activity_bank_open_xieyi

    private val offset = 0


    private var isCheck = false


    @SuppressLint("HandlerLeak")
    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            when (msg.what) {
                1 -> enterNextPage()
                2 -> {
                    showToastMsg("人脸验证授权失败")
                    mButNext!!.isEnabled = true
                }
            }
        }
    }

    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)
        //      StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.white), 60);

        mTitleText!!.text = resources.getString(R.string.bank_card_open_title)


        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            mAccno = bundle.getString("accno")
            mPatncode = bundle.getString("patncode")
            mOpnbnknm = bundle.getString("opnbnknm")
            mOpnbnkid = bundle.getString("opnbnkid")
            mLogoPath = bundle.getString("logopath")
        }

        /* mPatncode = "PHNM2018";
        mAccno = "13245546546546";*/

        Glide.with(this)
                .load(mLogoPath!! + "")
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        val bd = resource as BitmapDrawable
                        val bm = bd.bitmap
                        val p = createPaletteSync(bm)
                        //Palette.Swatch s = p.getVibrantSwatch();       //获取到充满活力的这种色调
                        //Palette.Swatch s = p.getDarkVibrantSwatch();    //获取充满活力的黑
                        //Palette.Swatch s = p.getLightVibrantSwatch();   //获取充满活力的亮
                        //Palette.Swatch s = p.getMutedSwatch();           //获取柔和的色调
                        //Palette.Swatch s = p.getDarkMutedSwatch();      //获取柔和的黑
                        //Palette.Swatch s = p.getLightMutedSwatch();    //获取柔和的亮
                        val s = p.vibrantSwatch
                        if (s != null) {
                            //设置背景颜色
                            //  mBankBgView.setCardBackgroundColor(s.getRgb());

                        } else {
                        }


                        Palette.from(bm).maximumColorCount(10).generate(object : Palette.PaletteAsyncListener {
                            /**
                             * Called when the [Palette] has been generated. `null` will be passed when an
                             * error occurred during generation.
                             */
                            override fun onGenerated(palette: Palette?) {
                                val list = palette?.swatches
                                var colorSize = 0
                                lateinit var maxSwatch: Palette.Swatch
                                for (i in list!!.indices) {
                                    val swatch = list.get(i)
                                    if (swatch != null) {
                                        val population = swatch.population
                                        if (colorSize < population) {
                                            colorSize = population
                                            maxSwatch = swatch
                                        }
                                    }
                                }
                                if (maxSwatch != null) {
                                    mBankBgView!!.setCardBackgroundColor(maxSwatch.rgb)
                                    mBankBgView!!.background.alpha = (0.8 * 255).toInt()
                                }
                            }
                        })


                        mBankImageView!!.setImageBitmap(bm)
                    }
                })

        initView()

        mCardNumTv?.let { BankCardTextWatcher.bind(it) }

        mCardNumTv!!.isFocusable = true
        mCardNumTv!!.requestFocus()
        mCardNumTv!!.isEnabled = false
        //mCardNumTv.setText(UtilTools.getIDXing(mAccno));
        mCardNumTv!!.setText(mAccno)
        mBankNameTv!!.text = mOpnbnknm

        val topXml = resources.getString(R.string.bank_open_top_tips)
        val topStr = String.format(topXml, mOpnbnknm)
        mBankOpenTipTv!!.text = topStr

        initXieyi()
        serverRandomAction()
    }

    /**
     *
     * @param bitmap
     * @return
     */
    fun createPaletteSync(bitmap: Bitmap): Palette {
        return Palette.from(bitmap).generate()
    }

    private fun initView() {


    }


    /**
     * 服务协议的显示信息
     */
    private fun initXieyi() {

        val tip = "同意《电子账户服务协议》"
        var dian = tip.length
        if (tip.contains("《")) {
            dian = tip.indexOf("《")
        } else {
            dian = tip.length
        }

        /* 用来标识在 Span 范围内的文本前后输入新的字符时是否把它们也应用这个效果
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括)
        Spanned.SPAN_INCLUSIVE_EXCLUSIVE(前面包括，后面不包括)
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE(前面不包括，后面包括)
        Spanned.SPAN_INCLUSIVE_INCLUSIVE(前后都包括)*/
        val ss = SpannableString(tip)
        ss.setSpan(TextSpanClick(false), dian, tip.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.white5)), 0, dian, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.white)), dian, tip.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mXieyiTv!!.text = ss
        //添加点击事件时，必须设置
        mXieyiTv!!.movementMethod = LinkMovementMethod.getInstance()

    }


    /**
     * 服务器端随机数
     */
    private fun serverRandomAction() {
        mRequestTag = MethodUrl.serverRandom
        val map = HashMap<String, String>()
        val mHeadermap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeadermap, MethodUrl.serverRandom, map)
    }

    /**
     * 转加密获得密码
     */
    private fun passJiaMi() {
        mRequestTag = MethodUrl.erLeihuPass

        val map = HashMap<String, Any>()
        map["patncode"] = mPatncode!!
        map["clientRandom"] = mClientRandom
        map["serverRandom"] = mServerRandom
        map["password"] = mClientPass
        val mHeadermap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeadermap, MethodUrl.erLeihuPass, map)
    }

    /**
     * 二类户开户提交信息
     */
    private fun erleihuAction() {
        mRequestTag = MethodUrl.erLeihuPassOpen

        val map = HashMap<String, Any>()
        map["patncode"] = mPatncode!!
        map["crdpswd"] = mResultPass
        map["accno"] = mAccno!!
        map["opnbnkid"] = mOpnbnkid!!
        map["opnbnknm"] = mOpnbnknm!!
        val mHeadermap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeadermap, MethodUrl.erLeihuPassOpen, map)
    }

    /**
     * 二类户开户成功后  开始绑卡
     */
    private fun erleihuBind() {
        mRequestTag = MethodUrl.erleiHuBind

        val map = HashMap<String, Any>()
        map["patncode"] = mPatncode!!
        map["crdno"] = mErLeiHuCard
        val mHeadermap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeadermap, MethodUrl.erleiHuBind, map)
    }


    @OnClick(R.id.back_img, R.id.but_next, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
            R.id.but_next -> {
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
        when (mType) {
            MethodUrl.serverRandom//服务器端随机数
            -> mServerRandom = tData["serverRandom"]!!.toString() + ""
            MethodUrl.erLeihuPass//二类户密码加密
            -> {
                mResultPass = tData["pinPassword"]!!.toString() + ""
                LogUtil.i("南商行密码控件", "转加密成功")
                erleihuAction()
            }
            MethodUrl.erLeihuPassOpen//提交最后开户信息
            -> {
                showToastMsg("开户成功")
                mErLeiHuCard = tData["crdno"]!!.toString() + ""
                mErLeiHuLogo = tData["logopath"]!!.toString() + ""
                mErLeiBankName = tData["bankname"]!!.toString() + ""
                erleihuBind()
            }
            MethodUrl.erleiHuBind -> {
                showToastMsg("绑卡成功")
                val intent2 = Intent()
                intent2.action = MbsConstans.BroadcastReceiverAction.OPEN_BANK
                sendBroadcast(intent2)

                val intent = Intent(this@BankOpenXieyiActivity, BankQianyueActivity::class.java)
                val erLeihuBank = HashMap<String, Any>()
                erLeihuBank["patncode"] = mPatncode!!
                erLeihuBank["crdno"] = mErLeiHuCard
                erLeihuBank["logopath"] = mErLeiHuLogo
                erLeihuBank["bankname"] = mErLeiBankName
                intent.putExtra("DATA", erLeihuBank as Serializable)
                startActivity(intent)
                finish()
            }
            MethodUrl.refreshToken -> {
                MbsConstans.REFRESH_TOKEN = tData["refresh_token"]!!.toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.serverRandom -> serverRandomAction()
                    MethodUrl.erLeihuPassOpen -> erleihuAction()
                    MethodUrl.erLeihuPass -> passJiaMi()
                    MethodUrl.erleiHuBind -> erleihuBind()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

        dealFailInfo(map, mType)
    }


    private inner class TextSpanClick(private val status: Boolean) : ClickableSpan() {



        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false//取消下划线false
        }

        override fun onClick(v: View) {
            val intent = Intent(this@BankOpenXieyiActivity, HtmlActivity::class.java)
            var name = ""
            if (MbsConstans.USER_MAP == null) {
                name = ""
            } else {
                name = MbsConstans.USER_MAP!!["name"]!!.toString() + ""
            }
            LogUtil.i("开户人姓名", name)
            intent.putExtra("title", "开户协议")
            intent.putExtra("id", MbsConstans.XIEYI_URL + "H5/static/html/khxy.html?name=" + name)
            startActivity(intent)
        }
    }

    private fun enterNextPage() {
        //startActivityForResult(Intent(this, LivenessActivity::class.java), PAGE_INTO_LIVENESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SETTING -> {
                Toast.makeText(this@BankOpenXieyiActivity, R.string.message_setting_comeback, Toast.LENGTH_SHORT).show()
            }
        }
        var intent: Intent
        val bundle: Bundle?
        if (requestCode == 1) {
            when (resultCode) {
                //
                MbsConstans.FaceType.FACE_CHECK_BANK_PASS -> {
                    bundle = data!!.extras
                    isCheck = bundle != null
                    mButNext!!.isEnabled = true
                }
                else -> mButNext!!.isEnabled = true
            }

        } else if (requestCode == PAGE_INTO_LIVENESS) {//人脸识别返回来的数据
            if (resultCode == Activity.RESULT_OK) {
                bundle = data!!.extras
                bundle!!.putInt(MbsConstans.FaceType.FACE_KEY, MbsConstans.FaceType.FACE_CHECK_BANK_PASS)
                intent = Intent(this@BankOpenXieyiActivity, ResultActivity::class.java)
                intent.putExtras(bundle)
                //设置返回数据
                startActivityForResult(intent, 1)
            } else {
                mButNext!!.isEnabled = true
            }
        }
    }

    /**
     * 联网授权
     */
    private fun netWorkWarranty() {
/*
        val uuid = ConUtil.getUUIDString(this)

        Thread(Runnable {
            val manager = Manager(this@BankOpenXieyiActivity)
            val licenseManager = LivenessLicenseManager(this@BankOpenXieyiActivity)
            manager.registerLicenseManager(licenseManager)
            manager.takeLicenseFromNetwork(uuid)
            if (licenseManager.checkCachedLicense() > 0) {
                //授权成功
                mHandler.sendEmptyMessage(1)
            } else {
                //授权失败
                mHandler.sendEmptyMessage(2)
            }
        }).start()*/
    }


    protected fun toast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val PAGE_INTO_LIVENESS = 101


        private val REQUEST_CODE_SETTING = 10011
    }

}
