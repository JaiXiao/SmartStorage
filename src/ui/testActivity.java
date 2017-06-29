package ui;

import android_serialport_api.demo.R;

import java.util.ArrayList;
import java.util.List;

import adapter.MainPagerAdapter;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import base.BaseActivity;

public class testActivity extends BaseActivity{
	private ViewPager viewPager;
	private TextView tv_tab_info; 
	private TextView tv_tab_setting; 
	private TextView tv_tab_goods; 
	
	private LinearLayout ll_tab_info;
	private LinearLayout ll_tab_setting;
	private LinearLayout ll_tab_goods;
	
	private List<Fragment> fragments;
	private MainPagerAdapter adapter;
	@Override
	public void initView() {
		setContentView(R.layout.activity_test);
		
		getWindow().setBackgroundDrawableResource(R.drawable.bg_shidu);//set the main background
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		
		tv_tab_info = (TextView) findViewById(R.id.tv_tab_info);
		tv_tab_setting = (TextView) findViewById(R.id.tv_tab_setting);
		tv_tab_goods = (TextView) findViewById(R.id.tv_tab_goods);
		
		ll_tab_info = (LinearLayout) findViewById(R.id.ll_tab_info);
		ll_tab_setting = (LinearLayout) findViewById(R.id.ll_tab_setting);
		ll_tab_goods = (LinearLayout) findViewById(R.id.ll_tab_goods);
	}

	@Override
	public void initListener() {
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		ll_tab_info.setOnClickListener(this);
		ll_tab_setting.setOnClickListener(this);
		ll_tab_goods.setOnClickListener(this);
	}

	@Override
	public void initData() {
		fragments = new ArrayList<Fragment>();

		StorageInfoFragment fragment1 = new StorageInfoFragment();
		StorageGoodsFragment fragment2 = new StorageGoodsFragment();
		StorageSettingFragment fragment3 = new StorageSettingFragment();
		fragments.add(fragment1);
		fragments.add(fragment2);
		fragments.add(fragment3);
		adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
		viewPager.setAdapter(adapter);
	}

	@Override
	public void processClick(View v) {
		switch (v.getId()) {
		case R.id.ll_tab_info:
			viewPager.setCurrentItem(0);
			break;
		case R.id.ll_tab_setting:
			viewPager.setCurrentItem(1);
			break;
		case R.id.ll_tab_goods:
			viewPager.setCurrentItem(2);
			break;
		default:
			break;

		}
		
	}

	@Override
	protected void onDataReceived(String message) {
		// TODO Auto-generated method stub
		
	}

}
