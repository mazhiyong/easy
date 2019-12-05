package com.lairui.easy.mvp.base

/**
 * 描述：
 * * 代理对象的基础实现 ：  一些基础的方法
 *
 * @param <V> 视图接口对象(view) 具体业务各自继承自IBaseView
 * @param <T> 业务请求返回的具体对象
 * 作者：dc on 2017/2/16 15:07
 * 邮箱：597210600@qq.com
</T></V> */
open class BasePresenterImp<V : IBaseView>
/**
 * @descriptoin     构造方法
 * @author    dc
 * @param view 具体业务的视图接口对象
 * @date 2017/2/16 15:12
 */
(view: V) : IBaseRequestCallBack<MutableMap<String,Any>> {

    var iBaseView: IBaseView? = null  //基类视图

    init {
        this.iBaseView = view
    }

    /**
     * @descriptoin    请求之前显示progress
     * @author    dc
     * @date 2017/2/16 15:13
     */
    override fun beforeRequest() {
        iBaseView!!.showProgress()
    }


    /**
     * @descriptoin    请求异常显示异常信息
     * @author    dc
     * @date 2017/2/16 15:13
     */
    /*    @Override
    public void requestError(Throwable throwable) {
        iBaseView.loadDataError(throwable);
        iBaseView.disimissProgress(); //请求错误，提示错误信息之后隐藏progress
    }*/
    override fun requestError(map: MutableMap<String, Any>, mType: String) {
        iBaseView!!.loadDataError(map, mType)
        iBaseView!!.disimissProgress() //请求错误，提示错误信息之后隐藏progress
    }

    /**
     * @descriptoin    请求完成之后隐藏progress
     * @author    dc
     * @date 2017/2/16 15:14
     */
    override fun requestComplete() {
        iBaseView!!.disimissProgress()
    }

    /**
     * @descriptoin    请求成功获取成功之后的数据信息
     * @author    dc
     * @param callBack 回调的数据
     * @date 2017/2/16 15:14
     */
    @SuppressWarnings("unchecked")
    override fun requestSuccess(callBack: MutableMap<String,Any>, mType: String) {
        iBaseView!!.loadDataSuccess(callBack, mType)
    }

}
