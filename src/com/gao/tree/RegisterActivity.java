package com.gao.tree;

import java.util.ArrayList;
import java.util.List;
import com.geniuseoe.spiner.demo.widget.AbstractSpinerAdapter;
import com.geniuseoe.spiner.demo.widget.CustemObject;
import com.geniuseoe.spiner.demo.widget.CustemSpinerAdapter;
import com.geniuseoe.spiner.demo.widget.SpinerPopWindow;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener, AbstractSpinerAdapter.IOnItemSelectListener{
	private TextView mTView,mTView1;
	private ImageButton mBtnDropDown,mBtnDropDown1;
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private List<CustemObject> nameList1 = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter,mAdapter1;
	private EditText mynum,mypwd;
	private Button btnAdd;
	private DBHelper dbHelper;	
	private String   strCurDeparment;
	private int      iDepartCount=0,iUserCount=0;
	private String[] DeparmentList=new String[1000];
	private boolean  bFirstSpinner=true;
	private static final int ITEM = Menu.FIRST;
	private static final int ITEM1 = Menu.FIRST+1;
	private Intent newintent;
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 View bv = this.findViewById(android.R.id.title);
	        ((TextView) bv).setTextColor(Color.BLACK);
	        ((TextView) bv).setTextSize(15);
	        ((View) bv.getParent()).setBackgroundResource(R.drawable.background_login);
		Intent intent=getIntent(); 
		iDepartCount=intent.getIntExtra("DepartCount", 0);
		iUserCount=intent.getIntExtra("UserCount", 0);
        DeparmentList=intent.getStringArrayExtra("Department");
        //Toast.makeText(RegisterActivity.this,DeparmentList[0]+DeparmentList[1], Toast.LENGTH_SHORT).show();
        Display display = getWindowManager().getDefaultDisplay();
        int iWidth=display.getWidth();
        int iHeight=display.getHeight();      
    	if(iWidth==1920 && iHeight ==1128)
    		setContentView(R.layout.registerbig);
		else if(iWidth==1024 && iHeight ==720)
			setContentView(R.layout.registermid);
		else
			setContentView(R.layout.registersmall);  
		mynum=(EditText) findViewById(R.id.username_edit);
		mypwd=(EditText) findViewById(R.id.password_edit);
		btnAdd=(Button) findViewById(R.id.sigreg_button);
		dbHelper=new DBHelper(this);	
		btnAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mTView.getText().toString().equals("")){
					Toast.makeText(RegisterActivity.this,"所属部门不能为空，请选择 ", Toast.LENGTH_SHORT).show();
					showSpinWindow();
					return;
				}
				if(mTView1.getText().toString().equals("")){
					Toast.makeText(RegisterActivity.this,"所属权限不能为空，请选择 ", Toast.LENGTH_SHORT).show();
					showSpinWindow1();
					return;
				}
				if(mynum.getText().toString().equals("")){
					Toast.makeText(RegisterActivity.this,"用户名不能为空，请输入 ", Toast.LENGTH_SHORT).show();
					mynum.setFocusable(true);
					mynum.setFocusableInTouchMode(true);
					mynum.requestFocus();
					mynum.requestFocusFromTouch();	
					return;
				}
				if(mypwd.getText().toString().equals("")){
					Toast.makeText(RegisterActivity.this,"密码不能为空，请输入 ", Toast.LENGTH_SHORT).show();
					mypwd.setFocusable(true);
					mypwd.setFocusableInTouchMode(true);
					mypwd.requestFocus();
					mypwd.requestFocusFromTouch();	
					return;
				}
				String  strSql = String.format("insert  into UserInfo values('%s','%s','%s','%s')", mynum.getText().toString(), mypwd.getText().toString(),mTView.getText().toString(),mTView1.getText().toString());
				ExecSQL(strSql);				
				//Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
				//startActivity(intent);
			}
		});
	     setupViews(iWidth,iHeight);
	     setupViews1(iWidth,iHeight);
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
	 			AlertDialog.Builder builder = new Builder(RegisterActivity.this); 
	       		 builder.setIcon(android.R.drawable.ic_dialog_info);
	       	        builder.setMessage("确定要退出?"); 
	       	        builder.setTitle("提示"); 
	       	        builder.setPositiveButton("确认", 
	       	                new android.content.DialogInterface.OnClickListener() { 
	       	                    public void onClick(DialogInterface dialog, int which) { 
	       	                        dialog.dismiss(); 
	       	                     RegisterActivity.this.finish(); 
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
				boolean SaveFlag=false;
				if(LoginActivity.TestConnect())
				   SaveFlag= DataBaseUtil.ExecSQL(strSql);				
				Message msg = new Message();
				msg.what=1001;
				Bundle data = new Bundle();		
				data.putBoolean("result", SaveFlag);				
				msg.setData(data);
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
					boolean ret = msg.getData().getBoolean("result");					
					dbHelper.DeleteUser(mynum.getText().toString(), mypwd.getText().toString(),mTView.getText().toString());
					dbHelper.InsertUser(mynum.getText().toString(), mypwd.getText().toString(),mTView.getText().toString(),mTView1.getText().toString());
					LoginActivity.strUName[iUserCount]=mynum.getText().toString();
					LoginActivity.strUDepart[iUserCount]=mTView.getText().toString();
					LoginActivity.strUPwd[iUserCount]=mypwd.getText().toString();
					LoginActivity.strUType[iUserCount]=mTView1.getText().toString();
					LoginActivity.iUserCount++;
					// 广播通知
			        Intent intent = new Intent();
			        intent.setAction("action.refreshFriend");
			        sendBroadcast(intent);
			        Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_LONG).show();
					finish();
	        			
					break;

				default:
					break;
			}
		};
	};
	    private void setupViews(int iWidth,int iHeight)
	    {  				
	    	mTView = (TextView) findViewById(R.id.tv_value);
	    	 mTView.setOnClickListener(this);
			mBtnDropDown = (ImageButton) findViewById(R.id.bt_dropdown);
			mBtnDropDown.setOnClickListener(this);	
			for(int i = 0; i <iDepartCount; i++){
				CustemObject object = new CustemObject();
				object.data = DeparmentList[i];
				nameList.add(object);
			}		
			
			mAdapter = new CustemSpinerAdapter(this,iWidth,iHeight);
			mAdapter.refreshData(nameList, 0);			
			mSpinerPopWindow = new SpinerPopWindow(this);
			mSpinerPopWindow.setAdatper(mAdapter);
			mSpinerPopWindow.setItemListener(this);
		
	    }
	    private void  setupViews1(int iWidth,int iHeight)
	    {  	    	
	    	mTView1 = (TextView) findViewById(R.id.tv_value1);
			mBtnDropDown1 = (ImageButton) findViewById(R.id.bt_dropdown1);
			mTView1.setOnClickListener(this);
			mBtnDropDown1.setOnClickListener(this);					
			String[] names1 = getResources().getStringArray(R.array.autho_list);
			for(int i = 0; i <4; i++){
				CustemObject object = new CustemObject();
				object.data = names1[i];
				nameList1.add(object);
			}					
			mAdapter1 = new CustemSpinerAdapter(this,iWidth,iHeight);			
			mAdapter1.refreshData(nameList1, 0);		
			mSpinerPopWindow1 = new SpinerPopWindow(this);		
			mSpinerPopWindow1.setAdatper(mAdapter1);		
			mSpinerPopWindow1.setItemListener(this);
	    }
		@Override
		public void onClick(View view) {
			switch(view.getId()){
			case R.id.bt_dropdown:
			case R.id.tv_value:
				bFirstSpinner=true;
				showSpinWindow();
				break;
			case R.id.bt_dropdown1:
			case R.id.tv_value1:
				bFirstSpinner=false;
				showSpinWindow1();
				break;
			}
		}
		private void setHero(int pos){
			if (pos >= 0 && pos <= nameList.size()){
				CustemObject value = nameList.get(pos);			
				mTView.setText(value.toString());
			}
		}	
		private void setHero1(int pos){
			if (pos >= 0 && pos <= nameList1.size()){
				CustemObject value = nameList1.get(pos);			
				mTView1.setText(value.toString());
			}
		}	
		private SpinerPopWindow mSpinerPopWindow,mSpinerPopWindow1;
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
		@Override
		public void onItemClick(int pos) 
		{
			if(bFirstSpinner)
				setHero(pos);
			else
				setHero1(pos);
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
	
}