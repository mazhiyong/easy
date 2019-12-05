package com.lairui.easy.di.component

import com.lairui.easy.di.module.AppModule

import javax.inject.Singleton

import dagger.Component

/**
 * DarggeR中 Application的注射器
 */
@Singleton  //单例
//依赖Module层
@Component(modules = [AppModule::class])
interface AppComponent
