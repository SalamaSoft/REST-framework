package com.salama.service.core.net;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface ResponseWrapper {

	public ServletResponse getResponse();
	
	public void flushBuffer() throws IOException;
	
	public int getBufferSize();
	
	public Locale getLocale();

	public String getContentType();
	
	public ServletOutputStream	getOutputStream() throws IOException;
	
	public PrintWriter getWriter() throws IOException;
	
	public boolean isCommitted();
	
	public void reset();
	
	public void resetBuffer();
	
	public void setBufferSize(int size);
	
	public void setContentType(String type);
	
	public void setContentLength(int len);

	public void setCharacterEncoding(String charset);
	
	public String getCharacterEncoding();
	
	public void setLocale(Locale loc);
	
	public void writeFile(File srcFile) throws IOException;
	
	public void addHeader(String name, String value);
	
	public void setHeader(String name, String value);

	public void setDateHeader(String name, long date);
	
	public void addDateHeader(String name, long date);

	public String getHeader(String name);

	public Collection<String> getHeaderNames();
	
	public Collection<String> getHeaders(java.lang.String name);

	public int getStatus();

	public void setStatus(int sc);
	
	public void sendError(int sc) throws IOException;
	
	public void sendError(int sc, java.lang.String msg) throws IOException;
	
	/**
	 * @deprecated
	 * @param fileName
	 * @param fileNameEncoding
	 */
	public void setDownloadFileName(String fileName, String fileNameEncoding);
	
	public void setDownloadFileName(RequestWrapper request, ResponseWrapper response, String fileName);
	
}
