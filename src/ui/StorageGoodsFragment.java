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

public class StorageGoodsFragment extends BaseFragment{

	private StorageDBOpenHelper oh = null; 
	private SQLiteDatabase db = null;
	private Cursor cursor;
	
	
    private ListView lv;
    private Button btn_addgoods;
    private GoodsAdapter goodsAdapter;
    
    private int longClickPosition;
    private PopupWindow popupWindow;
    private TextView tvDelte;
	@Override
	public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_storage_goods, null);
		
		lv = (ListView) view.findViewById(R.id.lv_goods);
		btn_addgoods = (Button) view.findViewById(R.id.btn_addgoods);
		return view;
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id){
				longClickPosition = position;
				if(null == popupWindow){
					View popView = getActivity().getLayoutInflater().inflate(R.layout.layout_long_click_dialog, null);
					tvDelte = (TextView) popView.findViewById(R.id.tv_delete);
					tvDelte.setOnClickListener(clickListener);
					popupWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					popupWindow.setAnimationStyle(R.style.PopAnimStyle);
					popupWindow.setOutsideTouchable(true);
					popupWindow.setBackgroundDrawable(new BitmapDrawable());
				}
				if (popupWindow.isShowing()){
					popupWindow.dismiss();
				}
				int[] location = new int[2];
				view.getLocationOnScreen(location);
				popupWindow.showAtLocation(view,Gravity.TOP, 0, location[1] - view.getHeight());
				return true;
			}
		});
		btn_addgoods.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}
	
	//  删除item
	private OnClickListener clickListener = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(!db.isOpen()) {
				db = oh.getWritableDatabase();
			}		
			cursor.moveToPosition(longClickPosition);
			int _id = cursor.getInt(cursor.getColumnIndex("_id"));
			int i = db.delete("goods","_id = ?",new String[]{_id + ""});
			setListView();
			ToastUtils.ShowShortToast(getActivity(), "删除成功!");
			popupWindow.dismiss();
		}
	};
	
	

	public void setListView() {
		if(!db.isOpen()) {
			db = oh.getWritableDatabase();
		}
		cursor = db.query("goods",null,null,null,null,null,null,null);
		db.close();
		goodsAdapter = new GoodsAdapter(getActivity(), cursor);
		lv.setAdapter(goodsAdapter);
		
	}
	
	@Override
	public void processClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_addgoods:
			InputDialog.showDialog(getActivity(), "", new OnInputDialogListener() {
				
				public void onConfirm(String text1, String text2, String text3) {
					// TODO Auto-generated method stub
					if(!TextUtils.isEmpty(text1) && !TextUtils.isEmpty(text2) && !TextUtils.isEmpty(text3)){
						if(!db.isOpen()) {
							db = oh.getWritableDatabase();
						}
						ContentValues values = new ContentValues();
						values.put("name",text1);
						values.put("type", text2);
						values.put("value",Integer.parseInt(text3));
						db.insert("goods", null, values);
						setListView();
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
