package com.zephyricstudios.catchme;

import java.util.ArrayList;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;

public class SeekerAdapter extends ArrayAdapter<Seeker> {
	private final Activity context;
	private final ArrayList<Seeker> values;
	private final int layoutID;
	private final OnClickListener listener;
	private final Typeface light;
	
	public SeekerAdapter(Activity context, int layoutID, ArrayList<Seeker> values,
			OnClickListener listener, Typeface light) {
		super(context, layoutID, values);
		this.context = context;
		this.values = values;
		this.layoutID = layoutID;
		this.listener = listener;
		this.light = light;
	}
	
	static class ViewHolder {
		TextView textName;
		ImageView buttonDelete;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater mInflater = context.getLayoutInflater();
			convertView = mInflater.inflate(layoutID, null);
			
			holder = new ViewHolder();
			holder.textName = (TextView) convertView.findViewById(R.id.item_text);
			holder.textName.setTypeface(light);
			
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.textName.setText(values.get(position).getName());
		holder.textName.setTypeface(light);
		
		return convertView;
		
	}
}