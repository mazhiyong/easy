package com.lairui.easy.utils.tool

import java.lang.ref.WeakReference
import java.util.HashMap

class DataHolder {
    internal var data: MutableMap<String, WeakReference<List<MutableMap<String, Any>>?>> = HashMap()
    fun save(id: String, `object`: List<MutableMap<String, Any>>?) {
        data[id] = WeakReference(`object`)
    }

    fun retrieve(id: String): Any? {
        val objectWeakReference = data[id]
        return objectWeakReference!!.get()
    }

    companion object {

        private var mInstance: DataHolder? = null

        val instance: DataHolder?
            get() {
                if (mInstance == null) {
                    synchronized(DataHolder::class.java) {
                        if (mInstance == null) {
                            mInstance = DataHolder()
                        }
                    }
                }
                return this!!.mInstance
            }
    }
}