package com.gao.tree;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.gao.tree.ListTableView;

public class TreeView extends ListActivity {
    private  ArrayList<PDFOutlineElement> mPdfOutlinesCount = new ArrayList<PDFOutlineElement>();
	private  ArrayList<PDFOutlineElement> mPdfOutlines = new ArrayList<PDFOutlineElement>();
	private TreeViewAdapter treeViewAdapter = null;
	
	public  int iRowCount=0,iDepartCount=0;
	public  int iUserCount=0;
	public String strDepartment[];
	public String strUName[];
	public String strFirstVal[],strSecondVal[],strThirdVal[],strFouthVal[],strOperateUnit[];
	public String strCurrUser;

	private static final int ITEM = Menu.FIRST;
	private static final int ITEM1 = Menu.FIRST + 1;
	private static final int ITEM2 = Menu.FIRST + 2;
	private static final int ITEM3 = Menu.FIRST + 3;
	private static final int ITEM4 = Menu.FIRST + 4;
	private DBHelper dbHelper;	
	private Intent newintent;
	int iWidth,iHeight;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
         super.onCreate(savedInstanceState);  
         View bv = this.findViewById(android.R.id.title);
         ((TextView) bv).setTextColor(Color.BLACK);
         ((TextView) bv).setTextSize(15);
         ((View) bv.getParent()).setBackgroundResource(R.drawable.background_login);
         
         Intent intent=getIntent(); 		      
         iRowCount= intent.getIntExtra("RowCount", 0);	      
	     iDepartCount=intent.getIntExtra("DepartCount", 0);
		 iUserCount=intent.getIntExtra("UserCount", 0);
		 strCurrUser=intent.getStringExtra("CurrUser"); 
	     strDepartment=intent.getStringArrayExtra("Department");  
		 strFirstVal = intent.getStringArrayExtra("Firstarr");
		 strSecondVal= intent.getStringArrayExtra("Secondarr");
		 strThirdVal = intent.getStringArrayExtra("Thirdarr");
		 strFouthVal= intent.getStringArrayExtra("Foutharr");	 
		 strOperateUnit= intent.getStringArrayExtra("OperatorUnit");	
		 InitialData() ;
		 Display display = getWindowManager().getDefaultDisplay();
         iWidth=display.getWidth();
         iHeight=display.getHeight();      
    	 if(iWidth==1920 && iHeight ==1128)
    		treeViewAdapter = new TreeViewAdapter(this, R.layout.outlinebig,mPdfOutlinesCount);
		 else if(iWidth==1024 && iHeight ==720)
			treeViewAdapter = new TreeViewAdapter(this, R.layout.outlinemid,mPdfOutlinesCount);
		 else
			treeViewAdapter = new TreeViewAdapter(this, R.layout.outlinesmall,mPdfOutlinesCount); 
	    	
		 setListAdapter(treeViewAdapter);
		 registerForContextMenu(getListView());
		 dbHelper=new DBHelper(this);
		
    }   
    public boolean onCreateOptionsMenu(Menu menu) {
		// menu.add("一个参数的add方法的菜单");
		// menu.add("一个参数的add方法的菜单2");
		 //menu.add(R.string.titileRes);
    	if(LoginActivity.strCurType.equals("管理员"))
    	{
    		menu.add(0, ITEM, 0, "注册用户").setIcon(R.drawable.registeruser);
    		menu.add(0, ITEM4, 0, "管理用户").setIcon(R.drawable.user);
    	}
    	
		menu.add(0, ITEM1, 0, "修改密码").setIcon(R.drawable.changepwd);
		menu.add(0, ITEM2, 0, "启动飞鸽").setIcon(R.drawable.feige);
		menu.add(0, ITEM3, 0, "退出系统").setIcon(R.drawable.exitsys);

		//menu.add(0, ITEM, 0, "下载").setIcon(R.drawable.download);//设置图标
		//menu.add(0, ITME2, 0, "上传").setIcon(R.drawable.upload);

		return true;
	}

	// 通过点击了哪个菜单子项来改变Activity的标题
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case ITEM:
			Intent intent=new Intent(TreeView.this,RegisterActivity.class);
    		Bundle bundle=new Bundle();          	
    		bundle.putInt("DepartCount", iDepartCount);
            bundle.putInt("UserCount", iUserCount);
            bundle.putStringArray("Department", strDepartment);
            intent.putExtras(bundle);
			startActivity(intent);		
			break;
		case ITEM1:
			Intent intent1=new Intent(TreeView.this,ModifyActivity.class);
    		Bundle bundle1=new Bundle();          	
    		bundle1.putBoolean("AdminUser", true);
    		bundle1.putString("CurUser", strCurrUser);          
            intent1.putExtras(bundle1);
			startActivity(intent1);		
			break;
		case ITEM2:
			launchApp("com.netfeige");
		    break;
		case ITEM3:
			AlertDialog.Builder builder = new Builder(TreeView.this); 
      		 builder.setIcon(android.R.drawable.ic_dialog_info);
      	        builder.setMessage("确定要退出?"); 
      	        builder.setTitle("提示"); 
      	        builder.setPositiveButton("确认", 
      	                new android.content.DialogInterface.OnClickListener() { 
      	                    public void onClick(DialogInterface dialog, int which) { 
      	                        dialog.dismiss(); 
      	                      int currentVersion = android.os.Build.VERSION.SDK_INT;  
      	                    if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {  
      	                        Intent startMain = new Intent(Intent.ACTION_MAIN);  
      	                        startMain.addCategory(Intent.CATEGORY_HOME);  
      	                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
      	                        startActivity(startMain);  
      	                        System.exit(0);  
      	                    } else {// android2.1  
      	                        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);  
      	                        am.restartPackage(getPackageName());  
      	                    }   
      	                    } 
      	                }); 
      	        builder.setNegativeButton("取消", 
      	                new android.content.DialogInterface.OnClickListener() { 
      	                    public void onClick(DialogInterface dialog, int which) { 
      	                        dialog.dismiss(); 
      	                    } 
      	                }); 
      	        		builder.create().show(); 
			
			break;
		case ITEM4:
			Intent intent4=new Intent(TreeView.this,UserListView.class);    		
			startActivity(intent4);		
		    break;

		}
		return true;
	}
    @Override
    public void onConfigurationChanged(Configuration newConfig) {    
        super.onConfigurationChanged(newConfig);
        // 检测屏幕的方向：纵向或横向
        if (this.getResources().getConfiguration().orientation 
                == Configuration.ORIENTATION_LANDSCAPE) {
            //当前为横屏， 在此处添加额外的处理代码
        }
        else if (this.getResources().getConfiguration().orientation 
                == Configuration.ORIENTATION_PORTRAIT) {
            //当前为竖屏， 在此处添加额外的处理代码
        }
        //检测实体键盘的状态：推出或者合上    
        if (newConfig.hardKeyboardHidden 
                == Configuration.HARDKEYBOARDHIDDEN_NO){ 
            //实体键盘处于推出状态，在此处添加额外的处理代码
        } 
        else if (newConfig.hardKeyboardHidden
                == Configuration.HARDKEYBOARDHIDDEN_YES){ 
            //实体键盘处于合上状态，在此处添加额外的处理代码
        }
    }
  
	private void InitialData() 
    {
    	String strId = "01",strId1 = "101",strId2 = "1001",strId3 = "2001";		
		String strLstFirst="",strLstSecond="",strLstThrid="";
		PDFOutlineElement pdfOutlineElement[]=new PDFOutlineElement[100000]; 
    	int iCount=0;    	
    	//String strdat=String.format("%1$d,", iRowCount);
		
    	for(int i=0;i<iRowCount;i++)
    	{    		
	    	if(!strLstFirst.equals(strFirstVal[i]))
			{
				strLstFirst=strFirstVal[i];
				strLstSecond=strSecondVal[i];
				strLstThrid=strThirdVal[i];	
				strId=String.format("%1$d",i+1);
				if(strSecondVal[i]==null)
				{						
					pdfOutlineElement[iCount]=new PDFOutlineElement(strId, strFirstVal[i], false, false, "00", 0,false);
					mPdfOutlinesCount.add(pdfOutlineElement[iCount]);
					iCount++;					
				}
				else
				{	
					
					pdfOutlineElement[iCount]=new PDFOutlineElement(strId, strFirstVal[i], false, true, "00", 0,false);
					mPdfOutlinesCount.add(pdfOutlineElement[iCount]);
					iCount++;
					strId1=String.format("%1$d",i+1000);					
					if(strThirdVal[i]==null)
					{
						
						pdfOutlineElement[iCount]=new PDFOutlineElement(strId1, strSecondVal[i], false, false, strId, 1,false);
						iCount++;
					}
					else
					{	
										
						pdfOutlineElement[iCount]=new PDFOutlineElement(strId1, strSecondVal[i], false, true, strId, 1,false);
						iCount++;	
						strId2=String.format("%1$d",i+5000);
						strId3=String.format("%1$d",i+10000);
						if(strFouthVal[i]==null)
						{
							pdfOutlineElement[iCount]=new PDFOutlineElement(strId2, strThirdVal[i], false, false, strId1, 2,false);
							iCount++;
						}
						else
						{		
									
							pdfOutlineElement[iCount++]=new PDFOutlineElement(strId2, strThirdVal[i], false, true, strId1, 2,false);
							pdfOutlineElement[iCount++]=new PDFOutlineElement(strId3, strFouthVal[i], false, false, strId2, 3,false);
						}
						
					}
					
				}
			}
			else
			{
				if(!strLstSecond.equals(strSecondVal[i]))
				{
					strLstSecond=strSecondVal[i];
					strLstThrid=strThirdVal[i];
					strId1=String.format("%1$d",i+1000);	
					if(strThirdVal[i]==null)
					{
						pdfOutlineElement[iCount]=new PDFOutlineElement(strId1, strSecondVal[i], false, false, strId, 1,false);
						iCount++;
					}
					else
					{
							
						pdfOutlineElement[iCount]=new PDFOutlineElement(strId1, strSecondVal[i], false, true, strId, 1,false);
						iCount++;
						strId2=String.format("%1$d",i+5000);
						strId3=String.format("%1$d",i+10000);
						if(strFouthVal[i]==null)
						{
							pdfOutlineElement[iCount]=new PDFOutlineElement(strId2, strThirdVal[i], false, false, strId1, 2,false);
							iCount++;
						}
						else
						{
							pdfOutlineElement[iCount++]=new PDFOutlineElement(strId2, strThirdVal[i], false, true, strId1, 2,false);
							pdfOutlineElement[iCount++]=new PDFOutlineElement(strId3, strFouthVal[i], false, false, strId2, 3,false);
						}
						
					}
				}
				else
				{
					if(!strLstThrid.equals(strThirdVal[i]))
					{
						strLstThrid=strThirdVal[i];	
						strId2=String.format("%1$d",i+5000);
						strId3=String.format("%1$d",i+10000);
						if(strFouthVal[i]==null)
						{
							pdfOutlineElement[iCount]=new PDFOutlineElement(strId2, strThirdVal[i], false, false, strId1, 2,false);
							iCount++;
						}
						else
						{
							
							pdfOutlineElement[iCount++]=new PDFOutlineElement(strId2, strThirdVal[i], false, true, strId1, 2,false);
							pdfOutlineElement[iCount++]=new PDFOutlineElement(strId3, strFouthVal[i], false, false, strId2, 3,false);
						}
					
					}
					else
					{
						strId3=String.format("%1$d",i+10000);	
						if(strFouthVal[i]!=null)
							pdfOutlineElement[iCount++]=new PDFOutlineElement(strId3, strFouthVal[i], false, false, strId2, 3,false);
						
					}
				}			
			}		
		}
      
    	for(int j=0;j<iCount;j++)
		{    			
			mPdfOutlines.add(pdfOutlineElement[j]);	    			
		}				
	}

	private class TreeViewAdapter extends ArrayAdapter {

		public TreeViewAdapter(Context context, int textViewResourceId,
				List objects) {
			super(context, textViewResourceId, objects);
			mInflater = LayoutInflater.from(context);
			mfilelist = objects;
			mIconCollapse = BitmapFactory.decodeResource(
					context.getResources(), R.drawable.outline_list_collapse);
			mIconExpand = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.outline_list_expand);

		}

		private LayoutInflater mInflater;
		private List<PDFOutlineElement> mfilelist;
		private Bitmap mIconCollapse;
		private Bitmap mIconExpand;


		public int getCount() {
			return mfilelist.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			/*if (convertView == null) {*/
			 if(iWidth==1920 && iHeight ==1128)
				 convertView = mInflater.inflate(R.layout.outlinebig, null);
			 else if(iWidth==1024 && iHeight ==720)
				 convertView = mInflater.inflate(R.layout.outlinemid, null);
			 else
				 convertView = mInflater.inflate(R.layout.outlinesmall, null);
			
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
			/*} else {
				holder = (ViewHolder) convertView.getTag();
			}*/

			int level = mfilelist.get(position).getLevel();
 			holder.icon.setPadding(25 * (level + 1), holder.icon
					.getPaddingTop(), 0, holder.icon.getPaddingBottom());
			holder.text.setText(mfilelist.get(position).getOutlineTitle());
			if (mfilelist.get(position).isMhasChild()
					&& (mfilelist.get(position).isExpanded() == false)) {
				holder.icon.setImageBitmap(mIconCollapse);
			} else if (mfilelist.get(position).isMhasChild()
					&& (mfilelist.get(position).isExpanded() == true)) {
				holder.icon.setImageBitmap(mIconExpand);
			} else if (!mfilelist.get(position).isMhasChild()){
				holder.icon.setImageBitmap(mIconCollapse);
				holder.icon.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}

		class ViewHolder {
			TextView text;
			ImageView icon;

		}
	}

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo)
		{	
			AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterContextMenuInfo) menuInfo;
	        int iPos= (int) contextMenuInfo.position;// 这里的info.id对应的就是数据库中_id的值
			menu.setHeaderTitle("请选择需要进行的操作");
			menu.add(0, 0, 0, "获取数据");	     
			/*if(mPdfOutlinesCount.get(iPos).getLevel()==3&&LoginActivity.strCurType.equals("管理员"))				
			{
               menu.add(0, 1, 0, "删除");
               menu.add(0, 2, 0, "修改");	 
			}*/
		}
		public boolean onContextItemSelected(MenuItem item)
	    {
	        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	        int iPos= (int) info.position;// 这里的info.id对应的就是数据库中_id的值
	      
			String strTitle=mPdfOutlinesCount.get(iPos).getOutlineTitle();	
			 String strFirst="";
			 String strSecond="";
			 String strThird="";
			 String strFourth="";
			 String strOperator="";
			int iLevel=mPdfOutlinesCount.get(iPos).getLevel();
			if(iLevel==0)
				strFirst=strTitle;
			else if(iLevel==1)
			{
				int iID=0;
				for (int i = 0; i < strSecondVal.length; i++)
				{
					if (strTitle.equals(strSecondVal[i]))
					{
						//
						iID=i;
						break;
					}/**/				
				}	
				strFirst=strFirstVal[iID];
				strSecond=strTitle;
				
			}
			else if(iLevel==2)
			{
				int iID=0;
				for (int i = 0; i < strThirdVal.length; i++)
				{
					if (strTitle.equals(strThirdVal[i]))
					{
						//
						iID=i;
						break;
					}/**/				
				}		
				strFirst=strFirstVal[iID];
				strSecond=strSecondVal[iID];
				strThird=strThirdVal[iID];			
			}
			else if(iLevel==3)
			{
				int iID=0;
				for (int i = 0; i < strFouthVal.length; i++)
				{
					if (strTitle.equals(strFouthVal[i]))
					{
						//
						iID=i;
						break;
					}/**/				
				}		
				strFirst=strFirstVal[iID];
				strSecond=strSecondVal[iID];
				strThird=strThirdVal[iID];
				strFourth=strTitle;
				strOperator=strOperateUnit[iID];
				//Toast.makeText(getApplicationContext(),strFirst+strSecond+strThird+strFourth, Toast.LENGTH_SHORT).show();
			}
			
	        switch (item.getItemId()) {
	            case 0:
	            	
	            	int iLocalDataCount=GetLocalDataCount();
	            	LoadTreeData(iLocalDataCount);		
	            	Toast.makeText(getApplicationContext(),"正在获取字典数据,请稍候...", Toast.LENGTH_SHORT).show();
	    			      	
	                break;
	            case 1:             	
	            	AlarmDialog(strFirst, strSecond, strThird, strFourth, strOperator);
	            	//startDictActivity(strFirst, strSecond, strThird, strFourth, strOperator, "Delete");	         	
	                break;
	            case 2:               	
	            	startDictActivity(strFirst, strSecond, strThird, strFourth, strOperator, "Modify");		    					    	        		
	            	break; 				  	
	            default:            	
	            	break;
	        }
	        
	        return super.onContextItemSelected(item);
	    }
		public  int GetLocalDataCount()
    	{    	
    		//String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
    		//SQLiteDatabase sdb=SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
    		int LocalDataCount=0;
    		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
      		String sql="select  count(*) as count from DataDictionary";
      		Cursor cursor=sdb.rawQuery(sql,null);
      		while (cursor.moveToNext()) 
	        {
				LocalDataCount=Integer.parseInt(cursor.getString(cursor.getColumnIndex("count")));
				
			}
	        cursor.close();  
	        return LocalDataCount;
	        // Looper.prepare();
	        //String strdata=String.format("个数:%1$d", iRowCount);
			//Toast.makeText(getApplicationContext(), strdata, Toast.LENGTH_LONG).show();
			//Looper.loop();
    	} 
		public  int GetDataDictonaryCount()
    	{     		
    		try
    		{    			
    			Connection conn = DataBaseUtil.getSQLConnection();
    			if(conn==null)
    			{	/*Looper.prepare();
    				Toast.makeText(getApplicationContext(), "磊连接服务器数据库失败，请检查网络连接！将使用本地数据字典。" , Toast.LENGTH_LONG).show();
    				Looper.loop();*/
    			    
    				return -1;
    			}
    			String sql = "select  count(*) as count from DataDictionary";
    			Statement stmt = conn.createStatement();
    			ResultSet rs = stmt.executeQuery(sql);
    			int iCount=-1;
    			while(rs.next())
    			{		    					
    				iCount =Integer.parseInt(rs.getString("count"));  
    			}
    			rs.close();
    			stmt.close();
    			conn.close();
    			    			
    			return iCount;   			 
    		} 
    		catch (SQLException e)
    		{
    			e.printStackTrace();					
				Looper.prepare();
		    	Toast.makeText(getApplicationContext(), "获取数据字典更新状态时连接服务器数据库异常!" + e.getMessage(), Toast.LENGTH_LONG).show();
		    	Looper.loop();
		    	return -1;
    		}
    	} 
		public  int GetRemoteData()
    	{    		
    		String result = "字段1  -  字段2\n";
    		try
    		{
    			
    			if(!LoginActivity.TestConnect())
    				return -1;
    			Connection conn = DataBaseUtil.getSQLConnection();
    			if(conn==null)
    			{	/*Looper.prepare();
    				Toast.makeText(getApplicationContext(), "连接服务器数据库失败，请检查网络连接！将使用本地数据字典。" , Toast.LENGTH_LONG).show();
    				Looper.loop();*/
    				return -1;
    			}
    			String sql = "select  * from DataDictionary order by firstitem,seconditem,thirditem";
    			Statement stmt = conn.createStatement();
    			ResultSet rs = stmt.executeQuery(sql);	
    			dbHelper.ClearTable("datadictionary");
    			iRowCount=0;
    			while(rs.next())
    			{		    					
    				strFirstVal[iRowCount] = rs.getString("FirstItem");
					strSecondVal[iRowCount] = rs.getString("SecondItem");
					strThirdVal[iRowCount] = rs.getString("ThirdItem");
					strFouthVal[iRowCount] = rs.getString("FourthItem");
					strOperateUnit[iRowCount] = rs.getString("OperateUnit");					  			
					dbHelper.InsertDictionary(strFirstVal[iRowCount], strSecondVal[iRowCount], strThirdVal[iRowCount], strFouthVal[iRowCount], strOperateUnit[iRowCount],"TRUE");
					iRowCount++;  
    			}
    			rs.close();
    			stmt.close();
    			conn.close();
    			return 1;   			 
    		} 
    		catch (SQLException e)
    		{
    			e.printStackTrace();					
				Looper.prepare();
		    	Toast.makeText(getApplicationContext(), "连接服务器数据库异常!" + e.getMessage(), Toast.LENGTH_LONG).show();
		    	Looper.loop();
		    	return -1;
    		}
    	} 
		private void  LoadTreeData(final int LocalDataCount)
    	{
    		Runnable run = new Runnable()
    		{			
    			@Override
    			public void run()
    			{
    				int bReadRemoteData=0;
    				int bRet=GetDataDictonaryCount();
    				if(bRet!=LocalDataCount&&bRet>0)
    				{
    					bReadRemoteData=GetRemoteData();
    				}
    				Looper.prepare();
    				Toast.makeText(getApplicationContext(),"成功获取数据字典,重启本系统后生效.", Toast.LENGTH_SHORT).show();
	    			Looper.loop();
    		    	
    				
    				
    			}
    			
    		};
    		new Thread(run).start();
    		 
    	}
		private void startDictActivity(String strFirst,String strSecond,String strThird,String strFourth,String strOperator,String strType) {
			Intent intent = new Intent();  
        	intent.setClass(TreeView.this, DictionaryActivity.class); 	
			Bundle bundle=new Bundle(); 	             
			bundle.putString("TrainTrype", strFirst);
            bundle.putString("TrainPart", strSecond);
            bundle.putString("TrainLoc", strThird);
            bundle.putString("Content", strFourth);
            bundle.putString("Opertator", strOperator);	
            bundle.putString("CurUser", strCurrUser);
            bundle.putString("Type", strType);
            bundle.putStringArray("DepartmentArr", strDepartment);	               
            intent.putExtras(bundle);        	
			startActivity(intent);
			finish();
			
		}
		private void AlarmDialog(final String strFirst,final String strSecond,final String strThird,final String strFourth,final String strOperator)
		{
			AlertDialog.Builder builder = new Builder(TreeView.this); 
    		 builder.setIcon(android.R.drawable.ic_dialog_info);
    	        builder.setMessage("确定要删除吗?"); 
    	        builder.setTitle("提示"); 
    	        builder.setPositiveButton("确认", 
                new android.content.DialogInterface.OnClickListener() { 
                    public void onClick(DialogInterface dialog, int which) { 
                        dialog.dismiss(); 
                       startDictActivity(strFirst, strSecond, strThird, strFourth, strOperator, "Delete");	   
                    } 
                }); 
    	        builder.setNegativeButton("取消", 
                new android.content.DialogInterface.OnClickListener() { 
                    public void onClick(DialogInterface dialog, int which) { 
                        dialog.dismiss(); 
                    } 
                }); 
        		builder.create().show(); 
		}
		private void ExecSQL(final String strSql)	
		{		
			Runnable run = new Runnable()
			{			
				@Override
				public void run()
				{					
					DataBaseUtil.ExecSQL(strSql);					
				}
			};
			new Thread(run).start();
			 
		}
  /**/
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (mPdfOutlinesCount.get(position).getLevel()==3) {
			
			String strCurContent=mPdfOutlinesCount.get(position).getOutlineTitle();
			
			int iID=0;
			for (int i = 0; i < strFouthVal.length; i++)
			{
				if (strCurContent.equals(strFouthVal[i]))
				{
					//
					iID=i;
					break;
				}/**/				
			}		
			
			String strParent=mPdfOutlinesCount.get(position).getParent();
			Intent intent = new Intent();  
	        intent.setClass(TreeView.this, ListTableView.class); 	        
	        Bundle bundle=new Bundle(); 
	        bundle.putInt("DepartCount", iDepartCount); 
            bundle.putInt("UserCount", iUserCount); 
            bundle.putStringArray("Department", strDepartment);
	        bundle.putString("CurrUser", strCurrUser);
            bundle.putString("Receive", strOperateUnit[iID]);           
            bundle.putString("content",strCurContent);
            bundle.putString("TrainType",strFirstVal[iID]);
            bundle.putBoolean("Flag",false);
            intent.putExtras(bundle);
            startActivity(intent);
            /**/
            return;
		}
		if (mPdfOutlinesCount.get(position).isExpanded())
		{
			mPdfOutlinesCount.get(position).setExpanded(false);
			PDFOutlineElement pdfOutlineElement=mPdfOutlinesCount.get(position);
			ArrayList<PDFOutlineElement> temp=new ArrayList<PDFOutlineElement>();
			
			for (int i = position+1; i < mPdfOutlinesCount.size(); i++)
			{
				if (pdfOutlineElement.getLevel()>=mPdfOutlinesCount.get(i).getLevel()) 
				{
					break;
				}
				temp.add(mPdfOutlinesCount.get(i));
			}			
			mPdfOutlinesCount.removeAll(temp);			
			treeViewAdapter.notifyDataSetChanged();
			/*fileExploreAdapter = new TreeViewAdapter(this, R.layout.outline,
					mPdfOutlinesCount);*/
			//setListAdapter(fileExploreAdapter);
			
		}
		else
		{
			mPdfOutlinesCount.get(position).setExpanded(true);
			int level = mPdfOutlinesCount.get(position).getLevel();
			int nextLevel = level + 1;
			for (PDFOutlineElement pdfOutlineElement : mPdfOutlines) 
			{
				int j=1;
				if (pdfOutlineElement.getParent()==mPdfOutlinesCount.get(position).getId())
				{
					pdfOutlineElement.setLevel(nextLevel);
					pdfOutlineElement.setExpanded(false);
					mPdfOutlinesCount.add(position+j, pdfOutlineElement);
					j++;
				}			
			}
			treeViewAdapter.notifyDataSetChanged();
		}
	}
	public void launchApp(String pocketName) {
		PackageManager packageManager = this.getPackageManager();
		List<PackageInfo> packages = getAllApps();
		PackageInfo pa = null;
		for(int i=0;i<packages.size();i++){
			pa = packages.get(i);
			//获得应用名
			String appLabel = packageManager.getApplicationLabel(pa.applicationInfo).toString();
			//获得包名
			String appPackage = pa.packageName;
			Log.d(""+i, appLabel+"  "+appPackage);
		}
		newintent = packageManager.getLaunchIntentForPackage(pocketName);
		startActivity(newintent);
	}
	
	public List<PackageInfo> getAllApps() {  
	    List<PackageInfo> apps = new ArrayList<PackageInfo>();  
	    PackageManager pManager = this.getPackageManager();  
	    //获取手机内所有应用  
	    List<PackageInfo> paklist = pManager.getInstalledPackages(0);  
	    for (int i = 0; i < paklist.size(); i++) {  
	        PackageInfo pak = (PackageInfo) paklist.get(i);  
	        //判断是否为非系统预装的应用  (大于0为系统预装应用)
	        if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {  
	            apps.add(pak);  
	        }  
	    }  
	    return apps;  
	}  
}