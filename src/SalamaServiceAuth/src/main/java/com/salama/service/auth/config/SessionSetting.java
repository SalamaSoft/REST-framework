package com.salama.service.auth.config;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class SessionSetting implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5978764927899070297L;
	
	private String _invalidSessionUri = "";

	public String getInvalidSessionUri() {
		return _invalidSessionUri;
	}

	public void setInvalidSessionUri(String invalidSessionUri) {
		_invalidSessionUri = invalidSessionUri;
	}
	
	
}
