package com.salama.modeldriven.generator.data;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class ObjectiveCProperty implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5764854448482375732L;

	private String memType = "";
	
	private String name = "";
	
	private String type = "";

	public String getMemType() {
		return memType;
	}

	public void setMemType(String memType) {
		this.memType = memType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
