package com.salama.service.clouddata.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author XingGu Liu
 *
 */
public class CloudDataServiceContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2563907768059493811L;
	
	private String authTicketManagerBackupDirPath = "";
	
	private List<CloudDataAppConfig> appConfigs = new ArrayList<CloudDataAppConfig>();

	public List<CloudDataAppConfig> getAppConfigs() {
		return appConfigs;
	}

	public void setAppConfigs(List<CloudDataAppConfig> appConfigs) {
		this.appConfigs = appConfigs;
	}

	public String getAuthTicketManagerBackupDirPath() {
		return authTicketManagerBackupDirPath;
	}

	public void setAuthTicketManagerBackupDirPath(
			String authTicketManagerBackupDirPath) {
		this.authTicketManagerBackupDirPath = authTicketManagerBackupDirPath;
	}

	
}
