package com.lairui.easy.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log

import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.manage.WebSocketMsgListner

import java.lang.ref.WeakReference
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

/**
 * 主线程  后台Service
 */
@SuppressLint("Registered")
class MessageService : Service() {

    private var mWebSocket: WebSocket? = null


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }


    override fun onCreate() {
        super.onCreate()
        msgService = this
        initWebSocket()

        myHandler = MyHandler(baseContext)
        //创建子线程
        myRunnable = MyRunnable(baseContext)
        Log.i("show", "MessageService")
    }

    /**
     * Websocket 初始化
     */
    private fun initWebSocket() {
        if (MbsConstans.USER_MAP != null) {
            val okHttpClient = OkHttpClient()
            val request = Request.Builder()
                    .url(MbsConstans.WEBSOCKET_URL + "xx/xx")
                    .addHeader("clno", MbsConstans.USER_MAP!!["clno"]!!.toString() + "")
                    .build()

            mWebSocket = okHttpClient.newWebSocket(request, WebSocketMsgListner.insance!!)

            //关闭okhttp客户端
            okHttpClient.dispatcher().executorService().shutdown()
            Log.i("show", "开始连接到websocket服务器")
        }

    }


    /**
     * static  防止内存泄露
     */
    internal class MyRunnable(var mContext: Context) : Runnable {

        override fun run() {
            val message = Message.obtain()
            myHandler!!.sendMessage(message)
        }
    }

    internal class MyHandler(context: Context) : Handler() {
        //弱引用  解决内存泄露问题
        private val mWeakReference: WeakReference<Context>

        init {
            mWeakReference = WeakReference(context)
        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            msgService!!.initWebSocket()
            myHandler!!.postDelayed(myRunnable, mReConnectTime.toLong())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private var msgService: MessageService? = null
        private var myHandler: MyHandler? = null
        @SuppressLint("StaticFieldLeak")
        private var myRunnable: MyRunnable? = null

        private val mReConnectTime = 1000 * 30

        /**
         * 重新连接（每间隔30秒进行一次心跳检测）
         */
        fun reConnect() {
            cancelReconnect()
            myHandler!!.postDelayed(myRunnable, mReConnectTime.toLong())
            Log.i("show", "重写连接")
        }

        /**
         * 取消 重新连接
         */
        fun cancelReconnect() {
            myHandler!!.removeCallbacks(myRunnable)
            Log.i("show", "取消连接")
        }
    }
}
