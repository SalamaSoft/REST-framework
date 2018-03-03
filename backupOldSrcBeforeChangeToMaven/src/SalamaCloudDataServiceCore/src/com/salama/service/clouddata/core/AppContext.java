package com.salama.service.clouddata.core;

import java.sql.Connection;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface AppContext {

	/**
	 * Get the DB connection for the current app.
	 * @return
	 */
	public Connection getDBConnection() throws AppException;
	
	/**
	 * Get the appId of current context
	 * @return
	 */
	public String getAppId();
	
	
	/***** These are the methods about authentication -----> *****/
	/**
	 * If there already exists the authTicket which belongs to the userId, the old one will be replaced by the new one.
	 * @param role
	 * @param userId
	 * @return
	 */
	public String allocateAuthTicket(String role, String userId, long expiringTime) throws AppException;
	
	public boolean isAuthTicketValid(String authTicket) throws AppException;
	
	public AuthUserInfo getAuthUserInfo(String authTicket) throws AppException;
	
	public void updateAuthInfo(String authTicket, String role, long expiringTime) throws AppException;
	
	public void deleteAuthInfo(String authTicket) throws AppException;
	
	/*****  <----- *****/

	/**
	 * get value from session.(It may not be HttpSession, may be the remote session server)
	 * @param key
	 * @return
	 */
	public String getSessionValue(String authTicket, String key) throws AppException;
	
	/**
	 * set value from session.(It may not be HttpSession, may be the remote session server)
	 * @param key
	 * @param value
	 */
	public String setSessionValue(String authTicket, String key, String value) throws AppException;
	public String removeSessionValue(String authTicket, String key) throws AppException;

	/**
	 * Convert object to XML
	 * @param obj
	 * @param clazz
	 * @return
	 */
	public String objectToXml(Object obj, Class<?> clazz) throws AppException;
	
	public Object xmlToObject(String objXml, Class<?> clazz) throws AppException;
	
	public Class<?> findClass(String className);
}
