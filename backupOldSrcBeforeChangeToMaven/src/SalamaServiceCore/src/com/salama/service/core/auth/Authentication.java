package com.salama.service.core.auth;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class Authentication implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1709221835319163866L;

	private UserData _userData = null;
	
	private Credential _credential = null;
	
	private RemoteClientInfo _remoteClient = null;

	public UserData getUserData() {
		return _userData;
	}

	public void setUserData(UserData userData) {
		_userData = userData;
	}

	public Credential getCredential() {
		return _credential;
	}

	public void setCredential(Credential credential) {
		_credential = credential;
	}

	public RemoteClientInfo getRemoteClient() {
		return _remoteClient;
	}

	public void setRemoteClient(RemoteClientInfo remoteClient) {
		_remoteClient = remoteClient;
	}
	
	
}
