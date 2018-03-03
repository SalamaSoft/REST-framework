package com.salama.service.auth;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


import MetoXML.Util.DataClassFinder;

import com.salama.service.auth.base.LoginEntryPoint;
import com.salama.service.auth.base.LoginUserService;
import com.salama.service.core.auth.Authentication;
import com.salama.service.core.auth.Credential;
import com.salama.service.core.auth.RemoteClientInfo;
import com.salama.service.core.auth.UserData;

/**
 * 
 * @author XingGu Liu
 *
 */
public class DefaultLoginEntryPoint implements LoginEntryPoint {

	protected String _fieldNameOfUserName; 
	protected String _fieldNameOfPassword;
	protected LoginUserService _loginUserService = null;

	public DefaultLoginEntryPoint()  {
	}

	@Override
	public void init(LoginUserService loginUserService,
			String fieldNameOfUserName, String fieldNameOfPassword)
	{
		_fieldNameOfUserName = fieldNameOfUserName; 
		_fieldNameOfPassword = fieldNameOfPassword;
		_loginUserService = loginUserService;
	}	
	
	@Override
	public Authentication doLogin(ServletRequest request,
			ServletResponse response) throws AuthenticateException {
		String userName = request.getParameter(_fieldNameOfUserName);
		String password = request.getParameter(_fieldNameOfPassword);
		
		Authentication authentication = new Authentication();
		
		UserData userData = _loginUserService.authenticate(request, response, userName, password);
		
		Credential credential = new Credential();
		
		RemoteClientInfo remoteClientInfo = new RemoteClientInfo();
		remoteClientInfo.setRemoteAddress(request.getRemoteAddr());
		
		if(DataClassFinder.IsInterfaceType(request.getClass(), HttpServletRequest.class)) {
			remoteClientInfo.setSessionId(((HttpServletRequest) request).getSession().getId());
		} else {
			remoteClientInfo.setSessionId(credential.getTicket());
		}
		
		authentication.setUserData(userData);
		authentication.setCredential(credential);
		authentication.setRemoteClient(remoteClientInfo);
		
		return authentication;
	}
	
}
