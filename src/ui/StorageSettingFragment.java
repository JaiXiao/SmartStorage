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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android_serialport_api.demo.R;
import base.BaseFragment;
import dbhelper.StorageDBOpenHelper;
import dialog.InputDialog;
import dialog.InputDialog.OnInputDialogListener;
import utils.ToastUtils;

public class StorageSettingFragment extends BaseFragment{

	public static double MAX_TEMPERATURE = 0;
	public static double MIN_TEMPERATURE = 0;
	
	public static double MAX_HUMIDITY = 0;
	public static double MIN_HUMIDITY = 0;
	
	public static double MAX_SMOKE = 0;
	public static double MIN_SMOKE = 0;
	
	private StorageDBOpenHelper oh; 
	private SQLiteDatabase db;
	
	private RelativeLayout rl_setting_temperature;
	private RelativeLayout rl_setting_humidity;
	private RelativeLayout rl_setting_smoke;
	
	@Override
	public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_storage_setting, null);
		
		rl_setting_temperature = (RelativeLayout) view.findViewById(R.id.rl_setting_temperature);
		rl_setting_humidity = (RelativeLayout) view.findViewById(R.id.rl_setting_humidity);
		rl_setting_smoke = (RelativeLayout) view.findViewById(R.id.rl_setting_smoke);
		return view;
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		rl_setting_temperature.setOnClickListener(this);
		rl_setting_humidity.setOnClickListener(this);
		rl_setting_smoke.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
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
