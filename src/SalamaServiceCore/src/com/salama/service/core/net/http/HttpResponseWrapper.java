package com.salama.service.core.net.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.salama.service.core.net.RequestWrapper;
import com.salama.service.core.net.ResponseWrapper;

/**
 * 
 * @author XingGu Liu
 *
 */
public class HttpResponseWrapper implements ResponseWrapper {
	public final static String HEADER_NAME_CONTENT_DISPOSITION = "Content-Disposition";

	protected ServletResponse _response = null;

	protected Charset _defaultCharset = null;
	
	public HttpResponseWrapper(HttpServletResponse response) {
		this(response, 1024, Charset.forName("utf-8"));
	}
	
	public HttpResponseWrapper(HttpServletResponse response, 
			int bufferSize, Charset defaultCharset) {
		_defaultCharset = defaultCharset;
		_response = response;
		_response.setBufferSize(bufferSize);
		_response.setCharacterEncoding(defaultCharset.name());
	}

	@Override
	public ServletResponse getResponse() {
		return _response;
	}
	
	@Override
	public void flushBuffer() throws IOException {
		_response.flushBuffer();
	}
	
	@Override
	public int getBufferSize() {
		return _response.getBufferSize();
	} 
	
	@Override
	public Locale getLocale() {
		return _response.getLocale();
	}

	@Override
	public String getContentType() {
		return _response.getContentType();
	}
	
	@Override
	public ServletOutputStream	getOutputStream() throws IOException {
		return _response.getOutputStream();
	} 	
	
	@Override
	public PrintWriter getWriter() throws IOException {
		return _response.getWriter();
	}
	
	@Override
	public boolean isCommitted() {
		return _response.isCommitted();
	}
	
	@Override
	public void reset() {
		_response.reset();
	}
	
	@Override
	public void resetBuffer() {
		_response.resetBuffer();
	}
	
	@Override
	public void setBufferSize(int size) {
		_response.setBufferSize(size);
	}
	
	@Override
	public void setContentType(String type) {
		_response.setContentType(type);
	}
	
	@Override
	public void setContentLength(int len) {
		_response.setContentLength(len);
	}

	@Override
	public void setCharacterEncoding(String charset) {
		_response.setCharacterEncoding(charset);
	} 
	
	@Override
	public String getCharacterEncoding() {
		return _response.getCharacterEncoding();
	} 
	
	@Override
	public void setLocale(Locale loc) {
		_response.setLocale(loc);
	}
	
	@Override
	public void writeFile(File srcFile) throws IOException {
		FileInputStream fs = null;
		int buffLen = _response.getBufferSize();
		byte[] tempBuff = new byte[buffLen];
		int readCount = 0;
		OutputStream outPutStream = _response.getOutputStream();		
		try {
			fs = new FileInputStream(srcFile);
			while(true) {
				readCount = fs.read(tempBuff, 0, buffLen);
				
				if(readCount <= 0) {
					break;
				} 
				
				outPutStream.write(tempBuff, 0, readCount);
			}
		} finally {
			try {
				fs.close();
			} catch(IOException e) {
			}
			try {
				outPutStream.close();
			} catch(IOException e) {
			}
		}
	}

	@Override
	public void addHeader(String name, String value) {
		((HttpServletResponse)_response).addHeader(name, value);
	}

	@Override
	public void setHeader(String name, String value) {
		((HttpServletResponse)_response).setHeader(name, value);
	}

	@Override
	public void addDateHeader(String name, long date) {
		((HttpServletResponse)_response).addDateHeader(name, date);
	}
	
	@Override
	public String getHeader(String name) {
		return ((HttpServletResponse)_response).getHeader(name);
	}
	
	@Override
	public Collection<String> getHeaderNames() {
		return ((HttpServletResponse)_response).getHeaderNames();
	}
	
	@Override
	public Collection<String> getHeaders(String name) {
		return ((HttpServletResponse)_response).getHeaders(name);
	}
	
	@Override
	public int getStatus() {
		return ((HttpServletResponse)_response).getStatus();
	}
	
	@Override
	public void sendError(int sc) throws IOException {
		((HttpServletResponse)_response).sendError(sc);
	}
	
	@Override
	public void sendError(int sc, String msg) throws IOException {
		((HttpServletResponse)_response).sendError(sc, msg);
	}
	
	@Override
	public void setDateHeader(String name, long date) {
		((HttpServletResponse)_response).setDateHeader(name, date);
	}
	
	@Override
	public void setStatus(int sc) {
		((HttpServletResponse)_response).setStatus(sc);
	}
	
	@Override
	public void setDownloadFileName(String downloadFileName, String fileNameEncoding) {
		String encodedFileName = null;
		
		try {
			encodedFileName = URLEncoder.encode(downloadFileName, fileNameEncoding);
		} catch (UnsupportedEncodingException e) {
			encodedFileName = downloadFileName;
		}
		
		((HttpServletResponse)_response).addHeader(
				HEADER_NAME_CONTENT_DISPOSITION, 
				"attachment;filename=" + encodedFileName);
	}

	@Override
	public void setDownloadFileName(RequestWrapper request,
			ResponseWrapper response, String downloadFileName) {
		String userAgent = ((HttpServletRequest)request.getRequest()).getHeader("user-agent");
		String encodedFileName = null;
		try {
			encodedFileName = URLEncoder.encode(downloadFileName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			encodedFileName = downloadFileName;
		}

		String fileNamePart = "filename=" + encodedFileName;
		if(userAgent != null) {
			userAgent = userAgent.toLowerCase();
			if(userAgent.indexOf("msie") >= 0) {
				fileNamePart = "filename=" + encodedFileName;
			} else if(userAgent.indexOf("opera") >= 0) {
				fileNamePart = "filename*=UTF-8''" + encodedFileName;  
			} else if(userAgent.indexOf("safari") >= 0) {
				try {
					fileNamePart = "filename=\"" + new String(downloadFileName.getBytes("UTF-8"),"iso-8859-1") + "\"";
				} catch (UnsupportedEncodingException e) {
				}  
			} else if(userAgent.indexOf("applewebkit") >= 0) {
				fileNamePart = "filename=" + encodedFileName;
			} else if(userAgent.indexOf("mozilla") >= 0) {
				fileNamePart = "filename*=UTF-8''" + encodedFileName;  
			}
		}

		response.addHeader(HttpResponseWrapper.HEADER_NAME_CONTENT_DISPOSITION, "attachment;" + fileNamePart);
	}
	
}
