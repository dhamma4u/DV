package com.fandf.locale;


import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
/**
 * 
 * @author SDS
 *
 */

@ManagedBean(name = "localeManager")
public class LocaleManager {
/*------VARIABLES--------*/
	private String locale = "" ;
	private LinkedHashMap<String, String> languageMap = new LinkedHashMap<String,String>() ;
	
/*--------GETTER AND SETTER---------------*/
	/**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}
	/**
	 * @param locale the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}
	/**
	 * @return the languageMap
	 */
	public LinkedHashMap<String, String> getLanguageMap() {
		return languageMap;
	}
	/**
	 * @param languageMap the languageMap to set
	 */
	public void setLanguageMap(LinkedHashMap<String, String> languageMap) {
		this.languageMap = languageMap;
	}
/*--------------METHODS ONLY BELOW THIS-------------------*/
}
