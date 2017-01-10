/**
 * 
 */
package com.fandf.security;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import com.fandf.commonUtil.ComponentUtil;

/**
 * @author SDS
 *
 */
public class FFTrackerPhaseListener  implements PhaseListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6766047996650679610L;
	
	// The phase where the listener is going to be called 
	private PhaseId phaseId = PhaseId.RENDER_RESPONSE; 
	
	
	@Override
	public void afterPhase(PhaseEvent arg0) {
		System.out.println("After Phase : ");
	}

	@Override
	public void beforePhase(PhaseEvent event) 
	{
		System.out.println("Before Phase "); 
		FacesContext fc = event.getFacesContext();
		// Check to see if they are on the login page.
		boolean status = validateUserAndProcessFurther() ;
		boolean loginPage = fc.getViewRoot().getViewId().lastIndexOf("index") > -1 ? true : false;
		if (!loginPage && !status)
		{
			System.out.println("Handling Request .. Navigating to Login page " );
			NavigationHandler nh = fc.getApplication().getNavigationHandler();
			nh.handleNavigation(fc, null, "loggedOut");
		}
	}

	@Override
	public PhaseId getPhaseId() {
		return phaseId;
	}

	private boolean validateUserAndProcessFurther()
	{
		boolean status = false ;
		try
		{
			Authenticator authenticator = ComponentUtil.getInstance("Authenticator") ;
			if (authenticator != null)
			{
				if ( authenticator.isLoggedInStatus() )
					status = true ;
			}
		} catch (Exception e)
		{
			e.printStackTrace(); 
		}
		return status  ;
	}



}
