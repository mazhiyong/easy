package com.lairui.easy.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder

import androidx.core.app.NotificationCompat
import android.widget.ProgressBar
import android.widget.RemoteViews
import android.widget.TextView

import com.lairui.easy.R

import com.lairui.easy.ui.module.activity.MainActivity
import com.lairui.easy.utils.permission.InstallRationale
import com.lairui.easy.utils.secret.MD5
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.tool.AsyncTaskUtil
import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.utils.tool.UtilTools
import com.yanzhenjie.permission.AndPermission

import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.HashMap

class DownloadService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStart(intent: Intent, startId: Int) {
        super.onStart(intent, startId)
    }

    override fun onCreate() {
        super.onCreate()
        nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        context = this
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {

        private var nm: NotificationManager? = null
        private var notification: Notification? = null
        private val cancelUpdate = false

        //private static ExecutorService executorService = Executors.newFixedThreadPool(5); // 固定五个线程来执行任务
        var download: MutableMap<Int, Int> = HashMap()
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        private var views: RemoteViews? = null
        @SuppressLint("StaticFieldLeak")
        var mActivity: Context? = null

        @SuppressLint("StaticFieldLeak")
        var mTextView: TextView? = null
        @SuppressLint("StaticFieldLeak")
        var mProgressBar: ProgressBar? = null

        fun downNewFile(url: String, notificationId: Int, name: String, code: String, activity: Context) {
            mActivity = activity
            if (download.containsKey(notificationId))
                return
            //notification = new Notification(R.drawable.ic_launcher,"0%", System.currentTimeMillis());

            val id = "my_channel_01"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0以上需要处理
                val mChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
                //Toast.makeText(context, mChannel.toString(), Toast.LENGTH_SHORT).show();

                /*mChannel.canBypassDnd();//是否绕过请勿打扰模式
			mChannel.enableLights(true);//闪光灯
			mChannel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
			mChannel.setLightColor(Color.RED);//闪关灯的灯光颜色
			mChannel.canShowBadge();//桌面launcher的消息角标
			mChannel.enableVibration(true);//是否允许震动
			mChannel.getAudioAttributes();//获取系统通知响铃声音的配置
			mChannel.getGroup();//获取通知取到组
			mChannel.setBypassDnd(true);//设置可绕过  请勿打扰模式
			mChannel.setVibrationPattern(new long[]{100, 100, 200});//设置震动模式
			mChannel.shouldShowLights();//是否会有灯光*/

                mChannel.enableVibration(false)//是否允许震动
                mChannel.vibrationPattern = longArrayOf(0)//设置震动模式
                mChannel.setSound(null, null)

                nm!!.createNotificationChannel(mChannel)

                notification = Notification.Builder(context, "title")
                        .setChannelId(id)
                        .setContentTitle("5 new messages")
                        .setContentText("hahaha")
                        .setSmallIcon(R.drawable.default_pic).build()
            } else {
                val notificationBuilder = NotificationCompat.Builder(context, "title")
                        .setContentTitle("5 new messages")
                        .setContentText("hahaha")
                        .setSmallIcon(R.drawable.default_pic)
                        .setOngoing(true)
                        .setChannelId(id)//无效
                notification = notificationBuilder.build()

                notification!!.sound = null
                notification!!.vibrate = null

                /*	Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			 */
                /**
                 * sound属性是一个 Uri 对象。 可以在通知发出的时候播放一段音频，这样就能够更好地告知用户有通知到来.
                 * 如：手机的/system/media/audio/ringtones 目录下有一个 Basic_tone.ogg音频文件，
                 * 可以写成： Uri soundUri = Uri.fromFile(new
                 * File("/system/media/audio/ringtones/Basic_tone.ogg"));
                 * notification.sound = soundUri; 我这里为了省事，就去了手机默认设置的铃声
                 *//*
			notification.sound = uri;
			long[] vibrates = new long[]{100, 100, 200};
			notification.vibrate = vibrates;
		*/
            }
            notification!!.flags = notification!!.flags or Notification.FLAG_ONGOING_EVENT
            nm!!.notify(notificationId, notification)

            //设置任务栏中下载进程显示的views
            views = RemoteViews(context.packageName, R.layout.apkupdate)
            notification!!.contentView = views

            /*		notification.icon = android.R.drawable.stat_sys_download;
		// notification.icon=android.R.drawable.stat_sys_download_done;
		notification.tickerText = name + "开始下载";
		notification.when = System.currentTimeMillis();
		notification.defaults = Notification.DEFAULT_LIGHTS;

*/

            val notifyIntent = Intent(Intent.ACTION_MAIN)
            notifyIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            notifyIntent.component = ComponentName(MainActivity.mInstance.packageName, MainActivity.mInstance.localClassName)
            notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED//关键的一步，设置启动模式

            //显示在“正在进行中”
            //notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
            val contentIntent = PendingIntent.getActivity(context, notificationId, notifyIntent, 0)
            //notification.setLatestEventInfo(context, name, "0%", contentIntent);
            notification!!.contentIntent = contentIntent
            download[notificationId] = 0
            //views.setOnClickPendingIntent(R.id.turn_next, contentIntent);
            // 将下载任务添加到任务栏中
            nm!!.notify(notificationId, notification)
            // 启动线程开始执行下载任务
            downFile(url, notificationId, name, code)
        }

        // 下载更新文件
        private fun downFile(url: String, notificationId: Int, name: String, md5Code: String) {

            AsyncTaskUtil.excute(5, callback = object : AsyncTaskUtil.TaskCallback {
                internal var total = 0
                override fun initOnUI() {
                    if (mProgressBar != null) {
                        mProgressBar!!.max = 100
                    }
                }

                override fun cancellTask() {
                    //取消任务  移除通知栏
                    download.remove(notificationId)
                    nm!!.cancel(notificationId)

                }

                override fun doTask(vararg maps: Map<Any, Any>): Map<Any, Any> {
                    val map = HashMap<Any, Any>()

                    var tempFile: File? = null
                    try {
                        val client = DefaultHttpClient()
                        // params[0]代表连接的url
                        val get = HttpGet(url)
                        val response = client.execute(get)
                        val entity = response.entity
                        val length = entity.contentLength
                        val `is` = entity.content
                        if (`is` != null) {
                            MbsConstans.APP_DOWN_PATH = UtilTools.getAppDownPath(context)
                            val rootFile = File(MbsConstans.APP_DOWN_PATH)
                            if (!rootFile.exists() && !rootFile.isDirectory)
                                rootFile.mkdirs()
                            //原有APK文件
                            tempFile = File(MbsConstans.APP_DOWN_PATH + "/" + url.substring(url.lastIndexOf("/") + 1))
                            if (!tempFile.exists()) {
                                tempFile.createNewFile()
                            }
                            if (tempFile.exists()) {
                                //如果MD5值相等  已下载最新APK 退出防止反复下载
                                if (md5Code == MD5.md5File(tempFile)) {
                                    /*Message message=myHandler.obtainMessage(2,tempFile);
                                            message.arg1 = notificationId;
                                            myHandler.sendMessage(message);*/
                                    AsyncTaskUtil.mcallback!!.updateProgress(-1)
                                    map["total"] = "" + 100
                                    map["restlut"] = tempFile
                                    return map
                                } else {
                                    //删除无用文件
                                    tempFile.delete()
                                }
                            }
                            // 已读出流作为参数创建一个带有缓冲的输出流
                            val bis = BufferedInputStream(`is`)

                            // 创建一个新的写入流，讲读取到的图像数据写入到文件中
                            val fos = FileOutputStream(tempFile)
                            // 已写入流作为参数创建一个带有缓冲的写入流
                            val bos = BufferedOutputStream(fos)

                            var read: Int
                            var count: Long = 0
                            var precent = 0
                            val buffer = ByteArray(1024)
                            read = bis.read(buffer)
                            while (read != -1) {
                                bos.write(buffer, 0, read)
                                count += read.toLong()
                                precent = (count.toDouble() / length * 100).toInt()
                                total = precent
                                //判断是否取消当前下载任务
                                if (cancelUpdate) {
                                    tempFile.delete()
                                    //取消更新
                                    cancellTask()

                                }
                                // 每下载完成1%就通知任务栏进行修改下载进度
                                if (precent - download[notificationId]!! >= 1) {
                                    download[notificationId] = precent
                                    AsyncTaskUtil.mcallback!!.updateProgress(precent)
                                }
                            }
                            bos.flush()
                            bos.close()
                            fos.flush()
                            fos.close()
                            `is`.close()
                            bis.close()
                        }


                    } catch (e: ClientProtocolException) {
                        e.printStackTrace()
                        if (tempFile!!.exists())
                            tempFile.delete()

                        total = 0
                        AsyncTaskUtil.mcallback!!.updateProgress(-2)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        if (tempFile!!.exists())
                            tempFile.delete()

                        total = 0
                        AsyncTaskUtil.mcallback!!.updateProgress(-2)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        if (tempFile!!.exists())
                            tempFile.delete()

                        total = 0
                        AsyncTaskUtil.mcallback!!.updateProgress(-2)

                    }

                    map["total"] = "" + total
                    map["restlut"] = tempFile!!
                    return map
                }

                override fun progressTask(vararg values: Int?) {
                    val precent = values[0]

                    //下载失败
                    if (precent == -2) {
                        if (mProgressBar != null && mTextView != null) {
                            mProgressBar!!.progress = 0
                            mTextView!!.text = "下载失败"
                        }
                        cancellTask()


                    }
                    //已下载
                    if (precent == -1) {
                        if (mProgressBar != null) {
                            mProgressBar!!.progress = 100
                        }
                        cancellTask()

                    }
                    //正在下载
                    if (precent!! > 0 && precent < 99) {
                        if (mProgressBar != null && mTextView != null) {
                            mProgressBar!!.progress = precent
                            mTextView!!.text = "已下载$precent%"
                        }
                        //通知栏信息
                        views!!.setTextViewText(R.id.apkDownTextView, "已下载$precent%")
                        precent?.let { views!!.setProgressBar(R.id.apkDownProgressBar, 100, it, false) }
                        notification!!.contentView = views
                        nm!!.notify(notificationId, notification)
                    }

                    //下载完成
                    if (precent!! > 99) {
                        if (mProgressBar != null && mTextView != null) {
                            mProgressBar!!.progress = 100
                            mTextView!!.text = "已下载" + 100 + "%"
                        }
                        views!!.setTextViewText(R.id.apkDownTextView, "已下载" + 100 + "%")
                        views!!.setProgressBar(R.id.apkDownProgressBar, 100, 100, false)
                        nm!!.notify(notificationId, notification)
                        // 下载完成后清除所有下载信息
                        cancellTask()
                    }

                }

                override fun resultTask(objectObjectMap: Map<Any, Any>) {
                    //执行安装提示
                    val total = objectObjectMap["total"]!!.toString() + ""
                    if (total == "100") {
                        val file = objectObjectMap["restlut"] as File?
                        install(file!!, context)
                    }
                }
            })
        }


        // 安装下载后的apk文件
        fun install(file: File, context: Context) {


            LogUtil.i("---------------------------------------------------------------------------", file.absolutePath)
            /**
             * Install package.
             */
            AndPermission.with(mActivity)
                    .install()
                    .file(file)
                    .rationale(InstallRationale())
                    .onGranted { }
                    .onDenied {
                        // The user refused to install.
                    }
                    .start()


        }
    }


}
