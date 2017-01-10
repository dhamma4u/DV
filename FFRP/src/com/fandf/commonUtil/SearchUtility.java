package com.fandf.commonUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;



/**
 * <b> Description : </b> This class specifically filters and searches the data
 * on specific data source
 * 
 * @author DHAMMDIP
 * @version 0.1
 * @param <T>
 */
public class SearchUtility<T> {

	private Logger infologger = Logger.getLogger("SearchUtility");

	/**
	 * Default constructor
	 */
	public SearchUtility() {
		//infologger.info("............... SEARCH UTILITY LOGGING STARTED. .. ");
	}

	/**
	 * Method filters record from the list based on the String. Considers the
	 * complete String to filter.
	 * @param <T>
	 * 
	 * @param listToFilter
	 *            List of the objects as a input
	 * @param type
	 *            to filter data as a input
	 *            <table style='border:2px solid #ffa876;' >
	 *            <tr>
	 *            	<th>Serial No</th> 
	 *            	<th style='border:2px solid #ffa876;'>Type</th> 
	 *            	<th style='border:2px solid #ffa876;'>Parameters Description</th>
	 *            	<th style='border:2px solid #ffa876;'>Example</th>
	 *            </tr>
	 *            <tr>
	 *            	<td>1</td>
	 *            	<td style='border:2px solid #ffa876;'>keyword</td>
	 *            	<td style='border:2px solid #ffa876;'>multiple values should be space separated</td>
	 *            	<td style='border:2px solid #ffa876;'>ex1. some <br> </br> ex2. some times</td>
	 *            </tr>
	 *            <tr>
	 *           	 <td>2</td>
	 *        		 <td style='border:2px solid #ffa876;'>keyphrase</td>
	 *           	 <td style='border:2px solid #ffa876;' >only single string</td>
	 *            	<td style='border:2px solid #ffa876;'>some times or not this time, any time</td>
	 *            </tr>
	 *            <tr>
	 *            	<td>3</td>
	 *            	<td style='border:2px solid #ffa876;'>date</td>
	 *            	<td style='border:2px solid #ffa876;'>date should be in long which is converted in String . if single date is passed then considered the same if start date and end date is there then  pass it with comma separation</td>
	 *            	<td style='border:2px solid #ffa876;'>ex1. 1410076025247,1420444025247</td>
	 *            </tr>
	 *            </table>
	 *     @param params      You need to pass key value pair of the other search Criteria
	 *            Specifically You need to pass Extra parameter key as a List Object class Variable and its value as a string
	 *   <br> <b> Try Stand-Alone of SearchUtility To Test. Only UnComment The main method code and private Class code  and Run IT . To Better Understanding </b>
	 *@return List of the filtered objects
	 * 
	 * 
	 */
	@SuppressWarnings("hiding")
	public <T> List<T> searchOnData(List<T> listToFilter, String type, String... params)
	{
		infologger.info("............... SEARCH UTILITY searchOnData STARTED. .. ");
		//System.out.println("............... SEARCH UTILITY searchOnData STARTED. .. ");
		infologger.info("Parameteres passed :: List To Filter Size : "+ listToFilter.size() + "  :: Type : " + type+ "  :: Parameters Length : " + params.length);
		//System.out.println("\t Parameteres passed :: List To Filter Size : "+ listToFilter.size() + "  :: Type : " + type+ "  :: Parameters Length : " + params.length);

		List<T> filteredList = new ArrayList<T>();

		if ((listToFilter != null) && (listToFilter.size() > 0) && (type != null) && !(type.equalsIgnoreCase("")) && (params != null) && (params.length > 0))
		{
			//infologger.info("All Conditions Satisfied started filtering...  ");
			try 
			{
				String valueToFilter = "";
				if (type.equalsIgnoreCase("keyword"))
				{
					if( (params[0] != null) && !(params[0].equalsIgnoreCase("")) )
					{
						//System.out.println("\t Parameter not zero : " +  params[0]);
						params[0] = params[0].trim();
						valueToFilter = params[0].replaceAll("  ", " ");
						valueToFilter = valueToFilter.trim();
						valueToFilter = params[0].replaceAll(" ", "\\|\\*\\|");
					}
					infologger.info("Found Keyword(s) :  " + valueToFilter);
					//System.out.println("\t Found Keyword(s) :  " + valueToFilter);
					
				} else if (type.equalsIgnoreCase("keyphrase")) 
				{
					if( (params[0] != null) && !(params[0].equalsIgnoreCase("")) )
					{
						params[0] = params[0].trim();
						valueToFilter = params[0] ;
					}
					infologger.info("Found Keyphrase :  " + valueToFilter );
				}
				else if (type.equalsIgnoreCase("date")) 
				{
					if ( (params.length > 2) && (params[0] != null) && (params[1] != null) && (params[2] != null) &&
							!(params[0].equalsIgnoreCase("")) && !(params[2].equalsIgnoreCase("")) && !(params[2].equalsIgnoreCase("")) )
					{
						String dateparameter = params[0].trim() ;
						long startdate = Long.parseLong(params[1].trim()) ;
						long enddate = Long.parseLong(params[2].trim()) ;
						infologger.info("Found Date Parameters  :  " + "Field Name : " + dateparameter + " :: Start Date : " + startdate + " :: End Date :: " + enddate  );
						//System.out.println("Found Date Parameters  :  " + "Field Name : " + dateparameter + " :: Start Date : " + startdate + " :: End Date :: " + enddate  );
						filteredList = filterByDate(listToFilter, dateparameter, startdate, enddate ) ;
						infologger.info("\t Final filtered List Size :  " + filteredList.size() + " : " );
					//	System.out.println("\t Final filtered List Size :  " + filteredList.size() + " : " + filteredList);
					}
					return filteredList ;
				}
			//Below code IS The call for the Actual Logic and Prepare the Filteration List	
				if (params.length > 1)
				{	
//					infologger.info("\t if   filtered List Size :  " + listToFilter.size() + " : ");
					
					filteredList = processDataFiltration(listToFilter, valueToFilter, null, null);
					for (int i = 1; i < params.length; i++)
					{
						if ((i % 2) > 0) 
						{
							infologger.info("\t Found Extra Params ::  Key :  " + params[i] + "  :: value : " + params[(i + 1)]);
							//System.out.println("\t Found Extra Params ::  Key :  " + params[i] + "  :: value : " + params[(i + 1)]);
							String fieldname = params[i];
							String fieldvalue = params[(i + 1)];
							
							if(filteredList.size() > 0){
								filteredList = processDataFiltration(filteredList, null, fieldname, fieldvalue);
							}
							else
								filteredList = processDataFiltration(listToFilter, null, fieldname, fieldvalue);
						}
					}
				} else
				{
//					infologger.info("\t Else  filtered List Size :  " + listToFilter.size() + " : ");
					filteredList = processDataFiltration(listToFilter, valueToFilter, null, null);
				}
				infologger.info("\t Final filtered List Size :  " + filteredList.size() + " : ");
				//System.out.println("\t Final filtered List Size :  " + filteredList.size() + " : " + filteredList);
				//Here It ends The Logical Part 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		infologger.info("............... SEARCH UTILITY searchOnData FINISHED------------. .. ");
		return filteredList;
	}

	/**
	 * This method Filters only keywords based Records . Getting Used
	 * Internally.
	 * @param <T>
	 * 
	 * @param list
	 * @param param
	 */
	@SuppressWarnings("hiding")
	private <T> List<T> processDataFiltration(List<T> list, String param, String searchFieldName, String searchFieldvalue)
	{
//		infologger.info("list Size:- processDataFiltration"+list.size());
		String[] filterValues = null;
		String valueToFilter = "";

		if( (param != null)  && !(param.equalsIgnoreCase("")) && (param.length() > 0 )  )
		{
			valueToFilter = param.trim();
			filterValues = valueToFilter.split("\\|\\*\\|");
		}

		HashMap<T, T> objectsMap = new HashMap<T, T>() ;
		for (T object : list) 
		{
			if(object!=null)
			{
//				infologger.info("object is not null");
//				infologger.info(object);
				for (Field field : object.getClass().getDeclaredFields()) 
				{
					
					if(field!=null)
					{
//						infologger.info("field is not null");
//						infologger.info(field);
						field.setAccessible(true);
						String fieldName = field.getName();
						try 
						{
							Object fieldValue = field.get(object);
							if((filterValues != null) && (filterValues.length > 0))
							{
								for (String value : filterValues) 
								{
									if (fieldValue != null)
									{
										if (fieldValue.toString().toLowerCase().contains(value.toLowerCase())) 
										{
											objectsMap.put(object, object);
											break;
										}
									}
								}
							} else if ((searchFieldName != null) && !(searchFieldName.equalsIgnoreCase("")) && (searchFieldvalue != null) && !(searchFieldvalue.equalsIgnoreCase("")) ) 
							{
								if( (fieldName.equals(searchFieldName)) && (fieldValue != null)  )
								{
									if (fieldValue.toString().toLowerCase().contains(searchFieldvalue.toLowerCase())) 
									{
										objectsMap.put(object, object);
										break; 
									}
								}
							}
						} catch (Exception esd) {
							esd.printStackTrace();
						}
					}
				}
			}
		}
		List<T> templist = new ArrayList<T>() ;
		templist.addAll(objectsMap.keySet()) ;

		return templist ;

	}

	/**
	 * SEARCH ON THE BASIS OF THE DATE
	 * 
	 * @param args
	 */
	@SuppressWarnings("hiding")
	public <T> List<T> filterByDate(List<T> listToFilter, String dateParameterName, long startdate, long enddate )
	{
		HashMap<T, T> objectsMap = new HashMap<T, T>() ;
		for (T object : listToFilter) 
		{
			for (Field field : object.getClass().getDeclaredFields()) 
			{
				field.setAccessible(true);
				String fieldName = field.getName();
				try 
				{
					Object fieldValue = field.get(object);
					{
						if( ( dateParameterName != null )&& !(dateParameterName.equalsIgnoreCase("")) && (fieldName.equals(dateParameterName)) && (fieldValue != null)  )
						{
							Date testdate = (Date) fieldValue ;
							if ( (startdate <= testdate.getTime()) && (testdate.getTime() <= enddate )) 
							{
								//System.out.println("Matched Criteria of the Date Adding Record.");
								objectsMap.put(object, object);
								break;
							}
						}
					}
				} catch (Exception esd) {
					esd.printStackTrace();
				}
			}
		}
		List<T> templist = new ArrayList<T>() ;
		templist.addAll(objectsMap.keySet()) ;
		return templist ;
	}

	/*
	 * Main Method to test the program
	 */
/*	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String args[]) 
	{
		SearchUtility su = new SearchUtility();
		
		List<SearchUtility.TestClass> testlist = new ArrayList<SearchUtility.TestClass>() ;
		for (int i = 0; i < 4; i++) 
		{
			SearchUtility.TestClass ts = su.new TestClass() ;
			if(i == 0 ){ ts.setDate(new Date(1420632226564L)) ; ts.setDdouble(100.11); ts.setLlong(1000); ts.setNumber(100) ; ts.setText("First parameter") ;  }
			if(i == 1 ){ ts.setDate(new Date(1420632446306L)) ; ts.setDdouble(200.11); ts.setLlong(2000); ts.setNumber(200) ; ts.setText("Second Class instant") ;  }
			if(i == 2 ){ ts.setDate(new Date(1420634298319L)) ; ts.setDdouble(300.11); ts.setLlong(3000); ts.setNumber(300) ; ts.setText("dhruv is busy") ;  }
			if(i == 3 ){ ts.setDate(new Date(1420634870410L)) ; ts.setDdouble(400.11); ts.setLlong(4000); ts.setNumber(400) ; ts.setText("Nayaku given some F lution") ;  }
			testlist.add(ts)  ;
		}

		System.out.println("Method KEYWORD Call ------------- :: su.searchOnData(snlis1t, 'keyword', '400 dhru' )" );
			su.searchOnData(testlist, "keyword", "400 dhru" ) ;
		System.out.println("Method KEYWORD Call ------------- :: su.searchOnData(snlis1t, 'keyword', 'first dhruv', 'text', 'First parameter'  )" ); 
			su.searchOnData(testlist, "keyword", "first dhruv", "text", "First parameter"  ) ;
		System.out.println("Method KEYWORD Call ------------- :: su.searchOnData(snlis1t, 'keyword', null, 'text', 'First parameter', 'llong' , '2000'  )" ) ;
			su.searchOnData(testlist, "keyword", null, "text", "First parameter", "llong" , "2000"  ) ;
		System.out.println("Method KEYPHRASE Call ------------- :: su.searchOnData(snlis1t, 'keyphrase', 'first dhruv', 'text', 'First parameter'  )" ); 
			su.searchOnData(testlist, "keyphrase", "First parameter", "llong", "1000"  ) ;
		System.out.println("Method KEYPHRASE Call ------------- :: su.searchOnData(snlis1t, 'keyphrase', null, 'text', 'First parameter'  )" ); 
			su.searchOnData(testlist, "keyphrase", null, "llong", "1000"  ) ;
		System.out.println("Method DATE Call ------------- :: su.searchOnData(snlis1t, 'date', 'date', '1420632226564', '1420634298319'    )" ); 
			su.searchOnData(testlist, "date", "date", "1420632226564", "1420634298319"  ) ;
		System.out.println("Method DATE Call ------------- :: su.searchOnData(snlis1t, 'date', 'date', '1420634298319', '1420634870410'    )" ); 
			su.searchOnData(testlist, "date", "date", "1420634298319", "1420634870410"  ) ;
	}
	
	private class TestClass 
	{
		private String text = "" ;
		private int number = 0 ;
		private Date date = new Date() ;
		private Double ddouble = 0.00 ;
		private long llong = 00L ;
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public int getNumber() {
			return number;
		}
		public void setNumber(int number) {
			this.number = number;
		}
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public Double getDdouble() {
			return ddouble;
		}
		public void setDdouble(Double ddouble) {
			this.ddouble = ddouble;
		}
		public long getLlong() {
			return llong;
		}
		public void setLlong(long llong) {
			this.llong = llong;
		}
	}*/
	
}
