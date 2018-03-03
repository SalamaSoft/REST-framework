package com.salama.service.invoke.config;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class ClassSetting implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3967578837195119015L;
	
	private String className = "";

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
}
