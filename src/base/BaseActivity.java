package base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android_serialport_api.demo.SerialPortActivity;

/**
 * @ClassName: BaseActivity 
 * @Description: TODO 
 * @author 
 * @date 2017年7月2日 上午11:26:51 
 */
public abstract class BaseActivity extends FragmentActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initView();  // 初始化界面
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

