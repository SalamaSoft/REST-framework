package com.salama.service.clouddata.core;

import com.salama.service.core.net.RequestWrapper;
import com.salama.service.core.net.ResponseWrapper;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface ICloudDataService {
	public final static String AUTH_TICKET = "authTicket";

	public String cloudDataService(
			String serviceType,
			String serviceMethod, 
			RequestWrapper request, ResponseWrapper response);
}
