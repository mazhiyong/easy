package com.lairui.easy.utils.tool

import android.content.Context
import android.os.Handler
import android.os.Message

import java.lang.ref.WeakReference

/**
 * Handler 封装类(不重建线程)
 * 1、创建Handler实例
 * 2、封装Handler的分发消息的两种方式（消息、线程）
 * 3、封装消息、线程的的处理操作
 */
class HandlerUtil {

    /**
     * Post 方法
     * @param message Message
     * @param runnable  Runnable
     * @param time  延迟时间
     * @param what  EmptyMessage时的 int值
     */
    fun postMessage(message: Message?, runnable: String?, time: Long, what: Int) {

        if (message == null && runnable == null) {
            mMyHandler!!.sendEmptyMessageDelayed(what, time)
        }
        //当我们构造Handler传参为CallBack的时候，我们使用handler.handleMessage()；来触发的时候，handler是没有处理的。
        // 只能使用sendMessage();的方式来发送。
        if (message != null && runnable == null) {
            mMyHandler!!.sendMessageDelayed(message, time)
        }

        if (message == null && runnable != null && runnable == Runable) {
            mMyHandler!!.postDelayed(myRunable, time)
        }

    }

    class MyRunable : Runnable {
        override fun run() {
            //处理Message消息 更新UI
            if (mCallBack != null) {
                val message = Message()
                message.what = -1
                mCallBack!!.runHandleMessage(message)
            }
        }
    }


    //若引用  防止Handler 内存泄露
    class MyHandler(context: Context) : Handler() {
        private val reference: WeakReference<Context>

        init {
            reference = WeakReference(context)
        }

        //处理Message消息 更新UI
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (mCallBack != null) {
                mCallBack!!.runHandleMessage(msg)
            }
        }

    }

    interface MessageCallBack {
        fun runHandleMessage(message: Message)
    }

    companion object {
        val Runable = "myrunable"
        internal var mCallBack: MessageCallBack? = null
        internal var mHandlerUtil: HandlerUtil? = null
        //staic 修饰Handler
        internal var mMyHandler: MyHandler? = null
        private var myRunable: MyRunable? = null
        fun getmCallBack(): MessageCallBack? {
            return mCallBack
        }

        fun setmCallBack(mCallBack: MessageCallBack) {
            HandlerUtil.mCallBack = mCallBack
        }

        fun init(context: Context): HandlerUtil {
            if (mMyHandler == null) {
                mMyHandler = MyHandler(context)
            }
            if (mHandlerUtil == null) {
                mHandlerUtil = HandlerUtil()
            }

            if (myRunable == null) {
                myRunable = MyRunable()
            }

            return mHandlerUtil as HandlerUtil
        }

        /**
         * 结果处理的回调
         * @param messageCallBack
         */
        fun doMessage(messageCallBack: MessageCallBack) {
            setmCallBack(messageCallBack)
        }

        /**
         * 释放资源
         */
        fun release() {
            if (myRunable != null) {
                mMyHandler!!.removeCallbacks(myRunable)
                mMyHandler!!.removeCallbacksAndMessages(null)
            }
            myRunable = null
            mMyHandler = null
            mCallBack = null

        }
    }
}
