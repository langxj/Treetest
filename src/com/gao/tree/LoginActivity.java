package com.gao.tree;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.geniuseoe.spiner.demo.widget.AbstractSpinerAdapter;
import com.geniuseoe.spiner.demo.widget.CustemObject;
import com.geniuseoe.spiner.demo.widget.CustemSpinerAdapter;
import com.geniuseoe.spiner.demo.widget.SpinerPopWindow;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class LoginActivity extends Activity  implements OnClickListener, AbstractSpinerAdapter.IOnItemSelectListener{
	private static final String PREFS_NAME = "MyUserInfo";
 	public static EditText Pwd;
 	private static Button Login,btnView;
 	//private static TextView Register;
	private DBHelper dbHelper;
	public  int bReadRemoteData=0;
	public  int iRowCount=0;
	public static int iDepartCount=0;
	public static  int iUserCount=0,iTrainTypeCount=0;
	public  String strFirstVal[]=new String[10000],strSecondVal[]=new String[10000],strThirdVal[]=new String[10000],strFouthVal[]=new String[10000],strOperateUnit[]=new String[10000];
    public static  String strDepartment[]=new String[30];
	public static  String strUName[]=new String[500],strTrainType[]=new String[300];
	public static  String strUPwd[]=new String[500];
	public static  String strUDepart[]=new String[500];
	public static  String strUType[]=new String[500];
	public static  String strCurTrainNum;
	public static  String strCurAttach,strCurUser,strServerIP,strCurTrainType,strCurModifyType;
	public static  int    iUpdateTime;
	public static  boolean        bConnectFlag=true;
	private TextView mTView,mTView1,mTView2;
	private ImageButton mBtnDropDown,mBtnDropDown1,mBtnDropDown2;
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private List<CustemObject> nameList1 = new ArrayList<CustemObject>();
	private List<CustemObject> nameList2 = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter,mAdapter1,mAdapter2;
	public static String dbName="localdata.db";//数据库的名字
    private static String DATABASE_PATH="/data/data/com.gao.tree/databases/";//数据库在手机里的路径
    private Timer mTimer,mTimer1,mTimer2;
    public  static String strCurType,strCurDepartment,strQueryCurType;
    public  static int iCount=0;
    private Intent newintent;
	private static final int ITEM = Menu.FIRST;
	private static final int ITEM1 = Menu.FIRST+1;
	private static final int ITEM2 = Menu.FIRST+2;
	public  static int LocalDataCount;
	private int iWidth,iHeight;
	private final int AIRPLAY_MESSAGE_HIDE_TOAST=10;
	private Toast mToast=null;  
	private int  bFirstSpinner=0;
	 
    
	//private final String DATABASE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/localdata";//路径
	//private final String DATABASE_FILENAME = "localdata.db";//数据库名
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏 
    	//openDatabase();
        super.onCreate(savedInstanceState);
        View bv = this.findViewById(android.R.id.title);
        ((TextView) bv).setTextColor(Color.BLACK);
        ((TextView) bv).setTextSize(15);
        ((View) bv.getParent()).setBackgroundResource(R.drawable.background_login);
        
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);		   
		strServerIP=sp.getString("ServerIP", "172.18.201.223");   
		iUpdateTime=sp.getInt("UpdateTime", 60); 	
		TestConnect();
		//Toast.makeText(LoginActivity.this,"ip"+strServerIP, Toast.LENGTH_LONG).show(); 
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
        Display display = getWindowManager().getDefaultDisplay();
        iWidth=display.getWidth();
        iHeight=display.getHeight();      
    	if(iWidth==1920 && iHeight ==1128)
    		setContentView(R.layout.loginbig);
		else if(iWidth==1024 && iHeight ==720)
			setContentView(R.layout.loginmid);
		else
			setContentView(R.layout.loginsmall);           
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.refreshFriend");        
        registerReceiver(mRefreshBroadcastReceiver, intentFilter);
     
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("action.refreshDictFriend");        
        registerReceiver(mRefreshDictBroadcastReceiver, intentFilter1);
        
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("action.refreshUploadFriend");        
        registerReceiver(mRefreshUploadBroadcastReceiver, intentFilter2);  
        //Num=(EditText) findViewById(R.id.username_edit);
        dbHelper=new DBHelper(this);
        boolean dbExist = checkDataBase();
        if(dbExist)
        {  
        }
        else
        {//不存在就把raw里的数据库写入手机
        	try
        	{
        		copyDataBase();
        	}
        	catch(IOException e)
        	{
        		Toast.makeText(LoginActivity.this,"系统异常，没有任何操作权限", Toast.LENGTH_SHORT).show();
        		return;
        		//throw new Error("Error copying database");
        	}
        }
        
        Pwd=(EditText) findViewById(R.id.password_edit);
        Login=(Button) findViewById(R.id.signin_button);   
        btnView=(Button) findViewById(R.id.view_record); 
        //Register=(TextView) findViewById(R.id.register_link);    
        Login.setOnClickListener((OnClickListener) this);
        btnView.setOnClickListener((OnClickListener) this);
        //Register.setOnClickListener((OnClickListener) this); 
        if(!bConnectFlag)
        {
        	 if(mToast == null)
        	 {  
        		 mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT) ;  
             }  
        	 mToast.setText("连接服务器数据库失败，请检查网络连接或本地IP设置！当前IP为:"+strServerIP) ;  
        	 mToast.show() ;  
        	
        }
			
        else
        {
        	if(mToast == null)
       	    {  
       		    mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT) ;  
            }  
       	    mToast.setText("正在初始化，请稍候…");  
       	    mToast.show() ; 
        	
        }
        //mTimer = new Timer();	
        //mTimer1=new Timer(); 
        //mTimer2=new Timer(); 
        //UpLocalDictionaryToRemote(false);        
        //GetLocalDataCount();               
       // LoadTreeData();
        LoadTrainType(); 
        GetLocalData();		     
	    LoadUser();        
	    LoadDepatment();   
	  
        //mToast.setText("正在更新数据字典，请稍候…");  
   	    //mToast.show() ; 
		//Toast.makeText(LoginActivity.this,"正在更新数据字典，请稍候…", Toast.LENGTH_LONG).show();       
      
     }		  
     public void isIpReachable() 
		{      
		    try    
		    {          
		    	 
		        InetAddress addr = InetAddress.getByName(strServerIP);           
		        if (addr.isReachable(1000))           
		        {         
		        	bConnectFlag=true;     
		            return ;      
		        }
		        bConnectFlag=false;   
		         
		    }      
		    catch (UnknownHostException e)       
		    {              
		    }      
		    catch (IOException e)    
		    {              
		    }
		    bConnectFlag=false;  
		}
	public static boolean TestConnect() {	
		 
		try    
	    {          
	        InetAddress addr = InetAddress.getByName(strServerIP);           
	        if (addr.isReachable(500))           
	        {         
	        	bConnectFlag=true;     
	            return true;      
	        }
	        bConnectFlag=false;  
	        return false;
	         
	    }      
	    catch (UnknownHostException e)       
	    {              
	    }      
	    catch (IOException e)    
	    {              
	    }
	    bConnectFlag=false;  
	    return false;
			//p = Runtime.getRuntime().exec("/system/bin/ping -c "+ 1433 + " " + strServerIP);       
		/*Runnable run = new Runnable()
		{			
			@Override
			public void run()
			{	
				
				 try    
				    {          
				        InetAddress addr = InetAddress.getByName(strServerIP);           
				        if (addr.isReachable(200))           
				        {         
				        	bConnectFlag=true;     
				            return ;      
				        }
				        bConnectFlag=false;   
				         
				    }      
				    catch (UnknownHostException e)       
				    {              
				    }      
				    catch (IOException e)    
				    {              
				    }
				    bConnectFlag=false;  			
			}
		};
		new Thread(run).start();	*/
	}

	

	private void UpdateUIState() {
		 
    	 //setTimerTask(); 
 	     //setTimerTask1();
 	    // setTimerTask2();/**/
    	 Toast.makeText(LoginActivity.this,"初始化完成，请登录", Toast.LENGTH_SHORT).show();
    	 //mToast.setText("初始化完成，请登录!");  
    	 //mToast.show() ; 
    	 Login.setEnabled(true);
    	 btnView.setEnabled(true);
    	 Pwd.setEnabled(true);  
		
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// menu.add("一个参数的add方法的菜单");
		// menu.add("一个参数的add方法的菜单2");
		 //menu.add(R.string.titileRes);
		menu.add(0, ITEM2, 0, "服务器设置").setIcon(R.drawable.setip);   
    	menu.add(0, ITEM, 0, "启动飞鸽").setIcon(R.drawable.feige);
		menu.add(0, ITEM1, 0, "退出系统").setIcon(R.drawable.exitsys);
	
		//menu.add(0, ITEM, 0, "下载").setIcon(R.drawable.download);//设置图标
		//menu.add(0, ITME2, 0, "上传").setIcon(R.drawable.upload);

		return true;
	}
  
   
    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.refreshFriend"))
            {
            	nameList.clear();
            	GetLocalUser();
            	setupViews(iWidth,iHeight);
            }
        }
    };
    private BroadcastReceiver mRefreshDictBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.refreshDictFriend"))
            {        	
          	    GetLocalData();
            	Intent intent1=new Intent(LoginActivity.this,TreeView.class);
    			Bundle bundle=new Bundle(); 
                bundle.putInt("RowCount", iRowCount); 
                bundle.putInt("DepartCount", iDepartCount); 
                bundle.putInt("UserCount", iUserCount); 
                bundle.putString("CurrUser", strCurUser);
                bundle.putStringArray("Department", strDepartment);
               
                bundle.putStringArray("Firstarr", strFirstVal);
                bundle.putStringArray("Secondarr", strSecondVal);
                bundle.putStringArray("Thirdarr", strThirdVal);
                bundle.putStringArray("Foutharr", strFouthVal);	
                bundle.putStringArray("OperatorUnit", strOperateUnit);	    	            
                intent1.putExtras(bundle);                
    			startActivity(intent1);    			
    			/*mTimer1.cancel();
            	mTimer1=new Timer();          	  
          	    setTimerTask1();   */    	
            }
        }

		
    };
    private BroadcastReceiver mRefreshUploadBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
           /* String action = intent.getAction();
            if (action.equals("action.refreshUploadFriend"))
            {            	
            	mTimer.cancel();
            	mTimer=new Timer();          	  
          	    setTimerTask(); 
            	
            }*/
        }
    };
     
	// 通过点击了哪个菜单子项来改变Activity的标题
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {		
		case ITEM:
			launchApp("com.netfeige");
			break;
		case ITEM1:
			AlertDialog.Builder builder = new Builder(LoginActivity.this); 
      		 builder.setIcon(android.R.drawable.ic_dialog_info);
      	        builder.setMessage("确定要退出?"); 
      	        builder.setTitle("提示"); 
      	        builder.setPositiveButton("确认", 
      	                new android.content.DialogInterface.OnClickListener() { 
      	                    public void onClick(DialogInterface dialog, int which) { 
      	                        dialog.dismiss(); 
      	                     LoginActivity.this.finish(); 
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
		case ITEM2:
			IPDialog();	
			break;
			

		}
		return true;
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
                    		
                    		Toast.makeText(LoginActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
                    		
                    	}
                    	else
                    	{
                    		IPDialog();
                    		Toast.makeText(LoginActivity.this,"输入的IP地址格式不正确,请重新设置",Toast.LENGTH_SHORT).show();
                    	}
                    }
                });
        builder.show();
       
    }
	 private static boolean isIpAddress(String value) { int start = 0; int end = value.indexOf('.'); int numBlocks = 0; while (start < value.length()) { if (end == -1) { end = value.length(); } try { int block = Integer.parseInt(value.substring(start, end)); if ((block > 255) || (block < 0)) { return false; } } catch (NumberFormatException e) { return false; } numBlocks++; start = end + 1; end = value.indexOf('.', start); } return numBlocks == 4; }
	    
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    // cancel timer
	    unregisterReceiver(mRefreshBroadcastReceiver);  	    
	    unregisterReceiver(mRefreshDictBroadcastReceiver); 
	    unregisterReceiver(mRefreshUploadBroadcastReceiver); 
	    //mTimer.cancel();
	    //mTimer1.cancel();
	    //mTimer2.cancel();
	}
	private void setTimerTask() {
	    mTimer.schedule(new TimerTask() {
	        @Override
	        public void run() {     	    			
	            Message message = new Message();
	            message.what = 1;
	            doActionHandler.sendMessage(message);
	        }
	    }, 1000, 300*1000/* 表示5分钟之後，每隔5秒執行一次 */);
	}
	private void setTimerTask1() {
	    mTimer1.schedule(new TimerTask() {
	        @Override
	        public void run() {     	    			
	            Message message = new Message();
	            message.what = 1;
	            doActionHandler1.sendMessage(message);
	        }
	    }, 1000, LoginActivity.iUpdateTime*60*1000/* 表示5秒之後，每隔5秒執行一次 */);
	}
	private void setTimerTask2() {
	    mTimer2.schedule(new TimerTask() {
	        @Override
	        public void run() {     	    			
	            Message message = new Message();
	            message.what = 1;
	            doActionHandler2.sendMessage(message);
	        }
	    }, 1000, 1000/* 表示1秒之後，每隔10秒检查一次网络连接 */);
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
	            	UpLocalDataToRemote();
	                break;
	            default:
	                break;
	        }
	    }		
	};
	private Handler doActionHandler1 = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        int msgId = msg.what;
	        switch (msgId) {
	            case 1:	            	
	            	UpLocalDictionaryToRemote(true);
	                break;
	            default:
	                break;
	        }
	    }		
	};
	private Handler doActionHandler2 = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        int msgId = msg.what;
	        switch (msgId) {
	            case 1:	  	            	
	            	TestConnect();	        	
	                break;
	            default:
	                break;
	        }
	    }		
	};
	public  void setupViews(int iWidth,int iHeight)
	{  				
	    mTView = (TextView) findViewById(R.id.tv_value);
	    mTView.setOnClickListener(this);	
		mBtnDropDown = (ImageButton) findViewById(R.id.bt_dropdown);
		mBtnDropDown.setOnClickListener(this);				
		for(int i = 0; i <iUserCount; i++){
			CustemObject object = new CustemObject();
			object.data = strUName[i];
			nameList.add(object);
		}			
		mAdapter = new CustemSpinerAdapter(this,iWidth,iHeight);
		mAdapter.refreshData(nameList, 0);
		mSpinerPopWindow = new SpinerPopWindow(this);
		mSpinerPopWindow.setAdatper(mAdapter);
		mSpinerPopWindow.setItemListener(this);
		LoadUserDate();	
    }	
	public  void setupViews1(int iWidth,int iHeight)
	{  				
	    mTView1 = (TextView) findViewById(R.id.tv_value1);
	    mTView1.setOnClickListener(this);	
		mBtnDropDown1 = (ImageButton) findViewById(R.id.bt_dropdown1);
		mBtnDropDown1.setOnClickListener(this);				
		for(int i = 0; i <iTrainTypeCount; i++){
			CustemObject object = new CustemObject();
			object.data = strTrainType[i];
			nameList1.add(object);
		}			
		mAdapter1 = new CustemSpinerAdapter(this,iWidth,iHeight);
		mAdapter1.refreshData(nameList1, 0);
		mSpinerPopWindow1 = new SpinerPopWindow(this);
		mSpinerPopWindow1.setAdatper(mAdapter1);
		mSpinerPopWindow1.setItemListener(this);
		
    }	
	public  void setupViews2(int iWidth,int iHeight)
	{  				
	    mTView2 = (TextView) findViewById(R.id.tv_value2);
	    mTView2.setOnClickListener(this);	
		mBtnDropDown2 = (ImageButton) findViewById(R.id.bt_dropdown2);
		mBtnDropDown2.setOnClickListener(this);	
		String[] names2 = getResources().getStringArray(R.array.modifytype_list);
		for(int i = 0; i <4; i++){
			CustemObject object = new CustemObject();
			object.data = names2[i];
			nameList2.add(object);
		}			
			
		mAdapter2 = new CustemSpinerAdapter(this,iWidth,iHeight);
		mAdapter2.refreshData(nameList2, 0);
		mSpinerPopWindow2 = new SpinerPopWindow(this);
		mSpinerPopWindow2.setAdatper(mAdapter2);
		mSpinerPopWindow2.setItemListener(this);
		
    }	
	private void setHero(int pos){
		if (pos >= 0 && pos <= nameList.size()){
			CustemObject value = nameList.get(pos);			
			mTView.setText(value.toString());
			if(query(value.toString()))
			{
				if(strQueryCurType.equals("接收员")||strQueryCurType.equals("生产员"))    	   			
    			{
					Login.setVisibility(View.GONE) ;					  
    			}
    			else
    			{
    				Login.setVisibility(View.VISIBLE) ;	 
    				  
    			}    				
			}
			else
			{
				Login.setVisibility(View.VISIBLE) ;
				btnView.setVisibility(View.VISIBLE) ;	  
				
			}   	
		}
	}	
	private void setHero1(int pos){
		if (pos >= 0 && pos <= nameList1.size()){
			CustemObject value = nameList1.get(pos);			
			mTView1.setText(value.toString());
			
		}
	}	
	private void setHero2(int pos){
		if (pos >= 0 && pos <= nameList2.size()){
			CustemObject value = nameList2.get(pos);			
			mTView2.setText(value.toString());
			
		}
	}	
	private SpinerPopWindow mSpinerPopWindow,mSpinerPopWindow1,mSpinerPopWindow2;
	private void showSpinWindow(){
		Log.e("", "showSpinWindow");
		mSpinerPopWindow.setWidth(mTView.getWidth());
		mSpinerPopWindow.showAsDropDown(mTView);
	}
	private void showSpinWindow1(){
		Log.e("", "showSpinWindow1");
		mSpinerPopWindow1.setWidth(mTView1.getWidth());
		mSpinerPopWindow1.showAsDropDown(mTView1);
	}
	private void showSpinWindow2(){
		Log.e("", "showSpinWindow2");
		mSpinerPopWindow2.setWidth(mTView2.getWidth());
		mSpinerPopWindow2.showAsDropDown(mTView2);
	}
	
	@Override
	public void onItemClick(int pos) 
	{
		if(bFirstSpinner==2)
		{
			setHero2(pos);
			
		}
		if(bFirstSpinner==1)
		{
			setHero1(pos);
			
		}
		else if(bFirstSpinner==0)
		{
			setHero(pos);
			Pwd.setText("");
			
		}
		
		
		
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
	protected void UpLocalUserToRemote() 
	{
		if(!bConnectFlag)
			return;
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
  		String sql="select * from userinfo";
  		Cursor cursor=sdb.rawQuery(sql,null);	
  		iUserCount=0;
  		while (cursor.moveToNext()) 
        {
  			strUName[iUserCount]= cursor.getString(cursor.getColumnIndex("name"));
  			strUPwd[iUserCount] =cursor.getString(cursor.getColumnIndex("pwd"));
  			strUDepart[iUserCount] =cursor.getString(cursor.getColumnIndex("department"));
  			strUType[iUserCount]= cursor.getString(cursor.getColumnIndex("type"));  			
  			String  strSql = String.format("insert  into UserInfo values('%s','%s','%s','%s')", strUName[iUserCount], strUPwd[iUserCount],strUDepart[iUserCount],strUType[iUserCount]);
  			//Toast.makeText(LoginActivity.this,"开始 上传数据"+strSql, Toast.LENGTH_SHORT).show();
  			
  			 ExecUserSQL(strSql);
  			
  			iUserCount++;
		}         
        cursor.close();  
     
	}
	protected void UpLocalDataToRemote() 
	{
		if(!bConnectFlag)
			return;
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
  		String sql="select * from ProblemData";
  		Cursor cursor=sdb.rawQuery(sql,null);		
  		String strTrainNum="",strDate="",strUser="", strRecv="", strLabier="",strContent="",strState="",strPath="",strAttch="",strTrainType="",strRecvTime="",strEndTime="",strCheckTime="",strCheckFailTime="",strRefreshTime="",strCancelTime="",strCancelReason="",strFailReason="",strQualUser="";		
  		boolean bRet=false;
  		while (cursor.moveToNext()) 
        {
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
			strCancelReason=cursor.getString(cursor.getColumnIndex("FailReason"));			
			strFailReason=cursor.getString(cursor.getColumnIndex("FailReason"));
			strQualUser=cursor.getString(cursor.getColumnIndex("QualUser"));
			if(strState==null)
				strState="6";
			int iState= Integer.parseInt(strState);
  			String  strSql = String.format("insert  into ProblemData values('%s','%s','%s','%s','%s','%s',%d,%s,%s,%s,%s,%s,%s,'%s','%s','%s','%s','%s')", strTrainNum,strDate,strUser, strRecv, strLabier,strContent,iState,strRecvTime,strEndTime,strCheckTime,strCancelTime,strRefreshTime,strCheckFailTime,strAttch,strTrainType,strCancelReason,strFailReason,strQualUser);
  			//Toast.makeText(LoginActivity.this,"开始 上传数据"+strSql, Toast.LENGTH_SHORT).show();
  			ExecSQL(strSql,strDate);
  			if(!strPath.equals(""))
  			{
	  			String strFileName=strDate;  		
	  			strFileName=strFileName.replaceAll("-", "");
	  			strFileName=strFileName.replaceAll(":", "");
	  			strFileName=strFileName.replaceAll(" ", "");        
	  			strFileName=strFileName+".jpg";
	  			readFileSendData(strUser,strPath,strFileName);	  			
  			}
  			 bRet=true;
		}
        cursor.close();  
        if(!bRet)
        {
        	mTimer.cancel();
        }
     
	}
	protected void UpLocalDictionaryToRemote(boolean bFlag) 
	{
		if(!bConnectFlag)
			return;
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
  		String sql="select * from DataDictionary where IsRemoteData='FALSE'";
  		Cursor cursor=sdb.rawQuery(sql,null);		
  		String strFirst="",strSecond="",strThird="", strFourth="", strOperator="";	
  		boolean bRet=false;  	  
  		while (cursor.moveToNext()) 
        { 
  			strFirst= cursor.getString(cursor.getColumnIndex("FirstItem"));
  			strSecond =cursor.getString(cursor.getColumnIndex("SecondItem"));
  			strThird =cursor.getString(cursor.getColumnIndex("ThirdItem"));
  			strFourth =cursor.getString(cursor.getColumnIndex("FourthItem"));
  			strOperator = cursor.getString(cursor.getColumnIndex("OperateUnit"));  			
  			String  strSql = String.format("insert  into DataDictionary values('%s','%s','%s','%s','%s')", strFirst, strSecond,strThird,strFourth,strOperator);
  			ExecUserSQL(strSql);  
  			bRet=true;
		}
  		cursor.close();  
  		if(!bRet&&bFlag)
  		{
  			mTimer1.cancel();
  		}
            
	}
	public static void readFileSendData(final String strUser,final String strFilePath,final String strFileName)
	 {
		new Thread(new Runnable()
		{			
			@Override
			public void run()
			{
				UploadImage(strUser,strFilePath,strFileName);
				
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
	private void ExecUserSQL(final String strSql)	
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
	private void ExecSQL(final String strSql,final String strCurSubmitDate)	
	{		
		Runnable run = new Runnable()
		{			
			@Override
			public void run()
			{
				boolean SaveFlag=false; 
				if(bConnectFlag)
				{
					SaveFlag=DataBaseUtil.ExecSQL(strSql);
				}						
				if(SaveFlag)
				{
					dbHelper.DeleteProblem(strCurSubmitDate);
					/*Looper.prepare();
					Toast.makeText(getApplicationContext(), "保存成功" , Toast.LENGTH_LONG).show();
					Looper.loop();*/
				}
				else
				{
					/*Looper.prepare();
					Toast.makeText(getApplicationContext(), "保存失败" , Toast.LENGTH_LONG).show();
				    Looper.loop();*/
				}
				/**/;//
			}
		};
		new Thread(run).start();
		 
	}
	@Override
    	public void onClick(View v) {
    		// TODO Auto-generated method stub
    		switch(v.getId()){
    		case R.id.bt_dropdown:
    		case R.id.tv_value:
    			bFirstSpinner=0;
    			showSpinWindow();
    			break;
    		case R.id.bt_dropdown1:
    		case R.id.tv_value1:
    			bFirstSpinner=1;
    			showSpinWindow1();
    			break;
    		case R.id.bt_dropdown2:
    		case R.id.tv_value2:
    			bFirstSpinner=2;
    			showSpinWindow2();
    			break;
    	
    		case R.id.view_record:
    			if(mTView2.getText().toString().equals("")){
    				Toast.makeText(LoginActivity.this,"回修票类型不能为空，请选择 ", Toast.LENGTH_SHORT).show();
    				showSpinWindow2();
    			
    				return;
    			}
    			if(mTView1.getText().toString().equals("")){
    				Toast.makeText(LoginActivity.this,"车型不能为空，请选择 ", Toast.LENGTH_SHORT).show();
    				showSpinWindow1();
    			
    				return;
    			}
    			if(mTView.getText().toString().equals("")){
    				Toast.makeText(LoginActivity.this,"用户名不能为空 ", Toast.LENGTH_SHORT).show();
    				showSpinWindow();
    			
    				return;
    			}
    			if(Pwd.getText().toString().equals("")){
    				Toast.makeText(LoginActivity.this,"密码不能为空 ", Toast.LENGTH_SHORT).show();
    				Pwd.setFocusable(true);
    				Pwd.setFocusableInTouchMode(true);
    				Pwd.requestFocus();
    				Pwd.requestFocusFromTouch();	
    				return;
    			}    			
    			SaveUserDate();
    			boolean bRet=login(mTView.getText().toString(),Pwd.getText().toString());
    			if(bRet)
    			{	
    				if(strCurType.equals("管理员")||strCurType.equals("检查员"))
    				{
    					strCurModifyType=mTView2.getText().toString();
    					strCurTrainType=mTView1.getText().toString();
    	    			strCurUser=mTView.getText().toString();
				    	Pwd.setText("");
	    				Intent ViewRecord = new Intent();  
		    			ViewRecord.setClass(LoginActivity.this, ListTableView.class); 	        
		    	        Bundle blView=new Bundle(); 
		    	        blView.putInt("DepartCount", iDepartCount); 
		    	        blView.putInt("UserCount", iUserCount); 
		    	        blView.putStringArray("Department", strDepartment);
		    	        blView.putString("CurrUser",  mTView.getText().toString());
		    	        blView.putString("Receive", "");           
		    	        blView.putString("content","");
		    	        blView.putString("TrainType",strCurTrainType);
		    	        blView.putString("ModiType",strCurModifyType);
		    	        blView.putBoolean("Flag",true);
		                ViewRecord.putExtras(blView);
		                startActivity(ViewRecord);   
    				}
    				else
    				{
    					Pwd.setText("");
	      				Intent intent = new Intent();  
	      		        intent.setClass(LoginActivity.this, ListTableView1.class); 	        
	      		        Bundle bundle=new Bundle(); 
	      		        bundle.putString("CurrUser", mTView.getText().toString());
	    	            bundle.putString("Receive", strCurDepartment);
	      	            intent.putExtras(bundle);
	      		        startActivity(intent);  
    				}
    			}
    			else
    			{
    				Toast.makeText(LoginActivity.this, "用户名或密码有误！", Toast.LENGTH_LONG).show();
    				return;
    			}        		
    			break;
        	case R.id.signin_button:
        		if(mTView2.getText().toString().equals("")){
    				Toast.makeText(LoginActivity.this,"回修票类型不能为空，请选择 ", Toast.LENGTH_SHORT).show();
    				showSpinWindow2();
    			
    				return;
    			}
        		if(mTView1.getText().toString().equals("")){
    				Toast.makeText(LoginActivity.this,"车型不能为空，请选择 ", Toast.LENGTH_SHORT).show();
    				showSpinWindow1();
    			
    				return;
    			}
        		if(mTView.getText().toString().equals("")){
    				Toast.makeText(LoginActivity.this,"用户名不能为空,请选择 ", Toast.LENGTH_SHORT).show();
    				showSpinWindow();
    			
    				return;
    			}
    			if(Pwd.getText().toString().equals("")){
    				Toast.makeText(LoginActivity.this,"密码不能为空，请输入", Toast.LENGTH_SHORT).show();
    				Pwd.setFocusable(true);
    				Pwd.setFocusableInTouchMode(true);
    				Pwd.requestFocus();
    				Pwd.requestFocusFromTouch();	
    				return;
    			}
    		
    			
    			SaveUserDate();
    			boolean bool=login(mTView.getText().toString(),Pwd.getText().toString());
    			if(bool)
    			{	
    				if(strCurType.equals("管理员")||strCurType.equals("检查员"))
    				{
	    				//Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_LONG).show();    				
				    	
				    	if(iRowCount<=0)
				    	{
				    		Toast.makeText(LoginActivity.this, "系统异常，本地数据库不存在，请联系开发人员！", Toast.LENGTH_LONG).show();
				    		return;
				    	}	
				    	strCurUser=mTView.getText().toString();
				    	strCurTrainType=mTView1.getText().toString();
				    	strCurModifyType=mTView2.getText().toString();
				    	Pwd.setText("");
	    				Intent intent=new Intent(LoginActivity.this,TreeView.class);
	    				Bundle bundle=new Bundle(); 
	    	            bundle.putInt("RowCount", iRowCount); 
	    	            bundle.putInt("DepartCount", iDepartCount); 
	    	            bundle.putInt("UserCount", iUserCount); 
	    	            bundle.putString("CurrUser", mTView.getText().toString());
	    	            bundle.putStringArray("Department", strDepartment);
	    	          
	    	            bundle.putStringArray("Firstarr", strFirstVal);
	    	            bundle.putStringArray("Secondarr", strSecondVal);
	    	            bundle.putStringArray("Thirdarr", strThirdVal);
	    	            bundle.putStringArray("Foutharr", strFouthVal);	
	    	            bundle.putStringArray("OperatorUnit", strOperateUnit);	    	            
	    	            intent.putExtras(bundle);        	
	      				startActivity(intent);
    				}
    				else
    				{   	
    					Pwd.setText("");
	      				Intent intent = new Intent();  
	      		        intent.setClass(LoginActivity.this, ListTableView1.class); 	        
	      		        Bundle bundle=new Bundle(); 
	      		        bundle.putString("CurrUser", mTView.getText().toString());
	    	            bundle.putString("Receive", strCurDepartment);
	      	            intent.putExtras(bundle);
	      		        startActivity(intent);  
    				}
    			}else{
    				Toast.makeText(LoginActivity.this, "用户名或密码有误！", Toast.LENGTH_LONG).show();
    				return;
    			}
        		break;    	
        	case R.id.register_link:
        		/*Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
        		Bundle bundle=new Bundle();          	
        		bundle.putInt("departcount", iDepartCount);
	            bundle.putInt("usercount", iUserCount);
	            bundle.putStringArray("Department", strDepartment);
	            intent.putExtras(bundle);
  				startActivity(intent);
  				break;*/
        	
    		}
    	}

		 //登录
	  	public boolean query(String username){
	  		
	  		//String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
			//SQLiteDatabase sdb=SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
	  		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
	  		String sql="select * from userinfo where name=?";
	  		Cursor cursor=sdb.rawQuery(sql, new String[]{username});		
	  		if(cursor.moveToFirst()==true){
	  			strQueryCurType=cursor.getString(cursor.getColumnIndex("type"));	  		
	  			cursor.close();
	  			return true;
	  		}
	  		return false;
	  	}
    	 //登录
      	public boolean login(String username,String password){
      		
      		//String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
    		//SQLiteDatabase sdb=SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
      		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
      		String sql="select * from userinfo where name=? and pwd=?";
      		Cursor cursor=sdb.rawQuery(sql, new String[]{username,password});		
      		if(cursor.moveToFirst()==true){
      			strCurType=cursor.getString(cursor.getColumnIndex("type"));
      			strCurDepartment=cursor.getString(cursor.getColumnIndex("department"));
      			cursor.close();
      			return true;
      		}
      		return false;
      	}
      	//退出
      	 public boolean onKeyDown(int keyCode, KeyEvent event)  {  
             if (keyCode == KeyEvent.KEYCODE_BACK )   {  
            	 AlertDialog.Builder builder = new Builder(LoginActivity.this); 
           		 builder.setIcon(android.R.drawable.ic_dialog_info);
           	        builder.setMessage("确定要退出?"); 
           	        builder.setTitle("提示"); 
           	        builder.setPositiveButton("确认", 
           	                new android.content.DialogInterface.OnClickListener() { 
           	                    public void onClick(DialogInterface dialog, int which) { 
           	                        dialog.dismiss(); 
           	                     LoginActivity.this.finish(); 
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
             return false;       
         }
     	private void SaveUserDate() {
    		// 载入配置文件
    		SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
    		// 写入配置文件
    		Editor spEd = sp.edit();    		
    		spEd.putString("name", mTView.getText().toString());    		
    		spEd.putString("traintype", mTView1.getText().toString());
    		spEd.putString("moditype", mTView2.getText().toString());
    		spEd.commit();
    	}
    	/**
    	 * 载入已记住的用户信息
    	 */
    	private void LoadUserDate() {    		
    		SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
    		//if (sp.getBoolean("isSave", false)) 
    		{
    			String username = sp.getString("name", "");	
    			String trainType=sp.getString("traintype", "");	
    			String modiType=sp.getString("moditype", "");	
    			strCurTrainNum = sp.getString("TrainNum", "");	
    			strCurAttach = sp.getString("Attach", "");    
    			mTView2.setText(modiType); 
    			mTView1.setText(trainType); 
    			mTView.setText(username);   
    			if(query(username))
    			{
    				if(strQueryCurType.equals("接收员")||strQueryCurType.equals("生产员"))    			
	    			{
    					Login.setVisibility(View.GONE) ;	    								 
	    			}
	    			else
	    			{
	    				Login.setVisibility(View.VISIBLE) ;	    				
	    			}    				
    			}   					
    		}  		
    	}   
    	Handler mUserHandler = new Handler(){
    		
    		public void handleMessage(android.os.Message msg) {
    			switch (msg.what)
    			{    			
    				case 1001:        									
    					boolean ret = msg.getData().getBoolean("result");
    					if(!ret)
    					{	
    						GetLocalUser();    						
    					} 
    					//Toast.makeText(LoginActivity.this,"用户数据更新完成，正在更新部门数据…", Toast.LENGTH_SHORT).show();
    					setupViews2(iWidth,iHeight);
    					setupViews(iWidth,iHeight); 			
    			}
    		}
    	};
    	Handler mTrainTypeHandler = new Handler(){
    		
    		public void handleMessage(android.os.Message msg) {
    			switch (msg.what)
    			{    			
    				case 1001:        									
    					boolean ret = msg.getData().getBoolean("result");
    					if(!ret)
    					{	
    						GetLocalTrainType();    						
    					} 
    					//Toast.makeText(LoginActivity.this,"用户数据更新完成，正在更新部门数据…", Toast.LENGTH_SHORT).show();
    					setupViews1(iWidth,iHeight); 			
    			}
    		}
    	};
    	Handler mTreeDataHandler = new Handler(){
    		
    		public void handleMessage(android.os.Message msg) {
    			switch (msg.what)
    			{    			
    				case 1001:        									
    					int ret = msg.getData().getInt("result");
    					if(ret==-1)
    					{	 
    						
    						//Toast.makeText(getApplicationContext(), "下载数据字典时连接服务器数据库失败，请检查网络连接或本地IP设置！将使用本地数据字典。" , Toast.LENGTH_LONG).show();
    						GetLocalData();   
    						 
    					}
    					else if(ret==0)
    						GetLocalData();
    					//UpLocalUserToRemote();        
    				    LoadUser();        
    				    LoadDepatment();   
    					
    							
    			}
    		}
    	};
    	private void LoadDepatment() 
    	{
    		Runnable run = new Runnable()
    		{			
    			@Override
    			public void run()
    			{   	
    				boolean bRet=false;
    				if(bConnectFlag)
    				    bRet=GetRemoteDepartment();  
    				
    				Message msg = new Message();
    				msg.what=1001;
    				Bundle data = new Bundle();    					
    				data.putBoolean("result", bRet);				
    				msg.setData(data);
    				mDepartmentHandler.sendMessage(msg);
    			}    			
    		};
    		new Thread(run).start();    		 
    	}
    	Handler mDepartmentHandler = new Handler(){
    		public void handleMessage(android.os.Message msg) {
    			switch (msg.what)
    			{
    				case 1001:    					
    					boolean ret = msg.getData().getBoolean("result");
    					if(!ret)
    					{			
    						GetLocalDepartment();
    					} 	
    					//Toast.makeText(LoginActivity.this,"部门数据更新完成", Toast.LENGTH_SHORT).show();
    					UpdateUIState(); 
    			}
    		}
    	};
    	private void LoadUser() 
    	{
    		Runnable run = new Runnable()
    		{			
    			@Override
    			public void run()
    			{
    				boolean bRet=false;
    				if(bConnectFlag)
    				   bRet=GetRemoteUser();    					
    				Message msg = new Message();
    				msg.what=1001;
    				Bundle data = new Bundle();    					
    				data.putBoolean("result", bRet);				
    				msg.setData(data);
    				mUserHandler.sendMessage(msg);
    				
    			}
    			
    		};
    		new Thread(run).start();    		 
    	}
    	private void LoadTrainType() 
    	{
    		Runnable run = new Runnable()
    		{			
    			@Override
    			public void run()
    			{
    				boolean bRet=false;
    				if(bConnectFlag)
    				   bRet=GetRemoteTrainType();    					
    				Message msg = new Message();
    				msg.what=1001;
    				Bundle data = new Bundle();    					
    				data.putBoolean("result", bRet);				
    				msg.setData(data);
    				mTrainTypeHandler.sendMessage(msg);
    				
    			}
    			
    		};
    		new Thread(run).start();    		 
    	}
    	private void  LoadTreeData()
    	{
    		Runnable run = new Runnable()
    		{			
    			@Override
    			public void run()
    			{
    				bReadRemoteData=0;
    				int bRet=GetDataDictonaryCount();
    				if(bRet!=LocalDataCount&&bRet>0)
    				{
    					bReadRemoteData=GetRemoteData();
    				}
    				
    				if(!bConnectFlag)
    					bReadRemoteData=-1;
    				Message msg = new Message();
    				msg.what=1001;
    				Bundle data = new Bundle();    					
    				data.putInt("result", bReadRemoteData);				
    				msg.setData(data);
    				mTreeDataHandler.sendMessage(msg);
    			}
    			
    		};
    		new Thread(run).start();
    		 
    	}
    	
    	public  void GetLocalData()
    	{    	
    		//String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
    		//SQLiteDatabase sdb=SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
    		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
      		String sql="select *  from DataDictionary order by firstitem,seconditem,thirditem";
      		Cursor cursor=sdb.rawQuery(sql,null);
      		iRowCount=0;
      		while (cursor.moveToNext()) 
	        {
				strFirstVal[iRowCount]= cursor.getString(cursor.getColumnIndex("FirstItem"));
				strSecondVal[iRowCount] =cursor.getString(cursor.getColumnIndex("SecondItem"));
				strThirdVal[iRowCount] =cursor.getString(cursor.getColumnIndex("ThirdItem"));
				strFouthVal[iRowCount] =cursor.getString(cursor.getColumnIndex("FourthItem"));
				strOperateUnit[iRowCount] = cursor.getString(cursor.getColumnIndex("OperateUnit"));
	        	iRowCount++;
			}
	        cursor.close();   
	       
	        // Looper.prepare();
	         //String strdata=String.format("个数:%1$d", iRowCount);
		    //Toast.makeText(getApplicationContext(), strdata, Toast.LENGTH_LONG).show();
			//Looper.loop();
    	} 
    	public  void GetLocalDepartment()
    	{    	
    		//String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
    		//SQLiteDatabase sdb=SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
    		
    		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
      		String sql="select * from Department";
      		Cursor cursor=sdb.rawQuery(sql,null);	
      		iDepartCount=0;
      		while (cursor.moveToNext()) 
	        {
      			strDepartment[iDepartCount]= cursor.getString(cursor.getColumnIndex("name"));      			
      			iDepartCount++;
			}
	        cursor.close();   
	        // Looper.prepare();
	        //String strdata=String.format("个数:%1$d", iRowCount);
			//Toast.makeText(getApplicationContext(), strDepartment[0]+strDepartment[1]+strDepartment[2], Toast.LENGTH_LONG).show();
			//Looper.loop();
    	} 
    	public  boolean GetRemoteDepartment()
    	{ 
    		try
    		{    			
    			Connection conn = DataBaseUtil.getSQLConnection();
    			if(conn==null)
    			{
    				/*Looper.prepare();
    				Toast.makeText(getApplicationContext(), "连接服务器数据库失败，请检查网络连接！将使用本地数据字典。" , Toast.LENGTH_LONG).show();
    				Looper.loop();*/
    				return false;
    			}
    			String sql = "select * from Department";
    			Statement stmt = conn.createStatement();
    			ResultSet rs = stmt.executeQuery(sql);	
    			dbHelper.ClearTable("Department");
    			iDepartCount=0;
    			while(rs.next())
    			{		    
    				
          			strDepartment[iDepartCount]= rs.getString("name");          			
					dbHelper.InsertDepartment(strDepartment[iDepartCount]);
					iDepartCount++;					
    			}
    			rs.close();
    			stmt.close();
    			conn.close();		
    			
    			return true;   			 
    		} 
    		catch (SQLException e)
    		{
    			e.printStackTrace();					
				Looper.prepare();
		    	Toast.makeText(getApplicationContext(), "读取部门数据集时连接服务器数据库异常!" + e.getMessage(), Toast.LENGTH_LONG).show();
		    	Looper.loop();
		    	return false;
    		}
    	} 
    	public  void GetLocalUser()
    	{    	
    		//String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
    		//SQLiteDatabase sdb=SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
    		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
      		String sql="select * from userinfo";
      		Cursor cursor=sdb.rawQuery(sql,null);	
      		iUserCount=0;
      		while (cursor.moveToNext()) 
	        {
      			strUName[iUserCount]= cursor.getString(cursor.getColumnIndex("name"));
      			strUPwd[iUserCount] =cursor.getString(cursor.getColumnIndex("pwd"));
      			strUDepart[iUserCount] =cursor.getString(cursor.getColumnIndex("department"));
      			strUType[iUserCount]= cursor.getString(cursor.getColumnIndex("type"));
      			iUserCount++;
			}      		
	        cursor.close();   
	        // Looper.prepare();
	        //String strdata=String.format("个数:%1$d", iRowCount);
			//Toast.makeText(getApplicationContext(), strUName[0]+strUName[0], Toast.LENGTH_LONG).show();
			//Looper.loop();
    	} 
    	public  boolean GetRemoteUser()
    	{ 
    		try
    		{	Connection conn = DataBaseUtil.getSQLConnection();    			
    			if(conn==null)
    			{
    				/*Looper.prepare();
    				Toast.makeText(getApplicationContext(), "连接服务器数据库失败，请检查网络连接！将使用本地数据字典。" , Toast.LENGTH_LONG).show();
    				Looper.loop();*/
    				return false;
    			}
    			String sql = "select * from userinfo order by department";
    			Statement stmt = conn.createStatement();
    			ResultSet rs = stmt.executeQuery(sql);	
    			dbHelper.ClearTable("userinfo");
    			iUserCount=0;
    			while(rs.next())
    			{		    
          			strUName[iUserCount]= rs.getString("name");
          			strUPwd[iUserCount] = rs.getString("pwd");
          			strUDepart[iUserCount]= rs.getString("department");
          			strUType[iUserCount]= rs.getString("type");
					dbHelper.InsertUser(strUName[iUserCount],strUPwd[iUserCount],strUDepart[iUserCount],strUType[iUserCount]);
					iUserCount++;					
    			}
    			rs.close();
    			stmt.close();
    			conn.close();
    			
    			
    			return true;   			 
    		} 
    		catch (SQLException e)
    		{
    			e.printStackTrace();					
				Looper.prepare();
		    	Toast.makeText(getApplicationContext(), "读取用户列表时连接服务器数据库异常!" + e.getMessage(), Toast.LENGTH_LONG).show();
		    	Looper.loop();
		    	return false;
    		}
    	}
    	public  void GetLocalTrainType()
    	{    	
    		//String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
    		//SQLiteDatabase sdb=SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
    		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
      		String sql="select FirstItem from DataDictionary group by FirstItem";
      		Cursor cursor=sdb.rawQuery(sql,null);	
      		iTrainTypeCount=0;
      		while (cursor.moveToNext()) 
	        {
      			strTrainType[iTrainTypeCount]= cursor.getString(cursor.getColumnIndex("FirstItem"));      			
      			iTrainTypeCount++;
			}      		
	        cursor.close();   
	        // Looper.prepare();
	        //String strdata=String.format("个数:%1$d", iRowCount);
			//Toast.makeText(getApplicationContext(), strUName[0]+strUName[0], Toast.LENGTH_LONG).show();
			//Looper.loop();
    	} 
    	public  boolean GetRemoteTrainType()
    	{ 
    		try
    		{	Connection conn = DataBaseUtil.getSQLConnection();    			
    			if(conn==null)
    			{
    				/*Looper.prepare();
    				Toast.makeText(getApplicationContext(), "连接服务器数据库失败，请检查网络连接！将使用本地数据字典。" , Toast.LENGTH_LONG).show();
    				Looper.loop();*/
    				return false;
    			}
    			String sql = "select FirstItem from DataDictionary group by FirstItem";
    			Statement stmt = conn.createStatement();
    			ResultSet rs = stmt.executeQuery(sql);    			
    			iTrainTypeCount=0;
    			while(rs.next())
    			{		    
    				strTrainType[iTrainTypeCount]= rs.getString("FirstItem");          						
					iTrainTypeCount++;					
    			}
    			rs.close();
    			stmt.close();
    			conn.close();    			
    			
    			return true;   			 
    		} 
    		catch (SQLException e)
    		{
    			e.printStackTrace();					
				Looper.prepare();
		    	Toast.makeText(getApplicationContext(), "读取车型列表时连接服务器数据库异常!" + e.getMessage(), Toast.LENGTH_LONG).show();
		    	Looper.loop();
		    	return false;
    		}
    	} 
    	public  int GetRemoteData()
    	{    		
    		String result = "字段1  -  字段2\n";
    		try
    		{
    			if(!bConnectFlag)
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
    	public  int GetDataDictonaryCount()
    	{    		
    		
    		try
    		{
    			if(!bConnectFlag)
    				return -1;
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
    	/**
	     * 判断数据库是否存在
	     * @return false or true
	     */
	    public boolean checkDataBase()
	    {
	    	SQLiteDatabase checkDB = null;
	    	try{
	    		String databaseFilename = DATABASE_PATH+dbName;
	    		checkDB =SQLiteDatabase.openDatabase(databaseFilename, null,
	    				SQLiteDatabase.OPEN_READONLY);
	    	}catch(SQLiteException e){
	    		
	    	}
	    	if(checkDB!=null){
	    		checkDB.close();
	    	}
	    	return checkDB !=null?true:false;
	    }
	    
	    public void deleteDataBase()
	    {
	    	File f = new File(DATABASE_PATH, dbName);
    		if(f.exists());
    			f.delete();  
	    }
	    public boolean IsFirst()
	    {
	    	SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE); 
	    	boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true); 
	    	Editor editor = sharedPreferences.edit(); 
	    	if (isFirstRun) 
	    	{ 	    	
		    	editor.putBoolean("isFirstRun", false); 
		    	editor.commit(); 
		    	return true;
	    	} 
	    	else 
	    	{ 
	    		return false;
	    	} 
	    	
	    }
	    /**
	     * 复制数据库到手机指定文件夹下
	     * @throws IOException
	     */
	    public void copyDataBase() throws IOException{
	    	String databaseFilenames =DATABASE_PATH+dbName;
	    	File dir = new File(DATABASE_PATH);
	    	if(!dir.exists())//判断文件夹是否存在，不存在就新建一个
	    		dir.mkdir();
	    	FileOutputStream os = null;
	    	try{
	    		os = new FileOutputStream(databaseFilenames);//得到数据库文件的写入流
	    	}catch(FileNotFoundException e){
	    		e.printStackTrace();
	    	}
	    	InputStream is = LoginActivity.this.getResources().openRawResource(R.raw.localdata);//得到数据库文件的数据流
	        byte[] buffer = new byte[8192];
	        int count = 0;
	        try{
	        	
	        	while((count=is.read(buffer))>0){
	        		os.write(buffer, 0, count);
	        		os.flush();
	        	}
	        }catch(IOException e){
	        	
	        }
	        try{
	        	is.close();
	        	os.close();
	        }catch(IOException e){
	        	e.printStackTrace();
	        }
	    }
    }
