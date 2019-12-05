package com.lairui.easy.di.module

import com.lairui.easy.utils.tool.TextViewUtils

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

/**
 * 各个Object 类的实例化
 */
@Module
class ObjectModule {

    //实例化TextViewUtils
    @Provides
    @Singleton
    internal fun providerTextViewUtils(): TextViewUtils {
        return TextViewUtils()
    }


}
