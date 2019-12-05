package com.lairui.easy.ui.temporary.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Handler
import android.os.Message
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog

import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.jaeger.library.StatusBarUtil

import butterknife.BindView
import butterknife.OnClick

class PDFLookActivity : BasicActivity() {

    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.webView)
    lateinit var mWebView: WebView
    @BindView(R.id.pb_bar)
    lateinit var mPbBar: ProgressBar
    @BindView(R.id.linearLayout1)
    lateinit var mLinearLayout1: LinearLayout
    @BindView(R.id.load_imageview)
    lateinit var mLoadImageView: ImageView
    @BindView(R.id.right_img)
    lateinit var mRightImg: ImageView
    @BindView(R.id.right_lay)
    lateinit var mRightLay: LinearLayout

    private var id: String? = ""
    private var mUrl = ""


    override val contentView: Int
        get() = R.layout.activity_pdf

    private val mWebViewHandler = @SuppressLint("HandlerLeak")
    object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {
                1 -> {
                    mLinearLayout1!!.visibility = View.GONE
                    stopAnim()
                }
                2 -> {
                    mLinearLayout1!!.visibility = View.GONE
                    stopAnim()
                    AlertDialog.Builder(this@PDFLookActivity)
                            .setCancelable(false)
                            .setOnKeyListener { dialog, keyCode, event ->
                                if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
                                    dialog.dismiss()
                                    finish()
                                    true
                                } else {
                                    false
                                }
                            }
                            .setTitle(R.string.title_dialog)
                            .setMessage("未能解析此文件，请稍后重试")
                            .setPositiveButton(R.string.but_sure) { dialog, which -> finish() }
                            .show()
                }
            }

        }
    }


    lateinit var aniDrawable: AnimationDrawable


    override fun init() {

        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("id")
        }
        if (id!!.contains("http")) {
            mUrl = id as String
        } else {
            mUrl = MbsConstans.SERVER_URL + MethodUrl.pdfUrl + "?path=" + id
        }

        /**
         * 设置是否仅仅跟踪左侧边缘的滑动返回
         * 因为银行卡里面有嵌套 viewpager  所以要边界返回
         */
        mSwipeBackHelper!!.setIsOnlyTrackingLeftEdge(true)

        mTitleText!!.text = "查看协议"
        mRightImg!!.setImageResource(R.drawable.shared)
        // mRightLay.setVisibility(View.VISIBLE);
        //mRightImg.setVisibility(View.VISIBLE);
        initView()
        LogUtil.i("pdfUrl", mUrl)
        preView(mUrl)

        initAnimation()
        mLinearLayout1!!.visibility = View.VISIBLE
        startAnim()
    }

    protected fun initView() {
        val webSettings = mWebView!!.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = true
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true


        //------------------------------------------------------------------------------------------------
        //把本类的一个实例添加到js的全局对象window中，
        //这样就可以使用window.injs来调用它的方法
        mWebView!!.addJavascriptInterface(InJavaScript(), "injs")
        //达到禁用国产机中点击数据进行拨号的bug
        mWebView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                //当有新连接时，使用当前的 WebView
                if (url != null && url.startsWith("http")) {
                    //view.loadUrl(url);
                    val uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
                return true
            }
        }
        //设置支持JavaScript脚本
        webSettings.javaScriptEnabled = true
        //设置可以访问文件
        webSettings.allowFileAccess = true
        mWebView!!.isFocusable = false
        mWebView!!.isFocusableInTouchMode = false
        //------------------------------------------------------------------------------------------------

        //支持javascript
        mWebView!!.settings.javaScriptEnabled = true
        // 设置可以支持缩放
        mWebView!!.settings.setSupportZoom(true)
        // 设置出现缩放工具
        mWebView!!.settings.builtInZoomControls = true
        //扩大比例的缩放
        mWebView!!.settings.useWideViewPort = true
        //自适应屏幕
        mWebView!!.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        mWebView!!.settings.loadWithOverviewMode = true


        mWebView!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {

                mPbBar!!.visibility = View.VISIBLE
                mPbBar!!.progress = newProgress
                if (newProgress == 100) {
                    mPbBar!!.visibility = View.GONE
                }
                super.onProgressChanged(view, newProgress)
            }

            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                //Log.e("ANDROID_LAB", "TITLE=" + title);
                LogUtil.i("ANDROID_LAB", "TITLE=$title")
                //mTitleView.setText(title);
            }

        }

    }

    /**
     * 预览pdf
     *
     * @param pdfUrl url或者本地文件路径
     */
    private fun preView(pdfUrl: String) {
        //1.只使用pdf.js渲染功能，自定义预览UI界面
        mWebView!!.loadUrl("file:///android_asset/index.html?$pdfUrl")
        //        mWebView.loadUrl("file:///android_asset/index.html?https://gagayi.oss-cn-beijing.aliyuncs.com/video/D57C71A83521E12EFD9334B6C27AE092.pdf" );
        //2.使用mozilla官方demo加载在线pdf
        //        mWebView.loadUrl("http://mozilla.github.io/pdf.js/web/viewer.html?file=" + pdfUrl);
        //3.pdf.js放到本地
        //        mWebView.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + pdfUrl);
        //4.使用谷歌文档服务
        //        mWebView.loadUrl("http://docs.google.com/gviewembedded=true&url=" + pdfUrl);
    }


    //在java中调用js代码 传参
    fun sendInfoToJs() {
        //调用js中的函数：showInfoFromJava(msg)
        mWebView!!.loadUrl("javascript:getFromAndroid('23546456')")
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


     inner class InJavaScript {
        @JavascriptInterface
        fun runOnAndroidJavaScript(str: String, type: Int) {

            //可以请求后台，根据需要做相关的操作
            Thread(Runnable {
                // 请求的信息，从后台得到信息;
                val msg = Message()
                msg.what = type
                mWebViewHandler.sendMessage(msg)
            }).start()
        }

    }


    @OnClick(R.id.back_img, R.id.left_back_lay, R.id.right_img)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
        }// sendInfoToJs();
    }

    private fun initAnimation() {
        aniDrawable = mLoadImageView!!.drawable as AnimationDrawable
    }

    fun startAnim() {
        aniDrawable!!.start()
    }

    fun stopAnim() {
        aniDrawable!!.stop()
    }


    override fun onDestroy() {
        if (mWebView != null) {

            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            val parent = mWebView!!.parent
            if (parent != null) {
                (parent as ViewGroup).removeView(mWebView)
            }

            mWebView!!.stopLoading()
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mWebView!!.settings.javaScriptEnabled = false
            mWebView!!.clearHistory()
            mWebView!!.clearView()
            mWebView!!.removeAllViews()
            mWebView!!.destroy()

        }
        super.onDestroy()
    }


}
