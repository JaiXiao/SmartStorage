package ui;

import dialog.SettingDialog;
import dialog.SettingDialog.OnSettingDialogListener;

import adapter.GoodsAdapter;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android_serialport_api.demo.R;
import base.BaseFragment;
import dbhelper.StorageDBOpenHelper;
import dialog.InputDialog;
import dialog.InputDialog.OnInputDialogListener;
import utils.ToastUtils;

public class StorageSettingFragment extends BaseFragment{

	public static double MAX_TEMPERATURE = 50;
	public static double MIN_TEMPERATURE = 0;
	
	public static double MAX_HUMIDITY = 95;
	public static double MIN_HUMIDITY = 20;
	
	public static double MAX_SMOKE = 30000;
	public static double MIN_SMOKE = 0;
	
	public static boolean IS_OPEN = true;
	
	private StorageDBOpenHelper oh; 
	private SQLiteDatabase db;
	
	
	private RelativeLayout rl_setting_switch;
	private RelativeLayout rl_setting_temperature;
	private RelativeLayout rl_setting_humidity;
	private RelativeLayout rl_setting_smoke;
	
	
	private Switch switch_setting_open;
	private TextView tv_setting_temperature;
	private TextView tv_setting_humidity;
	private TextView tv_setting_smoke;
	
	@Override
	public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_storage_setting, null);
		
		rl_setting_switch = (RelativeLayout) view.findViewById(R.id.rl_setting_switch);
		rl_setting_temperature = (RelativeLayout) view.findViewById(R.id.rl_setting_temperature);
		rl_setting_humidity = (RelativeLayout) view.findViewById(R.id.rl_setting_humidity);
		rl_setting_smoke = (RelativeLayout) view.findViewById(R.id.rl_setting_smoke);
		
		switch_setting_open = (Switch) view.findViewById(R.id.switch_setting_open);
		tv_setting_temperature = (TextView) view.findViewById(R.id.tv_setting_temperature);
		tv_setting_humidity = (TextView) view.findViewById(R.id.tv_setting_humidity);
		tv_setting_smoke = (TextView) view.findViewById(R.id.tv_setting_smoke);
		return view;
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		rl_setting_temperature.setOnClickListener(this);
		rl_setting_humidity.setOnClickListener(this);
		rl_setting_smoke.setOnClickListener(this);
		switch_setting_open.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				double flag = 1;
				if (isChecked) {  // 打开
					IS_OPEN = true;
					flag = 1;
                } else {  // 关闭
                	IS_OPEN = false;
                	flag = 0;
                } 
				ContentValues values = new ContentValues();
				values.put("max", flag);
				values.put("min", flag);
				db = oh.getWritableDatabase();
				int i = db.update("settings", values, "flag = ?", new String[]{"4"});
			}
		});
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		oh = new StorageDBOpenHelper(getActivity());
		db = oh.getWritableDatabase();
		Cursor c = db.rawQuery("select * from settings", null);
   	 	int number = c.getCount();
   	 	if(number == 0) {
   	 		// 1: 温度
   			// 2: 湿度
   			// 3: 烟雾
   			// 4: 开关
	   	 	ContentValues values = new ContentValues();
			values.put("flag", 1);
			values.put("max", 50);
			values.put("min", 0);
			db.insert("settings",null,values);
			
			values = new ContentValues();
			values.put("flag", 2);
			values.put("max", 95);
			values.put("min", 20);
			db.insert("settings",null,values);
			
			values = new ContentValues();
			values.put("flag", 3);
			values.put("max", 30000);
			values.put("min",0);
			db.insert("settings",null,values);
			
			values = new ContentValues();
			values.put("flag", 4);
			values.put("max", 1);
			values.put("min", 1);
			db.insert("settings",null,values);
			switch_setting_open.setChecked(true);
   	 	} else {
	   	 	while(c.moveToNext()){
				// 先通过列名获取列索引 然后在获取该列的内容
				int flag = c.getInt(c.getColumnIndex("flag"));
				double max = c.getDouble(c.getColumnIndex("max"));
				double min = c.getDouble(c.getColumnIndex("min"));
				if(flag == 1) {
					MAX_TEMPERATURE = max;
					MIN_TEMPERATURE = min;
				}else if(flag == 2) {
					MAX_HUMIDITY = max;
					MIN_HUMIDITY = min;
				}else if(flag == 3) {
					MAX_SMOKE = max;
					MIN_SMOKE = min;
				}else if(flag == 4) {
					if(max == 1) {
						IS_OPEN = true;
						switch_setting_open.setChecked(true);		
					}else {
						IS_OPEN = false;
						switch_setting_open.setChecked(false);
					}
				}
			}
   	 	}
   	 	tv_setting_temperature.setText(MIN_TEMPERATURE + " - " + MAX_TEMPERATURE);
   	 	tv_setting_humidity.setText(MIN_HUMIDITY + " - " + MAX_HUMIDITY);
   	 	tv_setting_smoke.setText(MIN_SMOKE + " - " + MAX_SMOKE);
	} 

	
	@Override
	public void processClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.rl_setting_temperature:
			SettingDialog.showDialog(getActivity(), "温度范围", new OnSettingDialogListener() {
				
				public void onConfirm(String max, String min) {
					// TODO Auto-generated method stub
					if(!TextUtils.isEmpty(max) && !TextUtils.isEmpty(min)){
						MAX_TEMPERATURE = Double.parseDouble(max);
						MIN_TEMPERATURE = Double.parseDouble(min);
						ContentValues values = new ContentValues();
						values.put("max", MAX_TEMPERATURE);
						values.put("min", MIN_TEMPERATURE);
						db = oh.getWritableDatabase();
						int i = db.update("settings", values, "flag = ?", new String[]{"1"});
					
						tv_setting_temperature.setText(MIN_TEMPERATURE + " - " + MAX_TEMPERATURE);
					}else {
						ToastUtils.ShowShortToast(getActivity(), "信息不可以为空!");
					}
				}
				
				public void onCancel() {
					// TODO Auto-generated method stub
					
				}
			});
			
			break;
		case R.id.rl_setting_humidity:
			SettingDialog.showDialog(getActivity(), "湿度范围", new OnSettingDialogListener() {
				
				public void onConfirm(String max, String min) {
					// TODO Auto-generated method stub
					if(!TextUtils.isEmpty(max) && !TextUtils.isEmpty(min)){
						MAX_HUMIDITY = Double.parseDouble(max);
						MIN_HUMIDITY = Double.parseDouble(min);
						ContentValues values = new ContentValues();
						values.put("max", MAX_HUMIDITY);
						values.put("min", MIN_HUMIDITY);
						db = oh.getWritableDatabase();
						int i = db.update("settings", values, "flag = ?", new String[]{"2"});
					
						tv_setting_humidity.setText(MIN_HUMIDITY + " - " + MAX_HUMIDITY);
					}else {
						ToastUtils.ShowShortToast(getActivity(), "信息不可以为空!");
					}
				}
				
				public void onCancel() {
					// TODO Auto-generated method stub
					
				}
			});
			break;
		case R.id.rl_setting_smoke:
			SettingDialog.showDialog(getActivity(), "烟雾范围", new OnSettingDialogListener() {
				
				public void onConfirm(String max, String min) {
					// TODO Auto-generated method stub
					if(!TextUtils.isEmpty(max) && !TextUtils.isEmpty(min)){
						MAX_SMOKE = Double.parseDouble(max);
						MIN_SMOKE = Double.parseDouble(min);
						ContentValues values = new ContentValues();
						values.put("max", MAX_SMOKE);
						values.put("min", MIN_SMOKE);
						db = oh.getWritableDatabase();
						int i = db.update("settings", values, "flag = ?", new String[]{"3"});
					
						tv_setting_smoke.setText(MIN_SMOKE + " - " + MAX_SMOKE);
					}else {
						ToastUtils.ShowShortToast(getActivity(), "信息不可以为空!");
					}
				}
				
				public void onCancel() {
					// TODO Auto-generated method stub
					
				}
			});
			break;
		}
	}

}
