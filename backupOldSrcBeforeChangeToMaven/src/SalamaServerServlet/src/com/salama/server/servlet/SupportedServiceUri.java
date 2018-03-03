package com.salama.server.servlet;

import java.util.HashMap;

/**
 * 
 * @author XingGu Liu
 *
 */
public class SupportedServiceUri {
	public final static Integer ServiceTypeInvokeService = 1;
	public final static Integer ServiceTypeSetSessionValue = 2;
	public final static Integer ServiceTypeSetSessionValues = 3;
	public final static Integer ServiceTypeGetSessionValue = 4;
	public final static Integer ServiceTypeUploadService = 5;
	public final static Integer ServiceTypeDownloadService = 6;
	public final static Integer ServiceTypeCloudDataService = 7;
	
	public final static String InvokeService = "/invokeService.do";
	
	public final static String SetSessionValue = "/setSessionValue.do";
	
	public final static String SetSessionValues = "/setSessionValues.do";

	public final static String GetSessionValue = "/getSessionValue.do";

	public final static String UploadService = "/uploadService.do";
	
	public final static String DownloadService = "/downloadService.do";
	
	public final static String CloudDataService = "/cloudDataService.do";

	private static HashMap<String, Integer> _supportedUriMap = null;
	
	static {
		_supportedUriMap = new HashMap<String, Integer>();
		
		_supportedUriMap.put(InvokeService, ServiceTypeInvokeService);
		_supportedUriMap.put(SetSessionValue, ServiceTypeSetSessionValue);
		_supportedUriMap.put(SetSessionValues, ServiceTypeSetSessionValues);
		_supportedUriMap.put(GetSessionValue, ServiceTypeGetSessionValue);
		_supportedUriMap.put(UploadService, ServiceTypeUploadService);
		_supportedUriMap.put(DownloadService, ServiceTypeDownloadService);
		_supportedUriMap.put(CloudDataService, ServiceTypeCloudDataService);
	};
	
	public final static String[] getSupportedServiceUris() {
		return new String[] {
				InvokeService, 
				SetSessionValue, SetSessionValues, GetSessionValue,
				UploadService, DownloadService, CloudDataService
		};
	}

	public final static Integer getServiceType(String uri) {
		return _supportedUriMap.get(uri);
	}
}
