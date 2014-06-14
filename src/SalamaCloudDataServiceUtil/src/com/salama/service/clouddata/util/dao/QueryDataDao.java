package com.salama.service.clouddata.util.dao;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONWriter;

import CollectionCommon.ITreeNode;
import MetoXML.AbstractReflectInfoCachedSerializer;

import com.salama.service.clouddata.util.XmlDataUtil;

/**
 * 
 * @author XingGu Liu
 *
 */
public final class QueryDataDao extends AbstractReflectInfoCachedSerializer {
	private final static Logger log = Logger.getLogger(QueryDataDao.class);
	/*
	public static <DataType> List<DataType> findData(Connection conn, String sql, Class<DataType> dataType) 
	throws SQLException, InstantiationException, InvocationTargetException, IllegalAccessException {
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(sql);
	
			return findData(conn, stmt, dataType);
		} finally {
			try {
				stmt.close();
			} catch(Exception e) {
			}
		}
	}
	*/
	
	public static <DataType> List<DataType> findData(PreparedStatement stmt, Class<DataType> dataType) 
	throws SQLException, InstantiationException, InvocationTargetException, IllegalAccessException {
		ResultSet rs = null;
		List<DataType> dataList = new ArrayList<DataType>();
		
		try {
			rs = stmt.executeQuery();
			
			ResultSetMetaData rsMeta = rs.getMetaData();
			int colCount = rsMeta.getColumnCount();
			int i = 0;
			String colName = null;
			DataType data = null;
			
			List<PropertyDescriptor> propertyList = new ArrayList<PropertyDescriptor>();
			
			for(i = 1; i <= colCount; i++) {
				colName = rsMeta.getColumnLabel(i);
				try {
					//propertyList.add(new PropertyDescriptor(colName, dataType));
					propertyList.add(findPropertyDescriptor(colName, dataType));
				} catch(IntrospectionException e) {
				}
			}
			
			PropertyDescriptor property = null;
			while(rs.next()) {
				data = dataType.newInstance();

				for(i = 1; i <= colCount; i++) {
					try {
						property =  propertyList.get(i - 1);
						setDataValFromResultSet(data, property, rs);
					} catch(Exception ex) {
					}
				}
				
				dataList.add(data);
			}
			
			return dataList;
		} finally {
			try {
				rs.close();
			} catch(Exception e) {
			}
		}
	}

	private static void setDataValFromResultSet(Object data, PropertyDescriptor property, ResultSet rs) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SQLException  {
		if(property.getPropertyType() == String.class) {
			property.getWriteMethod().invoke(data, rs.getString(property.getName()));
		} else if(property.getPropertyType() == long.class) {
			property.getWriteMethod().invoke(data, rs.getLong(property.getName()));
		} else if(property.getPropertyType() == int.class) {
			property.getWriteMethod().invoke(data, rs.getInt(property.getName()));
		} else if(property.getPropertyType() == short.class) {
			property.getWriteMethod().invoke(data, rs.getShort(property.getName()));
		} else if(property.getPropertyType() == double.class) {
			property.getWriteMethod().invoke(data, rs.getDouble(property.getName()));
		} else if(property.getPropertyType() == float.class) {
			property.getWriteMethod().invoke(data, rs.getFloat(property.getName()));
		} else if(property.getPropertyType() == Long.class) {
			property.getWriteMethod().invoke(data, rs.getLong(property.getName()));
		} else if(property.getPropertyType() == Integer.class) {
			property.getWriteMethod().invoke(data, rs.getInt(property.getName()));
		} else if(property.getPropertyType() == Short.class) {
			property.getWriteMethod().invoke(data, rs.getShort(property.getName()));
		} else if(property.getPropertyType() == Double.class) {
			property.getWriteMethod().invoke(data, rs.getDouble(property.getName()));
		} else if(property.getPropertyType() == Float.class) {
			property.getWriteMethod().invoke(data, rs.getFloat(property.getName()));
		} else {
			property.getWriteMethod().invoke(data, rs.getObject(property.getName()));
		}
	}
	
	public static String findDataXml(PreparedStatement stmt, String rootNodeName, String dataNodeName) 
	throws SQLException {
		ResultSet rs = null;

		try {
			rs = stmt.executeQuery();
			
			ResultSetMetaData rsMeta = rs.getMetaData();
			int colCount = rsMeta.getColumnCount();
			int i = 0;
			//String colName = null;
			String colLabel = null;
			Object colValue = null;
			int colType;
			
			StringBuilder xmlResult = new StringBuilder(); 
			XmlDataUtil.appendXmlTagBegin(xmlResult, rootNodeName);
			while(rs.next()) {
				XmlDataUtil.appendXmlTagBegin(xmlResult, dataNodeName);
				for(i = 1; i <= colCount; i++) {
					//colName = rsMeta.getColumnName(i);
					colLabel = rsMeta.getColumnLabel(i);
					colType = rsMeta.getColumnType(i);

					if(colType == java.sql.Types.BIGINT
							|| colType == java.sql.Types.BOOLEAN
							|| colType == java.sql.Types.INTEGER 
							|| colType == java.sql.Types.SMALLINT 
							|| colType == java.sql.Types.TINYINT 
							) {
						colValue = rs.getLong(i);
						if(colValue == null) {
							colValue = 0;
						}
					} else if (colType == java.sql.Types.FLOAT
							|| colType == java.sql.Types.DOUBLE
							|| colType == java.sql.Types.DECIMAL
							|| colType == java.sql.Types.REAL
							) {
						colValue = rs.getDouble(i);
						if(colValue == null) {
							colValue = 0;
						}
					} else {
						colValue = rs.getString(i);
						if(colValue == null) {
							//log.debug("findDataXml() colVal[" + colName + "] is null");
							colValue = "";
						} else {
							//log.debug("findDataXml() colVal[" + colName + "]:" + colValue);
						}
					}
										
					XmlDataUtil.appendXmlLeafTag(xmlResult, colLabel, colValue.toString());
				}
				XmlDataUtil.appendXmlTagEnd(xmlResult, dataNodeName);
			}
			XmlDataUtil.appendXmlTagEnd(xmlResult, rootNodeName);

			return xmlResult.toString();
		} finally {
			try {
				rs.close();
			} catch(Exception e) {
			}
		}
	}

	public static String findDataJSON(PreparedStatement stmt) 
	throws SQLException, JSONException {
		ResultSet rs = null;

		try {
			rs = stmt.executeQuery();
			
			ResultSetMetaData rsMeta = rs.getMetaData();
			int colCount = rsMeta.getColumnCount();
			int i = 0;
			//String colName = null;
			String colLabel = null;
			Object colValue = null;
			int colType;

			StringWriter sw = new StringWriter();
			JSONWriter json = new JSONWriter(sw);

			json.array();
			while(rs.next()) {
				json.object();
				for(i = 1; i <= colCount; i++) {
					//colName = rsMeta.getColumnName(i);
					colLabel = rsMeta.getColumnLabel(i);
					colType = rsMeta.getColumnType(i);
					
					//column
					json.key(colLabel);
					if(colType == java.sql.Types.BIGINT
							|| colType == java.sql.Types.BOOLEAN
							|| colType == java.sql.Types.INTEGER 
							|| colType == java.sql.Types.SMALLINT 
							|| colType == java.sql.Types.TINYINT 
							) {
						colValue = rs.getLong(i);
						json.value(colValue);
					} else if (colType == java.sql.Types.FLOAT
							|| colType == java.sql.Types.DOUBLE
							|| colType == java.sql.Types.DECIMAL
							|| colType == java.sql.Types.REAL
							) {
						colValue = rs.getDouble(i);
						json.value(colValue);
					} else {
						colValue = rs.getString(i);
						if(colValue == null) {
							colValue = "";
						}
						
						json.value(colValue);
					}
						
				}
				json.endObject();
			}
			
			json.endArray();
			
			return sw.toString();
		} finally {
			try {
				rs.close();
			} catch(Exception e) {
			}
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
