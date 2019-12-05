package com.lairui.easy.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase


import com.lairui.easy.utils.tool.LogUtil
import com.lairui.easy.basic.MbsConstans

/**
 *
 * @Description:首页缓存本地数据库信息
 *
 *
 * @time 2016年5月14日 下午5:41:19
 *
 * @email:646869341@qq.com
 *
 * QQ:646869341
 */
class IndexData private constructor() {

    private var db: SQLiteDatabase? = null
    private val dbPath = MbsConstans.DATABASE_PATH + "/" + MbsConstans.DATABASE_NAME

    fun openDb() {
        db = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
    }


    fun execSQL(sql: String) {
        openDb()
        if (db!!.isOpen) {
            db!!.execSQL(sql)
            db!!.close()
        }
    }

    /**
     * 得到某个字段的值
     * @param key
     * @return
     */
    fun selectIndex(key: String): String? {
        openDb()
        if (db!!.isOpen) {
            val cursor = db!!.rawQuery("select $key from tb_index_data where id = (select MAX(id) from tb_index_data) ", null)
            val valueIndex = cursor.getColumnIndex(key)
            while (cursor.moveToNext()) {
                return cursor.getString(valueIndex)
            }
            cursor.close()
            db!!.close()
        }
        return null
    }

    /**
     * 插入本地数据库
     * @param map
     */
    fun insertDB(map: MutableMap<String, Any>) {
        openDb()
        if (db!!.isOpen) {
            val values = ContentValues()
            values.put("advert_json", map["advertJson"]!!.toString() + "")
            values.put("content_json", map["nameCodeJson"]!!.toString() + "")
            db!!.insert("tb_index_data", "id", values)
            db!!.close()
        }
    }

    /**
     * 更新本地数据库首页信息，如果没有老数据的话，插入一条新的数据
     * 如果有老数据的话，更新老数据，理想状态下，有且只有一条数据
     * @param key
     * @param value
     */
    fun updateDB(key: String, value: String) {
        openDb()

        LogUtil.i("打印log日志", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
        if (db!!.isOpen) {
            val cursor = db!!.rawQuery("select * from tb_index_data", null)
            if (cursor.count == 0) {
                val values = ContentValues()
                values.put(key, value)
                db!!.insert("tb_index_data", "id", values)
                db!!.close()
            } else {
                val contentValues = ContentValues()
                contentValues.put(key, value)
                db!!.update("tb_index_data", contentValues, null, null)
                db!!.close()
            }
        }
    }

    //清空数据
    fun clearData() {
        openDb()
        if (db!!.isOpen) {
            val sql = "DELETE FROM tb_index_data;"
            db!!.execSQL(sql)
            db!!.close()
        }
    }


    fun closeDB() {
        if (db != null && db!!.isOpen) {
            db!!.close()
        }
    }

    companion object {

        private var sqliteDBHelp: IndexData? = null
        val instance: IndexData
            get() {
                if (sqliteDBHelp == null) {
                    sqliteDBHelp = IndexData()
                }
                return sqliteDBHelp as IndexData
            }
    }

}
