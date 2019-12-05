package com.lairui.easy.service

import android.app.IntentService
import android.content.Context
import android.content.Intent


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 *
 *
 *
 * app  开启的时候 初始化 操作  减少app 开启的效率
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
/**
 * Instantiates a new Init service.
 */
class InitService : IntentService("InitService") {


    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_FOO == action) {
                LoadInit()
            }
        }
    }


    /**
     * 初始化操作数据
     */
    private fun LoadInit() {


    }

    companion object {
        // TODO: Rename actions, choose action names that describe tasks that this
        // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
        private val ACTION_FOO = "com.xinli.vkeeper.services.action.FOO"

        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @param context the context
         * @see IntentService
         */
        // TODO: Customize helper method
        fun startActionFoo(context: Context) {
            try {
                val intent = Intent(context, InitService::class.java)
                intent.action = ACTION_FOO

                /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {*/
                context.startService(intent)
                // }
            } catch (e: Exception) {
                e.printStackTrace()

            }

        }
    }

}
