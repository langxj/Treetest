package com.gao.tree;

import java.util.ArrayList;
import java.util.List;
import com.geniuseoe.spiner.demo.widget.AbstractSpinerAdapter;
import com.geniuseoe.spiner.demo.widget.CustemObject;
import com.geniuseoe.spiner.demo.widget.CustemSpinerAdapter;
import com.geniuseoe.spiner.demo.widget.SpinerPopWindow;

import android.app.Activity;
import android.app.ActivityManager;
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

public class DictionaryActivity extends Activity implements OnClickListener, AbstractSpinerAdapter.IOnItemSelectListener{
	private TextView mTView;
	private ImageButton mBtnDropDown,mBtnDropDown1;
	private List<CustemObject> nameList = new ArrayList<CustemObject>();
	private List<CustemObject> nameList1 = new ArrayList<CustemObject>();
	private AbstractSpinerAdapter mAdapter,mAdapter1;
	private EditText edtTrainType,edtTrainPart,edtTrainLoc,edtTrainContent;
	private Button btnAdd;
	private DBHelper dbHelper;	
	private String   strCurDeparment;
	private int      iDepartCount=0,iUserCount=0;
	private String[] DeparmentList=new String[1000];
	private boolean  bFirstSpinner=true;
	private static final int ITEM = Menu.FIRST;
	private static final int ITEM1 = Menu.FIRST + 1;
	private static final int ITEM2 = Menu.FIRST + 2;
	private static final int ITEM3 = Menu.FIRST + 3;
	private static final int ITEM4 = Menu.FIRST + 4;
	private Intent newintent;
	private String strCurUser,strCurTrainType,strCurTrainPart,strCurTrainLoc,strCurContent,strCurOperator,strCurType;
	private String strDepartment[];
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 View bv = this.findViewById(android.R.id.title);
	        ((TextView) bv).setTextColor(Color.BLACK);
	        ((TextView) bv).setTextSize(15);
	        ((View) bv.getParent()).setBackgroundResource(R.drawable.background_login);
	    dbHelper=new DBHelper(this);	
		Intent intent=getIntent(); 
		strCurTrainType=intent.getStringExtra("TrainTrype");
		strCurTrainPart=intent.getStringExtra("TrainPart");
		strCurTrainLoc=intent.getStringExtra("TrainLoc");
		strCurContent=intent.getStringExtra("Content");
		strCurOperator=intent.getStringExtra("Opertator");
		strCurUser=intent.getStringExtra("CurUser");
		strCurType=intent.getStringExtra("Type");
		if(strCurType.equals("Delete"))
		{
			dbHelper.DeleteDictionary(strCurTrainType, strCurTrainPart,strCurTrainLoc,strCurContent,strCurOperator);
		    String strSql=String.format("delete from DataDictionary where FirstItem='%s' and SecondItem='%s' and ThirdItem='%s' and FourthItem='%s'", strCurTrainType, strCurTrainPart,strCurTrainLoc,strCurContent);
		    ExecSQL(strSql,"Delete");	
		}
		strDepartment=intent.getStringArrayExtra("DepartmentArr");
        //Toast.makeText(RegisterActivity.this,DeparmentList[0]+DeparmentList[1], Toast.LENGTH_SHORT).show();
		Display display = getWindowManager().getDefaultDisplay();
        int iWidth=display.getWidth();
        int iHeight=display.getHeight();      
    	if(iWidth==1920 && iHeight ==1128)
    		setContentView(R.layout.dictionarybig);
		else if(iWidth==1024 && iHeight ==720)
			setContentView(R.layout.dictionarymid);
		else
			setContentView(R.layout.dictionarysmall);  
		edtTrainType=(EditText) findViewById(R.id.EditTrainType);
		edtTrainPart=(EditText) findViewById(R.id.EditTrainPart);
		edtTrainLoc=(EditText) findViewById(R.id.username_edit);
		edtTrainContent=(EditText) findViewById(R.id.password_edit);
		edtTrainType.setText(strCurTrainType);
		edtTrainPart.setText(strCurTrainPart);
		edtTrainLoc.setText(strCurTrainLoc);
		edtTrainContent.setText(strCurContent);		
		btnAdd=(Button) findViewById(R.id.sigreg_button);
		if(strCurType.equals("Add"))
			btnAdd.setText("增加");
		else
			btnAdd.setText("修改");
		
		
		btnAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mTView.getText().toString().equals("")){
					Toast.makeText(DictionaryActivity.this,"接收单位不能为空，请选择 ", Toast.LENGTH_SHORT).show();
					showSpinWindow();
					return;
				}
				if(edtTrainType.getText().toString().equals("")){
					Toast.makeText(DictionaryActivity.this,"所属车型不能为空，请输入 ", Toast.LENGTH_SHORT).show();
					edtTrainType.setFocusable(true);
					edtTrainType.setFocusableInTouchMode(true);
					edtTrainType.requestFocus();
					edtTrainType.requestFocusFromTouch();	
					return;
				}
				if(edtTrainPart.getText().toString().equals("")){
					Toast.makeText(DictionaryActivity.this,"所属部件不能为空，请输入 ", Toast.LENGTH_SHORT).show();
					edtTrainPart.setFocusable(true);
					edtTrainPart.setFocusableInTouchMode(true);
					edtTrainPart.requestFocus();
					edtTrainPart.requestFocusFromTouch();	
					return;
				}
				
				if(edtTrainLoc.getText().toString().equals("")){
					Toast.makeText(DictionaryActivity.this,"所属位置不能为空，请输入 ", Toast.LENGTH_SHORT).show();
					edtTrainLoc.setFocusable(true);
					edtTrainLoc.setFocusableInTouchMode(true);
					edtTrainLoc.requestFocus();
					edtTrainLoc.requestFocusFromTouch();	
					return;
				}
				if(edtTrainContent.getText().toString().equals("")){
					Toast.makeText(DictionaryActivity.this,"故障内容不能为空，请输入 ", Toast.LENGTH_SHORT).show();
					edtTrainContent.setFocusable(true);
					edtTrainContent.setFocusableInTouchMode(true);
					edtTrainContent.requestFocus();
					edtTrainContent.requestFocusFromTouch();	
					return;
				}
				String  strSql ="",strType=""; 
				if(strCurType.equals("Add"))
				{
					strSql = String.format("insert  into DataDictionary values('%s','%s','%s','%s','%s')", edtTrainType.getText().toString(), edtTrainPart.getText().toString(),edtTrainLoc.getText().toString(),edtTrainContent.getText().toString(),mTView.getText().toString());
					strType="Add";
				}
				else
				{					
					strSql = String.format("update DataDictionary set FirstItem='%s',SecondItem='%s',ThirdItem='%s',FourthItem='%s',OperateUnit='%s' where FirstItem='%s' and SecondItem='%s' and ThirdItem='%s' and FourthItem='%s'", edtTrainType.getText().toString(), edtTrainPart.getText().toString(),edtTrainLoc.getText().toString(),edtTrainContent.getText().toString(),mTView.getText().toString(),edtTrainType.getText().toString(), edtTrainPart.getText().toString(),edtTrainLoc.getText().toString(),edtTrainContent.getText().toString());
					strType="Update";
				}
				
				ExecSQL(strSql,strType);				
				
			}
		});
		setupViews(iWidth,iHeight);
	  
	    }

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    // cancel timer
		Intent intent = new Intent();
	    intent.setAction("action.refreshDictFriend");
	    sendBroadcast(intent);
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
				Intent intent=new Intent(DictionaryActivity.this,RegisterActivity.class);
	    		Bundle bundle=new Bundle();          	
	    		bundle.putInt("DepartCount", iDepartCount);
	            bundle.putInt("UserCount", iUserCount);
	            bundle.putStringArray("Department", strDepartment);
	            intent.putExtras(bundle);
				startActivity(intent);		
				break;
			case ITEM1:
				Intent intent1=new Intent(DictionaryActivity.this,ModifyActivity.class);
	    		Bundle bundle1=new Bundle();          	
	    		bundle1.putBoolean("AdminUser", true);
	    		bundle1.putString("CurUser", strCurUser);          
	            intent1.putExtras(bundle1);
				startActivity(intent1);		
				break;
			case ITEM2:
				launchApp("com.netfeige");
			    break;
			case ITEM3:
				AlertDialog.Builder builder = new Builder(DictionaryActivity.this); 
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
				Intent intent4=new Intent(DictionaryActivity.this,UserListView.class);    		
				startActivity(intent4);		
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
	private void ExecSQL(final String strSql,final String strType)	
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
				data.putString("Type", strType);	
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
					boolean bRet=msg.getData().getBoolean("return");
					String strType=msg.getData().getString("Type");
					
					if(strType.equals("Update"))
					{
						if(bRet)
							dbHelper.UpdateDictionary(edtTrainType.getText().toString(), edtTrainPart.getText().toString(),edtTrainLoc.getText().toString(),edtTrainContent.getText().toString(),mTView.getText().toString(),"TRUE",strCurTrainType,strCurTrainPart,strCurTrainLoc,strCurContent);	      
						else
							dbHelper.UpdateDictionary(edtTrainType.getText().toString(), edtTrainPart.getText().toString(),edtTrainLoc.getText().toString(),edtTrainContent.getText().toString(),mTView.getText().toString(),"FALSE",strCurTrainType,strCurTrainPart,strCurTrainLoc,strCurContent);	      
						Toast.makeText(DictionaryActivity.this, "修改成功！", Toast.LENGTH_LONG).show();
						finish();
					}
					else if(strType.equals("Add"))
					{
						dbHelper.DeleteDictionary(edtTrainType.getText().toString(), edtTrainPart.getText().toString(),edtTrainLoc.getText().toString(),edtTrainContent.getText().toString(),mTView.getText().toString());
						if(bRet)
							dbHelper.InsertDictionary(edtTrainType.getText().toString(), edtTrainPart.getText().toString(),edtTrainLoc.getText().toString(),edtTrainContent.getText().toString(),mTView.getText().toString(),"TRUE");
						else
							dbHelper.InsertDictionary(edtTrainType.getText().toString(), edtTrainPart.getText().toString(),edtTrainLoc.getText().toString(),edtTrainContent.getText().toString(),mTView.getText().toString(),"FALSE");
				        Toast.makeText(DictionaryActivity.this, "添加成功,若继续添加请输入，否则按返回键返回！", Toast.LENGTH_LONG).show();
						//finish();
				        edtTrainType.setText("");
						edtTrainPart.setText("");
						edtTrainLoc.setText("");
						edtTrainContent.setText("");
					}
					else
					{
						Toast.makeText(DictionaryActivity.this, "删除成功！", Toast.LENGTH_LONG).show();
						finish();
					}
				
	        			
					break;

				default:
					break;
			}
		};
	};
	    private void setupViews(int iWidth,int iHeight)
	    {  				
	    	mTView = (TextView) findViewById(R.id.tv_value);
			mBtnDropDown = (ImageButton) findViewById(R.id.bt_dropdown);
			mBtnDropDown.setOnClickListener(this);	
			for(int i = 0; i <strDepartment.length; i++){
				CustemObject object = new CustemObject();
				object.data = strDepartment[i];
				nameList.add(object);
			}		
			
			mAdapter = new CustemSpinerAdapter(this,iWidth,iHeight);
			mAdapter.refreshData(nameList, 0);			
			mSpinerPopWindow = new SpinerPopWindow(this);
			mSpinerPopWindow.setAdatper(mAdapter);
			mSpinerPopWindow.setItemListener(this);
			mTView.setText(strCurOperator);
		
	    }
	   
		@Override
		public void onClick(View view) {
			
				showSpinWindow();
		
			
		}
		private void setHero(int pos){
			if (pos >= 0 && pos <= nameList.size()){
				CustemObject value = nameList.get(pos);			
				mTView.setText(value.toString());
			}
		}	
		
		private SpinerPopWindow mSpinerPopWindow,mSpinerPopWindow1;
		private void showSpinWindow(){
			Log.e("", "showSpinWindow");
			mSpinerPopWindow.setWidth(mTView.getWidth());
			mSpinerPopWindow.showAsDropDown(mTView);
		}
		
		@Override
		public void onItemClick(int pos) 
		{
			
			setHero(pos);
			
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