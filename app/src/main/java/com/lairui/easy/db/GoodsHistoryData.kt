package com.lairui.easy.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase


import com.lairui.easy.basic.MbsConstans

import java.util.ArrayList
import java.util.HashMap

class GoodsHistoryData private constructor() {

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

    fun selectById(goodsId: Int): MutableMap<String, Any>? {
        openDb()
        if (db!!.isOpen) {
            val cursor = db!!.rawQuery("select * from tb_goods_cart where goods_id = $goodsId and is_work = 0", null)
            val idIndex = cursor.getColumnIndex("id")
            val goodsIdIndex = cursor.getColumnIndex("goods_id")
            val goodsCountIndex = cursor.getColumnIndex("goods_count")
            val goodsAmountIndex = cursor.getColumnIndex("goods_price")
            val isWorkIndex = cursor.getColumnIndex("is_work")
            val isDeleteIndex = cursor.getColumnIndex("is_delete")
            while (cursor.moveToNext()) {
                val id = cursor.getInt(idIndex)
                val gid = cursor.getInt(goodsIdIndex)
                val goodsCount = cursor.getInt(goodsCountIndex)
                val goodsAmount = cursor.getString(goodsAmountIndex)
                val isWork = cursor.getInt(isWorkIndex)
                val isDelete = cursor.getInt(isDeleteIndex)

                val map = HashMap<String, Any>()
                map["id"] = id
                map["gid"] = gid
                map["gcount"] = goodsCount
                map["goods_price"] = goodsAmount
                map["is_work"] = isWork
                map["is_delete"] = isDelete
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
            val cursor = db!!.rawQuery("select * from tb_goods_history", null)
            val idIndex = cursor.getColumnIndex("id")
            val goodsIdIndex = cursor.getColumnIndex("goods_id")
            /*int goodsCountIndex=cursor.getColumnIndex("goods_count");
			int goodsAmountIndex=cursor.getColumnIndex("goods_price");
			int isWorkIndex=cursor.getColumnIndex("is_work");
			int isDeleteIndex=cursor.getColumnIndex("is_delete");*/
            while (cursor.moveToNext()) {
                val id = cursor.getInt(idIndex)
                val goodsId = cursor.getInt(goodsIdIndex)
                /*int goodsCount = cursor.getInt(goodsCountIndex);
				String goodsAmount = cursor.getString(goodsAmountIndex);
				int isWork = cursor.getInt(isWorkIndex);
				int isDelete = cursor.getInt(isDeleteIndex);*/

                val map = HashMap<String, Any>()
                //map.put("id", id);
                map["goodsId"] = goodsId
                //map.put("goodsCount", goodsCount);
                //map.put("goods_price", goodsAmount);
                //map.put("is_work", isWork);
                //map.put("is_delete", isDelete);
                list.add(map)
            }
            cursor.close()
            db!!.close()
        }
        return list
    }


    fun insertDB(goodsId: Int) {
        openDb()
        if (db!!.isOpen) {
            val values = ContentValues()
            values.put("goods_id", goodsId)
            db!!.insert("tb_goods_cart", "id", values)
            db!!.close()
        }
    }

    fun updateDB(goodsCount: Int, goodsId: Int) {
        openDb()
        if (db!!.isOpen) {
            val contentValues = ContentValues()
            contentValues.put("goods_count", goodsCount)
            db!!.update("tb_goods_cart", contentValues, "goods_id=? ", arrayOf(goodsId.toString() + ""))
            db!!.close()
        }
    }

    fun updateStatus(list: List<MutableMap<String, Any>>, isWork: Int) {
        openDb()
        if (db!!.isOpen) {
            for (i in list.indices) {
                val map = list[i]
                //ContentValues contentValues = new ContentValues();
                //contentValues.put("is_work", isWork);
                //db.update("tb_goods_cart", contentValues, "goods_id=?", new String[]{map.get("goodsId")+""});
                //删除数据
                db!!.delete("tb_goods_cart", "goods_id=?", arrayOf(map["goodsId"]!!.toString() + ""))
            }
            db!!.close()
        }
    }


    fun closeDB() {
        if (db != null && db!!.isOpen) {
            db!!.close()
        }
    }

    companion object {

        private var sqliteDBHelp: GoodsHistoryData? = null
        val instance: GoodsHistoryData
            get() {
                if (sqliteDBHelp == null) {
                    sqliteDBHelp = GoodsHistoryData()
                }
                return sqliteDBHelp!!
            }
    }

}
