package com.salama.service.auth.config;

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
	private static final long serialVersionUID = -4550110031377132321L;
	
	private String className = "";

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
}
