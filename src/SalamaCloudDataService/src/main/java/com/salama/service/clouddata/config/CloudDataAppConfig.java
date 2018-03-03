package com.salama.service.clouddata.config;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class CloudDataAppConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6970880132656186257L;

	private String appId = "";
	
	private String basePackage = "";
	
	private String dbConnectionJNDIName = "";
	
	private String exposedPackage = "";

	private ClassConstructorSetting appAuthUserDataManagerSetting = new ClassConstructorSetting();

	private ClassConstructorSetting appServiceFilterSetting = new ClassConstructorSetting();
	
	public ClassConstructorSetting getAppAuthUserDataManagerSetting() {
		return appAuthUserDataManagerSetting;
	}

	public void setAppAuthUserDataManagerSetting(
			ClassConstructorSetting appAuthUserDataManagerSetting) {
		this.appAuthUserDataManagerSetting = appAuthUserDataManagerSetting;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public String getDbConnectionJNDIName() {
		return dbConnectionJNDIName;
	}

	public void setDbConnectionJNDIName(String dbConnectionJNDIName) {
		this.dbConnectionJNDIName = dbConnectionJNDIName;
	}

	public String getExposedPackage() {
		return exposedPackage;
	}

	public void setExposedPackage(String exposedPackage) {
		this.exposedPackage = exposedPackage;
	}

	public ClassConstructorSetting getAppServiceFilterSetting() {
		return appServiceFilterSetting;
	}

	public void setAppServiceFilterSetting(
			ClassConstructorSetting appServiceFilterSetting) {
		this.appServiceFilterSetting = appServiceFilterSetting;
	}
	
}
