package bean;


import android.database.Cursor;

public class Good {
	private String name;
	private String type;
	private int value;
	
	public Good(String name, String type, int value) {
		super();
		this.name = name;
		this.type = type;
		this.value = value;
	}
	public static Good createFromCursor(Cursor cursor) {
		String name = cursor.getString(cursor.getColumnIndex("name"));
		String type = cursor.getString(cursor.getColumnIndex("type"));
		int value = cursor.getInt(cursor.getColumnIndex("value"));
		
		return new Good(name, type, value);
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
