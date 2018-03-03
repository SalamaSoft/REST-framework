package com.salama.service.core.auth;

import java.io.Serializable;

public class RemoteClientInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3159828874716534543L;

	private String _remoteAddress = "";
	
	private String _sessionId = "";

	public String getRemoteAddress() {
		return _remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		_remoteAddress = remoteAddress;
	}

	public String getSessionId() {
		return _sessionId;
	}

	public void setSessionId(String sessionId) {
		_sessionId = sessionId;
	}
	
	
}
