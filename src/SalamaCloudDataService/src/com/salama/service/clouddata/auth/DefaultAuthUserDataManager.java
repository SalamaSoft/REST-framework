package com.salama.service.clouddata.auth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import MetoXML.Base.XmlContentEncoder;

import com.salama.service.clouddata.core.AppAuthUserDataManager;
import com.salama.service.clouddata.core.AppException;
import com.salama.service.clouddata.core.AuthUserInfo;

/**
 * 
 * @author XingGu Liu
 *
 */
public final class DefaultAuthUserDataManager implements AppAuthUserDataManager {

	private final static Logger logger = Logger
			.getLogger(DefaultAuthUserDataManager.class);

	private final static Charset DEFAULT_CHARSET = Charset.forName("utf-8");

	/**
	 * <${authTicket, TicketUserInfo}>
	 */
	private ConcurrentHashMap<String, UserSessionData> _userDataMap = new ConcurrentHashMap<String, UserSessionData>();

	private final static String BACK_UP_FILE_NAME = "DefaultTicketManager";

	private String _appId = null;

	private String _backupDirPath = null;

	private Random _random = null;

	private MessageDigest _md5 = null;

	private final Object methodLock = new Object();

	public DefaultAuthUserDataManager() {
		_random = new Random(System.currentTimeMillis());

		try {
			_md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private String createNewTicket(String role, String userId) {
		// 1st MD5 -----------------------------------------------------
		// appId + role + userId
		StringBuilder userInfo = new StringBuilder();
		userInfo.append(_appId).append('.').append(userId).append('.')
				.append(role);
		byte[] userInfoBytes = userInfo.toString().getBytes();

		byte[] md5Bytes1 = _md5.digest(userInfoBytes);

		// 2nd MD5 -----------------------------------------------------
		byte[] bytes = new byte[8 + md5Bytes1.length];
		int offset = 0;

		// random
		long val = _random.nextLong();
		bytes[offset] = (byte) (val & 0xFFL);
		bytes[offset + 1] = (byte) ((val >> 8) & 0xFFL);
		bytes[offset + 2] = (byte) ((val >> 16) & 0xFFL);
		bytes[offset + 3] = (byte) ((val >> 24) & 0xFFL);
		bytes[offset + 4] = (byte) ((val >> 32) & 0xFFL);
		bytes[offset + 5] = (byte) ((val >> 40) & 0xFFL);
		bytes[offset + 6] = (byte) ((val >> 48) & 0xFFL);
		bytes[offset + 7] = (byte) ((val >> 56) & 0xFFL);
		offset += 8;

		System.arraycopy(md5Bytes1, 0, bytes, 8, md5Bytes1.length);
		byte[] md5bytes2 = _md5.digest(bytes);

		_md5.reset();
		return toHexString(md5bytes2, 0, md5bytes2.length);
	}

	@Override
	public String allocateAuthTicket(String role, String userId,
			long expiringTime) {
		synchronized (methodLock) {
			String authTicket = createNewTicket(role, userId);

			UserSessionData userSessionData = new UserSessionData();
			userSessionData.setAuthTicket(authTicket);
			long curTimeMS = System.currentTimeMillis();
			userSessionData.setLoginTime(curTimeMS);
			userSessionData.setSessionAccessTime(curTimeMS);

			AuthUserInfo userInfo = new AuthUserInfo();
			// userInfo.setAppId(_appId);
			userInfo.setRole(role);
			userInfo.setUserId(userId);
			userInfo.setExpiringTime(expiringTime);

			userSessionData.setAuthUserInfo(userInfo);

			_userDataMap.put(authTicket, userSessionData);

			return authTicket;
		}
	}

	@Override
	public void backupAllData() {
		backupOnHardDisk();
	}

	@Override
	public void restoreAllData() {
		restoreOnHardDisk();
	}

	private void backupOnHardDisk() {
		// Hard disk backup
		synchronized (methodLock) {
			File backupDir = new File(_backupDirPath);
			if (!backupDir.isDirectory()) {
				backupDir.delete();
			}
			if (!backupDir.exists()) {
				backupDir.mkdirs();
			}

			Enumeration<UserSessionData> enumUserData = _userDataMap.elements();

			UserSessionData userData = null;
			while (enumUserData.hasMoreElements()) {
				userData = enumUserData.nextElement();

				try {
					backupUserSessionData(userData);
				} catch (Exception e) {
					logger.error(
							"backup error. authTicket:\r\n"
									+ userData.getAuthTicket() + "\r\n", e);
				}

			}
		}
	}

	private void restoreOnHardDisk() {
		// Hard disk restore
		synchronized (methodLock) {
			File backupFileDir = new File(_backupDirPath);

			backupFileDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					try {
						if (checkBackupFileName(file)) {
							UserSessionData userData = restoreUserSessionData(file);
							_userDataMap.put(userData.getAuthTicket(), userData);
						}
					} catch (Exception e) {
						logger.error("userData File:" + file.getAbsolutePath()
								+ "\r\n", e);
					}

					return false;
				}
			});
		}
	}

	private String getBackupFileName(String authTicket) {
		return BACK_UP_FILE_NAME + "." + _appId + "." + authTicket + ".bak";
	}

	private boolean checkBackupFileName(File file) {
		String fileName = file.getName();

		if (!fileName.endsWith(".bak")) {
			return false;
		}

		if (!fileName.startsWith(BACK_UP_FILE_NAME)) {
			return false;
		}

		if (fileName.charAt(BACK_UP_FILE_NAME.length()) != '.') {
			return false;
		}

		fileName = fileName.substring(BACK_UP_FILE_NAME.length() + 1,
				fileName.length() - 4);

		int index = fileName.lastIndexOf('.');
		if (index < 0) {
			return false;
		}

		String appId = fileName.substring(0, index);
		if (!appId.equals(_appId)) {
			return false;
		}

		// String authTicket = fileName.substring(index + 1);
		//
		// UserSessionData userData = new UserSessionData();
		// userData.setAuthTicket(authTicket);

		return true;
	}

	private final static String BACKUP_FILE_ITEM_TOKEN = "<";

	private void backupUserSessionData(UserSessionData userData)
			throws IOException {
		File file = new File(_backupDirPath,
				getBackupFileName(userData.getAuthTicket()));

		FileOutputStream fos = null;
		OutputStreamWriter sw = null;
		BufferedWriter bufW = null;

		fos = new FileOutputStream(file);
		sw = new OutputStreamWriter(fos, DEFAULT_CHARSET);
		bufW = new BufferedWriter(sw, 10240);

		try {
			bufW.write(userData.getAuthTicket());
			bufW.write("\r\n");

			bufW.write(Long.toString(userData.getLoginTime()));
			bufW.write("\r\n");

			bufW.write(Long.toString(userData.getSessionAccessTime()));
			bufW.write("\r\n");

			bufW.write(userData.getAuthUserInfo().getUserId());
			bufW.write("\r\n");

			bufW.write(userData.getAuthUserInfo().getRole());
			bufW.write("\r\n");

			if (userData.getSessionValueMap() != null) {
				Set<Entry<String, String>> userDataSet = userData
						.getSessionValueMap().entrySet();
				Iterator<Entry<String, String>> iterDataEntry = userDataSet
						.iterator();
				Entry<String, String> entry = null;
				while (iterDataEntry.hasNext()) {
					entry = iterDataEntry.next();

					bufW.write(entry.getKey());
					bufW.write("\r\n");
					bufW.write(XmlContentEncoder.EncodeContent(entry.getValue()));
					bufW.write(BACKUP_FILE_ITEM_TOKEN + "\r\n");
				}
			}
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
			try {
				sw.close();
			} catch (Exception e) {
			}
			try {
				bufW.close();
			} catch (Exception e) {
			}
		}

	}

	private UserSessionData restoreUserSessionData(File file)
			throws IOException {
		FileInputStream fis = null;
		InputStreamReader sr = null;
		BufferedReader bufR = null;

		fis = new FileInputStream(file);
		sr = new InputStreamReader(fis, DEFAULT_CHARSET);
		bufR = new BufferedReader(sr, 10240);

		UserSessionData userData = new UserSessionData();
		userData.setAuthUserInfo(new AuthUserInfo());
		try {
			String line = null;

			line = bufR.readLine();
			userData.setAuthTicket(line);

			line = bufR.readLine();
			userData.setLoginTime(Long.parseLong(line));

			line = bufR.readLine();
			userData.setSessionAccessTime(Long.parseLong(line));

			line = bufR.readLine();
			userData.getAuthUserInfo().setUserId(line);

			line = bufR.readLine();
			userData.getAuthUserInfo().setRole(line);

			String key = null;
			StringBuilder value = new StringBuilder();
			int valueLen = 0;
			boolean startFlg = true;
			while (true) {
				if (line == null) {
					break;
				}

				if (startFlg) {
					startFlg = false;

					key = line;
					value.delete(0, value.length());
				} else {
					if (line.endsWith(BACKUP_FILE_ITEM_TOKEN)) {
						valueLen = line.length()
								- BACKUP_FILE_ITEM_TOKEN.length();
						if (valueLen > 0) {
							value.append(line.substring(0, valueLen));
						}

						if (userData.getSessionValueMap() == null) {
							userData.setSessionValueMap(new ConcurrentHashMap<String, String>());
						}

						userData.getSessionValueMap().put(
								key,
								XmlContentEncoder.DecodeContent(value
										.toString()));
						startFlg = true;
					} else {
						value.append(line);
					}
				}

			}

			return userData;
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
			try {
				sr.close();
			} catch (Exception e) {
			}
			try {
				bufR.close();
			} catch (Exception e) {
			}
		}

	}

	@Override
	public AuthUserInfo getAuthUserInfo(String authTicket) throws AppException {
		UserSessionData userData = _userDataMap.get(authTicket);
		if (userData == null) {
			return null;
		} else {
			return userData.getAuthUserInfo();
		}
	}

	@Override
	public String getSessionValue(String authTicket, String key)
			throws AppException {
		UserSessionData userData = _userDataMap.get(authTicket);
		if (userData == null) {
			return null;
		} else {
			userData.setSessionAccessTime(System.currentTimeMillis());

			if (userData.getSessionValueMap() != null) {
				return userData.getSessionValueMap().get(key);
			} else {
				return null;
			}
		}
	}

	@Override
	public String removeSessionValue(String authTicket, String key) throws AppException {
		UserSessionData userData = _userDataMap.get(authTicket);
		if (userData == null) {
			return null;
		} else {
			userData.setSessionAccessTime(System.currentTimeMillis());

			if (userData.getSessionValueMap() == null) {
				return key;
			} else {
				userData.getSessionValueMap().remove(key);
			}

			return key;
		}
	}

	@Override
	public String setSessionValue(String authTicket, String key, String value)
			throws AppException {
		UserSessionData userData = _userDataMap.get(authTicket);
		if (userData == null) {
			return null;
		} else {
			userData.setSessionAccessTime(System.currentTimeMillis());

			if (userData.getSessionValueMap() == null) {
				userData.setSessionValueMap(new ConcurrentHashMap<String, String>());
			}

			userData.getSessionValueMap().put(key, value);

			return key;
		}
	}

	@Override
	public boolean isAuthTicketValid(String authTicket) throws AppException {
		UserSessionData userData = _userDataMap.get(authTicket);
		if (userData == null) {
			return false;
		} else {
			if (System.currentTimeMillis() > userData.getAuthUserInfo()
					.getExpiringTime()) {
				return false;
			} else {
				return true;
			}
		}
	}

	@Override
	public void updateAuthInfo(String authTicket, String role, long expiringTime)
			throws AppException {
		UserSessionData userData = _userDataMap.get(authTicket);
		if (userData == null) {
			return;
		} else {
			userData.getAuthUserInfo().setRole(role);
			userData.getAuthUserInfo().setExpiringTime(expiringTime);
		}
	}

	@Override
	public void deleteAuthInfo(String authTicket) throws AppException {
		_userDataMap.remove(authTicket);
	}

	@Override
	public void setAppId(String appId) {
		_appId = appId;
	}

	@Override
	public void setBackupDirPath(String backupDirPath) {
		_backupDirPath = backupDirPath;
	}

	private static String toHexString(byte[] val, int offset, int length) {
		long lVal = 0;
		int cnt = length / 8;
		int startIndex = offset;
		StringBuilder hexStr = new StringBuilder();
		
		for(int i = 0; i < cnt; i++) {
			
			lVal = 
				((((long)val[startIndex]) << 56) & 0xFF00000000000000L) + 
				((((long)val[startIndex + 1]) << 48) & 0x00FF000000000000L) +
				((((long)val[startIndex + 2]) << 40) & 0x0000FF0000000000L) +
				((((long)val[startIndex + 3]) << 32) & 0x000000FF00000000L) +
				((((long)val[startIndex + 4]) << 24) & 0x00000000FF000000L) +
				((((long)val[startIndex + 5]) << 16) & 0x0000000000FF0000L) +
				((((long)val[startIndex + 6]) << 8) &  0x000000000000FF00L) +
				((((long)val[startIndex + 7]) ) & 0x00000000000000FFL) ;
			hexStr.append(toHexString(lVal));
			
			startIndex += 8;
		}
		
		for(; startIndex < length; startIndex++) {
			hexStr.append(toHexString(val[startIndex]));
		}
		
		return hexStr.toString();
	}

	private static String toHexString(long val) {
		StringBuilder hexStr = new StringBuilder(Long.toHexString(val));

		for(int i = hexStr.length(); i < 16; i++) {
			hexStr.insert(0, '0');
		}
		
		return hexStr.toString();
	}
	
}
