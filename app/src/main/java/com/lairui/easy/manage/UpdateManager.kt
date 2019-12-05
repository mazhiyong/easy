package com.lairui.easy.manage

import android.content.Context
import android.view.View

import com.lairui.easy.R
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mywidget.dialog.UpdateDialog
import com.lairui.easy.service.DownloadService
import com.lairui.easy.utils.permission.PermissionsUtils
import com.lairui.easy.utils.permission.RePermissionResultBack
import com.yanzhenjie.permission.Permission

import java.lang.ref.WeakReference

/**
 * 版本更新管理
 */
class UpdateManager(var mContext: Context) {
    private val mReference: WeakReference<Context>
    private var mUpdateDialog: UpdateDialog? = null

    init {
        mReference = WeakReference(mContext)
    }

    /**
     * 获取后台版本号，判断是否需要更新
     * @param
     * @return
     */
    fun update(tData: MutableMap<String, Any>) {
        val versonCode = tData["versionCode"]!!.toString() + ""
        val versonName = tData["versionName"]!!.toString() + ""
        val versonMsg = tData["versionUpdateMsg"]!!.toString() + ""
        val versonDownUrl = tData["downUrl"]!!.toString() + ""
        val versonMd5Code = tData["md5Code"]!!.toString() + ""
        val versonForceUpdate = tData["forceUpdate"]!!.toString() + ""
        val versonOsType = tData["osType"]!!.toString() + ""
        val versonId = tData["id"]!!.toString() + ""

        MbsConstans.UpdateAppConstans.VERSION_NET_CODE = Integer.parseInt(versonCode)
        MbsConstans.UpdateAppConstans.VERSION_NET_APK_NAME = "boss"
        MbsConstans.UpdateAppConstans.VERSION_APK_ID = versonId
        MbsConstans.UpdateAppConstans.VERSION_NET_CODE_NAME = versonName
        MbsConstans.UpdateAppConstans.VERSION_NET_APK_URL = versonDownUrl
        MbsConstans.UpdateAppConstans.VERSION_NET_UPDATE_MSG = versonMsg
        MbsConstans.UpdateAppConstans.VERSION_MD5_CODE = versonMd5Code
        if (versonForceUpdate == "true") {
            MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE = true
        }
        //升级
        val requset = arrayOf(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.CAMERA)
        if (MbsConstans.UpdateAppConstans.VERSION_NET_CODE > MbsConstans.UpdateAppConstans.VERSION_APP_CODE) {
            mUpdateDialog = mReference.get()?.let { UpdateDialog(it, true) }
            val onClickListener = View.OnClickListener { v ->
                when (v.id) {
                    R.id.cancel -> if (MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE) {
                    } else {
                        mUpdateDialog!!.dismiss()
                    }
                    R.id.confirm -> mReference.get()?.let { it ->
                        PermissionsUtils.requsetRunPermission(it, object : RePermissionResultBack {
                            override fun requestSuccess() {
                                //授权成功，下载更新
                                mUpdateDialog!!.progressLay!!.visibility = View.VISIBLE
                                mReference.get()?.let {
                                    DownloadService.downNewFile(MbsConstans.UpdateAppConstans.VERSION_NET_APK_URL, 1003,
                                            MbsConstans.UpdateAppConstans.VERSION_NET_APK_NAME, MbsConstans.UpdateAppConstans.VERSION_MD5_CODE, it)
                                }

                            }

                            override fun requestFailer() {

                            }

                        }, com.yanzhenjie.permission.runtime.Permission.Group.STORAGE)
                    }
                }
            }
            mUpdateDialog!!.setCanceledOnTouchOutside(false)
            mUpdateDialog!!.setCancelable(false)
            var ss = ""
            if (MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE) {
                ss = "必须更新"
            } else {
                ss = "建议更新"
            }
            mUpdateDialog!!.onClickListener = onClickListener
            mUpdateDialog!!.initValue("检查新版本($ss)", "更新内容:\n" + MbsConstans.UpdateAppConstans.VERSION_NET_UPDATE_MSG)
            mUpdateDialog!!.show()

            if (MbsConstans.UpdateAppConstans.VERSION_UPDATE_FORCE) {
                mUpdateDialog!!.setCancelable(false)
                mUpdateDialog!!.tv_cancel!!.isEnabled = false
                mUpdateDialog!!.tv_cancel!!.visibility = View.GONE
            } else {
                mUpdateDialog!!.setCancelable(true)
                mUpdateDialog!!.tv_cancel!!.isEnabled = true
                mUpdateDialog!!.tv_cancel!!.visibility = View.VISIBLE
            }
            mUpdateDialog!!.progressLay!!.visibility = View.GONE
            DownloadService.mProgressBar = mUpdateDialog!!.progressBar
            DownloadService.mTextView = mUpdateDialog!!.prgText
        }
    }

    companion object {
        var manager: UpdateManager? = null

        @Synchronized
        fun getInstance(context: Context): UpdateManager {
            if (manager == null) {
                manager = UpdateManager(context)
            }
            return manager as UpdateManager
        }
    }


}


