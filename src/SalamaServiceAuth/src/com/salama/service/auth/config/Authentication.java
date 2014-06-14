package com.salama.service.auth.config;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class Authentication implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7569563014668039653L;

	private ClassSetting _loginUserService = new ClassSetting();
	
	private EntryPoint _loginEntryPoint = new EntryPoint();
	
	private LoginFields _loginFields = new LoginFields();
	
	private EntryPoint _logoutEntryPoint = new EntryPoint();

	private HttpAuthentication _http = new HttpAuthentication();
	
	private TicketManager  _ticketManager = new TicketManager();
	
	public Authentication() {
		//Default config
		_loginUserService.setClassName("TodoTodoTodoTodoTodoTodoTodoTodo");
		
		_loginEntryPoint.setUri("/login.do");
		_loginEntryPoint.setClassName("com.salama.service.auth.DefaultLoginEntryPoint");

		_logoutEntryPoint.setUri("/logout.do");
		_logoutEntryPoint.setClassName("com.salama.service.auth.DefaultLogoutEntryPoint");
		
		_loginFields.setUserName("userName");
		_loginFields.setPassword("password");

		_ticketManager.setClassName("com.salama.service.auth.DefaultTicketManager");
		_ticketManager.setIdleTimeoutSeconds(1800);
	}
	
	public TicketManager getTicketManager() {
		return _ticketManager;
	}

	public void setTicketManager(TicketManager ticketManager) {
		_ticketManager = ticketManager;
	}

	public EntryPoint getLoginEntryPoint() {
		return _loginEntryPoint;
	}

	public void setLoginEntryPoint(EntryPoint loginEntryPoint) {
		_loginEntryPoint = loginEntryPoint;
	}

	public EntryPoint getLogoutEntryPoint() {
		return _logoutEntryPoint;
	}

	public void setLogoutEntryPoint(EntryPoint logoutEntryPoint) {
		_logoutEntryPoint = logoutEntryPoint;
	}

	public LoginFields getLoginFields() {
		return _loginFields;
	}

	public void setLoginFields(LoginFields loginFields) {
		_loginFields = loginFields;
	}

	public HttpAuthentication getHttp() {
		return _http;
	}

	public void setHttp(HttpAuthentication http) {
		_http = http;
	}
	
	public ClassSetting getLoginUserService() {
		return _loginUserService;
	}

	public void setLoginUserService(ClassSetting loginUserService) {
		_loginUserService = loginUserService;
	}
	
}
