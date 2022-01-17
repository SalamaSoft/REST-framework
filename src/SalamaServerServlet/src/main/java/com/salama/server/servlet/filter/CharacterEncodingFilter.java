package com.salama.server.servlet.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


/**
 * 
 * @author XingGu Liu
 *
 */
public class CharacterEncodingFilter implements Filter {
	private static final Log logger = LogFactory.getLog(CharacterEncodingFilter.class);
	
	public final static String INIT_PARAM_NAME_ENCODING = "encoding";
	protected final static String FILTER_ALREDY_EXCUTED = 
			"Excecuted:com.salama.server.servlet.filter.CharacterEncodingFilter";
	
	protected String _encoding = "utf-8";
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		logger.debug("init()");
		_encoding = config.getInitParameter(INIT_PARAM_NAME_ENCODING);
	}
	
	@Override
	public void destroy() {
		logger.debug("destroy()");
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		if(request.getAttribute(FILTER_ALREDY_EXCUTED) == null) {
			request.setAttribute(FILTER_ALREDY_EXCUTED, Boolean.TRUE);
		
			request.setCharacterEncoding(_encoding);
			response.setCharacterEncoding(_encoding);
			
			filterChain.doFilter(request, response);
		} else {
			//do nothing
			filterChain.doFilter(request, response);
		}
	}
	
	
}
