package com.salama.service.core.interfaces;

import com.salama.service.core.ServiceErrorMsgException;
import com.salama.service.core.net.http.MultipartRequestWrapper;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface FileUpload2Handler<ParamType> {
	public Object uploadFile(MultipartRequestWrapper request, ParamType param) 
			throws ServiceErrorMsgException;
}
