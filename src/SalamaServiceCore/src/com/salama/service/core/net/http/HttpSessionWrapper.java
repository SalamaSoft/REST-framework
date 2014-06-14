package com.salama.service.core.net.http;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.salama.service.core.net.SessionWrapper;

/**
 * 
 * @author XingGu Liu
 *
 */
public class HttpSessionWrapper implements SessionWrapper {
	private HttpSession _session = null;
	
	public HttpSessionWrapper(HttpSession session) {
		_session = session;
	}
	
	@Override
	  public long getCreationTime() {
		  return _session.getCreationTime();
	  }
	  
	@Override
	  public String getId() {
		  return _session.getId();
	  }
	  
	@Override
	  public long getLastAccessedTime() {
		  return _session.getLastAccessedTime();
	  }

	@Override
	  public ServletContext getServletContext() {
		  return _session.getServletContext();
	  }
	  
	@Override
	  public void setMaxInactiveInterval(int interval) {
		  _session.setMaxInactiveInterval(interval);
	  }
	  
	@Override
	  public int getMaxInactiveInterval() {
		  return _session.getMaxInactiveInterval();
	  }
	  
	@Override
	  public Object getAttribute(String name) {
		  return _session.getAttribute(name);
	  }
	  
	@Override
	  public Enumeration<String> getAttributeNames() {
		  return _session.getAttributeNames();
	  }
	  
	@Override
	  public void setAttribute(String name, Object value) {
		  _session.setAttribute(name, value);
	  }
	  
	@Override
	  public void removeAttribute(java.lang.String name) {
		  _session.removeAttribute(name);
	  }
	  
	@Override
	  public void invalidate() {
		  _session.invalidate();
	  }
	  
	@Override
	  public boolean isNew() {
		  return _session.isNew();
	  }
	
}
