/**
 * 
 */
package com.fandf.twitter;





/**
 * @author SDS
 *
 */
public class TestTwitterClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	//	TwitterInformationProcessor tffp = new TwitterInformationProcessor() ;
/*		System.out.println("--------------------Followers Ritesh.----------");
		System.out.println(""+  tffp.fetchFollowersByScreenName("Ritesh1307")  ); //
		System.out.println("--------------------Friends/Followings Ritesh.----------");
		System.out.println(""+  tffp.fetchFriendsByScreenName("Ritesh1307")  ); //
*/
/*		System.out.println("--------------------Followers Amit.----------");
		System.out.println(""+ tffp.fetchJSONStringUserList( tffp.fetchFollowersByScreenName("@Vishwajit999") )  ); //
		System.out.println("--------------------Friends/Followings Amit.----------");
		System.out.println(""+  tffp.fetchJSONStringUserList(tffp.fetchFriendsByScreenName("@Vishwajit999") )  ); //
		System.out.println("--------------------User Information of Amit.----------");
		System.out.println(""+ tffp.fetchJSONStringUser( tffp.fetchUserInfoByScreenName("vishwajit999") ) ); //
*/		 
/*		TwitterAnalysisBean tab = new TwitterAnalysisBean() ;
		tab.setSearchScreenName("ameet5488");
		tab.processTwitterFriendsFollowers(); 
		System.out.println( "SDS "+ tab.getTwitterDetailObject() ) ;*/
		TwitterDataIngestionProcessor tdip = new TwitterDataIngestionProcessor() ;
		tdip.processConfuredIdDataFetching(); 
		tdip.processDataValidation();
		/*String sampleTwitterDate = "Sat Jan 10 17:18:56 +0000 2009" ;
		System.out.println(" Date From Util TW :: " + DateUtils.getFormatedDate(sampleTwitterDate, DateUtils.TWITTER_DATE_FORMAT, DateUtils.DISPLAY_DATE_FORMAT));
		System.out.println(" Date :: " + new Date() );
		Date toda = new Date() ;
		System.out.println(" Date From Util IST :: " + DateUtils.getFormatedDate(toda+"", DateUtils.IST_DATE_FORMAT, DateUtils.DISPLAY_DATE_FORMAT));
		System.out.println(" Date From Util D IST :: " + DateUtils.getFormatedDate(toda, DateUtils.IST_DATE_FORMAT, DateUtils.DISPLAY_DATE_FORMAT));
	printSystemTimeInMillis(); 	*/
	}
	
	static public void printSystemTimeInMillis()
	{
		System.out.println("Current time in Millis ::  " +  System.currentTimeMillis() );
	}
	

}
