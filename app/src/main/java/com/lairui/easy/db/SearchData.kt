package com.lairui.easy.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase


import com.lairui.easy.basic.MbsConstans

import java.util.ArrayList
import java.util.Date
import java.util.HashMap

class SearchData private constructor() {

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

    fun selectByName(searchName: String): MutableMap<String, Any>? {
        openDb()
        if (db!!.isOpen) {
            val cursor = db!!.rawQuery("select * from tb_goods_search where search_name = '$searchName'", null)
            val idIndex = cursor.getColumnIndex("id")
            val searchNameIndex = cursor.getColumnIndex("search_name")
            val updateDateIndex = cursor.getColumnIndex("update_date")
            while (cursor.moveToNext()) {
                val id = cursor.getInt(idIndex)
                val searchName1 = cursor.getString(searchNameIndex)
                val updateDate = cursor.getString(updateDateIndex)

                val map = HashMap<String, Any>()
                map["id"] = id
                map["searchName"] = searchName1
                map["updateDate"] = updateDate
                return map
            }
            cursor.close()
            db!!.close()
        }
        return null
    }

    fun selectDB(): List<MutableMap<String, Any>> {
        val list = ArrayList<MutableMap<String, Any>>()
        openDb()
        if (db!!.isOpen) {
            val cursor = db!!.rawQuery("select * from tb_goods_search order by update_date desc", null)
            val idIndex = cursor.getColumnIndex("id")
            val searchNameIndex = cursor.getColumnIndex("search_name")
            val updateDateIndex = cursor.getColumnIndex("update_date")
            while (cursor.moveToNext()) {
                val id = cursor.getInt(idIndex)
                val searchName1 = cursor.getString(searchNameIndex)
                val updateDate = cursor.getString(updateDateIndex)

                val map = HashMap<String, Any>()
                map["id"] = id
                map["value"] = searchName1
                map["updateDate"] = updateDate
                list.add(map)
            }
            cursor.close()
            db!!.close()
        }
        return list
    }


    fun insertDB(searchName: String) {
        openDb()
        if (db!!.isOpen) {
            val values = ContentValues()
            values.put("search_name", searchName)
            values.put("update_date", Date().toString() + "")
            db!!.insert("tb_goods_search", "id", values)
            db!!.close()
        }
    }

    fun updateDB(searchName: String) {
        openDb()
        if (db!!.isOpen) {
            val contentValues = ContentValues()
            contentValues.put("update_date", Date().toString() + "")
            db!!.update("tb_goods_search", contentValues, "search_name=? ", arrayOf(searchName + ""))
            db!!.close()
        }
    }

    //清空数据
    fun clearData() {
        openDb()
        if (db!!.isOpen) {
            val sql = "DELETE FROM tb_goods_search;"
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

        private var sqliteDBHelp: SearchData? = null
        val instance: SearchData
            get() {
                if (sqliteDBHelp == null) {
                    sqliteDBHelp = SearchData()
                }
                return sqliteDBHelp!!
            }
    }

}
