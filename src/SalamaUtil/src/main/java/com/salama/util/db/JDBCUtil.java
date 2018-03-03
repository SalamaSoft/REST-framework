package com.salama.util.db;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import MetoXML.Util.ITreeNode;
import org.apache.log4j.Logger;

import MetoXML.AbstractReflectInfoCachedSerializer;

public final class JDBCUtil extends AbstractReflectInfoCachedSerializer {
	private static final Logger logger = Logger.getLogger(JDBCUtil.class);
	
	public static Object ResultSetToData(
			ResultSet rs, Class<?> dataClass, 
			boolean isIgnorePropertiesNotExist) 
			throws IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException, SQLException {
		return ResultSetToData(rs, dataClass, isIgnorePropertiesNotExist, true);
	}
	
	
	public static Object ResultSetToData(
			ResultSet rs, Class<?> dataClass, 
			boolean isIgnorePropertiesNotExist, boolean isTrimStr) 
			throws IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException, SQLException {
		Object data = dataClass.newInstance();

		ResultSetMetaData rsMeta = rs.getMetaData();
		int colCount = rsMeta.getColumnCount();
		String colName = "";
		
		PropertyDescriptor prop = null;

		for(int i = 1; i <= colCount; i++) {
			//colType = rsMeta.getColumnType(i);
			colName = rsMeta.getColumnLabel(i);

			//logger.debug("ResultSetToData() colName:" + colName);
			
			try {
				//prop = new PropertyDescriptor(colName, dataClass);
				prop = findPropertyDescriptor(colName, dataClass);
				//logger.debug("ResultSetToData() prop:" + prop);

				if(prop.getWriteMethod() != null) {
					if(isTrimStr && rs.getObject(i) != null && rs.getObject(i).getClass() == String.class) {
						prop.getWriteMethod().invoke(data, rs.getString(i).trim());
					} else if(prop.getPropertyType() == long.class) {
						prop.getWriteMethod().invoke(data, rs.getLong(i));
					} else if(prop.getPropertyType() == int.class) {
						prop.getWriteMethod().invoke(data, rs.getInt(i));
					} else if(prop.getPropertyType() == short.class) {
						prop.getWriteMethod().invoke(data, rs.getShort(i));
					} else if(prop.getPropertyType() == double.class) {
						prop.getWriteMethod().invoke(data, rs.getDouble(i));
					} else if(prop.getPropertyType() == float.class) {
						prop.getWriteMethod().invoke(data, rs.getFloat(i));
					} else if(prop.getPropertyType() == Long.class) {
						prop.getWriteMethod().invoke(data, rs.getLong(i));
					} else if(prop.getPropertyType() == Integer.class) {
						prop.getWriteMethod().invoke(data, rs.getInt(i));
					} else if(prop.getPropertyType() == Short.class) {
						prop.getWriteMethod().invoke(data, rs.getShort(i));
					} else if(prop.getPropertyType() == Double.class) {
						prop.getWriteMethod().invoke(data, rs.getDouble(i));
					} else if(prop.getPropertyType() == Float.class) {
						prop.getWriteMethod().invoke(data, rs.getFloat(i));
					} else {
						if(String.class == prop.getPropertyType()) {
							if(rs.getObject(i) != null) {
								prop.getWriteMethod().invoke(data, rs.getString(i).trim());
							} else {
								prop.getWriteMethod().invoke(data, (String)null);
							}
						} else {
							prop.getWriteMethod().invoke(data, rs.getObject(i));
						}
					}
				}
			} catch (IllegalArgumentException e1) {
				if(!isIgnorePropertiesNotExist) {
					throw e1;
				}
			} catch (IntrospectionException e1) {
				if(!isIgnorePropertiesNotExist) {
					throw e1;
				}
			} catch (IllegalAccessException e1) {
				if(!isIgnorePropertiesNotExist) {
					throw e1;
				}
			} catch (InvocationTargetException e1) {
				if(!isIgnorePropertiesNotExist) {
					throw e1;
				}
			}
		}
		
		return data;
	}

	public static void setPreparedStatement(PreparedStatement pstmt, int startIndex,
			List<Object> parameterValues, List<Class<?>> parameterTypes
			) 
			throws SQLException {
		if(parameterValues == null || parameterTypes == null) {
			return;
		}
		
		Object value = null;
		Class<?> type = null;
		int index = startIndex;
		
		for(int i = 0; i < parameterTypes.size(); i++) {
			type = parameterTypes.get(i);
			value = parameterValues.get(i);
			
			if(Array.class.isAssignableFrom(type)) {
				pstmt.setArray(index, (Array) value);
			} else if(BigDecimal.class.isAssignableFrom(type)) {
				pstmt.setBigDecimal(index, (BigDecimal) value);
			} else if(Blob.class.isAssignableFrom(type)) {
				pstmt.setBlob(index, (Blob) value);
			} else if(Boolean.class.isAssignableFrom(type)) {
				pstmt.setBoolean(index, (Boolean) value);
			} else if(Byte.class.isAssignableFrom(type)) {
				pstmt.setByte(index, (Byte) value);
			} else if(type.isArray() && type.getComponentType() == byte.class) {
				pstmt.setBytes(index, (byte[]) value);
			} else if(Clob.class.isAssignableFrom(type)) {
				pstmt.setClob(index, (Clob) value);
			} else if(Date.class.isAssignableFrom(type)) {
				pstmt.setDate(index, (Date) value);
			} else if(Double.class.isAssignableFrom(type)) {
				pstmt.setDouble(index, (Double) value);
			} else if(Float.class.isAssignableFrom(type)) {
				pstmt.setFloat(index, (Float) value);
			} else if(Integer.class.isAssignableFrom(type)) {
				pstmt.setInt(index, (Integer) value);
			} else if(Long.class.isAssignableFrom(type)) {
				pstmt.setLong(index, (Long) value);
			} else if(Short.class.isAssignableFrom(type)) {
				pstmt.setShort(index, (Short) value);
			} else if(String.class.isAssignableFrom(type)) {
				pstmt.setString(index, (String) value);
			} else if(Time.class.isAssignableFrom(type)) {
				pstmt.setTime(index, (Time) value);
			} else if(Timestamp.class.isAssignableFrom(type)) {
				pstmt.setTimestamp(index, (Timestamp) value);
			} else if(URL.class.isAssignableFrom(type)) {
				pstmt.setURL(index, (URL) value);
			} else {
				pstmt.setObject(index, value);
			}
			
			index++;
		}
	}


	@Override
	protected void BackwardToNode(ITreeNode arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void ForwardToNode(ITreeNode arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

}
