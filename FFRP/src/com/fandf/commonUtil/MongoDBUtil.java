/**
 * 
 */
package com.fandf.commonUtil;

import java.util.Arrays;
import java.util.ResourceBundle;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

/**
 * @author SDS
 *
 */
public class MongoDBUtil {

	private  ResourceBundle resources = ResourceBundle.getBundle("dbResources") ;
	private  int mongoDBPort = Integer.parseInt(resources.getString("mongoDBPort")) ;
	private  String mongoDBHost = resources.getString("mongoDBHost")  ;
	private  String mongoDBDatabaseName = resources.getString("mongoDBDatabaseName") ;
	private  String mongoDBUserName = resources.getString("mongoDBUserName") ;
	private  String mongoDBUserPassword =  resources.getString("mongoDBUserPassword") ;
	private MongoClient mongoClient = null ;
	java.util.logging.Logger log = java.util.logging.Logger.getLogger(this.getClass().getName()) ;

/*--------------------------Getter and Setter Below This line--------------------------*/
	/**
	 * @return the mongoDBPort
	 */
	public int getMongoDBPort() {
		return mongoDBPort;
	}


	/**
	 * @param mongoDBPort the mongoDBPort to set
	 */
	public void setMongoDBPort(int mongoDBPort) {
		this.mongoDBPort = mongoDBPort;
	}


	/**
	 * @return the mongoDBHost
	 */
	public  String getMongoDBHost() {
		return mongoDBHost;
	}


	/**
	 * @param mongoDBHost the mongoDBHost to set
	 */
	public  void setMongoDBHost(String mongoDBHost) {
		this.mongoDBHost = mongoDBHost;
	}


	/**
	 * @return the mongoDBDatabaseName
	 */
	public  String getMongoDBDatabaseName() {
		return mongoDBDatabaseName;
	}


	/**
	 * @param mongoDBDatabaseName the mongoDBDatabaseName to set
	 */
	public  void setMongoDBDatabaseName(String mongoDBDatabaseName) {
		this.mongoDBDatabaseName = mongoDBDatabaseName;
	}


	/**
	 * @return the mongoDBUserName
	 */
	public  String getMongoDBUserName() {
		return mongoDBUserName;
	}


	/**
	 * @param mongoDBUserName the mongoDBUserName to set
	 */
	public  void setMongoDBUserName(String mongoDBUserName) {
		this.mongoDBUserName = mongoDBUserName;
	}


	/**
	 * @return the mongoDBUserPassword
	 */
	public  String getMongoDBUserPassword() {
		return mongoDBUserPassword;
	}


	/**
	 * @param mongoDBUserPassword the mongoDBUserPassword to set
	 */
	public  void setMongoDBUserPassword(String mongoDBUserPassword) {
		this.mongoDBUserPassword = mongoDBUserPassword;
	}
/*------------555555555555555 Method Only Below This 11111111-----------------*/
	
	/**
	 * Creates the Instance of the Mongo Client  
	 * @return database of the Mongo. 
	 */
	public MongoDatabase getMongoDBInstace()
	{
		try
		{
			MongoCredential credential = MongoCredential.createCredential(getMongoDBUserName(), getMongoDBDatabaseName(),getMongoDBUserPassword().toCharArray());
			mongoClient = new MongoClient(new ServerAddress(getMongoDBHost(), getMongoDBPort()), Arrays.asList(credential));
			MongoDatabase db = mongoClient.getDatabase(getMongoDBDatabaseName()) ;
			log.info("Instace Activated! Mongo DB Name :: " + db.getName());
			return db ;
		} catch (Exception e)
		{
			closeDBInstace() ;
			e.printStackTrace(); 
		}
		return null ;
	}
	
	
	/**
	 * Very mandatory method to close the active instance of the Mongo Database 
	 * @return
	 */
	public boolean closeDBInstace()
	{
		try
		{
			if ( mongoClient != null)
			{
				mongoClient.close() ;
				log.info("DB instance properly Closed..!");
			}
		} catch ( Exception e)
		{
			if ( mongoClient != null)
			{
				mongoClient.close() ;
				log.info("DB instance properly Closed..!");
			}
			e.printStackTrace(); 
		}
		return true;
	}
	
	

}
