package com.salama.service.auth.config;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class LoginFields implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7003480573591124537L;

	private String _userName = "";
	
	private String _password = "";

	public String getUserName() {
		return _userName;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	public String getPassword() {
		return _password;
	}

	public void setPassword(String password) {
		_password = password;
	}
	
	
}
