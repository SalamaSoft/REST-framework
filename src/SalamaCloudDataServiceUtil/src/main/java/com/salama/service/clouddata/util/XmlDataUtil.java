package com.salama.service.clouddata.util;

import MetoXML.Base.XmlContentEncoder;
import MetoXML.Base.XmlParseException;
import MetoXML.XmlDeserializer;
import MetoXML.XmlSerializer;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * 
 * @author XingGu Liu
 *
 */
public final class XmlDataUtil {

	public static void appendXmlTagBegin(StringBuilder xml, String tagName) {
		xml.append("<");
		xml.append(tagName);
		xml.append(">");
	}

	public static void appendXmlTagEnd(StringBuilder xml, String tagName) {
		xml.append("</");
		xml.append(tagName);
		xml.append(">");
	}
	
	public static void appendXmlLeafTag(StringBuilder xml, String tagName, String value) {
		XmlDataUtil.appendXmlTagBegin(xml, tagName);
		//encode value
		xml.append(XmlContentEncoder.EncodeContent(value));
		
		XmlDataUtil.appendXmlTagEnd(xml, tagName);
	}

	public static String convertObjectToXml(Object obj, Class<?> objType) 
			throws IOException, InvocationTargetException, IllegalAccessException, IntrospectionException {
		return XmlSerializer.objectToString(obj, objType, false, false);
	}

	public static Object convertXmlToObject(String xml, Class<?> objType) 
			throws IOException, InvocationTargetException, IllegalAccessException, IntrospectionException, 
			NoSuchMethodException, InstantiationException, XmlParseException {
		return XmlDeserializer.stringToObject(xml, objType);
	}

}
