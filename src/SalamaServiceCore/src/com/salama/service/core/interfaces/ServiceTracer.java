package com.salama.service.core.interfaces;

import com.salama.service.core.net.RequestWrapper;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface ServiceTracer {
	public void init();
	
	public void destroy();
	
	/**
	 * 
	 * @param request
	 * @param serviceType
	 * @param serviceMethod
	 * @param paramType
	 * @param paramValueXml
	 */
	public void onInvokeService(RequestWrapper request, 
			String serviceType,
			String serviceMethod, 
			String paramType,
			String paramValueXml);

	/**
	 * 
	 * @param request
	 * @param serviceType
	 * @param serviceMethod
	 * @param paramType
	 * @param paramValueXml
	 * @param elapsedTime how long elapsed in service  
	 */
	public void onInvokeServiceDone(RequestWrapper request, 
			String serviceType,
			String serviceMethod, 
			String paramType,
			String paramValueXml,
			long elapsedTime);
	
	/**
	 * 
	 * @param request
	 * @param key
	 * @param dataType
	 * @param property
	 * @param propertyType
	 * @param valueXml
	 */
	public void onSetSessionValue(RequestWrapper request, 
			String key,
			String dataType, 
			String property,
			String propertyType, 
			String valueXml);

	public void onSetSessionValueDone(RequestWrapper request, 
			String key,
			String dataType, 
			String property,
			String propertyType, 
			String valueXml,
			long elapsedTime);
	
	/**
	 * 
	 * @param request
	 * @param sessionValueSetXml
	 */
	public void onSetSessionValues(RequestWrapper request,
			String sessionValueSetXml);

	public void onSetSessionValuesDone(RequestWrapper request,
			String sessionValueSetXml,
			long elapsedTime);
	
	/**
	 * 
	 * @param request
	 * @param key
	 * @param dataType
	 * @param property
	 * @param propertyType
	 */
	public void onGetSessionValue(RequestWrapper request,
			String key,
			String dataType, String property,
			String propertyType);
	
	public void onGetSessionValueDone(RequestWrapper request,
			String key,
			String dataType, String property,
			String propertyType,
			long elapsedTime);
	/**
	 * 
	 * @param request
	 * @param serviceType
	 * @param paramType
	 * @param paramValueXml
	 */
	public void onUploadService(RequestWrapper request,
			String serviceType,
			String paramType,
			String paramValueXml);
	
	public void onUploadServiceDone(RequestWrapper request,
			String serviceType,
			String paramType,
			String paramValueXml,
			long elapsedTime);
	/**
	 * 
	 * @param request
	 * @param serviceType
	 */
	public void onDownloadService(RequestWrapper request,
			String serviceType);
	
	public void onDownloadServiceDone(RequestWrapper request,
			String serviceType,
			long elapsedTime);
	
	public void onCloudDataService(
			RequestWrapper request,
			String serviceType,
			String serviceMethod 
			);
	
	public void onCloudDataServiceDone(
			RequestWrapper request,
			String serviceType,
			String serviceMethod,
			long elapsedTime
			);
	
	/**
	 * 
	 * @param request
	 */
	public void onUnSupportedService(RequestWrapper request);
}
