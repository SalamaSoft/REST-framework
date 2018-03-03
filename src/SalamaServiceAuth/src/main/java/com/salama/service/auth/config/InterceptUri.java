package com.salama.service.auth.config;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class InterceptUri implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7311977358866889314L;

	private String _pattern = "";
	
	private String _accessRoles="";

	public String getPattern() {
		return _pattern;
	}

	public void setPattern(String pattern) {
		_pattern = pattern;
	}

	public String getAccessRoles() {
		return _accessRoles;
	}

	public void setAccessRoles(String accessRoles) {
		_accessRoles = accessRoles;
	}
	
	
}
