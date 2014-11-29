package com.gao.tree;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBHelper extends SQLiteOpenHelper{
	
	private final static int DB_VERSION=1;
	private final static String DB_NAME="localdata.db";
	private final static String TABLE__USER_NAME="userinfo";
	private final static String TABLE_DICTIONARY_NAME="datadictionary";
	private final static String TABLE_PROBLEM_NAME="problemdata";
	private final static String TABLE_DEPARTMENT_NAME="Department";
	//private  final static String ID="_id";
	  
	public DBHelper(Context context) {
		super(context,DB_NAME , null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//CreateUserInfo(db);
		//CreateDictionary(db);
		//CreateProblem(db);
			
	}
   
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql = "DROP TABLE IF EXISTS " + TABLE__USER_NAME;
		db.execSQL(sql);
		sql = "DROP TABLE IF EXISTS " + TABLE_DICTIONARY_NAME;
		db.execSQL(sql);
		sql = "DROP TABLE IF EXISTS " + TABLE_PROBLEM_NAME;
		db.execSQL(sql);
		sql = "DROP TABLE IF EXISTS " + TABLE_DEPARTMENT_NAME;
		db.execSQL(sql);
	
	}	
	public void InsertUser(String name,String pwd,String deparment,String type)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("insert into "+TABLE__USER_NAME+"(name,pwd,department,type) values(?,?,?,?)", new String[]{name,pwd,deparment,type});
	}
	public  void  ModifyPwd(String strName,String strPwd)
	{
		SQLiteDatabase db=this.getWritableDatabase();		
		ContentValues  cv=new ContentValues();
		cv.put("pwd", strPwd);		
		db.update(TABLE__USER_NAME, cv, "Name= ?", new String[]{strName});
	}
	public  void  UpdateUser(int id,String text1,String text2,String deparment,String type)
	{
		SQLiteDatabase db=this.getWritableDatabase();		
		ContentValues  cv=new ContentValues();
		cv.put("name", text1);
		cv.put("pwd", text2);
		cv.put("deparment", deparment);
		cv.put("type", type);
		db.update(TABLE__USER_NAME, cv, "_id= ?", new String[]{Integer.toString(id)});
	}
	
	public  void  DeleteUser(String strName,String strDepart,String strType){
		SQLiteDatabase db=this.getWritableDatabase();
		try{
			String strSql=String.format("delete from userinfo where name='%s' and type='%s' and department='%s'", strName, strType,strDepart);
			db.execSQL(strSql);
		}catch(Exception e){
			Log.e("ExecSQL Exception",e.getMessage());
    	    		e.printStackTrace();
		}
	}
	public  Cursor  SelectUser(){
		SQLiteDatabase db=this.getWritableDatabase();
		//Cursor c=db.rawQuery("select * from "+TABLE_NAME, null);
		Cursor cursor=db.query(TABLE__USER_NAME, null, null, null, null, null, null);
		return cursor;
	}

	public void ExecSql(String strSql)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL(strSql);
	}
	public void InsertProblem(String strCurSysID,String TrainNum ,String SubmitDate,String  Submitter ,String  Receiver ,String  Liabler,String  Content,String strState,String strPath,String strReceDate,String strEndDate,String strQualDate,String strCancelDate,String strRefreshDate,String strFailDate,String strAttach,String strTrainType,String strCancelReason,String strFailReason,String strQualUser,String strCurModifyType)
	{		
		SQLiteDatabase db=this.getWritableDatabase();			
		db.execSQL("insert into "+TABLE_PROBLEM_NAME+"(_id,TrainNum,SubmitDate,Submitter ,Receiver,Liabler,Content,State,ImagePath,ReceDate,EndDate,QualDate,CancelDate,RefreshDate,FailDate,Attach,TrainType,CancelReason,FailReason,QualUser,ModifyType) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new String[]{strCurSysID,TrainNum,SubmitDate,Submitter ,Receiver,Liabler,Content,strState,strPath, strReceDate, strEndDate, strQualDate, strCancelDate, strRefreshDate,strFailDate,strAttach,strTrainType,strCancelReason,strFailReason,strQualUser,strCurModifyType});
	}
	public  void  DeleteProblem(String SubmitDate)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.delete(TABLE_PROBLEM_NAME, "SubmitDate= ?", new String[]{SubmitDate});
	}
	public  void  UpdateProblem(String TrainNum ,String SubmitDate,String  Receiver ,String  Content)
	{
		SQLiteDatabase db=this.getWritableDatabase();		
		ContentValues  cv=new ContentValues();
		cv.put("TrainNum", TrainNum);
		cv.put("Receiver", Receiver);
		cv.put("Content", Content);	
		db.update(TABLE_PROBLEM_NAME, cv, "SubmitDate= ?", new String[]{SubmitDate});
	}	
	public  void  UpdatePhotoPath(String SubmitDate,String  PhotoPath)
	{
		SQLiteDatabase db=this.getWritableDatabase();		
		ContentValues  cv=new ContentValues();
		cv.put("ImagePath", PhotoPath);
		db.update(TABLE_PROBLEM_NAME, cv, "SubmitDate= ?", new String[]{SubmitDate});
	}	
	public void UpdateDictionary(String FirstItem ,String SecondItem ,String ThirdItem ,String FourthItem ,String OperateUnit,String strIsRemote,String OldFirstItem ,String OldSecondItem ,String OldThirdItem ,String OldFourthItem)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		String strSql=String.format("update DataDictionary set FirstItem='%s',SecondItem='%s',ThirdItem='%s',FourthItem='%s',OperateUnit='%s',IsRemoteData='%s' where FirstItem='%s' and SecondItem='%s' and ThirdItem='%s' and FourthItem='%s'", FirstItem,SecondItem,ThirdItem,FourthItem,OperateUnit,strIsRemote,OldFirstItem , OldSecondItem , OldThirdItem , OldFourthItem);
		
		
		db.execSQL(strSql);
	}
	public void DeleteDictionary(String FirstItem ,String SecondItem ,String ThirdItem ,String FourthItem,String strOperator)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		String strSql=String.format("delete from DataDictionary where FirstItem='%s' and SecondItem='%s' and ThirdItem='%s' and FourthItem='%s' and OperateUnit='%s' ", FirstItem,SecondItem,ThirdItem,FourthItem,strOperator);
		db.execSQL(strSql);
	}
	public void InsertDictionary(String FirstItem ,String SecondItem ,String ThirdItem ,String FourthItem ,String OperateUnit,String strIsRemote)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("insert into "+TABLE_DICTIONARY_NAME+"(FirstItem,SecondItem,ThirdItem,FourthItem,OperateUnit,IsRemoteData) values(?,?,?,?,?,?)", new String[]{FirstItem,SecondItem,ThirdItem,FourthItem,OperateUnit,strIsRemote});
	}
	public void ClearTable(String strName)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("DELETE FROM " + strName +";");
		RevertSeq(strName);
     
	}
	 private void RevertSeq(String strName) 
	 {
		 SQLiteDatabase db=this.getWritableDatabase();
		 db.execSQL("update sqlite_sequence set seq=0 where name='"+strName+"'");
	 }



	public void InsertDepartment(String strName) {
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("insert into "+TABLE_DEPARTMENT_NAME+"(name) values(?)", new String[]{strName});
		
	}	
}
