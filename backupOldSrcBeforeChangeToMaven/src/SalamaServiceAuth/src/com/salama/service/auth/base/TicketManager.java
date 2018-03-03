package com.salama.service.auth.base;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface TicketManager {
	public String createNewTicket(Object userObj);
	
	public void updateTicketUserObj(String ticket, Object userObj);
	
	public Object getTicketUserObj(String ticket);
	
	public void deleteTicket(String ticket);
	
	public boolean isTicketValid(String ticket);
	
	public void reportAlive(String ticket);
	
	public void setIdleTimeoutSeconds(int timeoutSec);

	public int getIdleTimeoutSeconds();
}
