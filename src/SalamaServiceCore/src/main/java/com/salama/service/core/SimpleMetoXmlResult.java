package com.salama.service.core;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;

import MetoXML.XmlDeserializer;
import MetoXML.XmlReader;
import MetoXML.XmlSerializer;
import MetoXML.XmlWriter;
import MetoXML.Base.XmlContentEncoder;
import MetoXML.Base.XmlNode;
import MetoXML.Base.XmlParseException;

/**
 * Xml format:
 * <SimpleMetoXmlResult>
 * 	<Error>
 * 		[Error message]
 * 	</Error>
 * 	<Result>
 * 		[Nodes of data properties]
 * 	</Result>
 * </SimpleMetoXmlResult>
 * @author XingGu Liu
 *
 */
public class SimpleMetoXmlResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5401356051527130958L;

	private static final Logger log = Logger.getLogger(SimpleMetoXmlResult.class);
	
	public static final String ROOT_NODE_TAG_NAME = "SimpleMetoXmlResult";

	public static final String ERROR_NODE_TAG_NAME = "Error";
	
	public static final String RESULT_NODE_TAG_NAME = "Result";
	
	public static final String RETURN_LINE = "\n";
	
	private String error = "";
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Object getResultValue() {
		return resultValue;
	}

	public void setResultValue(Object resultValue) {
		this.resultValue = resultValue;
	}

	private Object resultValue = null;
	
	/*
	public static String errorResultXml(Error error, Charset charset) {
		try {
			XmlSerializer xmlSer = new XmlSerializer();
			
			//String resultValueTagName = XmlSerializer.GetTypeTagName(resultValueType);
			//XmlNode resultNode = xmlSer.ConvertObjectToXmlNode(RESULT_NODE_TAG_NAME, resultValue, resultValueType);
			
			XmlNode rootNode = new XmlNode();
			rootNode.setName(ROOT_NODE_TAG_NAME);
			
			XmlNode errorNode = xmlSer.ConvertObjectToXmlNode(ERROR_NODE_TAG_NAME, error, Error.class);
			
			rootNode.setFirstChildNode(errorNode);
			//rootNode.setLastChildNode(resultNode);
			
			errorNode.setParentNode(rootNode);
			//resultNode.setParentNode(rootNode);
			
			//errorNode.setNextNode(resultNode);
			//resultNode.setPreviousNode(errorNode);
			
			XmlWriter xmlWriter = new XmlWriter();
			
			return xmlWriter.XmlNodeToString(rootNode, charset);
		} catch (IntrospectionException e) {
			log.error(e);
			return errorResultXml(e);
		} catch (IllegalAccessException e) {
			log.error(e);
			return errorResultXml(e);
		} catch (InvocationTargetException e) {
			log.error(e);
			return errorResultXml(e);
		} catch (IOException e) {
			log.error(e);
			return errorResultXml(e);
		}
	}

	*/

	/*
	public static String resultToXml(Object resultValue, Class<?> resultValueType, Charset charset) {
		resultToXml(resultValue, resultValueType, charset, false, false);
	}
	*/
	
	public static String resultToXml(Object resultValue, Class<?> resultValueType, Charset charset,
			boolean isUseClassFullName, boolean isMakeFirstCharUpperCase) {
		return resultToXml(resultValue, resultValueType, null, charset, isUseClassFullName, isMakeFirstCharUpperCase);
	}

	private static String resultToXml(Object resultValue, Class<?> resultValueType, Error error, Charset charset, 
			boolean isUseClassFullName, boolean isMakeFirstCharUpperCase) {
		try {
			XmlSerializer xmlSer = new XmlSerializer();
			
			//String resultValueTagName = XmlSerializer.GetTypeTagName(resultValueType);
			XmlNode resultNode = xmlSer.ConvertObjectToXmlNode(RESULT_NODE_TAG_NAME, resultValue, resultValueType, isUseClassFullName, isMakeFirstCharUpperCase);
			
			XmlNode rootNode = new XmlNode();
			rootNode.setName(ROOT_NODE_TAG_NAME);
			
			XmlNode errorNode = null;

			if(error != null) {
				errorNode = xmlSer.ConvertObjectToXmlNode(ERROR_NODE_TAG_NAME, error, Error.class);
			} else {
				errorNode = new XmlNode();
				errorNode.setName(ERROR_NODE_TAG_NAME);
				errorNode.setContent("");
			}
			
			rootNode.setFirstChildNode(errorNode);
			rootNode.setLastChildNode(resultNode);
			
			errorNode.setParentNode(rootNode);
			resultNode.setParentNode(rootNode);
			
			errorNode.setNextNode(resultNode);
			resultNode.setPreviousNode(errorNode);
			
			XmlWriter xmlWriter = new XmlWriter();
			
			return xmlWriter.XmlNodeToString(rootNode, charset);
		} catch (IntrospectionException e) {
			log.error(e);
			return errorResultXml(e);
		} catch (IllegalAccessException e) {
			log.error(e);
			return errorResultXml(e);
		} catch (InvocationTargetException e) {
			log.error(e);
			return errorResultXml(e);
		} catch (IOException e) {
			log.error(e);
			return errorResultXml(e);
		}
	} 
	
	public static Object xmlToResultData(String resultXml, Class<?> resultValueType, Charset charset) 
			throws IOException, XmlParseException, InvocationTargetException, 
			IllegalAccessException, InstantiationException, NoSuchMethodException
	{
		try {
			XmlReader xmlReader = new XmlReader();
			XmlNode rootNode = xmlReader.StringToXmlNode(resultXml, charset);
			
			
			XmlNode resultNode = rootNode.getLastChildNode();
			if(!resultNode.getName().equals(RESULT_NODE_TAG_NAME)) {
				resultNode = rootNode.getFirstChildNode();
				
				if(!resultNode.getName().equals(RESULT_NODE_TAG_NAME)) {
					//There is no tag named "Result"
					return null;
				}
			}
			
			XmlDeserializer xmlDes = new XmlDeserializer();
			return xmlDes.ConvertXmlNodeToObject(resultNode, resultValueType);
		} catch (IOException e) {
			log.error(e);
			throw e;
		} catch (XmlParseException e) {
			log.error(e);
			throw e;
		} catch (InvocationTargetException e) {
			log.error(e);
			throw e;
		} catch (IllegalAccessException e) {
			log.error(e);
			throw e;
		} catch (InstantiationException e) {
			log.error(e);
			throw e;
		} catch (NoSuchMethodException e) {
			log.error(e);
			throw e;
		}
	}

	public static String errorResultXml(Exception e) {
		return errorResultXml(e.getClass().getName(), e.getMessage());
	}

	public static String errorResultXml(Error error) {
		return errorResultXml(error.getType(), error.getMsg());
	}

	public static String emptyResultXml() {
		StringBuilder resultXml = new StringBuilder();
		
		resultXml.append("<" + ROOT_NODE_TAG_NAME + ">" + RETURN_LINE);
		resultXml.append("  <" + ERROR_NODE_TAG_NAME + ">" + "</" + ERROR_NODE_TAG_NAME + ">" + RETURN_LINE);
		resultXml.append("</" + ROOT_NODE_TAG_NAME + ">");
		
		return resultXml.toString();
	}

	public static String errorResultXml(String errorType, String errorMsg) {
		StringBuilder resultXml = new StringBuilder();
		
		resultXml.append("<" + ROOT_NODE_TAG_NAME + ">" + RETURN_LINE);
		resultXml.append("  <" + ERROR_NODE_TAG_NAME + ">" + RETURN_LINE);
		resultXml.append("    <Type>" + errorType + "</Type>" + RETURN_LINE);
		resultXml.append("    <Msg>" + XmlContentEncoder.EncodeContent(errorMsg) + "</Msg>" + RETURN_LINE);
		resultXml.append("  </" + ERROR_NODE_TAG_NAME + ">" + RETURN_LINE);
		resultXml.append("</" + ROOT_NODE_TAG_NAME + ">");
		
		return resultXml.toString();
	}
	
	
}
