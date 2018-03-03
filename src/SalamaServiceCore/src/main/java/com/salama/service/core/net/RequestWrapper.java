package com.salama.service.core.net;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface RequestWrapper {
	
	public ServletRequest getRequest();
	
	public boolean isMultipartContent();
	
	public SessionWrapper getSession();
	
	public Object getAttribute(String name);
	
	public Enumeration<String> getAttributeNames();
	
	public String getCharacterEncoding();
	
	public int getContentLength();
	
	public String getContentType();
	
	public String getLocalAddr();
	
	public Locale getLocale();
	
	public String getLocalName();
	
	public int getLocalPort();
	
	public String getParameter(String name);
	
	public Enumeration<String> getParameterNames();
	
	public String getProtocol() ;
	
	public String getRemoteAddr();
	
	public String getRemoteHost();
	
	public int getRemotePort();
	
	public String getScheme();
	
	public String getServerName();
	
	public int getServerPort();
	
	public ServletContext getServletContext();
	
	public boolean isSecure();
	
	public void removeAttribute(String name);

	public void setAttribute(String name, Object value);
	
	public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException;
	
}
