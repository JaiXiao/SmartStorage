package com.yang.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class StorageDBOpenHelper extends SQLiteOpenHelper{
	
	public StorageDBOpenHelper(Context context) {
        //arg1: 数据库文件的名字
        //arg2: 游标工厂，等同于结果集，null代表使用默认工厂
        //arg3: 版本号，不能小于1
        super(context, "storage.db", null, 1);
    }

	public StorageDBOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table goods(_id integer primary key autoincrement, name char(20), type char(20), value integer(10))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		System.out.println("数据库升级");
	}

}
