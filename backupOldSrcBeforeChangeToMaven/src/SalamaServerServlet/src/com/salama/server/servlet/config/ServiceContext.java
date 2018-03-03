package com.salama.server.servlet.config;

import java.io.Serializable;

import com.salama.service.invoke.config.UploadSetting;

/**
 * 
 * @author XingGu Liu
 *
 */
public class ServiceContext implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3442494970653113893L;
	
	private UploadSetting _uploadSetting = new UploadSetting();

	public UploadSetting getUploadSetting() {
		return _uploadSetting;
	}

	public void setUploadSetting(UploadSetting uploadSetting) {
		_uploadSetting = uploadSetting;
	}
	
}
