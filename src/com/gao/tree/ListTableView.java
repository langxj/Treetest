package com.gao.tree;   
  

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;   
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;   
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;   
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
 
public class ListTableView extends Activity {   
    private ListView listView;
    private HorizontalScrollView layout;
    private ArrayList<String> list1= new ArrayList<String>();
    private ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
    private static String strCurrUser,strCurLoginUser;
	private String strCurRecvUnit;
    private EditText edtTrainNum, edtContent,edtAttach;
    private Spinner  edtRecv;
    private Button btnSubmit,btnQuery,btnPhoto;
    public  int iRowCount=0,iDepartCount=0;
	public  int iUserCount=0;
	public String strDepartment[];
	public  String strUName[];
    public  int iUpdateRowNum=0;
    public  String strUpdateState;
    private Timer mTimer;
    private String  strOldTrainum,strOldContent;
    private String  strCurSysID="",strCurTrainNum,strCurDate, strCurRecv, strCurContent,strCurLabier,strCurState,strCurImagePath,strCurCancelReason,strCurFailReason;
    private String  strCurQualUser,strCurModifyType,strCurTrainType,strCurAttach,strCurSubmiter,strCurRecvTime,strCurEndTime,strCurCheckTime,strCurCheckFailTime,strCurRefreshTime,strCurCancelTime;
    private DBHelper dbHelper;    
    private String  strCurUpdateDate;
    private boolean bUpdateTreeFlag;
    MyAdapter adapter;
    private static final int ITEM = Menu.FIRST;
	private static final int ITEM1 = Menu.FIRST + 1;
	private static final int ITEM2 = Menu.FIRST + 2;
	private static final int ITEM3 = Menu.FIRST + 3;
	private static final int ITEM4 = Menu.FIRST + 4;
	private static final int ITEM5 = Menu.FIRST + 5;
	private static final int ITEM6 = Menu.FIRST + 6;
	private static int   UpdateFlag=0;
	private static int UpdatePhotoFlag=0;
	private int iCheckFailCount=0;
	private Intent newintent;
    String strLoadsql = "select * from  ProblemData  where State<>4 order by SubmitDate";
    private static final int PHOTO_CAPTURE = 0x11;
	private static String photoPath = "/sdcard/treetest/";
	private static String photoName =  "laolisb.jpg";
	private static String imagePath = photoPath + photoName;
	Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
	private ImageView img_photo;
	private static final String TAG = "uploadFile";
	private int iSelctListPos=0;
	private  int iWidth,iHeight;
	private ArrayAdapter<String> DepartAdapter;	

    public void onCreate(Bundle savedInstanceState) {  
    	//requestWindowFeature( Window.FEATURE_NO_TITLE ); //无标题
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        super.onCreate(savedInstanceState); 
        View bv = this.findViewById(android.R.id.title);
        ((TextView) bv).setTextColor(Color.BLACK);
        ((TextView) bv).setTextSize(15);
        ((View) bv.getParent()).setBackgroundResource(R.drawable.background_login);
        Display display = getWindowManager().getDefaultDisplay();
        iWidth=display.getWidth();
        iHeight=display.getHeight();
      
    	if(iWidth==1920 && iHeight ==1128)
    		setContentView(R.layout.listtablebig);
		else if(iWidth==1024 && iHeight ==720)
			setContentView(R.layout.listtablemid);
		else
			setContentView(R.layout.listtablesmall);
           
        btnSubmit=(Button)findViewById(R.id.submit);  
        btnQuery=(Button)findViewById(R.id.add);  
        //btnPhoto = (Button) findViewById(R.id.photo);
        edtTrainNum=(EditText)findViewById(R.id.editSub);
        edtAttach=(EditText)findViewById(R.id.editAttach);
        edtRecv=(Spinner)findViewById(R.id.Spinner01);         
        edtContent=(EditText)findViewById(R.id.editContent);  
        edtTrainNum.setText(LoginActivity.strCurTrainNum);
        edtAttach.setText(LoginActivity.strCurAttach);
        
        listView = (ListView) findViewById(R.id.listview);
        layout = (HorizontalScrollView) findViewById(R.id.layout);
        dbHelper=new DBHelper(this);	
        Intent intent=getIntent(); 
        iDepartCount=intent.getIntExtra("DepartCount", 0);
		iUserCount=intent.getIntExtra("UserCount", 0);
		strCurLoginUser=LoginActivity.strCurUser; 
	    strDepartment=intent.getStringArrayExtra("Department");
	    strCurTrainType=intent.getStringExtra("TrainType");	  
	    strCurModifyType=LoginActivity.strCurModifyType;
        String strReceive=intent.getStringExtra("Receive"); 
        String strContent=intent.getStringExtra("content");  
        bUpdateTreeFlag=intent.getBooleanExtra("Flag", false);  
        SpinnerAdapter dder = new SpinnerAdapter(this,android.R.layout.simple_spinner_dropdown_item, strDepartment);
        edtRecv.setAdapter(dder);

        /*DepartAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,strDepartment);
        DepartAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtRecv.setAdapter(DepartAdapter);
        edtRecv.setOnItemSelectedListener(new SpinnerSelectedListener());     
        //edtRecv.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(),"找到"+strReceive, Toast.LENGTH_SHORT).show();*/
        int iPos=0;
        for(int i=0;i<strDepartment.length;i++)
        {        	
        	if(strReceive.equals(strDepartment[i]))
        	{          		
        		iPos=i;
        		break;
        	}
        }   
        if(iPos==0)
            edtRecv.setSelection(edtRecv.getCount()-1);
        else
        	edtRecv.setSelection(iPos);
        	
        strCurRecv=strReceive;     
        edtContent.setText(strContent); 
        //btnPhoto.setOnClickListener(new photo());
        btnSubmit.setOnClickListener(new OnClickListener() 
        {
			public void onClick(View arg0) 
			{				
				if(btnSubmit.getText().equals("修改"))
				{
					if(strCurTrainNum.equals(""))
					{
						Toast.makeText(getApplicationContext(),"车号不能为空 ", Toast.LENGTH_SHORT).show();
						edtTrainNum.setFocusable(true);
						edtTrainNum.setFocusableInTouchMode(true);
						edtTrainNum.requestFocus();
						edtTrainNum.requestFocusFromTouch();	
						return;
					}
					if(strCurAttach.equals(""))
					{
						Toast.makeText(getApplicationContext(),"配属不能为空 ", Toast.LENGTH_SHORT).show();
						edtAttach.setFocusable(true);
						edtAttach.setFocusableInTouchMode(true);
						edtAttach.requestFocus();
						edtAttach.requestFocusFromTouch();	
						return;
					}
					
					if(strCurRecv==null)
					{
						Toast.makeText(getApplicationContext(),"接收单位不能为空 ", Toast.LENGTH_SHORT).show();
						return;
					}
					if(strCurContent.equals(""))
					{
						Toast.makeText(getApplicationContext(),"故障内容不能为空 ", Toast.LENGTH_SHORT).show();
						edtContent.setFocusable(true);
						edtContent.setFocusableInTouchMode(true);
						edtContent.requestFocus();
						edtContent.requestFocusFromTouch();	
						return;
					}
					strCurTrainNum=edtTrainNum.getText().toString();
					//lxjstrCurRecv=edtRecv.getText().toString();				
					strCurContent=edtContent.getText().toString();		
					SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");     
					strCurRefreshTime  =   sDateFormat.format(new   java.util.Date());
					boolean bRet=isLocalData(strCurDate) ;
					if(bRet)
					{
						dbHelper.UpdateProblem(strCurTrainNum, strCurDate, strCurRecv, strCurContent);	
						adapter.UpdateData(iUpdateRowNum,edtTrainNum.getText().toString(),strCurRecv,edtContent.getText().toString(),strUpdateState,strCurRefreshTime);
			            listView.setAdapter(adapter);
			            layout.setVisibility(View.VISIBLE); 	            					
			            btnSubmit.setText("提交");
			            edtContent.setText(""); 
					}
					else
					    UpdateRemoteData(iUpdateRowNum);
					
				}
				else
					SaveListData();
			}	
        }); 
        
        btnQuery.setOnClickListener(new OnClickListener() 
        {
			public void onClick(View arg0) 
			{			
					
				String strTrainNum=edtTrainNum.getText().toString();				
				String strContent=edtContent.getText().toString();	
				String strAttach=edtAttach.getText().toString();	
				if(strTrainNum.equals("")&&strContent.equals("")&&strAttach.equals("")&&strCurRecv==null)
				{
					Toast.makeText(getApplicationContext(),"至少输入一个查询条件", Toast.LENGTH_SHORT).show();
					edtTrainNum.setFocusable(true);
					edtTrainNum.setFocusableInTouchMode(true);
					edtTrainNum.requestFocus();
					edtTrainNum.requestFocusFromTouch();	
					return;
				}
				String strSql,strWhere="";
				boolean   bFlag=false;
				
				if(!strTrainNum.equals(""))
				{
					strWhere="TrainNum like '%"+strTrainNum+"%'";
				    bFlag=true;
				}
				if(!strAttach.equals(""))
				{
					if(bFlag)
					   strWhere=strWhere+" and Attach like '%"+strAttach+"%'";
					else
						strWhere=strWhere+"Attach like '%"+strAttach+"%'";
					 bFlag=true;
				}
				if(!strContent.equals(""))
				{
					if(bFlag)
					    strWhere=strWhere+" and Content like '%"+strContent+"%'";
					else
						 strWhere=strWhere+"Content like '%"+strContent+"%'";
					bFlag=true;		
				}
				if(strCurRecv!=null)
				{
					if(bFlag)
					   strWhere=strWhere+" and Receiver like '%"+strCurRecv+"%'";
					else
					   strWhere=strWhere+"Receiver like '%"+strCurRecv+"%'";					
				}
				strLoadsql= String.format("select * from ProblemData where %s and state<>4 order by SubmitDate",strWhere);				
				//Toast.makeText(getApplicationContext(), strLoadsql, Toast.LENGTH_LONG).show(); 
				LoadData();
			}	
        }); 
       /*listView.setOnItemClickListener(new ListView.OnItemClickListener() {  
      	  
            @Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                    long arg3) {  
            	ArrayList<String> list =  (ArrayList<String>) adapter.getItem(arg2);
            	//Toast.makeText(getApplicationContext(), list.get(3)+strCurrUser, Toast.LENGTH_LONG).show();   
            	if(strCurLoginUser.equals(list.get(3)))
  			    {	
	            	adapter.setSelectedPosition(arg2); 
	            	adapter.notifyDataSetInvalidated();     
  			    }
  
            }  
        });*/
      //设置长按菜单项
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener()
        {       
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
			{	
				AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterContextMenuInfo) menuInfo;
				adapter  = new MyAdapter(ListTableView.this, lists,iWidth,iHeight);				
				ArrayList<String> list =  (ArrayList<String>) adapter.getItem(contextMenuInfo.position);							
			    //if(strCurLoginUser.equals(list.get(3))||LoginActivity.strCurType.equals("管理员"))
				int iCount=GetLocalDataCount();
				if(lists.size()>0)
			    {				    	
				    menu.setHeaderTitle("请选择需要进行的操作");
				    menu.add(0, 7, 0, "详细信息");	               
	                menu.add(0, 5, 0, "获取数据");  
	                menu.add(0, 6, 0, "查看图片");
				    if(!LoginActivity.strCurType.equals("生产员"))
				    {
					    menu.add(0, 4, 0, "拍摄故障");
		                menu.add(0, 0, 0, "删除任务");
		                menu.add(0, 1, 0, "修改任务");             
		                if(list.get(7).equals("2"))
		                {
		                	menu.add(0, 2, 0, "任务合格");	
		                	menu.add(0, 3, 0, "任务不合格");		                	
		                } 
		                if(iCount>0)
		                   menu.add(0, 10, 0, "提交所有");
		                if(list.get(7).equals("6"))
		                {
		                	menu.add(0, 8, 0, "发布任务");	
		                	//menu.add(0, 9, 0, "发布所有");		                	
		                } 
	                } 
	              
			    }
			    else
			    {
			    	 menu.setHeaderTitle("请选择需要进行的操作");
			    	 menu.add(0, 5, 0, "获取数据"); 
			    	
			    }
			  
			    
			}
        });  
        InitHeaderData(true);
        //mTimer = new Timer();
	    //LoadData();	
	    
	    adapter.clearAll();			
		InitHeaderData(true);
		int iCount=GetLocalDataCount();
		if(iCount>0)
    		   LoadLocalData();  
		adapter  = new MyAdapter(ListTableView.this, lists,iWidth,iHeight);  
		listView.setAdapter(adapter);
		layout.setVisibility(View.VISIBLE);  
		listView.setSelection(adapter.getCount());	 
    }  
    
   
    public boolean onCreateOptionsMenu(Menu menu) {
		// menu.add("一个参数的add方法的菜单");
		// menu.add("一个参数的add方法的菜单2");
		 //menu.add(R.string.titileRes);
    	if(LoginActivity.strCurType.equals("管理员"))
    	{    		
    		menu.add(0, ITEM, 0, "注册用户").setIcon(R.drawable.registeruser);
    		menu.add(0, ITEM4, 0, "管理用户").setIcon(R.drawable.user); 
    		menu.add(0, ITEM5, 0, "服务器设置").setIcon(R.drawable.setip);      		
    	}
    	
    	menu.add(0, ITEM6, 0, "更新时间").setIcon(R.drawable.updatetime);
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
			Intent intent=new Intent(ListTableView.this,RegisterActivity.class);
    		Bundle bundle=new Bundle();          	
    		bundle.putInt("DepartCount", iDepartCount);
            bundle.putInt("UserCount", iUserCount);
            bundle.putStringArray("Department", strDepartment);
            intent.putExtras(bundle);
			startActivity(intent);		
			break;
		case ITEM1:
			Intent intent1=new Intent(ListTableView.this,ModifyActivity.class);
    		Bundle bundle1=new Bundle();          	
    		bundle1.putBoolean("AdminUser", true);
    		bundle1.putString("CurUser", strCurLoginUser);          
            intent1.putExtras(bundle1);
			startActivity(intent1);		
			break;
		case ITEM2:
			launchApp("com.netfeige");
		    break;
		case ITEM3:
			AlertDialog.Builder builder = new Builder(ListTableView.this); 
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
			Intent intent4=new Intent(ListTableView.this,UserListView.class);    		
			startActivity(intent4);		
		    break;
	    case ITEM5:
	    	IPDialog();	
		    break;
	    case ITEM6:
	    	UpdateTimeSet() ;	
		    break;
		}
		return true;
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
    private void LoadData() {    
    	   	    
    		Runnable run = new Runnable()
    		{			
    			@Override
    			public void run()    			
    			{
    				
    				boolean bRet=false;
    				if(LoginActivity.TestConnect())
   					    bRet=LoadRemoterData();
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
         list.add("序号");
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
	         adapter  = new MyAdapter(ListTableView.this, lists,iWidth,iHeight);       
	         listView.setAdapter(adapter);
	         layout.setVisibility(View.VISIBLE);
         }
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
    public boolean   isLocalData(String strSubDate)
	{
    	SQLiteDatabase sdb=dbHelper.getReadableDatabase();  
    	String strSql=String.format("select * from problemdata where SubmitDate='%s'" ,strSubDate);  	
  		Cursor cursor=sdb.rawQuery(strSql,null);  		
  		boolean bRet=false;
		while (cursor.moveToNext()) 
        {	
			bRet=true;
			break;
        }
        cursor.close();  
      
        return bRet;
        
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
	    if(bUpdateTreeFlag)
	    {
	 		Intent intent = new Intent();
	 	    intent.setAction("action.refreshDictFriend");
	 	    sendBroadcast(intent);
	    }
	    //mTimer.cancel();
	}
	private void setTimerTask() {
	    mTimer.schedule(new TimerTask() {
	        @Override
	        public void run() {	
	        	boolean bRet=false;
	        	if(LoginActivity.TestConnect())
					 bRet=LoadRemoterData();
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
	            			//Toast.makeText(getApplicationContext(), "更新数据时连接服务器数据库失败，请检查网络连接！" , Toast.LENGTH_LONG).show();
            			if(!LoginActivity.TestConnect())
            	    	{
            	    		Toast.makeText(getApplicationContext(), "连接服务器数据库失败，请检查网络！" , Toast.LENGTH_LONG).show();
            				
            	    	} 
            			adapter.clearAll();			
	        			InitHeaderData(true);
	        			int iCount=GetLocalDataCount();
	        			if(iCount>0)
		            		   LoadLocalData();  
            			adapter  = new MyAdapter(ListTableView.this, lists,iWidth,iHeight);  
            			listView.setAdapter(adapter);
		        		layout.setVisibility(View.VISIBLE);  
		        		listView.setSelection(adapter.getCount());	           		
	            		
	            	}
	            	else
	            	{	
	            		int iCount=GetLocalDataCount();
	            		if(iCount>0)
	            		   LoadLocalData();  	
		            	adapter  = new MyAdapter(ListTableView.this, lists,iWidth,iHeight);  	            	
		            	listView.setAdapter(adapter);
		        		layout.setVisibility(View.VISIBLE);  
		        		listView.setSelection(adapter.getCount());	
		        		strLoadsql = "select * from  ProblemData  where State<>4 order by SubmitDate";
	            	}
	            	
	                break;
	            default:
	                break;
	        }
	    }

		private void SaveExistData() 
		{
			   for (int i = 1; i < lists.size(); i++) {  
				   ArrayList<String> list=   lists.get(i); 
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
					
				    dbHelper.DeleteProblem(strCurDate);
				    dbHelper.InsertProblem(strCurSysID,strCurTrainNum, strCurDate, strCurrUser, strCurRecv, strCurLabier, strCurContent,strCurState,strCurImagePath,strCurRecvTime,strCurEndTime,strCurCheckTime,strCurCancelTime,strCurRefreshTime,strCurCheckFailTime,strCurAttach,strCurTrainType,strCurCancelReason,strCurFailReason,"",strCurModifyType);
			    }  
			 
			
		}

		
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
        final int iPos= (int) info.position;// 这里的info.id对应的就是数据库中_id的值
        adapter  = new MyAdapter(ListTableView.this, lists,iWidth,iHeight);
		final ArrayList<String> list =  (ArrayList<String>) adapter.getItem(iPos);		
		iSelctListPos=iPos;	
		strCurSysID=list.get(0);	
		strCurTrainNum=list.get(1);	
    	strCurDate=list.get(2);    
    	strCurrUser=list.get(3);  
    	strCurRecvUnit=strCurRecv=list.get(4);	
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
		strCurImagePath=imagePath;	/**/	
        switch (item.getItemId()) {
            case 0:
            	AlertDialog.Builder dlg = new Builder(ListTableView.this); 
            	dlg.setIcon(android.R.drawable.ic_dialog_info);
            	dlg.setMessage("确定要删除吗?"); 
            	dlg.setTitle("提示"); 
            	dlg.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener()
            	{ 
                    public void onClick(DialogInterface dialog, int which)
                    { 
                        dialog.dismiss(); 
                        DeleteRemoteData(list,iPos);            	
                  	    adapter.remove(iPos);            	
                  	    listView.setAdapter(adapter);
                        layout.setVisibility(View.VISIBLE); 
                    } 
 	                
            	}); 
            	dlg.setNegativeButton("取消",new android.content.DialogInterface.OnClickListener() 
            	{ 
                    public void onClick(DialogInterface dialog, int which) 
                    { 
                        dialog.dismiss(); 
                    } 
 	               
            	}); 
            	dlg.create().show(); 
            	
                break;
            case 1:   
            	if(strCurState.equals("6"))
        		     strUpdateState="6";
            	else
            		 strUpdateState="0";/**/
            	
            	iUpdateRowNum=iPos;            	
            	edtTrainNum.setText(strCurTrainNum); 
            	
            	
            	strOldTrainum=strCurTrainNum;
                strOldContent=strCurContent;
               
                int iSel=0; 
                if(strCurRecv!=null)
                {
                	for(int i=0;i<strDepartment.length;i++)
                	{        	
	                	if(strCurRecv.equals(strDepartment[i]))
	                	{          		
	                		iSel=i;
	                		break;
	                	}
                    } 
                	edtRecv.setSelection(iSel);     
                }
                else                	
                {
                	edtRecv.setSelection(edtRecv.getCount()-1); 
                }
                
                /**/
            	//lxjedtRecv.setText(strCurRecv); 
                edtContent.setText(strCurContent); 
                edtAttach.setText(strCurAttach);
                btnSubmit.setText("修改");
                break;
            case 2:              	
        		strUpdateState="4";        		
        		QualDialog(iPos,strCurRecv); 
            	
                break;
            case 3:          	
        		strUpdateState="5";          		
        		CancelDialog(iPos,strCurRecv);            	  	           	
            	break;
            case 4:   
            	strUpdateState=strCurState; 
            	DoCapture();
            	break;
            case 5:   
            	strLoadsql = " select * from  ProblemData  where State<>4 order by SubmitDate";
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
    	    	} /**/  				
				break;
            case 6:            	
            	File file=new File(imagePath);
            	if(!file.exists())
            	{
            		Toast.makeText(ListTableView.this,"尚未拍摄故障图片，请先拍照",Toast.LENGTH_SHORT).show();
            		break;
            	}
            	Intent intent = new Intent(Intent.ACTION_VIEW);
            	//Uri mUri = Uri.parse("file://" + picFile.getPath());Android3.0以后最好不要通过该方法，存在一些小Bug
            	intent.setDataAndType(Uri.fromFile(file), "image/*");
            	startActivity(intent);
            	break;
            case 7:
            	//String strSql=String.format("select * from ProblemData where SubmitDate ='%s' and  Content='%s'", strCurDate,strCurContent);
            	//GetDetailInfo(strSql);   if(strCurState.equals("0"))
            	String strText="";	
                if(strCurState.equals("0"))
                {
                	strCurState="已发布";
                	strText=String.format("回修票类型:%s\r\n车型:%s\r\n车号:%s\r\n配属:%s\r\n发布时间:%s\r\n发布人:%s\r\n接收单位:%s\r\n故障内容:%s\r\n任务状态:%s\r\n修改时间:%s",strCurModifyType,strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurrUser,strCurRecv,strCurContent,strCurState,strCurRefreshTime);
                	
                }
				else if(strCurState.equals("1"))
				{
				   strCurState="已接收";				   
				   strText=String.format("回修票类型:%s\r\n车型:%s\r\n车号:%s\r\n配属:%s\r\n发布时间:%s\r\n发布人:%s\r\n接收单位:%s\r\n故障内容:%s\r\n任务状态:%s\r\n接收时间:%s\r\n修改时间:%s",strCurModifyType,strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurrUser,strCurRecv,strCurContent,strCurState,strCurRecvTime,strCurRefreshTime);
               	
				}
				else if(strCurState.equals("2"))
				{
				   strCurState="已完成";
				   strText=String.format("回修票类型:%s\r\n车型:%s\r\n车号:%s\r\n配属:%s\r\n发布时间:%s\r\n发布人:%s\r\n接收单位:%s\r\n责任人:%s\r\n故障内容:%s\r\n接收时间:%s\r\n任务状态:%s\r\n完成时间:%s\r\n修改时间:%s",strCurModifyType,strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurrUser,strCurRecv,strCurLabier,strCurContent,strCurRecvTime,strCurState,strCurEndTime,strCurRefreshTime);
	               	
				}
				else if(strCurState.equals("3"))
				{
					strCurState="已撤销";
					strText=String.format("回修票类型:%s\r\n车型:%s\r\n车号:%s\r\n配属:%s\r\n发布时间:%s\r\n发布人:%s\r\n接收单位:%s\r\n责任人:%s\r\n故障内容:%s\r\n任务状态:%s\r\n撤销时间:%s\r\n撤销原因:%s\r\n修改时间:%s",strCurModifyType,strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurrUser,strCurRecv,strCurLabier,strCurContent,strCurState,strCurCancelTime,strCurCancelReason,strCurRefreshTime);
				}
				else if(strCurState.equals("5"))
				{
					strCurState="不合格";
					strText=String.format("回修票类型:%s\r\n车型:%s\r\n车号:%s\r\n配属:%s\r\n发布时间:%s\r\n发布人:%s\r\n确认人:%s\r\n接收单位:%s\r\n责任人:%s\r\n故障内容:%s\r\n任务状态:%s\r\n检查不合格时间:%s\r\n不合格原因:%s\r\n接收时间:%s\r\n完成时间:%s\r\n修改时间:%s",strCurModifyType,strCurTrainType,strCurTrainNum,strCurAttach,strCurDate,strCurrUser,strCurQualUser,strCurRecv,strCurLabier,strCurContent,strCurState,strCurCheckFailTime,strCurFailReason,strCurRecvTime,strCurEndTime,strCurRefreshTime);
				}		
				else if(strCurState.equals("6"))
				{
					strCurState="已提交";
					strText=String.format("回修票类型:%s\r\n车型:%s\r\n车号:%s\r\n配属:%s\r\n接收单位:%s\r\n故障内容:%s\r\n任务状态:%s\r\n提交时间:%s\r\n提交人:%s\r\n修改时间:%s",strCurModifyType,strCurTrainType,strCurTrainNum,strCurAttach,strCurRecv,strCurContent,strCurState,strCurDate,strCurrUser,strCurRefreshTime);
				}		
				AlertDialog.Builder builder;
				AlertDialog dialog;
				LayoutInflater inflater1 = getLayoutInflater();
				View view1 ;
				if(iWidth==1920 && iHeight ==1128)
				{
					view1= inflater1.inflate(R.layout.userdefinedtoastbig,(ViewGroup) findViewById(R.id.toast_layout));
				}
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
            case 8:    
            	strUpdateState="6";  
            	boolean bRet=isLocalData(strCurDate) ;
				if(bRet)
				{
					Toast.makeText(getApplicationContext(), "为保证数据安全性，请先提交数据，再进行发布！" , Toast.LENGTH_LONG).show();
        			return false;
				}
				else
				{					
            	   SubmitRemoteState(iPos,true);             	
				}
				break;
            case 10:   
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
    private static boolean isIpAddress(String value) { int start = 0; int end = value.indexOf('.'); int numBlocks = 0; while (start < value.length()) { if (end == -1) { end = value.length(); } try { int block = Integer.parseInt(value.substring(start, end)); if ((block > 255) || (block < 0)) { return false; } } catch (NumberFormatException e) { return false; } numBlocks++; start = end + 1; end = value.indexOf('.', start); } return numBlocks == 4; }
    private static String checkIp(String ip){
		String validStr = ".0123456789";
		StringBuffer sb = new StringBuffer();
		int i = 0;

		for( i = 0; i < ip.length(); i++){
			if( validStr.indexOf(ip.charAt(i)) >= 0 ) sb.append(ip.charAt(i));
		}
		if( sb.toString().length() == 0 ) return "";

		String newIP = "";
		String[] arrIp = sb.toString().split("\\.");
		for( i = 0; i < arrIp.length && i < 4 ; i++){
			if( arrIp.equals("")) break;
			if( i > 0 && i < 4 ) newIP += ".";
			if( Integer.parseInt(arrIp[i]) > 255 ){
				newIP += String.format("%d", Integer.parseInt(arrIp[i])/10);
			}else{
				newIP += arrIp[i];
			}
		}
		if( i < 4 && sb.toString().endsWith("-")) newIP += ".";
		return newIP;
	}
    private void IPDialog() 
    {    	
        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入数据服务器IP").setIcon(R.drawable.user).setView(inputServer);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {                    	
                    	String strCurIP = inputServer.getText().toString();   
                    	if(isIpAddress(strCurIP))
                    	{
                    		LoginActivity.strServerIP=strCurIP;
                    		SharedPreferences sp = getSharedPreferences("MyUserInfo", 0);
                    		// 写入配置文件
                    		Editor spEd = sp.edit();    		
                    		spEd.putString("ServerIP",strCurIP);                     		
                    		spEd.commit();		
                    		Toast.makeText(ListTableView.this,"设置成功",Toast.LENGTH_SHORT).show();
                    		
                    	}
                    	else
                    	{
                    		IPDialog();
                    		Toast.makeText(ListTableView.this,"输入的IP地址格式不正确,请重新设置",Toast.LENGTH_SHORT).show();
                    	}
                    }
                });
        builder.show();
       
    }
    private void UpdateTimeSet() 
    {    	
        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入更新时间（分）").setIcon(R.drawable.user).setView(inputServer);
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
                    		Toast.makeText(ListTableView.this,"设置成功",Toast.LENGTH_SHORT).show();
                    		
                    	}
                    	
                    }
                });
        builder.show();
       
    }
    private void QualDialog(final int iPos,final String strCurRecv) 
    {    	
        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入确认人姓名").setIcon(R.drawable.user).setView(inputServer);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() 
        {
                    public void onClick(DialogInterface dialog, int which) 
                    {                    	
                    	strCurQualUser = inputServer.getText().toString();
                    	
                    	UpdateRemoteState(iPos);                                     
                    }
                });
        builder.show();
       
    }
    private void CancelDialog(final int iPos,final String strCurRecv) 
    {    	
        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入不合格的原因").setIcon(R.drawable.user).setView(inputServer);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                    	
                    	strCurFailReason = inputServer.getText().toString();                    	
                    	QualDialog(iPos, strCurRecv) ;
                       
                    }
                });
        builder.show();
       
    }
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
	Handler mExecHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				case 1001:
					String  ExecType = msg.getData().getString("type");	
					boolean ret = msg.getData().getBoolean("result");
					String  strSql=msg.getData().getString("sql");
					int     iPos=msg.getData().getInt("pos");
					if(ExecType.equals("Delete"))
					{
						dbHelper.DeleteProblem(strCurDate);
						DeletePhoto(strCurImagePath);
						
						//Toast.makeText(getApplicationContext(), "删除数据时连接服务器数据库失败，将暂存至本地数据库，网通后自动上传数据！" , Toast.LENGTH_LONG).show();
						
					}
					else if(ExecType.equals("Update"))
					{	
						if(!ret)
						{
							dbHelper.InsertProblem(strCurSysID,edtTrainNum.getText().toString(), strCurDate, strCurrUser, strCurRecv, strCurLabier, edtContent.getText().toString(),strUpdateState,strCurImagePath,strCurRecvTime,strCurEndTime,strCurCheckTime,strCurCancelTime,strCurRefreshTime,strCurCheckFailTime,strCurAttach,strCurTrainType,strCurCancelReason,strCurFailReason,strCurQualUser,strCurModifyType);
							Toast.makeText(getApplicationContext(), "修改数据时连接服务器数据库失败，将数据存储在本地，请在网通时提交数据！" , Toast.LENGTH_LONG).show();
						}
						adapter.UpdateData(iUpdateRowNum,edtTrainNum.getText().toString(),strCurRecv,edtContent.getText().toString(),strUpdateState,strCurRefreshTime);
			            listView.setAdapter(adapter);
			            layout.setVisibility(View.VISIBLE); 			
						btnSubmit.setText("提交");
				        edtContent.setText("");
						//
						/*Intent intent = new Intent();
				        intent.setAction("action.refreshUploadFriend");
				        sendBroadcast(intent);*/
					}
					else if(ExecType.equals("Submit"))
					{
						if(!ret)
						{							
							Toast.makeText(getApplicationContext(), "发布任务时连接服务器数据库失败，将数据存储在本地，请在网通时提交数据！" , Toast.LENGTH_LONG).show();
							//dbHelper.DeleteProblem(strCurDate);
							dbHelper.InsertProblem(strCurSysID,strCurTrainNum, strCurDate, strCurrUser, strCurRecvUnit, "", strCurContent,"0","",null,null,null,null,null,null,strCurAttach,strCurTrainType,"","","",strCurModifyType);
							
						}
						 adapter.UpdateSubmitState(iPos, "0");
				         listView.setAdapter(adapter);
				         layout.setVisibility(View.VISIBLE); 	
						/*Toast.makeText(getApplicationContext(), strCurRecv+"发布任务时连接服务器数据库失败，将数据存储在本地，请在网通时提交数据！" , Toast.LENGTH_LONG).show();
						dbHelper.DeleteProblem(strCurDate);
						dbHelper.InsertProblem(strCurTrainNum, strCurDate, strCurrUser, strCurRecv, "", strCurContent,"0","",null,null,null,null,null,null,strCurAttach,strCurTrainType,"","","");*/
					}
					else if(ExecType.equals("State"))
					{
						if(!ret)
						{							
							Toast.makeText(getApplicationContext(), "确认任务时连接服务器数据库失败，将数据存储在本地，请在网通时提交数据！" , Toast.LENGTH_LONG).show();
							//dbHelper.DeleteProblem(strCurDate);
							dbHelper.InsertProblem(strCurSysID,strCurTrainNum, strCurDate, strCurrUser, strCurRecvUnit, strCurLabier, strCurContent,strUpdateState,strCurImagePath,strCurRecvTime,strCurEndTime,strCurCheckTime,strCurCancelTime,strCurRefreshTime,strCurCheckFailTime,strCurAttach,strCurTrainType,strCurCancelReason,strCurFailReason,strCurQualUser,strCurModifyType);
							
						}
						if(strUpdateState.equals("4"))    		
				    	{
				    	    adapter.remove(iPos);            	
				        	listView.setAdapter(adapter);
				            layout.setVisibility(View.VISIBLE); 
				    	}
				    	else
				    	{
				    		adapter.UpdateFailState(iPos, strUpdateState,strCurUpdateDate,strCurFailReason,strCurQualUser);
				        	listView.setAdapter(adapter);
				            layout.setVisibility(View.VISIBLE); 
				        }
				        
						
						/*Toast.makeText(getApplicationContext(), strCurRecv+"确认任务时连接服务器数据库失败，将数据存储在本地，请在网通时提交数据！" , Toast.LENGTH_LONG).show();
						dbHelper.DeleteProblem(strCurDate);
						dbHelper.InsertProblem(strCurTrainNum, strCurDate, strCurrUser, strCurRecv, strCurLabier, strCurContent,strUpdateState,strCurImagePath,strCurRecvTime,strCurEndTime,strCurCheckTime,strCurCancelTime,strCurRefreshTime,strCurCheckFailTime,strCurAttach,strCurTrainType,strCurCancelReason,strCurFailReason,strCurQualUser);
					   */
					}
					
					break;

				default:
					break;
			}
		}

		public  void DeletePhoto(String strCurImagePath) 
		{		
		    File file=new File(strCurImagePath);
			if (file.exists()) 
			{ // 判断文件是否存在
				if (file.isFile()) 
				{ // 判断是否是文件
					file.delete(); // delete()方法 你应该知道 是删除的意思;
				} 				
			}
			file.delete();			
		}	
	};
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
    private void ExecSQL(final String strSql,final String strType,final int iPos)	
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
				/**/
				Message msg = new Message();
				msg.what=1001;
				Bundle data = new Bundle();
				data.putString("type", strType);			
				data.putBoolean("result", ret);	
				data.putString("sql",strSql);				
				data.putInt("pos",iPos);
				msg.setData(data);
				mExecHandler.sendMessage(msg);
			}
		};
		new Thread(run).start();
		 
	}
  
    private void DeleteRemoteData(ArrayList<String> list,int iPos) 
    {
    	if(!LoginActivity.TestConnect())
    	{
    		//Toast.makeText(getApplicationContext(), "连接服务器数据库失败，请检查网络！" , Toast.LENGTH_LONG).show();
			//return;
    	}
    	String  strSql = String.format("delete from ProblemData  where SysID='%s'", list.get(0));
    	ExecSQL(strSql,"Delete",iPos);		
	}
    private void UpdateRemoteData(int iPos) 
    {
    	if(!LoginActivity.TestConnect())
    	{
    		//Toast.makeText(getApplicationContext(), "更新任务时连接服务器数据库失败，将数据存储在本地，请在网通时提交数据！" , Toast.LENGTH_LONG).show();
			//return;
    	}
    	String  strSql="";  
        strSql= String.format("update ProblemData set TrainNum='%s',Receiver='%s',Content='%s',State=%s ,RefreshDate='%s' where SysID='%s' ", strCurTrainNum,strCurRecv,strCurContent,strUpdateState,strCurRefreshTime,strCurSysID);
        ExecSQL(strSql,"Update",iPos);		
	}
    private void SubmitRemoteState(final int iPos,boolean bFlag) 
    {   
    	//SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");     
    	//strCurUpdateDate  =   sDateFormat.format(new   java.util.Date());
    	
    	/*if(!LoginActivity.TestConnect())
    	{
    		Toast.makeText(getApplicationContext(), "发布任务时连接服务器数据库失败，请检查网络连接！" , Toast.LENGTH_LONG).show();
			return;
    	}*/
    	String  strSql="";
    	if(bFlag)    		
    	{    		
    	    strSql = String.format("update ProblemData set State=0 where SysID='%s'", strCurSysID);
    	}
    	else
    	{
    		strSql = "update ProblemData set State=0 where state=6";
			//adapter.UpdateSubmitAllState();
    		/*if(LoginActivity.strCurType.equals("管理员"))
    		{
    			strSql = "update ProblemData set State=0 where state=6";
    			adapter.UpdateSubmitAllState();
            
    		}
    		/else
    		{
    			
    			
				
    			//adapter.UpdateSubmitAllState(strCurLoginUser);
    			//strSql = String.format("update ProblemData set State=0 where state=6 and Submitter='%s'",strCurLoginUser);
    			//adapter.UpdateSubmitAllState();
    		}*/
    		//listView.setAdapter(adapter);
            //layout.setVisibility(View.VISIBLE); 
			
    	}
    	ExecSQL(strSql,"Submit",iPos);	
    	   	
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
			adapter  = new MyAdapter(ListTableView.this, lists,iWidth,iHeight);  
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
 			adapter  = new MyAdapter(ListTableView.this, lists,iWidth,iHeight);  
 			listView.setAdapter(adapter);
 			layout.setVisibility(View.VISIBLE);  
 			listView.setSelection(adapter.getCount());	
  		}
  		
        
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
    private void UpdateRemoteState(final int iPos) 
    {
    	/*if(!LoginActivity.TestConnect())
    	{
    		Toast.makeText(getApplicationContext(), "确认任务时连接服务器数据库失败，请检查网络！" , Toast.LENGTH_LONG).show();
			return;
    	}*/
    	SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");     
    	strCurUpdateDate  =   sDateFormat.format(new   java.util.Date());   
    	String  strSql="";
    	if(strUpdateState.equals("4"))    		
    	{
    		strCurCheckTime=strCurUpdateDate;
    	    strSql = String.format("update ProblemData set State=%s,QualDate='%s',QualUser='%s' where SysID='%s'", strUpdateState,strCurUpdateDate,strCurQualUser,strCurSysID);
    		/*adapter.remove(iPos);            	
        	listView.setAdapter(adapter);
            layout.setVisibility(View.VISIBLE); */
    	}
    	else
    	{
    		/*adapter.UpdateFailState(iPos, strUpdateState,strCurUpdateDate,strCurFailReason,strCurQualUser);
        	listView.setAdapter(adapter);
            layout.setVisibility(View.VISIBLE); */
    		strCurCheckFailTime=strCurUpdateDate;
    		strSql = String.format("update ProblemData set State=%s,FailDate='%s',FailReason='%s',QualUser='%s'  where SysID='%s'", strUpdateState,strCurUpdateDate,strCurFailReason,strCurQualUser,strCurSysID);
    	}
    	
    	ExecSQL(strSql,"State",iPos);
    
    	    	
    } 
    public void SaveListData()
    {    
    	adapter  = new MyAdapter(ListTableView.this, lists,iWidth,iHeight);
    	strCurTrainNum=edtTrainNum.getText().toString();				
		strCurContent=edtContent.getText().toString();	
		strCurAttach=edtAttach.getText().toString();			
		strCurLabier="";			
		SharedPreferences sp = getSharedPreferences("MyUserInfo", 0);
		// 写入配置文件
		Editor spEd = sp.edit();    		
		spEd.putString("TrainNum",strCurTrainNum); 
		spEd.putString("Attach", strCurAttach);    
		LoginActivity.strCurAttach=strCurAttach;
		LoginActivity.strCurTrainNum=strCurTrainNum;
		spEd.commit();		
		if(strCurTrainNum.equals(""))
		{
			Toast.makeText(getApplicationContext(),"车号不能为空 ", Toast.LENGTH_SHORT).show();
			edtTrainNum.setFocusable(true);
			edtTrainNum.setFocusableInTouchMode(true);
			edtTrainNum.requestFocus();
			edtTrainNum.requestFocusFromTouch();	
			return;
		}
		if(strCurAttach.equals(""))
		{
			Toast.makeText(getApplicationContext(),"配属不能为空 ", Toast.LENGTH_SHORT).show();
			edtAttach.setFocusable(true);
			edtAttach.setFocusableInTouchMode(true);
			edtAttach.requestFocus();
			edtAttach.requestFocusFromTouch();	
			return;
		}
		
		if(strCurRecv==null)
		{
			Toast.makeText(getApplicationContext(),"接收单位不能为空 ", Toast.LENGTH_SHORT).show();
			return;
		}
		if(strCurContent.equals(""))
		{
			Toast.makeText(getApplicationContext(),"故障内容不能为空 ", Toast.LENGTH_SHORT).show();
			edtContent.setFocusable(true);
			edtContent.setFocusableInTouchMode(true);
			edtContent.requestFocus();
			edtContent.requestFocusFromTouch();	
			return;
		}
		
    	SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    	strCurDate   =   sDateFormat.format(new   java.util.Date()); 
    	
    	ArrayList<String> list = new ArrayList<String>();
    
    	list.add(""+adapter.getCount());
    	list.add(strCurTrainNum);
    	list.add(strCurDate); 
    	list.add(strCurLoginUser);
    	list.add(strCurRecv);
    	list.add("");
    	list.add(strCurContent);	
    	list.add("6");
    	list.add(null);
    	list.add(null);
    	list.add(null);
    	list.add(null);
    	list.add(null);
    	list.add(null);
    	list.add(strCurAttach);
    	list.add(LoginActivity.strCurTrainType);
    	list.add("");
    	list.add("");
    	list.add("");
    	list.add(LoginActivity.strCurModifyType);
		lists.add(list);		
		//String  strSql = String.format("insert  into ProblemData values('%s','%s','%s','%s',NULL,'%s',6,NULL,NULL,NULL,NULL,NULL,NULL,'%s','%s',NULL,NULL,NULL)", strCurTrainNum,strCurDate,strCurLoginUser,strCurRecv,strCurContent,strCurAttach,strCurTrainNum);
		//ExecSQL(strSql,"Insert");
		dbHelper.DeleteProblem(strCurDate);
		dbHelper.InsertProblem(""+adapter.getCount(),strCurTrainNum, strCurDate, LoginActivity.strCurUser, strCurRecv, "", strCurContent,"6","",null,null,null,null,null,null,strCurAttach,LoginActivity.strCurTrainType,"","","",LoginActivity.strCurModifyType);
		//Toast.makeText(getApplicationContext(), "保存数据时连接服务器数据库失败，将暂存至本地数据库，网通后自动上传数据！" , Toast.LENGTH_LONG).show();
		
		/*Intent intent = new Intent();
        intent.setAction("action.refreshUploadFriend");
        sendBroadcast(intent);*/
		// MyAdapter adapter  = new MyAdapter(ListTableView.this, lists);
		listView.setAdapter(adapter);
		layout.setVisibility(View.VISIBLE);	
		listView.setSelection(adapter.getCount());		
		
		//lxj		
		//lxjedtRecv.setHint(R.string.table_recv_group_hint);	
		edtContent.setText("");
		edtContent.setHint(R.string.table_content_hint);	
    }
    static Handler mUploadPhotoHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				case 1001:
					
					boolean ret = msg.getData().getBoolean("result");
					if(!ret)
					{					
						UpdatePhotoFlag=1;						
					}
					else
					{
						UpdatePhotoFlag=2;
					}
			}
		}
    };
     Handler mUploadImageHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				case 1001:
					
					boolean ret = msg.getData().getBoolean("result");
					if(!ret)
					{					
						dbHelper.DeleteProblem(strCurDate);
						dbHelper.InsertProblem("",strCurTrainNum, strCurDate, strCurrUser, strCurRecv, strCurLabier, strCurContent,strUpdateState,strCurImagePath,strCurRecvTime,strCurEndTime,strCurCheckTime,strCurCancelTime,strCurRefreshTime,strCurCheckFailTime,strCurAttach,strCurTrainType,strCurCancelReason,strCurFailReason,"",strCurModifyType);
						Toast.makeText(getApplicationContext(), "上传照片时连接服务器失败，将暂存至本地数据库，网通后自动上传数据！" , Toast.LENGTH_LONG).show();
						
					}
			}
		}
    };
    public  void readFileSendData(final String filePath,final String strFileName)
			 {
    	new Thread(new Runnable()
    	{			
			@Override
			public void run()
			{
				
				boolean bRet=UploadImage(filePath,strFileName);
				Message msg = new Message();
				msg.what=1001;
				Bundle data = new Bundle();			
				data.putBoolean("result", bRet);				
				msg.setData(data);
				mUploadImageHandler.sendMessage(msg);
			}
		}).start();
	}
    public static boolean UploadImage(String filePath, String strFileName)
    {
    	try 
		{
			Log.i(TAG, "readFileSendData filePath=" + filePath);
			DataInputStream ddis = new DataInputStream(new FileInputStream(filePath));			
			Socket socket = new Socket(LoginActivity.strServerIP, 4000);
	        OutputStream outputSteam = socket.getOutputStream(); 				
			DataOutputStream dos = new DataOutputStream(outputSteam);	
			String str=strCurrUser+"|"+strFileName+"|"+ddis.available()+"|the end\r\n";		    	
			dos.write(str.getBytes("GBK"), 0, str.length());
			Log.i(TAG, "ddis =" + ddis);
			int length = 0;
			int totalNum = 0;
			byte[] buffer = new byte[1024];// 每次上传的大小
			Log.i(TAG, "img.avaliable=" + ddis.available());
	
			while ((length = ddis.read(buffer)) != -1)
			{
				totalNum += length;
				Log.i(TAG, "length=" + length);
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
			Log.i(TAG, "readFileSendData(): send bytes=" + totalNum);
			return true;
		}
		catch (Exception e) {                    
			Log.i(TAG, "UploadFileErr" );
			return false;
        }
    }
    public void DoCapture()    
    {
    	Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		File file = new File(photoPath);
		if (!file.exists()) { // 检查图片存放的文件夹是否存在
			file.mkdir(); // 不存在的话 创建文件夹
		}
		File photo = new File(imagePath);
		imageUri = Uri.fromFile(photo);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
		startActivityForResult(intent, PHOTO_CAPTURE);
    }
	/*class photo implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			DoCapture();
		}

	} */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		String sdStatus = Environment.getExternalStorageState();
		switch (requestCode) {
		case PHOTO_CAPTURE:
			  if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
				Log.i("内存卡错误", "请检查您的内存卡");
			  }
			  else 
			  {
				try 
				{
					BitmapFactory.Options op = new BitmapFactory.Options();
					// 设置图片的大小
					Bitmap bitMap = BitmapFactory.decodeFile(imagePath);
					int width = bitMap.getWidth();
					int height = bitMap.getHeight();
					// 设置想要的大小
					int newWidth = 680;
					int newHeight = 1240;
					// 计算缩放比例
					float scaleWidth = ((float) newWidth) / width;
					float scaleHeight = ((float) newHeight) / height;
					// 取得想要缩放的matrix参数
					Matrix matrix = new Matrix();
					matrix.postScale(scaleWidth, scaleHeight);
					// 得到新的图片
					bitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height,
							matrix, true);
					// canvas.drawBitmap(bitMap, 0, 0, paint)
					// 防止内存溢出
					op.inSampleSize = -10; // 这个数字越大,图片大小越小.
					Bitmap pic = null;
					pic = BitmapFactory.decodeFile(imagePath, op);
					//img_photo.setImageBitmap(pic); // 这个ImageView是拍照完成后显示图片
					FileOutputStream b = null;				
					b = new FileOutputStream(imagePath);				
					if (pic != null) 
					{
						pic.compress(Bitmap.CompressFormat.JPEG, 100, b);							
						dbHelper.UpdatePhotoPath(strCurDate, strCurImagePath);					
						//readFileSendData(imagePath,photoName);						
					}
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			break;
		default:
			return;
		}
		
	}	
	private class SpinnerAdapter extends ArrayAdapter<String> {
	    Context context;
	    String[] items = new String[] {};

	    public SpinnerAdapter(final Context context,
	            final int textViewResourceId, final String[] objects) {
	        super(context, textViewResourceId, objects);
	        this.items = objects;
	        this.context = context;
	    }

	    @Override
	    public View getDropDownView(int position, View convertView,
	            ViewGroup parent) {

	        if (convertView == null) {
	            LayoutInflater inflater = LayoutInflater.from(context);
	            convertView = inflater.inflate(
	                    android.R.layout.simple_spinner_item, parent, false);
	        }

	        TextView tv = (TextView) convertView
	                .findViewById(android.R.id.text1);
	        tv.setText(items[position]);
	        tv.setTextColor(Color.BLACK);
	        if(iWidth==1920 && iHeight ==1128)
	        	tv.setTextSize(25);
			else if(iWidth==1024 && iHeight ==720)
			    tv.setTextSize(20);
			else
				tv.setTextSize(15);  
	        return convertView;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        if (convertView == null) {
	            LayoutInflater inflater = LayoutInflater.from(context);
	            convertView = inflater.inflate(
	                    android.R.layout.simple_spinner_item, parent, false);
	        }

	        // android.R.id.text1 is default text view in resource of the android.
	        // android.R.layout.simple_spinner_item is default layout in resources of android.

	        TextView tv = (TextView) convertView
	                .findViewById(android.R.id.text1);
	        tv.setText(items[position]);
	        tv.setTextColor(Color.BLACK);
	        if(iWidth==1920 && iHeight ==1128)
	        	tv.setTextSize(25);
			else if(iWidth==1024 && iHeight ==720)
			    tv.setTextSize(20);
			else
				tv.setTextSize(15);  
	       
	        strCurRecv=items[position];
	        return convertView;
	    }
	}
	
    	
}  