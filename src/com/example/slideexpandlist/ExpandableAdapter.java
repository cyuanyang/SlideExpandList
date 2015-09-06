package com.example.slideexpandlist;

import java.util.List;
import java.util.Map;

import slide.CoustomerItemView;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author chenyuanyang
 *
 */
public class ExpandableAdapter extends BaseExpandableListAdapter{

	private  Context context;
	List<String> groups;
	Map<String , List<String>> children;
	
	public ExpandableAdapter(Context context , List<String> groups , Map<String , List<String>> children){
		this.groups = groups;
		this.children =children;
		this.context = context;
	}
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		String group = groups.get(groupPosition);
		return children.get(group);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition,final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			convertView =new CoustomerItemView(context , R.layout.item);
		}
		TextView nameView = (TextView) convertView.findViewById(R.id.name);
		ImageButton btn = (ImageButton) convertView.findViewById(R.id.side_btn);
		final String group = groups.get(groupPosition);
		String name = children.get(group).get(childPosition);
		nameView.setText(name);
		
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "group="+groupPosition+"child="+childPosition, 0).show();
			}
		});
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		String group = groups.get(groupPosition);
		return children.get(group).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		TextView textView = null;
		if (convertView == null) {
			textView = new TextView(context);
			textView.setPadding(50, 10, 0, 10);
			convertView = textView;
		}else{
			textView = (TextView) convertView;
		}
		
		textView.setText(groups.get(groupPosition));
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
