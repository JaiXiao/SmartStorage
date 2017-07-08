package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
import dbhelper.StorageDBOpenHelper;

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
	private int humiTempCount = 1;//每接收humiTempCount个新数据，就存储一条温湿度数据到表单中
	private ContentValues values = null;
	
	private StorageDBOpenHelper oh; 
	private SQLiteDatabase db = null;
	
	private LineChart mChart;
	private int drawCount = 1;
	Cursor c = null;
	LineData data = new LineData();
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
        mChart = (LineChart)view.findViewById(R.id.HumidityChart);
		return view;
	}
	
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
	功能描述：显示错误信息
	输入参数：错误符
	输出参数：错误信息，以对话框的形式出现
	返回结果：无
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
		oh = new StorageDBOpenHelper(getActivity());
		db = oh.getWritableDatabase();
		c = db.query("TempHumi", null, null, null, null, null, null);
		
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
		
		initDrawGraph();//对温湿度折线图进行基本配置
	}
	
	
	/**************************************************************************
	功能描述：初始化折现图（对温湿度折线图进行基本配置）
	输入参数：无
	输出参数：无
	返回结果：无
	*************************************************************************/
	private void initDrawGraph(){
		mChart.setDescription("仓库温湿度监测");  
	      mChart.setNoDataTextDescription("暂时尚无数据");  
	      // 可触摸
	      mChart.setTouchEnabled(true);  
	      // 可拖曳  
	      mChart.setDragEnabled(true);  
	      // 可缩放  
	      mChart.setScaleEnabled(true);  
	      mChart.setDrawGridBackground(false);  
	      mChart.setPinchZoom(true);  
	      // 设置图表的背景颜色  
	      mChart.setBackgroundColor(Color.WHITE);  
	      // 数据显示的颜色  
	      data.setValueTextColor(Color.BLACK);  

	      // 先增加一个空的数据，随后往里面动态添加  
	      mChart.setData(data);  

	      // 图表的注解(只有当数据集存在时候才生效)  
	  	  Legend l = mChart.getLegend();  
	      // 可以修改图表注解部分的位置  
	      // l.setPosition(LegendPosition.LEFT_OF_CHART);  
	      // 线性，也可是圆  
	      l.setForm(LegendForm.LINE);  
	      // 颜色  
	      l.setTextColor(Color.RED);  
	      
	      // x坐标轴  
	      XAxis xl = mChart.getXAxis();  
	      xl.setTextColor(Color.BLACK);  
	      xl.setDrawGridLines(false);  
	      xl.setAvoidFirstLastClipping(true);  
	      // 几个x坐标轴之间才绘制
	      xl.setSpaceBetweenLabels(5);  
	      // 如果false，那么x坐标轴将不可见  
	      xl.setEnabled(true);  
	      // 将X坐标轴放置在底部，默认是在顶部。  
	      xl.setPosition(XAxisPosition.BOTTOM);  

	      // 图表左边的y坐标轴线  
	      YAxis leftAxis = mChart.getAxisLeft();  
	      leftAxis.setTextColor(Color.BLACK);  
	      // 最大值  
	      leftAxis.setAxisMaxValue(40f);  
	      // 最小值  
	      leftAxis.setAxisMinValue(20f);  
	      // 不一定要从0开始  
	      leftAxis.setStartAtZero(false);  
	      leftAxis.setDrawGridLines(true);  
	      YAxis rightAxis = mChart.getAxisRight();  
	      // 不显示图表的右边y坐标轴线  
	      rightAxis.setEnabled(false);
	}
	
	
	
	/**************************************************************************
	功能描述：画温湿度折线图
	输入参数：无
	输出参数：无
	返回结果：无
	*************************************************************************/
	private void drawGraph() {
		 c = db.query("TempHumi", null, null, null, null, null, null);//cursor需要重新关联表单
		 LineData data = mChart.getData();
		 data.addXValue((data.getXValCount()) + ""); 
		 
		 LineDataSet humiditySet = data.getDataSetByIndex(0);//湿度折线图数据集合
		 if (humiditySet == null) {  
			 humiditySet = createLineDataSetHumi();  
	            data.addDataSet(humiditySet);  
	     }
		 
		 LineDataSet temperSet = data.getDataSetByIndex(1);//温度折线图数据集合
		 if (temperSet == null) {  
			 temperSet = createLineDataSetTemper();  
	            data.addDataSet(temperSet);  
	     }
		 
		 c.moveToLast();//将光标指向温湿度表单的最后一行
		 String humidity = c.getString(c.getColumnIndex("humidity"));
		 String temper = c.getString(c.getColumnIndex("temper"));
		 System.out.println("c.getCount()="+c.getCount());//显示当前温湿度表单的总行数
		 System.out.println("humidity = ****"+ humidity);
		 System.out.println("temper = ****"+ temper);
		 
		 Entry humidityEntry = new Entry((Float.parseFloat(humidity)), humiditySet.getEntryCount());
		 Entry temperEntry = new Entry((Float.parseFloat(temper)), temperSet.getEntryCount());
		 
		// 往LineData里面添加点。注意：addEntry的第二个参数即代表折线的下标索引。  
	    // 因为本例有两个个统计折线，（依据下标索引）统计折线添加
	     data.addEntry(humidityEntry, 0);  
	     data.addEntry(temperEntry, 1);
	     
	    // 通知数据更新  
	     mChart.notifyDataSetChanged();
	     
	    // 当前统计图表中最多在x轴坐标线上显示的总量  
	     mChart.setVisibleXRangeMaximum(5);  
	    // 此代码将刷新图表的绘图  
	     mChart.moveViewToX(data.getXValCount() - 5);  
	}
	
	
	
	/**************************************************************************
	功能描述：创建湿度折线图数据集合
	输入参数：无
	输出参数：无
	返回结果：无
	*************************************************************************/	
    private LineDataSet createLineDataSetHumi() {  
        LineDataSet set = new LineDataSet(null, "动态添加的数据");  
        set.setAxisDependency(AxisDependency.LEFT);  
        
        // 折线的颜色  
        set.setColor(ColorTemplate.getHoloBlue());  
//	    set.setColor(Color.parseColor("#6699FF"));;  
        
        set.setCircleColor(Color.BLACK);  
        set.setLineWidth(10f);  
        set.setCircleSize(5f);  
        set.setFillAlpha(128);  
        set.setFillColor(ColorTemplate.getHoloBlue());  
        set.setHighLightColor(Color.GREEN);  
        set.setValueTextColor(Color.RED);  
        set.setValueTextSize(10f);  
        set.setDrawValues(true);  
        return set;  
    } 
    
    
    
	/**************************************************************************
	功能描述：创建温度折线图数据集合
	输入参数：无
	输出参数：无
	返回结果：无
	*************************************************************************/	
    private LineDataSet createLineDataSetTemper() {  
        LineDataSet set = new LineDataSet(null, "动态添加的数据");  
        set.setAxisDependency(AxisDependency.LEFT);  
        
        // 折线的颜色  
        set.setColor(Color.parseColor("#FFCC00"));  
  
        set.setCircleColor(Color.BLACK);  
        set.setLineWidth(10f);  
        set.setCircleSize(5f);  
        set.setFillAlpha(128);  
        set.setFillColor(ColorTemplate.getHoloBlue());  
        set.setHighLightColor(Color.GREEN);  
        set.setValueTextColor(Color.RED);  
        set.setValueTextSize(10f);  
        set.setDrawValues(true);  
        return set;  
    } 
    
    
    
	/**************************************************************************
	功能描述：判断是否为合法数据
	输入参数：接受的字符串
	输出参数：无
	返回结果：无
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
					if(mApplication.getFlag()==1){
						if (textwendu != null) {

							String str = new String(buffer, 0, size);
							receive = receive + str;
							String sub = null;
							int d = 0;
							
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
								if(StorageSettingFragment.IS_OPEN == true){
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
										if(temp.length==6 &&( temp[0].equals("u") || temp[0].equals("d") )) {
											System.out.println("***" + temp[0]+ "***" + temp[1] + "***"+ temp[2]+ "***" + temp[3]+ "***" + temp[4] + "***"+ temp[5]);
											if (humiTempCount == 1)	{values = new ContentValues();}
											if(temp[1].equals("01")) {//shidu
												humiTempCount--;
												String shidu = temp[3];
	//											System.out.println("shidu = " + shidu + "%RH");
												textshidu.setText(" 湿度：" + shidu + "%RH");
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
												if(humiTempCount <= 0){
													values.put("Humidity", d);
													System.out.println("d_Humidity = "+d);
													if(!db.isOpen()) {
														db = oh.getWritableDatabase();
													}
												}
											}else if(temp[1].equals("02")){//wendu
												String wendu = temp[3];
	//											System.out.println("wendu = " + wendu + " ¡ãC");
												textwendu.setText(" 温度：" + wendu + "℃");
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
												if(humiTempCount <= 0){
													values.put("Temper", d);
													if(!db.isOpen()) {
														db = oh.getWritableDatabase();
													}
													System.out.println("d_Temper = "+d);
													values.put("date", new java.sql.Date(new Date().getTime()).toString());
													db.insert("TempHumi", null, values);
													values = null;
													humiTempCount = 1;
													drawCount--;
												}
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
											if(drawCount <= 0){
												// 画温湿度折线图
												drawGraph();
												drawCount = 1;
											}
										}
										break;
									}
								}
							}
						}
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
	功能描述：销毁函数
	输入参数：无
	输出参数：无
	返回结果：无
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
