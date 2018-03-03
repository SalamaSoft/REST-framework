package com.salama.modeldriven.generator.data;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class JavaProperty implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2287269199270004975L;

	private String name = "";
	
	private String nameWithUpperCasePrefix = "";

	private String type = "";
	
	private String defaultValue = "";

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

	public String getNameWithUpperCasePrefix() {
		return nameWithUpperCasePrefix;
	}

	public void setNameWithUpperCasePrefix(String nameWithUpperCasePrefix) {
		this.nameWithUpperCasePrefix = nameWithUpperCasePrefix;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}


}
