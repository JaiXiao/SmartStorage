package base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android_serialport_api.demo.SerialPortActivity;

public abstract class BaseActivity extends SerialPortActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initView();  // 初始化View
		initListener();  // 初始化监听器
		initData();  // 初始化数据
	}
	
	public abstract void initView();
	public abstract void initListener();
	public abstract void initData();
	public abstract void processClick(View v);
	
	public void onClick(View v) {
		processClick(v);
		
	}
}

