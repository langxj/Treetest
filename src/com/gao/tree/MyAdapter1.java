package com.gao.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MyAdapter1 extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private ArrayList<ArrayList<String>> lists;
	private int selectedPosition = -1;// 选中的位置 
	private int iWidth=1920,iHeight=1128;
	
	public MyAdapter1(Context context, ArrayList<ArrayList<String>> lists,int iVal,int iVal1) {
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
		if(view == null)
		{
			
			if(iWidth==1920 && iHeight ==1128)
			   view = inflater.inflate(R.layout.list_itembig1, null);
			else if(iWidth==1024 && iHeight ==720)
			   view = inflater.inflate(R.layout.list_itemmid1, null);
			else
			   view = inflater.inflate(R.layout.list_itemsmall1, null);
		}
		view.setBackgroundColor(Color.rgb( 176 ,  224 ,  230 )); 
		TextView textView1 = (TextView) view.findViewById(R.id.text1);
		TextView textView2 = (TextView) view.findViewById(R.id.text2);
		TextView textView3 = (TextView) view.findViewById(R.id.text3);
		TextView textView4 = (TextView) view.findViewById(R.id.text4);
		TextView textView5 = (TextView) view.findViewById(R.id.text5);
		TextView textView6 = (TextView) view.findViewById(R.id.text6);
		TextView textView7 = (TextView) view.findViewById(R.id.text7);
		
		
		textView1.setText(list.get(0));
		textView2.setText(list.get(1));
		textView3.setText(list.get(2));
		textView4.setText(list.get(3));
		textView4.setVisibility(View.GONE);
		textView5.setText(list.get(4));
		textView6.setText(list.get(5));
		textView7.setText(list.get(6));
		 if (selectedPosition == index&& index != 0) 
		 {  		
			view.setBackgroundColor(Color.BLUE);  
         } 
		 else 		
		 {	
			 String strRow=list.get(7);
		
			 
		    if(strRow.equals("0"))		 
		    	view.setBackgroundColor(Color.RED);	
		    else if (strRow.equals("1"))			
		    	view.setBackgroundColor(Color.YELLOW);
		    else if (strRow.equals("2"))	
		    	view.setBackgroundColor(Color.GREEN);
		    else if(strRow.equals("3"))	
				 view.setBackgroundColor(Color.rgb( 167, 87, 168));	
		    else if(strRow.equals("5"))	
				 view.setBackgroundColor(Color.BLUE);	
		    if(strRow.equals("6"))		 
		    	view.setBackgroundColor(Color.TRANSPARENT); 		    	
		 }
		 if(index <= 0) 	
			 view.setBackgroundColor(Color.TRANSPARENT); 
		
		
		
		
		return view;
	}

	public boolean UpdateData(int iPos,String strTrainNum, String strRecv, String strContent,String strState,String strDate) {
		ArrayList<String> list= lists.get(iPos);
		list.set(1, strTrainNum);
		list.set(4, strRecv);
		list.set(6, strContent);		
		list.set(7, strState);		
		list.set(12, strDate);
		lists.set(iPos, list);
		return false;
	}
	public boolean UpdateSubmitState(int iPos,String strState) {
		ArrayList<String> list= lists.get(iPos);			
		list.set(7, strState);					
		lists.set(iPos, list);
		return false;
	}
	public void UpdateSubmitAllState(String strUser) {
		for(int i = 0;i < lists.size(); i++)
		{
			ArrayList<String> list= lists.get(i);
			if(list.get(7).equals("6")&&list.get(3).equals(strUser))
			{
				list.set(7, "0");
			}			
		}		
	}
	public void UpdateSubmitAllState() {
		for(int i = 0;i < lists.size(); i++)
		{
			ArrayList<String> list= lists.get(i);
			if(list.get(7).equals("6"))
			{
				list.set(7, "0");
			}			
		}		
	}

	public boolean UpdateFailState(int iPos,String strState,String strCheckFailTime,String strFailReason,String strCurQualUser) {
		ArrayList<String> list= lists.get(iPos);	
	
		list.set(7, strState);		
		list.set(13, strCheckFailTime);
		list.set(17, strFailReason);
		list.set(18, strCurQualUser);
		lists.set(iPos, list);
		return false;
	}

	public boolean UpdateState(int iPos,String strState,String strLabier,String strRecvTime,String strEndTime,String strCancelTime,String strCancelReason) {
		ArrayList<String> list= lists.get(iPos);	
		list.set(5, strLabier);
		list.set(7, strState);
		list.set(8, strRecvTime);
		list.set(9, strEndTime);
		list.set(11, strCancelTime);
		list.set(16, strCancelReason);
		lists.set(iPos, list);
		return false;
	}

	public void clearAll() {
		lists.clear();			
	}


	public void UpdateAllSubmitState(String strCurUpdateDate) {
		// TODO Auto-generated method stub
		
	}

	

}
