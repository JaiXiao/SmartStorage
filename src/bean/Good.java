package bean;


import android.database.Cursor;

public class Good {
	private String name;
	private String type;
	private int value;
	private String date;
	
	public Good(String name, String type, int value, String date) {
		super();
		this.name = name;
		this.type = type;
		this.value = value;
		this.date = date;
	}
	public static Good createFromCursor(Cursor cursor) {
		String name = cursor.getString(cursor.getColumnIndex("name"));
		String type = cursor.getString(cursor.getColumnIndex("type"));
		int value = cursor.getInt(cursor.getColumnIndex("value"));
		String date = cursor.getString(cursor.getColumnIndex("date"));
		
		return new Good(name, type, value, date);
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}	
	
}
