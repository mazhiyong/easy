package com.lairui.easy.api

import com.lairui.easy.mvp.model.RequestModelImp

import io.reactivex.disposables.CompositeDisposable


interface RxActionManager<T> {

    fun add(tag: T, subscription: RequestModelImp)
    fun addCompositeDisposable(tag: T, compositeDisposable: CompositeDisposable)
    fun remove(tag: T)
    fun cancel(tag: T)
    fun cancelActivityAll(tag: T)

    fun cancelAll()
}
