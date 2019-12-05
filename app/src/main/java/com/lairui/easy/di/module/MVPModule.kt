package com.lairui.easy.di.module

import com.lairui.easy.api.ApiManagerService
import com.lairui.easy.di.component.DaggerNetComponent

import javax.inject.Inject
import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
/**
 * MVP persener和modle层的对象的实例化
 */
@Module
class MVPModule {


    @Inject
    lateinit var mApiManager: ApiManagerService

    //ApiManagerService单例对象
    @Provides
    @Singleton
    internal fun providerApiManage(): ApiManagerService {
        DaggerNetComponent.create().injectTo(this)
        return mApiManager
    }

    //订阅类 单例对象
    @Provides
    @Singleton
    internal fun providerCompos(): CompositeDisposable {
        return CompositeDisposable()
    }
}
