package adapter;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android_serialport_api.demo.R;
import bean.Good;

public class GoodsAdapter extends CursorAdapter{

	public GoodsAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		return View.inflate(context, R.layout.item_goods_list, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		ViewHolder holder = getHolder(view);
		Good good = Good.createFromCursor(cursor);
		holder.tv_name.setText(good.getName());
		holder.tv_type.setText(good.getType());
		holder.tv_value.setText(good.getValue() + "");
		holder.tv_date.setText(good.getDate());
	}
	
	private ViewHolder getHolder(View view){
		ViewHolder holder = (ViewHolder) view.getTag();
		if(holder == null){
			holder = new ViewHolder(view);
			view.setTag(holder);
		}
		return holder;
	}
	
	class ViewHolder{
		private TextView tv_name;
		private TextView tv_type;
		private TextView tv_value;
		private TextView tv_date;

		public ViewHolder(View view) {
			tv_name = (TextView) view.findViewById(R.id.tv_good_name);
			tv_type = (TextView) view.findViewById(R.id.tv_good_type);
			tv_value = (TextView) view.findViewById(R.id.tv_good_value);
			tv_date = (TextView) view.findViewById(R.id.tv_good_date);
		}
	}

}
