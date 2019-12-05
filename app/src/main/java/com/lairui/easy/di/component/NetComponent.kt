package com.lairui.easy.di.component

import com.lairui.easy.di.module.MVPModule
import com.lairui.easy.di.module.NetModule
import com.lairui.easy.utils.tool.JSONUtil

import javax.inject.Singleton

import dagger.Component

/**
 * 网络连接的DaggeR的注射器
 */
@Singleton
@Component(modules = [NetModule::class])
interface NetComponent {

    //void  injectTo(ApiManager apiManager);
    fun injectTo(mvpModule: MVPModule)

    fun injectTo(jsonUtil: JSONUtil)
}
