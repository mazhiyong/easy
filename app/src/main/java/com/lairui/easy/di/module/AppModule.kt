package com.lairui.easy.di.module

import android.content.Context

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

/**
 * DarggeR中 Application的Module (全局单例 变量的 初始化)
 */
@Module //注入module
class AppModule(private val mContext: Context) {

    // 实例化APP全局可用的Context
    @Provides
    @Singleton
    fun providerContext(): Context {
        return mContext
    }


}
