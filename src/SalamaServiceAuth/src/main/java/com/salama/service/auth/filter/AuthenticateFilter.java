package com.salama.service.auth.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import MetoXML.Util.DataClassFinder;

import com.salama.service.auth.AuthenticateException;
import com.salama.service.auth.AuthenticationContext;
import com.salama.service.auth.base.LoginEntryPoint;
import com.salama.service.auth.base.LoginResult;
import com.salama.service.auth.base.LoginUserService;
import com.salama.service.auth.base.LogoutEntryPoint;
import com.salama.service.auth.base.LogoutResult;
import com.salama.service.auth.base.TicketStatus;
import com.salama.service.core.SimpleMetoXmlResult;
import com.salama.service.core.auth.Authentication;
import com.salama.service.core.context.ServiceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author XingGu Liu
 *
 */
public class AuthenticateFilter implements Filter {
	public final static String PARAM_NAME_AUTH_TICKET = "Salama_Service_Auth_Ticket";
	public final static String SESSION_KEY_ORIGINAL_URI_BEFORE_GOTO_LOGIN = "Salama_Service_Auth_Original_URI_Before_Goto_Login";

	private static final Log logger = LogFactory.getLog(AuthenticateFilter.class);

//	public final static String FILTER_PARAM_NAME_CONFIG_LOCATION= "config-location";

	protected final static String FILTER_ALREDY_EXCUTED = 
			"Excecuted:com.salama.service.auth.filter.SecurityFilter";

	protected static final String DefaultEncoding = "utf-8";
	
	protected static final Charset DefaultCharset = Charset.forName(DefaultEncoding);
	
	private String _filterName = "";
	
//	private String _configLocation = "";
	
//	private Class<?> _loginEntryPointClass = null;
//	private Class<?> _logoutEntryPointClass = null;
//	private Class<?> _loginUserServiceClass = null;
//	
	private LoginEntryPoint _loginEntryPoint = null;
	private LogoutEntryPoint _logoutEntryPoint = null;
//	private LoginUserService _loginUserService = null;

	private String _loginUri = "";
	private String _logoutUri = "";
	
	private AuthenticationContext _authenticationContext = null;
	
	public AuthenticateFilter() {
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		_filterName = config.getFilterName();
//		_configLocation = config.getInitParameter(FILTER_PARAM_NAME_CONFIG_LOCATION);
//		logger.info("filterName:" + _filterName + "; _configLocation:" + _configLocation);
		
		_authenticationContext = (AuthenticationContext) ServiceContext.getContext(
				config.getServletContext()).getContext(AuthenticationContext.class);

		_loginUri = _authenticationContext.getLoginUri();
		
		_logoutUri = _authenticationContext.getLogoutUri();
		
		//Init LoginEntry
		try {
			LoginUserService loginUserService = _authenticationContext.createLoginUserService();
			
			_loginEntryPoint = _authenticationContext.createLoginEntryPoint();
			_loginEntryPoint.init(loginUserService, 
					_authenticationContext.getLoginFieldNameOfUserName(), 
					_authenticationContext.getLoginFieldNameOfPassword() );

			
			_logoutEntryPoint = (LogoutEntryPoint)_authenticationContext.createLogoutEntryPoint();
			
			logger.info("Login, Logout entry points initiallized");
		} catch(Exception ex) {
			logger.error("init()", ex);
			throw new ServletException(ex);
		}
	
		logger.info(_filterName + " inited");
		
	}
	

	@Override
	public void destroy() {
		//Nothing
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		try {
			if(request.getAttribute(FILTER_ALREDY_EXCUTED) == null) {
				request.setAttribute(FILTER_ALREDY_EXCUTED, Boolean.TRUE);
				
				if(DataClassFinder.IsInterfaceType(request.getClass(), HttpServletRequest.class)) {
					doFilterHttp((HttpServletRequest)request, (HttpServletResponse)response, filterChain);
				} else {
					//do nothing for now
					filterChain.doFilter(request, response);
				}
			} else {
				//do nothing
				filterChain.doFilter(request, response);
			}
		} catch(IOException e) {
			logger.error("doFilter()", e);
			throw e;
		} catch(ServletException e) {
			logger.error("doFilter()", e);
			throw e;
		}
	}
	
	protected void doFilterHttp(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		
		String uri = ((HttpServletRequest)request).getRequestURI();
		uri = uri.substring(request.getContextPath().length());
		
		if(uri.equals(_loginUri)) {
			//login
			try {
				//Instance
				
				Authentication authentication = _loginEntryPoint.doLogin(request, response);

				if(authentication == null) {
					AuthenticateException e = new AuthenticateException("Login failed");
					httpOutputError(response, e);
				} else {
					//create ticket
					String ticket = _authenticationContext.getTicketManager().createNewTicket(authentication);
					authentication.getCredential().setTicket(ticket);
					
					//Store the authentication
					saveAuthenticationToHttpSession(request.getSession(), authentication);

					//output result
					LoginResult result = new LoginResult();
					result.setTicket(ticket);
					httpOutputResult(response, result, LoginResult.class);
				}
			} catch (AuthenticateException e) {
				httpOutputError(response, e);
			}
		} else if (uri.equals(_logoutUri)) {
			//logout
			Authentication authentication = getAuthenticationFromHttpSession(request.getSession());
			
			//delete ticket
			if(authentication != null) {
				String ticket = authentication.getCredential().getTicket();
				_authenticationContext.getTicketManager().deleteTicket(ticket);
			}

			LogoutResult result = new LogoutResult();
			result.setLogoutSessionId(request.getSession().getId());

			_logoutEntryPoint.doLogout(request, response);
			
			httpOutputResult(response, result, LogoutResult.class);
		} else {
			//Other uri
			Authentication authentication = getAuthenticationFromHttpSession(request.getSession());
			boolean isAccessible = false;

			//If ticket exists, report alive
			String ticket = getTicketFromRequest(request);
			if(ticket != null) {
				if(_authenticationContext.getTicketManager().isTicketValid(ticket)) {
					_authenticationContext.getTicketManager().reportAlive(ticket);
					
					if(authentication == null) {
						//autoLogin
						authentication = (Authentication) _authenticationContext.getTicketManager().getTicketUserObj(ticket);
						saveAuthenticationToHttpSession(request.getSession(), authentication);
					}
				} else {
					_authenticationContext.getTicketManager().deleteTicket(ticket);
				}
			} else {
				//auto logout because ticket is null
				/*
				authentication = null;
				saveAuthenticationToHttpSession(request.getSession(), authentication);
				*/
			}
			
			if(authentication == null) {
				isAccessible = _authenticationContext.getUriAccessController().isAccessible(uri, null);
			} else {
				isAccessible = _authenticationContext.getUriAccessController().isAccessible(
						uri, authentication.getUserData().getAuthorities());
			}
			
			if(isAccessible) {
				doFilter(request, response, filterChain);
			} else {
				String invalidSessionUri = _authenticationContext.getInvalidSessionUri().trim();
				if((authentication == null)
						&& invalidSessionUri.length() > 0) {
					logger.debug("Forward to:" + invalidSessionUri);
					//request.getRequestDispatcher(invalidSessionUri).forward(request, response);
					
					request.getSession().setAttribute(SESSION_KEY_ORIGINAL_URI_BEFORE_GOTO_LOGIN, 
							request.getRequestURI() + "?" +  request.getQueryString());
					
					response.sendRedirect(request.getContextPath() + invalidSessionUri);
				} else {
					AuthenticateException e = new AuthenticateException("No authority to accecss");
					httpOutputError(response, e);
				}
			}
		}
	}
	
	protected void saveAuthenticationToHttpSession(HttpSession session, Authentication authentication) {
		logger.debug("saveAuthenticationToHttpSession sessionId:" + session.getId() + " authentication:" + authentication);
		session.setAttribute(session.getId(), authentication);
	}
	
	protected Authentication getAuthenticationFromHttpSession(HttpSession session) {
		return (Authentication) session.getAttribute(session.getId());
	}
	
	protected void httpOutputResult(HttpServletResponse response, Object result, Class<?> resultType) {
		String resultXml = SimpleMetoXmlResult.resultToXml(
				result, resultType, DefaultCharset, false, false);
		httpOutput(response, resultXml);
	}

	protected void httpOutputError(HttpServletResponse response, Exception error) {
		String resultXml = SimpleMetoXmlResult.errorResultXml(error);
		httpOutput(response, resultXml);
		
		logger.debug("httpOutputError():\r\n" + resultXml);
	}
	
	protected String getTicketFromRequest(HttpServletRequest request) {
		//check parameter
		String ticket = request.getParameter(PARAM_NAME_AUTH_TICKET);
		if(ticket != null && ticket.length() > 0) {
			return ticket;
		}
		
		//check cookie
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(int i = 0; i < cookies.length; i++) {
				if(PARAM_NAME_AUTH_TICKET.equals(cookies[i].getName())) {
					return cookies[i].getValue();
				}
			}
		}
		
		return null;
	}
	
	protected static void httpOutput(HttpServletResponse response, String content) {
		PrintWriter output = null;

		try {
			output = response.getWriter();
			
			output.print(content);
			
			output.flush();
		} catch(IOException e) {
			logger.error(e);
		} finally {
			try {
				output.close();
			} catch(Exception e) {
			}
		}
	}
	
}
