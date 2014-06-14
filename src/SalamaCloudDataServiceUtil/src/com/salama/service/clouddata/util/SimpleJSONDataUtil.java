package com.salama.service.clouddata.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author XingGu Liu
 *
 */
public class SimpleJSONDataUtil {
	private static Logger logger = Logger.getLogger(SimpleJSONDataUtil.class);

	private static SimpleDateFormat JavaUtilDateFormatForParse = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
	private static SimpleDateFormat JavaSqlTimeStampFormatForParse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static SimpleDateFormat JavaSqlDateFormatForParse = new SimpleDateFormat("yyyy-MM-dd");
	
	public static JSONObject convertObjectToJSONObject(Object obj) {
		return new JSONObject(obj);
	}

	public static String convertObjectToJSON(Object obj) {
		JSONObject json = new JSONObject(obj);
		
		return json.toString();
	}
	
	public static JSONArray convertListObjectToJSONArray(List<?> objList) {
		JSONArray jsonArray = new JSONArray();
		
		for(Object obj : objList) {
			jsonArray.put(new JSONObject(obj));
		}
		
		return jsonArray;
	}

	public static String convertListObjectToJSON(List<?> objList) {
		return convertListObjectToJSONArray(objList).toString();
	}
	
	public static <T> List<T> convertJSONToListObject(String jsonString, Class<T> objType) 
			throws IntrospectionException, IllegalAccessException, InstantiationException, JSONException, 
	ParseException, InvocationTargetException{
		JSONArray jsonArray = new JSONArray(jsonString);
		
		List<T> listData = new ArrayList<T>();
		int length = jsonArray.length();
		
		JSONObject jsonObject = null;
		T data = null;
		
		for(int i = 0; i < length; i++) {
			jsonObject = jsonArray.getJSONObject(i);
			data = (T) convertJSONToObject(jsonObject, objType);
			
			listData.add(data);
		}
		
		return listData;
	}

	public static Object convertJSONToObject(String jsonString, Class<?> objType) 
			throws IntrospectionException, IllegalAccessException, InstantiationException, JSONException, 
			ParseException, InvocationTargetException {
		JSONObject json = new JSONObject(jsonString);
		return convertJSONToObject(json, objType);
	}	
	
	public static Object convertJSONToObject(JSONObject jsonObject, Class<?> objType) 
			throws IntrospectionException, IllegalAccessException, InstantiationException, JSONException, 
			ParseException, InvocationTargetException {
		PropertyDescriptor[] properties = Introspector.getBeanInfo(objType).getPropertyDescriptors();
		
		Object data = objType.newInstance();
		
		String strValue = null;
		Object value = null;
		PropertyDescriptor property = null;
		String propName = null;
		for(int i = 0; i < properties.length; i++) {
			try {
				property = properties[i];
				propName = property.getName(); 
				if(propName.equals("class")) {
					continue;
				}
				
				strValue = jsonObject.get(propName).toString();
				value = Convert(property.getPropertyType(), strValue);
				property.getWriteMethod().invoke(data, value);
			} catch (JSONException e) {
				//do nothing
				logger.error("convertJSONToObject()", e);
				throw e;
			}
		}
		return data;
	}
	
    public static Object Convert(Class<?> type, String valueStr) throws IllegalAccessException, ParseException, IntrospectionException, InstantiationException, JSONException, InvocationTargetException {
		Class<?> cls = (Class<?>) type;
		if(cls == String.class) {
			return valueStr;
		} else if(cls == boolean.class) {
			return Boolean.valueOf(valueStr);
		} else if(cls == byte.class) {
	    	return Byte.valueOf(valueStr);
    	} else if(cls == short.class) {
    		return Short.valueOf(valueStr);
		} else if(cls == int.class) {
			return Integer.valueOf(valueStr);
		} else if(cls == long.class) {
			return Long.valueOf(valueStr);
		} else if(cls == float.class) {
			return Float.valueOf(valueStr);
		} else if(cls == double.class) {
			return Double.valueOf(valueStr);
		} else if(cls == char.class) {
			return valueStr.charAt(0);
		} else if(cls == Boolean.class) {
			return Boolean.valueOf(valueStr);
		} else if(cls == Byte.class) {
	    	return Byte.valueOf(valueStr);
    	} else if(cls == Short.class) {
    		return Short.valueOf(valueStr);
		} else if(cls == Integer.class) {
			return Integer.valueOf(valueStr);
		} else if(cls == Long.class) {
			return Long.valueOf(valueStr);
		} else if(cls == Float.class) {
			return Float.valueOf(valueStr);
		} else if(cls == Double.class) {
			return Double.valueOf(valueStr);
		} else if(cls == Character.class) {
			return valueStr.charAt(0);
		} else if(cls == Date.class) {
			return JavaUtilDateFormatForParse.parse(valueStr);
		} else if(cls == java.sql.Date.class) {
			Date date = JavaSqlDateFormatForParse.parse(valueStr);
			return new java.sql.Date(date.getTime());
		} else if(cls == java.sql.Timestamp.class) {
			Date date = JavaSqlTimeStampFormatForParse.parse(valueStr);
			return new Timestamp(date.getTime());
		} else if(cls == BigDecimal.class) {
			return new BigDecimal(valueStr);
		} else {
			return convertJSONToObject(valueStr, cls);
		}
	}
	

}
