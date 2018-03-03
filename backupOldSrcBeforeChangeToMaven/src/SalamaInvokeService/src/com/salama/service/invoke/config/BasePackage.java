package com.salama.service.invoke.config;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class BasePackage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1550157749677790859L;

	private String _basePackage = "";
	
	private String _description = "";
	
	/**
	 * Package name, such as java.util
	 * @return Package name
	 */
	public String getBasePackage() {
		return _basePackage;
	}
	
	public void setBasePackage(String basePackage) {
		_basePackage = basePackage;
	}
	
	/**
	 * Description about this package
	 * @return Description about this package
	 */
	public String getDescription() {
		return _description;
	}
	
	public void setDescription(String description) {
		_description = description;
	}
	
	
}
