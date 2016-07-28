package com.salama.service.core.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import MetoXML.XmlDeserializer;

import com.salama.service.core.context.config.ContextSetting;
import com.salama.util.ClassLoaderUtil;

/**
 * 
 * @author XingGu Liu
 *
 */
public class ServiceContext implements CommonContext {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2575930667547968086L;

	private static final Logger logger = Logger
			.getLogger(ServiceContext.class);
	
	private final static String VERSION = "20160505";
	static {
		logger.info("SalamaServiceCore VERSION:" + VERSION);
	}

	private com.salama.service.core.context.config.ServiceContext _serviceContextConfig = null;
	
	private HashMap<String, CommonContext> _contextMap = new HashMap<String, CommonContext>();

	@Override
	public void reload(ServletContext servletContext, String configLocation) {
		_contextMap.clear();

		String configFilePath = servletContext.getRealPath(configLocation);

		try {
			XmlDeserializer xmlDes = new XmlDeserializer();
			_serviceContextConfig = (com.salama.service.core.context.config.ServiceContext)
					xmlDes.Deserialize(configFilePath, 
					com.salama.service.core.context.config.ServiceContext.class, 
					XmlDeserializer.DefaultCharset);
		} catch (Exception e) {
			logger.error("reload()", e);
			throw new RuntimeException(e);
		}
		
		ContextSetting contextSetting = null;
		Class<?> contextClass = null;
		String contextConfigLocation = null;
		String contextClassName = null;
		CommonContext contextInstance = null;
		
		for(int i = 0; i < _serviceContextConfig.getContexts().size(); i++) {
			try {
				contextSetting = _serviceContextConfig.getContexts().get(i);
				
				contextConfigLocation = contextSetting.getConfigLocation().trim();
				contextClassName = contextSetting.getContextClass().trim();
				
				if(contextConfigLocation.length() != 0) {
					contextClass = ClassLoaderUtil.getDefaultClassLoader().loadClass(contextClassName);
					contextInstance = (CommonContext)contextClass.newInstance();
					contextInstance.reload(servletContext, contextConfigLocation);
					
					_contextMap.put(contextClassName, contextInstance); 
				}
			} catch (Exception e) {
				logger.error("reload()", e);
			} 
		}
		
		if(getContext(servletContext) == null) {
			setContext(servletContext, this);
		}
	}
	
	public static ServiceContext getContext(ServletContext servletContext) {
		return (ServiceContext) servletContext.getAttribute(ServiceContext.class.getName());
	}
	
	protected static void setContext(ServletContext servletContext, ServiceContext context) {
		servletContext.setAttribute(ServiceContext.class.getName(), context);
	}
	
	@Override
	public void destroy() {
		/* 
		Collection<CommonContext> contextCol =  _contextMap.values();
		
		Iterator<CommonContext> contextIte = contextCol.iterator();
		CommonContext context = null;
		
		while(contextIte.hasNext()) {
			context = contextIte.next();
			context.destroy();
		}
		*/
		
		//Context should be destroyed in the reverse order of initializing
		if(_serviceContextConfig != null && _serviceContextConfig.getContexts() != null) {
			int size = _serviceContextConfig.getContexts().size();
			for(int i = size - 1; i >= 0; i--) {
				ContextSetting contextSetting = _serviceContextConfig.getContexts().get(i);
				String contextClassName = contextSetting.getContextClass().trim();
				
				try {
					CommonContext contextInstance = _contextMap.get(contextClassName);
					contextInstance.destroy();
				} catch (Throwable e) {
					logger.error("Error occurred in destroying", e);
				}
			}
		}
		
		_contextMap.clear();
	}

	public CommonContext getContext(Class<?> contextClass) {
		return _contextMap.get(contextClass.getName());
	}
	
	public CommonContext getContext(String contextClassName) {
		return _contextMap.get(contextClassName);
	}
	
}
