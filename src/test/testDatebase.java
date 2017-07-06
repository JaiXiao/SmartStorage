package test;

import java.util.Date;
/**
 * @ClassName: testDatebase 
 * @Description: TODO 
 * @author LcritZ
 * @date 2017年7月2日 上午11:29:46 
 */
public class testDatebase {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String date = new java.sql.Date(new Date().getTime()).toString();
		System.out.println(date);
	}

}
