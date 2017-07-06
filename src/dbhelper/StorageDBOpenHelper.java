package dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class StorageDBOpenHelper extends SQLiteOpenHelper{
	
	public StorageDBOpenHelper(Context context) {
        
        super(context, "storage.db", null, 1);
    }

	public StorageDBOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		//创建设置表，记录各项最大最小值
		db.execSQL("create table settings(flag integer, max decimal(8,2), min decimal(8,2))");
		
		// 创建数据库商品表字段
		// 1: 物品名称
		// 2: 物品类型
		// 3: 物品价值
		// 4: 登记日期
		
		db.execSQL("create table goods(_id integer primary key autoincrement, name char(20), type char(20), value integer(10), date char(20))");
	
		//创建温湿度表
		// 1：温度值
		// 2: 湿度值
		// 3: 记录日期
		
		db.execSQL("create table TempHumi(humidity decimal(8,2), temper(8,2), date char(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		System.out.println("数据库更新成功");
	}

}
