package com.salama.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ResourceUtil {
	
	private ResourceUtil() {
		
	}
	
	public static String getPropValue(Properties props, String name) {
		String propValue = null;

		if (props == null) {
			return null;
		} else {
			propValue = props.getProperty(name);
			return propValue;
		}
	}

	/**
	 * Get properties
	 * 
	 * @param resourceName path of properties file. e.g.:/org/jwnet/XXX.properties
	 * @return properties
	 */
	public static Properties getProperties(String resourceName) {
		try {
			InputStream resourceStream = null;
			Properties properties = new Properties();
			resourceStream = (ResourceUtil.class)
					.getResourceAsStream(resourceName);
			properties.load(resourceStream);

			return properties;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Get properties
	 * 
	 * @param clazz: Class
	 * @param fileName e.g.：Test1.properties
	 * @return null if does not exist
	 */
	public static Properties getProperties(Class<?> clazz, String fileName) {
		try {
			InputStream resourceStream = null;
			Properties properties = new Properties();
			resourceStream = clazz.getResourceAsStream(fileName);
			properties.load(resourceStream);

			return properties;
		} catch (IOException e) {
			return null;
		}
	}
}
