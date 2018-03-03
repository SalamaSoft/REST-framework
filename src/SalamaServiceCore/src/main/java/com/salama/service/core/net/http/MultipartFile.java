package com.salama.service.core.net.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItem;

/**
 * 
 * @author XingGu Liu
 *
 */
public class MultipartFile {
	private FileItem _fileItem = null;
	
	public MultipartFile(FileItem fileItem) {
		_fileItem = fileItem;
	}
	
	public String getName() {
		return _fileItem.getFieldName();
	}
	
	public String getOriginalFilename() {
		return _fileItem.getName();
	}
	
	public String getContentType() {
		return _fileItem.getContentType();
	}
	
	public long getSize() {
		return _fileItem.getSize();
	}
	
	public boolean isEmpty() {
		if(_fileItem.getSize() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public InputStream getInputStream() throws IOException {
		return _fileItem.getInputStream();
	}
	
	public void transferTo(File destFile) throws Exception {
		_fileItem.write(destFile);
	}

}
