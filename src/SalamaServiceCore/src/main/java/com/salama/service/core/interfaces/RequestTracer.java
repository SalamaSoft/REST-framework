package com.salama.service.core.interfaces;

import com.salama.service.core.net.RequestWrapper;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface RequestTracer {
	
	public void init();
	
	public void destroy();
	
	/**
	 * 
	 * @param request
	 */
	public void onRequest(RequestWrapper request);
	
}
