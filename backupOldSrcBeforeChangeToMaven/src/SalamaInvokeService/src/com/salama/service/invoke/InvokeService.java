package com.salama.service.invoke;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.log4j.Logger;

import MetoXML.XmlDeserializer;
import MetoXML.Base.XmlParseException;

import com.salama.reflect.MethodInvokeUtil;
import com.salama.reflect.PreScanClassFinder;
import com.salama.service.core.ServiceErrorMsgException;
import com.salama.service.core.SimpleMetoXmlResult;
import com.salama.service.core.auth.Authentication;
import com.salama.service.core.auth.MethodAccessController;
import com.salama.service.core.auth.MethodAccessNoAuthorityException;
import com.salama.service.core.data.session.MapValueSet;
import com.salama.service.core.data.session.SessionValueSet;
import com.salama.service.core.interfaces.FileDownloadHandler;
import com.salama.service.core.interfaces.FileUpload2Handler;
import com.salama.service.core.interfaces.FileUploadHandler;
import com.salama.service.core.interfaces.RequestHandler;
import com.salama.service.core.interfaces.SessionHandler;
import com.salama.service.core.net.RequestWrapper;
import com.salama.service.core.net.ResponseWrapper;
import com.salama.service.core.net.SessionWrapper;
import com.salama.service.core.net.http.MultipartRequestWrapper;
import com.salama.service.invoke.config.XmlResultSetting;

/**
 * 
 * @author XingGu Liu
 *
 */
public final class InvokeService {
	private static final Logger logger = Logger
			.getLogger(InvokeService.class);

	public static final String DefaultEncoding = "utf-8";
	
	public static final Charset DefaultCharset = Charset.forName(DefaultEncoding);

	private PreScanClassFinder _serviceClassFinder = null;
	private PreScanClassFinder _dataClassFinder = null; 
	private MethodAccessController _methodAccessController = null;
	
	private XmlResultSetting _xmlResultSetting = null;
	
	public InvokeService(PreScanClassFinder serviceClassFinder, 
			PreScanClassFinder dataClassFinder, 
			MethodAccessController methodAccessController, XmlResultSetting xmlResultSetting) {
		_serviceClassFinder = serviceClassFinder; 
		_dataClassFinder = dataClassFinder;
		_methodAccessController = methodAccessController;
		
		_xmlResultSetting = xmlResultSetting;
	}
	
	private Class<?> getServiceType(String className) throws ClassNotFoundException {
		return _serviceClassFinder.findClass(className);
	}
	
	private Class<?> getDataType(String className) throws ClassNotFoundException {
		return _dataClassFinder.findClass(className);
	}

	public String invokeService(
			String serviceType,
			String serviceMethod, 
			String paramType,
			String paramValueXml,
			RequestWrapper request,
			ResponseWrapper response
			) {
		return processInvokeService(
				serviceType,
				serviceMethod, 
				paramType,
				paramValueXml,
				request,
				response
				);
	}	
	
	public void downloadService(
			String serviceType,
			RequestWrapper request,
			ResponseWrapper response
			) {
		try {
			// Get type class
			Class<?> serviceTypeClass = getServiceType(serviceType);
			
			if(!FileDownloadHandler.class.isAssignableFrom(serviceTypeClass)) {
				throw new ServiceErrorMsgException (
						"The service for downloading must implement com.salama.service.core.interfaces.FileDownloadHandler. Service:"
						+ serviceType);
			} else {
				Object service = serviceTypeClass.newInstance();
				
				if(SessionHandler.class.isAssignableFrom(serviceTypeClass)) {
					((SessionHandler)service).setSession(request.getSession());
				} else if (RequestHandler.class.isAssignableFrom(serviceTypeClass)) {
					((RequestHandler)service).setRequest(request);
				}
				
				((FileDownloadHandler)service).downloadFile(request, response);
			}
		} catch (Exception e) {
			logger.error("downloadService()", e);
		} finally {
			try {
				response.getOutputStream().close();
			} catch (IOException e) {
			}
		}
		
	}

	public String uploadService(
			String serviceType,
			String paramType,
			String paramValueXml,
			MultipartRequestWrapper multipartRequest
			) {
		try {
			// Get type class
			Class<?> serviceTypeClass = getServiceType(serviceType);
			Object paramValue = null;
					
			if(FileUploadHandler.class.isAssignableFrom(serviceTypeClass)) {
				
			} else if (FileUpload2Handler.class.isAssignableFrom(serviceTypeClass)) {
				Class<?> paramTypeClass = void.class;
				paramValue = null;

				if(paramType != null && paramType.length() > 0) {
					paramTypeClass = getDataType(paramType);
					paramValue = XmlDeserializer.stringToObject(paramValueXml,
							paramTypeClass);
				}
				
				
			} else {
				throw new ServiceErrorMsgException (
						"The service for uploading must implement com.salama.service.core.interfaces.FileUploadHandler. Service:"
						+ serviceType);
			}
			
			if(!FileUploadHandler.class.isAssignableFrom(serviceTypeClass)
					&& !FileUpload2Handler.class.isAssignableFrom(serviceTypeClass)) {
				throw new ServiceErrorMsgException (
						"The service for uploading must implement com.salama.service.core.interfaces.FileUploadHandler. Service:"
						+ serviceType);
			} else {
				Object service = serviceTypeClass.newInstance();
				
				if(SessionHandler.class.isAssignableFrom(serviceTypeClass)) {
					((SessionHandler)service).setSession(multipartRequest.getSession());
				} else if (RequestHandler.class.isAssignableFrom(serviceTypeClass)) {
					((RequestHandler)service).setRequest(multipartRequest);
				}
				
				Object returnValue = null;

				if(FileUploadHandler.class.isAssignableFrom(serviceTypeClass)) {
					returnValue = ((FileUploadHandler)service).uploadFile(multipartRequest);
				} else if (FileUpload2Handler.class.isAssignableFrom(serviceTypeClass)) {
					Class<?> paramTypeClass = void.class;
					paramValue = null;

					if(paramType != null && paramType.length() > 0) {
						paramTypeClass = getDataType(paramType);
						paramValue = XmlDeserializer.stringToObject(paramValueXml,
								paramTypeClass);
					}
					
					returnValue = ((FileUpload2Handler)service).uploadFile(multipartRequest, paramValue);
				} 				
				
				String resultXml;
				
				if (returnValue == null) {
					resultXml = SimpleMetoXmlResult.emptyResultXml();
				} else {
					resultXml = SimpleMetoXmlResult.resultToXml(returnValue,
							returnValue.getClass(), DefaultCharset, 
							_xmlResultSetting.isUseClassFullName(),
							_xmlResultSetting.isMakeFirstCharUpperCase());
				}
				
				logger.debug("uploadService() resultXml:\r\n" + resultXml);
				
				return resultXml;
			}
		} catch (ClassNotFoundException e) {
			logger.error("uploadService()", e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (ServiceErrorMsgException e) {
			logger.error("uploadService()", e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (InstantiationException e) {
			logger.error("uploadService()", e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (IllegalAccessException e) {
			logger.error("uploadService()", e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch(Exception e) {
			logger.error("uploadService()", e);
			return SimpleMetoXmlResult.errorResultXml(e);
		}
	}

	private String processInvokeService(
			String serviceType,
			String serviceMethod, 
			String paramType,
			String paramValueXml,
			RequestWrapper request,
			ResponseWrapper response
			) {
		logger.debug("invokeService() serviceType:" + serviceType
				+ ";methodName:" + serviceMethod + ";paramType:" + paramType
				+ ";paramValueXml:" + "\r\n" + paramValueXml);

		try {
			logger.debug("invokeService() invoke the service method:"
					+ serviceType + "." + serviceMethod + "(" + paramType + ")");
			logger.debug("invokeService() paramValueXml:\r\n" + paramValueXml);

			// Get type class
			Class<?> serviceTypeClass = getServiceType(serviceType);

			Class<?> paramTypeClass = void.class;
			Object paramValue = null;

			long beginTimeForDebugUsage = System.currentTimeMillis();
			if(paramType != null && paramType.length() > 0) {
				paramTypeClass = getDataType(paramType);
				paramValue = XmlDeserializer.stringToObject(paramValueXml,
						paramTypeClass, _dataClassFinder);
				
				logger.debug("invokeService() time usage(ms) of parsing paramValueXml:" + Long.toString(System.currentTimeMillis() - beginTimeForDebugUsage));
			}

			Method method = null;
			
			if(paramTypeClass == void.class) {
				method = MethodInvokeUtil.GetMethod(serviceTypeClass,
						serviceMethod, (Class[])null);
			} else {
				method = MethodInvokeUtil.GetMethod(serviceTypeClass,
						serviceMethod, new Class[] { paramTypeClass });
			}
			
			//Check method authority
			checkServiceAccessAuthority(method, request.getSession());
			
			Object service = null;
			if(!MethodInvokeUtil.IsMethodStatic(method)) {
				service = serviceTypeClass.newInstance();
				
				if(SessionHandler.class.isAssignableFrom(serviceTypeClass)) {
					((SessionHandler)service).setSession(request.getSession());
				} else if (RequestHandler.class.isAssignableFrom(serviceTypeClass)) {
					((RequestHandler)service).setRequest(request);
				}
			}

			// Handler before invoke
			// ************************************************************
			/* Not support any more
			handleLogicBeforeInvokeService(method,
					sessionPreHandlerType, sessionPreHandlerMethod,
					paramTypeClass, paramValue, sessionWrapper);
			*/

			// Invoke service method
			// *****************************************************************
			logger.debug("invokeService() invoke the service method:"
					+ serviceType + "." + serviceMethod + "(" + paramType + ")");

			Class<?> returnValueType = null;
			String resultXml = null;

			returnValueType = method.getReturnType();

			beginTimeForDebugUsage = System.currentTimeMillis();
			
			Object returnValue = null;
			if(paramTypeClass == void.class) {
				returnValue = MethodInvokeUtil.InvokeMethod(service, method,
						(Object[])null);
			} else {
				returnValue = MethodInvokeUtil.InvokeMethod(service, method,
						new Object[] { paramValue });
			}

			logger.debug("invokeService() time usage(ms) [" + serviceType + "." + serviceMethod + "]:" + Long.toString(System.currentTimeMillis() - beginTimeForDebugUsage));
			beginTimeForDebugUsage = System.currentTimeMillis();

			if (returnValueType == void.class) {
				resultXml = SimpleMetoXmlResult.resultToXml(null,
						returnValueType, DefaultCharset,
						_xmlResultSetting.isUseClassFullName(),
						_xmlResultSetting.isMakeFirstCharUpperCase()
						);
			} else {
				if(returnValue != null) {
					resultXml = SimpleMetoXmlResult.resultToXml(returnValue,
							returnValue.getClass(), DefaultCharset,
							_xmlResultSetting.isUseClassFullName(),
							_xmlResultSetting.isMakeFirstCharUpperCase()
							);
				} else {
					resultXml = SimpleMetoXmlResult.resultToXml(returnValue,
							returnValueType, DefaultCharset,
							_xmlResultSetting.isUseClassFullName(),
							_xmlResultSetting.isMakeFirstCharUpperCase()
							);
				}
			}

			logger.debug("invokeService() time usage(ms):" + Long.toString(System.currentTimeMillis() - beginTimeForDebugUsage) + " resultXml:\r\n" + resultXml);

			// Handler after invoke
			// service************************************************************
			/* Not support any more
			handleLogicAfterInvokeService(sessionAfterHandlerType,
					sessionAfterHandlerMethod,
					paramTypeClass, paramValue,
					returnValueType, returnValue,
					sessionWrapper);
			*/
			return resultXml;
		} catch (MethodAccessNoAuthorityException e) {
			return SimpleMetoXmlResult.errorResultXml((MethodAccessNoAuthorityException)e);
		} catch (ClassNotFoundException e) {
			logger.error("invokeService()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (IOException e) {
			logger.error("invokeService()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (XmlParseException e) {
			logger.error("invokeService()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (InvocationTargetException e) {
			logger.error("invokeService()",e);
			Throwable cause = e.getCause(); 
			if(cause != null) {
				if(cause.getClass() == ServiceErrorMsgException.class) {
					return SimpleMetoXmlResult.errorResultXml((ServiceErrorMsgException)cause);
				} else if (e.getCause().getClass() == MethodAccessNoAuthorityException.class) {
					return SimpleMetoXmlResult.errorResultXml((MethodAccessNoAuthorityException)cause);
				} else {
					return SimpleMetoXmlResult.errorResultXml(e);
				}
			} else {
				return SimpleMetoXmlResult.errorResultXml(e);
			}
		} catch (IllegalAccessException e) {
			logger.error("invokeService()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (InstantiationException e) {
			logger.error("invokeService()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (NoSuchMethodException e) {
			logger.error("invokeService()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (Exception e) {
			logger.error("invokeService()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		}

	}

/*	
	private void handleLogicBeforeInvokeService(
			Method serviceMethod, 
			String sessionHandlerType, String sessionHandlerMethod,
			Class<?> paramTypeClass, Object paramValue,
			SessionWrapper sessionWrapper) 
	throws NoSuchMethodException,ClassNotFoundException, InvocationTargetException, 
	InstantiationException, IllegalAccessException, MethodAccessNoAuthorityException
	{
		// Access authorize
		checkServiceAccessAuthority(serviceMethod, sessionWrapper);

		// Session prehandler
		if (sessionHandlerType != null && sessionHandlerType.trim().length() > 0) {
			handleSession(sessionHandlerType, sessionHandlerMethod, paramTypeClass,
					paramValue, sessionWrapper);
		}
	}

	private void handleLogicAfterInvokeService(
			String sessionHandlerType,
			String sessionHandlerMethod, Class<?> paramTypeClass, Object paramValue,
			Class<?> returnTypeClass, Object returnValue,
			SessionWrapper sessionWrapper) throws NoSuchMethodException,
			ClassNotFoundException, InvocationTargetException,
			InstantiationException, IllegalAccessException {
		// Session prehandler
		if (sessionHandlerType != null && sessionHandlerType.trim().length() > 0) {
			handleSession(sessionHandlerType, sessionHandlerMethod, paramTypeClass,
					paramValue, returnTypeClass, returnValue, sessionWrapper);
		}
	}

	private void handleSession(String handlerType, String handlerMethod,
			Class<?> paramTypeClass, Object paramValue,
			SessionWrapper sessionWrapper) throws NoSuchMethodException,
			ClassNotFoundException, InvocationTargetException,
			InstantiationException, IllegalAccessException {
		Class<?> sessionInterceptorTypeClass = _serviceClassFinder.findClass(handlerType);
		
		Method methodOfSessionInterceptor = null;
		if(paramTypeClass == void.class) {
			methodOfSessionInterceptor = MethodInvokeUtil.GetMethod(
					sessionInterceptorTypeClass, handlerMethod, 
					new Class[] {SessionWrapper.class});
		} else {
			methodOfSessionInterceptor = MethodInvokeUtil.GetMethod(
					sessionInterceptorTypeClass, handlerMethod, new Class[] {
							SessionWrapper.class, paramTypeClass });
		}
		logger.debug("handleSession() invoke the sessionHandler:" + handlerType
				+ "." + handlerMethod + "(HttpServletRequest, "
				+ paramTypeClass.getName() + ")");

		Object service = null;
		if(!MethodInvokeUtil.IsMethodStatic(methodOfSessionInterceptor)) {
			service = sessionInterceptorTypeClass.newInstance();
		}
		
		if(paramTypeClass == void.class) {
			MethodInvokeUtil.InvokeMethod(service, methodOfSessionInterceptor, 
					new Object[] {sessionWrapper});
		} else {
			MethodInvokeUtil.InvokeMethod(service, methodOfSessionInterceptor, new Object[] {
					sessionWrapper, paramValue });
		}
	}

	private void handleSession(String handlerType, String handlerMethod,
			Class<?> paramTypeClass, Object paramValue,
			Class<?> returnTypeClass, Object returnValue,
			SessionWrapper sessionWrapper) throws NoSuchMethodException,
			ClassNotFoundException, InvocationTargetException,
			InstantiationException, IllegalAccessException {
		Class<?> sessionInterceptorTypeClass = _serviceClassFinder.findClass(handlerType);
		
		Method methodOfSessionInterceptor = null;
		
		logger.debug("handleSession() invoke the sessionHandler:" + handlerType
				+ "." + handlerMethod + "(HttpServletRequest, "
				+ paramTypeClass.getName() + ")");
		
		Object service = null;
		
		if(paramTypeClass == void.class) {
			if(returnTypeClass == void.class) {
				methodOfSessionInterceptor = MethodInvokeUtil.GetMethod(
						sessionInterceptorTypeClass, handlerMethod, 
						new Class[] {SessionWrapper.class});

				if(!MethodInvokeUtil.IsMethodStatic(methodOfSessionInterceptor)) {
					service = sessionInterceptorTypeClass.newInstance();
				}

				MethodInvokeUtil.InvokeMethod(service, methodOfSessionInterceptor, 
						new Object[] {sessionWrapper});
			} else {
				methodOfSessionInterceptor = MethodInvokeUtil.GetMethod(
						sessionInterceptorTypeClass, handlerMethod, 
						new Class[] {SessionWrapper.class, returnTypeClass});
				
				if(!MethodInvokeUtil.IsMethodStatic(methodOfSessionInterceptor)) {
					service = sessionInterceptorTypeClass.newInstance();
				}

				MethodInvokeUtil.InvokeMethod(service, methodOfSessionInterceptor, 
						new Object[] {sessionWrapper, returnValue});
			}
		} else {
			if(returnTypeClass == void.class) {
				methodOfSessionInterceptor = MethodInvokeUtil.GetMethod(
						sessionInterceptorTypeClass, handlerMethod, new Class[] {
								SessionWrapper.class, paramTypeClass });

				if(!MethodInvokeUtil.IsMethodStatic(methodOfSessionInterceptor)) {
					service = sessionInterceptorTypeClass.newInstance();
				}

				MethodInvokeUtil.InvokeMethod(service, methodOfSessionInterceptor, 
						new Object[] {sessionWrapper, paramValue});
			} else {
				methodOfSessionInterceptor = MethodInvokeUtil.GetMethod(
						sessionInterceptorTypeClass, handlerMethod, new Class[] {
								SessionWrapper.class, paramTypeClass, returnTypeClass});

				if(!MethodInvokeUtil.IsMethodStatic(methodOfSessionInterceptor)) {
					service = sessionInterceptorTypeClass.newInstance();
				}

				MethodInvokeUtil.InvokeMethod(service, methodOfSessionInterceptor, 
						new Object[] {sessionWrapper, paramValue, returnValue});
			}
		}
	}
	*/
	
	public String setSessionValue(
			String key,
			String dataType, 
			String property,
			String propertyType, 
			String valueXml,
			SessionWrapper session) {
		try {
			logger.debug("setSessionValue():"
					+ "SessionKey:" + key + ";dataType:" + dataType 
					+ ";property:" + property + ";propertyType:" + propertyType);
			logger.debug("setSessionValue() valueXml:\r\n" + valueXml);

			Class<?> dataTypeClass = getDataType(dataType);
			
			Class<?> propertyTypeClass = null;
			
			if(property != null && property.length() > 0
					&& propertyType != null && propertyType.length() > 0)
			{
				propertyTypeClass = getDataType(propertyType);
			}

			setSessionValue(session, key, dataTypeClass, property,
					propertyTypeClass, valueXml);

			return SimpleMetoXmlResult.emptyResultXml();
		} catch (ClassNotFoundException e) {
			logger.error("setSessionValue()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (OgnlException e) {
			logger.error("setSessionValue()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (NoSuchMethodException e) {
			logger.error("setSessionValue()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (InstantiationException e) {
			logger.error("setSessionValue()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (IllegalAccessException e) {
			logger.error("setSessionValue()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (InvocationTargetException e) {
			logger.error("setSessionValue()",e);
			Throwable cause = e.getCause(); 
			if(cause != null) {
				if(ServiceErrorMsgException.class.isAssignableFrom(cause.getClass())) {
					return SimpleMetoXmlResult.errorResultXml((ServiceErrorMsgException)cause);
				} else if (MethodAccessNoAuthorityException.class.isAssignableFrom(cause.getClass())) {
					return SimpleMetoXmlResult.errorResultXml((MethodAccessNoAuthorityException)cause);
				} else {
					return SimpleMetoXmlResult.errorResultXml(e);
				}
			} else {
				return SimpleMetoXmlResult.errorResultXml(e);
			}
		} catch (XmlParseException e) {
			logger.error("setSessionValue()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (IOException e) {
			logger.error("setSessionValue()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (Exception e) {
			logger.error("setSessionValue()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		}
	}

	public String setSessionValues(String sessionValueSetXml,
			SessionWrapper session) {
		logger.debug("setSessionValues() sessionValueSetXml:\r\n" + sessionValueSetXml);

		try {
			SessionValueSet sessionValueSet = (SessionValueSet) XmlDeserializer
					.stringToObject(sessionValueSetXml, SessionValueSet.class);

			setSessionValues(session, sessionValueSet);

			return SimpleMetoXmlResult.emptyResultXml();
		} catch (IOException e) {
			logger.error("setSessionValues()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (XmlParseException e) {
			logger.error("setSessionValues()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (InvocationTargetException e) {
			logger.error("setSessionValues()",e);
			Throwable cause = e.getCause(); 
			if(cause != null) {
				if(ServiceErrorMsgException.class.isAssignableFrom(cause.getClass())) {
					return SimpleMetoXmlResult.errorResultXml((ServiceErrorMsgException)cause);
				} else if (MethodAccessNoAuthorityException.class.isAssignableFrom(cause.getClass())) {
					return SimpleMetoXmlResult.errorResultXml((MethodAccessNoAuthorityException)cause);
				} else {
					return SimpleMetoXmlResult.errorResultXml(e);
				}
			} else {
				return SimpleMetoXmlResult.errorResultXml(e);
			}
		} catch (IllegalAccessException e) {
			logger.error("setSessionValues()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (InstantiationException e) {
			logger.error("setSessionValues()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (NoSuchMethodException e) {
			logger.error("setSessionValues()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (OgnlException e) {
			logger.error("setSessionValues()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (ClassNotFoundException e) {
			logger.error("setSessionValues()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (Exception e) {
			logger.error("setSessionValues()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		}
	}

	public String getSessionValue(String key,
			String dataType, String property,
			String propertyType, SessionWrapper session) {
		try {
			logger.debug("setSessionValue():"
					+ "SessionKey:" + key + ";dataType:" + dataType 
					+ ";property:" + property + ";propertyType:" + propertyType);

			Class<?> dataTypeClass = getDataType(dataType);

			Class<?> propertyTypeClass = null;
			
			if(property != null && property.length() > 0
					&& propertyType != null && propertyType.length() > 0)
			{
				propertyTypeClass = getDataType(propertyType);
			}

			String valueXml = getSessionValueXml(session, key, dataTypeClass,
					property, propertyTypeClass);

			logger.debug("getSessionValue() returnValueXml:\r\n" + valueXml);
			
			return valueXml;
		} catch (ClassNotFoundException e) {
			logger.error("getSessionValue()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (IOException e) {
			logger.error("getSessionValue()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (InvocationTargetException e) {
			logger.error("getSessionValue()",e);
			Throwable cause = e.getCause(); 
			if(cause != null) {
				if(ServiceErrorMsgException.class.isAssignableFrom(cause.getClass())) {
					return SimpleMetoXmlResult.errorResultXml((ServiceErrorMsgException)cause);
				} else if (MethodAccessNoAuthorityException.class.isAssignableFrom(cause.getClass())) {
					return SimpleMetoXmlResult.errorResultXml((MethodAccessNoAuthorityException)cause);
				} else {
					return SimpleMetoXmlResult.errorResultXml(e);
				}
			} else {
				return SimpleMetoXmlResult.errorResultXml(e);
			}
		} catch (IllegalAccessException e) {
			logger.error("getSessionValue()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (IntrospectionException e) {
			logger.error("getSessionValue()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (OgnlException e) {
			logger.error("getSessionValue()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		} catch (Exception e) {
			logger.error("getSessionValue()",e);
			return SimpleMetoXmlResult.errorResultXml(e);
		}
	}

	private static void setSessionValue(SessionWrapper session, String key,
			Class<?> dataTypeClass, String property, Class<?> propertyTypeClass,
			String valueXml) throws OgnlException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, XmlParseException, IOException {
		if (property == null || property.trim().length() == 0) {
			// Data
			Object value = XmlDeserializer.stringToObject(valueXml,
					dataTypeClass);
			session.setAttribute(key, value);
		} else {
			Object data = session.getAttribute(key);

			if (data == null) {
				data = dataTypeClass.newInstance();
				session.setAttribute(key, data);
			}

			setDataPropertyValue(data, property, propertyTypeClass, valueXml);
		}
	}

	private void setSessionValues(SessionWrapper session,
			SessionValueSet sessionValueSet) throws OgnlException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			XmlParseException, IOException, ClassNotFoundException {
		MapValueSet valueSet = null;
		String key = null;
		Class<?> dataTypeClass = null;
		String property = null;
		String propertyType = null;
		Class<?> propertyTypeClass = null;
		String valueXml = null;

		for (int i = 0; i < sessionValueSet.getSessionValueSets().size(); i++) {
			valueSet = sessionValueSet.getSessionValueSets().get(i);

			key = valueSet.getKey();
			dataTypeClass = getDataType(valueSet.getDataType());
			property = valueSet.getProperty();
			
			propertyType = valueSet.getPropertyType();
			
			if(property != null && property.trim().length() > 0
					&& propertyType != null && propertyType.trim().length() > 0)
			{
				propertyTypeClass = getDataType(propertyType);
			} else {
				propertyTypeClass = null;
			}
			
			valueXml = valueSet.getValueXml();

			setSessionValue(session, key, dataTypeClass, property,
					propertyTypeClass, valueXml);
		}
	}

	private void checkServiceAccessAuthority(
			Method serviceMethod, 
			SessionWrapper session) throws MethodAccessNoAuthorityException 
	{
		Authentication authentication = (Authentication) session.getAttribute(session.getId());
		
		boolean accessible;
		if((authentication != null)
				&& (authentication.getUserData() != null)) {
			accessible = _methodAccessController.isMethodAccessible(
					serviceMethod, 
					authentication.getUserData().getAuthorities());
		} else {
			accessible = _methodAccessController.isMethodAccessible(serviceMethod, null);
		}
		
		if(!accessible) {
			throw new MethodAccessNoAuthorityException("User has no authority to access this method:" + serviceMethod.getName());
		}
		
	}
	
	
	private String getSessionValueXml(SessionWrapper session, String key,
			Class<?> dataTypeClass, String property, Class<?> propertyTypeClass)
			throws IOException, InvocationTargetException,
			IllegalAccessException, IntrospectionException, OgnlException {
		Object data = session.getAttribute(key);
		String resultXml = "";

		if (data == null) {
			resultXml = SimpleMetoXmlResult.emptyResultXml();

			logger.debug("getSessionValueXml() resultXml:\r\n" + resultXml);
			return resultXml;
		}

		if (property == null || property.trim().length() == 0) {
			// Data
			resultXml = SimpleMetoXmlResult.resultToXml(session.getAttribute(key),
					dataTypeClass, DefaultCharset,
					_xmlResultSetting.isUseClassFullName(),
					_xmlResultSetting.isMakeFirstCharUpperCase()
					);  
		} else {
			Object propertyValue = getDataPropertyValue(data, property,
					propertyTypeClass);
			
			resultXml = SimpleMetoXmlResult.resultToXml(propertyValue, propertyTypeClass, DefaultCharset,
					_xmlResultSetting.isUseClassFullName(),
					_xmlResultSetting.isMakeFirstCharUpperCase()
					);
		}
		
		logger.debug("getSessionValueXml() resultXml:\r\n" + resultXml);
		return resultXml;
	}

	private static void setDataPropertyValue(Object data, String property,
			Class<?> propertyTypeClass, String valueXml) throws OgnlException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			XmlParseException, IOException {
		if (property != null && property.trim().length() > 0) {
			Object value = XmlDeserializer.stringToObject(valueXml,
					propertyTypeClass);
			Ognl.setValue(property, data, value);
		}
	}

	private static Object getDataPropertyValue(Object data, String property,
			Class<?> propertyTypeClass) throws OgnlException, IOException,
			InvocationTargetException, IllegalAccessException,
			IntrospectionException {
		Object value = null;
		if (property != null && property.length() > 0) {
			value = Ognl.getValue(property, data);
		}

		return value;
	}
}
