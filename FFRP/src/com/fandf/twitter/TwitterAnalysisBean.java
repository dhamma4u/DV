/**
 * 
 */
package com.fandf.twitter;


import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fandf.commonUtil.MongoDBUtil;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

/**
 * @author SDS
 *
 */
@ManagedBean(name="twitterAnalysisBean")
@SessionScoped
public class TwitterAnalysisBean {

	java.util.logging.Logger log = java.util.logging.Logger.getLogger(this.getClass().getName()) ;
	private String searchScreenName = "" ;
	private JSONObject twitterDetailObject = new JSONObject() ;
	private JSONParser jsonparser = new JSONParser() ;
	
	private HashMap<String, LinkedList<String>> sourceTypeConfiguredIdListMap = new HashMap<String, LinkedList<String>>() ;
	private String userNumericId = "" ;
	private String selectedDataSourceType = "" ;
	
/*--------------------------Getter and Setter Below This line--------------------------*/	

	/**
	 * @return the searchScreenName
	 */
	public String getSearchScreenName() {
		return searchScreenName;
	}

	/**
	 * @param searchScreenName the searchScreenName to set
	 */
	public void setSearchScreenName(String searchScreenName) {
		this.searchScreenName = searchScreenName;
	}
	
/**
	 * @return the twitterDetailObject
	 */
	public JSONObject getTwitterDetailObject() {
		return twitterDetailObject;
	}

	/**
	 * @return the userNumericId
	 */
	public String getUserNumericId() {
		return userNumericId;
	}
	
	/**
	 * @param userNumericId the userNumericId to set
	 */
	public void setUserNumericId(String userNumericId) {
		this.userNumericId = userNumericId;
	}

	/**
	 * @param twitterDetailObject the twitterDetailObject to set
	 */
	public void setTwitterDetailObject(JSONObject twitterDetailObject) {
		this.twitterDetailObject = twitterDetailObject;
	}

	/**
	 * @return the sourceTypeConfiguredIdListMap
	 */
	public HashMap<String, LinkedList<String>> getSourceTypeConfiguredIdListMap() {
		return sourceTypeConfiguredIdListMap;
	}

	/**
	 * @param sourceTypeConfiguredIdListMap the sourceTypeConfiguredIdListMap to set
	 */
	public void setSourceTypeConfiguredIdListMap(
			HashMap<String, LinkedList<String>> sourceTypeConfiguredIdListMap) {
		this.sourceTypeConfiguredIdListMap = sourceTypeConfiguredIdListMap;
	}

	
	/**
	 * @return the selectedDataSourceType
	 */
	public String getSelectedDataSourceType() {
		return selectedDataSourceType;
	}

	/**
	 * @param selectedDataSourceType the selectedDataSourceType to set
	 */
	public void setSelectedDataSourceType(String selectedDataSourceType) {
		this.selectedDataSourceType = selectedDataSourceType;
	}
	

/*--0-0-0-0-0-0-0-0-0-0-MEHODS ONLY BELOW THIS0-0-0-0-0-0-0-0-0-0-0-0-0-0--*/
	public void processTwitterFriendsFollowers()
	{
		MongoDBUtil mongoDBUtil = new MongoDBUtil() ;
		try
		{			
			MongoDatabase db = mongoDBUtil.getMongoDBInstace() ;
			System.out.println("DB Name :: " + db.getName());
			System.out.println("DB COLLECTION :: " + db.getCollection("TWITTER_DETAIL"));
			FindIterable<Document> iterable  = db.getCollection("TWITTER_DETAIL").find( new Document("SCREENNAME", new Document("$regex", searchScreenName).append("$options","i")));
			iterable.forEach(new Block<Document>() {
				@Override
				public void apply(final Document document) 
				{
					try
					{
						twitterDetailObject = (JSONObject) jsonparser.parse(document.toJson() ) ;
					} catch (ParseException e)
					{
						e.printStackTrace();
					}
					
				} 
			});
			mongoDBUtil.closeDBInstace() ;
		}
		catch (Exception e)
		{
			mongoDBUtil.closeDBInstace() ;
			e.printStackTrace(); 
		} 
	}
	
	/**
	 * 
	 */
	public void fetchConfuredIdDataOfLoggedInUser( String loggeInUserNumericId )
	{	
		log.info("---------Logged In User Id :: " + loggeInUserNumericId );
		if ( loggeInUserNumericId == null || loggeInUserNumericId.isEmpty())
			return  ;
		
		this.userNumericId = loggeInUserNumericId ;
		sourceTypeConfiguredIdListMap.clear();
		MongoDBUtil mongoDBUtil = new MongoDBUtil() ;
		try
		{			
			MongoDatabase db = mongoDBUtil.getMongoDBInstace() ;
			log.info("DB Name :: " + db.getName());
			log.info("DB COLLECTION :: " + db.getCollection("CONFIGURED_IDS"));

			FindIterable<Document> cursor = db.getCollection("CONFIGURED_IDS").find( new Document("USERID", userNumericId)); 
			cursor.forEach(new Block<Document>() 
			{
				@Override
				public void apply(final Document document) 
				{
					try
					{
						log.info(" Document " + document );
						String sourceType = document.getString("SOURCE_TYPE") ;
						JSONObject configuredIdObject = new JSONObject() ;
						configuredIdObject = (JSONObject) jsonparser.parse(document.toJson()) ;
						if ( (sourceTypeConfiguredIdListMap != null) && (configuredIdObject != null) )
						{
							log.info("Putting The Type and Id :: " + sourceType +  " , " + configuredIdObject.get("CONFIGURED_ID") );
							if ( sourceTypeConfiguredIdListMap.containsKey(sourceType) )
							{
								LinkedList<String> congIdList = (LinkedList<String>) sourceTypeConfiguredIdListMap.get(sourceType) ;
								congIdList.add((String)configuredIdObject.get("CONFIGURED_ID")) ;
							}
							else
							{
								LinkedList<String> congIdList = new LinkedList<String>() ;
								congIdList.add((String)configuredIdObject.get("CONFIGURED_ID")) ;
								sourceTypeConfiguredIdListMap.put(sourceType, congIdList) ;
							}
						}
					} catch (Exception e)
					{
						e.printStackTrace();
					}

				} 
			});
			mongoDBUtil.closeDBInstace() ;
			
			log.info("******* *****  *** Configured ID MAP :: " + sourceTypeConfiguredIdListMap );
			
		}
		catch (Exception e)
		{
			mongoDBUtil.closeDBInstace() ;
			e.printStackTrace(); 
		} 

	}
	
	/**
	 * 
	 */
	public void processSelectedTypeAndIdSpecificData()
	{
		log.info("USer OF ID " + userNumericId +" Hit Find Buttong For ::");
		log.info("DATA SOURCE TYPE :: " + selectedDataSourceType ); 
		log.info("SEARCHED TERM :: " + searchScreenName );
		if ( (selectedDataSourceType == null) || (selectedDataSourceType.isEmpty()))
			return ;
		if ( (searchScreenName == null) && ( searchScreenName.isEmpty() ) )
			return ;
		if ( selectedDataSourceType.toLowerCase().startsWith("t"))
			processTwitterFriendsFollowers() ;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Object> fetchUserDSList()
	{
		if ( (sourceTypeConfiguredIdListMap != null) && !(sourceTypeConfiguredIdListMap.isEmpty()) )
		{
			List<Object> dsNameList  =  Arrays.asList(sourceTypeConfiguredIdListMap.keySet().toArray()) ;
			if (dsNameList != null )
				return dsNameList ;			
		}
		return null ;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<String> fetchUserConfiguredIDList()
	{
		if ( (sourceTypeConfiguredIdListMap != null) && !(sourceTypeConfiguredIdListMap.isEmpty()) 
				&& (selectedDataSourceType != null) && !(selectedDataSourceType.isEmpty()) )
		{
			List<String> dsNameList  =  sourceTypeConfiguredIdListMap.get(selectedDataSourceType) ;
			if (dsNameList != null )
				return dsNameList ;			
		}
		return null ;
	}
}
