package com.fandf.commonUtil;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.faces.event.PhaseEvent;


/**
 * 
 * @author sandeep
 *
 */
public class DBUtil {

	/**
	 * THIS METHOD INSERT THE SESSION IN DATABASE .
	 * @param CMSSESSIONID
	 * @param LEA_ID
	 * @param LEA_USER_ID
	 * @param LOGGED_STATUS
	 * @param state
	 */
	public static synchronized void insertSessionInDataTable(String CMSSESSIONID, int LEA_ID, String LEA_USER_ID, String LOGGED_STATUS, String state) 
	{
		PreparedStatement pstmt;
		Connection con = null;
		pstmt = null;
		con = ConnectionService.getConnection();
		try {
			String sql = "insert into USER_LOGIN_MASTER (CMSSESSIONID,LEA_ID,LEA_USER_ID,LOGGED_STATUS,IN_TIME,STATE)values(?,?,?,?,?,?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, CMSSESSIONID);
			pstmt.setInt(2, LEA_ID);
			pstmt.setString(3, LEA_USER_ID);
			pstmt.setString(4, LOGGED_STATUS);
			pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			pstmt.setString(6, state);
			int result = pstmt.executeUpdate();
			pstmt.close();
			con.close();
			System.out.println("result " + result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 finally
			{
				try 
				{
					if(pstmt != null )
						pstmt.close();
					if( (con != null)  && (!con.isClosed()))
						con.close(); 
				} catch (SQLException e)
				{
					e.printStackTrace();
				} 
			}
			
	}
	
/**
 * THIS METHOD REMOVES SESSION FROM THE DATABASE
 * @param LEA_USER_ID
 */
	public static synchronized void removeLoggedInSessions(String LEA_USER_ID ) 
	{
		int result;
		PreparedStatement pstmt;
		Connection con = null;
		result = 0;
		pstmt = null;
		con = ConnectionService.getConnection();
		try {
			//DELETE FROM USER_LOGIN_MASTER WHERE LEA_USER_ID='pqr';
			String sql = "DELETE FROM USER_LOGIN_MASTER WHERE LEA_USER_ID=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, LEA_USER_ID);
			result = pstmt.executeUpdate();
			pstmt.close();
			con.close();
			System.out.println("result :: "+ result );

		} catch (SQLException e)
		{
			e.printStackTrace();
		} finally
		{
			try 
			{
				if(pstmt != null )
					pstmt.close();
				if( (con != null)  && (!con.isClosed()))
					con.close(); 
			} catch (SQLException e)
			{
				e.printStackTrace();
			} 
		}
		
	}
	
	/**
	 * 
	 * @param event
	 * @param sessionid
	 * @return
	 */
	public static synchronized boolean AuthenticateSessionFromDB(PhaseEvent event, String sessionid) 
	{
		boolean status = false;
		CallableStatement pstmt = null;
		ResultSet rs = null;
		Connection con = null;
		
		System.out.println("sessionid  : " + sessionid);

		try {
			con = ConnectionService.getConnection();
			if (con != null) {
				pstmt = con.prepareCall("{ Call getauthenticatesession(?,?) }");
				System.out.println("getSessionId() :: " + sessionid);
				pstmt.setString(1, sessionid);
				pstmt.registerOutParameter(2, -10);
				pstmt.execute();
				rs = (ResultSet) pstmt.getObject(2);
				while (rs.next()) {
					System.out.println("rs.getString(logged_status) :: " + rs.getString("logged_status"));
					String record_found = rs.getString("logged_status");
					// if record found is 0 then not active else active.

					if (record_found.equalsIgnoreCase("Active")) {
						// true related to presence of record in the table
						status = true;
					}
					/*
					 * if (!record_found.equalsIgnoreCase("0")) { // true
					 * related to presence of record in the table status = true;
					 * }
					 */
					System.out.println("status :: " + status);
				}
				rs.close();
				pstmt.close();
				con.close();
				// ConnectionService.CloseConnection() ;
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		} finally 
		{
			try 
			{
				if (rs != null) 
					rs.close();
				if (pstmt != null) 
					pstmt.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return status ;
	}
	
}
