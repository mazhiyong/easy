package com.lairui.easy.utils.tool


import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.lairui.easy.di.component.DaggerNetComponent
import java.lang.reflect.Type

import java.util.ArrayList

import javax.inject.Inject

class JSONUtil {

    @Inject
    lateinit var gson: Gson

    fun objectToJson(o: Any?): String {
        return gson!!.toJson(o)
    }

    fun jsonToList(json: String): List<MutableMap<String, Any>>? {
        var retList: List<MutableMap<String, Any>>? = null
        try {
            // json转为带泛型的list
            retList = gson!!.fromJson<List<MutableMap<String, Any>>>(json, object : TypeToken<List<MutableMap<String, Any>>>() {

            }.type)
        } catch (e: Exception) {
            return retList
        }

        return retList
    }

    fun jsonMap(json: String): MutableMap<String, Any>? {
        var map: MutableMap<String, Any>? = null
        try {
            // json转为带泛型的list
            map = gson!!.fromJson<MutableMap<String, Any>>(json, object : TypeToken<MutableMap<String, Any>>() {

            }.type)
        } catch (e: Exception) {
            return map
        }

        return map
    }

    fun <T> JsonToBean(json: String, cls: Class<T>): T? {
        var t: T? = null
        try {
            val gson = Gson()
            t = gson.fromJson(json, cls)
        } catch (e: Exception) {

        }

        return t
    }

    fun jsonToListStr2(json: String): List<List<String>>? {
        var retList: List<List<String>>? = null
        try {
            // json转为带泛型的list
            retList = gson.fromJson<List<List<String>>>(json, object : TypeToken<List<List<String>>>() {
            }.type)
        } catch (e: Exception) {
            return retList
        }

        return retList
    }


    fun <T> JsonToListBean(json: String, cls: Class<T>): List<T> {
        val retList = ArrayList<T>()
        val array = JsonParser().parse(json).asJsonArray
        for (element in array) {
            retList.add(gson!!.fromJson(element, cls))
        }
        return retList
    }

    companion object {

        var JSONUtil: JSONUtil? = null

        val instance: JSONUtil
            get() {
                if (JSONUtil == null) {
                    JSONUtil = JSONUtil()
                }
                DaggerNetComponent.create().injectTo(JSONUtil!!)
                return JSONUtil as JSONUtil
            }
    }




    /**
     * 小写下划线的格式解析JSON字符串到对象
     *
     * 例如 is_success->isSuccess
     *
     * @param json
     * @param classOfT
     * @return
     */
    fun <T> fromJsonUnderScoreStyle(json: String, classOfT: Class<T>): T {
        return gson.fromJson(json, classOfT)
    }

    /**
     * JSON字符串转为Map<String></String>,String>
     *
     * @param json
     * @return
     */
    fun <T> fronJson2Map(json: String): T {
        return gson.fromJson(json, object : TypeToken<Map<String, String>>() {

        }.type)
    }

    /**
     * 小写下划线的格式将对象转换成JSON字符串
     *
     * @param src
     * @return
     */
    fun toJson(src: Any): String {
        return gson.toJson(src)
    }

    fun toPrettyString(src: Any): String {
        return gson.toJson(src)
    }

    fun <T> fromJson2Object(src: String, t: Class<T>): T {
        return gson.fromJson(src, t)
    }

    fun <T> fromJson2Object(src: String, typeOfT: Type): T {
        return gson.fromJson(src, typeOfT)
    }

}
