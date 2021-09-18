package com.salama.service.core.net.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * 
 * @author XingGu Liu
 *
 */
public class HttpRequestWrapper extends AbstractRequestWrapper {

	private static final Logger log = Logger.getLogger(HttpRequestWrapper.class);
	
	protected boolean isGetMethod = false;
	protected boolean isGetAndUrlEncoded = false;
	
	protected String encoding = "utf-8";
	
	public HttpRequestWrapper(HttpServletRequest request, HttpSessionWrapper session) {
		super(request, session);
		
		String enc = request.getCharacterEncoding();
		if(enc != null && enc.length() > 0) {
			this.encoding = enc;
		}

		if("get".equalsIgnoreCase(request.getMethod())) {
			this.isGetMethod = true;
		}
		
		/*
		//application/x-www-form-urlencoded
		String method = request.getMethod();
		String contentType = request.getContentType();
		
		if(method != null && method.equalsIgnoreCase("get")) {
			this.isGetMethod = true;
			
			if(contentType != null) {
				contentType = contentType.toLowerCase();
				
				if(contentType.indexOf("x-www-form-urlencoded") >= 0) {
					isGetAndUrlEncoded = true;
				}
			}
		}
		*/
		
		if(log.isDebugEnabled()) {
			final StringBuilder headers = new StringBuilder();

			Enumeration<String> headerNames = request.getHeaderNames();
			String headerName;
			while(headerNames.hasMoreElements()) {
				headerName = headerNames.nextElement();
				headers.append(" ").append(headerName).append(":").append(request.getHeader(headerName));
			}
			
			log.debug("HttpRequestWrapper() req_" + _reqId + " method:"
					+ request.getMethod() + " characterEncoding:" + enc + " contentType:" + request.getContentType()
					+ " headers-> " + headers.toString());
		}
	}
	
	@Override
	public String getParameter(String name) {
		/*
		if(this.isGetAndUrlEncoded) {
			String val = super.getParameter(name);
			try {
				String urlDecodedVal = URLDecoder.decode(val, this.encoding);
				return new String(urlDecodedVal.getBytes("iso-8859-1"), this.encoding);
			} catch (Exception e) {
				return val;
			}
		} else {
			return super.getParameter(name);
		}
		*/

		if(this.isGetMethod) {
			String paramVal = super.getParameter(name);
			try {
				return new String(paramVal.getBytes("iso-8859-1"), this.encoding);
			} catch (Exception e) {
				return paramVal;
			}
		} else {
			return super.getParameter(name);
		}
		
	}
	
}
