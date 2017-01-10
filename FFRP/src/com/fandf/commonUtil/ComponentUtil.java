package com.fandf.commonUtil;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

public class ComponentUtil {

	private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(ComponentUtil.class.getName()); 
	public ComponentUtil()
	{
		super() ;
	}
	
	
	@SuppressWarnings("unchecked")
	public static synchronized <T> T getInstance(String managedBeanName) 
	{
		log.info("Checking the Instance is Availabel Or Not .. for ManagedBean: " +managedBeanName );
	    FacesContext facesContext = FacesContext.getCurrentInstance();
	    if(facesContext != null)
	    {
	    	// return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
	    	if( (T) facesContext.getExternalContext().getSessionMap().get(managedBeanName) != null )
	    	{
	    		log.info("ManagedBean : " + (T) facesContext.getExternalContext().getSessionMap().get(managedBeanName) );
	    		return (T) facesContext.getExternalContext().getSessionMap().get(managedBeanName) ;
	    	}
	    	else if( (T) facesContext.getELContext().getELResolver().getValue(facesContext.getELContext(), null,managedBeanName) != null)
	    	{
	    		log.info("ManagedBean 2 : " + ((T) facesContext.getELContext().getELResolver().getValue(facesContext.getELContext(), null,managedBeanName)));
	    		return (T) facesContext.getELContext().getELResolver().getValue(facesContext.getELContext(), null,managedBeanName);
	    	}else if((T)facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(), "#{managedBeanName}", Object.class).getValue(facesContext.getELContext()) != null)
	    	{
	    		log.info("ManagedBean 3 : " + (T)facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(), "#{managedBeanName}", Object.class).getValue(facesContext.getELContext()));
	    		return (T)facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(), "#{managedBeanName}", Object.class).getValue(facesContext.getELContext());
	    	}else if((T)facesContext.getApplication().evaluateExpressionGet(facesContext, "#{managedBeanName}", Object.class) != null)
	    	{
	    		log.info("ManagedBean 4 : " + (T)facesContext.getApplication().evaluateExpressionGet(facesContext, "#{managedBeanName}", Object.class));
	    		return (T)facesContext.getApplication().evaluateExpressionGet(facesContext, "#{managedBeanName}", Object.class);
	    	}else{
	    		log.info("Unabel to get ManagedBean instance..!! return null ");
	    		return null;
	    	}
	    }
	    else 
	    {
	    	log.info("Unabel to get faces context..!! return null ");
	    	return null; 
		}
	}
	
	protected Application getApplication(FacesContext facesContext)
	{
	    return facesContext.getApplication();       
	}
	@SuppressWarnings("deprecation")
	protected Object getManagedBean(String beanName, FacesContext facesContext)
	{       
	    return getApplication(facesContext).getVariableResolver().resolveVariable(facesContext, beanName);
	}
		
}
