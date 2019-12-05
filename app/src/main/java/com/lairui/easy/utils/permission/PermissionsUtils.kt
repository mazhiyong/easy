package com.lairui.easy.utils.permission

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.widget.Toast

import com.lairui.easy.R
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission


import java.util.ArrayList

import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

/**
 * Describe：6.0动态权限管理帮助类
 *
 */
object PermissionsUtils {

    /**
     * 判断权限是否授权
     *
     * @param context     context
     * @param permissions 权限列表
     */
    fun checkPermissions(context: Context, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionsList = ArrayList<String>()
            if (permissions != null && permissions.size != 0) {
                for (permission in permissions) {
                    if (!isHavePermissions(context, permission)) {
                        permissionsList.add(permission)
                    }
                }
                if (permissionsList.size > 0) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * 判断设置返回后，权限是否获得授权
     */
    fun checkSettingPermissions(context: Context, permissions: List<String>?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionsList = ArrayList<String>()
            if (permissions != null && permissions.size != 0) {
                for (permission in permissions) {
                    if (!isHavePermissions(context, permission)) {
                        permissionsList.add(permission)
                    }
                }
                if (permissionsList.size > 0) {
                    return false
                }
            }
        }
        return true

    }


    /**
     * 检查是否授权某权限
     */
    private fun isHavePermissions(context: Context, permissions: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permissions) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 动态申请单个权限
     */
    @JvmStatic
    fun requsetRunPermission(context: Context?, mSuccessBack: RePermissionResultBack?, permissions: String) {
        Log.i("show", "111.....")
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .rationale(RuntimeRationale())
                .onGranted {
                    //context.Toasts("授权成功");
                    mSuccessBack?.requestSuccess()
                }.onDenied { permissions ->
                    Log.i("show", "333.....")
                    if (AndPermission.hasAlwaysDeniedPermission(context, permissions)) {
                        if (context != null) {
                            showSettingDialog(context, mSuccessBack, permissions)
                        }
                    }
                }.start()


    }


    /**
     * 动态申请权限组
     * @param permissions
     */

    @JvmStatic
    fun requsetRunPermission(context: Context, mResultBack: RePermissionResultBack?, vararg permissions: Array<String>?) {
        Log.i("show", "111.....a")
        AndPermission.with(context)
                .runtime()
                .permission(*permissions)
                .rationale(RuntimeRationale())
                .onGranted {
                    //context.Toasts("授权成功");
                    mResultBack?.requestSuccess()
                }.onDenied { permissions ->
                    Toast.makeText(context, R.string.failure, Toast.LENGTH_SHORT).show()
                    if (AndPermission.hasAlwaysDeniedPermission(context, permissions)) {
                        showSettingDialog(context, mResultBack, permissions)
                    }
                }.start()
    }

    /**
     * Display setting dialog.
     */
    fun showSettingDialog(context: Context, mSuccessBack: RePermissionResultBack?, permissions: List<String>) {
        Log.i("show","*********a")
        val permissionNames = Permission.transformText(context, permissions)
        val message = context.getString(R.string.message_permission_always_failed, TextUtils.join("\n", permissionNames))

        AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.title_dialog)
                .setMessage(message)
                .setPositiveButton(R.string.setting) { dialog, which -> setPermission(context, mSuccessBack, permissions) }
                .setNegativeButton(R.string.cancel) { dialog, which ->
                    mSuccessBack?.requestFailer()
                }
                .show()
    }


    internal fun setPermission(context: Context, mSuccessBack: RePermissionResultBack?, permissons: List<String>) {

        AndPermission.with(context).runtime().setting().start(1234)
        /*AndPermission.with(context)
                .runtime()
                .setting()
                .onComeback(new Setting.Action() {
                    @Override
                    public void onAction() {
                        Log.i("show","从设置返回.....");
                        //检测权限是否设置成功
                        if(PermissionsUtils.checkSettingPermissions(context,permissons)){
                            if(mSuccessBack != null){
                                mSuccessBack.requestSuccess();
                            }
                        }else {
                            if(mSuccessBack != null){
                                mSuccessBack.requestFailer();
                            }
                        }


                    }
                }).start();*/
    }
}
