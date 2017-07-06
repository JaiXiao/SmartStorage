package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android_serialport_api.SerialPort;
import android_serialport_api.demo.Application;
import android_serialport_api.demo.R;
import android_serialport_api.demo.SerialPortActivity;
import base.BaseFragment;

public class StorageInfoFragment extends BaseFragment{

	private TextView textshidu;
	private TextView textwendu;
	private ProgressBar ProgressBarshidu;
	private ProgressBar ProgressBarwendu;
	
	private TextView yanwu;
	private TextView shengyin;
	private TextView chaoshengbo;
	private Button yanwujingbao;
	private Button fangdaojingbao;
	private Button kaoqiangjingbao;
	
	
	
	protected Application mApplication;
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private BufferedReader br;
	
	private String receive = "";
	
	@Override
	public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_storage_info, null);
		textshidu = (TextView)view.findViewById(R.id.textshidu);
        textwendu = (TextView)view.findViewById(R.id.textwendu);
        ProgressBarshidu = (ProgressBar)view.findViewById(R.id.progress_horizontal_shidu);
        ProgressBarwendu = (ProgressBar)view.findViewById(R.id.progress_horizontal_wendu);
        yanwujingbao = (Button)view.findViewById(R.id.yanwujingbao);
        fangdaojingbao = (Button)view.findViewById(R.id.fangdaojingbao);
        kaoqiangjingbao = (Button)view.findViewById(R.id.kaoqiangjingbao);
		return view;
	}
	
	
	/**************************************************************************
	¹¦ÄÜÃèÊö£º¶Á´®¿ÚµÄÏß³Ì
	ÊäÈë²ÎÊý£ºÎÞ
	Êä³ö²ÎÊý£ºÎÞ
	·µ»Ø½á¹û£ºÎÞ
	*************************************************************************/
	private class ReadThread extends Thread {
		
		@Override
		public void run() {
			super.run();
			while(!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[256];
					if (mInputStream == null) return;
					size = mInputStream.read(buffer, 0, buffer.length);
					if (size > 0) {
//						String message = new String(buffer, size);
//						onDataReceived(message);
						onDataReceived(buffer, size);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	/**************************************************************************
	¹¦ÄÜÃèÊö£ºÏÔÊ¾´íÎóÐÅÏ¢
	ÊäÈë²ÎÊý£º´íÎó·û
	Êä³ö²ÎÊý£º´íÎóÐÅÏ¢£¬ÒÔ¶Ô»°¿òµÄÐÎÊ½³öÏÖ
	·µ»Ø½á¹û£ºÎÞ
	*************************************************************************/
	private void DisplayError(int resourceId) {
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		b.setTitle("Error");
		b.setMessage(resourceId);
		b.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				getActivity().finish();
			}
		});
		b.show();
	}
	
	
	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		mApplication = (Application) getActivity().getApplication();
		try {
			mSerialPort = mApplication.getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			br = new BufferedReader(new InputStreamReader(mInputStream, "UTF8"));
			/* Create a receiving thread */
			mReadThread = new ReadThread();
			mReadThread.start();
		} catch (SecurityException e) {
			DisplayError(R.string.error_security);
		} catch (IOException e) {
			DisplayError(R.string.error_unknown);
		} catch (InvalidParameterException e) {
			DisplayError(R.string.error_configuration);
		}
	}

	/**************************************************************************
	¹¦ÄÜÃèÊö£º½ÓÊÕ´®¿ÚÐÅÏ¢
	ÊäÈë²ÎÊý£ºbufferÎª´®¿Ú»º³åÇø£¬sizeÎª»º³åÇø´óÐ¡
	Êä³ö²ÎÊý£ºÎÞ
	·µ»Ø½á¹û£ºÎÞ
	*************************************************************************/
		public static boolean isNum(String str){		
	    	return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	    }
	
		int j = 0;
		int k = 0;
		boolean flag = false;
		String hand = "";
		protected void onDataReceived(final byte[] buffer, final int size){
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					if(mApplication.getFlag()==1)
					{
						if (textwendu != null) {

							String str = new String(buffer, 0, size);
							receive = receive + str;
//							System.out.println("^" + str.length()+"*"); 
							// 
							String sub = null;
							int d = 0;
							if(receive.split(",").length == 6 && receive.length() != 0 && receive.charAt(0)=='$' && receive.charAt(1)=='u'
									&& receive.charAt(receive.length() - 1)=='#') {
								System.out.println("*************");
								String message = receive;
								String[] arr = receive.split(",");
								sub = arr[3];//select the part of data
								d = Integer.parseInt(sub);
								//Humidity
								System.out.println("==================message = " + message);
								if(arr[1].equals("09")) {
									
//									System.out.println("shidu = " + StorageSettingFragment.MAX_TEMPERATURE + "%RH");
									System.out.println("==================shidu = " + sub + "%RH");
									textshidu.setText(" Êª¶È£º " + sub + "%RH");
									if(d > StorageSettingFragment.MAX_HUMIDITY)//the warning value(you can change it depend on situation)
									{
										Drawable dr = getResources().getDrawable(R.drawable.barcolor);
										ProgressBarshidu.setProgressDrawable(dr);
									}
									else
									{
										Drawable dr = getResources().getDrawable(R.drawable.nocolor);
										ProgressBarshidu.setProgressDrawable(dr);
									}
									//change the bar according to the value of data
									ProgressBarshidu.setProgress((int) d);
								}
								
								
								// =========
								
								
							}
							
							if(flag)
								hand = "";      
							while(k < receive.length()) {
								if(receive.charAt(k) == '$')   {
									break;
								} else {
									k++;
								}
							}       
							k++;
							while(k < receive.length()) {      
								if(receive.charAt(k) != '#') {
									hand = hand + String.valueOf(receive.charAt(k));
									k++;
									if(k == receive.length()) {
										k = 0;       
										flag = true;
										break; 
									}      
								} else if(receive.charAt(k) == '#') {
									receive = receive.substring(k, receive.length());
									k = 0;
									String temp[] = hand.split("\\,");	
									if(temp.length==6 || temp[0].equals("u") || temp[0].equals("d")) {
										System.out.println("***" + temp[0]+ "***" + temp[1] + "***"+ temp[2]+ "***" + temp[3]+ "***" + temp[4] + "***"+ temp[5]);
									
										if(temp[1].equals("01")) {//shidu
											String shidu = temp[3];
//											System.out.println("shidu = " + shidu + "%RH");
											textshidu.setText("Êª¶È£º " + shidu + "%RH");
											d = Integer.parseInt(shidu);
											if(d > StorageSettingFragment.MAX_HUMIDITY)//the warning value(you can change it depend on situation)
											{
												Drawable dr = getResources().getDrawable(R.drawable.barcolor);
												ProgressBarshidu.setProgressDrawable(dr);
											}
											else
											{
												Drawable dr = getResources().getDrawable(R.drawable.nocolor);
												ProgressBarshidu.setProgressDrawable(dr);
											}
											//change the bar according to the value of data
											ProgressBarshidu.setProgress((int) d);
										}else if(temp[1].equals("02")){//wendu
											String wendu = temp[3];
//											System.out.println("wendu = " + wendu + " ¡ãC");
											textwendu.setText("ÎÂ¶È£º " + wendu + " ¡ãC");
											d = Integer.parseInt(wendu);
											if(d > StorageSettingFragment.MAX_TEMPERATURE)//the warning value(you can change it depend on situation)
											{
												Drawable dr = getResources().getDrawable(R.drawable.barcolor);
												ProgressBarwendu.setProgressDrawable(dr);
											}
											else
											{
												Drawable dr = getResources().getDrawable(R.drawable.nocolor);
												ProgressBarwendu.setProgressDrawable(dr);
											}
											//change the bar according to the value of data
											ProgressBarwendu.setProgress((int) d);
										}else if(temp[1].equals("13")){//yanwu
											if(temp[3].equals("Y")){
												yanwujingbao.setBackgroundColor(Color.RED);
											}else{
												yanwujingbao.setBackgroundColor(Color.parseColor("#99CC33"));
											}
										}else if(temp[1].equals("09")){//chaoshengbo
											String chaoshengbo = temp[3];
											d = Integer.parseInt(chaoshengbo);
											if(d <= 3){
												kaoqiangjingbao.setBackgroundColor(Color.RED);
											}else{
												kaoqiangjingbao.setBackgroundColor(Color.parseColor("#99CC33"));
											}
										}
										else if(temp[1].equals("10")){//shengyin
											if(temp[3].equals("1")){
												fangdaojingbao.setBackgroundColor(Color.RED);
											}else{
												fangdaojingbao.setBackgroundColor(Color.parseColor("#99CC33"));
											}
										}
										else {
											System.out.println("*******" + receive);
										}
									}
									break;
								}
							} 
						}
					}
				}
			});

		}
		protected void onDataReceived(final String message){
			getActivity().runOnUiThread(new Runnable() {
				
				public void run() {
					String sub = null;
					int d = 0;
//					System.out.println(message);
//					System.out.println(message.length());
					if(message == "" || message == null)
						System.out.println("null");
				    //judge the format of data is true or not
					if( message.length()!= 0 && message.charAt(0)=='$' && message.charAt(1)=='u'
							&& message.charAt(message.length() - 1)=='#') {
						System.out.println("the format of data is true");
						sub = message.substring(3, message.length()-1);//select the part of data
						d = Integer.parseInt(sub);
						//Humidity
						if(message.charAt(1)=='0') {
							
							System.out.println("shidu = "+ StorageSettingFragment.MAX_TEMPERATURE + "%RH");
							System.out.println("shidu = "+ sub + "%RH");
							textshidu.setText(" Êª¶È£º "+sub + "%RH");
							if(d>45)//the warning value(you can change it depend on situation)
							{
								Drawable dr=getResources().getDrawable(R.drawable.barcolor);
								ProgressBarshidu.setProgressDrawable(dr);
							}
							else
							{
								Drawable dr=getResources().getDrawable(R.drawable.nocolor);
								ProgressBarshidu.setProgressDrawable(dr);
							}
							//change the bar according to the value of data
							ProgressBarshidu.setProgress((int) d);
						}
						
						//Temperature
						if(message.charAt(1)=='1') {
							System.out.println("wendu = "+ sub +" ¡ãC");
							textwendu.setText(" ÎÂ¶È£º "+sub + " ¡ãC");
							if(d>40)//the warning value(you can change it depend on situation)
							{
								Drawable dr=getResources().getDrawable(R.drawable.barcolor);
								ProgressBarwendu.setProgressDrawable(dr);
							}
							else
							{
								Drawable dr=getResources().getDrawable(R.drawable.nocolor);
								ProgressBarwendu.setProgressDrawable(dr);
							}
							//change the bar according to the value of data
							ProgressBarwendu.setProgress((int) d);
							
						}
						//Smoke
						//Light
						if(message.charAt(1)=='2') {

						}
						
					}
					else{
						//if the format of data is not true(I don't know what to do )
					}
					
				}
				
			});

		}
	
	@Override
	public void processClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
	
			default:
				break;
		}
	}
	
	/**************************************************************************
	¹¦ÄÜÃèÊö£ºÏú»Ùº¯Êý
	ÊäÈë²ÎÊý£ºÎÞ
	Êä³ö²ÎÊý£ºÎÞ
	·µ»Ø½á¹û£ºÎÞ
	*************************************************************************/
	@Override
	public void onDestroy() {
		if (mReadThread != null)
			mReadThread.interrupt();
		mApplication.closeSerialPort();
		mSerialPort = null;
		super.onDestroy();
	}

}
