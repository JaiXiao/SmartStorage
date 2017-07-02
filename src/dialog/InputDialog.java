package dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android_serialport_api.demo.R;

public class InputDialog extends BaseDialog {
	//输入数据界面
	
	private String title;
	private TextView tv_inputdialog_title;
	private EditText et_inputdialog_message_name;
	private EditText et_inputdialog_message_type;
	private EditText et_inputdialog_message_value;
	private Button bt_inputdialog_confirm;
	private Button bt_inputdialog_cancel;
	private OnInputDialogListener onInputDialogListener;
	
	protected InputDialog(Context context, String title, OnInputDialogListener onInputDialogListener) {
		super(context);
		this.title = title;
		this.onInputDialogListener = onInputDialogListener;
	}
	
	public static void showDialog(Context context, String title, OnInputDialogListener onInputDialogListener2){
		InputDialog dialog = new InputDialog(context, title, onInputDialogListener2);
		
		dialog.setView(new EditText(context));
		dialog.show();
	}

	@Override
	public void initView() {
		setContentView(R.layout.dialog_input);
		tv_inputdialog_title = (TextView) findViewById(R.id.tv_inputdialog_title);
		et_inputdialog_message_name = (EditText) findViewById(R.id.et_inputdialog_message_name);
		et_inputdialog_message_type = (EditText) findViewById(R.id.et_inputdialog_message_type);
		et_inputdialog_message_value = (EditText) findViewById(R.id.et_inputdialog_message_value);
		bt_inputdialog_cancel = (Button) findViewById(R.id.bt_inputdialog_cancel);
		bt_inputdialog_confirm = (Button) findViewById(R.id.bt_inputdialog_confirm);
	}

	@Override
	public void initListener() {
		bt_inputdialog_cancel.setOnClickListener(this);
		bt_inputdialog_confirm.setOnClickListener(this);

	}

	@Override
	public void initData() {
		tv_inputdialog_title.setText(title);

	}

	@Override
	public void processClick(View v) {
		switch (v.getId()) {
		case R.id.bt_inputdialog_cancel:
			if(onInputDialogListener != null){
				onInputDialogListener.onCancel();
			}
			break;
		case R.id.bt_inputdialog_confirm:
			if(onInputDialogListener != null){
				onInputDialogListener.onConfirm(et_inputdialog_message_name.getText().toString(),et_inputdialog_message_type.getText().toString(),et_inputdialog_message_value.getText().toString());
			}
			break;
		}
		dismiss();
	}
	
	public interface OnInputDialogListener{
		void onCancel();
		void onConfirm(String text1, String text2, String text3);
		
	}

}
