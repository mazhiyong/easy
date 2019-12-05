package com.lairui.easy.di.scope

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

import javax.inject.Scope

/**
 * 自定义作用域  为Activity层
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
annotation class ActivityScope
