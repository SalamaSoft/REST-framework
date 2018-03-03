package com.salama.service.auth.base;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class TicketStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7871653168204144705L;
	
	private long lastReportTime = 0;
	
	private Object userObj = null;

	public long getLastReportTime() {
		return lastReportTime;
	}

	public void setLastReportTime(long lastReportTime) {
		this.lastReportTime = lastReportTime;
	}
	
	public Object getUserObj() {
		return userObj;
	}

	public void setUserObj(Object userObj) {
		this.userObj = userObj;
	}
}
