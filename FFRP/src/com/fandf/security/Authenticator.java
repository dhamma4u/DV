package com.fandf.security;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.bson.Document;

import sds.service.PasswordService;

import com.fandf.commonUtil.ComponentUtil;
import com.fandf.commonUtil.MongoDBUtil;
import com.fandf.twitter.TwitterAnalysisBean;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

/**
 * 
 * @author SDS
 *
 */
@ManagedBean(name="Authenticator")
@SessionScoped
public class Authenticator {
	private String username = "" ;
	private String password = "" ;
	
	private boolean loggedInStatus = false ;
	private HttpSession session ;
	private String sessionId = "" ;

	private String userType = "" ;
	private Long id = 0L ;
	
	java.util.logging.Logger log = java.util.logging.Logger.getLogger(this.getClass().getName()) ;
	
/*--0-0-0-0-0-0-0-0-0-0-GETTERS AND SETERS BELOW THIS0-0-0-0-0-0-0-0-0-0-0-0-0-0--*/
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the loggedInStatus
	 */
	public boolean isLoggedInStatus() {
		return loggedInStatus;
	}

	/**
	 * @param loggedInStatus the loggedInStatus to set
	 */
	public void setLoggedInStatus(boolean loggedInStatus) {
		this.loggedInStatus = loggedInStatus;
	}
	
	/**
	 * @return the session
	 */
	public HttpSession getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(HttpSession session) {
		this.session = session;
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	/**
	 * @return the userType
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * @param userType the userType to set
	 */
	public void setUserType(String userType) {
		this.userType = userType;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	
/*--0-0-0-0-0-0-0-0-0-0-MEHODS ONLY BELOW THIS0-0-0-0-0-0-0-0-0-0-0-0-0-0--*/
	
	/**
	 * Creates New session on success of the login and persist is 
	 * @return String status of the User as 'loggedIn'
	 */
	public String login()
	{
		log.info("--------------**  FRIENDS AND FOLLOWERS TRACKER LOGIN STARTED  **----------------------");
		log.info("USERNAME :: " + username );
		log.info("PASSWORD :: " + PasswordService.getEncryptedPassword(password) );
		
		 authenticate(username, password) ;
		if(loggedInStatus)
		{
			FacesContext facesContext = FacesContext.getCurrentInstance();
			if(facesContext != null)
			{
				log.info("FACES CONTEXT  :: " + facesContext );
				session = (HttpSession) facesContext.getExternalContext().getSession(true);
				session.setAttribute("user", username);
				sessionId = session.getId() ;
				log.info("SESSION ID GENERATED :: " + session.getId());
				log.info("USER TYPE :: " + userType );
				log.info("USER ID :: " + id );
				log.info("--------------**  USER AUTHENTICATED. **----------------------");
				try
				{
					TwitterAnalysisBean tab = ComponentUtil.getInstance("twitterAnalysisBean");
					tab.fetchConfuredIdDataOfLoggedInUser( "" + id ) ;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			return "LoggedIn" ;
		}
		log.info("Authenication Failed..!!");
		return "" ;
	}
	
	/**
	 * This method is for internal process of Login. 
	 * @param username
	 * @param password
	 * @return
	 */
	private void authenticate(String username, String password) 
	{
		setLoggedInStatus(false);
		if( (username != null) && (password != null) && !(username.equalsIgnoreCase("")) && !(password.equalsIgnoreCase("")))
		{
			MongoDBUtil mongoDBUtil = new MongoDBUtil() ;
			try
			{
				MongoDatabase db = mongoDBUtil.getMongoDBInstace() ;
				System.out.println("DB Name :: " + db.getName());
				System.out.println("DB COLLECTION :: " + db.getCollection("LOGIN_DETAIL"));
				FindIterable<Document> iterable  = db.getCollection("LOGIN_DETAIL").find( new Document("USERNAME", username.toUpperCase()).append("PASSWORD", password));
				
				iterable.forEach(new Block<Document>() {
				    @Override
				    public void apply(final Document document) 
				    {
				    	log.info("Doc Direct APPEND doc Search :: " + document);
				        setLoggedInStatus(true);
				        setUserType(document.getString("TYPE"));
				        String preId = document.getDouble("ID") + "" ;
				        setId( Long.parseLong( preId.substring(0, preId.indexOf(".")) ) );
				        log.info("Logged IN sucess..");
				    } 
				});
				mongoDBUtil.closeDBInstace() ;
			}
			catch (Exception e)
			{
				mongoDBUtil.closeDBInstace() ;
				e.printStackTrace(); 
				setLoggedInStatus(false);
			}
		}
	}
	
	/**
	 * Make All Variables to its default state .
	 * Session get Invalidate .
	 * @return String status of Logout as 'loggedOut'
	 */
	public String logout()
	{
		log.info("Session Id :: " + sessionId );
		username = "" ;
		password = "" ;
		loggedInStatus = false ;
		sessionId = "" ;
		session.invalidate();
		log.info("session Invalidated.!");
		log.info("--------------** LOGOUT  **----------------------");
		return "loggedOut" ;
	}
	
	

}
