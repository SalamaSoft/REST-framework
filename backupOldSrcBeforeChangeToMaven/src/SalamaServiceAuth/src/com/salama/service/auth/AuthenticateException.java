package com.salama.service.auth;

import java.util.Properties;

import com.salama.util.ResourceUtil;
import com.salama.util.StringUtil;

/**
 * 
 * @author XingGu Liu
 *
 */
public class AuthenticateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8735387590794689632L;

	public AuthenticateException() {
		super();
	}
	
	public AuthenticateException(String errorMsg) {
		super(errorMsg);
	}
	
	public AuthenticateException(Throwable e) {
		super(e);
	}
	
	public AuthenticateException(String errorMsg, Throwable e) {
		super(errorMsg, e);
	}
	
	public AuthenticateException(Class<?> clazz, String msgKey, String... msgArgs) {
		super(getMsg(clazz, msgKey, msgArgs));
	}
	
	protected static String getMsg(Class<?> clazz, String msgKey, String... msgArgs) {
		Properties prop = ResourceUtil.getProperties(clazz, clazz.getSimpleName() + ".properties");

		String msgPattern = ResourceUtil.getPropValue(prop, msgKey);
		
		return StringUtil.formatString(msgPattern, msgArgs);
	}
	
}
