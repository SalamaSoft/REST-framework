package com.salama.service.clouddata.auth;

import java.util.concurrent.ConcurrentHashMap;

import com.salama.service.clouddata.core.AuthUserInfo;

/**
 * 
 * @author XingGu Liu
 *
 */
public class UserSessionData {
	private String authTicket = null;
	
	public String getAuthTicket() {
		return authTicket;
	}

	public void setAuthTicket(String authTicket) {
		this.authTicket = authTicket;
	}

	private AuthUserInfo authUserInfo = null;

	private long loginTime = 0;

	private long sessionAccessTime = System.currentTimeMillis();

	private ConcurrentHashMap<String, String> sessionValueMap = null;

	public AuthUserInfo getAuthUserInfo() {
		return authUserInfo;
	}

	public void setAuthUserInfo(AuthUserInfo authUserInfo) {
		this.authUserInfo = authUserInfo;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	public long getSessionAccessTime() {
		return sessionAccessTime;
	}

	public void setSessionAccessTime(long sessionAccessTime) {
		this.sessionAccessTime = sessionAccessTime;
	}

	public ConcurrentHashMap<String, String> getSessionValueMap() {
		return sessionValueMap;
	}

	public void setSessionValueMap(
			ConcurrentHashMap<String, String> sessionValueMap) {
		this.sessionValueMap = sessionValueMap;
	}

}
