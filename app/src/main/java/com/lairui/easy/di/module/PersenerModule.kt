package com.lairui.easy.di.module

import android.content.Context

import com.lairui.easy.mvp.presenter.RequestPresenterImp
import com.lairui.easy.mvp.view.RequestView

import javax.inject.Singleton
import dagger.Module
import dagger.Provides


/**
 * MVP  中Presener的注解
 */
@Module
class PersenerModule
/*public static PersenerModule getInstance(RequestView view,Context context) {
       // mView =view;
        //mContext = context;
        //if(instance == null){
            instance = new PersenerModule(view,context);
       // }
        return instance;
    }*/
( val mView: RequestView, private val mContext: Context) {

    val instance: PersenerModule? = null

    @Provides
    @Singleton
    internal fun providerView(): RequestView {
        return this.mView
    }


    @Provides
    @Singleton
    internal fun providerContext(): Context {
        //return ActivityManager.getInstance().currentActivity();
        return mContext
    }

    //实例化Presenter层
    @Provides
    @Singleton
    internal fun providerRequestPresenterImp(requestView: RequestView, mContext: Context): RequestPresenterImp {
        return RequestPresenterImp(requestView, mContext)
    }

}
