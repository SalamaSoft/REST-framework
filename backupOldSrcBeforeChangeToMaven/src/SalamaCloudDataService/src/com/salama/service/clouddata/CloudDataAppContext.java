package com.salama.service.clouddata;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.NamingException;

import MetoXML.XmlDeserializer;
import MetoXML.XmlSerializer;
import MetoXML.Util.ClassFinder;

import com.salama.reflect.PreScanClassFinder;
import com.salama.service.clouddata.config.CloudDataAppConfig;
import com.salama.service.clouddata.core.AppAuthUserDataManager;
import com.salama.service.clouddata.core.AppContext;
import com.salama.service.clouddata.core.AppException;
import com.salama.service.clouddata.core.AppServiceFilter;
import com.salama.service.clouddata.core.AuthUserInfo;
import com.salama.util.db.DBUtil;

/**
 * 
 * @author XingGu Liu
 *
 */
public class CloudDataAppContext implements AppContext, ClassFinder {

	//private final static Logger logger = Logger.getLogger(CloudDataAppContext.class);
	
	private CloudDataAppConfig _appConfig = null;	
	private AppAuthUserDataManager _appAuthUserDataManager = null;
	private PreScanClassFinder _allAppClassFinder = null;
	private AppServiceFilter _appServiceFilter = null;
	private String[] _exposedPackageList = null;
	
	public CloudDataAppContext(CloudDataAppConfig appConfig, 
			AppAuthUserDataManager appAuthUserDataManager,
			PreScanClassFinder allAppClassFinder,
			AppServiceFilter appServiceFilter) {
		_appConfig = appConfig;
		_appAuthUserDataManager = appAuthUserDataManager;
		_allAppClassFinder = allAppClassFinder;
		_appServiceFilter = appServiceFilter;
		
		String exposedPackage = appConfig.getExposedPackage();
		if(exposedPackage != null && exposedPackage.length() > 0) {
			StringTokenizer stk = new StringTokenizer(exposedPackage, ",");
			List<String> exposedPkgList = new ArrayList<String>();
			
			String pkg = null;
			while(stk.hasMoreTokens()) {
				pkg = stk.nextToken().trim();
				if(pkg.length() > 0) {
					exposedPkgList.add(pkg);
				}
			}
			
			if(exposedPkgList.size() > 0) {
				_exposedPackageList = new String[exposedPkgList.size()];
				for(int i = 0; i < exposedPkgList.size(); i++) {
					_exposedPackageList[i] = exposedPkgList.get(i);
				}
			}
		}
	}
	
	@Override
	public Connection getDBConnection() throws AppException {
		try {
			return DBUtil.getConnection("java:/comp/env/" + _appConfig.getDbConnectionJNDIName());
		} catch (NamingException e) {
			throw new AppException(e);
		} catch (SQLException e) {
			throw new AppException(e);
		}
	}

	@Override
	public String getAppId() {
		return _appConfig.getAppId();
	}

	@Override
	public String allocateAuthTicket(String role, String userId, long expiringTime) throws AppException {
		return _appAuthUserDataManager.allocateAuthTicket(role, userId, expiringTime);
	}
	
	@Override
	public boolean isAuthTicketValid(String authTicket) throws AppException {
		return _appAuthUserDataManager.isAuthTicketValid(authTicket);
	}
	
	@Override
	public AuthUserInfo getAuthUserInfo(String authTicket) throws AppException {
		return _appAuthUserDataManager.getAuthUserInfo(authTicket);
	}
	
	@Override
	public void updateAuthInfo(String authTicket, String role, long expiringTime) throws AppException {
		_appAuthUserDataManager.updateAuthInfo(authTicket, role, expiringTime);
	}
	
	@Override
	public void deleteAuthInfo(String authTicket) throws AppException {
		_appAuthUserDataManager.deleteAuthInfo(authTicket);
	}

	@Override
	public String getSessionValue(String authTicket, String key) throws AppException {
		return _appAuthUserDataManager.getSessionValue(authTicket, key);
	}
	
	@Override
	public String setSessionValue(String authTicket, String key, String value) throws AppException {
		return _appAuthUserDataManager.setSessionValue(authTicket, key, value);
	}

	@Override
	public String removeSessionValue(String authTicket, String key) throws AppException {
		return _appAuthUserDataManager.removeSessionValue(authTicket, key);
	}

	@Override
	public String objectToXml(Object obj, Class<?> clazz) throws AppException {
		try {
			return XmlSerializer.objectToString(obj, clazz, false, false);
		} catch(Exception e) {
			throw new AppException(e);
		}
	}
	
	@Override
	public Object xmlToObject(String objXml, Class<?> clazz) throws AppException {
		try {
			return XmlDeserializer.stringToObject(objXml, clazz, _allAppClassFinder);
		} catch(Exception e) {
			throw new AppException(e);
		}
	}

	@Override
	public Class<?> findClass(String className) {
		try {
			return _allAppClassFinder.findClass(className);
		} catch(Exception e) {
			return null;
		}
	}

	public AppServiceFilter getAppServiceFilter() {
		return _appServiceFilter;
	}

	public boolean isPackageExposed(String packageName) {
		if(!packageName.startsWith(_appConfig.getBasePackage())) {
			return false;
		}
		
		if(_exposedPackageList != null && _exposedPackageList.length > 0) {
			if(_exposedPackageList.length == 1) {
				if(packageName.startsWith(_exposedPackageList[0])) {
					return true;
				} else {
					return false;
				}
			} else {
				for(int i = 0; i < _exposedPackageList.length; i++) {
					if(packageName.startsWith(_exposedPackageList[i])) {
						return true;
					}
				}
				
				return false;
			}
			
		} else {
			return true;
		}
	}
	
}
