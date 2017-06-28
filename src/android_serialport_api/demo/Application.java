/*****************************************************************
�ļ�����Application.java
�汾�ţ�v1.0
�������ڣ�2013-5-17
���ߣ���������Ƽ����޹�˾, www.fesxp.com
��Ҫ����������SerialPort getSerialPort()��ô򿪵Ĵ��ڣ�closeSerialPort()�رմ򿪵Ĵ���
�޸���־����
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

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;


import android.content.SharedPreferences;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

public class Application extends android.app.Application {

	public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
	private SerialPort mSerialPort = null;
/**************************************************************************
�����������򿪴���ttySAC0�����ò�����Ϊ115200;
�����������
�����������
���ؽ����ttySAC0���ڶ���
*************************************************************************/
	
	public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPort == null) {
			String path="/dev/ttySAC2";
			int baudrate=115200;
			/* Open the serial port */
			mSerialPort = new SerialPort(new File(path), baudrate, 0);
		}
		return mSerialPort;
	}
/**************************************************************************
�����������رմ���
�����������
�����������
���ؽ������
*************************************************************************/

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}
}
