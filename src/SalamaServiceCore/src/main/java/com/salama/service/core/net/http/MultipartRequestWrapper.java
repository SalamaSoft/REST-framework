package com.salama.service.core.net.http;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.salama.service.core.net.SessionWrapper;
import org.apache.log4j.Logger;

/**
 * 
 * @author XingGu Liu
 */
public class MultipartRequestWrapper extends AbstractRequestWrapper {
	private static final Logger log = Logger.getLogger(HttpRequestWrapper.class);
	private Hashtable<String, String> _fieldMap = new Hashtable<String, String>();
	private Hashtable<String, MultipartFile> _multipartFileMap = new Hashtable<String, MultipartFile>();

	public MultipartRequestWrapper(
			ServletRequest request,
			SessionWrapper session,
			Hashtable<String, String> fieldMap, 
			Hashtable<String, MultipartFile> multipartFileMap) {
		super(request, session);
		_fieldMap = fieldMap;
		_multipartFileMap = multipartFileMap;

		if(log.isDebugEnabled() && HttpServletRequest.class.isAssignableFrom(request.getClass())) {
			final StringBuilder headers = new StringBuilder();
			final HttpServletRequest req = (HttpServletRequest) request;

			Enumeration<String> headerNames = req.getHeaderNames();
			String headerName;
			while(headerNames.hasMoreElements()) {
				headerName = headerNames.nextElement();
				headers.append(" ").append(headerName).append(":").append(req.getHeader(headerName));
			}

			log.debug("HttpRequestWrapper() req_" + _reqId + " method:" + req.getMethod()
					+ " characterEncoding:" + req.getCharacterEncoding() + " contentType:" + request.getContentType()
					+ " headers-> " + headers.toString());
		}
	}

	@Override
	public boolean isMultipartContent() {
		return true;
	}
	
	@Override
	public String getParameter(String name) {
		return _fieldMap.get(name);
	}
	
	@Override
	public Enumeration<String> getParameterNames() {
		return _fieldMap.keys();
	}
	
	public MultipartFile getFile(String name) {
		return _multipartFileMap.get(name);
	}
	
	public Enumeration<String> getFileNames() {
		return _multipartFileMap.keys();
	}
	
	public Collection<MultipartFile> getFiles() {
		return _multipartFileMap.values();
	}
}
