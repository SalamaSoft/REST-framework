package com.salama.service.core.auth;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class Credential implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5937512906527661868L;
	
	private String _ticket = "";

	public String getTicket() {
		return _ticket;
	}

	public void setTicket(String ticket) {
		_ticket = ticket;
	}
	
}
