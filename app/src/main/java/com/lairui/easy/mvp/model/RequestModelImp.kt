package com.lairui.easy.mvp.model

import android.content.Context
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log

import com.lairui.easy.api.ErrorHandler
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.api.RxApiManager
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.di.component.DaggerMVPComponent
import com.lairui.easy.mvp.base.BaseModel
import com.lairui.easy.mvp.base.IBaseRequestCallBack
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.SPUtils

import java.io.IOException
import java.util.HashMap

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * 描述：MVP中的M实现类，处理业务逻辑数据
 */
class RequestModelImp(mContext: Context) : BaseModel(), RequestModel<MutableMap<String, Any>> {
    var context: Context? = null
    internal var mCompositeSubscription: CompositeDisposable? = null

    init {
        context = mContext
        //mCompositeSubscription = new CompositeDisposable();
        DaggerMVPComponent.create().InjectinTo(this)
        RxApiManager.get()!!.add(context!!, this)
    }

    /**
     * Delete请求(支持RequsetBody) 返回ResponseBody
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     * @param iBaseRequestCallBack
     */
    override fun requestDeleteToRes(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: RequestBody, iBaseRequestCallBack: IBaseRequestCallBack<MutableMap<String, Any>>) {
        mCompositeSubscription = CompositeDisposable()
        RxApiManager.get()!!.addCompositeDisposable(context!!, mCompositeSubscription!!)
        mCompositeSubscription!!.add(mApiManagerService!!.requestDeleteToRes(mHeaderMap, MbsConstans.SERVER_URL + mUrl, mParaMap)  //将subscribe添加到subscription，用于注销subscribe
                .observeOn(AndroidSchedulers.mainThread())//指定事件消费线程
                .subscribeOn(Schedulers.io())  //指定 subscribe() 发生在 IO 线程
                .subscribeWith(object : DisposableObserver<Response<ResponseBody>>() {

                    public override fun onStart() {
                        super.onStart()
                        //onStart它总是在 subscribe 所发生的线程被调用 ,如果你的subscribe不是主线程，则会出错，则需要指定线程;
                        iBaseRequestCallBack.beforeRequest()
                    }

                    override fun onComplete() {
                        //回调接口：请求已完成，可以隐藏progress
                        //iBaseRequestCallBack.requestComplete();
                    }

                    override fun onError(e: Throwable) {
                        iBaseRequestCallBack.requestComplete()
                        e.printStackTrace()
                        LogUtil.i("打印log日志", "################################################" + mCompositeSubscription!!.isDisposed)
                        //回调接口：请求异常
                        val map = ErrorHandler.handleException(e)
                        iBaseRequestCallBack.requestError(map, mUrl)
                        LogUtil.i("打印log日志", "$mUrl   $map------------------获取请求异常信息--------------$e")
                    }

                    override fun onNext(responseBody: Response<ResponseBody>) {
                        iBaseRequestCallBack.requestComplete()
                        val mDataMap = HashMap<String, Any>()
                        //回调接口：请求成功，获取实体类对象
                        //iBaseRequestCallBack.requestSuccess(mDataMap,mUrl);
                        var result: String? = null
                        try {
                            result = responseBody.body()!!.string().trim { it <= ' ' }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        LogUtil.i("打印log日志", "$mUrl------------------获取get请求结果json字符串-----------------$result")
                        if (!TextUtils.isEmpty(result)) {
                            mDataMap["result"] = result!!
                        } else {
                            mDataMap["msg"] = ""
                        }
                        //回调接口：请求成功，获取实体类对象
                        iBaseRequestCallBack.requestSuccess(mDataMap, mUrl)
                        LogUtil.i("打印log日志", "$mUrl------------------获取请求结果-----------------$mDataMap")

                    }
                }))
    }


    /**
     * Delete请求(支持RequsetBody) 返回Map
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     * @param iBaseRequestCallBack
     */
    override fun requestDeleteToMap(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: RequestBody, iBaseRequestCallBack: IBaseRequestCallBack<MutableMap<String, Any>>) {
        mCompositeSubscription = CompositeDisposable()
        RxApiManager.get()!!.addCompositeDisposable(context!!, mCompositeSubscription!!)
        mCompositeSubscription!!.add(mApiManagerService!!.requestDeleteToMap(mHeaderMap, MbsConstans.SERVER_URL + mUrl, mParaMap)  //将subscribe添加到subscription，用于注销subscribe
                .observeOn(AndroidSchedulers.mainThread())//指定事件消费线程
                .subscribeOn(Schedulers.io())  //指定 subscribe() 发生在 IO 线程
                .subscribeWith(object : DisposableObserver<MutableMap<String, Any>>() {

                    public override fun onStart() {
                        super.onStart()
                        //onStart它总是在 subscribe 所发生的线程被调用 ,如果你的subscribe不是主线程，则会出错，则需要指定线程;
                        iBaseRequestCallBack.beforeRequest()
                    }

                    override fun onComplete() {
                        //回调接口：请求已完成，可以隐藏progress
                        //iBaseRequestCallBack.requestComplete();
                    }

                    override fun onError(e: Throwable) {
                        iBaseRequestCallBack.requestComplete()
                        e.printStackTrace()
                        LogUtil.i("打印log日志", "################################################" + mCompositeSubscription!!.isDisposed)
                        //回调接口：请求异常
                        val map = ErrorHandler.handleException(e)
                        iBaseRequestCallBack.requestError(map, mUrl)
                        LogUtil.i("打印log日志", "$mUrl   $map------------------获取请求异常信息--------------$e")
                    }

                    override fun onNext(mDataMap: MutableMap<String, Any>) {
                        iBaseRequestCallBack.requestComplete()
                        //回调接口：请求成功，获取实体类对象
                        iBaseRequestCallBack.requestSuccess(mDataMap, mUrl)
                        LogUtil.i("打印log日志", "$mUrl------------------获取请求结果-----------------$mDataMap")

                    }
                }))
    }


    /**
     * Put请求  返回ResponseBody
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     * @param iBaseRequestCallBack
     */
    override fun requestPutToRes(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: RequestBody, iBaseRequestCallBack: IBaseRequestCallBack<MutableMap<String, Any>>) {
        mCompositeSubscription = CompositeDisposable()
        RxApiManager.get()!!.addCompositeDisposable(context!!, mCompositeSubscription!!)
        mCompositeSubscription!!.add(mApiManagerService!!.requestPutToRes(mHeaderMap, MbsConstans.SERVER_URL + mUrl, mParaMap)  //将subscribe添加到subscription，用于注销subscribe
                .observeOn(AndroidSchedulers.mainThread())//指定事件消费线程
                .subscribeOn(Schedulers.io())  //指定 subscribe() 发生在 IO 线程
                .subscribeWith(object : DisposableObserver<Response<ResponseBody>>() {

                    public override fun onStart() {
                        super.onStart()
                        //onStart它总是在 subscribe 所发生的线程被调用 ,如果你的subscribe不是主线程，则会出错，则需要指定线程;
                        iBaseRequestCallBack.beforeRequest()
                    }

                    override fun onComplete() {
                        //回调接口：请求已完成，可以隐藏progress
                        //iBaseRequestCallBack.requestComplete();
                    }

                    override fun onError(e: Throwable) {
                        iBaseRequestCallBack.requestComplete()
                        e.printStackTrace()
                        LogUtil.i("打印log日志", "################################################" + mCompositeSubscription!!.isDisposed)
                        //回调接口：请求异常
                        val map = ErrorHandler.handleException(e)
                        iBaseRequestCallBack.requestError(map, mUrl)
                        LogUtil.i("打印log日志", "$mUrl   $map------------------获取请求异常信息--------------$e")
                    }

                    override fun onNext(responseBody: Response<ResponseBody>) {
                        iBaseRequestCallBack.requestComplete()
                        val mDataMap = HashMap<String, Any>()
                        //回调接口：请求成功，获取实体类对象
                        // iBaseRequestCallBack.requestSuccess(mDataMap,mUrl);
                        // LogUtil.i("打印log日志",mUrl+"------------------获取请求结果-----------------"+mDataMap);
                        try {
                            val result = responseBody.body()!!.string().trim { it <= ' ' }
                            LogUtil.i("打印log日志", "$mUrl------------------获取put请求结果json字符串-----------------$result")
                            if (!TextUtils.isEmpty(result)) {
                                mDataMap["msg"] = result
                            } else {
                                mDataMap["msg"] = ""
                            }
                            iBaseRequestCallBack.requestSuccess(mDataMap, mUrl)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                }))
    }


    /**
     * Put请求  返回Map
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     * @param iBaseRequestCallBack
     */
    override fun requestPutToMap(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: RequestBody, iBaseRequestCallBack: IBaseRequestCallBack<MutableMap<String, Any>>) {
        mCompositeSubscription = CompositeDisposable()
        RxApiManager.get()!!.addCompositeDisposable(context!!, mCompositeSubscription!!)
        mCompositeSubscription!!.add(mApiManagerService!!.requestPutToMap(mHeaderMap, MbsConstans.SERVER_URL + mUrl, mParaMap)  //将subscribe添加到subscription，用于注销subscribe
                .observeOn(AndroidSchedulers.mainThread())//指定事件消费线程
                .subscribeOn(Schedulers.io())  //指定 subscribe() 发生在 IO 线程
                .subscribeWith(object : DisposableObserver<MutableMap<String, Any>>() {

                    public override fun onStart() {
                        super.onStart()
                        //onStart它总是在 subscribe 所发生的线程被调用 ,如果你的subscribe不是主线程，则会出错，则需要指定线程;
                        iBaseRequestCallBack.beforeRequest()
                    }

                    override fun onComplete() {
                        //回调接口：请求已完成，可以隐藏progress
                        //iBaseRequestCallBack.requestComplete();
                    }

                    override fun onError(e: Throwable) {
                        iBaseRequestCallBack.requestComplete()
                        e.printStackTrace()
                        LogUtil.i("打印log日志", "################################################" + mCompositeSubscription!!.isDisposed)
                        //回调接口：请求异常
                        val map = ErrorHandler.handleException(e)
                        iBaseRequestCallBack.requestError(map, mUrl)
                        LogUtil.i("打印log日志", "$mUrl   $map------------------获取请求异常信息--------------$e")
                    }

                    override fun onNext(mDataMap: MutableMap<String, Any>) {
                        iBaseRequestCallBack.requestComplete()
                        //回调接口：请求成功，获取实体类对象
                        iBaseRequestCallBack.requestSuccess(mDataMap, mUrl)
                        LogUtil.i("打印log日志", "$mUrl------------------获取请求结果-----------------$mDataMap")

                    }
                }))
    }


    /**
     * Post请求  返回体 Map
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     * @param iBaseRequestCallBack
     */
    override fun requestPostToMap(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: RequestBody, iBaseRequestCallBack: IBaseRequestCallBack<MutableMap<String, Any>>) {
        mCompositeSubscription = CompositeDisposable()
        RxApiManager.get()!!.addCompositeDisposable(context!!, mCompositeSubscription!!)
        mCompositeSubscription!!.add(mApiManagerService!!.requestPostToMap(mHeaderMap, MbsConstans.SERVER_URL + mUrl, mParaMap)  //将subscribe添加到subscription，用于注销subscribe
                .observeOn(AndroidSchedulers.mainThread())//指定事件消费线程
                .subscribeOn(Schedulers.io())  //指定 subscribe() 发生在 IO 线程
                .subscribeWith(object : DisposableObserver<MutableMap<String, Any>>() {

                    public override fun onStart() {
                        super.onStart()
                        //onStart它总是在 subscribe 所发生的线程被调用 ,如果你的subscribe不是主线程，则会出错，则需要指定线程;
                        iBaseRequestCallBack.beforeRequest()
                    }

                    override fun onComplete() {
                        //回调接口：请求已完成，可以隐藏progress
                        //iBaseRequestCallBack.requestComplete();
                    }

                    override fun onError(e: Throwable) {
                        iBaseRequestCallBack.requestComplete()
                        e.printStackTrace()
                        LogUtil.i("打印log日志", "################################################" + mCompositeSubscription!!.isDisposed)
                        //回调接口：请求异常
                        val map = ErrorHandler.handleException(e)
                        iBaseRequestCallBack.requestError(map, mUrl)
                        LogUtil.i("打印log日志", "$mUrl   $map------------------获取请求异常信息--------------$e")

                    }

                    override fun onNext(mDataMap: MutableMap<String, Any>) {
                        iBaseRequestCallBack.requestComplete()
                        //回调接口：请求成功，获取实体类对象
                        iBaseRequestCallBack.requestSuccess(mDataMap, mUrl)
                        if (mUrl == MethodUrl.refreshToken) {

                            SPUtils.put(context!!, MbsConstans.SharedInfoConstans.REFRESH_TOKEN, MbsConstans.REFRESH_TOKEN)
                        }
                        LogUtil.i("打印log日志", "$mUrl------------------获取请求结果-----------------$mDataMap")
                    }
                }))
    }


    /**
     * Post请求 返回体ResponseBody
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     * @param iBaseRequestCallBack
     */
    override fun requestPostToRes(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: RequestBody, iBaseRequestCallBack: IBaseRequestCallBack<MutableMap<String, Any>>) {

        mCompositeSubscription = CompositeDisposable()
        RxApiManager.get()!!.addCompositeDisposable(context!!, mCompositeSubscription!!)
        mCompositeSubscription!!.add(mApiManagerService!!.requestPostToRes(mHeaderMap, MbsConstans.SERVER_URL + mUrl, mParaMap)  //将subscribe添加到subscription，用于注销subscribe
                .observeOn(AndroidSchedulers.mainThread())//指定事件消费线程
                .subscribeOn(Schedulers.io())  //指定 subscribe() 发生在 IO 线程
                .subscribeWith(object : DisposableObserver<Response<ResponseBody>>() {

                    public override fun onStart() {
                        super.onStart()
                        //onStart它总是在 subscribe 所发生的线程被调用 ,如果你的subscribe不是主线程，则会出错，则需要指定线程;
                        iBaseRequestCallBack.beforeRequest()
                    }

                    override fun onComplete() {
                        //回调接口：请求已完成，可以隐藏progress
                        //iBaseRequestCallBack.requestComplete();
                    }

                    override fun onError(e: Throwable) {
                        iBaseRequestCallBack.requestComplete()
                        e.printStackTrace()
                        LogUtil.i("打印log日志", "################################################" + mCompositeSubscription!!.isDisposed)
                        //回调接口：请求异常
                        val map = ErrorHandler.handleException(e)
                        iBaseRequestCallBack.requestError(map, mUrl)
                        LogUtil.i("打印log日志", "$mUrl   $map------------------获取请求异常信息--------------$e")

                    }

                    override fun onNext(responseBody: Response<ResponseBody>) {
                        iBaseRequestCallBack.requestComplete()
                        val mDataMap = HashMap<String, Any>()
                        //回调接口：请求成功，获取实体类对象
                        //iBaseRequestCallBack.requestSuccess(mDataMap,mUrl);
                        try {
                            val result = responseBody.body()!!.string().trim { it <= ' ' }
                            LogUtil.i("打印log日志", "$mUrl------------------获取get请求结果json字符串-----------------$result")

                            if (!TextUtils.isEmpty(result)) {
                                mDataMap["result"] = result
                            } else {
                                mDataMap["msg"] = ""
                            }
                            iBaseRequestCallBack.requestSuccess(mDataMap, mUrl)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                }))
    }

    /**
     * Get 请求 返回体Map
     * @param mHeaderMap
     * @param mUrl
     * @param mParam
     * @param iBaseRequestCallBack
     */
    override fun requestGetToMap(mHeaderMap: Map<String, String>, mUrl: String, mParam: Map<String, String>, iBaseRequestCallBack: IBaseRequestCallBack<MutableMap<String, Any>>) {
        mCompositeSubscription = CompositeDisposable()
        RxApiManager.get()!!.addCompositeDisposable(context!!, mCompositeSubscription!!)
        mCompositeSubscription!!.add(mApiManagerService!!.requestGetToMap(mHeaderMap, MbsConstans.SERVER_URL + mUrl, mParam)  //将subscribe添加到subscription，用于注销subscribe
                .observeOn(AndroidSchedulers.mainThread())//指定事件消费线程
                .subscribeOn(Schedulers.io())  //指定 subscribe() 发生在 IO 线程
                .subscribeWith(object : DisposableObserver<MutableMap<String, Any>>() {

                    public override fun onStart() {
                        super.onStart()
                        //onStart它总是在 subscribe 所发生的线程被调用 ,如果你的subscribe不是主线程，则会出错，则需要指定线程;
                        iBaseRequestCallBack.beforeRequest()
                    }

                    override fun onComplete() {
                        //回调接口：请求已完成，可以隐藏progress
                        //iBaseRequestCallBack.requestComplete();
                    }

                    override fun onError(e: Throwable) {
                        iBaseRequestCallBack.requestComplete()
                        e.printStackTrace()
                        LogUtil.i("打印log日志", "requestGetToMap################################################" + mCompositeSubscription!!.isDisposed)
                        //回调接口：请求异常
                        val map = ErrorHandler.handleException(e)
                        iBaseRequestCallBack.requestError(map, mUrl)
                        LogUtil.i("打印log日志", "$mUrl   $map------------------获取请求异常信息--------------$e")
                    }

                    override fun onNext(mDataMap: MutableMap<String, Any>) {
                        iBaseRequestCallBack.requestComplete()
                        //回调接口：请求成功，获取实体类对象
                        iBaseRequestCallBack.requestSuccess(mDataMap, mUrl)
                        LogUtil.i("打印log日志", "$mUrl------------------获取请求结果-----------------$mDataMap")
                    }
                }))
    }

    /**
     * Get 请求 返回体 ResponseBody
     * @param mHeaderMap
     * @param mUrl
     * @param mParamMap
     * @param iBaseRequestCallBack
     */
    override fun requestGetToRes(mHeaderMap: Map<String, String>, mUrl: String, mParamMap: Map<String, String>, iBaseRequestCallBack: IBaseRequestCallBack<MutableMap<String, Any>>) {
        mCompositeSubscription = CompositeDisposable()
        RxApiManager.get()!!.addCompositeDisposable(context!!, mCompositeSubscription!!)
        mCompositeSubscription!!.add(mApiManagerService!!.requestGetToRes(mHeaderMap, MbsConstans.SERVER_URL + mUrl, mParamMap)  //将subscribe添加到subscription，用于注销subscribe
                .observeOn(AndroidSchedulers.mainThread())//指定事件消费线程
                .subscribeOn(Schedulers.io())  //指定 subscribe() 发生在 IO 线程
                .subscribeWith(object : DisposableObserver<ResponseBody>() {

                    public override fun onStart() {
                        super.onStart()
                        //onStart它总是在 subscribe 所发生的线程被调用 ,如果你的subscribe不是主线程，则会出错，则需要指定线程;
                        iBaseRequestCallBack.beforeRequest()
                    }

                    override fun onComplete() {
                        //回调接口：请求已完成，可以隐藏progress
                        //iBaseRequestCallBack.requestComplete();
                    }

                    override fun onError(e: Throwable) {
                        iBaseRequestCallBack.requestComplete()
                        e.printStackTrace()
                        LogUtil.i("打印log日志", "loadGetStringData################################################" + mCompositeSubscription!!.isDisposed)
                        //回调接口：请求异常
                        val map = ErrorHandler.handleException(e)
                        iBaseRequestCallBack.requestError(map, mUrl)
                        LogUtil.i("打印log日志", "$mUrl   $map------------------获取请求异常信息--------------$e   ")
                    }

                    override fun onNext(responseBody: ResponseBody) {
                        iBaseRequestCallBack.requestComplete()
                        val mDataMap = HashMap<String, Any>()
                        //回调接口：请求成功，获取实体类对象
                        //iBaseRequestCallBack.requestSuccess(mDataMap,mUrl);
                        LogUtil.i(" System.out 打印log日志", "$mUrl------------------获取get请求结果json字符串-----------------$responseBody")
                        try {
                            if (responseBody == null) {
                                LogUtil.i("System.out  responseBody为空", "$mUrl------------------获取get请求结果json字符串-----------------")
                            } else {
                                when (mUrl) {
                                    MethodUrl.imageCode //加载图形验证码 返回BitMap
                                    -> {
                                        val inputStream = responseBody.byteStream()
                                        val bitmap = BitmapFactory.decodeStream(inputStream)
                                        mDataMap["img"] = bitmap
                                    }
                                    else -> {
                                        val result = responseBody.string().trim { it <= ' ' } //返回Map
                                        if (!TextUtils.isEmpty(result)) {
                                            mDataMap["result"] = result
                                        } else {
                                            mDataMap["msg"] = ""
                                        }
                                    }
                                }

                            }

                            iBaseRequestCallBack.requestSuccess(mDataMap, mUrl)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                }))
    }


    /**
     * 表单提交  返回ResponseBody
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     * @param iBaseRequestCallBack
     */
    override fun requestPostFormToRes(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: Map<String, String>, iBaseRequestCallBack: IBaseRequestCallBack<MutableMap<String, Any>>) {
        mCompositeSubscription = CompositeDisposable()
        RxApiManager.get()!!.addCompositeDisposable(context!!, mCompositeSubscription!!)
        mCompositeSubscription!!.add(mApiManagerService!!.requestPostFormToRes(mHeaderMap, MbsConstans.SERVER_URL + mUrl, mParaMap)
                .observeOn(AndroidSchedulers.mainThread())//指定事件消费线程
                .subscribeOn(Schedulers.io())//指定 subscribe() 发生在 IO 线程
                .subscribeWith(object : DisposableObserver<Response<ResponseBody>>() {

                    override fun onStart() {
                        super.onStart()
                        //onStart它总是在 subscribe 所发生的线程被调用 ,如果你的subscribe不是主线程，则会出错，则需要指定线程;
                        iBaseRequestCallBack.beforeRequest()
                    }

                    override fun onError(e: Throwable) {
                        val map = ErrorHandler.handleException(e)
                        iBaseRequestCallBack.requestError(map, mUrl)
                        println("$mUrl$map------------------获取请求异常信息--------------$e")
                    }

                    override fun onComplete() {
                        //回调接口：请求已完成，可以隐藏progress
                        iBaseRequestCallBack.requestComplete()
                    }

                    override fun onNext(responseBody: Response<ResponseBody>) {
                        val mDataMap = HashMap<String, Any>()
                        try {
                            val result = responseBody.body()!!.string().trim { it <= ' ' }
                            Log.i("show", "result:$result")
                            if (!TextUtils.isEmpty(result)) {
                                mDataMap["result"] = result
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        iBaseRequestCallBack.requestSuccess(mDataMap, mUrl)
                        println("$mUrl------------------获取请求结果-----------------$mDataMap")
                    }
                }) as Disposable
        )
    }

    /**
     * 表单提交  返回Map
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     * @param iBaseRequestCallBack
     */
    override fun requestPostFormToMap(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: Map<String, String>, iBaseRequestCallBack: IBaseRequestCallBack<MutableMap<String, Any>>) {
        mCompositeSubscription = CompositeDisposable()
        RxApiManager.get()!!.addCompositeDisposable(context!!, mCompositeSubscription!!)
        mCompositeSubscription!!.add(mApiManagerService!!.requestPostFormToMap(mHeaderMap, MbsConstans.SERVER_URL + mUrl, mParaMap)
                .observeOn(AndroidSchedulers.mainThread())//指定事件消费线程
                .subscribeOn(Schedulers.io())//指定 subscribe() 发生在 IO 线程
                .subscribeWith(object : DisposableObserver<MutableMap<String, Any>>() {

                    override fun onStart() {
                        super.onStart()
                        //onStart它总是在 subscribe 所发生的线程被调用 ,如果你的subscribe不是主线程，则会出错，则需要指定线程;
                        iBaseRequestCallBack.beforeRequest()
                    }

                    override fun onError(e: Throwable) {
                        val map = ErrorHandler.handleException(e)
                        iBaseRequestCallBack.requestError(map, mUrl)
                        println("$mUrl$map------------------获取请求异常信息--------------$e")
                    }

                    override fun onComplete() {
                        //回调接口：请求已完成，可以隐藏progress
                        iBaseRequestCallBack.requestComplete()
                    }

                    override fun onNext(mDataMap: MutableMap<String, Any>) {
                        iBaseRequestCallBack.requestComplete()
                        //回调接口：请求成功，获取实体类对象
                        iBaseRequestCallBack.requestSuccess(mDataMap, mUrl)
                        LogUtil.i("打印log日志", "$mUrl------------------获取请求结果-----------------$mDataMap")
                    }
                }) as Disposable
        )
    }


    /**
     * 下载文件 支持断点续传
     * @param start
     * @param mHeaderMap
     * @param mUrl
     * @param param
     * @param iBaseRequestCallBack
     */
    override fun downloadFile(start: String, mHeaderMap: Map<String, String>, mUrl: String, param: Map<String, String>, iBaseRequestCallBack: IBaseRequestCallBack<MutableMap<String, Any>>) {
        mCompositeSubscription = CompositeDisposable()
        RxApiManager.get()!!.addCompositeDisposable(context!!, mCompositeSubscription!!)
        mCompositeSubscription!!.add(mApiManagerService!!.downloadFile(start, mHeaderMap, mUrl, param)  //将subscribe添加到subscription，用于注销subscribe
                .observeOn(AndroidSchedulers.mainThread())//指定事件消费线程
                .subscribeOn(Schedulers.io())  //指定 subscribe() 发生在 IO 线程
                .subscribeWith(object : DisposableObserver<Response<ResponseBody>>() {

                    public override fun onStart() {
                        super.onStart()
                        //onStart它总是在 subscribe 所发生的线程被调用 ,如果你的subscribe不是主线程，则会出错，则需要指定线程;
                        iBaseRequestCallBack.beforeRequest()
                    }

                    override fun onComplete() {
                        //回调接口：请求已完成，可以隐藏progress
                        //iBaseRequestCallBack.requestComplete();
                    }

                    override fun onError(e: Throwable) {
                        iBaseRequestCallBack.requestComplete()
                        e.printStackTrace()
                        LogUtil.i("打印log日志", "################################################" + mCompositeSubscription!!.isDisposed)
                        //回调接口：请求异常
                        val map = ErrorHandler.handleException(e)
                        iBaseRequestCallBack.requestError(map, mUrl)
                        LogUtil.i("打印log日志", "$mUrl   $map------------------获取请求异常信息--------------$e")

                    }

                    override fun onNext(mDataMap: Response<ResponseBody>) {
                        val map = HashMap<String, Any>()
                        map["file"] = mDataMap
                        iBaseRequestCallBack.requestComplete()
                        //回调接口：请求成功，获取实体类对象
                        iBaseRequestCallBack.requestSuccess(map, mUrl)
                        LogUtil.i("打印log日志", "$mUrl------------------获取请求结果-----------------$mDataMap")
                    }
                }))
    }


    /**
     * 上传图片/文件  返回Map
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     * @param parts
     * @param iBaseRequestCallBack
     */
    override fun postFileToMap(mHeaderMap: Map<String, String>, mUrl: String, mSignMap: MutableMap<String, Any>, mParaMap: Map<String, RequestBody>, parts: List<MultipartBody.Part>, iBaseRequestCallBack: IBaseRequestCallBack<MutableMap<String, Any>>) {
        mCompositeSubscription = CompositeDisposable()
        RxApiManager.get()!!.addCompositeDisposable(context!!, mCompositeSubscription!!)
        mCompositeSubscription!!.add(mApiManagerService!!.postFileToMap(mHeaderMap, MbsConstans.SERVER_URL + mUrl, mParaMap, parts)  //将subscribe添加到subscription，用于注销subscribe
                .observeOn(AndroidSchedulers.mainThread())//指定事件消费线程
                .subscribeOn(Schedulers.io())  //指定 subscribe() 发生在 IO 线程
                .subscribeWith(object : DisposableObserver<MutableMap<String, Any>>() {

                    public override fun onStart() {
                        super.onStart()
                        //onStart它总是在 subscribe 所发生的线程被调用 ,如果你的subscribe不是主线程，则会出错，则需要指定线程;
                        iBaseRequestCallBack.beforeRequest()
                    }

                    override fun onComplete() {
                        //回调接口：请求已完成，可以隐藏progress
                        //iBaseRequestCallBack.requestComplete();
                    }

                    override fun onError(e: Throwable) {
                        iBaseRequestCallBack.requestComplete()
                        e.printStackTrace()
                        //回调接口：请求异常
                        var map: MutableMap<String, Any> = ErrorHandler.handleException(e)
                        if (map == null) {
                            map = HashMap()
                        }
                        map as MutableMap<String,Any>
                        map.putAll(mSignMap)
                        iBaseRequestCallBack.requestError(map, mUrl)
                        LogUtil.i("打印log日志", "$mUrl$map------------------获取请求异常信息--------------$e")

                    }

                    override fun onNext(mDataMap: MutableMap<String, Any>) {
                        iBaseRequestCallBack.requestComplete()
                        //回调接口：请求成功，获取实体类对象
                        mDataMap as MutableMap<String,Any>
                        mDataMap.putAll(mSignMap)
                        iBaseRequestCallBack.requestSuccess(mDataMap, mUrl)
                        LogUtil.i("打印log日志", "$mUrl------------------获取请求结果-----------------$mDataMap")
                    }
                }))
    }

    /**
     * 上传图片/文件  ResponseBody
     * @param mHeaderMap
     * @param mUrl
     * @param mParaMap
     * @param parts
     * @param iBaseRequestCallBack
     */
    override fun postFileToRes(mHeaderMap: Map<String, String>, mUrl: String, mParaMap: Map<String, RequestBody>, parts: List<MultipartBody.Part>, iBaseRequestCallBack: IBaseRequestCallBack<MutableMap<String, Any>>) {
        mCompositeSubscription = CompositeDisposable()
        RxApiManager.get()!!.addCompositeDisposable(context!!, mCompositeSubscription!!)
        mCompositeSubscription!!.add(mApiManagerService!!.postFileToRes(mHeaderMap, MbsConstans.SERVER_URL + mUrl, mParaMap, parts)  //将subscribe添加到subscription，用于注销subscribe
                .observeOn(AndroidSchedulers.mainThread())//指定事件消费线程
                .subscribeOn(Schedulers.io())  //指定 subscribe() 发生在 IO 线程
                .subscribeWith(object : DisposableObserver<Response<ResponseBody>>() {

                    public override fun onStart() {
                        super.onStart()
                        //onStart它总是在 subscribe 所发生的线程被调用 ,如果你的subscribe不是主线程，则会出错，则需要指定线程;
                        iBaseRequestCallBack.beforeRequest()
                    }

                    override fun onComplete() {
                        //回调接口：请求已完成，可以隐藏progress
                        //iBaseRequestCallBack.requestComplete();
                    }

                    override fun onError(e: Throwable) {
                        iBaseRequestCallBack.requestComplete()
                        e.printStackTrace()
                        //回调接口：请求异常
                        val map = ErrorHandler.handleException(e)
                        iBaseRequestCallBack.requestError(map, mUrl)
                        LogUtil.i("打印log日志", "$mUrl$map------------------获取请求异常信息--------------$e")
                    }

                    override fun onNext(responseBody: Response<ResponseBody>) {
                        val mDataMap = HashMap<String, Any>()

                        try {
                            val result = responseBody.body()!!.string()
                            Log.i("show", "result:$result")
                            if (!TextUtils.isEmpty(result)) {
                                mDataMap["result"] = result
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        iBaseRequestCallBack.requestSuccess(mDataMap, mUrl)
                        println("$mUrl------------------获取请求结果-----------------$mDataMap")
                    }
                }))
    }

    override fun onUnsubscribe() {
        if (mCompositeSubscription != null) {
            LogUtil.i("打印log日志", "onUnsubscribe  " + mCompositeSubscription!!.isDisposed)
            //判断状态
            if (!mCompositeSubscription!!.isDisposed) {
                mCompositeSubscription!!.clear()  //注销
                mCompositeSubscription!!.dispose()
            }
        } else {
            LogUtil.i("打印log日志", "onUnsubscribe  为空" + context!!.packageName)

        }

    }
}
