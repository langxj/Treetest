package com.gao.tree;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class ModifyActivity extends Activity  implements OnClickListener, AbstractSpinerAdapter.IOnItemSelectListener{
	
 	public static EditText Pwd,Pwd1;
 	private static Button Login;
 	private static TextView Register;
	private DBHelper dbHelper;
	public static  int iUserCount=0;
	public static String strUName[]=new String[1024];
	private TextView mTView;
	private ImageButton mBtnDropDown;
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter;
	private String strCurUser;
	private boolean bAdminUser;
    private Intent newintent;
	private static final int ITEM = Menu.FIRST;
	private static final int ITEM1 = Menu.FIRST+1;
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
    	Intent intent=getIntent(); 		
    	bAdminUser=intent.getBooleanExtra("AdminUser",false);
		strCurUser=intent.getStringExtra("CurUser");
		Display display = getWindowManager().getDefaultDisplay();
        int iWidth=display.getWidth();
        int iHeight=display.getHeight();      
    	if(iWidth==1920 && iHeight ==1128)
    		setContentView(R.layout.modifypwdbig);
		else if(iWidth==1024 && iHeight ==720)
			setContentView(R.layout.modifypwdmid);
		else
			setContentView(R.layout.modifypwdsmall);       
        dbHelper=new DBHelper(this);      
        Pwd=(EditText) findViewById(R.id.password_edit);
        Pwd1=(EditText) findViewById(R.id.password_edit1);
        Login=(Button) findViewById(R.id.signin_button);         
        Login.setOnClickListener((OnClickListener) this);   
       setupViews(iWidth,iHeight);;
       
    }		
    
    public boolean onCreateOptionsMenu(Menu menu) {	
		
    	menu.add(0, ITEM, 0, "启动飞鸽").setIcon(R.drawable.feige);
		menu.add(0, ITEM1, 0, "退出系统").setIcon(R.drawable.exitsys);	
		return true;
	}
  
 // 通过点击了哪个菜单子项来改变Activity的标题
 	public boolean onOptionsItemSelected(MenuItem item) {

 		switch (item.getItemId()) {		
 		case ITEM:
 			launchApp("com.netfeige");
 			break;
 		case ITEM1:
 			AlertDialog.Builder builder = new Builder(ModifyActivity.this); 
       		 builder.setIcon(android.R.drawable.ic_dialog_info);
       	        builder.setMessage("确定要退出?"); 
       	        builder.setTitle("提示"); 
       	        builder.setPositiveButton("确认", 
       	                new android.content.DialogInterface.OnClickListener() { 
       	                    public void onClick(DialogInterface dialog, int which) { 
       	                        dialog.dismiss(); 
       	                     ModifyActivity.this.finish(); 
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

 		}
 		return true;
 	}	
	public  void setupViews(int iWidth,int iHeight)
	{  				
	    mTView = (TextView) findViewById(R.id.tv_value);
	    mTView.setOnClickListener(this);
		mBtnDropDown = (ImageButton) findViewById(R.id.bt_dropdown);
		mBtnDropDown.setOnClickListener(this);
		if(bAdminUser)
		{
			for(int i = 0; i < LoginActivity.iUserCount; i++){
				CustemObject object = new CustemObject();
				object.data = LoginActivity.strUName[i];
				nameList.add(object);
			}			
		}
		else
		{
			CustemObject object = new CustemObject();
			object.data = strCurUser;
			nameList.add(object);
		}
			
		mAdapter = new CustemSpinerAdapter(this,iWidth,iHeight);
		mAdapter.refreshData(nameList, 0);
		mSpinerPopWindow = new SpinerPopWindow(this);
		mSpinerPopWindow.setAdatper(mAdapter);
		mSpinerPopWindow.setItemListener(this);
		mTView.setText(strCurUser);
		Pwd.setFocusable(true);
		Pwd.setFocusableInTouchMode(true);
		Pwd.requestFocus();
		Pwd.requestFocusFromTouch();	
    }		
	private void setHero(int pos){
		if (pos >= 0 && pos <= nameList.size()){
			CustemObject value = nameList.get(pos);			
			mTView.setText(value.toString());
		}
	}	
	private SpinerPopWindow mSpinerPopWindow;
	private void showSpinWindow(){
		Log.e("", "showSpinWindow");
		mSpinerPopWindow.setWidth(mTView.getWidth());
		mSpinerPopWindow.showAsDropDown(mTView);
	}
	@Override
	public void onItemClick(int pos) 
	{
		setHero(pos);
		Pwd.setText("");
		
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
	
	private void ExecSQL(final String strSql)	
	{		
		Runnable run = new Runnable()
		{			
			@Override
			public void run()
			{
				if(LoginActivity.TestConnect())
				    DataBaseUtil.ExecSQL(strSql);					
				Message msg = new Message();
				msg.what=1001;
				mExecHandler.sendMessage(msg);
			}
		};
		new Thread(run).start();
		 
	}
	Handler mExecHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				case 1001:					
					dbHelper.ModifyPwd(mTView.getText().toString(),Pwd.getText().toString());
			        Toast.makeText(ModifyActivity.this, "修改成功！", Toast.LENGTH_LONG).show();
			        if(bAdminUser)
			        {
				        Pwd.setText("");
	    				Pwd1.setText("");
	    				Pwd.setFocusable(true);
	    				Pwd.setFocusableInTouchMode(true);
	    				Pwd.requestFocus();
	    				Pwd.requestFocusFromTouch();
			        }
			        else
					  finish();	        			
					break;
				default:
					break;
			}
		};
	};
	@Override
    	public void onClick(View v) {
		
    		// TODO Auto-generated method stub
    		switch(v.getId()){
    		case R.id.bt_dropdown:
    		case R.id.tv_value:
    			showSpinWindow();
    			break;
        	case R.id.signin_button:
        		if(mTView.getText().toString().equals("")){
    				Toast.makeText(ModifyActivity.this,"请选择用户 ", Toast.LENGTH_SHORT).show();
    				showSpinWindow();
    				return;
    			}
    			if(Pwd.getText().toString().equals("")){
    				Toast.makeText(ModifyActivity.this,"密码不能为空 ", Toast.LENGTH_SHORT).show();
    				Pwd.setFocusable(true);
    				Pwd.setFocusableInTouchMode(true);
    				Pwd.requestFocus();
    				Pwd.requestFocusFromTouch();	
    				return;
    			}    
    			if(Pwd1.getText().toString().equals("")){
    				Toast.makeText(ModifyActivity.this,"确认密码不能为空 ", Toast.LENGTH_SHORT).show();
    				Pwd1.setFocusable(true);
    				Pwd1.setFocusableInTouchMode(true);
    				Pwd1.requestFocus();
    				Pwd1.requestFocusFromTouch();	
    				return;
    			}   
    			if(!Pwd1.getText().toString().equals(Pwd1.getText().toString())){
    				Toast.makeText(ModifyActivity.this,"两次密码输入不一置，请重新输入 ", Toast.LENGTH_SHORT).show();
    				Pwd.setText("");
    				Pwd1.setText("");
    				Pwd.setFocusable(true);
    				Pwd.setFocusableInTouchMode(true);
    				Pwd.requestFocus();
    				Pwd.requestFocusFromTouch();	
    				return;
    			} 
    			String  strSql = String.format("update  UserInfo set pwd='%s' where name='%s'", Pwd.getText().toString(), mTView.getText().toString());
				ExecSQL(strSql);	
    			
    		}
    	}  
    	
    }
