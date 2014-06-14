package com.salama.service.clouddata.core;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class AuthUserInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8117689189892711568L;
	
	private String userId;
	
	private String role;
	
	private long expiringTime = 0;

	public long getExpiringTime() {
		return expiringTime;
	}

	public void setExpiringTime(long expiringTime) {
		this.expiringTime = expiringTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
