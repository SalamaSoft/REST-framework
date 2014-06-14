package com.salama.service.auth.base;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.salama.service.auth.AuthenticateException;
import com.salama.service.core.auth.Authentication;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface LoginEntryPoint {
	
	public void init(LoginUserService loginUserService,
			String fieldNameOfUserName, String fieldNameOfPassword);
	
	public Authentication doLogin(ServletRequest request, ServletResponse response) 
	throws AuthenticateException;
}
