package com.salama.service.auth;

import java.util.Hashtable;
import java.util.UUID;

import com.salama.service.auth.base.TicketManager;
import com.salama.service.auth.base.TicketStatus;

/**
 * 
 * @author XingGu Liu
 *
 */
public class DefaultTicketManager implements TicketManager {

	protected Hashtable<String, TicketStatus> _ticketMap = new Hashtable<String, TicketStatus>();
	
	protected int _idleTimeoutSeconds = 1800;
	
	public DefaultTicketManager() {
		//_timeoutSeconds = timeoutSeconds;
	}
	
	@Override
	public String createNewTicket(Object userObj) {
		String ticket = UUID.randomUUID().toString();

		TicketStatus status = new TicketStatus();
		status.setLastReportTime(System.currentTimeMillis());
		status.setUserObj(userObj);
		
		_ticketMap.put(ticket, status);
		
		return ticket;
	}
	
	@Override
	public void updateTicketUserObj(String ticket, Object userObj) {
		TicketStatus status = _ticketMap.get(ticket);
		if(status != null) {
			status.setUserObj(userObj);
		}
	}
	
	@Override
	public Object getTicketUserObj(String ticket) {
		TicketStatus status = _ticketMap.get(ticket);
		if(status != null) {
			return status.getUserObj();
		}
		
		return null;
	}

	@Override
	public void deleteTicket(String ticket) {
		_ticketMap.remove(ticket);
	}

	@Override
	public boolean isTicketValid(String ticket) {
		TicketStatus status = _ticketMap.get(ticket); 
		if(status == null) {
			return false;
		} else {
			if( ((int)((System.currentTimeMillis() - status.getLastReportTime()) / 1000)) >= _idleTimeoutSeconds) {
				return false;
			} else {
				return true;
			}
		}
	}

	@Override
	public void reportAlive(String ticket) {
		TicketStatus status = _ticketMap.get(ticket); 
		if(status != null) {
			status.setLastReportTime(System.currentTimeMillis());
		}
	}

	@Override
	public void setIdleTimeoutSeconds(int timeoutSec) {
		_idleTimeoutSeconds = timeoutSec;
	}
	
	@Override
	public int getIdleTimeoutSeconds() {
		return _idleTimeoutSeconds;
	}
	
}
