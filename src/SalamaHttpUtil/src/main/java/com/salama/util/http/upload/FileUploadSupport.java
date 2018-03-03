package com.salama.util.http.upload;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;

import com.salama.service.core.net.http.HttpSessionWrapper;
import com.salama.service.core.net.http.MultipartFile;
import com.salama.service.core.net.http.MultipartRequestWrapper;

/**
 * 
 * @author XingGu Liu
 *
 */
public class FileUploadSupport {
	private final static String DEFAULT_TEMP_DIR = "/WEB-INF/temp";
	
	private String _defaultEncoding = "utf-8";

	private String _webOriginalEncoding = "iso-8859-1";
	
	private long _fileSizeMax = 1048576;
	
	private long _sizeMax = 20971520;
	
	private int _sizeThreshold = DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD;
	
	private File _tempDir = null;
	
	private FileCleaningTracker _fileCleaningTracker = null;
	//private DiskFileItemFactory _diskFileItemFactory = null;
	
//	private HashMap<String, ServletFileUpload> _fileUploadEncodingMap = new HashMap<String, ServletFileUpload>();
	
	/**
	 * 
	 * @param servletContext
	 * @param defaultEncoding
	 * @param fileSizeMax
	 * @param sizeMax
	 * @param sizeThreshold:
	 * @param tempDirPath
	 */
	public FileUploadSupport(
			ServletContext servletContext,
			String defaultEncoding, long fileSizeMax, long sizeMax, 
			int sizeThreshold, String tempDirPath) {
		_defaultEncoding = defaultEncoding;
		_fileSizeMax = fileSizeMax;
		_sizeMax = sizeMax;
		_sizeThreshold = sizeThreshold;
		
		String defaultTempDirPath = servletContext.getRealPath(DEFAULT_TEMP_DIR);
		if(tempDirPath == null || tempDirPath.length() == 0) {
			tempDirPath = defaultTempDirPath;
		} 
		_tempDir = new File(tempDirPath);
		if(!_tempDir.exists()) {
			boolean mkdirsSuccess = false;
			try {
				mkdirsSuccess = _tempDir.mkdirs();
			} catch (Exception e) {
			}
			
			if(!mkdirsSuccess && !tempDirPath.equals(defaultTempDirPath)) {
				_tempDir = new File(defaultTempDirPath);
				if(!_tempDir.exists()) {
					_tempDir.mkdirs();
				}
			}
		}
		
		_fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(servletContext);

		//_diskFileItemFactory = new DiskFileItemFactory(sizeThreshold, _tempDir);
		//_diskFileItemFactory.setFileCleaningTracker(fileCleaningTracker);

		//createFileUpload(_defaultEncoding);
	}

	public static boolean isMultipartContent(HttpServletRequest request) {
		return ServletFileUpload.isMultipartContent(request);
	}
	
	public MultipartRequestWrapper parseMultipartRequest(HttpServletRequest request) throws FileUploadException {
		return parseRequest(request); 
	}
	
	public String getTempDirPath() {
		return _tempDir.getAbsolutePath();
	}
	
	private MultipartRequestWrapper parseRequest(HttpServletRequest request) throws FileUploadException {
		String encoding = getEncoding(request);
		boolean needConvertEncoding = false;
		if(!encoding.toLowerCase().equals(_webOriginalEncoding)) {
			needConvertEncoding = true;
		}
		
		ServletFileUpload fileUpload = getFileUpload(encoding);
		
		List fileItems = fileUpload.parseRequest(request);
		Iterator iteratorFileItems = fileItems.iterator();

		FileItem fileItem = null;
		String fieldName = null;
		String fieldValue = null;
		
		MultipartFile multipartFile = null;
		
		Hashtable<String, String> fieldMap = new Hashtable<String, String>();
		Hashtable<String, MultipartFile> multipartFileMap = new Hashtable<String, MultipartFile>();

		while(iteratorFileItems.hasNext()) {
			fileItem = (FileItem) iteratorFileItems.next();
			
			if(fileItem.isFormField()) {
				fieldName = fileItem.getFieldName();
				fieldValue = fileItem.getString();
				
				if(needConvertEncoding) {
					try {
						fieldValue = new String(fieldValue.getBytes(_webOriginalEncoding), encoding);
					} catch (UnsupportedEncodingException e) {
					}
				}
				fieldMap.put(fieldName, fieldValue);
			} else {
				multipartFile = new MultipartFile(fileItem);
				
				multipartFileMap.put(multipartFile.getName(), multipartFile);
			}
		}
		
		return new MultipartRequestWrapper(
				request, new HttpSessionWrapper(request.getSession()),
				fieldMap, multipartFileMap);
	}
	
	
	private ServletFileUpload getFileUpload(String encoding) {
		/*
		ServletFileUpload fileUpload = _fileUploadEncodingMap.get(encoding);
		
		if(fileUpload != null) {
			return fileUpload;
		} else {
			return createFileUpload(encoding);
		}
		*/
		return createFileUpload(encoding);		
	}
	
	private ServletFileUpload createFileUpload(String encoding) {
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory(_sizeThreshold, _tempDir);
		diskFileItemFactory.setFileCleaningTracker(_fileCleaningTracker);

		ServletFileUpload fileUpload = new ServletFileUpload(diskFileItemFactory);
		fileUpload.setHeaderEncoding(encoding);
		fileUpload.setFileSizeMax(_fileSizeMax);
		fileUpload.setSizeMax(_sizeMax);
		
		//_fileUploadEncodingMap.put(fileUpload.getHeaderEncoding(), fileUpload);
		
		return fileUpload;
	}
	
	private String getEncoding(HttpServletRequest request) {
		String encoding = request.getCharacterEncoding();

		if (encoding == null) {
			encoding = _defaultEncoding;
		}
		
		return encoding;
	}  
	
}
