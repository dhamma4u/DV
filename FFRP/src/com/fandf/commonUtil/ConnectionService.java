package com.fandf.commonUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ConnectionService 
{
	 static Connection con = null;
	 public ConnectionService()
	 {
		try
		{	
			
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	 }

	/*----connection code-----------------------------*/	
		
	public static Connection getConnection()
	{	
		ResourceBundle rb = ResourceBundle.getBundle("connections");
	 	String first = rb.getString("dburl");
		String second =  rb.getString("dbuser");
		String third = rb.getString("dbpass");
		
		try
		{	
			Class.forName("oracle.jdbc.OracleDriver");
	
			con = DriverManager.getConnection(first,second,third);
			if(con != null)
			{ 
				System.out.println("connection ::" + con);
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		return con;
	}	
	
	public static Connection getConnection(int i)
	{
		String first = "jdbc:oracle:thin:@192.168.0.25:1522:xe" ;
		String second = "AQTYDEV_REF" ;
		String third = "aqtydevref" ;
		
		try
		{	
			Class.forName("oracle.jdbc.OracleDriver");
	
			con = DriverManager.getConnection(first,second,third);
			if(con != null)
			{ 
				System.out.println("connection ::" + con);
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		return con;
	}
	
	public static void CloseConnection()
	{
		if(con != null)
		{
			try 
			{
				con.close();
			}
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[])
	{
		System.out.println("con RB : " + getConnection());
		System.out.println("con  : " + getConnection(1));
	}
}
