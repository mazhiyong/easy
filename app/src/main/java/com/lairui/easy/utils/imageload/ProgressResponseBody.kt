package com.lairui.easy.utils.imageload

import android.util.Log

import java.io.IOException

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Okio
import okio.Source

/**
 */
class ProgressResponseBody
/**
 * 获取拦截器传递过来的responseBody
 */
(url: String, private val mResponseBody: ResponseBody) : ResponseBody() {
    private var mBufferedSource: BufferedSource? = null
    private var mListener: ProgressListener? = null

    init {
        mListener = ProgressInterceptor.LISTENER_MAP.get(url)
    }

    override fun contentType(): MediaType? {
        return mResponseBody.contentType()
    }

    override fun contentLength(): Long {
        return mResponseBody.contentLength()
    }

    override fun source(): BufferedSource? {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(ProgressSource(mResponseBody.source()))
        }
        return this.mBufferedSource
    }

    private inner class ProgressSource(source: Source) : ForwardingSource(source) {
        internal var totalBytesRead: Long = 0
        internal var currentProgress: Int = 0

        @Throws(IOException::class)
        override fun read(sink: Buffer, byteCount: Long): Long {
            //读到的总字节数 也就是进行下载过程中的字节数
            val bytesRead = super.read(sink, byteCount)
            //整个文件的size
            val fullLength = mResponseBody.contentLength()
            if (bytesRead.equals(-1)) {
                //说明下载完毕
                totalBytesRead = fullLength
            } else {
                //下载进行中
                totalBytesRead += bytesRead
            }
            //获取进度
            val progress = (100f * totalBytesRead / fullLength).toInt()
            Log.d(TAG, "Download progress is$progress")

            //回调进度
            if (mListener != null && progress != currentProgress) {
                mListener!!.onProgress(progress)
            }
            if (mListener != null && totalBytesRead == fullLength) {
                mListener = null
            }

            currentProgress = progress
            return bytesRead

        }
    }

    companion object {
        private val TAG = "ProgressResponseBody"
    }
}
