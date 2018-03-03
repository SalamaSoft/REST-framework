package com.salama.service.core.context;

import java.io.Serializable;

import javax.servlet.ServletContext;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface CommonContext extends Serializable {
	
	public void reload(ServletContext servletContext, String configLocation);
	
	public void destroy();
}
