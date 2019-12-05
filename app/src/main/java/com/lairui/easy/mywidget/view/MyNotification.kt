package com.lairui.easy.mywidget.view

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.Settings
import android.widget.RemoteViews

import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

import com.lairui.easy.R
import com.lairui.easy.ui.module.activity.MainActivity
import com.lairui.easy.ui.module.activity.SplashActivity
import com.lairui.easy.ui.temporary.activity.TestActivity
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.UtilTools

import java.io.Serializable

import android.content.Context.NOTIFICATION_SERVICE

/**
 * 自定义 状态通知栏
 */
class MyNotification(private val mContext: Context) {
    private val mNotificationManager: NotificationManager
    private val mNotification: Notification? = null
    private val mViews: RemoteViews? = null


    init {
        mNotificationManager = mContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        initChannel()
    }

    /**
     * 显示通知栏消息
     * @param notificationId
     * @param content
     */
    fun showNotification(notificationId: Int, content: String) {

        var title = "通知消息"
        var des = ""

        val map = JSONUtil.instance.jsonMap(content)

        var notifyIntent: Intent? = null
        if (!UtilTools.isRunning(MainActivity.mInstance)) {
            notifyIntent = Intent(mContext, SplashActivity::class.java)
        } else {
            val type = map!!["type"]
            if (type == "result") {
                title = "标题"
                des = "内容"
                notifyIntent = Intent(mContext, TestActivity::class.java)
                notifyIntent.putExtra("DATA", map as Serializable)
            } else {
                notifyIntent = Intent(Intent.ACTION_MAIN)
                notifyIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                notifyIntent.component = ComponentName(MainActivity.mInstance.packageName, MainActivity.mInstance.localClassName)
                notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
        }

        val contentIntent = PendingIntent.getActivity(mContext, notificationId, notifyIntent, 0)

        val manager = mContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = manager.getNotificationChannel("chat")
            if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, mContext.packageName)
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.id)
                mContext.startActivity(intent)

            }

        }
        val notification = NotificationCompat.Builder(mContext, "chat")
                .setContentTitle(title)
                .setContentText(des)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.resources, R.drawable.logo))
                .setAutoCancel(true)
                .build()

        notification.contentIntent = contentIntent

        mNotificationManager.notify(notificationId, notification)
    }


    /**
     * android 8.0 初始化“通知渠道”
     */
    private fun initChannel() {
        //android 8.0自定义"通知渠道"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "chat"
            val channelName = "即时消息"
            val importance = NotificationManager.IMPORTANCE_HIGH
            createNotificationChannel(channelId, channelName, importance)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        channel.setShowBadge(true)
        val manager = mContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    companion object {

        var mInstance: MyNotification? = null
        fun getmInstance(context: Context): MyNotification {
            if (mInstance == null) {
                mInstance = MyNotification(context)
            }
            return mInstance as MyNotification
        }
    }


}

