package com.gao.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class UserAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private ArrayList<ArrayList<String>> lists;
	private int selectedPosition = -1;// 选中的位置 
	private int iWidth,iHeight;
	public UserAdapter(Context context, ArrayList<ArrayList<String>> lists,int iVal,int iVal1) {
		super();
		this.context = context;
		this.lists = lists;
		this.iWidth=iVal;
		this.iHeight=iVal1;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		ArrayList<String> list = lists.get(position);
		return list;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	public void remove(int iPos) {
		lists.remove(iPos);
		
	}
	 public void setSelectedPosition(int position) {  
         selectedPosition = position;  
     }  
	@Override
	public View getView(int index, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ArrayList<String> list = lists.get(index);
		if(view == null){
			if(iWidth==1920 && iHeight ==1128)
				view = inflater.inflate(R.layout.userlistbig, null);
			else if(iWidth==1024 && iHeight ==720)
				view = inflater.inflate(R.layout.userlistmid, null);
			else
				view = inflater.inflate(R.layout.userlistsmall, null);			
		}
		view.setBackgroundColor(Color.rgb( 176 ,  224 ,  230 )); 
		TextView textView1 = (TextView) view.findViewById(R.id.text1);
		TextView textView2 = (TextView) view.findViewById(R.id.text2);
		TextView textView3 = (TextView) view.findViewById(R.id.text3);
		TextView textView4 = (TextView) view.findViewById(R.id.text4);
		
		
		
		textView1.setText(list.get(0));
		textView2.setText(list.get(1));
		textView3.setText(list.get(2));
		textView4.setText(list.get(3));
	
		 if (selectedPosition == index&& index != 0) 
		 {  		
			view.setBackgroundColor(Color.BLUE);  
         } 
		 else 		
		 {	
			view.setBackgroundColor(Color.TRANSPARENT); 
		 }		
		return view;
	}

	public boolean UpdateData(int iPos, String strDepart, String strType) {
		ArrayList<String> list= lists.get(iPos);
		list.set(2, strDepart);
		list.set(3, strType);	
		lists.set(iPos, list);
		return false;
	}
	public void clearAll() {
		lists.clear();			
	}
}
