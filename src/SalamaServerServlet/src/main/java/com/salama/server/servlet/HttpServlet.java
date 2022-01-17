package com.salama.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;

import com.salama.service.clouddata.CloudDataServiceContext;
import com.salama.service.clouddata.core.ICloudDataService;
import com.salama.service.clouddata.core.ICloudDataServiceContext;
import com.salama.service.core.context.ServiceContext;
import com.salama.service.core.interfaces.ServiceTracer;
import com.salama.service.core.net.RequestWrapper;
import com.salama.service.core.net.http.HttpRequestWrapper;
import com.salama.service.core.net.http.HttpResponseWrapper;
import com.salama.service.core.net.http.HttpSessionWrapper;
import com.salama.service.core.net.http.MultipartRequestWrapper;
import com.salama.service.invoke.InvokeService;
import com.salama.util.ClassLoaderUtil;
import com.salama.util.http.upload.FileUploadSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author XingGu Liu
 *
 */
public class HttpServlet extends javax.servlet.http.HttpServlet {

	private static final long serialVersionUID = -5522241844236493287L;

	private static final Log logger = LogFactory.getLog(HttpServlet.class);

	public final static String INIT_PARAM_NAME_TRACER = "tracer";
	
	private com.salama.service.invoke.ServiceContext _invokeServiceContext = null;
	private InvokeService _invokeService = null;

	private ICloudDataServiceContext _cloudDataServiceContext = null;
	private ICloudDataService _cloudDataService = null;
	
	private com.salama.server.servlet.ServiceContext _servletServiceContext = null;
	
	private String _tracerClassName = null;
	private Class<?> _tracerClass = null;
	private ServiceTracer _tracer = null;
/*
	private int _responseBufferSize = 1024;

	private static final String DefaultEncoding = "utf-8";

	private static final Charset DefaultCharset = Charset.forName(DefaultEncoding);
*/
	
	
	public HttpServlet() {
		super();
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		//Init invoke service ------------------------------
		_invokeServiceContext = 
				((com.salama.service.invoke.ServiceContext) ServiceContext.getContext(
						getServletContext()).getContext(
						com.salama.service.invoke.ServiceContext.class));

		if(_invokeServiceContext != null) {
			_invokeService = _invokeServiceContext.createInvokeService();
		}
		
		//Init CloudDataService -----------------------------
		_cloudDataServiceContext = (CloudDataServiceContext) ServiceContext.getContext(
				getServletContext()).getContext(
				com.salama.service.clouddata.CloudDataServiceContext.class);
		if(_cloudDataServiceContext != null) {
			_cloudDataService = _cloudDataServiceContext.createCloudDataService();
		}
		
		//Init Servlet ServiceContext
		_servletServiceContext = (com.salama.server.servlet.ServiceContext) ServiceContext.getContext(
				getServletContext()).getContext(com.salama.server.servlet.ServiceContext.class);
		
		//Init tracer ------------------------------------------
		_tracerClassName = getInitParameter(INIT_PARAM_NAME_TRACER);
		if(_tracerClassName != null && _tracerClassName.trim().length() > 0) {
			try {
				_tracerClass = ClassLoaderUtil.getDefaultClassLoader().loadClass(_tracerClassName);
				logger.debug("_tracerClass:" + _tracerClass.getName());
			} catch (ClassNotFoundException e) {
				logger.error("init()", e);
				_tracerClass = null;
			}
		}
		
		if(_tracerClass != null) {
			try {
				_tracer = (ServiceTracer)_tracerClass.newInstance();
				_tracer.init();
			} catch (Throwable e) {
				logger.error("init()", e);
				_tracer = null;
			}
		}
		
	}
	
	@Override
	public void destroy() {
		if(_tracer != null) {
			_tracer.destroy();
		}

		super.destroy();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			if(!processService(req, resp)) {
				super.doGet(req, resp);
			}
		} catch(ServletException e) {
			logger.error("doGet()", e);
			throw e;
		} catch (IOException e) {
			logger.error("doGet()", e);
			throw e;
		} catch(FileUploadException e) {
			logger.error("doGet()", e);
			throw new ServletException(e);
		} catch(RuntimeException e) {
			logger.error("doGet()", e);
			throw e;
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			if(!processService(req, resp)) {
				super.doPost(req, resp);
			}
		} catch(ServletException e) {
			logger.error("doPost()", e);
			throw e;
		} catch (IOException e) {
			logger.error("doPost()", e);
			throw e; 
		} catch(FileUploadException e) {
			logger.error("doPost()", e);
			throw new ServletException(e);
		} catch(RuntimeException e) {
			logger.error("doPost()", e);
			throw e;
		}
	}
	
	protected boolean processService(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException, FileUploadException {
		String uri = req.getRequestURI();
		uri = uri.substring(req.getContextPath().length());

		Integer uriType = SupportedServiceUri.getServiceType(uri);
		
		if(uriType == null) {
			return false;
		} else {
			long processBeginTime = System.currentTimeMillis();
			
			boolean isMultiPartRequest = FileUploadSupport.isMultipartContent(req);
			HttpResponseWrapper responseWrapper = new HttpResponseWrapper(resp);
			RequestWrapper requestWrapper = null;
			if(isMultiPartRequest) {
				requestWrapper = _servletServiceContext.getFileUploadSupport().parseMultipartRequest(req);
			} else {
				requestWrapper = new HttpRequestWrapper(req, new HttpSessionWrapper(req.getSession()));
			}

			//default content type
			responseWrapper.setContentType("text/plain");
			
			if (uriType == SupportedServiceUri.ServiceTypeCloudDataService) {
				if(_cloudDataService == null) {
					logger.error("_cloudDataService is null");
					return false;
				}
				
				String serviceType;
				String serviceMethod;

				serviceType = requestWrapper.getParameter(InvokeServiceParameterName.ServiceType);
				serviceMethod = requestWrapper.getParameter(InvokeServiceParameterName.ServiceMethod);

				try {
					if(_tracer != null) {
						_tracer.onCloudDataService(requestWrapper, serviceType, serviceMethod);
					}
				} catch(Throwable e) {
					logger.error("processService()", e);
				}

				httpOutput(resp, 
						_cloudDataService.cloudDataService(
								serviceType, serviceMethod, requestWrapper, responseWrapper));
				
				try {
					if(_tracer != null) {
						_tracer.onCloudDataServiceDone(requestWrapper, serviceType, serviceMethod, (System.currentTimeMillis() - processBeginTime));
					}
				} catch(Throwable e) {
					logger.error("processService()", e);
				}
			} else if(uriType == SupportedServiceUri.ServiceTypeInvokeService) {
				String serviceType;
				String serviceMethod;
				String paramType;
				String paramValueXml;
//				String sessionPreHandlerType;
//				String sessionPreHandlerMethod;
//				String sessionAfterHandlerType;
//				String sessionAfterHandlerMethod;

				serviceType = requestWrapper.getParameter(InvokeServiceParameterName.ServiceType);
				serviceMethod = requestWrapper.getParameter(InvokeServiceParameterName.ServiceMethod);
				paramType = requestWrapper.getParameter(InvokeServiceParameterName.ParamType);
				paramValueXml = requestWrapper.getParameter(InvokeServiceParameterName.ParamValueXml);
//				sessionPreHandlerType = requestWrapper.getParameter(InvokeServiceParameterName.SessionPreHandlerType);
//				sessionPreHandlerMethod = requestWrapper.getParameter(InvokeServiceParameterName.SessionPreHandlerMethod);
//				sessionAfterHandlerType = requestWrapper.getParameter(InvokeServiceParameterName.SessionAfterHandlerType);
//				sessionAfterHandlerMethod = requestWrapper.getParameter(InvokeServiceParameterName.SessionAfterHandlerMethod);
				
				try {
					if(_tracer != null) {
						_tracer.onInvokeService(requestWrapper, 
								serviceType, serviceMethod, 
								paramType, paramValueXml);
					}
				} catch(Throwable e) {
					logger.error("processService()", e);
				}
				
				httpOutput(
						resp,
						_invokeService.invokeService(
							serviceType, serviceMethod, 
							paramType, paramValueXml, 
							requestWrapper, responseWrapper
							)
						);
				
				try {
					if(_tracer != null) {
						_tracer.onInvokeServiceDone(requestWrapper, serviceType, serviceMethod, paramType, paramValueXml, (System.currentTimeMillis() - processBeginTime));
					}
				} catch(Throwable e) {
					logger.error("processService()", e);
				}
			} else if (uriType == SupportedServiceUri.ServiceTypeDownloadService) {
				String serviceType;

				serviceType = requestWrapper.getParameter(InvokeServiceParameterName.ServiceType);

				try {
					if(_tracer != null) {
						_tracer.onDownloadService(requestWrapper, serviceType);
					}
				} catch(Throwable e) {
					logger.error("processService()", e);
				}
				
				_invokeService.downloadService(
						serviceType, 
						requestWrapper, responseWrapper 
						);
				
				try {
					if(_tracer != null) {
						_tracer.onDownloadServiceDone(requestWrapper, serviceType, (System.currentTimeMillis() - processBeginTime));
					}
				} catch(Throwable e) {
					logger.error("processService()", e);
				}
			} else if (uriType == SupportedServiceUri.ServiceTypeUploadService) {
				String serviceType = requestWrapper.getParameter(InvokeServiceParameterName.ServiceType);
				String paramType = requestWrapper.getParameter(InvokeServiceParameterName.ParamType);
				String paramValueXml = requestWrapper.getParameter(InvokeServiceParameterName.ParamValueXml);

				try {
					if(_tracer != null) {
						_tracer.onUploadService(requestWrapper, serviceType, paramType, paramValueXml);
					}
				} catch(Throwable e) {
					logger.error("processService()", e);
				}
				
				httpOutput(
						resp,
						_invokeService.uploadService(
						serviceType, 
						paramType,
						paramValueXml,
						(MultipartRequestWrapper)requestWrapper)
				);

				try {
					if(_tracer != null) {
						_tracer.onUploadServiceDone(requestWrapper, serviceType, paramType, paramValueXml, (System.currentTimeMillis() - processBeginTime));
					}
				} catch(Throwable e) {
					logger.error("processService()", e);
				}
			} else if (uriType == SupportedServiceUri.ServiceTypeSetSessionValue) {
				String key;
				String dataType; 
				String property;
				String propertyType;
				String valueXml;

				key = requestWrapper.getParameter(InvokeServiceParameterName.Key);
				dataType = requestWrapper.getParameter(InvokeServiceParameterName.DataType); 
				property = requestWrapper.getParameter(InvokeServiceParameterName.Property);
				propertyType = requestWrapper.getParameter(InvokeServiceParameterName.PropertyType);
				valueXml = requestWrapper.getParameter(InvokeServiceParameterName.ValueXml);

				try {
					if(_tracer != null) {
						_tracer.onSetSessionValue(requestWrapper, key, dataType, property, propertyType, valueXml);
					}
				} catch(Throwable e) {
					logger.error("processService()", e);
				}
				
				httpOutput(
						resp,
						_invokeService.setSessionValue(
								key, dataType, property, propertyType, valueXml, 
								requestWrapper.getSession()
								)
						);

				try {
					if(_tracer != null) {
						_tracer.onSetSessionValueDone(requestWrapper, key, dataType, property, propertyType, valueXml, (System.currentTimeMillis() - processBeginTime));
					}
				} catch(Throwable e) {
					logger.error("processService()", e);
				}
			} else if (uriType == SupportedServiceUri.ServiceTypeSetSessionValues) {
				String sessionValueSetXml;
				sessionValueSetXml = requestWrapper.getParameter(InvokeServiceParameterName.SessionValueSetXml);
				
				try {
					if(_tracer != null) {
						_tracer.onSetSessionValues(requestWrapper, sessionValueSetXml);
					}
				} catch(Throwable e) {
					logger.error("processService()", e);
				}

				httpOutput(
						resp,
						_invokeService.setSessionValues(sessionValueSetXml, 
								requestWrapper.getSession())
						);
				
				try {
					if(_tracer != null) {
						_tracer.onSetSessionValuesDone(requestWrapper, sessionValueSetXml, (System.currentTimeMillis() - processBeginTime));
					}
				} catch(Throwable e) {
					logger.error("processService()", e);
				}
			} else if (uriType == SupportedServiceUri.ServiceTypeGetSessionValue) {
				String key;
				String dataType; 
				String property;
				String propertyType;

				key = requestWrapper.getParameter(InvokeServiceParameterName.Key);
				dataType = requestWrapper.getParameter(InvokeServiceParameterName.DataType); 
				property = requestWrapper.getParameter(InvokeServiceParameterName.Property);
				propertyType = requestWrapper.getParameter(InvokeServiceParameterName.PropertyType);
				
				try {
					if(_tracer != null) {
						_tracer.onGetSessionValue(requestWrapper, key, dataType, property, propertyType);
					}
				} catch(Throwable e) {
					logger.error("processService()", e);
				}

				httpOutput(
						resp,
						_invokeService.getSessionValue(key, dataType, property, propertyType, 
								requestWrapper.getSession())
						);
				
				try {
					if(_tracer != null) {
						_tracer.onGetSessionValueDone(requestWrapper, key, dataType, property, propertyType, (System.currentTimeMillis() - processBeginTime));
					}
				} catch(Throwable e) {
					logger.error("processService()", e);
				}
			} else {
				//impossible branch
				try {
					if(_tracer != null) {
						_tracer.onUnSupportedService(requestWrapper);
					}
				} catch(Throwable e) {
					logger.error("processService()", e);
				}
			}
			
			return true;
		}
	}
	
	protected static void httpOutput(HttpServletResponse response, String content) {
		if(content == null) {
			return;
		}
		
		PrintWriter output = null;

		try {
			output = response.getWriter();
			
			output.print(content);
			
			output.flush();
		} catch(IOException e) {
			logger.error("httpOutput()", e);
		} finally {
			try {
				output.close();
			} catch(Exception e) {
			}
		}
	}
	
}
