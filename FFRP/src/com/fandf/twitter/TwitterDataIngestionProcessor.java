/**
 * 
 */
package com.fandf.twitter;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fandf.commonUtil.DateUtils;
import com.fandf.commonUtil.MongoDBUtil;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

/**
 * @author SDS
 *
 */
@ManagedBean(name="twitterDataIngestionProcessor")
@ApplicationScoped
public class TwitterDataIngestionProcessor {
	
	java.util.logging.Logger log = java.util.logging.Logger.getLogger(this.getClass().getName()) ;
	
	private JSONParser jsonparser = new JSONParser() ;
	private HashMap<String, LinkedList<JSONObject>> sourceTypeConfiguredIdListMap = new HashMap<String, LinkedList<JSONObject>>() ;
	
	List<String> jsonStringObjectFriendsList = new LinkedList<String>() ;
	List<String> jsonStringObjectFollowersList = new LinkedList<String>() ;
	String jsonStringObjectUser = "" ;
	JSONObject userFreindFollowerDBJsonObj = new JSONObject() ;
	
	TwitterInformationProcessor tffp = new TwitterInformationProcessor() ;	
/*--0-0-0-0-0-0-0-0-0-0-GETTERS AND SETERS BELOW THIS0-0-0-0-0-0-0-0-0-0-0-0-0-0--*/
	

	
/*--0-0-0-0-0-0-0-0-0-0-MEHODS ONLY BELOW THIS0-0-0-0-0-0-0-0-0-0-0-0-0-0--*/
	

	/**
	 * 
	 */
	public void processConfuredIdDataFetching()
	{
		sourceTypeConfiguredIdListMap.clear();
		MongoDBUtil mongoDBUtil = new MongoDBUtil() ;
		try
		{			
			MongoDatabase db = mongoDBUtil.getMongoDBInstace() ;
			log.info("DB Name :: " + db.getName());
			log.info("DB COLLECTION :: " + db.getCollection("CONFIGURED_IDS"));

			FindIterable<Document> cursor = db.getCollection("CONFIGURED_IDS").find(); 
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
						if ( (sourceTypeConfiguredIdListMap != null)  )
						{
							if ( sourceTypeConfiguredIdListMap.containsKey(sourceType) )
							{
								LinkedList<JSONObject> congIdList = (LinkedList<JSONObject>) sourceTypeConfiguredIdListMap.get(sourceType) ;
								congIdList.add(configuredIdObject) ;
							}
							else
							{
								LinkedList<JSONObject> congIdList = new LinkedList<JSONObject>() ;
								congIdList.add(configuredIdObject) ;
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
			
			log.info("Configured ID MAP :: " + sourceTypeConfiguredIdListMap );
			
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
	public void processDataValidation()
	{
		
		if ( (sourceTypeConfiguredIdListMap != null) && !(sourceTypeConfiguredIdListMap.isEmpty()) )
		{
			log.info("Data Sources :: " + sourceTypeConfiguredIdListMap.keySet() );
			for ( String dataSourceType : sourceTypeConfiguredIdListMap.keySet())
			{
				if ( (dataSourceType != null) )
				{
					if ( dataSourceType.toLowerCase().startsWith("t"))
					{
						LinkedList<JSONObject> congIdList = (LinkedList<JSONObject>) sourceTypeConfiguredIdListMap.get( dataSourceType ) ;
						if ( (congIdList != null) && !(congIdList.isEmpty()))
						{
							for ( JSONObject configuredIdObject : congIdList)
							{
								if ( configuredIdObject != null)
								{
									String screenName = (String) configuredIdObject.get("CONFIGURED_ID") ;
									if ( (screenName != null) && !(screenName.isEmpty()))
									{
										log.info("PROCESSING FOR sCREEN nAME :: " + screenName );
										processTwitterDataValidation( screenName ) ;
										try 
										{
											Thread.sleep((5 * 60 * 1000) );
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param screenName
	 */
	@SuppressWarnings({ "unchecked" })
	private void processTwitterDataValidation(String screenName)
	{
		if ( (screenName == null) || (screenName.isEmpty()) )
			return ;
		
		jsonStringObjectFollowersList.clear();
		jsonStringObjectFriendsList.clear();
		jsonStringObjectUser = "" ;
		userFreindFollowerDBJsonObj = null ;
		
		jsonStringObjectFollowersList.addAll( tffp.fetchJSONStringUserList( tffp.fetchFollowersByScreenName(screenName) ) )  ; 
		jsonStringObjectFriendsList.addAll(tffp.fetchJSONStringUserList(tffp.fetchFriendsByScreenName(screenName) ) ) ; 
		jsonStringObjectUser = tffp.fetchJSONStringUser( tffp.fetchUserInfoByScreenName(screenName) ) ; 
		
		System.out.println(" Screen Name  Running :: " + screenName );
		MongoDBUtil mongoDBUtil = new MongoDBUtil() ;
		try
		{			
			MongoDatabase db = mongoDBUtil.getMongoDBInstace() ;
			System.out.println("DB Name :: " + db.getName());
			System.out.println("DB COLLECTION :: " + db.getCollection("TWITTER_DETAIL"));
			FindIterable<Document> iterable  = db.getCollection("TWITTER_DETAIL").find( new Document("SCREENNAME",  screenName ));
			iterable.forEach(new Block<Document>()
			{
				@Override
				public void apply(final Document document) 
				{
					try
					{
						userFreindFollowerDBJsonObj = (JSONObject) jsonparser.parse(document.toJson() ) ;
					} catch (ParseException e)
					{
						e.printStackTrace();
					}
					
				} 
			});
			
			if ( (userFreindFollowerDBJsonObj == null) && (jsonStringObjectUser != null) )
			{
				
				JSONObject userJsObj = getParsedJSONObject(jsonStringObjectUser) ;
				JSONArray friendsArray = getParsedJSONArray( jsonStringObjectFriendsList + "") ;
				JSONArray followersArray = getParsedJSONArray( jsonStringObjectFollowersList + "") ;
				if ( (userJsObj != null) )
				{
					JSONObject ffuJsObj = new JSONObject() ;
					ffuJsObj.put("_id", userJsObj.get("id")); 
					ffuJsObj.put("SCREENNAME", userJsObj.get("screen_name"));
					ffuJsObj.put("USER", userJsObj);
					if ( (friendsArray != null) && !(friendsArray.isEmpty()))
					{
						for ( Object obj : friendsArray) 
						{
							JSONObject friendObj = (JSONObject) obj ;
							long friendCount = (long) friendObj.get("friends_count");
							long follsCount = (long) friendObj.get("followers_count");
							friendObj.put("friends_count", friendCount + "" ) ;
							friendObj.put("followers_count", follsCount + "" ) ;
							friendObj.put("friend_from_date", DateUtils.getFormatedDate( new Date(), DateUtils.IST_DATE_FORMAT, DateUtils.DISPLAY_DATE_FORMAT)) ;
							friendObj.put("isfriend", true) ;
							friendObj.put("twitter_joined_date", DateUtils.getFormatedDate((String)friendObj.get("created_at"), DateUtils.IST_DATE_FORMAT, DateUtils.DISPLAY_DATE_FORMAT)) ;
						}
						ffuJsObj.put("FRIENDS", friendsArray);
					}
					if ( (followersArray != null) && !(followersArray.isEmpty()))
					{
						for ( Object obj : followersArray) 
						{
							JSONObject friendObj = (JSONObject) obj ;
							long friendCount = (long) friendObj.get("friends_count");
							long follsCount = (long) friendObj.get("followers_count");
							friendObj.put("friends_count", friendCount + " " ) ;
							friendObj.put("followers_count", follsCount + " " ) ;
							friendObj.put("followed_from_date", DateUtils.getFormatedDate(  new Date(), DateUtils.IST_DATE_FORMAT, DateUtils.DISPLAY_DATE_FORMAT)) ;
							friendObj.put("isfollowed", true) ;
							friendObj.put("twitter_joined_date", DateUtils.getFormatedDate((String)friendObj.get("created_at"), DateUtils.IST_DATE_FORMAT, DateUtils.DISPLAY_DATE_FORMAT)) ;
						}
						ffuJsObj.put("FOLLOWERS", followersArray);
					}			
					ffuJsObj.put("record_inserted_date", DateUtils.getFormatedDate(new Date(), DateUtils.IST_DATE_FORMAT, DateUtils.DISPLAY_DATE_FORMAT));
					Document doc = new Document(ffuJsObj) ;
					db.getCollection("TWITTER_DETAIL").insertOne(doc);
					log.info("INSERTED REOCER FOR SCREEN NAME :: " + screenName );
				}
			
			} else
			{
				log.info("--0=0-=-00000 === USER ALREADY REGISTERED GOING TO DO VALIDATION ====--- AND UPDATION-==");
				
					JSONObject userJsObj = getParsedJSONObject(jsonStringObjectUser) ;
					JSONArray friendsArray = getParsedJSONArray( jsonStringObjectFriendsList + "") ;
					JSONArray followersArray = getParsedJSONArray( jsonStringObjectFollowersList + "") ;
					
					JSONArray friendsDBArray = (JSONArray) userFreindFollowerDBJsonObj.get("FRIENDS") ;
					JSONArray followersDBArray = (JSONArray) userFreindFollowerDBJsonObj.get("FOLLOWERS") ;
					
					JSONArray newlyAddedFriends = new JSONArray() ;
					JSONArray existingRemovedFriends = new JSONArray() ;
					
					JSONArray newlyAddedFollowers = new JSONArray() ;
					JSONArray existingRemovedFollowers = new JSONArray() ;
					
					for ( Object freObject : friendsArray)
					{
						boolean isnewfriend = true ;
						JSONObject freJsObj = (JSONObject) freObject ;
						for ( Object freDBObject : friendsDBArray)
						{
							JSONObject freDbJsObj = (JSONObject) freDBObject ;
							
							if ( ((String) freJsObj.get("screen_name")).equals((String)freDbJsObj.get("screen_name")) )
							{
								isnewfriend = false ;
								break ;
							}
						}
						if ( isnewfriend )
						{
							freJsObj.put("friend_from_date", DateUtils.getFormatedDate(new Date(), DateUtils.IST_DATE_FORMAT, DateUtils.DISPLAY_DATE_FORMAT)) ;
							freJsObj.put("isfriend", true) ;
							
							long friendCount = (long) freJsObj.get("friends_count");
							long follsCount = (long) freJsObj.get("followers_count");
							freJsObj.put("friends_count", friendCount + "" ) ;
							freJsObj.put("followers_count", follsCount + "" ) ;
							
							newlyAddedFriends.add(freJsObj) ;
						}
					}
					log.info( " SCREEN NAME :: " + screenName + " ::" + " Friends New Added Count :: " + newlyAddedFriends.size() /* + " ::: Details :: \n " + newlyAddedFriends + " \n"*/ );
					
					for ( Object freObject : newlyAddedFriends )
					{
						JSONObject freJsObj = (JSONObject) freObject ;
						for ( Object freDBObject : friendsArray)
						{
							JSONObject freDbJsObj = (JSONObject) freDBObject ;
							
							if ( ((String) freJsObj.get("screen_name")).equals((String)freDbJsObj.get("screen_name")) )
							{
								friendsArray.remove(freDBObject) ;
								break ;
							}
						}
					}
					
					log.info( " SCREEN NAME :: " + screenName + " ::" +  "Friends Existing Added Count :: " + friendsDBArray.size() /* + " ::: Details :: \n " + friendsDBArray + " \n" */ );
					
					for ( Object freObject : friendsDBArray )
					{
						boolean isNotfriend = true ;
						JSONObject freJsObj = (JSONObject) freObject ;
						for ( Object freDBObject : friendsArray)
						{
							JSONObject freDbJsObj = (JSONObject) freDBObject ;
							
							if ( ((String) freJsObj.get("screen_name")).equals((String)freDbJsObj.get("screen_name")) )
							{
								isNotfriend = false ;
								break ;
							}
						}
						if ( isNotfriend )
						{
							freJsObj.put("unfriended_date", DateUtils.getFormatedDate(new Date(), DateUtils.IST_DATE_FORMAT, DateUtils.DISPLAY_DATE_FORMAT)) ;
							freJsObj.put("isfriend", false) ;
							existingRemovedFriends.add(freJsObj) ;
						}
					}
					log.info( " SCREEN NAME :: " + screenName + " ::" +  "Removed Friends Count :: " + existingRemovedFriends.size() /* + " :: Details :: \n  "+ existingRemovedFriends + " \n" */ );

					for ( Object folObject : followersArray)
					{
						boolean isnewfollow = true ;
						JSONObject folJsObj = (JSONObject) folObject ;
						for ( Object folDBObject : followersDBArray)
						{
							JSONObject folDbJsObj = (JSONObject) folDBObject ;
							
							if ( ((String) folJsObj.get("screen_name")).equals((String)folDbJsObj.get("screen_name")) )
							{
								isnewfollow = false ;
								break ;
							}
						}
						if ( isnewfollow )
						{
							folJsObj.put("followed_from_date", DateUtils.getFormatedDate(new Date(), DateUtils.IST_DATE_FORMAT, DateUtils.DISPLAY_DATE_FORMAT)) ;
							folJsObj.put("isfollowed", true) ;
							
							long friendCount = (long) folJsObj.get("friends_count");
							long follsCount = (long) folJsObj.get("followers_count");
							folJsObj.put("friends_count", friendCount + "" ) ;
							folJsObj.put("followers_count", follsCount + "" ) ;
							
							newlyAddedFollowers.add(folJsObj) ;
						}
					}
					log.info( " SCREEN NAME :: " + screenName + " ::" +  "Followers New Added Count :: " + newlyAddedFollowers.size() /* + " ::: Details :: \n " + newlyAddedFollowers + " \n"*/ );
					for ( Object folObject : newlyAddedFollowers )
					{
						JSONObject folJsObj = (JSONObject) folObject ;
						for ( Object folDBObject : followersArray)
						{
							JSONObject folDbJsObj = (JSONObject) folDBObject ;
							
							if ( ((String) folJsObj.get("screen_name")).equals((String)folDbJsObj.get("screen_name")) )
							{
								followersArray.remove(folDBObject) ;
								break ;
							}
						}
					}
					log.info(" SCREEN NAME :: " + screenName + " ::" + "Followers Existing Added Count :: " + followersDBArray.size() /* + " ::: Details :: \n " + followersDBArray + " \n"*/ );
					
					for ( Object folObject : followersDBArray )
					{
						boolean isNotFollower = true ;
						JSONObject folJsObj = (JSONObject) folObject ;
						for ( Object folDBObject : friendsArray)
						{
							JSONObject folDbJsObj = (JSONObject) folDBObject ;
							
							if ( ((String) folJsObj.get("screen_name")).equals((String)folDbJsObj.get("screen_name")) )
							{
								isNotFollower = false ;
								break ;
							}
						}
						if ( isNotFollower )
						{
							folJsObj.put("unfollowed_date", DateUtils.getFormatedDate(new Date(), DateUtils.IST_DATE_FORMAT, DateUtils.DISPLAY_DATE_FORMAT)) ;
							folJsObj.put("isfollowed", false) ;
							existingRemovedFollowers.add(folJsObj) ;
						}
					}
					log.info(" SCREEN NAME :: " + screenName + " ::" + "Followers Removed Count :: " + existingRemovedFollowers.size() /* + " :: Details :: \n  "+ existingRemovedFollowers + " \n" */);
					log.info("User Screen Name :: " + userJsObj.get("screen_name") + " \n .....PROCESSING..End .");
					
					HashMap<String, JSONObject> freisFinalMap = new LinkedHashMap<String, JSONObject>() ;
					for( Object dbf : friendsDBArray)
					{
						JSONObject key = (JSONObject) dbf ;
						if ( (key != null) && ( key.get("screen_name") != null ))
						{
							String scN = (String) key.get("screen_name");

							key.put("friends_count", key.get("friends_count") + " " ) ;
							key.put("followers_count", key.get("followers_count") + " " ) ;
							
							freisFinalMap.put(scN, key) ;
						}
					}
					
					for( Object dbf : newlyAddedFriends)
					{
						JSONObject key = (JSONObject) dbf ;
						if ( (key != null) && ( key.get("screen_name") != null ))
						{
							String scN = (String) key.get("screen_name");
							
							key.put("friends_count", key.get("friends_count") + " " ) ;
							key.put("followers_count", key.get("followers_count") + " " ) ;
							
							freisFinalMap.put(scN, key) ;
						}
					}
					
					for( Object dbf : existingRemovedFriends)
					{
						JSONObject key = (JSONObject) dbf ;
						if ( (key != null) && ( key.get("screen_name") != null ))
						{
							String scN = (String) key.get("screen_name");
							
							key.put("friends_count", key.get("friends_count") + " " ) ;
							key.put("followers_count", key.get("followers_count") + " " ) ;
							
							freisFinalMap.put(scN, key) ;
						}
					}
					
					JSONArray finalFriendsArray = new JSONArray() ;
					finalFriendsArray.addAll(freisFinalMap.values()) ;
					
					HashMap<String, JSONObject> follFinalMap = new LinkedHashMap<String, JSONObject>() ;
					for( Object dbf : followersDBArray)
					{
						JSONObject key = (JSONObject) dbf ;
						if ( (key != null) && ( key.get("screen_name") != null ))
						{
							String scN = (String) key.get("screen_name");
							
							key.put("friends_count", key.get("friends_count") + " " ) ;
							key.put("followers_count", key.get("followers_count") + " " ) ;
							
							follFinalMap.put(scN, key) ;
						}
					}
					
					for( Object dbf : newlyAddedFollowers)
					{
						JSONObject key = (JSONObject) dbf ;
						if ( (key != null) && ( key.get("screen_name") != null ))
						{
							String scN = (String) key.get("screen_name");
							
							key.put("friends_count", key.get("friends_count") + " " ) ;
							key.put("followers_count", key.get("followers_count") + " " ) ;
							
							follFinalMap.put(scN, key) ;
						}
					}
					
					for( Object dbf : existingRemovedFollowers)
					{
						JSONObject key = (JSONObject) dbf ;
						if ( (key != null) && ( key.get("screen_name") != null ))
						{
							String scN = (String) key.get("screen_name");
							
							key.put("friends_count", key.get("friends_count") + " " ) ;
							key.put("followers_count", key.get("followers_count") + " " ) ;
							
							follFinalMap.put(scN, key) ;
						}
					}
					
					
					JSONArray finalFollsArray = new JSONArray() ;
					finalFollsArray.addAll(follFinalMap.values()) ;
					
					JSONObject ffuJsObj = new JSONObject() ;
					ffuJsObj.put("_id", userJsObj.get("id")); 
					ffuJsObj.put("SCREENNAME", userJsObj.get("screen_name"));
					ffuJsObj.put("USER", userJsObj);
					ffuJsObj.put("FRIENDS", finalFriendsArray);
					ffuJsObj.put("FOLLOWERS", finalFollsArray);
					
					String stringDoc = "" + ffuJsObj.toString() ;
					int i = 0;
					String replaceString = "{\"$numberLong\":" ;
					while ( i > 0)
					{
						int ind = stringDoc.indexOf(replaceString, i) ;
						if ( i > 0 )
						{
							log.info("wsds ");
							int len =  i + replaceString.length() ;
							int indexBra = stringDoc.indexOf("}", len ) ;
							String docString1 = stringDoc.substring(0, ind) ;
							String docString2 = stringDoc.substring(len , indexBra) ;
							String docString3 = stringDoc.substring(indexBra +1 , stringDoc.length() ) ;
							stringDoc = docString1 + docString2 + docString3 ;
							i = indexBra + 1 ;
						}
					}
					log.info("-----------------printing Doc---------------\n\n\n" );
					//System.out.println( stringDoc + "");
					
					log.info("-----------------printing Doc End---------------\n\n\n" );
					Document doc =  new Document( getParsedJSONObject(stringDoc) );
					db.getCollection("TWITTER_DETAIL").findOneAndReplace(new Document("SCREENNAME", screenName),doc);
					
/*					db.getCollection("TWITTER_DETAIL").updateOne(new Document("SCREENNAME", screenName),
				        new Document("$set", new Document("USER", userJsObj))
				            .append("$set", new Document("FRIENDS", finalFriendsArray))
				            .append("$set", new Document("FOLLOWERS", finalFollsArray))
				            );*/
					log.info("UPDATED IN DB VERIFY ONCE .IN DB .");
			}
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
	 * @return
	 */
	private JSONObject getParsedJSONObject(String stringJsonObj)
	{
		if ( (stringJsonObj != null) && !(stringJsonObj.isEmpty()))
		{
			JSONObject object = new JSONObject() ;
			try
			{
				object = (JSONObject) jsonparser.parse(stringJsonObj) ;
				if ( object != null )
					return object ;
			} catch (ParseException e) 
			{
				e.printStackTrace();
			}
		}
		return null ;
	}
	
	/**
	 * 
	 * @return
	 */
	private JSONArray getParsedJSONArray(String stringJsonArray)
	{
		if ( (stringJsonArray != null) && !(stringJsonArray.isEmpty()))
		{
			JSONArray objectArray = new JSONArray() ;
			try
			{
				objectArray = (JSONArray) jsonparser.parse(stringJsonArray) ;
				if ( (objectArray != null) && !(objectArray.isEmpty()) )
					return objectArray ;
			} catch (ParseException e) 
			{
				e.printStackTrace();
			}
		}
		return null ;
	}
	
}
