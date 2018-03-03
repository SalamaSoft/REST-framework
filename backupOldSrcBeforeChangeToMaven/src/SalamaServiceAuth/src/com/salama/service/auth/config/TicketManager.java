package com.salama.service.auth.config;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class TicketManager implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5200536899641925066L;
	
	private String _className = "";
	
	private int _idleTimeoutSeconds = 1800;

	public String getClassName() {
		return _className;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public int getIdleTimeoutSeconds() {
		return _idleTimeoutSeconds;
	}

	public void setIdleTimeoutSeconds(int idleTimeoutSeconds) {
		_idleTimeoutSeconds = idleTimeoutSeconds;
	}
	
}
