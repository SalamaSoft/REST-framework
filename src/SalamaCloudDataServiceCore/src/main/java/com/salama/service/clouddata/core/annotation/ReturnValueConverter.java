package com.salama.service.clouddata.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author XingGu Liu
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReturnValueConverter {
	public final static String COVERT_TYPE_PLAIN_TEXT = "text";
	public final static String COVERT_TYPE_XML = "xml";
	public final static String COVERT_TYPE_JSON = "json";
	public final static String COVERT_TYPE_PLAIN_TEXT_JSONP = "text.jsonp";
	public final static String COVERT_TYPE_XML_JSONP = "xml.jsonp";
	public final static String COVERT_TYPE_JSON_JSONP = "json.jsonp";
	
	
	ConverterType value() default ConverterType.PLAIN_TEXT;
	
	/**
	 * If valueFromRequestParam() is not empty , then 
	 * request.getParameter(valueFromRequestParam()) will override the value() to indicate the converter type.
	 * Available values of request.getParameter(valueFromRequestParam()) could be these text(No case sensitive): 
	 * "text", "xml", "json" 
	 * "text.jsonp", "xml.jsonp","json.jsonp" 
	 * If it ends with "jsonp", then return value will be wrapped like:
	 * var jsonpReturnValue = "URLEncoder.encode(${returnValue}, "utf-8").replaceAll("\\+", "%20")")
	 * (If value is not one of these 2 upon. then handle the return value as plain text )
	 * @return
	 */
	String valueFromRequestParam() default "";
	
	String jsonpReturnVariableNameFromRequestParam() default "jsonpReturn";
	
	boolean skipObjectConvert() default false;
}
