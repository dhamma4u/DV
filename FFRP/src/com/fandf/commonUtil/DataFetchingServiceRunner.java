/**
 * 
 */
package com.fandf.commonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.fandf.twitter.TwitterDataIngestionProcessor;

/**
 * @author SDS
 *
 */
@SuppressWarnings("serial")
public class DataFetchingServiceRunner extends HttpServlet
{
	@Override
    public void init() throws ServletException
    {
		System.out.println("--------****SDS***..!!");
		System.out.println("--------****SDS***..!!");
		System.out.println("--------****SDS***..!!");
		System.out.println("--------****SDS***..!!");
    	System.out.println("--------*******-SERVICE-DATA FETCHING.. STARTED...!!");
		System.out.println("---------- SDS Service is Going To Start Here ---- Automatically on Server StartUp *****");
		new Thread()
		{
			public void run() 
			{
				for(;;)
				{
					try
					{
						TwitterDataIngestionProcessor tdip = new TwitterDataIngestionProcessor();
						tdip.processConfuredIdDataFetching(); 
						tdip.processDataValidation();
						System.out.println("Thread Still Running.");
					} catch ( Exception e)
					{
						e.printStackTrace(); 
					}
					try 
					{
						System.out.println("thread going To Sleep For 12 Hrs .");
						sleep(12 * 60 * 60 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
		System.out.println("----------SERVICE.....");
    }
}
