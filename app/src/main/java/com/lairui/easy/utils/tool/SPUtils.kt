package com.lairui.easy.utils.tool

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

import android.content.Context
import android.content.SharedPreferences

import com.lairui.easy.utils.secret.AESHelper

object SPUtils {
    /**
     * 保存在手机里面的文件名
     */
    val FILE_NAME = "share_data"

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    fun put(context: Context, key: String, `object`: Any) {

        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)
        val editor = sp.edit()

        if (`object` is String) {
            if (UtilTools.empty(`object`)) {
                editor.putString(key, "")
            } else {
                val s = AESHelper.encrypt(`object` + "", "phb_(!%*#*@&#)ad")
                editor.putString(key, s)
            }

        } else if (`object` is Int) {
            editor.putInt(key, `object`)
        } else if (`object` is Boolean) {
            editor.putBoolean(key, `object`)
        } else if (`object` is Float) {
            editor.putFloat(key, `object`)
        } else if (`object` is Long) {
            editor.putLong(key, `object`)
        } else {
            if (UtilTools.empty(`object`)) {
                editor.putString(key, "")
            } else {
                val s = AESHelper.encrypt(`object`.toString() + "", "phb_(!%*#*@&#)ad")
                editor.putString(key, s)
            }

        }

        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    @JvmStatic
    operator fun get(context: Context, key: String, defaultObject: Any): Any? {
        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)

        if (defaultObject is String) {
            val s = sp.getString(key, defaultObject)
            if (UtilTools.empty(s)) {
                return ""
            }

            LogUtil.i("   share数据内容$key", "   解密前的信息" + s!!)

            var str: String? = ""
            try {
                str = AESHelper.decrypt(s + "", "phb_(!%*#*@&#)ad")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (UtilTools.empty(str)) {
                str = ""
            }
            LogUtil.i("    share数据内容$key", "   解密后的信息" + str!!)
            //return sp.getString(key, (String) str);
            return str
        } else if (defaultObject is Int) {
            return sp.getInt(key, defaultObject)
        } else if (defaultObject is Boolean) {
            return sp.getBoolean(key, defaultObject)
        } else if (defaultObject is Float) {
            return sp.getFloat(key, defaultObject)
        } else if (defaultObject is Long) {
            return sp.getLong(key, defaultObject)
        }

        return null
    }

    /**
     * 移除某个key值已经对应的值
     * @param context
     * @param key
     */
    fun remove(context: Context, key: String) {
        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.remove(key)
        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 清除所有数据
     * @param context
     */
    fun clear(context: Context) {
        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.clear()
        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 查询某个key是否已经存在
     * @param context
     * @param key
     * @return
     */
    fun contains(context: Context, key: String): Boolean {
        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)
        return sp.contains(key)
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    fun getAll(context: Context): Map<String, *> {
        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)
        return sp.all
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private object SharedPreferencesCompat {
        private val sApplyMethod = findApplyMethod()

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        private fun findApplyMethod(): Method? {
            try {
                val clz = SharedPreferences.Editor::class.java
                return clz.getMethod("apply")
            } catch (e: NoSuchMethodException) {
            }

            return null
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        fun apply(editor: SharedPreferences.Editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor)
                    return
                }
            } catch (e: IllegalArgumentException) {
            } catch (e: IllegalAccessException) {
            } catch (e: InvocationTargetException) {
            }

            editor.commit()
        }
    }

}