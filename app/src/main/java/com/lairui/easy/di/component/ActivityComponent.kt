package com.lairui.easy.di.component


import com.lairui.easy.di.module.ActivityModule
import com.lairui.easy.di.scope.ActivityScope

import dagger.Component

/**
 * DarggeR中 Activity的注射器
 */

@ActivityScope
//继承AppComponent  依赖Module层
@Component(dependencies = [AppComponent::class], modules = [ActivityModule::class])
interface ActivityComponent//void  inject(BaseActivity mainActivity);
