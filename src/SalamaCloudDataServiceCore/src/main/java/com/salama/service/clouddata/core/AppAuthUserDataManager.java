package com.salama.service.clouddata.core;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface AppAuthUserDataManager {
	public void setAppId(String appId);
	
	public void setBackupDirPath(String backupDirPath);
	
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

	public void backupAllData() throws AppException;
	
	public void restoreAllData() throws AppException;
}
