package com.salama.service.core.context.config;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class ContextSetting implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5990696350149568796L;
	
	private String contextClass = "";
	
	private String configLocation = "";

	public String getConfigLocation() {
		return configLocation;
	}

	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}
	
	public String getContextClass() {
		return contextClass;
	}

	public void setContextClass(String contextClass) {
		this.contextClass = contextClass;
	}
	
}
