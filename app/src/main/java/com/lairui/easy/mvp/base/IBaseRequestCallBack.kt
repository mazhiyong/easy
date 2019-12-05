package com.lairui.easy.mvp.base

/**
 * 描述：请求数据的回调接口
 * Presenter用于接受model获取（加载）数据后的回调
 * 作者：dc on 2017/2/16 11:22
 * 邮箱：597210600@qq.com
 */
interface IBaseRequestCallBack<T> {

    /**
     * @descriptoin    请求之前的操作
     * @author    dc
     * @date 2017/2/16 11:34
     */
    fun beforeRequest()

    /**
     * @descriptoin    请求异常
     * @author    dc
     * @date 2017/2/16 11:34
     */
    //void requestError(Throwable throwable);
    fun requestError(errorInfo: MutableMap<String, Any>, mType: String)


    /**
     * @descriptoin    请求完成
     * @author    dc
     * @date 2017/2/16 11:35
     */
    fun requestComplete()

    /**
     * @descriptoin    请求成功
     * @author    dc
     * @param callBack 根据业务返回相应的数据
     * @date 2017/2/16 11:35
     */
    fun requestSuccess(callBack: T, mType: String)
}
