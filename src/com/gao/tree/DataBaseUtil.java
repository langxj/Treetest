package com.gao.tree;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;
 

public class DataBaseUtil
{
	final static String ip="192.168.1.101";
	final static String user="sa";
	final static String pwd="";
	final static String db="LocomtiveOverMonitor";

	public static Connection getSQLConnection()
	{
		Connection con = null;
		try
		{
			if(!LoginActivity.TestConnect())
				return null;
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			DriverManager.setLoginTimeout(1);
			con = DriverManager.getConnection("jdbc:jtds:sqlserver://" + LoginActivity.strServerIP + ":1433/" + db + ";charset=utf8", user, pwd);
			
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return con;
	}

	public static String testSQL()
	{
		String result = "字段1  -  字段2\n";
		try
		{
			Connection conn = getSQLConnection();
			//Connection conn = getSQLConnection("192.168.1.101", "sa", "", "LocomtiveOverMonitor");
			String sql = "select  * from ProblemData";
			Statement stmt = conn.createStatement();// 閸掓稑缂揝tatement
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{// <code>ResultSet</code>閺堬拷鍨甸幐鍥ф倻缁楊兛绔寸悰锟�
				String s1 = rs.getString("TrainNum");
				String s2 = rs.getString("SubmitDate");
				result += s1 + "  -  " + s2 + "\n";
				System.out.println(s1 + "  -  " + s2);
			}
			
			rs.close();
			stmt.close();
			conn.close();
			
			
		} catch (SQLException e)
		{
			e.printStackTrace();
			result += "查询数据异常!" + e.getMessage();
		}
		return result;
	}
	public static boolean ExecSQL(String sql)
	{
		try
		{	
			Connection conn =null;	
			
			conn = getSQLConnection();	
			if(conn==null)
				return false;			
			Statement stmt = conn.createStatement();// 
		    stmt.execute(sql);			
			stmt.close();
			conn.close();
			return true;
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			
		}
		return false;
	}
	/*public static int getLoginTimeout()
	{
		try {
			
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			DriverManager.setLoginTimeout(5);
			int iRet=DriverManager.getLoginTimeout();
			return iRet;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		
		return -1;
		
		
	}*/
	public static void main(String[] args)
	{
		//testSQL();
	}

}
