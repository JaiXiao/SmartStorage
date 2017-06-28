package ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android_serialport_api.demo.R;
import base.BaseFragment;

public class StorageInfoGoodsFragment extends BaseFragment{

	private TextView textshidu;
	
	@Override
	public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_main, null);
		
		textshidu = (TextView)view.findViewById(R.id.textshidu);
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
		switch (v.getId()) {
			case R.id.textshidu:
				
				break;
			default:
				break;
		}
	}

}
