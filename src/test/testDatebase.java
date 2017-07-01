package test;

import java.util.Date;



public class testDatebase {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String date = new java.sql.Date(new Date().getTime()).toString();
		System.out.println(date);
	}

}
