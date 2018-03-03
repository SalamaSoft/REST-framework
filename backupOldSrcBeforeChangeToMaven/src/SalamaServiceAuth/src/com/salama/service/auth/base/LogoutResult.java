package com.salama.service.auth.base;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class LogoutResult implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5679394047027919735L;
	
	private String logoutSessionId = "";

	public String getLogoutSessionId() {
		return logoutSessionId;
	}

	public void setLogoutSessionId(String logoutSessionId) {
		this.logoutSessionId = logoutSessionId;
	}
	
	
}
