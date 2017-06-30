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
		db.execSQL("create table goods(_id integer primary key autoincrement, name char(20), type char(20), value integer(10))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		System.out.println("Êý¾Ý¿âÉý¼¶£¡");
	}

}
