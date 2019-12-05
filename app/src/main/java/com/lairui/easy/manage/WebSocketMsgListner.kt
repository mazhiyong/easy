package com.lairui.easy.manage

import android.os.Looper
import android.util.Log

import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.listener.WebListener
import com.lairui.easy.service.MessageService
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

/**
 * 后台消息推送管理
 */
class WebSocketMsgListner : WebSocketListener() {

    private var mHandler: android.os.Handler? = null
    var listener: WebListener? = null
    private var mWebSocket: WebSocket? = null

    fun clearListener() {
        this.listener = null
    }

    /**
     * 连接
     * @param webSocket
     * @param response
     */
    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        super.onOpen(webSocket, response)
        mWebSocket = webSocket
        mHandler = android.os.Handler(Looper.getMainLooper())

        //取消正在进行的连接
        MessageService.cancelReconnect()
        Log.i("show", "连接成功")
    }

    /**
     * 获取消息
     * @param webSocket
     * @param text
     */
    override fun onMessage(webSocket: WebSocket?, text: String?) {
        super.onMessage(webSocket, text)
        Log.i("show", "收到消息:" + text!!)
        mHandler!!.post {
            if (listener != null) {
                listener!!.outputMsg(text)
            }
        }
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
        super.onMessage(webSocket, bytes)

    }


    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.i("show", "连接失败")
        this.mWebSocket = null
        if (MbsConstans.USER_MAP != null) {
            MessageService.reConnect()
        } else {
            MessageService.cancelReconnect()
        }
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        super.onClosing(webSocket, code, reason)
        Log.i("show", "正在关闭/$code/$reason")
        mWebSocket = null
        //用户没有退出登录，则继续重新连接
        if (MbsConstans.USER_MAP != null) {
            MessageService.reConnect()
        }
    }

    override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
        super.onClosed(webSocket, code, reason)
        Log.i("show", "连接已关闭/$code/$reason")
        if (MbsConstans.USER_MAP != null) {
            MessageService.reConnect()
        } else {
            MessageService.cancelReconnect()
        }
    }

    fun sendMessage(s: String): Boolean {
        return if (mWebSocket != null) {
            mWebSocket!!.send(s)
        } else {
            false
        }
    }


    fun closeMessage(): Boolean {
        return if (mWebSocket != null) {
            mWebSocket!!.close(1000, "")
        } else {
            false
        }
    }

    companion object {

        private var infoManager: WebSocketMsgListner? = null

        val insance: WebSocketMsgListner?
            get() {
                if (infoManager == null) {
                    synchronized(WebSocketMsgListner::class.java) {
                        if (infoManager == null) {
                            infoManager = WebSocketMsgListner()
                        }
                    }
                }
                return infoManager

            }
    }
}
