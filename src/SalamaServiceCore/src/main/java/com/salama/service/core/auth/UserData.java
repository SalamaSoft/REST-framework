package com.salama.service.core.auth;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface UserData extends Serializable {
	public List<GrantedAuthority> getAuthorities();
	
}
