package com.lairui.easy.mvp.presenter

/**
 * 描述：MVP中的P接口定义
 */
interface RequestPresenter {

    //Delete请求(支持RequsetBody) 返回ResponseBody
    fun requestDeleteToRes(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>)

    //Delete请求(支持RequsetBody) 返回Map
    fun requestDeleteToMap(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>)


    //Put请求  返回ResponseBody
    fun requestPutToRes(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>)

    //Put请求  返回Map
    fun requestPutToMap(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>)


    //Get请求  返回ResponseBody
    fun requestGetToRes(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>)

    //Get请求  返回Map
    fun requestGetToMap(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>)


    //Post请求  返回ResponseBody
    fun requestPostToRes(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>)

    //Post请求  返回Map
    fun requestPostToMap(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: MutableMap<String, Any>)


    //表单提交  返回ResponseBody
    fun requestPostFormToRes(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>)

    //表单提交  返回Map
    fun requestPostFormToMap(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>)


    //下载文件  断点续传
    fun downloadFile(start: String, mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: Map<String, String>)


    //文件上传  返回Response
    fun postFileToRes(mHeaderMap: MutableMap<String, String>, mUrl: String, mParaMap: MutableMap<String, Any>, mFileMap: MutableMap<String, Any>)

    //文件上传  返回Map
    fun postFileToMap(mHeaderMap: MutableMap<String, String>, mUrl: String, mSignMap: MutableMap<String, Any>, mParaMap: MutableMap<String, Any>, mFileMap: MutableMap<String, Any>)


    /**
     * @descriptoin    注销subscribe
     * @author    dc
     * @date 2017/2/17 19:36
     */
    fun unSubscribe()

}
