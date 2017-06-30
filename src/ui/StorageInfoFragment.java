package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	private TextView textyanwu;
	private ProgressBar ProgressBarshidu;
	private ProgressBar ProgressBarwendu;
	private ProgressBar ProgressBaryanwu;
	
	
	protected Application mApplication;
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private BufferedReader br;
	
	@Override
	public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_storage_info, null);
		textshidu = (TextView)view.findViewById(R.id.textshidu);
        textwendu=(TextView)view.findViewById(R.id.textwendu);
        textyanwu=(TextView)view.findViewById(R.id.textyanwu);
        ProgressBarshidu=(ProgressBar)view.findViewById(R.id.progress_horizontal_shidu);
        ProgressBarwendu=(ProgressBar)view.findViewById(R.id.progress_horizontal_wendu);
        ProgressBaryanwu=(ProgressBar)view.findViewById(R.id.progress_horizontal_yanwu);
		return view;
	}
	
	
	/**************************************************************************
	���������������ڵ��߳�
	�����������
	�����������
	���ؽ������
	*************************************************************************/
	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while(!isInterrupted()) {
				int size;
				try {
//					byte[] buffer = new byte[256];
					if (br == null) return;
					String message = null;
					while((message = br.readLine())!=null)
					{
						System.out.println("^"+message+"^");
						onDataReceived(message);
					}
					
//					size = mInputStream.read(buffer);
//					if (size > 0) {
//						onDataReceived(buffer, size);
//					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	/**************************************************************************
	������������ʾ������Ϣ
	��������������
	���������������Ϣ���ԶԻ������ʽ����
	���ؽ������
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
			br = new BufferedReader(new InputStreamReader(mInputStream));
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
	�������������մ�����Ϣ
	���������bufferΪ���ڻ�������sizeΪ��������С
	�����������
	���ؽ������
	*************************************************************************/
	//	protected abstract void onDataReceived(final byte[] buffer, final int size);
		protected void onDataReceived(final String message){
			getActivity().runOnUiThread(new Runnable() {
				
				public void run() {
					String sub = null;
					int d = 0;
					System.out.println(message);
					System.out.println(message.length());
					if(message == "" || message == null)
						System.out.println("null");
				    //judge the format of data is true or not
					if( message.length()!= 0 && message.charAt(0)=='$' && message.charAt(2)==','
							&& message.charAt(message.length()-1)=='#') {
						System.out.println("the format of data is true");
						sub = message.substring(3, message.length()-1);//select the part of data
						d = Integer.parseInt(sub);
						//Humidity
						if(message.charAt(1)=='0') {
							System.out.println("shidu = "+ sub + "%RH");
							textshidu.setText(" ʪ�ȣ� "+sub + "%RH");
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
							System.out.println("wendu = "+ sub +" ��C");
							textwendu.setText(" �¶ȣ� "+sub + " ��C");
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
						if(message.charAt(1)=='3') {
							System.out.println("yanwu = "+ sub);
							textyanwu.setText(" ���� "+sub + "PM");
							if(d>30000)//the warning value(you can change it depend on situation)
							{
								Drawable dr=getResources().getDrawable(R.drawable.barcolor);
								ProgressBaryanwu.setProgressDrawable(dr);
							}
							else
							{
								Drawable dr=getResources().getDrawable(R.drawable.nocolor);
								ProgressBaryanwu.setProgressDrawable(dr);
							}
							//change the bar according to the value of data
							ProgressBaryanwu.setProgress((int) d);
						}
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
	�������������ٺ���
	�����������
	�����������
	���ؽ������
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
