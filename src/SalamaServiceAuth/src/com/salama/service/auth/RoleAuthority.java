package com.salama.service.auth;

import com.salama.service.core.auth.GrantedAuthority;

/**
 * 
 * @author XingGu Liu
 *
 */
public class RoleAuthority implements GrantedAuthority {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2353874937839830747L;
	
	private String _role = "";
	
	@Override
	public String getAuthority() {
		return _role;
	}

	public String getRole() {
		return _role;
	}

	public void setRole(String role) {
		_role = role;
	}
	
	
}
