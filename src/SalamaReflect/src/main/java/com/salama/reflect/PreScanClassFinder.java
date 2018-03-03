package com.salama.reflect;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

import MetoXML.Cast.BaseTypesMapping;
import MetoXML.Util.ClassFinder;

import com.salama.util.ClassLoaderUtil;
import com.salama.util.io.DirectoryRecursiveVisitor;

/**
 * 
 * @author XingGu Liu
 *
 */
public class PreScanClassFinder implements ClassFinder {
	private static final Logger logger = Logger.getLogger(PreScanClassFinder.class);

	protected HashMap<String, Class<?>> _classFullNameMap = new HashMap<String, Class<?>>();

	protected HashMap<String, Class<?>> _classNameMap = new HashMap<String, Class<?>>();

	public PreScanClassFinder() {
	}

	public void clearPreScannedClass() {
		_classFullNameMap.clear();
		_classNameMap.clear();
	}
	
	public void loadClassOfPackage(String scanBasePackage) {
		loadClasses(scanBasePackage, _classNameMap, _classFullNameMap);
	}

	@Override
	public Class<?> findClass(String className) throws ClassNotFoundException {
		Class<?> cls = null;
		
		cls = BaseTypesMapping.GetSupportedTypeByDisplayName(className);
		if(cls != null) {
			return cls;
		}
		
		cls = _classFullNameMap.get(className);
		if(cls != null) {
			return cls;
		}
		
		cls = _classNameMap.get(className);
		if(cls != null) {
			return cls;
		}

		cls = ClassLoaderUtil.getDefaultClassLoader().loadClass(className);
		if(cls != null) {
			return cls;
		}
		
		return null;
	}
	
	protected void loadClasses(String packageName, 
			HashMap<String, Class<?>> classNameMap, 
			HashMap<String, Class<?>> classFullNameMap) {

		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> enumerPackageUrl;
		try {
			enumerPackageUrl = ClassLoaderUtil.getDefaultClassLoader().getResources(packageDirName);
			URL url = null;
			String protocol = null;
			String filePath = null;
			
			while (enumerPackageUrl.hasMoreElements()) {
				url = enumerPackageUrl.nextElement();
				protocol = url.getProtocol();

				logger.debug("loadClasses() url:" + url.toString());
				
				if ("file".equals(protocol)) {
					filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					if(File.separatorChar != '/') {
						//the url on windows is like: /D://xxxxxxxxxx
						if(filePath.charAt(0) == '/') {
							filePath = filePath.substring(1);
						}

						filePath = filePath.replace('/', File.separatorChar);
					}

					ClassFileFilter classFilieFilter = new ClassFileFilter(filePath);
					RecurseDirForClassFile recursePackageDir = new RecurseDirForClassFile(
							packageName, classFilieFilter, classNameMap, classFullNameMap);
					recursePackageDir.recursiveVisit();
				} else if ("jar".equals(protocol)) {
					JarFile jarFile = null;
					try {
						jarFile = ((JarURLConnection) url.openConnection())
								.getJarFile();
						loadClassesForJarFile(packageName, jarFile, classNameMap, classFullNameMap);						
					} catch (IOException e) {
						logger.error("loadClasses()", e);
					} finally {
						if(jarFile != null) {
							try {
								jarFile.close();
							} catch (Exception e) {
							}
						}
					}
				}//if
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	protected static void RecursivePackageFilePath(String packageName, String packagePath, 
//			final boolean recursive, Set<Class<?>> classes) {
//		// ��ȡ�˰��Ŀ¼ ����һ��File
//		File dir = new File(packagePath);
//		// �����ڻ��� Ҳ����Ŀ¼��ֱ�ӷ���
//		if (!dir.exists() || !dir.isDirectory()) {
//			logger.warn("�û�������� " + packageName + " ��û���κ��ļ�");
//			return;
//		}
//		// ������ �ͻ�ȡ���µ������ļ� ����Ŀ¼
//		File[] dirfiles = dir.listFiles(new FileFilter() {
//			// �Զ�����˹��� ������ѭ��(����Ŀ¼) ��������.class��β���ļ�(����õ�java���ļ�)
//			public boolean accept(File file) {
//				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
//			}
//		});
//		// ѭ�������ļ�
//		for (File file : dirfiles) {
//			// �����Ŀ¼ �����ɨ��
//			if (file.isDirectory()) {
//				findAndAddClassesInPackageByFile(
//						packageName + "." + file.getName(),
//						file.getAbsolutePath(), recursive, classes);
//			} else {
//				// �����java���ļ� ȥ�������.class ֻ��������
//				String className = file.getName().substring(0,
//						file.getName().length() - 6);
//				try {
//					// ��ӵ�������ȥ
//					classes.add(Class.forName(packageName + '.' + className));
//				} catch (ClassNotFoundException e) {
//					logger.error("����û��Զ�����ͼ����� �Ҳ��������.class�ļ�");
//					e.printStackTrace();
//				}
//			}
//		}
//	}
	
	protected void loadClassesForJarFile(
			String targetPackageName, 
			JarFile jarFile,
			HashMap<String, Class<?>> classNameMap, 
			HashMap<String, Class<?>> classFullNameMap) {
		ClassJarEntryFilter entryFilter = new ClassJarEntryFilter(targetPackageName);
		
		Enumeration<JarEntry> entries = jarFile.entries();
		JarEntry entry;
		String className;
		String classFullName;
		int index;
		
		while(entries.hasMoreElements()) {
			entry = entries.nextElement();
			if(entryFilter.accept(entry)) {
				//Load class
				classFullName = entry.getName().replace('/', '.');
				if(classFullName.charAt(0) == '/') {
					classFullName = classFullName.substring(1, classFullName.length() - 6); 
				} else {
					classFullName = classFullName.substring(0, classFullName.length() - 6);
				}
				
				index = classFullName.lastIndexOf('.');
				if(index < 0) {
					className = classFullName; 
				} else {
					className = classFullName.substring(index + 1);
				}
				
				try {
					//Add into the map
					Class<?> cls = ClassLoaderUtil.getDefaultClassLoader().loadClass(classFullName);
					classNameMap.put(className, cls);
					classFullNameMap.put(classFullName, cls);
					logger.debug("Loaded " + classFullName);
				} catch (ClassNotFoundException e) {
					logger.error("loadClassesForJarFile()", e);
				}
			}
		}
	}
	
	public HashMap<String, Class<?>> getClassFullNameMap() {
		return _classFullNameMap;
	}

	public HashMap<String, Class<?>> getClassNameMap() {
		return _classNameMap;
	}
	
	protected class ClassFileFilter implements FileFilter {
		private String _targetPackagePath;
		
		public String getTargetPackagePath() {
			return _targetPackagePath;
		}

		public void setTargetPackagePath(String targetPackagePath) {
			_targetPackagePath = targetPackagePath;
		}

		public ClassFileFilter(String targetPackagePath) {
			_targetPackagePath = targetPackagePath;
			
			if(!_targetPackagePath.endsWith(File.separator)) {
				_targetPackagePath += File.separator;
			}
		}
		
		@Override
		public boolean accept(File pathname) {
			if(pathname.getAbsolutePath().startsWith(_targetPackagePath) 
					&& pathname.getAbsolutePath().toLowerCase().endsWith(".class")) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	protected class RecurseDirForClassFile extends DirectoryRecursiveVisitor {
		private String _targetPackageName;
		//private String _targetPackageDirName;
		private String _packageBaseDirPath;
		private ClassFileFilter _classFileFilter; 
		
		private HashMap<String, Class<?>> _classNameMap;
		private HashMap<String, Class<?>> _classFullNameMap;		
		
		public RecurseDirForClassFile (String targetPackageName, ClassFileFilter classFileFilter,
				HashMap<String, Class<?>> classNameMap, 
				HashMap<String, Class<?>> classFullNameMap) {
			super(classFileFilter);
			
			_classFileFilter = classFileFilter;
			_targetPackageName = targetPackageName;
			//_targetPackageDirName = targetPackageName.replace('.', File.separatorChar);
			
			_packageBaseDirPath = classFileFilter._targetPackagePath.substring(
					0,  classFileFilter._targetPackagePath.length() - 1 - _targetPackageName.length()); 
			
			logger.debug("_packageBaseDirPath:" + _packageBaseDirPath);
			logger.debug("_classFileFilter._targetPackagePath:" + _classFileFilter._targetPackagePath);
			
			_classNameMap = classNameMap;
			_classFullNameMap = classFullNameMap;
	    }
		
		public void recursiveVisit() {
			super.recursiveVisit(new File(_classFileFilter._targetPackagePath));
		}
		
		@Override
		protected void dealEnterDirectory(File arg0) {
			//Nothing TODO
		}
		
		@Override
		protected void dealFile(File file) {
			String className = file.getName().substring(0, file.getName().length() - 6);
			String classFullName = file.getAbsolutePath().substring(
					_packageBaseDirPath.length(), file.getAbsolutePath().length() - 6);
			classFullName = classFullName.replace(File.separatorChar, '.');
			
			try {
				//Add into the map
				Class<?> cls = ClassLoaderUtil.getDefaultClassLoader().loadClass(classFullName);
				_classNameMap.put(className, cls);
				_classFullNameMap.put(classFullName, cls);

				logger.debug("Loaded " + classFullName);
			} catch (ClassNotFoundException e) {
				logger.error("dealFile()", e);
			}
		}
		
		@Override
		protected void dealLeaveDirectory(File arg0) {
			//Nothing TODO
			
		}

		@Override
		protected void dealLeaveLeafDirectory(File arg0) {
			//Nothing TODO
			
		}
	}
	
	protected class ClassJarEntryFilter{
		private String _targetPackageJarEntryName;
		private String _targetPackageJarEntryName2;
		
		public ClassJarEntryFilter(String targetPackageName) {
			_targetPackageJarEntryName = targetPackageName.replace('.', '/') + '/';
			_targetPackageJarEntryName2 = '/' + _targetPackageJarEntryName;
		}
		
		public boolean accept(JarEntry entry) {
			String entryName = entry.getName();
			boolean result = true;

			if(entryName.charAt(0) == '/') {
				result = entryName.startsWith(_targetPackageJarEntryName2);
			} else {
				result = entryName.startsWith(_targetPackageJarEntryName);
			}
			
			if(result) {
				if(entryName.endsWith(".class")) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
}
