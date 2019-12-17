package com.lairui.easy.basic
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lairui.easy.R
import com.lairui.easy.di.component.DaggerPersenerComponent
import com.lairui.easy.di.module.PersenerModule
import com.lairui.easy.manage.ActivityManager
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.ui.temporary.activity.ChoosePhoneActivity
import com.lairui.easy.ui.module.activity.LoginActivity
import com.lairui.easy.api.ErrorHandler
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.mvp.presenter.RequestPresenterImp
import com.lairui.easy.mywidget.view.TipsToast
import com.lairui.easy.utils.tool.LogUtil
import java.util.ArrayList
import java.util.HashMap
import javax.inject.Inject
import butterknife.ButterKnife
import butterknife.Unbinder
abstract class BasicFragment : Fragment(), RequestView {

    protected var mInflater: LayoutInflater? = null

    var mRootView: View? = null

    var mUnbinder: Unbinder? = null

    @Inject
    lateinit var mRequestPresenterImp: RequestPresenterImp

    private var mActivityManager: ActivityManager? = null

    var mIsRefreshToken = false
    var mRequestTagList: MutableList<String> = ArrayList()
    /*@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		this.mInflater = inflater;
		mRootView = inflater.inflate(getLayoutId(), container, false);
		mUnbinder =ButterKnife.bind(this,mRootView);
		init();
		return mRootView;
	}*/
    abstract val layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mActivityManager = ActivityManager.instance

        this.mInflater = inflater
        if (mRootView != null) {
            val parent = mRootView!!.parent as ViewGroup
            parent.removeView(mRootView)
            //mUnbinder =ButterKnife.bind(this,mRootView);
            return mRootView
        }

        mRootView = inflater.inflate(layoutId, container, false)
        mUnbinder = ButterKnife.bind(this, mRootView!!)

        //DaggerPersenerComonent.builder().persenerModule(activity?.let { PersenerModule(this, it) }).build().injectTo(this)
        DaggerPersenerComponent.builder().persenerModule(PersenerModule(this, activity!!)).build().injectTo(this)
        init()
        return mRootView
    }

    abstract fun init()

    /**
     * 获取refreshToken方法
     */
    fun getRefreshToken() {
        val map = HashMap<String, Any>()
        map["access_token"] = MbsConstans.ACCESS_TOKEN
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.refreshToken, map)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder?.unbind()
    }


    fun dealFailInfo(map: MutableMap<String, Any>, mType: String) {
        val msg = map["errmsg"]!!.toString() + ""
        val errcodeStr = map["errcode"]!!.toString() + ""
        var errorCode = -1
        try {
            errorCode = java.lang.Double.valueOf(errcodeStr).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.i("show", "这里出现异常了" + e.message)
        }

        if (errorCode == ErrorHandler.REFRESH_TOKEN_DATE_CODE) {
            mRequestTagList.add(mType)
            println("$mType###########################################################$mRequestTagList")
            if (!mIsRefreshToken) {
                mIsRefreshToken = true
                LogUtil.i("show", "refreshToken过期重新请求refreshtoken接口")
                getRefreshToken()
            } else {
                LogUtil.i("show", "refreshToken过期重新请求refreshtoken接口，正在请求。不需要再请求了")

            }
        } else if (errorCode == ErrorHandler.ACCESS_TOKEN_DATE_CODE) {
            activity!!.finish()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            TipsToast.showToastMsg(resources.getString(R.string.toast_login_again))
        } else if (errorCode == ErrorHandler.PHONE_NO_ACTIVE) {//账号未激活
            mActivityManager!!.close()
            val intent = Intent(activity, ChoosePhoneActivity::class.java)
            startActivity(intent)
            TipsToast.showToastMsg(resources.getString(R.string.toast_no_active))
        } else {
            TipsToast.showToastMsg(msg)
        }

        if (mType == MethodUrl.refreshToken) {
            mIsRefreshToken = false
        }

    }


}