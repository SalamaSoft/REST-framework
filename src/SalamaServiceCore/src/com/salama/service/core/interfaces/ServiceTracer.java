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
	
	/**
	 * 
	 * @param request
	 * @param sessionValueSetXml
	 */
	public void onSetSessionValues(RequestWrapper request,
			String sessionValueSetXml);

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
	
	/**
	 * 
	 * @param request
	 * @param serviceType
	 */
	public void onDownloadService(RequestWrapper request,
			String serviceType);
	
	
	public void onCloudDataService(
			RequestWrapper request,
			String serviceType,
			String serviceMethod 
			);
	
	/**
	 * 
	 * @param request
	 */
	public void onUnSupportedService(RequestWrapper request);
}
