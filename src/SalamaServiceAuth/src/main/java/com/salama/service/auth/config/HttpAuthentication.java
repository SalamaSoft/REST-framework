package com.salama.service.auth.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author XingGu Liu
 *
 */
public class HttpAuthentication implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6213644218841658664L;

	private List<InterceptUri> _interceptUris = new ArrayList<InterceptUri>();

	private SessionSetting _session = new SessionSetting();
	
	public List<InterceptUri> getInterceptUris() {
		return _interceptUris;
	}

	public void setInterceptUris(List<InterceptUri> interceptUris) {
		_interceptUris = interceptUris;
	}

	public SessionSetting getSession() {
		return _session;
	}

	public void setSession(SessionSetting session) {
		_session = session;
	}

}
