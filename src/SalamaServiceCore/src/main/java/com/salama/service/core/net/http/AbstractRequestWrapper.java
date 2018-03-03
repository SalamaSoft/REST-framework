package com.salama.service.core.net.http;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import com.salama.service.core.net.RequestWrapper;
import com.salama.service.core.net.SessionWrapper;

/**
 * 
 * @author XingGu Liu
 *
 */
public class AbstractRequestWrapper implements RequestWrapper {
	protected ServletRequest _request = null;
	protected SessionWrapper _session = null;

	public AbstractRequestWrapper(ServletRequest request, SessionWrapper session) {
		_request = request;
		_session = session;
	}
	
	@Override
	public ServletRequest getRequest() {
		return _request;
	}
	
	@Override
	public boolean isMultipartContent() {
		return false;
	}
	
	@Override
	public SessionWrapper getSession() {
		return _session;
	}
	
	@Override
	public Object getAttribute(String name) {
		return _request.getAttribute(name);
	} 
	
	@Override
	public Enumeration<String> getAttributeNames() {
		return _request.getAttributeNames();
	}
	
	@Override
	public String getCharacterEncoding() {
		return _request.getCharacterEncoding();
	} 
	
	@Override
	public int getContentLength() {
		return _request.getContentLength();
	}
	
	@Override
	public String getContentType() {
		return _request.getContentType();
	}
	
	@Override
	public String getLocalAddr() {
		return _request.getLocalAddr();
	}
	
	@Override
	public Locale getLocale() {
		return _request.getLocale();
	}
	
	@Override
	public String getLocalName() {
		return _request.getLocalName();
	}
	
	@Override
	public int getLocalPort() {
		return _request.getLocalPort();
	}
	
	@Override
	public String getParameter(String name) {
		return _request.getParameter(name);
	} 
	
	@Override
	public Enumeration<String> getParameterNames() {
		return _request.getParameterNames();
	}
	
	@Override
	public String getProtocol() {
		return _request.getProtocol();
	}
	
	@Override
	public String getRemoteAddr() {
		return _request.getRemoteAddr();
	}
	
	@Override
	public String getRemoteHost() {
		return _request.getRemoteHost();
	}
	
	@Override
	public int getRemotePort() {
		return _request.getRemotePort();
	}
	
	@Override
	public String getScheme() {
		return _request.getScheme();
	}
	
	@Override
	public String getServerName() {
		return _request.getServerName();
	}
	
	@Override
	public int getServerPort() {
		return _request.getServerPort();
	}
	
	@Override
	public ServletContext getServletContext() {
		return _request.getServletContext();
	}
	
	@Override
	public boolean isSecure() {
		return _request.isSecure();
	}
	
	@Override
	public void removeAttribute(String name) {
		_request.removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		_request.setAttribute(name, value);
	}
	
	@Override
	public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
		_request.setCharacterEncoding(encoding);
	}
	
}
