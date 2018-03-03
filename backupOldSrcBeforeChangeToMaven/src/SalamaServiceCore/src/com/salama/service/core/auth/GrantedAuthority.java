package com.salama.service.core.auth;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface GrantedAuthority extends Serializable {
	public String getAuthority();
}
