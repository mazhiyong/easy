package com.lairui.easy.manage

import android.app.Activity
import android.content.Context
import android.util.Log


import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.ui.module.activity.MainActivity
import com.lairui.easy.utils.tool.LogUtil

import java.util.Stack

class ActivityManager private constructor() {

    /**
     * 返回到指定的Activity中
     * @param clazz
     */
    fun backTo(clazz: Class<*>?, b: Boolean) {
        if (clazz != null) {
            Log.i("popActivity", clazz.name)
            //在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
            val ps = clazz.name
            while (true) {
                if (!activityStack!!.isEmpty()) {
                    val activity = activityStack!!.peek()

                    if (activity.localClassName.equals(ps, ignoreCase = true)) {
                        (activity as BasicActivity).mIsRefresh = b
                        break
                    } else {

                        activityStack!!.pop().finish()
                    }
                } else
                    break
            }
        }
    }

    /**
     * 返回到指定的Activity中
     * @param clazz
     */
    fun backToMainActivity(clazz: Class<*>?, i: Int) {
        if (clazz != null) {
            Log.i("popActivity", clazz.name)
            //在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
            val ps = clazz.name
            while (true) {
                if (!activityStack!!.isEmpty()) {
                    val activity = activityStack!!.peek()
                    if ((activity.packageName + "." + activity.localClassName).equals(ps, ignoreCase = true)) {
                        if (activity is MainActivity) {

                        }
                        break
                    } else {
                        activityStack!!.pop().finish()
                    }
                } else
                    break
            }
        }
    }

    fun removeActivity(activity: Activity) {
        if (activityStack != null && !activityStack!!.isEmpty()) {
            activityStack!!.remove(activity)
            LogUtil.i("show", "栈中的activity $activityStack     已经移除的activity   $activity")
        }
    }

    //退出栈顶Activity
    fun popActivity(activity: Activity?) {
        var activity = activity
        if (activity != null) {
            Log.i("popActivity", activity.packageName + "." + activity.localClassName)
            //在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
            activity.finish()
            activityStack!!.remove(activity)
            activity = null
        }
    }

    //退出栈顶Activity
    fun popActivity() {
        if (activityStack != null && !activityStack!!.isEmpty()) {
            //在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
            val mActivity = activityStack!!.peek()
            popActivity(mActivity)
        }
    }

    fun popActivity(context: Context?) {
        if (context != null) {
            Log.i("popActivity", context.packageName)
            //在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
            activityStack!!.pop().finish()
        }
    }

    fun peepActivity(): Activity? {
        return if (activityStack != null && !activityStack!!.isEmpty()) {
            activityStack!!.peek()
        } else null
    }


    //获得当前栈顶Activity
    fun currentActivity(): Activity? {
        var activity: Activity? = null
        if (!activityStack!!.empty())
            activity = activityStack!!.lastElement()
        return activity
    }

    //将当前Activity推入栈中
    fun pushActivity(activity: Activity) {
        if (activityStack == null) {
            activityStack = Stack()
        }
        activityStack!!.add(activity)
    }

    //退出栈中所有Activity
    fun popAllActivityExceptOne(clazz: Class<*>) {
        while (true) {
            val activity = currentActivity() ?: break
            if (activity.javaClass == clazz) {
                break
            }
            popActivity(activity)
        }
    }

    //退出栈中所有Activity
    fun close() {
        val activity = currentActivity()
        activityStack!!.remove(activity)
        //Stack<Activity> stack2 = new Stack<Activity>();
        while (true) {

            if (!activityStack!!.isEmpty())
            //stack2.push(activityStack.pop());
                activityStack!!.pop().finish()
            else {
                break
            }
        }
        //关闭项目
        activity!!.finish()
    }

    companion object {
        //peek 不改变栈的值(不删除栈顶的值)，pop会把栈顶的值删除。
        /**
         * 堆栈处理Activity
         */
        private var activityStack: Stack<Activity>? = null

        /**
         * 实现单例模式
         */
        //		if (instance == null) {
        //
        //		}
       @JvmField val instance = ActivityManager()
    }
}
