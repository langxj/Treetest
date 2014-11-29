package com.gao.tree;   
  

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;  
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;   
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;   
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;   
import android.view.ViewGroup;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;   
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;   
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;   
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;   
import android.widget.TextView;
import android.widget.Toast;
 
public class ListTableView1 extends Activity {   
   
    private ListView listView;
    private HorizontalScrollView layout;
    private ArrayList<String> list1= new ArrayList<String>();
    private ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
    private String strCurrUser,strCurRecvUnit,strCurSubmitDate;  
    public  int iUpdateRowNum=0;
    public  String strUpdateState;
    private Timer mTimer;
    private String  strCurSysID,strCurTrainNum,strCurDate, strCurRecv, strCurContent,strCurLabier,strCurState,strCurImagePath,strCurCancelReason,strCurFailReason,strCurQualUser="";
    private String strCurModifyType,strCurTrainType,strCurAttach,strCurSubmiter,strCurRecvTime,strCurEndTime,strCurCheckTime,strCurCheckFailTime,strCurRefreshTime,strCurCancelTime;
    private DBHelper dbHelper;    
    private static int   UpdateFlag=0;
	private static int UpdatePhotoFlag=0;
	private int iCheckFailCount=0;
    MyAdapter1 adapter;
    String strLoadsql ;
    private static String photoPath = "/sdcard/treetest/";
	private static String photoName =  "laolisb.jpg";
	private static String imagePath = photoPath + photoName;
    private Intent newintent;
	private static final int ITEM = Menu.FIRST;
	private static final int ITME2 = Menu.FIRST + 1;
	private static final int ITME3 = Menu.FIRST + 2;
	private static final int ITME4 = Menu.FIRST + 3;
	private int iWidth,iHeight;
	private boolean bUpdateFlag=false;
	
    public void onCreate(Bundle savedInstanceState) {  
    	//requestWindowFeature( Window.FEATURE_NO_TITLE ); //无标题
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        super.onCreate(savedInstanceState); 
        View bv = this.findViewById(android.R.id.title);
        ((TextView) bv).setTextColor(Color.BLACK);
        ((TextView) bv).setTextSize(15);
        ((View) bv.getParent()).setBackgroundResource(R.drawable.background_login);
       setContentView(R.layout.listtable1);    
       listView = (ListView) findViewById(R.id.listview);
       layout = (HorizontalScrollView) findViewById(R.id.layout);
       dbHelper=new DBHelper(this);	
       Intent intent=getIntent(); 
       strCurLabier=intent.getStringExtra("CurrUser");
       strCurRecvUnit=intent.getStringExtra("Receive"); 
       if(!LoginActivity.strCurType.equals("生产员"))
    	   strLoadsql = String.format("select * from  ProblemData  where Receiver='%s' and State<>4 and state<>6 order by SubmitDate",strCurRecvUnit);
       else
    	   strLoadsql = String.format("select * from  ProblemData where State<>4 order by SubmitDate");
       Display display = getWindowManager().getDefaultDisplay();
       iWidth=display.getWidth();
       iHeight=display.getHeight();
      
       listView.setOnItemClickListener(new ListView.OnItemClickListener() {  
        	  
            @Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                    long arg3) {  
            	ArrayList<String> list =  (ArrayList<String>) adapter.getItem(arg2);				
			    if(strCurRecvUnit.equals(list.get(4))) 
            	{
			    	adapter.setSelectedPosition(arg2); 
			    	adapter.notifyDataSetInvalidated();
            	}    
            }  
        });  
      //设置长按菜单项
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener()
        {       
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {	
				AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterContextMenuInfo) menuInfo;
				adapter  = new MyAdapter1(ListTableView1.this, lists,iWidth,iHeight);
				ArrayList<String> list =  (ArrayList<String>) adapter.getItem(contextMenuInfo.position);
				int iCount=GetLocalDataCount();
			    if(strCurRecvUnit.equals(list.get(4))||LoginActivity.strCurType.equals("生产员"))
			    {			  
				    menu.setHeaderTitle("请选择需要进行的操作");
				    menu.add(0, 5, 0, "详细信息");
				    menu.add(0, 3, 0, "获取数据"); 
	                menu.add(0, 4, 0, "故障图片");
	                if(iCount>0)
		                menu.add(0, 6, 0, "提交所有");
	                if(!LoginActivity.strCurType.equals("生产员"))
				    {					   
		                menu.add(0, 0, 0, "接收任务");
		                menu.add(0, 1, 0, "完成任务");
		                //menu.add(0, 2, 0, "撤销任务");  
				    }
			    }
			    else
			    {
			    	 menu.setHeaderTitle("请选择需要进行的操作");
			    	 menu.add(0, 3, 0, "获取数据"); 
			    }
			}
        });  
    	
		 
       InitHeaderData(true);
      // mTimer = new Timer();
      int iCount=GetLocalDataCount();
	  if(iCount>0)
   		   LoadLocalData();  
	  
	   
    } 
    public  int  GetLocalDataCount()
   	{
       	SQLiteDatabase sdb=dbHelper.getReadableDatabase();  		
     		Cursor cursor=sdb.rawQuery("select count(*) as cnt from problemdata" ,null);  	
     		String strCount="0";
   		while (cursor.moveToNext()) 
           {			
   			strCount= cursor.getString(cursor.getColumnIndex("cnt"));
           }
           cursor.close();  
           int iCount=Integer.parseInt(strCount);
           return iCount;
           
   	}
    private void LoadData() {
    	 
    	Runnable run = new Runnable()
		{			
			@Override
			public void run()    			
			{				
				boolean bRet=LoadRemoterData();
	            Message message = new Message();
	            message.what = 1;
	            Bundle data = new Bundle();						
				data.putBoolean("result", bRet);				
				message.setData(data);
	            doActionHandler.sendMessage(message);				
			}
			
		};
		new Thread(run).start();
		
	}
	
	public  void   InitHeaderData(boolean bView)
    {
    	 ArrayList<String> list = new ArrayList<String>();
         list.add("票号");
         list.add("车号");
         list.add("提交时间");
         list.add("提交人");
         list.add("接收单位");
         list.add("负责人");
         list.add("故障内容");
         list.add("状态");
         list.add("接收时间");
         list.add("完成时间");
         list.add("合格时间");
         list.add("撤销时间");
         list.add("修改时间");
         list.add("不合格时间");
         list.add("配属");
         list.add("车型");
         list.add("失败原因");
         list.add("撤销原因");
         list.add("确认人");
         list.add("回修票类型");
         lists.add(list);
         if(bView)
         {        		
	         adapter  = new MyAdapter1(ListTableView1.this, lists,iWidth,iHeight); 	         
	         listView.setAdapter(adapter);
	         layout.setVisibility(View.VISIBLE);
	         
         }
    }   
	private void LoadLocalData() 
	{		
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();  		
  		Cursor cursor=sdb.rawQuery(strLoadsql,null);	 		
  		String strSysID,strTrainNum,strDate,strUser, strRecv, strLabier,strContent,strState,strAttch,strTrainType,strRecvTime,strEndTime,strCheckTime,strCheckFailTime,strRefreshTime,strCancelTime,strCancelReason,strFailReason,strQualUser,strModifyType;		  		
		int iCount=1;
  		while (cursor.moveToNext()) 
        {	
			strSysID=String.format("%d", iCount++);
			strTrainNum= cursor.getString(cursor.getColumnIndex("TrainNum"));
			strDate =cursor.getString(cursor.getColumnIndex("SubmitDate"));
			strUser=cursor.getString(cursor.getColumnIndex("Submitter"));
			strRecv= cursor.getString(cursor.getColumnIndex("Receiver"));
			strLabier= cursor.getString(cursor.getColumnIndex("Liabler"));
			strContent= cursor.getString(cursor.getColumnIndex("Content"));
			strState= cursor.getString(cursor.getColumnIndex("State"));
			strRecvTime= cursor.getString(cursor.getColumnIndex("ReceDate"));
			strEndTime= cursor.getString(cursor.getColumnIndex("EndDate"));
			strCheckTime= cursor.getString(cursor.getColumnIndex("QualDate"));
			strCancelTime= cursor.getString(cursor.getColumnIndex("CancelDate"));
			strRefreshTime= cursor.getString(cursor.getColumnIndex("RefreshDate"));
			strCheckFailTime= cursor.getString(cursor.getColumnIndex("FailDate"));			
			strAttch= cursor.getString(cursor.getColumnIndex("Attach"));
			strTrainType= cursor.getString(cursor.getColumnIndex("TrainType"));
			strCancelReason= cursor.getString(cursor.getColumnIndex("CancelReason"));
			strFailReason= cursor.getString(cursor.getColumnIndex("FailReason"));
			strQualUser= cursor.getString(cursor.getColumnIndex("QualUser"));
			strModifyType= cursor.getString(cursor.getColumnIndex("ModifyType"));
  			addLoadData(strSysID,strTrainNum,strDate,strUser, strRecv, strLabier,strContent,strState,strRecvTime,strEndTime,strCheckTime,strCancelTime,strRefreshTime,strCheckFailTime,strAttch,strTrainType,strCancelReason,strFailReason,strQualUser,strModifyType);     			
		}
  		Toast.makeText(getApplicationContext(), "为确保数据安全，请在网通时及时提交本地数据！" , Toast.LENGTH_LONG).show();
        cursor.close();   		
	}
	public  boolean LoadRemoterData()
	{ 		
		try
		{				
			Connection conn = DataBaseUtil.getSQLConnection();;
			if(conn==null)
			{
				//Looper.prepare();
				//Toast.makeText(getApplicationContext(), "更新数据时连接服务器数据库失败，请检查网络连接！" , Toast.LENGTH_LONG).show();
				//Looper.loop();
				return false;
				
			}
			adapter.clearAll();			
			InitHeaderData(false);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(strLoadsql);	
			String strSysID,strTrainNum,strDate,strUser, strRecv, strLabier,strContent,strState,strAttch,strTrainType,strRecvTime,strEndTime,strCheckTime,strCheckFailTime,strRefreshTime,strCancelTime,strCancelReason,strFailReason,strQualUser,strModifyType;		  		
			while(rs.next())
			{		    	
				strSysID = rs.getString("SysID");
				strTrainNum = rs.getString("TrainNum");
				strDate= rs.getString("SubmitDate");
				strUser = rs.getString("Submitter");
				strRecv = rs.getString("Receiver");
				strLabier= rs.getString("Liabler");	
				strContent= rs.getString("Content");
				strState = rs.getString("State");
				strRecvTime= rs.getString("ReceDate");
				strEndTime= rs.getString("EndDate"); 
				strCheckTime= rs.getString("QualDate"); 
				strCancelTime= rs.getString("CancelDate"); 
				strRefreshTime= rs.getString("RefreshDate"); 
				strCheckFailTime= rs.getString("FailDate");			
				strAttch= rs.getString("Attach"); 
				strTrainType= rs.getString("TrainType"); 				
				strCancelReason= rs.getString("CancelReason");
				strFailReason=  rs.getString("FailReason");
				strQualUser=  rs.getString("QualUser");
				strModifyType=  rs.getString("ModifyType");
				strDate=strDate.substring(0, strDate.length()-2);
	  			addLoadData(strSysID,strTrainNum,strDate,strUser, strRecv, strLabier,strContent,strState,strRecvTime,strEndTime,strCheckTime,strCancelTime,strRefreshTime,strCheckFailTime,strAttch,strTrainType,strCancelReason,strFailReason,strQualUser,strModifyType);     			
			}
			rs.close();
			stmt.close();
			conn.close();
			//listView.setSelection(adapter.getCount());
			return true;			 
		} 
		catch (SQLException e)
		{
			e.printStackTrace();					
			Looper.prepare();
	    	Toast.makeText(getApplicationContext(), "连接数据库异常!" + e.getMessage(), Toast.LENGTH_LONG).show();
	    	Looper.loop();
	    	return false;
		}
		
	} 
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    // cancel timer
	    //mTimer.cancel();
	}
	private void setTimerTask() {
	    mTimer.schedule(new TimerTask() {
	        @Override
	        public void run() {	
	        	boolean bRet=false;
	        	if(LoginActivity.TestConnect())
	        	{
	        	   bRet=LoadRemoterData();
	        	}
	            Message message = new Message();
	            message.what = 1;
	            Bundle data = new Bundle();						
				data.putBoolean("result", bRet);				
				message.setData(data);
	            doActionHandler.sendMessage(message);
	        }
	    }, 1000, LoginActivity.iUpdateTime*60*1000/* 表示5秒之後，每隔5秒執行一次 */);
	}
	
	/**
	 * do some action
	 */
	private Handler doActionHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        int msgId = msg.what;
	        switch (msgId) {
	            case 1:	 	 
	            	boolean ret = msg.getData().getBoolean("result");
	            	if(!ret)
	            	{	            		
	            		adapter.clearAll();			
	        			InitHeaderData(true);
	        			int iCount=GetLocalDataCount();
	        			if(iCount>0)
		            		   LoadLocalData();  
            			adapter  = new MyAdapter1(ListTableView1.this, lists,iWidth,iHeight);  
            			listView.setAdapter(adapter);
		        		layout.setVisibility(View.VISIBLE);  
		        		listView.setSelection(adapter.getCount());	             		
	            		
	            	}
	            	else
	            	{	
	            		
	            		int iCount=GetLocalDataCount();
	            		if(iCount>0)
	            		   LoadLocalData();  	 
		            	adapter  = new MyAdapter1(ListTableView1.this, lists,iWidth,iHeight);  	            	
		            	listView.setAdapter(adapter);
		        		layout.setVisibility(View.VISIBLE);  
		        		listView.setSelection(adapter.getCount());	
	            	}
	                break;
	            default:
	                break;
	        }
	    }

		
	};
	Handler mExecHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				case 1001:
					String  ExecType = msg.getData().getString("type");	
					boolean ret = msg.getData().getBoolean("result");					
					if(ret)
					{
						adapter  = new MyAdapter1(ListTableView1.this, lists,iWidth,iHeight);  	            	
		            	listView.setAdapter(adapter);
		        		layout.setVisibility(View.VISIBLE); 
						/*dbHelper.DeleteProblem(strCurDate);								
						dbHelper.InsertProblem(strCurTrainNum, strCurDate, strCurrUser, strCurRecv, strCurLabier, strCurContent,strUpdateState,strCurImagePath,strCurRecvTime,strCurEndTime,strCurCheckTime,strCurCancelTime,strCurRefreshTime,strCurCheckFailTime,strCurAttach,strCurTrainType,strCurCancelReason,strCurFailReason,"");
					    */
					}
					
	        			
					break;

				default:
					break;
			}
		};
	};
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
 
    public boolean onContextItemSelected(MenuItem item)
    {
    	
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int iPos= (int) info.position;// 这里的info.id对应的就是数据库中_id的值
        adapter  = new MyAdapter1(ListTableView1.this, lists,iWidth,iHeight);
		ArrayList<String> list =  (ArrayList<String>) adapter.getItem(iPos);
		strCurSysID=list.get(0);	
		strCurTrainNum=list.get(1);	
    	strCurDate=list.get(2);    
    	strCurrUser=list.get(3);  
    	strCurRecv=list.get(4);	
    	strCurLabier=list.get(5);	
		strCurContent=list.get(6);  
		strCurState=list.get(7);  		
		strCurRecvTime=list.get(8); 
		strCurEndTime=list.get(9); 
		strCurCheckTime=list.get(10); 
		strCurCancelTime=list.get(11); 
		strCurRefreshTime=list.get(12); 
		strCurCheckFailTime=list.get(13); 
		strCurAttach=list.get(14); 
		strCurTrainType=list.get(15);  
		strCurCancelReason=list.get(16); 
		strCurFailReason=list.get(17); 
		strCurQualUser=list.get(18); 
		strCurModifyType=list.get(19); 
		photoName=strCurDate;
     	photoName=photoName.replaceAll("-", "");
     	photoName=photoName.replaceAll(":", "");
     	photoName=photoName.replaceAll(" ", "");            
     	photoName=photoName+".jpg";
     	imagePath = photoPath + photoName;
		strCurImagePath=imagePath;
		
        switch (item.getItemId()) {
            case 0:
            	strUpdateState="1";     		
            	UpdateRemoteState(iPos); 
            	
                break;
            case 1:               		
            	strUpdateState="2";
            	inputTitleDialog(iPos);
            	//if(!strLabier.equals(""))
            	  //  UpdateRemoteState(iPos); 
                break;
            case 2:               	
        		strUpdateState="3"; 
        		CancelDialog(iPos);      		
        		
            	/*if(!strLabier.equals(""))
            	    UpdateRemoteState(iPos);*/ 
                break;
             case 3:              	
            	//mTimer.cancel();
            	//mTimer = new Timer();
            	 
            	 int iCount=GetLocalDataCount();
             	if(iCount>0)
             	{
             		SubmitAllDate(false);  
             	}
             	if(LoginActivity.TestConnect())
     	    	{
             		LoadData(); 
     	    	} 
				break;
             case 4:             	
             	File file=new File(imagePath);
             	if(!file.exists())
             	{
             		Toast.makeText(ListTableView1.this,"故障提交人未拍摄故障图片！",Toast.LENGTH_SHORT).show();
             		break;
             	}
             	Intent intent = new Intent(Intent.ACTION_VIEW);
             	//Uri mUri = Uri.parse("file://" + picFile.getPath());Android3.0以后最好不要通过该方法，存在一些小Bug
             	intent.setDataAndType(Uri.fromFile(file), "image/*");
             	startActivity(intent);
             	break;
             case 5:
            	//String strSql=String.format("select * from ProblemData where SubmitDate ='%s' and  Content='%s'", strCurDate,strCurContent);
            	//GetDetailInfo(strSql);   if(strCurState.equals("0"))
            	 
            	String strText="";	
            	/* if(strCurState.equals("0"))
                 {
                 	strCurState="已发布";
                 	strText=String.format("车型:%s\r\n车号:%s\r\n配属:%s\r\n发布时间:%s\r\n发布人:%s\r\n接收单位:%s\r\n故障内容:%s\r\n任务状态:%s\r\n修改时间:%s",strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurrUser,strCurRecv,strCurContent,strCurState,strCurRefreshTime);
                 	
                 }
 				else if(strCurState.equals("1"))
 				{
 				   strCurState="已接收";				   
                   strText=String.format("车型:%s\r\n车号:%s\r\n配属:%s\r\n发布时间:%s\r\n发布人:%s\r\n接收单位:%s\r\n故障内容:%s\r\n任务状态:%s\r\n接收时间:%s\r\n修改时间:%s",strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurrUser,strCurRecv,strCurContent,strCurState,strCurRecvTime,strCurRefreshTime);
                	
 				}
 				else if(strCurState.equals("2"))
 				{
 				   strCurState="已完成";
 				   strText=String.format("车型:%s\r\n车号:%s\r\n配属:%s\r\n发布时间:%s\r\n发布人:%s\r\n接收单位:%s\r\n责任人:%s\r\n故障内容:%s\r\n接收时间:%s\r\n任务状态:%s\r\n完成时间:%s\r\n修改时间:%s",strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurrUser,strCurRecv,strCurLabier,strCurContent,strCurRecvTime,strCurState,strCurEndTime,strCurRefreshTime);
 	               	
 				}
 				else if(strCurState.equals("3"))
 				{
 					strCurState="已撤销";
 					strText=String.format("车型:%s\r\n车号:%s\r\n配属:%s\r\n发布时间:%s\r\n发布人:%s\r\n接收单位:%s\r\n责任人:%s\r\n故障内容:%s\r\n任务状态:%s\r\n撤销时间:%s\r\n撤销原因:%s\r\n修改时间:%s",strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurrUser,strCurRecv,strCurLabier,strCurContent,strCurState,strCurCancelTime,strCurCancelReason,strCurRefreshTime);
 				}
 				else if(strCurState.equals("5"))
				{
					strCurState="不合格";
					strText=String.format("车型:%s\r\n车号:%s\r\n配属:%s\r\n发布时间:%s\r\n发布人:%s\r\n确认人:%s\r\n接收单位:%s\r\n责任人:%s\r\n故障内容:%s\r\n任务状态:%s\r\n检查不合格时间:%s\r\n不合格原因:%s\r\n接收时间:%s\r\n完成时间:%s\r\n修改时间:%s",strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurrUser,strCurQualUser,strCurRecv,strCurLabier,strCurContent,strCurState,strCurCheckFailTime,strCurFailReason,strCurRecvTime,strCurEndTime,strCurRefreshTime);
				}			
 				else if(strCurState.equals("6"))
 				{
 					strCurState="已提交";
 					strText=String.format("车型:%s\r\n车号:%s\r\n配属:%s\r\n接收单位:%s\r\n故障内容:%s\r\n任务状态:%s\r\n提交时间:%s\r\n提交人:%s\r\n修改时间:%s",strCurTrainType,strCurTrainNum,strCurAttach,strCurRecv,strCurContent,strCurState,strCurDate,strCurrUser,strCurRefreshTime);
 				}	*/	
            	
            	 if(strCurState.equals("0"))
                 {
                 	strCurState="已发布";
                 	strText=String.format("回修票类型:%s\r\n车型:%s\r\n车号:%s\r\n配属:%s\r\n发布时间:%s\r\n接收单位:%s\r\n故障内容:%s\r\n任务状态:%s\r\n修改时间:%s",strCurModifyType,strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurRecv,strCurContent,strCurState,strCurRefreshTime);
                 	
                 }
 				else if(strCurState.equals("1"))
 				{
 				   strCurState="已接收";				   
                   strText=String.format("回修票类型:%s\r\n车型:%s\r\n车号:%s\r\n配属:%s\r\n发布时间:%s\r\n接收单位:%s\r\n故障内容:%s\r\n任务状态:%s\r\n接收时间:%s\r\n修改时间:%s",strCurModifyType,strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurRecv,strCurContent,strCurState,strCurRecvTime,strCurRefreshTime);
                	
 				}
 				else if(strCurState.equals("2"))
 				{
 				   strCurState="已完成";
 				   strText=String.format("回修票类型:%s\r\n车型:%s\r\n车号:%s\r\n配属:%s\r\n发布时间:%s\r\n接收单位:%s\r\n责任人:%s\r\n故障内容:%s\r\n接收时间:%s\r\n任务状态:%s\r\n完成时间:%s\r\n修改时间:%s",strCurModifyType,strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurRecv,strCurLabier,strCurContent,strCurRecvTime,strCurState,strCurEndTime,strCurRefreshTime);
 	               	
 				}
 				else if(strCurState.equals("3"))
 				{
 					strCurState="已撤销";
 					strText=String.format("回修票类型:%s\r\n车型:%s\r\n车号:%s\r\n配属:%s\r\n发布时间:%s\r\n接收单位:%s\r\n责任人:%s\r\n故障内容:%s\r\n任务状态:%s\r\n撤销时间:%s\r\n撤销原因:%s\r\n修改时间:%s",strCurModifyType,strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurRecv,strCurLabier,strCurContent,strCurState,strCurCancelTime,strCurCancelReason,strCurRefreshTime);
 				}
 				else if(strCurState.equals("5"))
				{
					strCurState="不合格";
					strText=String.format("回修票类型:%s\r\n车型:%s\r\n车号:%s\r\n配属:%s\r\n发布时间:%s\r\n确认人:%s\r\n接收单位:%s\r\n责任人:%s\r\n故障内容:%s\r\n任务状态:%s\r\n检查不合格时间:%s\r\n不合格原因:%s\r\n接收时间:%s\r\n完成时间:%s\r\n修改时间:%s",strCurModifyType,strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurQualUser,strCurRecv,strCurLabier,strCurContent,strCurState,strCurCheckFailTime,strCurFailReason,strCurRecvTime,strCurEndTime,strCurRefreshTime);
				}			
 				else if(strCurState.equals("6"))
 				{
 					strCurState="已提交";
 					strText=String.format("回修票类型:%s\r\n车型:%s\r\n车号:%s\r\n配属:%s\r\n接收单位:%s\r\n故障内容:%s\r\n任务状态:%s\r\n提交时间:%s\r\n修改时间:%s",strCurModifyType,strCurTrainType,strCurTrainNum,strCurAttach,strCurRecv,strCurContent,strCurState,strCurDate,strCurRefreshTime);
 				}	
				AlertDialog.Builder builder;
				AlertDialog dialog;
				LayoutInflater inflater1 = getLayoutInflater();
				View view1 ;
				if(iWidth==1920 && iHeight ==1128)
					view1= inflater1.inflate(R.layout.userdefinedtoastbig,(ViewGroup) findViewById(R.id.toast_layout));
				else if(iWidth==1024 && iHeight ==720)
					view1= inflater1.inflate(R.layout.userdefinedtoastmid,(ViewGroup) findViewById(R.id.toast_layout));
				else
					view1= inflater1.inflate(R.layout.userdefinedtoastsmall,(ViewGroup) findViewById(R.id.toast_layout));
				
				TextView txtView_Title1 = (TextView) view1.findViewById(R.id.txt_Title);
				TextView txtView_Context1 = (TextView) view1.findViewById(R.id.txt_context);	
				txtView_Title1.setText("任务详细信息");
				txtView_Context1.setText(strText);
				builder = new AlertDialog.Builder(this);
				builder.setView(view1);
				dialog = builder.create();
				dialog.show(); 
              	break;
             case 6:   
             	//strUpdateState="7";  
             	if(!LoginActivity.TestConnect())
             	{
             		Toast.makeText(getApplicationContext(), "连接服务器数据库失败，请检查网络！" , Toast.LENGTH_LONG).show();
         			return false;
             	}
             	iCount=GetLocalDataCount();
             	if(iCount>0)
             	{  
             		SubmitAllDate(true);
             	}
             	
     	    	
 				break;	  	
            default:            	
            	break;
        }
        return super.onContextItemSelected(item);
    }
    private void SubmitAllDate(boolean bFlag) 
    {       	
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
  		String sql="select * from  ProblemData";
  		Cursor cursor=sdb.rawQuery(sql,null);		
  		String strSysID="", strTrainNum="",strDate="",strUser="", strRecv="", strLabier="",strContent="",strState="",strPath="",strAttch="",strTrainType="",strRecvTime="",strEndTime="",strCheckTime="",strCheckFailTime="",strRefreshTime="",strCancelTime="",strCancelReason="",strFailReason="",strQualUser="",strModifyType="";		
  		int iCount=0;
  		boolean bRet=false;
  		Toast mToast=null; 
  		mToast = Toast.makeText(this,"",Toast.LENGTH_LONG) ; 
  		iCheckFailCount=0;
  		UpdatePhotoFlag=0; 
  		UpdateFlag=0;  	
  		String  strSql="",strModifySql="";
  		boolean bModifyFlag=false;
  		while (cursor.moveToNext()) 
        { 			
  			strSysID= cursor.getString(cursor.getColumnIndex("_id"));
  			strTrainNum= cursor.getString(cursor.getColumnIndex("TrainNum"));
  			strDate =cursor.getString(cursor.getColumnIndex("SubmitDate"));
  			strUser =cursor.getString(cursor.getColumnIndex("Submitter"));
  			strRecv =cursor.getString(cursor.getColumnIndex("Receiver"));
  			strLabier = cursor.getString(cursor.getColumnIndex("Liabler"));
  			strContent= cursor.getString(cursor.getColumnIndex("Content"));  
  			strState= cursor.getString(cursor.getColumnIndex("State")); 
  			strPath= cursor.getString(cursor.getColumnIndex("ImagePath"));   			
  			strRecvTime=cursor.getString(cursor.getColumnIndex("ReceDate"));  			
			strEndTime= cursor.getString(cursor.getColumnIndex("EndDate"));			
			strCheckTime=cursor.getString(cursor.getColumnIndex("QualDate"));			
			strCancelTime=cursor.getString(cursor.getColumnIndex("CancelDate"));			
			strRefreshTime=cursor.getString(cursor.getColumnIndex("RefreshDate"));			
			strCheckFailTime=cursor.getString(cursor.getColumnIndex("FailDate"));					
			strAttch=cursor.getString(cursor.getColumnIndex("Attach"));			
			strTrainType=cursor.getString(cursor.getColumnIndex("TrainType")); 					
			strCancelReason=cursor.getString(cursor.getColumnIndex("CancelReason"));			
			strFailReason=cursor.getString(cursor.getColumnIndex("FailReason"));
			strQualUser=cursor.getString(cursor.getColumnIndex("QualUser"));			
			strModifyType=cursor.getString(cursor.getColumnIndex("ModifyType"));
			if(strRecvTime!=null)
			{
				strRecvTime="'"+strRecvTime+"'";
			}
			if(strEndTime!=null)
			{
				strEndTime="'"+strEndTime+"'";
			}
			if(strCheckTime!=null)
			{
				strCheckTime="'"+strCheckTime+"'";
			}
			if(strCancelTime!=null)
			{
				strCancelTime="'"+strCancelTime+"'";
			}
			if(strRefreshTime!=null)
			{
				strRefreshTime="'"+strRefreshTime+"'";
				bModifyFlag=true;
			}
			else
				bModifyFlag=false;
			if(strCheckFailTime!=null)
			{
				strCheckFailTime="'"+strCheckFailTime+"'";
			}
			
			if(strState==null)
				strState="6";
			int iState= Integer.parseInt(strState);			
			if(iState!=6)
			{
				strSql= String.format("update ProblemData set TrainNum='%s',Submitter='%s',Receiver='%s',Liabler='%s',Content='%s',State=%d,ReceDate=%s,EndDate=%s,QualDate=%s,CancelDate=%s,RefreshDate=%s,FailDate=%s,Attach='%s',TrainType='%s',CancelReason='%s',FailReason='%s',QualUser='%s',ModifyType='%s' where SysID='%s'", strTrainNum,strUser, strRecv, strLabier,strContent,iState,strRecvTime,strEndTime,strCheckTime,strCancelTime,strRefreshTime,strCheckFailTime,strAttch,strTrainType,strCancelReason,strFailReason,strQualUser,strModifyType,strSysID);
				
			}  
			else
			{
				if(bModifyFlag)
				{
					strSql= String.format("update ProblemData set TrainNum='%s',Submitter='%s',Receiver='%s',Liabler='%s',Content='%s',State=%d,ReceDate=%s,EndDate=%s,QualDate=%s,CancelDate=%s,RefreshDate=%s,FailDate=%s,Attach='%s',TrainType='%s',CancelReason='%s',FailReason='%s',QualUser='%s',ModifyType='%s' where SysID='%s'", strTrainNum,strUser, strRecv, strLabier,strContent,iState,strRecvTime,strEndTime,strCheckTime,strCancelTime,strRefreshTime,strCheckFailTime,strAttch,strTrainType,strCancelReason,strFailReason,strQualUser,strModifyType,strSysID);
					
				}
				else
				{
				    strSql = String.format("insert  into ProblemData values('%s','%s','%s','%s','%s','%s',%d,%s,%s,%s,%s,%s,%s,'%s','%s','%s','%s','%s','%s')", strTrainNum,strDate,strUser, strRecv, strLabier,strContent,iState,strRecvTime,strEndTime,strCheckTime,strCancelTime,strRefreshTime,strCheckFailTime,strAttch,strTrainType,strCancelReason,strFailReason,strQualUser,strModifyType);
				}
  			}
  			/* final EditText inputServer = new EditText(this);
  	        inputServer.setFocusable(true);
  	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
  	        builder.setTitle("请输入确认人姓名").setIcon(R.drawable.user).setView(inputServer);
  	        builder.setNegativeButton("取消", null);
  	        builder.setPositiveButton("确定",null);
  	        inputServer.setText(strSql);
  	      
  	        builder.show();
  			// Toast.makeText(getApplicationContext(), strSql , Toast.LENGTH_LONG).show();
  			/*if(!LoginActivity.TestConnect())
  	    	{
  	    	    Toast.makeText(getApplicationContext(), "由于网络原因，数据未能全部上传，请重新提交剩余部分。" , Toast.LENGTH_LONG).show();
	  	    	cursor.close();  
	  	        adapter.clearAll();			
	  			InitHeaderData(true);
	  			LoadLocalData();
	  			adapter  = new MyAdapter(ListTableView.this, lists,iWidth,iHeight);  
	  			listView.setAdapter(adapter);
	  			layout.setVisibility(View.VISIBLE);  
	  			listView.setSelection(adapter.getCount());	
  	    	    return ;
  	    	}*/
  			UpdatePhotoFlag=0;  	  			
  			if(!strPath.equals(""))
  			{
  				File file=new File(strPath);
				if (file.exists()) 
				{ // 判断文件是否存在
					if (file.isFile()) 
					{
						String strFileName=strDate;  		
			  			strFileName=strFileName.replaceAll("-", "");
			  			strFileName=strFileName.replaceAll(":", "");
			  			strFileName=strFileName.replaceAll(" ", "");        
			  			strFileName=strFileName+".jpg";
			  			readFileSendData(strUser,strPath,strFileName);	
			  			
			  			
					} 	
					else
					{
						UpdatePhotoFlag=2;  
					}
				}
				else
				{
					UpdatePhotoFlag=2;  
				}
	  			 			
  			} 
  			else
  			{
  				UpdatePhotoFlag=2;    				
  			}
  			while(UpdatePhotoFlag==0)
  			{
  				;
  			} 
  			if(UpdatePhotoFlag==1)
  				break; 	  			
  			UpdateFlag=0;  			
  			ExecUpLoadSQL(strSql,strDate,iCount,strPath);
  			while(UpdateFlag==0)
  			{
  				;
  			}  			
  			if(UpdateFlag==1)
  				break;
  			else
  			{  				
  				dbHelper.DeleteProblem(strDate);	
  			}
  			iCount++;		
		}
  		iCheckFailCount=GetLocalDataCount();		
  		if(bFlag&&iCheckFailCount<=0)
  		{
  	        mToast.setText("上传完成!") ;  
	    	mToast.show() ; 
	        cursor.close();  
	        adapter.clearAll();			
			InitHeaderData(true);		
			adapter  = new MyAdapter1(ListTableView1.this, lists,iWidth,iHeight);  
			listView.setAdapter(adapter);
			layout.setVisibility(View.VISIBLE);  
			listView.setSelection(adapter.getCount());	
  		}
  		if(bFlag&&iCheckFailCount>0)
  		{
  			String strTip=String.format("由于网络原因，余%d条数据未能成功上传，请重新提交剩余部分。", iCheckFailCount);
  			Toast.makeText(getApplicationContext(), strTip, Toast.LENGTH_LONG).show();
  			cursor.close();  
 	        adapter.clearAll();			
 			InitHeaderData(true);
 			LoadLocalData();
 			adapter  = new MyAdapter1(ListTableView1.this, lists,iWidth,iHeight);  
 			listView.setAdapter(adapter);
 			layout.setVisibility(View.VISIBLE);  
 			listView.setSelection(adapter.getCount());	
  		}
  		
        
    }  
    private void ExecUpLoadSQL(final String strSql,final String strCurSubmitDate,final int iPos,final String FilePath)	
	{		
		Runnable run = new Runnable()
		{			
			@Override
			public void run()
			{
				boolean ret=false;
				if(LoginActivity.TestConnect())
		    	{
		    		ret=DataBaseUtil.ExecSQL(strSql);		    		
		    	}									
				if(ret&&(UpdatePhotoFlag==2))
				{
									
					File file=new File(FilePath);
					if (file.exists()) 
					{ // 判断文件是否存在
						if (file.isFile()) 
						{ // 判断是否是文件
							file.delete(); // delete()方法 你应该知道 是删除的意思;
						} 				
					}
					file.delete();	
					UpdateFlag=2;
					/*Looper.prepare();					
					Toast.makeText(getApplicationContext(), strCurSubmitDate+"保存成功" , Toast.LENGTH_LONG).show();
					Looper.loop();
					*/
				}			
				else
				{
				    UpdateFlag=1;
				   
				}
				
			}
		};
		new Thread(run).start();
		 
	}
    public static void readFileSendData(final String strUser,final String strFilePath,final String strFileName)
	 {
		new Thread(new Runnable()
		{			
			@Override
			public void run()
			{
				boolean bRet=UploadImage(strUser,strFilePath,strFileName);			
				if(!bRet)
				{					
					UpdatePhotoFlag=1;						
				}
				else
				{
					UpdatePhotoFlag=2;
				}
			}
		}).start();
	}
		public static boolean UploadImage(String strUser,String filePath, String strFileName)
		{
		try 
		{
			//Log.i(TAG, "readFileSendData filePath=" + filePath);
			DataInputStream ddis = new DataInputStream(new FileInputStream(filePath));
			Socket socket = new Socket(LoginActivity.strServerIP, 4000);
		    OutputStream outputSteam = socket.getOutputStream(); 				
			DataOutputStream dos = new DataOutputStream(outputSteam);	
			String str=strUser+"|"+strFileName+"|"+ddis.available()+"|the end\r\n";		    	
			dos.write(str.getBytes("GBK"), 0, str.length());
			//Log.i(TAG, "ddis =" + ddis);
			int length = 0;
			int totalNum = 0;
			byte[] buffer = new byte[1024];// 每次上传的大小
			//Log.i(TAG, "img.avaliable=" + ddis.available());
		
			while ((length = ddis.read(buffer)) != -1)
			{
				totalNum += length;
				//Log.i(TAG, "length=" + length);
				//dos.writeInt(length);
				dos.write(buffer, 0, length);
				dos.flush();
			}
			// dos.writeInt(0);
			//dos.write(" \r\nThe end".getBytes());			
			dos.flush();
			if (ddis != null) 
			{
				ddis.close();
				ddis = null;
			}
			//Log.i(TAG, "readFileSendData(): send bytes=" + totalNum);
			return true;
		}
		catch (Exception e) {                    
			//Log.i(TAG, "UploadFileErr" );
			return false;
		}
		}
    private void GetLocalDetailData(String strSql) 
	{		
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();  		
  		Cursor cursor=sdb.rawQuery(strSql,null);	  		  		
		while (cursor.moveToNext()) 
        {			
			strCurState = cursor.getString(cursor.getColumnIndex("State"));
			strCurLabier =cursor.getString(cursor.getColumnIndex("Liabler"));
			strCurRecvTime= cursor.getString(cursor.getColumnIndex("ReceDate"));
			strCurEndTime = cursor.getString(cursor.getColumnIndex("EndDate"));
			strCurCheckTime =cursor.getString(cursor.getColumnIndex("QualDate")); 
			strCurCancelTime=cursor.getString(cursor.getColumnIndex("CancelDate"));	
			strCurRefreshTime= cursor.getString(cursor.getColumnIndex("RefreshDate"));
			strCurCheckFailTime =cursor.getString(cursor.getColumnIndex("FailDate")); 
			strCurAttach =cursor.getString(cursor.getColumnIndex("Attach")); 
			strCurTrainType =cursor.getString(cursor.getColumnIndex("TrainType")); 		
			strCurModifyType =cursor.getString(cursor.getColumnIndex("ModifyType")); 	
  			break;   			
		}
       cursor.close();   		
	}
    public  boolean GetRemoterDetailData(String strSql)
	{ 		
		try
		{		
			Connection conn = DataBaseUtil.getSQLConnection();;
			if(conn==null)
			{
				//Looper.prepare();
				//Toast.makeText(getApplicationContext(), "更新数据时连接服务器数hhhhhhh库失败，请检查网络连接！" , Toast.LENGTH_LONG).show();
				//Looper.loop();
				return false;
				
			}
			adapter.clearAll();
			InitHeaderData(false);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(strSql);	
			
			while(rs.next())
			{	
				strCurState = rs.getString("State");
				strCurLabier = rs.getString("Liabler");
				strCurRecvTime= rs.getString("ReceDate");
				strCurEndTime = rs.getString("EndDate");
				strCurCheckTime = rs.getString("QualDate");
				strCurCancelTime= rs.getString("CancelDate");	
				strCurRefreshTime= rs.getString("RefreshDate");
				strCurCheckFailTime = rs.getString("FailDate");
				strCurAttach = rs.getString("Attach");
				strCurTrainType = rs.getString("TrainType");
				strCurModifyType = rs.getString("ModifyType"); 	
				break;    			
			}
			rs.close();
			stmt.close();
			conn.close();
			
			
			//listView.setSelection(adapter.getCount());
			return true;			 
		} 
		catch (SQLException e)
		{
			e.printStackTrace();					
			Looper.prepare();
	    	Toast.makeText(getApplicationContext(), "连接数据库异常!" + e.getMessage(), Toast.LENGTH_LONG).show();
	    	Looper.loop();
	    	return false;
		}
		
	} 
	private void GetDetailInfo(final String strSql) {		
		Runnable run = new Runnable()
		{			
			@Override
			public void run()
			{
				boolean ret = GetRemoterDetailData( strSql);
				Message msg = new Message();
				msg.what=1001;
				Bundle data = new Bundle();	
				data.putString("sql", strSql);	
				data.putBoolean("result", ret);				
				msg.setData(data);
				mDetailDataHandler.sendMessage(msg);
			}
		};
		new Thread(run).start();		
	}
	Handler mDetailDataHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				case 1001:
					String  strSql = msg.getData().getString("sql");					
					boolean ret = msg.getData().getBoolean("result");					
					if(!ret)
					{
						GetLocalDetailData(strSql);
					}				
					if(strCurState.equals("0"))
						strCurState="已提交";
					else if(strCurState.equals("1"))
					   strCurState="已接收";
					else if(strCurState.equals("2"))
						   strCurState="已完成";
					else if(strCurState.equals("3"))
						   strCurState="已撤销";
					else if(strCurState.equals("4"))
						   strCurState="已合格";
					else if(strCurState.equals("5"))
						   strCurState="不合格";
					String strText="";					
					if(strCurState.equals("已撤销"))
						strText=String.format("  详细任务信息\r\n车型:%s\r\n车号:%s\r\n配属:%s\r\n提交时间:%s\r\n提交人:%s\r\n接收单位:%s\r\n责任人:%s\r\n故障内容:%s\r\n任务状态:%s\r\n撤销时间:%s\r\n撤销原因:%s\r\n接收时间:%s\r\n完成时间:%s\r\n检验合格时间:%s\r\n检验不合格时间:%s\r\n更新时间:%s",strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurrUser,strCurRecv,strCurLabier,strCurContent,strCurState,strCurCancelTime,strCurCancelReason,strCurRecvTime,strCurEndTime,strCurCheckTime,strCurCheckFailTime,strCurRefreshTime);
					else
						strText=String.format("  详细任务信息\r\n车型:%s\r\n车号:%s\r\n配属:%s\r\n提交时间:%s\r\n提交人:%s\r\n接收单位:%s\r\n责任人:%s\r\n故障内容:%s\r\n任务状态:%s\r\n接收时间:%s\r\n完成时间:%s\r\n检验合格时间:%s\r\n检验不合格时间:%s\r\n更新时间:%s",strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurrUser,strCurRecv,strCurLabier,strCurContent,strCurState,strCurRecvTime,strCurEndTime,strCurCheckTime,strCurCheckFailTime,strCurRefreshTime);
					Toast.makeText(getApplicationContext(),strText,Toast.LENGTH_SHORT).show();  
					
					
					break;
				default:
					break;
			}
		};
	};
	public void addLoadData(String strSysID,String strTrainNum,String strDate,String strUser,String strRecv,String strLabier,String strContent,String strState,String strRecvTime,String strEndTime,String strCheckTime,String strCancelTime,String strRefreshTime,String strCheckFailTime,String strAttch,String strTrainType,String strCancelReason,String strFailReason,String strQualUser,String strModifyType)
    {    
    	//adapter  = new MyAdapter(ListTableView.this, lists);    
    	ArrayList<String> list = new ArrayList<String>();    	
    	list.add(strSysID);
    	list.add(strTrainNum);
    	list.add(strDate); 
    	list.add(strUser);
    	list.add(strRecv);
    	list.add(strLabier);
    	list.add(strContent);	
    	list.add(strState);	
    	list.add(strRecvTime);	
    	list.add(strEndTime);	
    	list.add(strCheckTime);	
    	list.add(strCancelTime);	
    	list.add(strRefreshTime);	
    	list.add(strCheckFailTime);	
    	list.add(strAttch);	
    	list.add(strTrainType);	
    	list.add(strCancelReason);	
    	list.add(strFailReason ); 
    	list.add(strQualUser ); 
    	list.add(strModifyType); 
    	/*if(strState.equals("0"))
    	{
    		list.add("提交");
    	}
    	else if(strState.equals("1"))
    	{
    		list.add("接收");
    	}
    	else if(strState.equals("2"))
    	{
    		list.add("完成");
    	}  
    	else if(strState.equals("3"))
    	{
    		list.add("撤销");
    	} */
    	
		lists.add(list);
		//listView.setAdapter(adapter);
		//layout.setVisibility(View.VISIBLE);  
    }
    private void ExecSQL(final String strSql,final String strType)	
	{
		Runnable run = new Runnable()
		{			
			@Override
			public void run()
			{
				boolean ret=false;
				if(LoginActivity.TestConnect())					
					ret= DataBaseUtil.ExecSQL(strSql);
				Message msg = new Message();
				msg.what=1001;
				Bundle data = new Bundle();
				data.putString("type", strType);			
				data.putBoolean("result", ret);				
				msg.setData(data);
				mExecHandler.sendMessage(msg);
			}
		};
		new Thread(run).start();
		 
	}  
    private void UpdateRemoteState(int iPos) 
    {
    	if(!LoginActivity.TestConnect())
    	{
    		Toast.makeText(getApplicationContext(), "连接服务器数据库失败，请检查网络！" , Toast.LENGTH_LONG).show();
			return;
    	}
    	String  strSql ="";
    	SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");     
    	strCurSubmitDate   =   sDateFormat.format(new   java.util.Date());    
    	if(strUpdateState.equals("1"))
    	{
    		strCurRecvTime=strCurSubmitDate;
    		strSql =String.format("update ProblemData set State=%s,ReceDate='%s' where  SubmitDate ='%s' and  Content='%s'", strUpdateState,strCurSubmitDate,strCurDate,strCurContent);
    	}
    	else if(strUpdateState.equals("2"))
    	{    		
    		strCurEndTime=strCurSubmitDate;
    		strSql =String.format("update ProblemData set State=%s,Liabler='%s',EndDate='%s' where  SysID='%s' and SubmitDate ='%s'", strUpdateState,strCurLabier,strCurSubmitDate,strCurSysID,strCurDate);
    	}
    	else if(strUpdateState.equals("3"))
    	{
    		strCurCancelTime=strCurSubmitDate;
    		strSql =String.format("update ProblemData set State=%s,Liabler='%s',CancelDate='%s',CancelReason='%s' where  SysID='%s' and SubmitDate ='%s'", strUpdateState,strCurLabier,strCurSubmitDate,strCurCancelReason,strCurSysID,strCurDate);
    	}
    	ExecSQL(strSql,"State");	
    	adapter.UpdateState(iPos, strUpdateState,strCurLabier,strCurRecvTime,strCurEndTime,strCurCancelTime,strCurCancelReason);
    	listView.setAdapter(adapter);
        layout.setVisibility(View.VISIBLE); 
	
    }
 
    public boolean onCreateOptionsMenu(Menu menu) {
		// menu.add("一个参数的add方法的菜单");
		// menu.add("一个参数的add方法的菜单2");
		 //menu.add(R.string.titileRes);
    	
    	menu.add(0, ITEM, 0, "修改密码").setIcon(R.drawable.changepwd);
    	menu.add(0, ITME4, 0, "更新时间").setIcon(R.drawable.updatetime);
    	menu.add(0, ITME2, 0, "启动飞鸽").setIcon(R.drawable.feige);
		menu.add(0, ITME3, 0, "退出系统").setIcon(R.drawable.exitsys);
		

		//menu.add(0, ITEM, 0, "下载").setIcon(R.drawable.download);//设置图标
		//menu.add(0, ITME2, 0, "上传").setIcon(R.drawable.upload);

		return true;
	}
    
    private void CancelDialog(final int iPos) 
    {    	
        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入撤销的原因").setIcon(R.drawable.user).setView(inputServer);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                    	
                    	strCurCancelReason = inputServer.getText().toString();
                    	inputTitleDialog(iPos); 
                       
                    }
                });
        builder.show();
       
    }
    private void inputTitleDialog(final int iPos) 
    {    	
        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入责任人").setIcon(R.drawable.user).setView(inputServer);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                    	
                    	strCurLabier = inputServer.getText().toString();
                    	if(!strCurLabier.equals(""))
                    	    UpdateRemoteState(iPos);
                       
                    }
                });
        builder.show();
       
    }
	// 通过点击了哪个菜单子项来改变Activity的标题
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case ITEM:
			Intent intent1=new Intent(ListTableView1.this,ModifyActivity.class);
    		Bundle bundle1=new Bundle();          	
    		bundle1.putBoolean("AdminUser", false);
    		bundle1.putString("CurUser", LoginActivity.strCurUser);          
            intent1.putExtras(bundle1);
			startActivity(intent1);		
			break;
		case ITME2:
			launchApp("com.netfeige");
			break;
		case ITME3:
			AlertDialog.Builder builder = new Builder(ListTableView1.this); 
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
			
		case ITME4:
			UpdateTimeSet();
			break;

		}
		return true;
	}
	private void UpdateTimeSet() 
    {    	
        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入更新时间（秒）").setIcon(R.drawable.user).setView(inputServer);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {                    	
                    	 String strCurIP = inputServer.getText().toString(); 
                    	 Pattern p = Pattern.compile("[0-9]*"); 
                         Matcher m = p.matcher(strCurIP);                       
                    	 if(m.matches())
                    	 {
                    		LoginActivity.iUpdateTime=Integer.parseInt(strCurIP);
                    		SharedPreferences sp = getSharedPreferences("MyUserInfo", 0);
                    		// 写入配置文件
                    		Editor spEd = sp.edit();    		
                    		spEd.putInt("UpdateTime",LoginActivity.iUpdateTime);                     		
                    		spEd.commit();		
                    		Toast.makeText(ListTableView1.this,"设置成功",Toast.LENGTH_SHORT).show();
                    		
                    	}
                    	
                    }
                });
        builder.show();
       
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