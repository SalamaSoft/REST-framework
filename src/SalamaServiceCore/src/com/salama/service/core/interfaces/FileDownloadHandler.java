package com.salama.service.core.interfaces;

import com.salama.service.core.ServiceErrorMsgException;
import com.salama.service.core.net.RequestWrapper;
import com.salama.service.core.net.ResponseWrapper;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface FileDownloadHandler {
	
	/**
	 * 
	 * @param request
	 * @param responseWrapper
	 * @throws ServiceErrorMsgException
	 */
	public void downloadFile(RequestWrapper request, ResponseWrapper responseWrapper) 
			throws ServiceErrorMsgException;
}
