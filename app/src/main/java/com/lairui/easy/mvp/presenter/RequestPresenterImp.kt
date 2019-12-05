package com.lairui.easy.mvp.presenter

import android.content.Context

import com.google.gson.Gson
import com.lairui.easy.R
import com.lairui.easy.api.ErrorHandler
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mvp.base.BasePresenterImp
import com.lairui.easy.mvp.model.RequestModelImp
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.NetworkUtils
import com.lairui.easy.utils.tool.SPUtils
import com.lairui.easy.utils.tool.UtilTools

import java.io.File
import java.util.ArrayList
import java.util.HashMap

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody


/**
 * 描述：MVP中的P实现类
 */

class RequestPresenterImp(view: RequestView, context: Context) : BasePresenterImp<RequestView>(view), RequestPresenter {


    var context: Context? = null
    val mRequestModelImp: RequestModelImp

    init {
        this.context = context
        this.mRequestModelImp = RequestModelImp(context)
    }

    fun checkData() {

        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN) || UtilTools.empty(MbsConstans.REFRESH_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils[context!!, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, ""]!!.toString()
            MbsConstans.REFRESH_TOKEN = SPUtils[context!!, MbsConstans.SharedInfoConstans.REFRESH_TOKEN, ""]!!.toString()
            val s = SPUtils[context!!, MbsConstans.SharedInfoConstans.LOGIN_INFO, ""]!!.toString()
            MbsConstans.USER_MAP = JSONUtil.instance.jsonMap(s)
        }
    }

    fun netErrorBack(): MutableMap<String, Any> {
        val errorMap = HashMap<String, Any>()
        errorMap["errcode"] = ErrorHandler.ERROR.NETWORD_ERROR
        errorMap["errmsg"] = context!!.resources.getString(R.string.net_error)
        return errorMap
    }



    override fun requestDeleteToRes(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>) {
        checkData()
        if (!NetworkUtils.isNetAvailable(context!!)) {
            val errorMap = netErrorBack()
            this.requestError(errorMap, mUrl)
            return
        }
        LogUtil.i("requestGetToRes.ACCESS_TOKEN", MbsConstans.ACCESS_TOKEN)
        LogUtil.i("requestGetToRes.REFRESH_TOKEN", MbsConstans.REFRESH_TOKEN)
        if (!UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            mHeaderMap["access_token"] = MbsConstans.ACCESS_TOKEN
        }
        if (!UtilTools.empty(MbsConstans.REFRESH_TOKEN)) {
            mHeaderMap["refresh_token"] = MbsConstans.REFRESH_TOKEN
        }


        //请求参数为json类型的时候的请求，需要做下面操作
        val gson = Gson()
        val jsonStr = gson.toJson(mParaMap)
        LogUtil.i("打印log日志", "请求的参数$jsonStr")
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr)
        mRequestModelImp.requestDeleteToRes(mHeaderMap, mUrl, body, this)
    }

    /**
     * Delete请求(支持RequsetBody) 返回Map
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     */
    override fun requestDeleteToMap(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>) {
        checkData()
        if (!NetworkUtils.isNetAvailable(context!!)) {
            val errorMap = netErrorBack()
            this.requestError(errorMap, mUrl)
            return
        }
        LogUtil.i("requestGetToRes.ACCESS_TOKEN", MbsConstans.ACCESS_TOKEN)
        LogUtil.i("requestGetToRes.REFRESH_TOKEN", MbsConstans.REFRESH_TOKEN)
        if (!UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            mHeaderMap["access_token"] = MbsConstans.ACCESS_TOKEN
        }
        if (!UtilTools.empty(MbsConstans.REFRESH_TOKEN)) {
            mHeaderMap["refresh_token"] = MbsConstans.REFRESH_TOKEN
        }

        //请求参数为json类型的时候的请求，需要做下面操作
        val gson = Gson()
        val jsonStr = gson.toJson(mParaMap)
        LogUtil.i("打印log日志", "请求的参数$jsonStr")
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr)
        mRequestModelImp.requestDeleteToMap(mHeaderMap, mUrl, body, this)
    }

    /**
     * Put请求  返回ResponseBody
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     */
    override fun requestPutToRes(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>) {
        checkData()
        if (!NetworkUtils.isNetAvailable(context!!)) {
            val errorMap = netErrorBack()
            this.requestError(errorMap, mUrl)
            return
        }
        LogUtil.i("requestGetToRes.ACCESS_TOKEN", MbsConstans.ACCESS_TOKEN)
        LogUtil.i("requestGetToRes.REFRESH_TOKEN", MbsConstans.REFRESH_TOKEN)
        if (!UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            mHeaderMap["access_token"] = MbsConstans.ACCESS_TOKEN
        }
        if (!UtilTools.empty(MbsConstans.REFRESH_TOKEN)) {
            mHeaderMap["refresh_token"] = MbsConstans.REFRESH_TOKEN
        }


        //请求参数为json类型的时候的请求，需要做下面操作
        val gson = Gson()
        val jsonStr = gson.toJson(mParaMap)

        LogUtil.i("打印log日志", "请求的参数$jsonStr")
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr)
        mRequestModelImp.requestPutToRes(mHeaderMap, mUrl, body, this)
    }

    /**
     * Put请求  返回Map
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     */
    override fun requestPutToMap(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>) {
        checkData()
        if (!NetworkUtils.isNetAvailable(context!!)) {
            val errorMap = netErrorBack()
            this.requestError(errorMap, mUrl)
            return
        }
        LogUtil.i("requestGetToRes.ACCESS_TOKEN", MbsConstans.ACCESS_TOKEN)
        LogUtil.i("requestGetToRes.REFRESH_TOKEN", MbsConstans.REFRESH_TOKEN)
        if (!UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            mHeaderMap["access_token"] = MbsConstans.ACCESS_TOKEN
        }
        if (!UtilTools.empty(MbsConstans.REFRESH_TOKEN)) {
            mHeaderMap["refresh_token"] = MbsConstans.REFRESH_TOKEN
        }

        //请求参数为json类型的时候的请求，需要做下面操作
        val gson = Gson()
        val jsonStr = gson.toJson(mParaMap)

        LogUtil.i("打印log日志", "请求的参数$jsonStr")
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr)
        mRequestModelImp.requestPutToMap(mHeaderMap, mUrl, body, this)
    }


    /**
     * Get请求  返回ResponseBody
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     */
    override fun requestGetToRes(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>) {
        checkData()
        if (!NetworkUtils.isNetAvailable(context!!)) {
            val errorMap = netErrorBack()
            this.requestError(errorMap, mUrl)
            return
        }
        if (!UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            mHeaderMap["access_token"] = MbsConstans.ACCESS_TOKEN
        }
        if (!UtilTools.empty(MbsConstans.REFRESH_TOKEN)) {
            mHeaderMap["refresh_token"] = MbsConstans.REFRESH_TOKEN
        }

        mRequestModelImp.requestGetToRes(mHeaderMap, mUrl, mParaMap, this)
    }


    /**
     * Get请求  返回Map
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     */
    override fun requestGetToMap(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>) {
        checkData()
        if (!NetworkUtils.isNetAvailable(context!!)) {
            val errorMap = netErrorBack()
            this.requestError(errorMap, mUrl)
            return
        }

        LogUtil.i("requestGetToMap.ACCESS_TOKEN", MbsConstans.ACCESS_TOKEN)
        LogUtil.i("requestGetToMap.REFRESH_TOKEN", MbsConstans.REFRESH_TOKEN)
        if (!UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            mHeaderMap["access_token"] = MbsConstans.ACCESS_TOKEN
        }
        if (!UtilTools.empty(MbsConstans.REFRESH_TOKEN)) {
            mHeaderMap["refresh_token"] = MbsConstans.REFRESH_TOKEN
        }

        mRequestModelImp.requestGetToMap(mHeaderMap, mUrl, mParaMap, this)
    }


    /**
     * Post请求  返回ResponseBody
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     */
    override fun requestPostToRes(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>) {
        checkData()
        // mHeaderMap.put("Connection", "close");
        if (!NetworkUtils.isNetAvailable(context!!)) {
            val errorMap = netErrorBack()
            this.requestError(errorMap, mUrl)
            return
        }

        if (!UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            mHeaderMap["access_token"] = MbsConstans.ACCESS_TOKEN
        }
        if (!UtilTools.empty(MbsConstans.REFRESH_TOKEN)) {
            mHeaderMap["refresh_token"] = MbsConstans.REFRESH_TOKEN
        }


        //请求参数为json类型的时候的请求，需要做下面操作
        val gson = Gson()
        val jsonStr = gson.toJson(mParaMap)

        LogUtil.i("打印log日志", "请求的参数$jsonStr")
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr)
        mRequestModelImp.requestPostToRes(mHeaderMap, mUrl, body, this)
    }

    /**
     * Post请求  返回Map
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     */
    override fun requestPostToMap(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: MutableMap<String, Any>) {
        checkData()
        // mHeaderMap.put("Connection", "close");
        if (!NetworkUtils.isNetAvailable(context!!)) {
            val errorMap = netErrorBack()
            this.requestError(errorMap, mUrl)
            return
        }


        if (!UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            mHeaderMap["access_token"] = MbsConstans.ACCESS_TOKEN
        }
        if (!UtilTools.empty(MbsConstans.REFRESH_TOKEN)) {
            mHeaderMap["refresh_token"] = MbsConstans.REFRESH_TOKEN
        }


        //请求参数为json类型的时候的请求，需要做下面操作
        val gson = Gson()
        val jsonStr = gson.toJson(mParaMap)

        LogUtil.i("打印log日志", "请求的参数$jsonStr")
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr)
        mRequestModelImp.requestPostToMap(mHeaderMap, mUrl, body, this)
    }


    /**
     * //表单提交  返回ResponseBody
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     */
    override fun requestPostFormToRes(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>) {
        checkData()
        if (!UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            mHeaderMap["access_token"] = MbsConstans.ACCESS_TOKEN
        }
        if (!UtilTools.empty(MbsConstans.REFRESH_TOKEN)) {
            mHeaderMap["refresh_token"] = MbsConstans.REFRESH_TOKEN
        }


        mRequestModelImp.requestPostFormToRes(mHeaderMap, mUrl, mParaMap, this)
    }

    /**
     * //表单提交  返回Map
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     */
    override fun requestPostFormToMap(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>) {
        checkData()
        if (!UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            mHeaderMap["access_token"] = MbsConstans.ACCESS_TOKEN
        }
        if (!UtilTools.empty(MbsConstans.REFRESH_TOKEN)) {
            mHeaderMap["refresh_token"] = MbsConstans.REFRESH_TOKEN
        }

        mRequestModelImp.requestPostFormToMap(mHeaderMap, mUrl, mParaMap, this)
    }


    /**
     * 下载文件  支持断点续传
     * @param start
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     */
    override fun downloadFile(start: String, mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>) {
        checkData()
        if (!NetworkUtils.isNetAvailable(context!!)) {
            val errorMap = netErrorBack()
            this.requestError(errorMap, mUrl)
            return
        }
        if (!UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            mHeaderMap["access_token"] = MbsConstans.ACCESS_TOKEN
        }
        if (!UtilTools.empty(MbsConstans.REFRESH_TOKEN)) {
            mHeaderMap["refresh_token"] = MbsConstans.REFRESH_TOKEN
        }

        //请求参数为json类型的时候的请求，需要做下面操作
        mRequestModelImp.downloadFile(start, mHeaderMap, mUrl, mParaMap, this)
    }


    /**
     * 上传文件/图片  返回ResponseBody
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     * @param mFileMap
     */
    override fun postFileToRes(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: MutableMap<String, Any>, mFileMap: MutableMap<String, Any>) {
        checkData()
        if (!NetworkUtils.isNetAvailable(context!!)) {
            val errorMap = netErrorBack()
            this.requestError(errorMap, mUrl)
            return
        }

        if (!UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            mHeaderMap["access_token"] = MbsConstans.ACCESS_TOKEN
        }
        if (!UtilTools.empty(MbsConstans.REFRESH_TOKEN)) {
            mHeaderMap["refresh_token"] = MbsConstans.REFRESH_TOKEN
        }


        val parts = getRequestBodyFileMap(mFileMap)
        val requestBodyMap = getRequestBodyMap(mParaMap)

        mRequestModelImp.postFileToRes(mHeaderMap, mUrl, requestBodyMap, parts, this)
    }


    /**
     * 上传文件/图片  返回Map
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     * @param mFileMap
     */

    override fun postFileToMap(mHeaderMap: MutableMap<String, String>, mUrl: String, mSignMap: MutableMap<String, Any>, mParaMap: MutableMap<String, Any>, mFileMap: MutableMap<String, Any>) {
        checkData()
        if (!NetworkUtils.isNetAvailable(context!!)) {
            val errorMap = netErrorBack()
            this.requestError(errorMap, mUrl)
            return
        }

        if (MbsConstans.ACCESS_TOKEN != null && MbsConstans.ACCESS_TOKEN != "" && MbsConstans.ACCESS_TOKEN != "null") {
            mHeaderMap["access_token"] = MbsConstans.ACCESS_TOKEN
        }
        if (MbsConstans.REFRESH_TOKEN != null && MbsConstans.REFRESH_TOKEN != "" && MbsConstans.REFRESH_TOKEN != "null") {
            mHeaderMap["refresh_token"] = MbsConstans.REFRESH_TOKEN
        }


        //将File 封装成RequestBody
        val parts = getRequestBodyFileMap(mFileMap)
        //将String 封装成RequestBody
        val requestBodyMap = getRequestBodyMap(mParaMap)
        mRequestModelImp.postFileToMap(mHeaderMap, mUrl, mSignMap, requestBodyMap, parts, this)
    }


    /**
     * /将File 封装成RequestBody
     * @param map
     * @return
     */
    private fun getRequestBodyFileMap(map: MutableMap<String, Any>): List<MultipartBody.Part> {
        val parts = ArrayList<MultipartBody.Part>()
        for (`in` in map.keys) {
            //map.keySet()返回的是所有key的值
            val str = map[`in`]!!.toString() + ""//得到每个key多对用value的值
            val file = File(str)
            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val body = MultipartBody.Part.createFormData(`in`, file.name, requestBody)
            parts.add(body)
        }
        return parts
    }


    private fun getRequestBodyMap(map: MutableMap<String, Any>): Map<String, RequestBody> {
        val param = HashMap<String, RequestBody>()
        for (`in` in map.keys) {
            //map.keySet()返回的是所有key的值
            val str = map[`in`]!!.toString() + ""//得到每个key多对用value的值
            LogUtil.i("打印log日志", "$`in`     $str")
            param[`in`] = RequestBody.create(MediaType.parse("text/plain"), str)
        }

        return param
    }

    override fun unSubscribe() {
        mRequestModelImp.onUnsubscribe()
    }


}
