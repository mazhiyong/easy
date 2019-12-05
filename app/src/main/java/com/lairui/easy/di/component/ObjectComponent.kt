package com.lairui.easy.di.component

import com.lairui.easy.di.scope.ActivityScope

import javax.inject.Singleton

import dagger.Component

@ActivityScope
@Singleton
@Component(modules = [com.lairui.easy.di.module.ObjectModule::class])
interface ObjectComponent// void injectTo(PayHistoryAdapter adapter);
