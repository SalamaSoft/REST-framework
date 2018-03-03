package com.salama.service.auth.base;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class LoginResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1681133272563407567L;
	
	private String _ticket = "";

	public String getTicket() {
		return _ticket;
	}

	public void setTicket(String ticket) {
		_ticket = ticket;
	}
}
