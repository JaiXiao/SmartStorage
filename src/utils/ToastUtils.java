package utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

	public static void ShowShortToast(Context context, String msg){
		Toast.makeText(context, msg, 0).show();
	}
	
	public static void ShowLongToast(Context context, String msg){
		Toast.makeText(context, msg, 1).show();
	}
}