package com.salama.util;

public final class ClassLoaderUtil {
	private ClassLoaderUtil() {
		
	}
	
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader classLoader = null;

		try {
			classLoader = Thread.currentThread().getContextClassLoader();
		} catch (Throwable e) {
		}
		
		if (classLoader == null) {
			classLoader = ClassLoaderUtil.class.getClassLoader();
		}
		
		return classLoader;
	}
}
