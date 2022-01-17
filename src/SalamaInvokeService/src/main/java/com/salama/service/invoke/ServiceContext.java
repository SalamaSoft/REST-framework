package com.salama.service.invoke;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletContext;


import MetoXML.XmlDeserializer;

import com.salama.reflect.PreScanClassFinder;
import com.salama.service.core.auth.GrantedAuthority;
import com.salama.service.core.auth.MethodAccessController;
import com.salama.service.core.context.CommonContext;
import com.salama.service.invoke.config.BasePackage;
import com.salama.service.invoke.config.XmlResultSetting;
import com.salama.util.ClassLoaderUtil;
import com.salama.util.http.upload.FileUploadSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author XingGu Liu
 *
 */
public class ServiceContext implements CommonContext {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4704161998246249160L;

	private static final Log logger = LogFactory.getLog(ServiceContext.class);

	protected static final String DefaultEncoding = "utf-8";
	
	private com.salama.service.invoke.config.ServiceContext _serviceContextConfig = null;
	
	private PreScanClassFinder _dataClassFinder = new PreScanClassFinder(); 
	
	private PreScanClassFinder _serviceClassFinder = new PreScanClassFinder();
	
	private MethodAccessController _methodAccessController = null;
	
	//Move to ServletServiceContext
	//private FileUploadSupport _fileUploadSupport = null;
	
	private XmlResultSetting _xmlResultSetting = null;
	
	@Override
	public void reload(ServletContext servletContext, String configLocation) {
		logger.debug("reload() configLocation:" + configLocation);
		String configFilePath = servletContext.getRealPath(configLocation);
		
		XmlDeserializer xmlDes = new XmlDeserializer();
		
		try {
			_serviceContextConfig = (com.salama.service.invoke.config.ServiceContext)
			xmlDes.Deserialize(configFilePath, 
					com.salama.service.invoke.config.ServiceContext.class, 
					XmlDeserializer.DefaultCharset);
		} catch (Exception e) {
			logger.error("reload()", e);
			return;
		}

		
		try {
			reloadPreScanClass(_serviceClassFinder, _serviceContextConfig.getServiceScan());
		} catch (Exception e) {
			logger.error("reload()", e);
		}

		try {
			reloadPreScanClass(_dataClassFinder, _serviceContextConfig.getDataScan());
		} catch (Exception e) {
			logger.error("reload()", e);
		}
		
		try {
			_methodAccessController = null;
			_methodAccessController = (MethodAccessController) ClassLoaderUtil.getDefaultClassLoader().loadClass(
					_serviceContextConfig.getAccessController().getClassName()).newInstance();
			_methodAccessController.Init();
		} catch (Exception e) {
			_methodAccessController = null;
			logger.error("reload()", e);
		}
		
		//Init FileUploadSupport
		/*
		_fileUploadSupport = new FileUploadSupport(servletContext, 
				DefaultEncoding, 
				_serviceContextConfig.getUploadSetting().getFileSizeMax(), 
				_serviceContextConfig.getUploadSetting().getSizeMax(), 
				_serviceContextConfig.getUploadSetting().getSizeThreshold(), 
				_serviceContextConfig.getUploadSetting().getTempDirPath());
		*/
		
		//xmlResultSetting
		_xmlResultSetting = _serviceContextConfig.getXmlResultSetting();
		if(_xmlResultSetting == null) {
			_xmlResultSetting = new XmlResultSetting();
			_xmlResultSetting.setUseClassFullName(false);
			_xmlResultSetting.setMakeFirstCharUpperCase(false);
		}
		
	}
	
	protected static void reloadPreScanClass(
			PreScanClassFinder preScanClassFinder, 
			List<BasePackage> basePackageList) {
		preScanClassFinder.clearPreScannedClass();
		
		for(int i = 0; i < basePackageList.size(); i++) {
			preScanClassFinder.loadClassOfPackage(basePackageList.get(i).getBasePackage());
		}
	}
	
	@Override
	public void destroy() {
	}
	
	public Class<?> findServiceType(String className) throws ClassNotFoundException {
		return _serviceClassFinder.findClass(className);
	}
	
	public Class<?> findDataType(String className)  throws ClassNotFoundException {
		return _dataClassFinder.findClass(className);
	}
	
	public boolean isMethodAccessible(Method method, List<GrantedAuthority> grantedAuthorities) {
		if(_methodAccessController == null) {
			return true;
		} else {
			return _methodAccessController.isMethodAccessible(method, grantedAuthorities);
		}
	}
	
	public InvokeService createInvokeService() {
		return new InvokeService(_serviceClassFinder, _dataClassFinder, _methodAccessController, _xmlResultSetting);
	}
	
	/*
	public FileUploadSupport getFileUploadSupport() {
		return _fileUploadSupport;
	}
	*/
}
