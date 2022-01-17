package com.salama.server.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


import com.salama.service.core.interfaces.RequestTracer;
import com.salama.service.core.net.http.HttpRequestWrapper;
import com.salama.service.core.net.http.HttpSessionWrapper;
import com.salama.util.ClassLoaderUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author XingGu Liu
 *
 */
public class RequestTacerFilter implements Filter {
	private static final Log logger = LogFactory.getLog(RequestTacerFilter.class);
	
	public final static String INIT_PARAM_NAME_TRACER = "tracer";
	
	protected final static String FILTER_ALREDY_EXCUTED = 
			"Excecuted:com.salama.server.servlet.filter.RequestTacerFilter";

	protected String _tracerClassName = null;
	protected Class<?> _tracerClass = null;
	protected RequestTracer _tracer = null;
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		logger.debug("init()");

		_tracerClassName = config.getInitParameter(INIT_PARAM_NAME_TRACER);
		if(_tracerClassName != null && _tracerClassName.trim().length() > 0) {
			try {
				_tracerClass = ClassLoaderUtil.getDefaultClassLoader().loadClass(_tracerClassName);
			} catch (ClassNotFoundException e) {
				logger.error("init()", e);
				_tracerClass = null;
			}
		}
		
		if(_tracerClass != null) {
			try {
				_tracer = (RequestTracer)_tracerClass.newInstance();
				_tracer.init();
			} catch (Exception e) {
				logger.error("init()", e);
				_tracer = null;
			}
		}
	}

	@Override
	public void destroy() {
		logger.debug("destroy()");

		if(_tracer != null) {
			_tracer.destroy();
		}
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		if(request.getAttribute(FILTER_ALREDY_EXCUTED) == null) {
			request.setAttribute(FILTER_ALREDY_EXCUTED, Boolean.TRUE);

			doTrace(request, response);
			
			filterChain.doFilter(request, response);
		} else {
			//do nothing
			filterChain.doFilter(request, response);
		}
	}

	protected void doTrace(ServletRequest request, ServletResponse response) {
		try {
			if(_tracer != null) {
				if(HttpServletRequest.class.isAssignableFrom(request.getClass())) {
					_tracer.onRequest(new HttpRequestWrapper(
							(HttpServletRequest)request, 
							new HttpSessionWrapper( ((HttpServletRequest)request).getSession() )
							)
					);
				}
			}
		} catch(Exception e) {
			logger.error("onRequest()", e);
		}
	}
	
}
