package com.salama.service.clouddata.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author XingGu Liu
 *
 */
public class ClassConstructorSetting {
	private String className = "";
	
	private List<String> paramTypes = new ArrayList<String>();
	
	private List<String> paramValues = new ArrayList<String>();

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<String> getParamTypes() {
		return paramTypes;
	}

	public void setParamTypes(List<String> paramTypes) {
		this.paramTypes = paramTypes;
	}

	public List<String> getParamValues() {
		return paramValues;
	}

	public void setParamValues(List<String> paramValues) {
		this.paramValues = paramValues;
	}
	
}
