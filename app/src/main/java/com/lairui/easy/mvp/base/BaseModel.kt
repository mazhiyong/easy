package com.lairui.easy.mvp.base


import com.lairui.easy.api.ApiManagerService
import com.lairui.easy.di.component.DaggerMVPComponent

import javax.inject.Inject

/**
 * Model层的基类
 * 网络请求的配置
 */
open class BaseModel {
    //retrofit 请求数据的管理类
    @Inject
    lateinit var mApiManagerService: ApiManagerService

    init {
        DaggerMVPComponent.create().InjectinTo(this)
    }
}
