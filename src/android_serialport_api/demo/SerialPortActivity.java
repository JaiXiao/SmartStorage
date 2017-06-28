/*****************************************************************
文件名：SerialPortActivity.java
版本号：v1.0
创建日期：2013-5-17
作者：大连飞翔科技有限公司, www.fesxp.com
主要函数描述：ReadThread创建一个读串口线程，读取串口信息；
DisplayError(int resourceId)显示错误信息；
onDataReceived(final byte[] buffer, final int size)接收串口信息；
onDestroy()销毁析构函数
修改日志：无
*****************************************************************/
/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android_serialport_api.SerialPort;
import android_serialport_api.demo.R;

public abstract class SerialPortActivity extends Activity {

	protected Application mApplication;
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private BufferedReader br;
	/**************************************************************************
	功能描述：读串口的线程
	输入参数：无
	输出参数：无
	返回结果：无
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
	功能描述：显示错误信息
	输入参数：错误符
	输出参数：错误信息，以对话框的形式出现
	返回结果：无
	*************************************************************************/

	private void DisplayError(int resourceId) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Error");
		b.setMessage(resourceId);
		b.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				SerialPortActivity.this.finish();
			}
		});
		b.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (Application) getApplication();
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
	功能描述：接收串口信息
	输入参数：buffer为串口缓冲区，size为缓冲区大小
	输出参数：无
	返回结果：无
	*************************************************************************/

//	protected abstract void onDataReceived(final byte[] buffer, final int size);
	protected abstract void onDataReceived(final String message);
	/**************************************************************************
	功能描述：销毁函数
	输入参数：无
	输出参数：无
	返回结果：无
	*************************************************************************/

	@Override
	protected void onDestroy() {
		if (mReadThread != null)
			mReadThread.interrupt();
		mApplication.closeSerialPort();
		mSerialPort = null;
		super.onDestroy();
	}
}
