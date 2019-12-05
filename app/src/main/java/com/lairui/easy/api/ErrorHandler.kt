package com.lairui.easy.api

import android.net.ParseException

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.lairui.easy.utils.tool.LogUtil

import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException

import java.io.IOException
import java.net.ConnectException
import java.util.HashMap

import retrofit2.HttpException

class ErrorHandler {
    /**
     * 约定异常
     */
    object ERROR {
        /**
         * 未知错误
         */
        val UNKNOWN = 10000
        /**
         * 解析错误
         */
        val PARSE_ERROR = 10001
        /**
         * 网络错误
         */
        val NETWORD_ERROR = 10002
        /**
         * 协议出错
         */
        val HTTP_ERROR = 10003

        /**
         * 证书出错
         */
        val SSL_ERROR = 10005

        /**
         * 连接超时
         */
        val TIMEOUT_ERROR = 10006
    }

    class ResponeThrowable(throwable: Throwable, var code: Int) : Exception(throwable) {
        override var message: String? = null
    }

    inner class ServerException : RuntimeException() {
        var code: Int = 0
        override var message: String? = null
    }

    companion object {

        fun handle(throwable: Throwable): MutableMap<String, Any>? {
            if (throwable is HttpException) {
                try {
                    val type = object : TypeToken<MutableMap<String, Any>>() {}.type
                    return Gson().fromJson<MutableMap<String, Any>>(throwable.response().errorBody()!!.string(),type)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else {
                throwable.printStackTrace()
            }
            return null
        }


        val REFRESH_TOKEN_DATE_CODE = 1007
        val ACCESS_TOKEN_DATE_CODE = 1006
        val PHONE_NO_ACTIVE = 1075


        private val UNAUTHORIZED = 401
        private val FORBIDDEN = 403
        private val NOT_FOUND = 404
        private val REQUEST_TIMEOUT = 408
        private val INTERNAL_SERVER_ERROR = 500
        private val BAD_GATEWAY = 502
        private val SERVICE_UNAVAILABLE = 503
        private val GATEWAY_TIMEOUT = 504

        fun handleException(e: Throwable): MutableMap<String, Any> {
            var errorMap: MutableMap<String, Any>? = HashMap()

            if (e is HttpException) {
                try {
                    //                String  s = httpException.response().errorBody().string();
                    //                boolean b =   JSONUtil.validate(s);
                    //                if (b){
                    val type = object : TypeToken<MutableMap<String, Any>>() {}.type
                    val  errorMaps = Gson().fromJson<MutableMap<String, Any>>(e.response().errorBody()!!.string(),type)
                    //                }else {
                    //                    errorMap = new HashMap<>();
                    //                }
                } catch (e1: Exception) {
                    LogUtil.i("打印log日志", "解析结果错误" + e.code())
                    errorMap = HashMap()
                    e1.printStackTrace()
                }

                var errorMsg = ""
                when (e.code()) {
                    UNAUTHORIZED -> errorMsg = "未授权"
                    FORBIDDEN -> errorMsg = "服务器禁止访问"
                    NOT_FOUND -> errorMsg = "服务器未响应，请稍后重试"
                    REQUEST_TIMEOUT -> errorMsg = "请求超时，请稍后重试"
                    GATEWAY_TIMEOUT -> errorMsg = "链接服务器失败，请稍后重试"
                    INTERNAL_SERVER_ERROR -> errorMsg = "服务器内部错误，请稍后重试"
                    BAD_GATEWAY -> errorMsg = "错误网关，请稍后重试"
                    SERVICE_UNAVAILABLE -> errorMsg = "服务器不可用，请稍后重试"
                    else -> errorMsg = "请求服务器失败，请稍后重试"
                }

                if (errorMap == null || errorMap.isEmpty()) {
                    errorMap = HashMap()
                    errorMap["errcode"] = e.code()
                    errorMap["errmsg"] = errorMsg
                }

                return errorMap
            } else if (e is ServerException) {
                errorMap!!["errcode"] = e.code
                errorMap["errmsg"] = e.message!!
                return errorMap
            } else if (e is JsonParseException
                    || e is JSONException
                    || e is ParseException) {
                errorMap!!["errcode"] = ERROR.PARSE_ERROR
                errorMap["errmsg"] = "解析错误"
                return errorMap
            } else if (e is ConnectException) {
                errorMap!!["errcode"] = ERROR.NETWORD_ERROR
                errorMap["errmsg"] = "连接失败,请检查网络"
                return errorMap
            } else if (e is javax.net.ssl.SSLHandshakeException) {
                errorMap!!["errcode"] = ERROR.SSL_ERROR
                errorMap["errmsg"] = "证书验证失败"
                return errorMap
            } else if (e is ConnectTimeoutException) {
                errorMap!!["errcode"] = ERROR.TIMEOUT_ERROR
                errorMap["errmsg"] = "连接超时"
                return errorMap
            } else if (e is java.net.SocketTimeoutException) {
                errorMap!!["errcode"] = ERROR.TIMEOUT_ERROR
                errorMap["errmsg"] = "连接超时"
                return errorMap
            } else if (e is java.net.UnknownHostException) {
                errorMap!!["errcode"] = ERROR.NETWORD_ERROR
                errorMap["errmsg"] = "连接失败,请检查网络"
                return errorMap
            } else {
                errorMap!!["errcode"] = ERROR.UNKNOWN
                errorMap["errmsg"] = "未知错误"
                e.printStackTrace()
                return errorMap
            }
        }
    }
}
