package dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android_serialport_api.demo.R;

public class SettingDialog extends BaseDialog {
	//设置仓库的环境信息

	private String title;
	
	private int type;
	
	private TextView tv_settingdialog_title;
	private EditText et_settingdialog_message_max;
	private EditText et_settingdialog_message_min;
	private Button btn_settingdialog_confirm;
	private Button btn_settingdialog_cancel;
	private OnSettingDialogListener onSettingDialogListener;
	
	protected SettingDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	protected SettingDialog(Context context, String title, OnSettingDialogListener onSettingDialogListener) {
		super(context);
		this.type = type;
		this.title = title;
		this.onSettingDialogListener = onSettingDialogListener;
	}
	
	public static void showDialog(Context context, String title, OnSettingDialogListener onSettingDialogListener){
		SettingDialog dialog = new SettingDialog(context, title, onSettingDialogListener);
		
		dialog.setView(new EditText(context));
		dialog.show();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.dialog_setting);
		tv_settingdialog_title = (TextView) findViewById(R.id.tv_settingdialog_title);
		et_settingdialog_message_max = (EditText) findViewById(R.id.et_settingdialog_message_max);
		et_settingdialog_message_min = (EditText) findViewById(R.id.et_settingdialog_message_min);
		btn_settingdialog_cancel = (Button) findViewById(R.id.btn_settingdialog_cancel);
		btn_settingdialog_confirm = (Button) findViewById(R.id.btn_settingdialog_confirm);
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		btn_settingdialog_cancel.setOnClickListener(this);
		btn_settingdialog_confirm.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		tv_settingdialog_title.setText(title);
	}

	@Override
	public void processClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_settingdialog_cancel:
			if(onSettingDialogListener != null){
				onSettingDialogListener.onCancel();
			}
			break;
		case R.id.btn_settingdialog_confirm:
			if(onSettingDialogListener != null){
				onSettingDialogListener.onConfirm(et_settingdialog_message_max.getText().toString(),et_settingdialog_message_min.getText().toString());
			}
			break;
		}
		dismiss();
	}
	
	public interface OnSettingDialogListener {  
		void onCancel();
		void onConfirm(String max, String min);    
	} 

}
