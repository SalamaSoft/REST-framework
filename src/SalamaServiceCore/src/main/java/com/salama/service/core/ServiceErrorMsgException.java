package com.salama.service.core;

import java.util.Hashtable;
import java.util.Properties;

import com.salama.util.ResourceUtil;
import com.salama.util.StringUtil;

/**
 * 
 * @author XingGu Liu
 *
 */
public class ServiceErrorMsgException extends Exception {
	
	//private static final String PROPERTIES_FILE_EXT = ".properties";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8274296876367399668L;

	/**
	 * Key: Clazz.getName()
	 * Value: properties
	 */
	//private static Hashtable<String, Properties> msgPropertiesMap = new Hashtable<String, Properties>();
	
	public ServiceErrorMsgException() {
		super();
	}
	public ServiceErrorMsgException(String errorMsg) {
		super(errorMsg);
	}
	
	public ServiceErrorMsgException(Throwable e) {
		super(e);
	}
	
	public ServiceErrorMsgException(String errorMsg, Throwable e) {
		super(errorMsg, e);
	}
	

	public ServiceErrorMsgException(String errorMsg, String... msgArgs) {
		super(StringUtil.formatString(errorMsg, msgArgs));
	}
	
	public ServiceErrorMsgException(Class<?> clazz, String msgKey, String... msgArgs) {
		super(getMsg(clazz, msgKey, msgArgs));
	}
	
	private static String getMsg(Class<?> clazz, String msgKey, String... msgArgs) {
		Properties prop = null;

		/*
		String propertiesKey = getPropertiesKey(clazz);
		if(msgPropertiesMap.containsKey(propertiesKey)) {
			prop = msgPropertiesMap.get(propertiesKey);
		} else {
			prop = ResourceUtil.getProperties(clazz, clazz.getSimpleName() + ".properties");
			msgPropertiesMap.put(propertiesKey, prop);
		}
		*/
		prop = ResourceUtil.getProperties(clazz, clazz.getSimpleName() + ".properties");

		String msgPattern = ResourceUtil.getPropValue(prop, msgKey);
		
		return StringUtil.formatString(msgPattern, msgArgs);
	}
	
	private static String getPropertiesKey(Class<?> clazz) {
		return clazz.getName();
	}
}
