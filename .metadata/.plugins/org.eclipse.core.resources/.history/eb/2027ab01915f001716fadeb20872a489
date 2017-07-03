/*****************************************************************
文件名：Application.java
版本号：v1.0
创建日期：2013-5-17
作者：大连飞翔科技有限公司, www.fesxp.com
主要函数描述：SerialPort getSerialPort()获得打开的串口；closeSerialPort()关闭打开的串口
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
功能描述：打开串口ttySAC0并设置波特率为115200;
输入参数：无
输出参数：无
返回结果：ttySAC0串口对象
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
功能描述：关闭串口
输入参数：无
输出参数：无
返回结果：无
*************************************************************************/

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}
}
