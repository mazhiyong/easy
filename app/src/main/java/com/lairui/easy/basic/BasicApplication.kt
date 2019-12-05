package com.lairui.easy.basic
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Process
import android.text.TextUtils
import android.widget.Toast
import androidx.multidex.MultiDexApplication
import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper
import cn.wildfire.chat.kit.WfcUIKit
import cn.wildfire.chat.kit.conversation.message.viewholder.MessageViewHolderManager
import cn.wildfire.chat.kit.location.viewholder.LocationMessageContentViewHolder
import com.lairui.easy.api.Config
import com.lairui.easy.utils.tool.AppContextUtil
import com.facebook.stetho.Stetho
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.zhangke.websocket.WebSocketHandler
import com.zhangke.websocket.WebSocketSetting
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class BasicApplication : MultiDexApplication() {

    internal var appCount = 0
    /*public void setTypeface(){
		typeFace = Typeface.createFromAsset(getAssets(), "fonts/gagayi.ttf");
		try
		{
			Field field = Typeface.class.getDeclaredField("SERIF");
			field.setAccessible(true);
			field.set(null, typeFace);
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}*/


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // 安装tinker
        Beta.installTinker()

    }


    override fun onCreate() {
        super.onCreate()
        mMainThreadId = Process.myTid().toLong()
        mHandler = Handler()

        wfcUIKit = WfcUIKit()
        wfcUIKit!!.init(this)
        //注册推送服务
        //PushService.init(this, BuildConfig.APPLICATION_ID);
        //注册位置信息
        //注册推送服务
//PushService.init(this, BuildConfig.APPLICATION_ID);
//注册位置信息
        MessageViewHolderManager.getInstance().registerMessageViewHolder(LocationMessageContentViewHolder::class.java)
        setupWFCDirs()
        Stetho.initializeWithDefaults(this)


        //1400284289  腾讯云IM id
        //fd5fbca44186cb5cc5596e0df1e36600bed09990c2cb9763fe446446fe8ebb42  腾讯云IM 秘钥

        registerActivityListener()
        //setTypeface();
        AppContextUtil.init(this)
        /*		Context context = getApplicationContext();
		// 获取当前包名
		String packageName = context.getPackageName();
		// 获取当前进程名
		String processName = getProcessName(android.os.Process.myPid());
		// 设置是否为上报进程
		CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
		strategy.setUploadProcess(processName == null || processName.equals(packageName));

		CrashReport.initCrashReport(context, "186ece60d5", true, strategy);*/

        /**
         * 必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回
         * 第一个参数：应用程序上下文
         * 第二个参数：如果发现滑动返回后立即触摸界面时应用崩溃，请把该界面里比较特殊的 View 的 class 添加到该集合中，目前在库中已经添加了 WebView 和 SurfaceView
         */
        BGASwipeBackHelper.init(this, null)

        Bugly.init(this, "484b6f7ca3", false)
        Handler().post {
            /*CrashHandler mUncaughtHandler= CrashHandler.getmContext();
				mUncaughtHandler.init(getApplicationContext());*/
            context = applicationContext
        }
        //初始化websocket
        initWebSocket()


    }

    private fun initWebSocket() {
        val setting = WebSocketSetting()

        //setting.connectUrl = "ws://47.244.40.226:1317/"
        setting.connectUrl = "wss://echo.websocket.org"
        setting.connectTimeout = 10*1000
        setting.connectionLostTimeout = 60
        setting.reconnectFrequency = 50
        val mapHeader = HashMap<String,String>()
        setting.httpHeaders = mapHeader
        //设置消息分发器，接收到数据后先进入该类中处理，处理完再发送到下游
        //setting.setResponseProcessDispatcher(AppResponseDispatcher())
        //接收到数据后是否放入子线程处理，只有设置了 ResponseProcessDispatcher 才有意义
        //setting.setProcessDataOnBackground(true)
        //网络状态发生变化后是否重连，
        //需要调用 WebSocketHandler.registerNetworkChangedReceiver(context) 方法注册网络监听广播
        setting.setReconnectWithNetworkChanged(true)


        val webManager = WebSocketHandler.init(setting)
        webManager.start()

    }


    @SuppressLint("ObsoleteSdkInt")
    private fun registerActivityListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerActivityLifecycleCallbacks(
                    object : Application.ActivityLifecycleCallbacks {

                        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                        }

                        override fun onActivityStarted(activity: Activity) {
                            appCount++
                        }

                        override fun onActivityResumed(activity: Activity) {

                        }

                        override fun onActivityPaused(activity: Activity) {

                        }

                        override fun onActivityStopped(activity: Activity) {
                            appCount--
                            if (appCount == 0) {
                                Toast.makeText(applicationContext,
                                        resources.getString(com.lairui.easy.R.string.app_name_main) + "应用进入后台运行",
                                        Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

                        }

                        override fun onActivityDestroyed(activity: Activity) {

                        }
                    }
            )
        }
    }

    companion object {

        private var wfcUIKit: WfcUIKit? = null

        //主线程id
        @JvmField
        var mMainThreadId : Long = 0
        //主线程Handler
        @JvmField
        var mHandler: Handler? = null


        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
        lateinit var typeFace: Typeface

        /**
         * 获取进程号对应的进程名
         *
         * @param pid 进程号
         * @return 进程名
         */
        private fun getProcessName(pid: Int): String? {
            var reader: BufferedReader? = null
            try {
                reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
                var processName = reader.readLine()
                if (!TextUtils.isEmpty(processName)) {
                    processName = processName.trim { it <= ' ' }
                }
                return processName
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            } finally {
                try {
                    reader?.close()
                } catch (exception: IOException) {
                    exception.printStackTrace()
                }

            }
            return null
        }
    }

    private fun setupWFCDirs() {
        var file = File(Config.VIDEO_SAVE_DIR)
        if (!file.exists()) {
            file.mkdirs()
        }
        file = File(Config.AUDIO_SAVE_DIR)
        if (!file.exists()) {
            file.mkdirs()
        }
        file = File(Config.FILE_SAVE_DIR)
        if (!file.exists()) {
            file.mkdirs()
        }
        file = File(Config.PHOTO_SAVE_DIR)
        if (!file.exists()) {
            file.mkdirs()
        }
    }



}
