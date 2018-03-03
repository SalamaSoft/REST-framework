package com.salama.service.auth;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import MetoXML.XmlDeserializer;

import com.salama.service.auth.base.LoginEntryPoint;
import com.salama.service.auth.base.LoginUserService;
import com.salama.service.auth.base.LogoutEntryPoint;
import com.salama.service.auth.base.TicketManager;
import com.salama.service.auth.filter.UriAccessController;
import com.salama.service.core.context.CommonContext;
import com.salama.util.ClassLoaderUtil;

/**
 * 
 * @author XingGu Liu
 *
 */
public class AuthenticationContext implements CommonContext {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1255799061377030387L;

	private static final Logger logger = Logger.getLogger(AuthenticationContext.class);
	
	private com.salama.service.auth.config.Authentication _authenticationConfig = null;

	private Class<?> _loginEntryPointClass = null;
	private Class<?> _logoutEntryPointClass = null;
	private Class<?> _loginUserServiceClass = null;
	
	private UriAccessController _uriAccessController;
	private TicketManager _ticketManager = null;

	public AuthenticationContext() {
	}
	
	@Override
	public void reload(ServletContext servletContext, String configLocation) {
		//Load config
		String configFilePath = servletContext.getRealPath(configLocation);
		
		XmlDeserializer xmlDes = new XmlDeserializer();
		try {
			_authenticationConfig = (com.salama.service.auth.config.Authentication) xmlDes.Deserialize(
					configFilePath, com.salama.service.auth.config.Authentication.class, XmlDeserializer.DefaultCharset);

			logger.info("Config loaded:" + configLocation);
		} catch(Exception ex) {
			logger.error("reload()", ex);
		}
		
		try {
			_loginUserServiceClass = ClassLoaderUtil.getDefaultClassLoader().loadClass(
					_authenticationConfig.getLoginUserService().getClassName());
			
			_loginEntryPointClass = ClassLoaderUtil.getDefaultClassLoader().loadClass(
					_authenticationConfig.getLoginEntryPoint().getClassName());
			
			_logoutEntryPointClass = ClassLoaderUtil.getDefaultClassLoader().loadClass(
					_authenticationConfig.getLogoutEntryPoint().getClassName());
		} catch(Exception ex) {
			logger.error("reload()", ex);
		}
		
		//Init uriAccessController
		try {
			_uriAccessController = new UriAccessController(_authenticationConfig.getHttp());
			logger.info("UriAccessController inited");
		} catch(Exception ex) {
			logger.error("reload()", ex);
		}
	
		try {
		 	Class<?> ticketManagerClass = ClassLoaderUtil.getDefaultClassLoader().loadClass(
					_authenticationConfig.getTicketManager().getClassName());

		 	_ticketManager = (TicketManager) ticketManagerClass.newInstance();
			logger.info("TicketManager inited:" + ticketManagerClass.getName());
		} catch(Exception ex) {
			logger.error("reload()", ex);
		}
		
	}
	
	@Override
	public void destroy() {

	}
	
	public String getLoginUri() {
		return _authenticationConfig.getLoginEntryPoint().getUri();
	}
	
	public String getLogoutUri() {
		return _authenticationConfig.getLogoutEntryPoint().getUri();
	}
	
	public LoginUserService createLoginUserService() throws IllegalAccessException, InstantiationException {
		 return (LoginUserService)_loginUserServiceClass.newInstance();
	}

	public LoginEntryPoint createLoginEntryPoint() throws IllegalAccessException, InstantiationException {
		 return (LoginEntryPoint)_loginEntryPointClass.newInstance();
	}
	
	public LogoutEntryPoint createLogoutEntryPoint() throws IllegalAccessException, InstantiationException {
		 return (LogoutEntryPoint)_logoutEntryPointClass.newInstance();
	}
	
	public String getLoginFieldNameOfUserName() {
		return _authenticationConfig.getLoginFields().getUserName();
	}

	public String getLoginFieldNameOfPassword() {
		return _authenticationConfig.getLoginFields().getPassword();
	}
	
	public TicketManager getTicketManager() {
		return _ticketManager;
	}
	
	public UriAccessController getUriAccessController() {
		return _uriAccessController;
	}
	
	public String getInvalidSessionUri() {
		return _authenticationConfig.getHttp().getSession().getInvalidSessionUri();
	}
}
