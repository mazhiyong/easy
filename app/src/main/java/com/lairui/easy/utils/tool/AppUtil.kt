package com.lairui.easy.utils.tool

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri

import com.lairui.easy.basic.MbsConstans

import java.io.File
import java.util.ArrayList


/**
 *
 *
 */
class AppUtil(private val mContext: Context) {
    private var pkgList: List<PackageInfo> = ArrayList()
    private var packinfos: List<PackageInfo> = ArrayList()


    /**
     * 获取本地应用程序的版本号
     * @return
     */
    // 当前应用的版本名称
    // 当前版本的版本号
    // 当前版本的包名
    //String packageNames = info.packageName;
    // TODO Auto-generated catch block
    val appVersion: Int
        get() {

            var versionCode = 1
            try {

                val info = mContext.packageManager.getPackageInfo(mContext.packageName, 0)
                MbsConstans.UpdateAppConstans.VERSION_APP_NAME = info.versionName
                versionCode = info.versionCode
                MbsConstans.UpdateAppConstans.VERSION_APP_CODE = versionCode
            } catch (e: NameNotFoundException) {
                e.printStackTrace()
            }

            return versionCode
        }

    //获取应用程序名称
    val installPackages: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            for (info in packinfos) {
                val premissions = info.requestedPermissions
                if (premissions != null && premissions.size > 0) {
                    for (premission in premissions) {
                        if ("android.permission.INTERNET" == premission) {
                            val processName = info.applicationInfo.processName

                            if (!processName.contains(":")) {
                                val appName = info.applicationInfo.loadLabel(pmanager!!).toString()
                                val packageName = info.packageName
                            }
                        }
                    }
                }
            }

            return list
        }

    init {
        pmanager = mContext.packageManager
        if (pmanager != null) {
            pkgList = pmanager!!.getInstalledPackages(0)
            packinfos = pmanager!!.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES or PackageManager.GET_PERMISSIONS)
        }
    }

    //	/**
    //	 * 展示更新信息
    //	 */
    //	public void showVersionMsg(){
    //
    //		if(ParamUtils.VERSION_NET_CODE>ParamUtils.VERSION_APP_CODE){
    //			MessageBox.Builder mBuilder = new MessageBox.Builder(mContext)
    //			.setTitle("软件升级")
    //			.setMessage("发现新版本,建议立即更新使用.\n"+ParamUtils.VERSION_NET_UPDATE_MSG)
    //			.setPositiveButton("更新",
    //					new DialogInterface.OnClickListener() {
    //				public void onClick(DialogInterface dialog,
    //						int which) {
    //					dialog.cancel();
    //				}
    //			})
    //			.setNegativeButton("取消",
    //					new DialogInterface.OnClickListener() {
    //				public void onClick(DialogInterface dialog,
    //						int which) {
    //					dialog.cancel();
    //				}
    //			});
    //			mBuilder.create().show();
    //		}
    //	}

    //	/**
    //	 * 是否安装apk
    //	 */
    //	public void isInstall(final String filePath){
    //
    //		MessageBox.Builder mBuilder = new MessageBox.Builder(mContext)
    //		.setTitle("安装提示")
    //		.setMessage("新版本已经下载完毕是否安装?")
    //		.setPositiveButton("安装",
    //				new DialogInterface.OnClickListener() {
    //			public void onClick(DialogInterface dialog,
    //					int which) {
    //				dialog.cancel();
    //				appUtil.installApk(filePath);
    //			}
    //		})
    //		.setNegativeButton("取消",
    //				new DialogInterface.OnClickListener() {
    //			public void onClick(DialogInterface dialog,
    //					int which) {
    //				dialog.cancel();
    //			}
    //		});
    //		mBuilder.create().show();
    //	}


    /**
     * 进行apk安装
     * @param filePath    需要安装的apk路径
     */
    fun installApk(filePath: String) {

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(File(filePath)), "application/vnd.android.package-archive")
        mContext.startActivity(intent)
    }

    fun isMobile_spExist(packageName: String): Boolean {

        for (i in pkgList.indices) {

            val pI = pkgList[i]
            if (pI.packageName.equals(packageName, ignoreCase = true))
            //根据安装的应用的包名判断
                return true
        }


        return false
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var appUtil: AppUtil? = null
        private var pmanager: PackageManager? = null

        fun getInstance(context: Context): AppUtil {
            if (appUtil == null) {
                appUtil = AppUtil(context)
            }
            return appUtil as AppUtil
        }

        /**
         * 用来判断服务是否运行.
         * @param
         * @param className 判断的服务名字
         * @return true 在运行 false 不在运行
         */
        fun isServiceRunning(mContext: Context, className: String): Boolean {
            var isRunning = false
            val activityManager = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val serviceList = activityManager.getRunningServices(30)
            if (serviceList.size <= 0) {
                return false
            }
            for (i in serviceList.indices) {
                if (serviceList[i].service.className == className == true) {
                    isRunning = true
                    break
                }
            }
            return isRunning
        }


        /**
         * 获取当前应用程序的包名
         * @param context 上下文对象
         * @return 返回包名
         */
        @JvmStatic
        fun getAppProcessName(context: Context): String {
            //当前应用pid
            val pid = android.os.Process.myPid()
            //任务管理类
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            //遍历所有应用
            val infos = manager.runningAppProcesses
            for (info in infos) {
                if (info.pid == pid)
                //得到当前应用
                    return info.processName//返回包名
            }
            return ""
        }
    }


    //	public void getNetAppInfo(){
    //		Map<String, Object> postMap = new HashMap<String, Object>();
    //		postMap.put("code", "delivery_h2y");
    //
    //		Map<String, Object> resultMap = mRequestMbs.getResultMap(postMap, CommandCode.App.value(), MethodCode.UpdateApp.value());
    //		if (resultMap != null) {
    //
    //
    //			SysApp sysApp =   JSONUtil.getObject(resultMap.get(MbsEnum.PostKey.OBJECT_DATA.value()), SysApp.class);
    //			UpdateAppConstans.VERSION_NET_CODE =  (int) sysApp.getVersionCode();
    //			UpdateAppConstans.VERSION_NET_APK_NAME = sysApp.getApkName();
    //			UpdateAppConstans.VERSION_NET_UPDATE_MSG = sysApp.getVersionUpdateMsg();
    //			UpdateAppConstans.VERSION_NET_APK_URL = sysApp.getDownUrl();
    //			UpdateAppConstans.VERSION_MD5_CODE = sysApp.getMd5Code();
    //		}
    //	}

    //	@Override
    //	protected Map<String, Object> doInBackground(String... params) {
    //		// TODO Auto-generated method stub
    //		Map<String, Object> postMap = new HashMap<String, Object>();
    //		postMap.put("code", "delivery_h2y");
    //
    //		Map<String, Object> resultMap = mRequestMbs.getResultMap(postMap, CommandCode.App.value(), MethodCode.UpdateApp.value());
    //
    //		return resultMap;
    //	}
    //
    //	@Override
    //	protected void onPostExecute(Map<String, Object> resultMap) {
    //		super.onPostExecute(resultMap);
    //		Result result =   JSONUtil.getObject(resultMap.get(MbsEnum.PostKey.RESULT_DATA.value()),Result.class);
    //		if (result.getOpFlg() == ResultCode.getDataSuccess.value()) {
    //			SysApp sysApp =   JSONUtil.getObject(resultMap.get(MbsEnum.PostKey.OBJECT_DATA.value()), SysApp.class);
    //			UpdateAppConstans.VERSION_NET_CODE =  (int) sysApp.getVersionCode();
    //			UpdateAppConstans.VERSION_NET_APK_NAME = sysApp.getApkName();
    //		}
    //	}

}
