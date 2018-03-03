package com.salama.service.core.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 
 * @author XingGu Liu
 *
 */
public class ServiceContextListener implements ServletContextListener{

	public final static String CONTEXT_PARAM_SERVICE_CONTEXT_LOCATION = "contextConfigLocation";
	
	public ServiceContextListener() {
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		String configLocation = servletContext.getInitParameter(CONTEXT_PARAM_SERVICE_CONTEXT_LOCATION);
		
		ServiceContext serviceContext = new ServiceContext();
		serviceContext.reload(servletContext, configLocation);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ServiceContext.getContext(event.getServletContext()).destroy();
	}
	
}
