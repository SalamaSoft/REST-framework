package com.salama.server.servlet;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import MetoXML.XmlDeserializer;

import com.salama.service.core.context.CommonContext;
import com.salama.util.http.upload.FileUploadSupport;

/**
 * 
 * @author XingGu Liu
 *
 */
public class ServiceContext implements CommonContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2394620719493984169L;
	
	private static final Logger logger = Logger.getLogger(ServiceContext.class);
	
	protected static final String DefaultEncoding = "utf-8";
	
	private com.salama.server.servlet.config.ServiceContext _serviceContextConfig = null;

	private FileUploadSupport _fileUploadSupport = null;

	@Override
	public void reload(ServletContext servletContext, String configLocation) {
		logger.debug("reload() configLocation:" + configLocation);
		String configFilePath = servletContext.getRealPath(configLocation);
		
		XmlDeserializer xmlDes = new XmlDeserializer();
		
		try {
			_serviceContextConfig = (com.salama.server.servlet.config.ServiceContext)
			xmlDes.Deserialize(configFilePath, 
					com.salama.server.servlet.config.ServiceContext.class, 
					XmlDeserializer.DefaultCharset);
		} catch (Exception e) {
			logger.error("reload()", e);
			return;
		}
		
		//Init FileUploadSupport
		_fileUploadSupport = new FileUploadSupport(servletContext, 
				DefaultEncoding, 
				_serviceContextConfig.getUploadSetting().getFileSizeMax(), 
				_serviceContextConfig.getUploadSetting().getSizeMax(), 
				_serviceContextConfig.getUploadSetting().getSizeThreshold(), 
				_serviceContextConfig.getUploadSetting().getTempDirPath());
		logger.info("_fileUploadSupport.tempDirPath:" + _fileUploadSupport.getTempDirPath());
	}

	@Override
	public void destroy() {
	}

	public FileUploadSupport getFileUploadSupport() {
		return _fileUploadSupport;
	}
	
}
