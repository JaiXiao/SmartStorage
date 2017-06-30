package ui;

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
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android_serialport_api.demo.R;
import base.BaseFragment;
import dbhelper.StorageDBOpenHelper;
import dialog.InputDialog;
import dialog.InputDialog.OnInputDialogListener;
import utils.ToastUtils;

public class StorageSettingFragment extends BaseFragment{

	@Override
	public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_storage_setting, null);
	
		return view;
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void processClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
