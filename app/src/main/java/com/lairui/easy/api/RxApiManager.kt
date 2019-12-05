package com.lairui.easy.api

import android.annotation.TargetApi
import android.os.Build
import android.util.ArrayMap

import com.lairui.easy.mvp.model.RequestModelImp
import com.lairui.easy.utils.tool.LogUtil

import java.util.ArrayList

import io.reactivex.disposables.CompositeDisposable


class RxApiManager @TargetApi(Build.VERSION_CODES.KITKAT)
private constructor() : RxActionManager<Any> {

    //键是页面activity 如果一个页面有多个请求的话，   mListTags 会准确记录每个请求
    private val mListTags: ArrayMap<Any, MutableList<RequestModelImp>>
    //记录页面请求   键是页面activity  但是一个页面多个请求的话，会覆盖，只会记录最后请求的那一个
    private val maps: ArrayMap<Any, RequestModelImp>

    private val mListTags2: ArrayMap<Any, MutableList<CompositeDisposable>>

    init {
        maps = ArrayMap()
        mListTags = ArrayMap()
        mListTags2 = ArrayMap()
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun add(tag: Any, subscription: RequestModelImp) {
        if (mListTags[tag] == null) {
            val list = ArrayList<RequestModelImp>()
            list.add(subscription)
            mListTags[tag] = list
        } else {
            val list = mListTags[tag]
            list?.add(subscription)
            mListTags[tag] = list
        }

        maps[tag] = subscription
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun addCompositeDisposable(tag: Any, compositeDisposable: CompositeDisposable) {
        if (mListTags2[tag] == null) {
            val list = ArrayList<CompositeDisposable>()
            list.add(compositeDisposable)
            mListTags2[tag] = list
        } else {
            val list = mListTags2[tag]
            list?.add(compositeDisposable)
            mListTags2[tag] = list
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun remove(tag: Any) {
        if (!maps.isEmpty()) {
            maps.remove(tag)
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun removeAll() {
        if (!maps.isEmpty()) {
            maps.clear()
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun cancel(tag: Any) {

        if (maps.isEmpty()) {
            return
        }
        if (maps[tag] == null) {
            return
        }
        maps[tag]!!.onUnsubscribe()
        maps.remove(tag)
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun cancelActivityAll(tag: Any) {
        val list = mListTags[tag]
        if (list != null && list.size > 0) {
            for (requestModelImp in list) {
                requestModelImp.onUnsubscribe()
            }
            mListTags.remove(tag)
        }


        val list2 = mListTags2[tag]
        if (list2 != null && list2.size > 0) {
            for (compositeDisposable in list2) {
                if (compositeDisposable != null) {
                    LogUtil.i("打印log日志", tag.javaClass.simpleName + "onUnsubscribe  " + compositeDisposable.isDisposed)
                    //判断状态
                    if (!compositeDisposable.isDisposed) {
                        compositeDisposable.clear()  //注销
                        compositeDisposable.dispose()
                    }
                }
            }
            mListTags2.remove(tag)
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun cancelAll() {
        if (maps.isEmpty()) {
            return
        }
        val keys = maps.keys
        for (apiKey in keys) {
            cancel(apiKey)
        }
    }

    companion object {

        private var sInstance: RxApiManager? = null


        fun get(): RxApiManager? {

            if (sInstance == null) {
                synchronized(RxApiManager::class.java) {
                    if (sInstance == null) {
                        sInstance = RxApiManager()
                    }
                }
            }
            return sInstance
        }
    }
}
