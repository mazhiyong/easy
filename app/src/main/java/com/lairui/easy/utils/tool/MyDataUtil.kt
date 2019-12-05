package com.lairui.easy.utils.tool

import java.util.HashMap

/**
 * 使用单例和弱引用解决崩溃问题
 */
class MyDataUtil private constructor() {
    private val data: MutableMap<String, Any>?

    init {
        data = HashMap()
    }

    fun save(id: String, `object`: Any) {
        if (data != null) {
            data[id] = `object`
        }
    }

    fun retrieve(id: String): Any? {
        if (data == null || mInstance == null) {
            throw RuntimeException("你必须先初始化")
        }
        return data[id]
    }

    companion object {
        private var mInstance: MyDataUtil? = null

        val instance: MyDataUtil?
            get() {
                if (mInstance == null) {
                    synchronized(MyDataUtil::class.java) {
                        if (mInstance == null) {
                            mInstance = MyDataUtil()
                        }
                    }
                }
                return mInstance
            }
    }
}
