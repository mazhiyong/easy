package com.lairui.easy.ui.temporary.activity

import android.content.Intent
import android.net.Uri
import android.os.Build

import androidx.core.content.ContextCompat
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import com.lairui.easy.R
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil

import butterknife.BindView
import butterknife.OnClick

class HtmlActivity : BasicActivity() {


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
    @BindView(R.id.pb)
    lateinit var mPb: ProgressBar
    @BindView(R.id.cotent_webView)
    lateinit var mCotentWebView: WebView

    private var id: String? = ""

    private val mHtmlContent = ""
    private var mUrl: String? = ""
    var mTitle: String? = ""


    private var mCanBack = true

    private var mShowType: String? = ""

    override val contentView: Int
        get() = R.layout.activity_html_detail

    override fun init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {

            id = bundle.getString("id")
            mTitle = bundle.getString("title")
            mShowType = bundle.getString("TYPE")
            if (mShowType == null) {
                mShowType = ""
            }
        }


        mBackImg!!.setImageResource(R.drawable.aud)
        mBackText!!.text = "关闭"
        mBackText!!.setTextColor(ContextCompat.getColor(this, R.color.black22))

        /**
         * 设置是否仅仅跟踪左侧边缘的滑动返回
         * 因为银行卡里面有嵌套 viewpager  所以要边界返回
         */
        mSwipeBackHelper!!.setIsOnlyTrackingLeftEdge(true)

        /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mCotentWebView.getSettings().setSafeBrowsingEnabled(false);
        }*/


        //mUrl = MbsConstans.SERVER_URL + MethodUrl.pdfUrl + "?path=" + id;
        //mUrl = "http://172.16.1.216:8089/#/login";
        //mUrl = "http://www.baidu.com";

        mUrl = id
        if (mShowType == "1") {
            mUrl = "file:///android_asset/dr_about.html"
        }


        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)


        mPb.max = 100

        mCotentWebView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY

        mCotentWebView.settings.javaScriptEnabled = true   //设置支持Javascript
        mCotentWebView.settings.javaScriptCanOpenWindowsAutomatically = true
        mCotentWebView.settings.setSupportMultipleWindows(true)
        mCotentWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE


        mCotentWebView.settings.domStorageEnabled = true
        mCotentWebView.settings.setAppCacheMaxSize((1024 * 1024 * 8).toLong())
        val appCachePath = applicationContext.cacheDir.absolutePath
        mCotentWebView.settings.setAppCachePath(appCachePath)
        mCotentWebView.settings.allowFileAccess = true
        mCotentWebView.settings.setAppCacheEnabled(true)


        mCotentWebView.settings.setSupportZoom(true)
        //mCotentWebView.getSettings().setPluginsEnabled(true);
        mCotentWebView.settings.useWideViewPort = true
        mCotentWebView.settings.builtInZoomControls = true
        mCotentWebView.requestFocus()
        //mCotentWebView.getSettings().setLoadWithOverviewMode(true);
        //mCotentWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        mCotentWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {


                mPb.visibility = View.VISIBLE
                mPb.progress = newProgress
                if (newProgress == 100) {
                    mPb.visibility = View.GONE
                }
                super.onProgressChanged(view, newProgress)
            }

            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                Log.e("ANDROID_LAB", "TITLE=$title")
                if (UtilTools.empty(title)) {
                    mTitleText.text = title
                } else {
                    mTitleText.text = mTitle
                }
            }

        }

        //mContentWebView.loadUrl("http://10.10.10.182/html5Test/pages/demo/demo2.html");
        //设置Web视图
        //达到禁用国产机中点击数据进行拨号的bug
        mCotentWebView!!.webViewClient = object : WebViewClient() {

            /* @RequiresApi(api = Build.VERSION_CODES.N)
             public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                 if (request.isRedirect()) {
                     view.loadUrl(request.getUrl().toString());
                     return true;
                 }
                 return false;
             }*/
            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {

                val hitTestResult = view.hitTestResult

                if (hitTestResult != null) {
                    return false
                }
                //当有新连接时，使用当前的 WebView
                if (url != null && "tel" == url.substring(0, 3)) {
                    //String phoneNumber = url.substring(4, 14);
                    //url = "tel:" + "1" + phoneNumber;
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    this@HtmlActivity.startActivity(intent)
                } else {
                    if (url != null && url.startsWith("http")) {


                        //view.loadUrl(url);
                        // 如果想继续加载目标页面则调用下面的语句
                        // 如果不想那url就是目标网址，如果想获取目标网页的内容那你可以用HTTP的API把网页扒下来。
                        // 返回true表示停留在本WebView（不跳转到系统的浏览器）
                        //Android8.0以下的需要返回true 并且需要loadUrl；8.0之后效果相反
                        if (Build.VERSION.SDK_INT < 26) {
                            view.loadUrl(url)
                            return true
                        } else {
                            return false
                        }

                    }
                }
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (url.contains("registerSuccess")) {
                    mCanBack = false
                } else {
                    mCanBack = true
                }
            }
        }

        if (mShowType == "1") {
            mCotentWebView!!.loadUrl(mUrl)
        } else {
            urlData()
        }


    }


    private fun contentData() {
        mCotentWebView!!.loadDataWithBaseURL(null, mHtmlContent, "text/html", "utf-8", null)
        //mContentWebView.loadUrl(mUrl);
    }

    private fun urlData() {

        LogUtil.i("--------htmlActivity----------------", mUrl!!)
        if (mUrl!!.startsWith("http")) {
            mCotentWebView!!.loadUrl(mUrl)
        } else {
            mCotentWebView!!.loadDataWithBaseURL(null, mUrl, "text/html", "utf-8", null)
        }


    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mCotentWebView!!.canGoBack() && mCanBack) {
            mCotentWebView!!.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            mCotentWebView!!.goBack()// 返回前一个页面
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    @OnClick(R.id.back_img, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()
        }
    }


    override fun onDestroy() {
        if (mCotentWebView != null) {

            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            val parent = mCotentWebView!!.parent
            if (parent != null) {
                (parent as ViewGroup).removeView(mCotentWebView)
            }

            mCotentWebView!!.stopLoading()
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mCotentWebView!!.settings.javaScriptEnabled = false
            mCotentWebView!!.clearHistory()
            mCotentWebView!!.clearView()
            mCotentWebView!!.removeAllViews()
            mCotentWebView!!.destroy()

        }
        super.onDestroy()
    }


    override fun showProgress() {

    }

    override fun disimissProgress() {

    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {

    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {

    }
}
