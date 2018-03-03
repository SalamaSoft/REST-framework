package com.salama.service.auth.base;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface LogoutEntryPoint {
	public void doLogout(ServletRequest request, ServletResponse response);
}
