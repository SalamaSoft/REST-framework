package com.salama.service.clouddata;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.NotFoundException;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import MetoXML.XmlDeserializer;
import MetoXML.Cast.BaseTypesMapping;

import com.salama.reflect.PreScanClassFinder;
import com.salama.service.clouddata.auth.DefaultAuthUserDataManager;
import com.salama.service.clouddata.config.ClassConstructorSetting;
import com.salama.service.clouddata.config.CloudDataAppConfig;
import com.salama.service.clouddata.core.AppAuthUserDataManager;
import com.salama.service.clouddata.core.AppContext;
import com.salama.service.clouddata.core.AppServiceContext;
import com.salama.service.clouddata.core.AppServiceFilter;
import com.salama.service.clouddata.core.ICloudDataService;
import com.salama.service.clouddata.core.ICloudDataServiceContext;
import com.salama.service.clouddata.defaultsupport.DefaultSupportService;

/**
 * 
 * @author XingGu Liu
 *
 */
public class CloudDataServiceContext implements ICloudDataServiceContext {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2816894473785406330L;

	private static final Logger logger = Logger.getLogger(CloudDataServiceContext.class);

	private final static String DEFAULT_TEMP_DIR = "/WEB-INF/temp";

	private com.salama.service.clouddata.config.CloudDataServiceContext _cloudDataServiceContextConfig = null;
	
	private PreScanClassFinder _allAppClassFinder = new PreScanClassFinder();
	private PreScanClassFinder _appClassFinderTemp = new PreScanClassFinder();
	
	//key:serviceClassName value:AppContext
	private HashMap<String, AppContext> _serviceClassContextMap = new HashMap<String, AppContext>();
	private String[] _allAppExposedPackages = null;
	
	//key:appId Value:AppContext
	private HashMap<String, AppContext> _appIdContextMap = new HashMap<String, AppContext>();
	
	private List<CloudDataAppContext> _appContextList = new ArrayList<CloudDataAppContext>();

	private List<ClassPath> _javaAssistImportedClassPath = new ArrayList<ClassPath>();
	
	@Override
	public void reload(ServletContext servletContext, String configLocation) {
		logger.debug("reload. configLocation:" + configLocation);
		String configFilePath = servletContext.getRealPath(configLocation);
		
		XmlDeserializer xmlDes = new XmlDeserializer();

		try {
			_cloudDataServiceContextConfig = (com.salama.service.clouddata.config.CloudDataServiceContext)
			xmlDes.Deserialize(configFilePath, 
					com.salama.service.clouddata.config.CloudDataServiceContext.class, 
					XmlDeserializer.DefaultCharset);
		} catch (Exception e) {
			logger.error(e);
			return;
		}

		if(_cloudDataServiceContextConfig.getAuthTicketManagerBackupDirPath() == null 
				|| _cloudDataServiceContextConfig.getAuthTicketManagerBackupDirPath().length() == 0) {
			String defaultTempDirPath = servletContext.getRealPath(DEFAULT_TEMP_DIR);
			_cloudDataServiceContextConfig.setAuthTicketManagerBackupDirPath(defaultTempDirPath);
			
			File dir = new File(defaultTempDirPath);
			if(!dir.exists()) {
				dir.mkdirs();
			}
		}
		
		//clear all
		_serviceClassContextMap.clear();
		_appIdContextMap.clear();
		_allAppClassFinder.clearPreScannedClass();
		_appContextList.clear();
		
		CloudDataAppContext appContext = null;
		AppAuthUserDataManager authUserManager = null;
		Iterator<String> classFullNameIterator = null;
		String classFullName = null;
		logger.debug("_cloudDataServiceContextConfig.getAppConfigs().size:" + _cloudDataServiceContextConfig.getAppConfigs().size());

		//Class pool of javaassist
		ClassPool clsPool = ClassPool.getDefault();
		
		//load classes of DefaultSupport -----
		_appClassFinderTemp.clearPreScannedClass();
		_appClassFinderTemp.loadClassOfPackage(DefaultSupportService.class.getPackage().getName());
		_allAppClassFinder.getClassNameMap().putAll(_appClassFinderTemp.getClassNameMap());
		_allAppClassFinder.getClassFullNameMap().putAll(_appClassFinderTemp.getClassFullNameMap());
		
		//create app context ------
		for(CloudDataAppConfig appConfig : _cloudDataServiceContextConfig.getAppConfigs()) {
			try {
				//reload package
				_appClassFinderTemp.clearPreScannedClass();
				_appClassFinderTemp.loadClassOfPackage(appConfig.getBasePackage());
				
				//add to all app calss finder
				_allAppClassFinder.getClassNameMap().putAll(_appClassFinderTemp.getClassNameMap());
				_allAppClassFinder.getClassFullNameMap().putAll(_appClassFinderTemp.getClassFullNameMap());

				logger.debug("_allAppClassFinder size:" + _allAppClassFinder.getClassFullNameMap().size());
				
				//create appAuthUserDataManagerSetting
				if(appConfig.getAppAuthUserDataManagerSetting() == null
						|| appConfig.getAppAuthUserDataManagerSetting().getClassName() == null
						|| appConfig.getAppAuthUserDataManagerSetting().getClassName().trim().length() == 0
						|| DefaultAuthUserDataManager.class.getName().equals(
								appConfig.getAppAuthUserDataManagerSetting().getClassName())
						) {
					authUserManager = new DefaultAuthUserDataManager();
					logger.debug("authUserManager.class:" + DefaultAuthUserDataManager.class.getName());
				} else {
					authUserManager = (AppAuthUserDataManager) createByClassConstructorSetting(
							appConfig.getAppAuthUserDataManagerSetting());
					logger.debug("authUserManager.class:" + authUserManager.getClass().getName());
				}

				authUserManager.setAppId(appConfig.getAppId());
				authUserManager.setBackupDirPath(_cloudDataServiceContextConfig.getAuthTicketManagerBackupDirPath());

				//create appServiceFilter
				AppServiceFilter appServiceFilter = null;
				if(appConfig.getAppServiceFilterSetting() != null 
						&& appConfig.getAppServiceFilterSetting().getClassName() != null
						&& appConfig.getAppServiceFilterSetting().getClassName().trim().length() > 0) {
					appServiceFilter = (AppServiceFilter) createByClassConstructorSetting(appConfig.getAppServiceFilterSetting());
					logger.debug("appServiceFilter.class:" + appConfig.getAppServiceFilterSetting().getClassName());
				}
				
				//create appContext
				appContext = new CloudDataAppContext(appConfig, 
						authUserManager, _allAppClassFinder, appServiceFilter);
				
				//reload appServiceFilter
				if(appServiceFilter != null) {
					appServiceFilter.reload(servletContext, appContext);
				}
				
				_appContextList.add(appContext);
				
				logger.debug("appConfig.getAppId():" + appConfig.getAppId());
				
				//rebuild context map
				_appIdContextMap.put(appConfig.getAppId(), appContext);
				
				//build maps that exposed package -> appContext
				String[] exposedPackageNames = appConfig.getExposedPackage().split(",");
				Set<String> allAppExposedPackageSet = new HashSet<String>();
				for(String pkgName : exposedPackageNames) {
					String trimedPkgName = pkgName.trim();
					
					if(trimedPkgName.length() != 0) {
						_serviceClassContextMap.put(trimedPkgName, appContext);
						allAppExposedPackageSet.add(trimedPkgName);
					}
				}
				_allAppExposedPackages = allAppExposedPackageSet.toArray(new String[0]);
				
				//build maps that class name(under base package) -> appContext
				classFullNameIterator = _appClassFinderTemp.getClassFullNameMap().keySet().iterator();
				while(classFullNameIterator.hasNext()) {
					classFullName = classFullNameIterator.next();
					
					logger.debug("app classFullName:" + classFullName);
					
					_serviceClassContextMap.put(classFullName, appContext);

					//javassist
					clsPool.insertClassPath(new ClassClassPath(
							_appClassFinderTemp.getClassFullNameMap().get(classFullName)));
				}
			} catch(Exception e) {
				logger.error("", e);
			}
		} //for
		
		//set to AppServiceContext
		AppServiceContext.setServiceClassContextMap(_serviceClassContextMap);
		
		
		//Init javaassist -------------------------
		try {
			/*
			{
				String clsPath = servletContext.getRealPath("/WEB-INF/lib/"); 
				logger.info("ClassPool.insertClassPath():" + clsPath);
				ClassPath classPath = clsPool.insertClassPath(clsPath);
				_javaAssistImportedClassPath.add(classPath);
			}
			*/

			{
				String clsPath = servletContext.getRealPath("/WEB-INF/classes/"); 
				logger.info("ClassPool.insertClassPath():" + clsPath);
				ClassPath classPath = clsPool.insertClassPath(clsPath);
				_javaAssistImportedClassPath.add(classPath);
			}
		} catch (NotFoundException e) {
			logger.error("reload()", e);
		}
		
	}
	
	@Override
	public void destroy() {
		//clear all
		CloudDataAppContext appContext = null;		
		for(int i = 0; i < _appContextList.size(); i++) {
			appContext = _appContextList.get(i);
			if(appContext.getAppServiceFilter() != null) {
				appContext.getAppServiceFilter().destroy();
			}
		}
		
		_appContextList.clear();
		_serviceClassContextMap.clear();
		_appIdContextMap.clear();
		_allAppClassFinder.clearPreScannedClass();
		_appClassFinderTemp.clearPreScannedClass();
		
		//clear javaassist
		for(ClassPath classPath : _javaAssistImportedClassPath) {
			ClassPool.getDefault().removeClassPath(classPath);
		}
		_javaAssistImportedClassPath.clear();
		
	}

	@Override
	public ICloudDataService createCloudDataService() {
		CloudDataService cloudDataService = new CloudDataService(
				_allAppClassFinder, 
				_serviceClassContextMap,
				_allAppExposedPackages
				);
		
		return cloudDataService;
	}

	protected Object createByClassConstructorSetting(ClassConstructorSetting constructorSetting) throws ClassNotFoundException, IllegalAccessException, ParseException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, InvocationTargetException {
		Class<?> targetClass = Class.forName(constructorSetting.getClassName());
		int paramCount = constructorSetting.getParamTypes().size();
		
		Class<?>[] paramTypes = new Class<?>[paramCount];
		Object[] paramValues = new Object[paramCount];
		
		String paramClassName;
		String paramValue;
		for(int i = 0; i < paramCount; i++) {
			paramClassName = constructorSetting.getParamTypes().get(i).trim();
			paramValue = constructorSetting.getParamValues().get(i).trim();
			
			paramTypes[i] = Class.forName(paramClassName);
			paramValues[i] = BaseTypesMapping.Convert(paramTypes[i], paramValue);
		}
		
		Constructor<?> constructor = targetClass.getConstructor(paramTypes);
		return constructor.newInstance(paramValues);
	}
}
