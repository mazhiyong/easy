package com.lairui.easy.basic
import android.app.Activity
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.lairui.easy.R
import com.lairui.easy.di.component.DaggerPersenerComponent
import com.lairui.easy.di.module.PersenerModule
import com.lairui.easy.manage.ActivityManager
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.ui.temporary.activity.ChoosePhoneActivity
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.api.ErrorHandler
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.api.RxApiManager
import com.lairui.easy.mywidget.dialog.TipMsgDialog
import com.lairui.easy.mvp.presenter.RequestPresenterImp
import com.lairui.easy.mywidget.view.LoadingWindow
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.utils.tool.LogUtil
import com.jaeger.library.StatusBarUtil

import java.util.ArrayList
import java.util.HashMap

import javax.inject.Inject

import butterknife.ButterKnife
import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper


abstract class BasicActivity : AppCompatActivity(), BGASwipeBackHelper.Delegate, RequestView {
    // 装载Activity
    private var activityManager: ActivityManager? = null
    var mIsRefresh = false
    private var i = 0

    private var mLoadingWindow: LoadingWindow? = null


    var mRequestTagList: MutableList<String> = ArrayList()


    var mIsRefreshToken = false

    @Inject
    lateinit var mRequestPresenterImp: RequestPresenterImp

    abstract val contentView: Int

    protected  var mSwipeBackHelper: BGASwipeBackHelper ?= null


    var mZhangDialog: TipMsgDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if(savedInstanceState == null){
            super.onCreate(Bundle())
        }else{
            super.onCreate(savedInstanceState)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            MbsConstans.ALPHA = 100
            MbsConstans.TOP_BAR_COLOR = R.color.top_bar_bg
        } else {
            MbsConstans.ALPHA = 0
            MbsConstans.TOP_BAR_COLOR = R.color.top_bar_bg
        }

        initSwipeBackFinish()
        //super.onCreate(savedInstanceState)
        setContentView(contentView)


        //StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.yellow), 0);
        ButterKnife.bind(this)
        //IntentFilter filter = new IntentFilter();
        //filter.addAction(MbsConstans.BroadcastReceiverAction.NET_CHECK_ACTION);
        //registerReceiver(receiver, filter);

        //		new Handler().post(new Runnable() {
        //			@Override
        //			public void run() {

        activityManager = ActivityManager.instance
        //压入当前Activity
        activityManager!!.pushActivity(this@BasicActivity)
        DaggerPersenerComponent.builder().persenerModule(PersenerModule(this, this)).build().injectTo(this)

        //			}
        //		});
        init()
        setBarTextColor()
    }

    open fun setBarTextColor() {
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            StatusBarUtil.setDarkMode(this);
        } else {
            StatusBarUtil.setLightMode(this);
        }*/

        StatusBarUtil.setLightMode(this)
    }

    abstract fun init()


    open fun viewEnable() {

    }

    /**
     * 内存不够时
     * @param level
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            //开始自杀，清场掉所有的activity
            //下面这个是自己写的方法　　
            closeAllActivity()
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(0)
        }
    }

    fun getMsgCodeAction(mRequestPresenterImp: RequestPresenterImp) {
        val map = HashMap<String, Any>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.resetPassCode, map)
    }

    /**
     * 初始化滑动返回。在 super.onCreate(savedInstanceState) 之前调用该方法
     */
    private fun initSwipeBackFinish() {
        mSwipeBackHelper = BGASwipeBackHelper(this, this)

        // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackManager.getmContext().init(this) 来初始化滑动返回」
        // 下面几项可以不配置，这里只是为了讲述接口用法。

        // 设置滑动返回是否可用。默认值为 true
        mSwipeBackHelper?.setSwipeBackEnable(true)
        // 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
        mSwipeBackHelper?.setIsOnlyTrackingLeftEdge(false)
        // 设置是否是微信滑动返回样式。默认值为 true
        mSwipeBackHelper?.setIsWeChatStyle(false)
        // 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
        mSwipeBackHelper?.setShadowResId(R.drawable.bga_sbl_shadow)
        // 设置是否显示滑动返回的阴影效果。默认值为 true
        mSwipeBackHelper?.setIsNeedShowShadow(true)
        // 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
        mSwipeBackHelper?.setIsShadowAlphaGradient(true)
        // 设置触发释放后自动滑动返回的阈值，默认值为 0.3f
        mSwipeBackHelper?.setSwipeBackThreshold(0.3f)
    }

    private fun addInternetView() {
        //		View view = LayoutInflater.from(this).inflate(R.layout.internet_set_view,null);
        //		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //		layoutParams.setMargins(0, (int)getResources().getDimension(R.dimen.title_item_height) + UtilTools.getStatusHeight2(this), 0, 0);
        //		view.setLayoutParams(layoutParams);
    }

    /**
     * 显示dialog
     */
    fun showProgressDialog() {
        if (mLoadingWindow == null) {
            mLoadingWindow = LoadingWindow(this, R.style.Dialog)
            mLoadingWindow!!.setOnDismissListener { viewEnable() }
        }
        mLoadingWindow!!.showView()
    }

    /**
     * 显示dialog
     */
    fun showProgressDialog(text: String) {
        if (mLoadingWindow == null) {
            mLoadingWindow = LoadingWindow(this, R.style.Dialog)
            mLoadingWindow!!.setOnDismissListener { viewEnable() }

        }
        mLoadingWindow!!.setTipText(text)
        mLoadingWindow!!.showView()
    }


    /**
     * 隐藏dialog
     */
    fun dismissProgressDialog() {
        if (mLoadingWindow != null && mLoadingWindow!!.isShowing) {
            mLoadingWindow!!.cancleView()
        }
    }

    override fun onPause() {
        super.onPause()
    }


    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
        activityManager!!.removeActivity(this)
        RxApiManager.get()!!.cancel(this)
        RxApiManager.get()!!.cancelActivityAll(this)
    }


    /**
     * 返回到指定Activity
     * @param clazz        返回到的Activity
     */
    fun backToAndAutoLogin(clazz: Class<*>, isAutoLogin: Boolean) {
        activityManager!!.backTo(clazz, false)
        BasicActivity.isAutoLogin = isAutoLogin
    }

    /**
     * 返回到指定Activity
     * @param clazz        返回到的Activity
     * @param isRefresh    是否刷新数据
     */
    fun backTo(clazz: Class<*>, isRefresh: Boolean) {
        activityManager!!.backTo(clazz, isRefresh)
    }

    fun backToMainActivity(clazz: Class<*>, i: Int) {
        activityManager!!.backToMainActivity(clazz, i)
    }

    fun closeAllActivity() {
        activityManager!!.close()
    }

    /**
     * 返回到指定Activity
     * @param clazz        返回到的Activity
     */
    fun backTo(context: Context, clazz: Class<*>, resId: Int) {
        activityManager!!.backTo(clazz, false)
        //BasicActivity.isMsg = true;
        //BasicActivity.resId = resId;
        //showToastMsg(context, resId);
    }

    /**
     * 提示消息
     * @param resId
     */
    fun showToastMsg(resId: Int) {
        mHandler.post {
            //Toast.makeText(BasicActivity.this, resId, Toast.LENGTH_SHORT).show();
            showTips(resId)
        }
    }

    /**
     * 提示消息
     */
    fun showToastMsg(msg: String) {
        //        mHandler.post(new Runnable(){
        //            @Override
        //            public void run() {
        //Toast.makeText(BasicActivity.this, msg, Toast.LENGTH_SHORT).show();
        showTips(msg)
        //            }
        //        });
    }

    private fun showTips(iconResId: Int, msgResId: Int) {
        val tipsToast = TipsToast.makeText(msgResId,Toast.LENGTH_LONG)
        tipsToast.show()
        tipsToast.setIcon(iconResId)
        tipsToast.setText(msgResId)
    }

    private fun showTips(msgResId: Int) {
        val tipsToast = TipsToast.makeText(msgResId,Toast.LENGTH_SHORT)
        tipsToast.show()
        //tipsToast.setIcon(iconResId);
        tipsToast.setText(msgResId)
    }

    private fun showTips(msgResId: String) {
        val tipsToast = TipsToast.makeText(msgResId, Toast.LENGTH_LONG)
        tipsToast.show()
        //tipsToast.setIcon(iconResId);
        tipsToast.setText(msgResId)
    }


    /**
     * 按键监听
     * @return
     * @see Activity.onKeyDown
     */
    override fun onBackPressed() {
        // 正在滑动返回的时候取消返回按钮事件
        if (mSwipeBackHelper?.isSliding!!) {
            return
        }
        mSwipeBackHelper?.backward()
    }

    protected fun popActivity(activity: Activity) {
        activityManager!!.popActivity(activity)
    }

    protected fun closeActivity() {
        activityManager!!.close()
    }

    fun getActivityManager(): ActivityManager? {
        if (activityManager == null)
            activityManager = ActivityManager.instance
        return this.activityManager
    }

    fun setActivityManager(activityManager: ActivityManager) {
        var activityManager = activityManager
        activityManager = activityManager
    }

    fun isActivityOnTop(clazz: Class<*>): Boolean {
        val ps = clazz.name
        return isActivityOnTop(ps)
    }

    fun isActivityOnTop(clazz: String): Boolean {
        val mActivity = activityManager!!.peepActivity()

        if (mActivity != null) {
            if ((mActivity.packageName + "." + mActivity.localClassName).equals(clazz, ignoreCase = true))
                return true
        }
        return false
    }

    fun popActivity() {
        activityManager!!.popActivity()
    }

    /**
     * activity销毁前触发的方法
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
    }

    /**
     * activity恢复时触发的方法
     */
    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v!!.windowToken)
            }


        }
        try {
            return super.dispatchTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
        }

        return false
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.height
            val right = left + v.width
            return if (event.x > left && event.x < right
                    && event.y > top && event.y < bottom) {
                // 点击EditText的事件，忽略它。
                false
            } else {
                true
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private fun hideKeyboard(token: IBinder?) {
        if (token != null) {
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }


    /*//网络广播监听
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Bundle b = intent.getExtras();
			if (MbsConstans.BroadcastReceiverAction.NET_CHECK_ACTION.equals(action)) {
				if(MbsConstans.isNet){
					Toast.makeText(BasicActivity.this,"网络已经打开",Toast.LENGTH_SHORT).show();

				}else{
					Toast.makeText(BasicActivity.this,"网络已经关闭",Toast.LENGTH_SHORT).show();
				}
			}
		}
	};*/


    override fun isSupportSwipeBack(): Boolean {
        return true
    }

    override fun onSwipeBackLayoutSlide(slideOffset: Float) {

    }

    override fun onSwipeBackLayoutCancel() {

    }

    override fun onSwipeBackLayoutExecuted() {
        mSwipeBackHelper?.swipeBackward()
    }


    /**
     * 获取refreshToken方法
     */
    open fun getRefreshToken() {
        val map = HashMap<String, Any>()
        map["access_token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestPostToMap(mHeaderMap, MethodUrl.refreshToken, map)
    }

    fun dealFailInfo(map: MutableMap<String, Any>, mType: String) {
        val msg = map["errmsg"]!!.toString() + ""
        val errcodeStr = map["errcode"]!!.toString() + ""
        var errorCode = -1
        try {
            errorCode = java.lang.Double.valueOf(errcodeStr).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.i("show", "这里出现异常了" + e.message)
        }

        if (errorCode == ErrorHandler.REFRESH_TOKEN_DATE_CODE) {
            mRequestTagList.add(mType)
            if (!mIsRefreshToken) {
                mIsRefreshToken = true
                LogUtil.i("show", "refreshToken过期重新请求refreshtoken接口")
                getRefreshToken()
            } else {
                LogUtil.i("show", "refreshToken过期重新请求refreshtoken接口，正在请求。不需要再请求了")
            }
        } else if (errorCode == ErrorHandler.ACCESS_TOKEN_DATE_CODE) {
            closeAllActivity()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            showToastMsg(resources.getString(R.string.toast_login_again))
        } else if (errorCode == ErrorHandler.PHONE_NO_ACTIVE) {
            closeAllActivity()
            val intent = Intent(this, ChoosePhoneActivity::class.java)
            startActivity(intent)
            showToastMsg(resources.getString(R.string.toast_no_active))
        } else {
            when (mType) {
                MethodUrl.repayConfig -> showBaseMsgDialog(msg, true)
                else -> showToastMsg(msg)
            }
        }
        if (mType == MethodUrl.refreshToken) {
            mIsRefreshToken = false
        }
    }

    fun showBaseMsgDialog(msg: Any, isClose: Boolean) {
        if (mZhangDialog == null) {
            mZhangDialog = TipMsgDialog(this, true)
            mZhangDialog!!.setOnKeyListener { dialog, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
                    dialog.dismiss()
                    if (isClose) {
                        finish()
                    }

                    true
                } else {
                    false
                }
            }
            val onClickListener = View.OnClickListener { v ->
                when (v.id) {
                    R.id.cancel -> {
                        mZhangDialog!!.dismiss()
                        if (isClose) {
                            finish()
                        }
                    }
                    R.id.confirm -> mZhangDialog!!.dismiss()
                    R.id.tv_right -> {
                        mZhangDialog!!.dismiss()
                        if (isClose) {
                            finish()
                        }
                    }
                }
            }
            mZhangDialog!!.setCanceledOnTouchOutside(false)
            mZhangDialog!!.setCancelable(true)
            mZhangDialog!!.onClickListener = onClickListener
        }
        mZhangDialog!!.initValue("温馨提示", msg)
        mZhangDialog!!.show()

    }

    companion object {
        //自动登录标示
        protected var isAutoLogin = false

        private val mHandler = Handler(Looper.getMainLooper())
    }


}