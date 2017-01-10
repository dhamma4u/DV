/**
 * 
 */
package com.fandf.twitter;

import java.util.LinkedList;
import java.util.List;

import twitter4j.PagableResponseList;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.User;


/**
 * @author SDS
 *
 */
public class TwitterInformationProcessor {

	java.util.logging.Logger logger = java.util.logging.Logger.getLogger(this.getClass().getName()) ;
	Twitter twitter = new TwitterFactory().getInstance();
	

	/**
	 * Default Constructor
	 */
	public void processForUserDetail()
	{
		logger.info( "Method : processForUserDetail() Started ");
	}

	/**
	 * 
	 * @param screenName
	 * @return The List of the Users 
	 */
	public List<User> fetchFriendsByScreenName(String twitterScreenName)
	{
		if ( (twitterScreenName == null) || (twitterScreenName.isEmpty()) )
			return null ;
		try
		{
			List<User> listFriends = new LinkedList<User>() ;
			long cursor = -1;
			PagableResponseList<User> pagableFollowings;
			do
			{
				pagableFollowings = twitter.getFriendsList(twitterScreenName, cursor);
				for (User user : pagableFollowings) 
				{
					listFriends.add(user); // ArrayList<User>
				}
			} while ( (cursor = pagableFollowings.getNextCursor()) != 0 );
			
			if ( (listFriends != null) && !(listFriends.isEmpty()) )
				return listFriends ;
		} catch (TwitterException te) 
		{
			te.printStackTrace();
			logger.severe("Failed to get Friends Using ScreenName: " + twitterScreenName +"  ::: Error :: " + te.getMessage());
		}
		return null ;
	}
	
	/**
	 * 
	 * @param twitterNumericId
	 * @return List of the Users 
	 */
	public List<User> fetchFriendsByNumericId(long twitterNumericId)
	{
		if ( twitterNumericId == 0L )
			return null ;
		try
		{
			List<User> listFriends = new LinkedList<User>() ;
			long cursor = -1;
			PagableResponseList<User> pagableFollowings;
			do
			{
				pagableFollowings = twitter.getFriendsList(twitterNumericId, cursor);
				for (User user : pagableFollowings) 
				{
					listFriends.add(user); // ArrayList<User>
				}
			} while ( (cursor = pagableFollowings.getNextCursor()) != 0 );
			
			if ( (listFriends != null) && !(listFriends.isEmpty()) )
				return listFriends ;
		} catch (TwitterException te) 
		{
			te.printStackTrace();
			logger.severe("Failed to get Friends Using Numeric Id : " + twitterNumericId +"  ::: Error :: " + te.getMessage());
		}
		return null ;
	}
	
	/**
	 * 
	 * @param screenName String twitter screen name as Input
	 * @return The List of the Users or Followers of the twitter
	 */
	public List<User> fetchFollowersByScreenName(String twitterScreenName)
	{
		if ( (twitterScreenName == null) || (twitterScreenName.isEmpty()) )
			return null ;
		try
		{
			List<User> listFollowers = new LinkedList<User>() ;
			long cursor = -1;
            PagableResponseList<User> pagableFollowers;
            do 
            {
                pagableFollowers = twitter.getFollowersList(twitterScreenName, cursor);
                for (User user : pagableFollowers) 
                {
                    listFollowers.add(user); // ArrayList<User>
                }
            } while ((cursor = pagableFollowers.getNextCursor()) != 0);
			
			if ( (listFollowers != null) && !(listFollowers.isEmpty()) )
				return listFollowers ;
		} catch (TwitterException te) 
		{
			te.printStackTrace();
			logger.severe("Failed to get Followers Using ScreenName: " + twitterScreenName +"  ::: Error :: " + te.getMessage());
		}
		return null ;
	}
	
	/**
	 * 
	 * @param twitterNumericId long twitter id as input
	 * @return List of the Users or Followers
	 */
	public List<User> fetchFollowersByNumericId(long twitterNumericId)
	{
		if ( twitterNumericId == 0L )
			return null ;
		try
		{
			List<User> listFollowers = new LinkedList<User>() ;
			long cursor = -1;
            PagableResponseList<User> pagableFollowers;
            do 
            {
                pagableFollowers = twitter.getFollowersList(twitterNumericId, cursor);
                for (User user : pagableFollowers) 
                {
                    listFollowers.add(user); // ArrayList<User>
                }
            } while ((cursor = pagableFollowers.getNextCursor()) != 0);
			
			if ( (listFollowers != null) && !(listFollowers.isEmpty()) )
				return listFollowers ;
		} catch (TwitterException te) 
		{
			te.printStackTrace();
			logger.severe("Failed to get Followers Using Numeric Id : " + twitterNumericId +"  ::: Error :: " + te.getMessage());
		}
		return null ;
	}
	
	/**
	 * 
	 * @param screenName
	 * @return User Object of the Twitter 
	 */
	public User fetchUserInfoByScreenName(String twitterScreenName)
	{
		if ( (twitterScreenName == null) || (twitterScreenName.isEmpty()) )
			return null ;
		try
		{
			User user = twitter.showUser(twitterScreenName);
			if ( user != null )
				return user ;
		} catch (TwitterException te)
		{
			te.printStackTrace();
			logger.severe("Failed to get User Information Using ScreenName: " + twitterScreenName +"  ::: Error :: " + te.getMessage());
		}
		return null ;
	}
	
	/**
	 * 
	 * @param screenName
	 * @return User Object of the Twitter 
	 */
	public User fetchUserInfoByNumericId(long twitterNumericId)
	{
		if ( twitterNumericId == 0L )
			return null ;
		try
		{
			User user = twitter.showUser(twitterNumericId);
			if ( user != null )
				return user ;
		} catch (TwitterException te)
		{
			te.printStackTrace();
			logger.severe("Failed to get User Information Using Numeric Id : " + twitterNumericId +"  ::: Error :: " + te.getMessage());
		}
		return null ;
	}

	/**
	 * 
	 * @param screenName
	 * @return User Object List of the Twitter 
	 */
	public List<User> fetchUsersInfoByScreenNames(String[] twitterScreenNamesArray)
	{
		if ( (twitterScreenNamesArray == null) || (twitterScreenNamesArray.length < 1))
			return null ;
		try
		{
			
			ResponseList<User> users = twitter.lookupUsers(twitterScreenNamesArray);
			if (users != null)
			{
				List<User> usersList = new LinkedList<User>() ;
				for (User user :users)
				{
					if ( user != null )
						usersList.add(user) ;
				}
				if ( (usersList != null) && !(usersList.isEmpty()) )
					return usersList ;
			}
		
		} catch (TwitterException te)
		{
			te.printStackTrace();
			logger.severe("Failed to get User Information Using ScreenNames Array (printed Below) ::: Error :: " + te.getMessage());
			for ( String screenName : twitterScreenNamesArray )
				logger.info("Screen Name :: " +  screenName );
		}
		return null ;
	}
	
	/**
	 * 
	 * @param screenName
	 * @return User Object List of the Twitter 
	 */
	public List<User> fetchUsersInfoByNumericIds(long[] twitterNumericIdsArray)
	{
		if ( (twitterNumericIdsArray == null) || (twitterNumericIdsArray.length < 1) )
			return null ;
		try
		{
			
			ResponseList<User> users = twitter.lookupUsers(twitterNumericIdsArray);
			if (users != null)
			{
				List<User> usersList = new LinkedList<User>() ;
				for (User user :users)
				{
					if ( user != null )
						usersList.add(user) ;
				}
				if ( (usersList != null) && !(usersList.isEmpty()) )
					return usersList ;
			}
		
		} catch (TwitterException te)
		{
			te.printStackTrace();
			logger.severe("Failed to get User Information Using Numeric Ids Array (printed Below) ::: Error :: " + te.getMessage());
			for ( long id : twitterNumericIdsArray )
				logger.info("Screen Name :: " +  id );
		}
		return null ;
	}
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	public String fetchJSONStringUser( User user)
	{
		if ( user == null )
			return null ;
		try
		{
			String twitterUserInJsonStringFormat = TwitterObjectFactory.getRawJSON(user)  ;
			if ( (twitterUserInJsonStringFormat != null) && !(twitterUserInJsonStringFormat.isEmpty()) )
				return twitterUserInJsonStringFormat ;
		} catch (Exception e)
		{
			e.printStackTrace(); 
		}
		return null ;
	}
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	public List<String> fetchJSONStringUserList( List<User> usersListt)
	{
		if ( (usersListt == null) || (usersListt.isEmpty()) )
			return null ;
		try
		{
			List<String> jsonStringUserList = new LinkedList<String>() ;
			for ( User user : usersListt )
			{
				String jsonUserString = fetchJSONStringUser(user) ;
				if ( (jsonUserString != null) && !(jsonUserString.isEmpty()))
					jsonStringUserList.add(jsonUserString) ;
			}
			if ( (jsonStringUserList != null) && !(jsonStringUserList.isEmpty()) )
				return jsonStringUserList ;
		} catch (Exception e)
		{
			e.printStackTrace(); 
		}
		return null ;
	}
	
}
