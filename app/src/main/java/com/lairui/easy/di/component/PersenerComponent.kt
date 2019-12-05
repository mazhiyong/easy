package com.lairui.easy.di.component


import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.basic.BasicFragment
import com.lairui.easy.di.module.PersenerModule
import com.lairui.easy.di.scope.ActivityScope

import javax.inject.Singleton

import dagger.Component


@ActivityScope
@Singleton
@Component(modules = [PersenerModule::class])
interface PersenerComponent {
    //RequestPresenterImp
    fun injectTo(activity: BasicActivity)

    fun injectTo(fragment: BasicFragment)

}
