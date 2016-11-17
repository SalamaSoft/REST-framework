package com.salama.service.clouddata;


import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javassist.NotFoundException;
import javassist.bytecode.LocalVariableAttribute;

import org.apache.log4j.Logger;

import MetoXML.Cast.BaseTypesMapping;

import com.salama.reflect.MethodInvokeUtil;
import com.salama.reflect.PreScanClassFinder;
import com.salama.service.clouddata.core.AppContext;
import com.salama.service.clouddata.core.AppException;
import com.salama.service.clouddata.core.AppServiceFilter.ServiceFilterResult;
import com.salama.service.clouddata.core.ICloudDataService;
import com.salama.service.clouddata.core.annotation.ClientService;
import com.salama.service.clouddata.core.annotation.ClientService.NotificationNameParamType;
import com.salama.service.clouddata.core.annotation.ConverterType;
import com.salama.service.clouddata.core.annotation.ReturnValueConverter;
import com.salama.service.clouddata.defaultsupport.DefaultSupportService;
import com.salama.service.clouddata.util.JavaAssistUtil;
import com.salama.service.clouddata.util.SimpleJSONDataUtil;
import com.salama.service.clouddata.util.XmlDataUtil;
import com.salama.service.core.annotation.AccessibleRoles;
import com.salama.service.core.auth.MethodAccessNoAuthorityException;
import com.salama.service.core.net.RequestWrapper;
import com.salama.service.core.net.ResponseWrapper;
import com.salama.service.core.net.http.ContentTypeHelper;
import com.salama.service.core.net.http.MultipartFile;
import com.salama.service.core.net.http.MultipartRequestWrapper;

/**
 * 
 * @author XingGu Liu
 *
 */
public final class CloudDataService implements ICloudDataService {
	private static Logger logger = Logger.getLogger(CloudDataService.class);
	
	static {
		logger.info("VERSION:1.9.0(20160914)");
	}
	
	public static final String DefaultEncoding = "utf-8";
	public static final Charset DefaultCharset = Charset.forName(DefaultEncoding);
	public static final String DefaultContentTypeCharset = ";charset=utf-8";
	
	private static final String ReturnValue_Xml_MethodAccessNoAuthority = 
			"<Error><type>MethodAccessNoAuthorityException</type></Error>";
	private static final String ReturnValue_Json_MethodAccessNoAuthority = 
			"{\"type\":\"MethodAccessNoAuthorityException\"}";
	
	private static final String ClassName_DefaultSupportService = DefaultSupportService.class.getName();
	
	//public static final String HTTP_HEAD_NAME_WEB_CLIENT_SERVICE = "webclientservice_callback";

	private final static Class<?>[] StandardCloudDataServiceParamTypes = new Class[]{RequestWrapper.class, ResponseWrapper.class}; 
	
	private PreScanClassFinder _allAppClassFinder = null;
	
	//key:serviceClassName value:AppContext
	private HashMap<String, AppContext> _serviceClassContextMap = null;
	
	public CloudDataService(PreScanClassFinder allAppClassFinder, HashMap<String, AppContext> serviceClassContextMap) {
		_allAppClassFinder = allAppClassFinder;
		_serviceClassContextMap = serviceClassContextMap;
	}
	
	@Override
	public String cloudDataService(
			String serviceType,
			String serviceMethod, 
			RequestWrapper request,
			ResponseWrapper response) {
		try {
			if(logger.isDebugEnabled()) {
				logger.debug("cloudDataService() serviceType:" + serviceType + " serviceMethod:" + serviceMethod);
			}

			response.setStatus(HttpServletResponse.SC_OK);
			
			AppContext appContext = _serviceClassContextMap.get(serviceType);
			
			//verify accessible of serviceType
			if(appContext == null) {
				if(serviceType.equals(ClassName_DefaultSupportService)) {
					//in DefaultSupport
				} else {
					throw new RuntimeException("Service(" + serviceType + ") is not under exposed packge, then not allowed to invoke");
				}
			} else {
				if(!((CloudDataAppContext)appContext).isPackageExposed(serviceType)) {
					throw new RuntimeException("Service(" + serviceType + ") is not under exposed packge, then not allowed to invoke");
				}
			}
			
			
			Class<?> serviceTypeClass = getServiceType(serviceType);
			Method method = findMethod(serviceTypeClass, serviceMethod);

			//check the authority of accessible
			try {
				boolean isAccessible = checkServiceAccessAuthority(method, request, appContext);
				
				if(!isAccessible) {
					logger.error("No authority to access method:" + serviceType + "." + serviceMethod + "()");
					return "";
				}
			} catch(MethodAccessNoAuthorityException noAuthError) {
				return ReturnValue_Xml_MethodAccessNoAuthority;
			}
			
			String returnVal = invokeMethod(serviceType, serviceMethod, serviceTypeClass, method, request, response, appContext);
			if(logger.isDebugEnabled()) {
				logger.debug("returnValue:\r\n" + returnVal);
			}
			return returnVal;
		} catch (Exception e) {
			logger.error("dataService()", e);
			return null;
		} 
	}
	
	private Class<?> getServiceType(String className) throws ClassNotFoundException {
		//get class of serviceType
		if(className.indexOf('.') < 0) {
			char firstChar = className.charAt(0);
			if(firstChar >= 'a' && firstChar <= 'z') {
				return _allAppClassFinder.findClass(
						className.substring(0, 1).toUpperCase().concat(
								className.substring(1)));
			} else {
				return _allAppClassFinder.findClass(className);
			}
		} else {
			return _allAppClassFinder.findClass(className);
		}
	}
	
	private static Method findMethod(Class<?> serviceTypeClass, String serviceMethod) {
		Method method = null;
		
		try {
			method = MethodInvokeUtil.GetMethod(serviceTypeClass,
					serviceMethod, StandardCloudDataServiceParamTypes);
			if((method.getModifiers() & Modifier.PUBLIC) != 0) {
				throw new NoSuchMethodException();
			}
			
			return method;
		} catch (NoSuchMethodException e) {
			//find by name
			Method[] methods = serviceTypeClass.getMethods();
			for(Method methodTmp : methods) {
				if(methodTmp.getName().equals(serviceMethod)
						&& (methodTmp.getModifiers() & Modifier.PUBLIC) != 0
						) {
					method = methodTmp;
					break;
				}
			}
			
			return method;
		}
	}
	
	private static String invokeMethod(
			String serviceType, String serviceMethod,
			Class<?> serviceTypeClass, Method method, 
			RequestWrapper request,
			ResponseWrapper response, 
			AppContext appContext
			) throws IntrospectionException, IllegalAccessException, InvocationTargetException, IOException, InstantiationException, NotFoundException {
		//Invoke method ----------------------------------------------
		//create service instance if this method is not static method
		Object service = null;

		boolean isStaticMethod = true;
		if(!MethodInvokeUtil.IsMethodStatic(method)) {
			service = serviceTypeClass.newInstance();
			isStaticMethod = false;
		}
		
		//before invoke set the default content type
		ReturnValueConverter returnValueConverter = null;
		try {
			returnValueConverter = method.getAnnotation(ReturnValueConverter.class);
		} catch(Throwable e) {
			logger.error(null, e);
		}
		
		String callbackFunc = null;
		String notificationName = null;
		ClientService clientService = null;
		try {
			clientService = method.getAnnotation(ClientService.class);
		} catch(Throwable e) {
			logger.error(null, e);
		}

		/*
		if(returnValueConverter != null) {
			if(returnValueConverter.value() == ConverterType.PLAIN_TEXT) {
				response.setContentType(ContentTypeHelper.TextPlain);
			} else if(returnValueConverter.value() == ConverterType.XML) {
				response.setContentType("text/xml");
			} else if(returnValueConverter.value() == ConverterType.JSON) {
				response.setContentType("text/json");
			}
		} else {
			response.setContentType(ContentTypeHelper.TextPlain);
		}
		*/
		
		try {

			Class<?>[] paramTypes = method.getParameterTypes();
			boolean isStandardServiceMethd = false;
			if((paramTypes.length == 2)
					&& RequestWrapper.class.isAssignableFrom(paramTypes[0])
					&& ResponseWrapper.class.isAssignableFrom(paramTypes[1])
					) {
				isStandardServiceMethd = true;
			}
			
			Object returnValue = null;

			//String webServiceClientCallBack = ((HttpServletRequest)request.getRequest()).getHeader(HTTP_HEAD_NAME_WEB_CLIENT_SERVICE);
			//logger.debug("webServiceClientCallBack:" + webServiceClientCallBack);

			//appServiceFilter
			boolean isNeedDoAppServiceFilter = false;
			if(appContext != null && CloudDataAppContext.class.isAssignableFrom(appContext.getClass())) {
				/* moved to the position before getServiceType()
				if(!((CloudDataAppContext)appContext).isPackageExposed(serviceType)) {
					throw new RuntimeException("Service(" + serviceType + ") is not under exposed packge, then not allowed to invoke");
				}
				*/

				if(((CloudDataAppContext)appContext).getAppServiceFilter() != null) {
					isNeedDoAppServiceFilter = true;
				}
			} else {
				/* moved to the position before getServiceType()
				//check whether the serviceType is enabled to be exposed
				if(serviceTypeClass.getPackage().getName().equals(
						DefaultSupportService.class.getPackage().getName())) {
					//in DefaultSupport
				} else {
					throw new RuntimeException("Service(" + serviceType + ") is not under exposed packge, then not allowed to invoke");
				}
				*/
			}
			
			if(isStandardServiceMethd) {
				if(isNeedDoAppServiceFilter) {
					ServiceFilterResult serviceFilterResult = ((CloudDataAppContext)appContext).getAppServiceFilter().filter(
							request, response, isStandardServiceMethd, method, service, null);
					if(serviceFilterResult != null && serviceFilterResult.isServiceOverrided) {
						returnValue = serviceFilterResult.serviceReturnValue;
					} else {
						returnValue = method.invoke(service, request, response);
					}
				} else {
					returnValue = method.invoke(service, request, response);
				}
			} else {
				//handle parameters ----------
				Object[] paramValues = null;
				if(clientService == null) {
					//parameter value is achieved from request parameter
					if(paramTypes.length > 0) {
						paramValues = new Object[paramTypes.length];

						LocalVariableAttribute localVarAttr = JavaAssistUtil.getLocalVariableAttribute(serviceType, serviceMethod);
						if(localVarAttr == null) {
							throw new RuntimeException("getLocalVariableAttribute() failed. ServiceType:" + serviceType + " serviceMethod:" + serviceMethod);
						}

						/* no need to decode URI parameter, because the web server has decoded it.
						boolean isParamNeedDecode = isParamNeedDecode(request, returnValueConverter);
						String requestEncoding = request.getCharacterEncoding();
						if(requestEncoding == null || requestEncoding.length() == 0) {
							requestEncoding = "utf-8";
						}
						*/
						
						Class<?> paramType = null;
						String paramName = null;
						for(int i = 0; i < paramTypes.length; i++) {
							paramType = paramTypes[i];
							paramName = JavaAssistUtil.getParameterName(localVarAttr, i, isStaticMethod);
							
							if(paramType == (String.class)) {
								/* no need to decode URI parameter, because the web server has decoded it.
								if(isParamNeedDecode) {
									paramValues[i] = getDecodedRequestParam(request, paramName, requestEncoding);
								} else {
									paramValues[i] = request.getParameter(paramName);
								}
								*/

								paramValues[i] = request.getParameter(paramName);
								if(logger.isDebugEnabled()) {
									logger.debug("paramName:" + paramName + " paramValue:" + paramValues[i]);
								}
							} else if (paramType == (MultipartFile.class)) {
								paramValues[i] = ((MultipartRequestWrapper)request).getFile(paramName);
								if(logger.isDebugEnabled()) {
									logger.debug("paramName:" + paramName + " (MultiPartFile)");
								}
							} else if (paramType == (RequestWrapper.class)) {
								paramValues[i] = request;
							} else if (paramType == (ResponseWrapper.class)) {
								paramValues[i] = response;
							} else if (paramType.isPrimitive()) {
								String val;
								/* no need to decode URI parameter, because the web server has decoded it.
								if(isParamNeedDecode) {
									val = getDecodedRequestParam(request, paramName, requestEncoding);
								} else {
									val = request.getParameter(paramName);
								}
								*/

								val = request.getParameter(paramName);
								paramValues[i] = convertStringToPrimitiveType(val, paramType);
								if(logger.isDebugEnabled()) {
									logger.debug("paramName:" + paramName + " paramValue:" + val);
								}
							} else {
								throw new RuntimeException("Not support the param type:" + paramType.getName());
							}
						}
					}
				} else {
					int index = 0;
					String paramNamePrefix = "params[";
					String paramNameSuffix = "]";

					if(paramTypes.length > 0) {
						paramValues = new Object[paramTypes.length];

						/* no need to decode URI parameter, because the web server has decoded it.
						boolean isParamNeedDecode = isParamNeedDecode(request, returnValueConverter);
						String requestEncoding = request.getCharacterEncoding();
						if(requestEncoding == null || requestEncoding.length() == 0) {
							requestEncoding = "utf-8";
						}
						*/
						
						Class<?> paramType = null;
						for(int i = 0; i < paramTypes.length; i++) {
							paramType = paramTypes[i];

							if (paramType == (RequestWrapper.class)) {
								paramValues[i] = request;
							} else if (paramType == (ResponseWrapper.class)) {
								paramValues[i] = response;
							} else {
								String paramName = paramNamePrefix + Integer.toString(index++) + paramNameSuffix;

								if(clientService.notificationNameParamType() == NotificationNameParamType.FirstParam
										&& index == 0) {
									notificationName = request.getParameter(paramName);
								} 

								if(paramType == (String.class)) {
									/* no need to decode URI parameter, because the web server has decoded it.
									if(isParamNeedDecode) {
										paramValues[i] = getDecodedRequestParam(request, paramName, requestEncoding);
									} else {
										paramValues[i] = request.getParameter(paramName);
									}
									*/

									paramValues[i] = request.getParameter(paramName);
									if(logger.isDebugEnabled()) {
										logger.debug("paramName:" + paramName + " paramValue:" + paramValues[i]);
									}
								} else if (paramType == (MultipartFile.class)) {
									paramValues[i] = ((MultipartRequestWrapper)request).getFile(paramName);
									if(logger.isDebugEnabled()) {
										logger.debug("paramName:" + paramName + " (MultiPartFile)");
									}
								} else if (paramType.isPrimitive()) {
									String val;
									/* no need to decode URI parameter, because the web server has decoded it.
									if(isParamNeedDecode) {
										val = getDecodedRequestParam(request, paramName, requestEncoding);
									} else {
										val = request.getParameter(paramName);
									}
									*/

									val = request.getParameter(paramName);
									paramValues[i] = convertStringToPrimitiveType(val, paramType);
									if(logger.isDebugEnabled()) {
										logger.debug("paramName:" + paramName + " paramValue:" + val);
									}
								} else {
									throw new RuntimeException("Not support the param type:" + paramType.getName());
								}
							} //
						} // for
					}

					if(clientService.notificationNameParamType() == NotificationNameParamType.ByName) {
						notificationName = request.getParameter(clientService.notificationNameFromRequestParam());
					} else if(clientService.notificationNameParamType() == NotificationNameParamType.LastParam) {
						String paramName = paramNamePrefix + Integer.toString(index) + paramNameSuffix;
						notificationName = request.getParameter(paramName);
						
						if(notificationName == null && index > 0) {
							paramName = paramNamePrefix + Integer.toString(index-1) + paramNameSuffix;
						}
					}
					if(notificationName != null) {
						notificationName = notificationName.trim();
					}
					
					if(clientService.callBackNameFromRequestParam() != null) {
						callbackFunc = request.getParameter(clientService.callBackNameFromRequestParam());
						if(callbackFunc != null) {
							callbackFunc = callbackFunc.trim();
						}
					}
				} //if
				
				//invoke ----------
				if(isNeedDoAppServiceFilter) {
					ServiceFilterResult serviceFilterResult = ((CloudDataAppContext)appContext).getAppServiceFilter().filter(
							request, response, isStandardServiceMethd, method, service, paramValues);
					if(serviceFilterResult != null && serviceFilterResult.isServiceOverrided) {
						returnValue = serviceFilterResult.serviceReturnValue;
					} else {
						returnValue = method.invoke(service, paramValues);
					}
				} else {
					returnValue = method.invoke(service, paramValues);
				}
			}
			
			//invoke the method
			if(method.getReturnType() == (void.class)) {
				return null;
			} else {
				//convert return value
				return convertReturnValue(request, response, 
						returnValueConverter, returnValue, 
						clientService, callbackFunc, notificationName,
						false
						);
			}
		} catch (InvocationTargetException e) {
			if(e.getCause() != null && e.getCause().getClass() == MethodAccessNoAuthorityException.class) {
				String returnValue = ReturnValue_Xml_MethodAccessNoAuthority;
				if(returnValueConverter != null && returnValueConverter.valueFromRequestParam() != null) {
					String strConverterType = request.getParameter(returnValueConverter.valueFromRequestParam());
					if(strConverterType != null 
							&& strConverterType.startsWith(ReturnValueConverter.COVERT_TYPE_JSON)
					) {
						returnValue = ReturnValue_Json_MethodAccessNoAuthority;
					}
				} 
				
				return convertReturnValue(request, response, 
						returnValueConverter, returnValue, 
						clientService, callbackFunc, notificationName,
						true
						);
			} else {
				throw e;
			}
		}
	}
		
	private static Object  convertStringToPrimitiveType(String valueStr, Class<?> cls) {
		if(valueStr == null) {
			if(cls == boolean.class) {
				return false;
			} else {
				return 0;
			}
		} else {
			if(cls == boolean.class) {
				return Boolean.valueOf(valueStr);
			} else if(cls == byte.class) {
		    	return Byte.valueOf(valueStr);
	    	} else if(cls == short.class) {
	    		return Short.valueOf(valueStr);
			} else if(cls == int.class) {
				return Integer.valueOf(valueStr);
			} else if(cls == long.class) {
				return Long.valueOf(valueStr);
			} else if(cls == float.class) {
				return Float.valueOf(valueStr);
			} else if(cls == double.class) {
				return Double.valueOf(valueStr);
			} else if(cls == char.class) {
				return (char)Integer.parseInt(valueStr);
			} else if(cls == Boolean.class) {
				return Boolean.valueOf(valueStr);
			} else if(cls == Byte.class) {
		    	return Byte.valueOf(valueStr);
	    	} else if(cls == Short.class) {
	    		return Short.valueOf(valueStr);
			} else if(cls == Integer.class) {
				return Integer.valueOf(valueStr);
			} else if(cls == Long.class) {
				return Long.valueOf(valueStr);
			} else if(cls == Float.class) {
				return Float.valueOf(valueStr);
			} else if(cls == Double.class) {
				return Double.valueOf(valueStr);
			} else if(cls == Character.class) {
				return (char)Integer.parseInt(valueStr);
			} else {
				//not support
				logger.error("Not support the param type:" + cls.getName());
				return null;
			}
		}
	}
	
	private static boolean checkServiceAccessAuthority(
			Method method, RequestWrapper request, AppContext appContext
			) throws MethodAccessNoAuthorityException 
	{
		AccessibleRoles accessibleRoles = null;

		try {
			accessibleRoles = method.getAnnotation(AccessibleRoles.class);
		} catch(Exception ex) {
		}
		
		if(accessibleRoles == null || accessibleRoles.roles() == null || accessibleRoles.roles().length == 0 ) {
			return true;
		} else {
			//String authTicket = request.getParameter(AUTH_TICKET);
			String authTicket = getAuthTicketFromRequest(request);

			boolean isAccessible = false;

			String role = null;
			try {
				role = appContext.getAuthUserInfo(authTicket).getRole();
			} catch(Throwable e) {
				//return false;
			}

			for(String roleTmp : accessibleRoles.roles()) {
				if(roleTmp.equals(role)) {
					isAccessible = true;
					break;
				}
			}
			
			if(!isAccessible && accessibleRoles.returnError()) {
				throw new MethodAccessNoAuthorityException();
			}  else {
				return isAccessible;
			}
		}
	}

/*	
	private static boolean isParamNeedDecode(RequestWrapper request, ReturnValueConverter returnValueConverter) {
		if(returnValueConverter != null) {
			if((returnValueConverter.valueFromRequestParam() != null) 
					&& (returnValueConverter.valueFromRequestParam().length() > 0) 
					) {
				// When valueFromRequestParam() is not null, then default converter type is set to xml.
				String strConverterType = request.getParameter(returnValueConverter.valueFromRequestParam());
				
				if(strConverterType == null) {
					return false;
				} else {
					strConverterType = strConverterType.trim().toLowerCase();
					
					if(strConverterType.endsWith(".jsonp")) {
						return true;
					} else {
						return false;
					}
				}
			}  else {
				if(returnValueConverter.value() == ConverterType.XML_JSONP 
						|| returnValueConverter.value() == ConverterType.JSON_JSONP
						|| returnValueConverter.value() == ConverterType.PLAIN_TEXT_JSONP
						) {
					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}
	
	
	private static String getDecodedRequestParam(RequestWrapper request, String paramName, String encoding) {
		String rawValue = request.getParameter(paramName);
		try {
			String urlDecodedVal = URLDecoder.decode(rawValue, encoding);
			return new String(urlDecodedVal.getBytes("iso-8859-1"), encoding);
		} catch (Exception e) {
			return rawValue;
		}
	}
*/	
	private static String convertReturnValue(
			RequestWrapper request, ResponseWrapper response,
			ReturnValueConverter returnValueConverter, Object returnValue,
			ClientService clientService, String callbackFunc, String notificationName,
			boolean overrideSkipObjectConvert 
			)
			throws IntrospectionException, IllegalAccessException, InvocationTargetException, IOException {
		//judge the convert type
		ConverterType converterType = ConverterType.PLAIN_TEXT;
		
		if(returnValueConverter != null) {
			if((returnValueConverter.valueFromRequestParam() != null) 
					&& (returnValueConverter.valueFromRequestParam().length() > 0) 
					) {
				// When valueFromRequestParam() is not null, then default converter type is set to xml.
				String strConverterType = request.getParameter(returnValueConverter.valueFromRequestParam());
				
				if(strConverterType == null) {
					converterType = ConverterType.XML;
				} else {
					strConverterType = strConverterType.trim().toLowerCase();

					if(strConverterType.equals(ReturnValueConverter.COVERT_TYPE_XML_JSONP)) {
						converterType = ConverterType.XML_JSONP;
					} else if(strConverterType.equals(ReturnValueConverter.COVERT_TYPE_XML)) {
						converterType = ConverterType.XML;
					} else if(strConverterType.equals(ReturnValueConverter.COVERT_TYPE_JSON_JSONP)) {
						converterType = ConverterType.JSON_JSONP;
					} else if(strConverterType.equals(ReturnValueConverter.COVERT_TYPE_JSON)) {
						converterType = ConverterType.JSON;
					} else if(strConverterType.equals(ReturnValueConverter.COVERT_TYPE_PLAIN_TEXT_JSONP)) {
						converterType = ConverterType.PLAIN_TEXT_JSONP;
					} else if(strConverterType.equals(ReturnValueConverter.COVERT_TYPE_PLAIN_TEXT)) {
						converterType = ConverterType.PLAIN_TEXT;
					} else {
						converterType = ConverterType.XML;
					}
				}
			}  else {
				converterType = returnValueConverter.value();
			}
		}
		
		String value;
		
		if(returnValue == null) {
			value = "";
		} else {
			if(returnValueConverter != null 
					&& (overrideSkipObjectConvert
							|| returnValueConverter.skipObjectConvert()
						)
					) {
				if(String.class.isAssignableFrom(returnValue.getClass())) {
					value = (String) returnValue;
				} else {
					value = BaseTypesMapping.ConvertBaseTypeValueToStr(returnValue.getClass(), returnValue);
				}
			} else {
				//convert to xml or json or not convert -------------
				if (converterType == ConverterType.XML_JSONP || converterType == ConverterType.XML) {
					//xml
					value = XmlDataUtil.convertObjectToXml(returnValue, returnValue.getClass());
				} else if (converterType == ConverterType.JSON_JSONP || converterType == ConverterType.JSON) {
					//JSON
					//For now, only support the simple data or List<SimpleData>
					if(List.class.isAssignableFrom(returnValue.getClass())) {
						//List
						value = SimpleJSONDataUtil.convertListObjectToJSON((List<?>)returnValue);
					} else {
						//simple data
						value = SimpleJSONDataUtil.convertObjectToJSON(returnValue);
					}
				} else {
					//plain text
					if(String.class.isAssignableFrom(returnValue.getClass())) {
						value = (String) returnValue;
					} if(BaseTypesMapping.IsSupportedBaseType(returnValue.getClass())) {
						value = BaseTypesMapping.ConvertBaseTypeValueToStr(returnValue.getClass(), returnValue);
					} else {
						value = XmlDataUtil.convertObjectToXml(returnValue, returnValue.getClass());
					}
				}
			} // end of if(returnValueConverter.skipObjectConvert()
		} //end of if(returnValue == null) {

		boolean isWrapToClientService = false;
		if(clientService != null 
				&& callbackFunc != null && callbackFunc.length() > 0
				&& notificationName != null && notificationName.length() > 0) {
			isWrapToClientService = true;
		}
		
		if(isWrapToClientService) {
			response.setContentType(ContentTypeHelper.TextJavaScript + DefaultContentTypeCharset);
			return wrapToClientService(callbackFunc, notificationName, value);
		} else if (converterType == ConverterType.XML_JSONP 
				|| converterType == ConverterType.JSON_JSONP 
				|| converterType == ConverterType.PLAIN_TEXT_JSONP 
				) {
			//wrap into jsonp format
			response.setContentType(ContentTypeHelper.TextJavaScript + DefaultContentTypeCharset);
			String jsonpVarName = request.getParameter(
					returnValueConverter.jsonpReturnVariableNameFromRequestParam());
			return wrapToJsonp(jsonpVarName, value);
		} else if (converterType == ConverterType.JSON) {
			response.setContentType("text/json" + DefaultContentTypeCharset);
			return value;
		} else if (converterType == ConverterType.XML) {
			response.setContentType("text/xml" + DefaultContentTypeCharset);
			return value;
		} else {
			response.setContentType(ContentTypeHelper.TextPlain + DefaultContentTypeCharset);
			return value;
		}
	}

	private static String wrapToJsonp(String variableName, String value) throws UnsupportedEncodingException {
		return 
				"var " + variableName + " = \"" 
				+ URLEncoder.encode(value, DefaultEncoding).replace("+", "%20") 
				+ "\";";
	}
	
	private static String wrapToClientService(String callbackFunc, String notificationName, String value) throws UnsupportedEncodingException {
		return callbackFunc + "(\"" + notificationName + "\", \""
				+ URLEncoder.encode(value, DefaultEncoding).replace("+", "%20")
				+ "\")";
	}

	private final static String getAuthTicketFromRequest(RequestWrapper request) {
		String authTicket = request.getParameter(AUTH_TICKET);
		
		if(authTicket == null) {
			//check cookies
			HttpServletRequest httpRequest = (HttpServletRequest) request.getRequest();
			Cookie[] cookies = httpRequest.getCookies();
			if(cookies != null) {
				for(int i = 0; i < cookies.length; i++) {
					Cookie c = cookies[i];
					if(AUTH_TICKET.equals(c.getName())) {
						authTicket = c.getValue();
						break;
					}
				}
			}
		}
		
		return authTicket;
	}
	
}
