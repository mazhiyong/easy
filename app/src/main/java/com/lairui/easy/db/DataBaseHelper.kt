package com.lairui.easy.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 *
 * @description: 数据库操作的类
 *
 *
 * @date：2015年12月23日 上午10:27:32
 *
 * @E-mail: 646869341@qq.com
 */
class DataBaseHelper(context: Context, name: String, factory: CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {

        Log.i("show", "SQLLite  OnCreate")
        //创建购物车信息表
        db.execSQL("create table if not exists tb_goods_cart(id integer primary key,goods_id integer,goods_count integer,goods_price text,is_work integer,is_delete integer,if_activity text,is_gift text)")
        //创建搜索表
        db.execSQL("create table if not exists tb_goods_search(id integer primary key,search_name text,update_date text)")
        //创建商品浏览记录表
        db.execSQL("create table if not exists tb_goods_history(id integer primary key,goods_id integer)")
        //创建首页缓存表
        db.execSQL("create table if not exists tb_index_data(id integer primary key,advert_json text,content_json text)")
        //创建发票信息表
        db.execSQL("create table if not exists tb_fapiao_data(id integer primary key,fp_code text,fp_money text,fp_number text,fp_date text)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {// 如果有字段变动
        for (i in oldVersion + 1..newVersion) {// 迭代升级(跨版本升级)
            when (i) {
                2// 数据库版本号为2时
                -> {
                }
                3// 数据库版本号为3时
                -> {
                }
                4// 数据库版本号为4时
                -> {
                }
                5// 数据库版本号为5时
                -> {
                }
                6 -> {
                }
            }//String sql1 = "ALTER TABLE tb_goods_cart ADD COLUMN if_activity varchar(20)";
            //db.execSQL(sql1);
            //String sql2 = "ALTER TABLE tb_goods_cart ADD COLUMN is_gift varchar(20)";
            //db.execSQL(sql2);
            //String sql1 = "ALTER TABLE tb_goods_cart ADD COLUMN ss varchar(20)";
            //db.execSQL(sql1);

        }
        onCreate(db)// 重新执行oncreate初始化数据库 注意版本号的变动 要大于oldversion
    }
}
