package com.salama.service.auth.config;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class EntryPoint implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -462496263740236863L;

	private String className = "";
	
	private String uri = "";

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	
}
