package com.lairui.easy.mvp.base

/**
 * 描述：视图基类
 * 作者：dc on 2017/2/16 10:59
 * 邮箱：597210600@qq.com
 */
interface IBaseView {
    /**
     * @descriptoin    请求前加载progress
     * @author    dc
     * @date 2017/2/16 11:00
     */
    fun showProgress()

    /**
     * @descriptoin    请求结束之后隐藏progress
     * @author    dc
     * @date 2017/2/16 11:01
     */
    fun disimissProgress()

    /**
     * @descriptoin    请求数据成功
     * @author    dc
     * @param tData 数据类型
     * @date 2017/2/16 11:01
     */
    fun loadDataSuccess(tData: MutableMap<String,Any>, mType: String)

    /**
     * @descriptoin    请求数据错误
     * @param throwable 异常类型
     * @date 2017/2/16 11:01
     */
    //    void loadDataError(Throwable throwable);
    fun loadDataError(map: MutableMap<String, Any>, mType: String)
}
