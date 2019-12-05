package com.lairui.easy.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log

import com.lairui.easy.basic.MbsConstans

import java.util.ArrayList
import java.util.HashMap

class FaPiaoData private constructor() {
    private var db: SQLiteDatabase? = null
    private val dbPath = MbsConstans.DATABASE_PATH + "/" + MbsConstans.DATABASE_NAME

    /**
     * 打开数据库
     */
    fun openDb() {
        db = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
    }

    /**
     * 执行SQL语句
     * @param sql
     */
    fun execSQL(sql: String) {
        openDb()
        if (db!!.isOpen) {
            db!!.execSQL(sql)
            closeDB()
        }
    }

    /**
     * 条件查询(判断当前发票数据是否已存在)
     * @param fp_code  发票代码
     * @param fp_number 发票号码
     * @return
     */
    fun dataExist(fp_code: String, fp_number: String): Boolean {
        openDb()
        if (db!!.isOpen) {
            val cursor = db!!.rawQuery("select * from tb_fapiao_data where fp_code = " + fp_code
                    + " and fp_number =" + fp_number, null)
            val idIndex = cursor.getColumnIndex("id")
            val fp_codeIndex = cursor.getColumnIndex("fp_code")
            val fp_moneyIndex = cursor.getColumnIndex("fp_money")
            val fp_numberIndex = cursor.getColumnIndex("fp_number")
            val fp_dateIndex = cursor.getColumnIndex("fp_date")
            while (cursor.moveToNext()) {
                val id = cursor.getInt(idIndex)
                val code = cursor.getString(fp_codeIndex)
                val money = cursor.getString(fp_moneyIndex)
                val number = cursor.getString(fp_numberIndex)
                val date = cursor.getString(fp_dateIndex)
                if (fp_code == code && fp_number == number) {

                    /*	Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", id);
					map.put("fp_code", code);
					map.put("fp_number",number);
					map.put("fp_money", money);
					map.put("fp_date", date);*/
                    cursor.close()
                    closeDB()
                    return true
                }
            }
            cursor.close()
            closeDB()
        }
        return false
    }

    /**
     * 查询数据库列表
     * @return
     */
    fun selectDB(): List<MutableMap<String, Any>> {
        val list = ArrayList<MutableMap<String, Any>>()
        openDb()
        if (db!!.isOpen) {
            val cursor = db!!.rawQuery("select * from tb_fapiao_data", null)
            val idIndex = cursor.getColumnIndex("id")
            val fp_codeIndex = cursor.getColumnIndex("fp_code")
            val fp_moneyIndex = cursor.getColumnIndex("fp_money")
            val fp_numberIndex = cursor.getColumnIndex("fp_number")
            val fp_dateIndex = cursor.getColumnIndex("fp_date")

            while (cursor.moveToNext()) {
                val id = cursor.getInt(idIndex)
                val fp_code = cursor.getString(fp_codeIndex)
                val fp_money = cursor.getString(fp_moneyIndex)
                val fp_number = cursor.getString(fp_numberIndex)
                val fp_date = cursor.getString(fp_dateIndex)

                val map = HashMap<String, Any>()
                map["id"] = id
                map["fp_code"] = fp_code
                map["fp_number"] = fp_number
                map["fp_money"] = fp_money
                map["fp_date"] = fp_date
                list.add(map)
            }
            cursor.close()
            closeDB()
        }
        return list
    }

    /**
     * 批量查询数据库
     * @return
     */
    fun selectDBByListKey(keyList: List<MutableMap<String, Any>>): List<MutableMap<String, Any>> {
        val list = ArrayList<MutableMap<String, Any>>()
        openDb()
        if (db!!.isOpen) {
            val cursor = db!!.rawQuery("select * from tb_fapiao_data", null)
            val idIndex = cursor.getColumnIndex("id")
            val fp_codeIndex = cursor.getColumnIndex("fp_code")
            val fp_moneyIndex = cursor.getColumnIndex("fp_money")
            val fp_numberIndex = cursor.getColumnIndex("fp_number")
            val fp_dateIndex = cursor.getColumnIndex("fp_date")

            while (cursor.moveToNext()) {
                val id = cursor.getInt(idIndex)
                val fp_code = cursor.getString(fp_codeIndex)
                val fp_money = cursor.getString(fp_moneyIndex)
                val fp_number = cursor.getString(fp_numberIndex)
                val fp_date = cursor.getString(fp_dateIndex)

                for (key in keyList) {
                    val keyCode = key["fp_code"]!!.toString() + ""
                    if (key.equals(fp_code)) {
                        val map = HashMap<String, Any>()
                        map["id"] = id
                        map["fp_code"] = fp_code
                        map["fp_number"] = fp_number
                        map["fp_money"] = fp_money
                        map["fp_date"] = fp_date
                        list.add(map)
                    }
                }
            }
            cursor.close()
            closeDB()
        }
        return list
    }


    /**
     * 插入数据
     * @param
     */
    fun insertDB(fp_code: String, fp_number: String, fp_money: String, fp_date: String) {
        openDb()
        if (db!!.isOpen) {
            val values = ContentValues()
            values.put("fp_code", fp_code)
            values.put("fp_money", fp_money)
            values.put("fp_number", fp_number)
            values.put("fp_date", fp_date)
            db!!.insert("tb_fapiao_data", "id", values)
            closeDB()
        }
    }


    /***
     * 清空数据
     */
    fun clearData() {
        openDb()
        if (db!!.isOpen) {
            val sql = "DELETE FROM tb_fapiao_data;"
            db!!.execSQL(sql)
            Log.i("show", "清空成功")
            closeDB()
        }
    }


    /**
     * 关闭数据库
     */
    fun closeDB() {
        if (db != null && db!!.isOpen) {
            db!!.close()
        }
    }

    companion object {

        private var sqliteDBHelp: FaPiaoData? = null
        val instance: FaPiaoData
            get() {
                if (sqliteDBHelp == null) {
                    sqliteDBHelp = FaPiaoData()
                }
                return sqliteDBHelp as FaPiaoData
            }
    }


}
