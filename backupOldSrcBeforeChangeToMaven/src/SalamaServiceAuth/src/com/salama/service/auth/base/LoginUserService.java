package com.salama.service.auth.base;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.salama.service.auth.AuthenticateException;
import com.salama.service.core.auth.UserData;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface LoginUserService {
	public UserData authenticate(ServletRequest request, ServletResponse response,
			String userName, String userPassword) throws AuthenticateException;
}
