package dialog;


import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android_serialport_api.demo.R;

public class DeleteDialog extends BaseDialog{
	private IDialogOnclickInterface dialogOnclickInterface; 
	private Button btn_itemdelete;
	
	protected DeleteDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.dialog_delete);
		btn_itemdelete = (Button) findViewById(R.id.btn_itemdelete);
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		btn_itemdelete.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {  
        case R.id.btn_itemdelete:  
                dialogOnclickInterface.onDelete();  
                break;    
        default:  
                break;  
        }  
	}

	public interface IDialogOnclickInterface {  
        void onDelete();   
	} 
	
}
