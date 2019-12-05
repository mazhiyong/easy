package com.lairui.easy.di.component

import com.lairui.easy.di.module.MVPModule
import com.lairui.easy.mvp.base.BaseModel
import com.lairui.easy.mvp.model.RequestModelImp

import javax.inject.Singleton

import dagger.Component

/**
 * MVP的DaggeR的注射器
 */

@Singleton
@Component(modules = [MVPModule::class])
interface MVPComponent {

    //ApiManagerService
    fun InjectinTo(baseModel: BaseModel)

    //CompositeDisposable
    fun InjectinTo(requestModel: RequestModelImp)

}
