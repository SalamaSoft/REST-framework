package com.salama.service.clouddata.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
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

import CollectionCommon.ITreeNode;
import MetoXML.AbstractReflectInfoCachedSerializer;

/**
 * 
 * @author XingGu Liu
 *
 */
public class SimpleJSONDataUtil extends AbstractReflectInfoCachedSerializer {
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
//		PropertyDescriptor[] properties = Introspector.getBeanInfo(objType).getPropertyDescriptors();
		PropertyDescriptor[] properties = findPropertyDescriptorArray(objType);
		
		Object data = objType.newInstance();
		
		PropertyDescriptor property;
		String propName;
		for(int i = 0; i < properties.length; i++) {
			property = properties[i];
			propName = property.getName(); 
			if(propName.equals("class")) {
				continue;
			}
			
			if(!jsonObject.has(propName)) {
				continue;
			}
							
			Object value;
			try {
				if(isList(property.getPropertyType())) {
					String strValue = jsonObject.get(propName).toString();
					value = convertJSONToListObject(strValue, objType);
				} else if(isArray(property.getPropertyType())) {
					JSONArray jsonArray = (JSONArray) jsonObject.get(propName);
					
					Class<?> elementType = property.getPropertyType().getComponentType();
					value = Array.newInstance(elementType, jsonArray.length());
					for(int k = 0; k < jsonArray.length(); k++) {
						JSONObject jsonObjInArray = jsonArray.getJSONObject(k);
						Array.set(value, k, convertJSONToObject(jsonObjInArray, elementType));
					}
				} else if(property.getPropertyType() == String.class) {
					value = jsonObject.getString(propName);
				} else {
					String strValue = jsonObject.get(propName).toString();
					value = Convert(property.getPropertyType(), strValue);
				}
			} catch(JSONException e) {
				//null
				logger.warn("convertJSONToObject()", e);
				value = Convert(property.getPropertyType(), null);
			}
			
			property.getWriteMethod().invoke(data, value);
		}
		return data;
	}
	
    public static Object Convert(Class<?> type, String valueStr) throws IllegalAccessException, ParseException, IntrospectionException, InstantiationException, JSONException, InvocationTargetException {
		Class<?> cls = (Class<?>) type;
		if(cls == String.class) {
			return valueStr;
		} else if(cls == boolean.class) {
			return valueStr == null? Boolean.valueOf(false) : Boolean.valueOf(valueStr);
		} else if(cls == byte.class) {
	    	return valueStr == null? Byte.valueOf((byte)0) : Byte.valueOf(valueStr);
    	} else if(cls == short.class) {
    		return valueStr == null? Short.valueOf((short)0) : Short.valueOf(valueStr);
		} else if(cls == int.class) {
			return valueStr == null? Integer.valueOf(0) : Integer.valueOf(valueStr);
		} else if(cls == long.class) {
			return valueStr == null? Long.valueOf(0) : Long.valueOf(valueStr);
		} else if(cls == float.class) {
			return valueStr == null? Float.valueOf(0) : Float.valueOf(valueStr);
		} else if(cls == double.class) {
			return valueStr == null? Double.valueOf(0) : Double.valueOf(valueStr);
		} else if(cls == char.class) {
			return valueStr == null? Character.valueOf('\0') : valueStr.charAt(0);
		} else if(cls == Boolean.class) {
			return valueStr == null? Boolean.valueOf(false) : Boolean.valueOf(valueStr);
		} else if(cls == Byte.class) {
	    	return valueStr == null? Byte.valueOf((byte)0) : Byte.valueOf(valueStr);
    	} else if(cls == Short.class) {
    		return valueStr == null? Short.valueOf((short)0) : Short.valueOf(valueStr);
		} else if(cls == Integer.class) {
			return valueStr == null? Integer.valueOf(0) : Integer.valueOf(valueStr);
		} else if(cls == Long.class) {
			return valueStr == null? Long.valueOf(0) : Long.valueOf(valueStr);
		} else if(cls == Float.class) {
			return valueStr == null? Float.valueOf(0) : Float.valueOf(valueStr);
		} else if(cls == Double.class) {
			return valueStr == null? Double.valueOf(0) : Double.valueOf(valueStr);
		} else if(cls == Character.class) {
			return valueStr == null? Character.valueOf('\0') : valueStr.charAt(0);
		} else if(cls == Date.class) {
			return valueStr == null? null : JavaUtilDateFormatForParse.parse(valueStr);
		} else if(cls == java.sql.Date.class) {
			if(valueStr == null) {
				return null;
			} else {
				Date date = JavaSqlDateFormatForParse.parse(valueStr);
				return (new java.sql.Date(date.getTime()));
			}
		} else if(cls == java.sql.Timestamp.class) {
			if(valueStr == null) {
				return null;
			} else {
				Date date = JavaSqlTimeStampFormatForParse.parse(valueStr);
				return new Timestamp(date.getTime());
			}
		} else if(cls == BigDecimal.class) {
			return valueStr == null? BigDecimal.valueOf(0) : (new BigDecimal(valueStr));
		} else {
			return convertJSONToObject(valueStr, cls);
		}
	}

	@Override
	protected void BackwardToNode(ITreeNode arg0, int arg1) {
		//do nothing
	}

	@Override
	protected void ForwardToNode(ITreeNode arg0, int arg1, boolean arg2) {
		//do nothing
	}
	
    private static boolean isArray(Class<?> cls) {
    	return cls.isArray();
    }

    private static boolean isList(Class<?> cls) {
    	return List.class.isAssignableFrom(cls);
    }

}
