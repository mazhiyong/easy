package com.lairui.easy.mvp.model


import com.lairui.easy.mvp.base.IBaseRequestCallBack

import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * 描述：MVP中的M；处理获取网络数据
 * 作者：dc on 2017/2/16 11:03
 * 邮箱：597210600@qq.com
 */
interface RequestModel<T> {

    //Delete请求(支持RequsetBody) 返回ResponseBody
    fun requestDeleteToRes(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: RequestBody, iBaseRequestCallBack: IBaseRequestCallBack<T>)

    //Delete请求(支持RequsetBody) 返回Map
    fun requestDeleteToMap(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: RequestBody, iBaseRequestCallBack: IBaseRequestCallBack<T>)


    //Put请求  返回ResponseBody
    fun requestPutToRes(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: RequestBody, iBaseRequestCallBack: IBaseRequestCallBack<T>)

    //Put请求  返回Map
    fun requestPutToMap(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: RequestBody, iBaseRequestCallBack: IBaseRequestCallBack<T>)


    //Get请求  返回ResponseBody
    fun requestGetToRes(mHeaderMap: Map<String, String>, mUrl: String, param: Map<String, String>, iBaseRequestCallBack: IBaseRequestCallBack<T>)

    //Get请求  返回Map
    fun requestGetToMap(mHeaderMap: Map<String, String>, mUrl: String, mParam: Map<String, String>, iBaseRequestCallBack: IBaseRequestCallBack<T>)


    //Post请求  返回ResponseBody
    fun requestPostToRes(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: RequestBody, iBaseRequestCallBack: IBaseRequestCallBack<T>)

    //Post请求  返回Map
    fun requestPostToMap(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: RequestBody, iBaseRequestCallBack: IBaseRequestCallBack<T>)


    //表单提交  返回ResponseBody
    fun requestPostFormToRes(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: Map<String, String>, iBaseRequestCallBack: IBaseRequestCallBack<T>)

    //表单提交  返回Map
    fun requestPostFormToMap(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: Map<String, String>, iBaseRequestCallBack: IBaseRequestCallBack<T>)


    //下载文件 支持断点续传
    fun downloadFile(start: String, mHeaderMap: Map<String, String>, mUrl: String, param: Map<String, String>, iBaseRequestCallBack: IBaseRequestCallBack<T>)


    //上传图片、文件   返回Map
    fun postFileToMap(mHeaderMap: Map<String, String>, mUrl: String, mSignMap: MutableMap<String, Any>, mParaMa: Map<String, RequestBody>, parts: List<MultipartBody.Part>, iBaseRequestCallBack: IBaseRequestCallBack<T>)

    //上传图片、文件   返回ResponseBody
    fun postFileToRes(mHeaderMap: Map<String, String>, mUrl: String, mParaMa: Map<String, RequestBody>, parts: List<MultipartBody.Part>, iBaseRequestCallBack: IBaseRequestCallBack<T>)


    /**
     * @descriptoin    注销subscribe
     * @author
     * @param
     * @date 2017/2/17 19:02
     * @return
     */
    fun onUnsubscribe()
}
