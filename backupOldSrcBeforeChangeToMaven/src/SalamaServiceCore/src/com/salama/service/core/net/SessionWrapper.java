package com.salama.service.core.net;

import java.util.Enumeration;

import javax.servlet.ServletContext;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface SessionWrapper {
	  public long getCreationTime();
	  
	  /**
	   * 
	   * @return
	   */
	  public String getId();
	  
	  /**
	   * 
	   * @return
	   */
	  public long getLastAccessedTime();

	  /**
	   * 
	   * @return
	   */
	  public ServletContext getServletContext();
	  
	  /**
	   * 
	   * @param interval
	   */
	  public void setMaxInactiveInterval(int interval);
	  
	  /**
	   * 
	   * @return
	   */
	  public int getMaxInactiveInterval();

	  /**
	   * 
	   * @param name
	   * @return
	   */
	  public Object getAttribute(String name);
	  
	  /**
	   * 
	   * @return
	   */
	  public Enumeration<String> getAttributeNames();
	  
	  /**
	   * 
	   * @param name
	   * @param value
	   */
	  public void setAttribute(String name, Object value);
	  
	  /**
	   * 
	   * @param arg0
	   */
	  public void removeAttribute(java.lang.String arg0);
	  
	  /**
	   * 
	   */
	  public void invalidate();
	  
	  /**
	   * 
	   * @return
	   */
	  public boolean isNew();
}
