package com.lairui.easy.ui.module1.activity

import android.content.Intent

import androidx.core.content.ContextCompat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mywidget.dialog.UpdateDialog
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.lairui.easy.service.DownloadService
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.share.ShareUtil
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.UtilTools
import com.jaeger.library.StatusBarUtil
import com.mob.MobSDK

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener

/**
 * 新闻详情 界面
 */
class NewsItemActivity : BasicActivity(), RequestView {
    @BindView(R.id.back_img)
    lateinit var mBackImg: ImageView
    @BindView(R.id.title_text)
    lateinit var mTitleText: TextView
    @BindView(R.id.back_text)
    lateinit var mBackText: TextView
    @BindView(R.id.left_back_lay)
    lateinit var mLeftBackLay: LinearLayout



    private var mRequestTag = ""



    private var mShareMap: MutableMap<String, Any>? = null

    override val contentView: Int
        get() = R.layout.activity_news_item


    private var mUpdateDialog: UpdateDialog? = null


    override fun init() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA)

        mTitleText.text = "详情"
        //getTempToken();
        //getShareData()
    }

    @OnClick(R.id.back_img, R.id.left_back_lay)
    fun onViewClicked(view: View) {
        var intent: Intent? = null
        when (view.id) {
            R.id.back_img -> finish()
            R.id.left_back_lay -> finish()

        }
    }

    /**
     * 获取分享内容
     */
    fun getShareData() {
        mRequestTag = MethodUrl.shareUrl
        val map = HashMap<String, String>()
        val mHeaderMap = HashMap<String, String>()
        mRequestPresenterImp!!.requestGetToMap(mHeaderMap, MethodUrl.shareUrl, map)
    }



    override fun showProgress() {
        showProgressDialog()
    }

    override fun disimissProgress() {
        dismissProgressDialog()
    }

    override fun loadDataSuccess(tData: MutableMap<String, Any>, mType: String) {
        when (mType) {
            MethodUrl.shareUrl -> mShareMap = tData
            MethodUrl.refreshToken//获取refreshToken返回结果
            -> {
                MbsConstans.REFRESH_TOKEN = tData!!["refresh_token"].toString() + ""
                mIsRefreshToken = false
                when (mRequestTag) {
                    MethodUrl.shareUrl -> getShareData()
                }
            }
        }
    }

    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        dealFailInfo(map, mType)
    }




}
