package com.salama.service.clouddata.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * 
 * @author XingGu Liu
 *
 */
public final class AppServiceContext {
	private static final Logger log = Logger.getLogger(AppServiceContext.class);
	
	//key:serviceClassName value:AppContext
	private static HashMap<String, AppContext> _serviceClassContextMap = null;

	public static void setServiceClassContextMap(HashMap<String, AppContext> serviceClassContextMap) {
		StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
		String invokerClassName = stackTraces[2].getClassName();
		
//		log.debug("setServiceClassContextMap() invokerClassName:[" + invokerClassName + "]");
//		log.debug("serviceClassContextMap:" + serviceClassContextMap.size());
		if(invokerClassName.equals("com.salama.service.clouddata.CloudDataServiceContext")) {
			_serviceClassContextMap = serviceClassContextMap;
		}
	}
	
	public static AppContext getAppContext() {
		StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
		String invokerClassName = stackTraces[2].getClassName();
		
//		for(int i = 0; i < stackTraces.length; i++) {
//			log.debug("stackTrace[" + i + "]:" + stackTraces[i].getClassName());
//		}
//		log.debug("invokerClassName:[" + invokerClassName + "]");
//		log.debug("_serviceClassContextMap.size:" + _serviceClassContextMap.size());
//		Iterator<String> iteKeys = _serviceClassContextMap.keySet().iterator();
//		String key;
//		while(iteKeys.hasNext()) {
//			key = iteKeys.next();
//			log.debug("_serviceClassContextMap key:[" + key + "]");
//		}
		
		return _serviceClassContextMap.get(invokerClassName);
	}
}
