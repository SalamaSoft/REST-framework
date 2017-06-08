package com.salama.service.clouddata.util.dao;

import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SpringLayout.Constraints;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.salama.service.clouddata.util.SqlParamValidator;

import CollectionCommon.ITreeNode;
import MetoXML.AbstractReflectInfoCachedSerializer;
import MetoXML.Base.XmlNode;

//import com.salama.service.cloud.data.util.SqlParamValidator;

/**
 * 
 * @author XingGu Liu
 *
 */
public final class UpdateDataDao extends AbstractReflectInfoCachedSerializer {
	private static Logger logger = Logger.getLogger(UpdateDataDao.class);
	
	public static int updateData(Connection conn, Object data, String[] primaryKeys) throws SQLException {
		if(data == null) {
			return 0;
		}
		
		String tableName = data.getClass().getSimpleName();
		return updateData(conn, tableName, data, primaryKeys);
	}
	
	public static int updateData(Connection conn, String tableName, Object data, String[] primaryKeys) throws SQLException {
		return updateData(conn, tableName, data, primaryKeys, null);
	}
	
	public static int updateData(Connection conn, String tableName, Object data, String[] primaryKeys, String extraConstraintsSql) throws SQLException {
		return updateData(conn, tableName, data, data.getClass(), primaryKeys, extraConstraintsSql);
	}
	
	public static int updateData(Connection conn, String tableName, Object data, Class<?> dataClass, String[] primaryKeys, String extraConstraintsSql) throws SQLException {
		if(data == null) {
			return 0;
		} else {
			String identifierQuoteString = SqlParamValidator.getIdentifierQuoteString(conn);
			
			PreparedStatement pstmt = null;

			try {
				//valid primaryKeys
				/*
				for(int i = 0; i < primaryKeys.length; i++) {
					if(!SqlParamValidator.isValidColumnName(primaryKeys[i])) {
						throw new Exception("updateData() Invalid column name is found in primaryKeys of parameters");
					}
				}
				*/
				
				StringBuilder sql = new StringBuilder();
				
				sql.append("update ").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, tableName)).append(" set ");

				//PropertyDescriptor[] properties = Introspector.getBeanInfo(dataClass).getPropertyDescriptors();
				PropertyDescriptor[] listPropDesc = findPropertyDescriptorArray(dataClass);
				
				PropertyDescriptor property = null;
				ArrayList<PropertyDescriptor> propertyList = new ArrayList<PropertyDescriptor>();
				
				int index, i;
				
				index = 0;
				for(i = 0; i < listPropDesc.length; i++) {
					property = listPropDesc[i];
					
					if(!isPropertyHasReadWriteMethod(property)) {
						continue;
					}
					
					if(!isExistInArrayIgnoreCase(property.getName(), primaryKeys)) {
						if(index == 0) {
							sql.append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, property.getName())).append("=?");
						} else {
							sql.append(",").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, property.getName())).append("=?");
						}
						
						propertyList.add(property);
						index++;
					}
				}

				sql.append(" where ");
				
				for(index = 0; index < primaryKeys.length; index++) {
					if(index == 0) {
						sql.append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, primaryKeys[index])).append(" = ? ");
					} else {
						sql.append(" and ").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, primaryKeys[index])).append(" =? ");
					}
					
					//property = new PropertyDescriptor(primaryKeys[index], dataClass);
					property = findPropertyDescriptor(primaryKeys[index], dataClass);
					propertyList.add(property);
				}
				
				if(extraConstraintsSql != null && extraConstraintsSql.length() > 0) {
					sql.append(" and ( ").append(extraConstraintsSql).append(" )");
				}

				pstmt = conn.prepareStatement(sql.toString());

				//logger.debug("updateData sql:" + sql.toString());
				
				Object propVal = null;
				for(index = 1; index <= propertyList.size(); index++) {
					property = propertyList.get(index - 1);
					propVal = property.getReadMethod().invoke(data, (Object[])null);
					
					//logger.debug("updateData property[" + index + "] propName:" + property.getName() + " propVal:" + propVal);
					
					pstmt.setObject(index, propVal);
				}
				
			 	return pstmt.executeUpdate();
			} catch(SQLException e) {
				throw e;
			} catch(Exception e) {
				logger.error("updateData()", e);
				return 0;
			} finally {
				try {
					pstmt.close();
				} catch(Exception e) {
				}
			}
			
		}
	}

	public static int updateDataXml(Connection conn, XmlNode dataNode, String[] primaryKeys) throws SQLException {
		String tableName = dataNode.getName();
		
		return updateDataXml(conn, tableName, dataNode, primaryKeys);
	} 
	
	public static int updateDataXml(Connection conn, String tableName, XmlNode dataNode, String[] primaryKeys)
			throws SQLException {
		return updateDataXml(conn, tableName, dataNode, primaryKeys, null);
	}
	
	public static int updateDataXml(Connection conn, String tableName, XmlNode dataNode, String[] primaryKeys, String extraConstraintsSql) 
			throws SQLException {
		PreparedStatement pstmt = null;

		try {
			//valid primaryKeys
			/*
			for(int i = 0; i < primaryKeys.length; i++) {
				if(!SqlParamValidator.isValidColumnName(primaryKeys[i])) {
					throw new Exception("updateData() Invalid column name is found in primaryKeys of parameters");
				}
			}
			*/

			String identifierQuoteString = SqlParamValidator.getIdentifierQuoteString(conn);
			
			StringBuilder sql = new StringBuilder();
			sql.append("update ").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, tableName)).append(" set ");

			XmlNode nodeTmp;
			int index;
			List<XmlNode> nodeList = new ArrayList<XmlNode>();

			nodeTmp = dataNode.getFirstChildNode();
			index = 0;
			while(nodeTmp != null) {
				if(!isExistInArrayIgnoreCase(nodeTmp.getName(), primaryKeys)) {
					if(index == 0) {
						sql.append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, nodeTmp.getName())).append("=?");
					} else {
						sql.append(",").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, nodeTmp.getName())).append("=?");
					}
					
					nodeList.add(nodeTmp);

					index++;
				} 
				
				nodeTmp = nodeTmp.getNextNode();
			}

			sql.append(" where ");
			nodeTmp = dataNode.getFirstChildNode();
			index = 0;
			while(nodeTmp != null) {
				if(isExistInArrayIgnoreCase(nodeTmp.getName(), primaryKeys)) {
					if(index == 0) {
						sql.append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, nodeTmp.getName())).append("=?");
					} else {
						sql.append(" and ").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, nodeTmp.getName())).append("=?");
					}
					
					nodeList.add(nodeTmp);
					index++;
				}
				
				nodeTmp = nodeTmp.getNextNode();
			}
			
			if(extraConstraintsSql != null && extraConstraintsSql.length() > 0) {
				sql.append(" and ( ").append(extraConstraintsSql).append(" )");
			}

			pstmt = conn.prepareStatement(sql.toString());

			for(index = 1; index <= nodeList.size(); index++) {
				pstmt.setObject(index, nodeList.get(index-1).getContent());
			}
			
		 	return pstmt.executeUpdate();
		} catch(SQLException e) {
			throw e;
		} catch(Exception e) {
			logger.error("updateDataXml()", e);
			return 0;
		} finally {
			try {
				pstmt.close();
			} catch(Exception e) {
			}
		}
	}
	
	public static int updateDataJSON(Connection conn, String tableName, String dataJSON, String[] primaryKeys) 
			throws SQLException {
		return updateDataJSON(conn, tableName, dataJSON, primaryKeys, null);
	}

	public static int updateDataJSON(Connection conn, String tableName, String dataJSON, String[] primaryKeys, String extraConstraintsSql) 
			throws SQLException {
		PreparedStatement pstmt = null;

		try {
			String identifierQuoteString = SqlParamValidator.getIdentifierQuoteString(conn);
			
			StringBuilder sql = new StringBuilder();
			sql.append("update ").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, tableName)).append(" set ");

			int index, i;
			List<String> jsonKeyList = new ArrayList<String>();
			JSONObject jsonDataObject = new JSONObject(dataJSON);
			String[] colNames = JSONObject.getNames(jsonDataObject);
			String colName;

			index = 0;
			for(i = 0; i < colNames.length; i++) {
				colName = colNames[i];
				if(!isExistInArrayIgnoreCase(colName, primaryKeys)) {
					if(index == 0) {
						sql.append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, colName)).append("=?");
					} else {
						sql.append(",").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, colName)).append("=?");
					}
					
					jsonKeyList.add(colName);

					index++;
				} 
			}

			sql.append(" where ");

			for(index = 0; index < primaryKeys.length; index++) {
				if(index == 0) {
					sql.append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, primaryKeys[index])).append(" = ? ");
				} else {
					sql.append(" and ").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, primaryKeys[index])).append(" =? ");
				}
				
				jsonKeyList.add(primaryKeys[index]);
			}
			
			if(extraConstraintsSql != null && extraConstraintsSql.length() > 0) {
				sql.append(" and ( ").append(extraConstraintsSql).append(" )");
			}

			pstmt = conn.prepareStatement(sql.toString());

			String jsonKey;
			for(index = 1; index <= jsonKeyList.size(); index++) {
				jsonKey = jsonKeyList.get(index-1);
				pstmt.setObject(index, jsonDataObject.get(jsonKey));
			}
			
		 	return pstmt.executeUpdate();
		} catch(SQLException e) {
			throw e;
		} catch(Exception e) {
			logger.error("updateDataJSON()", e);
			return 0;
		} finally {
			try {
				pstmt.close();
			} catch(Exception e) {
			}
		}
	}
	
	public static int insertData(Connection conn, Object data) throws SQLException {
		if(data == null) {
			return 0;
		}
		
		String tableName = data.getClass().getSimpleName();
		return insertData(conn, tableName, data);
	}
	
	public static int insertData(Connection conn, String tableName, Object data) throws SQLException {
		return insertData(conn, tableName, data, data.getClass());
	}

	public static int insertData(Connection conn, String tableName, Object data, Class<?> dataClass) throws SQLException {
		if(data == null) {
			return 0;
		} else {
			String identifierQuoteString = SqlParamValidator.getIdentifierQuoteString(conn);
			
			PreparedStatement pstmt = null;

			try {
				//PropertyDescriptor[] properties = Introspector.getBeanInfo(dataClass).getPropertyDescriptors();
				//List<PropertyDescriptor> listPropDesc = getPropertyDescriptorList(dataClass);
				PropertyDescriptor[] listPropDesc = findPropertyDescriptorArray(dataClass);
				PropertyDescriptor property = null;
				
				int index;
				int i;
				StringBuilder sql = new StringBuilder();
				sql.append("insert into ").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, tableName)).append(" (");

				index = 0;
				for(i = 0; i < listPropDesc.length; i++) {
					property = listPropDesc[i];

					if(!isPropertyHasReadWriteMethod(property)) {
						continue;
					}
					
					if(index == 0) {
						sql.append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, property.getName()));
					} else {
						sql.append(",").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, property.getName()));
					}
					
					index++;
				}
				
				sql.append(") values (");
				
				index = 0;
				for(i = 0; i < listPropDesc.length; i++) {
					property = listPropDesc[i];

					if(!isPropertyHasReadWriteMethod(property)) {
						continue;
					}

					if(index == 0) {
						sql.append("?");
					} else {
						sql.append(",?");
					}
					
					index++;
				}

				sql.append(")");

				pstmt = conn.prepareStatement(sql.toString());

				index = 1;
				for(i = 0; i < listPropDesc.length; i++) {
					property = listPropDesc[i];

					if(!isPropertyHasReadWriteMethod(property)) {
						continue;
					}

					pstmt.setObject(index++, property.getReadMethod().invoke(data, (Object[])null));
				}
				
			 	return pstmt.executeUpdate();
			} catch(SQLException e) {
				throw e;
			} catch(Exception e) {
				logger.error("insertData()", e);
				return 0;
			} finally {
				try {
					pstmt.close();
				} catch(Exception e) {
				}
			}
			
		}
	}
	public static int insertDataXml(Connection conn, XmlNode dataNode) 
			throws SQLException {
		String tableName = dataNode.getName();
		
		return insertDataXml(conn, tableName, dataNode);
	}

	public static int insertDataXml(Connection conn, String tableName, XmlNode dataNode) 
			throws SQLException {
		String identifierQuoteString = SqlParamValidator.getIdentifierQuoteString(conn);

		PreparedStatement pstmt = null;

		try {
			//valid primaryKeys
			/*
			for(int i = 0; i < primaryKeys.length; i++) {
				if(!SqlParamValidator.isValidColumnName(primaryKeys[i])) {
					throw new Exception("updateData() Invalid column name is found in primaryKeys of parameters");
				}
			}
			*/

			StringBuilder sql = new StringBuilder();
			sql.append("insert into ").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, tableName)).append(" ( ");

			XmlNode nodeTmp;
			int index;

			nodeTmp = dataNode.getFirstChildNode();
			index = 0;
			while(nodeTmp != null) {
				if(index == 0) {
					sql.append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, nodeTmp.getName()));
				} else {
					sql.append(",").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, nodeTmp.getName()));
				}

				index++;
				
				nodeTmp = nodeTmp.getNextNode();
			}

			sql.append(" ) values ( ");
			List<XmlNode> nodeList = new ArrayList<XmlNode>();
			nodeTmp = dataNode.getFirstChildNode();
			index = 0;
			while(nodeTmp != null) {
				if(index == 0) {
					sql.append("?");
				} else {
					sql.append(", ?");
				}
				
				nodeList.add(nodeTmp);
				index++;
				
				nodeTmp = nodeTmp.getNextNode();
			}
			
			sql.append(")");
			
			pstmt = conn.prepareStatement(sql.toString());

			for(index = 1; index <= nodeList.size(); index++) {
				pstmt.setObject(index, nodeList.get(index-1).getContent());
				
				if(logger.isDebugEnabled()) {
					logger.debug("insertDataXml() pstmt[" + index + "]:" + nodeList.get(index-1).getContent());
				}
			}

			if(logger.isDebugEnabled()) {
				logger.debug("insertDataXml() sql:" + sql.toString());
			}
			
		 	return pstmt.executeUpdate();
		} catch(SQLException e) {
			throw e;
		} catch(Exception e) {
			logger.error("updateData()", e);
			return 0;
		} finally {
			try {
				pstmt.close();
			} catch(Exception e) {
			}
		}
	}

	public static int insertDataJSON(Connection conn, String tableName, String dataJSON) 
			throws SQLException {
		PreparedStatement pstmt = null;

		try {
			String identifierQuoteString = SqlParamValidator.getIdentifierQuoteString(conn);
			
			StringBuilder sql = new StringBuilder();
			sql.append("insert into ").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, tableName)).append(" ( ");

			int index, i;
			JSONObject jsonDataObject = new JSONObject(dataJSON);
			String[] colNames = JSONObject.getNames(jsonDataObject);
			String colName;

			index = 0;
			for(i = 0; i < colNames.length; i++) {
				colName = colNames[i];

				if(index == 0) {
					sql.append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, colName));
				} else {
					sql.append(",").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, colName));
				}
				
				index++;
			}

			sql.append(" ) values ( ");

			index = 0;
			for(i = 0; i < colNames.length; i++) {
				colName = colNames[i];

				if(index == 0) {
					sql.append("?");
				} else {
					sql.append(", ?");
				}
				
				index++;
			}

			sql.append(")");
			
			pstmt = conn.prepareStatement(sql.toString());

			String jsonKey;
			for(index = 1; index <= colNames.length; index++) {
				jsonKey = colNames[index-1];
				pstmt.setObject(index, jsonDataObject.get(jsonKey));
			}
			
		 	return pstmt.executeUpdate();
		} catch(SQLException e) {
			throw e;
		} catch(Exception e) {
			logger.error("insertDataJSON()", e);
			return 0;
		} finally {
			try {
				pstmt.close();
			} catch(Exception e) {
			}
		}
	}
	
	public static int deleteData(Connection conn, Object data, String[] primaryKeys) throws SQLException {
		if(data == null) {
			return 0;
		}
		
		String tableName = data.getClass().getSimpleName();
		return deleteData(conn, tableName, data, primaryKeys);
	}
	
	public static int deleteData(Connection conn, String tableName, Object data, String[] primaryKeys) throws SQLException {
		return deleteData(conn, tableName, data, primaryKeys, null);
	}
	
	public static int deleteData(Connection conn, String tableName, Object data, String[] primaryKeys, String extraConstraintsSql) throws SQLException {
		if(data == null) {
			return 0;
		} else {
			String identifierQuoteString = SqlParamValidator.getIdentifierQuoteString(conn);

			PreparedStatement pstmt = null;

			StringBuilder sql = new StringBuilder();
			try {
				//valid primaryKeys
				/*
				for(int i = 0; i < primaryKeys.length; i++) {
					if(!SqlParamValidator.isValidColumnName(primaryKeys[i])) {
						throw new Exception("updateData() Invalid column name is found in primaryKeys of parameters");
					}
				}
				*/
				
				Class<?> dataClass = data.getClass();
				
				sql.append("delete from ").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, tableName)).append(" where ");

				PropertyDescriptor property = null;
				ArrayList<PropertyDescriptor> propertyList = new ArrayList<PropertyDescriptor>();
				
				int index;
				for(index = 0; index < primaryKeys.length; index++) {
					if(index == 0) {
						sql.append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, primaryKeys[index])).append(" = ? ");
					} else {
						sql.append(" and ").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, primaryKeys[index])).append(" =? ");
					}
					
					//property = new PropertyDescriptor(primaryKeys[index], dataClass);
					property = findPropertyDescriptor(primaryKeys[index], dataClass);
					propertyList.add(property);
				}

				if(extraConstraintsSql != null && extraConstraintsSql.length() > 0) {
					sql.append(" and ( ").append(extraConstraintsSql).append(" )");
				}
				
				pstmt = conn.prepareStatement(sql.toString());

				for(index = 1; index <= propertyList.size(); index++) {
					property = propertyList.get(index - 1);
					pstmt.setObject(index, property.getReadMethod().invoke(data, (Object[])null));
				}
				
			 	return pstmt.executeUpdate();
			} catch(SQLException e) {
				logger.error("updateData() sql:" + sql.toString(), e);
				throw e;
			} catch(Exception e) {
				logger.error("updateData() sql:" + sql.toString(), e);
				return 0;
			} finally {
				try {
					pstmt.close();
				} catch(Exception e) {
				}
			}
			
		}
	}

	public static int deleteDataXml(Connection conn, String tableName, XmlNode dataNode, String[] primaryKeys) 
			throws SQLException {
		return deleteDataXml(conn, tableName, dataNode, primaryKeys, null);
	}
	
	public static int deleteDataXml(Connection conn, String tableName, XmlNode dataNode, String[] primaryKeys, String extraConstraintsSql) 
			throws SQLException {
		String identifierQuoteString = SqlParamValidator.getIdentifierQuoteString(conn);

		PreparedStatement pstmt = null;

		try {
			StringBuilder sql = new StringBuilder();
			sql.append("delete from ").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, tableName)).append(" where ");

			XmlNode nodeTmp;
			int index;
			List<XmlNode> nodeList = new ArrayList<XmlNode>();

			nodeTmp = dataNode.getFirstChildNode();
			index = 0;
			while(nodeTmp != null) {
				if(isExistInArrayIgnoreCase(nodeTmp.getName(), primaryKeys)) {
					if(index == 0) {
						sql.append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, nodeTmp.getName())).append("=?");
					} else {
						sql.append(" and ").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, nodeTmp.getName())).append("=?");
					}
					
					nodeList.add(nodeTmp);
					index++;
				}
				
				nodeTmp = nodeTmp.getNextNode();
			}
			
			if(extraConstraintsSql != null && extraConstraintsSql.length() > 0) {
				sql.append(" and ( ").append(extraConstraintsSql).append(" )");
			}

			pstmt = conn.prepareStatement(sql.toString());

			for(index = 1; index <= nodeList.size(); index++) {
				pstmt.setObject(index, nodeList.get(index-1).getContent());
			}
			
		 	return pstmt.executeUpdate();
		} catch(SQLException e) {
			throw e;
		} catch(Exception e) {
			logger.error("deleteDataXml()", e);
			return 0;
		} finally {
			try {
				pstmt.close();
			} catch(Exception e) {
			}
		}
	}

	public static int deleteDataJSON(Connection conn, String tableName, String dataJSON, String[] primaryKeys) 
			throws SQLException {
		return deleteDataJSON(conn, tableName, dataJSON, primaryKeys, null);
	}
	
	public static int deleteDataJSON(Connection conn, String tableName, String dataJSON, String[] primaryKeys, String extraConstraintsSql) 
			throws SQLException {
		PreparedStatement pstmt = null;

		try {
			String identifierQuoteString = SqlParamValidator.getIdentifierQuoteString(conn);
			
			StringBuilder sql = new StringBuilder();
			sql.append("delete from ").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, tableName)).append(" where ");

			int index;
			JSONObject jsonDataObject = new JSONObject(dataJSON);
			String[] colNames = JSONObject.getNames(jsonDataObject);

			for(index = 0; index < primaryKeys.length; index++) {
				if(index == 0) {
					sql.append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, primaryKeys[index])).append(" = ? ");
				} else {
					sql.append(" and ").append(SqlParamValidator.quoteSqlIdentifier(identifierQuoteString, primaryKeys[index])).append(" = ? ");
				}
			}

			if(extraConstraintsSql != null && extraConstraintsSql.length() > 0) {
				sql.append(" and ( ").append(extraConstraintsSql).append(" )");
			}
			
			pstmt = conn.prepareStatement(sql.toString());

			String jsonKey;
			for(index = 1; index <= colNames.length; index++) {
				jsonKey = colNames[index-1];
				pstmt.setObject(index, jsonDataObject.get(jsonKey));
			}
			
		 	return pstmt.executeUpdate();
		} catch(SQLException e) {
			throw e;
		} catch(Exception e) {
			logger.error("insertDataJSON()", e);
			return 0;
		} finally {
			try {
				pstmt.close();
			} catch(Exception e) {
			}
		}
	}
	
	protected static boolean isExistInArrayIgnoreCase(String str, String[] strArray) {
		for(int i = 0; i < strArray.length; i++) {
			if(str.equalsIgnoreCase(strArray[i])) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	protected void BackwardToNode(ITreeNode arg0, int arg1) {
		// Nothing to do
	}

	@Override
	protected void ForwardToNode(ITreeNode arg0, int arg1, boolean arg2) {
		// Nothing to do
	}
	
	/*
	protected static List<PropertyDescriptor> getPropertyDescriptorList(Class<?> dataClass) throws IntrospectionException {
		PropertyDescriptor[] properties = Introspector.getBeanInfo(dataClass).getPropertyDescriptors();
		
		List<PropertyDescriptor> listProp = new ArrayList<PropertyDescriptor>();
		
		PropertyDescriptor propDesc = null;
		for(int  i = 0; i < properties.length; i++) {
			propDesc = properties[i];
			if(propDesc.getReadMethod() != null && propDesc.getWriteMethod() != null) {
				listProp.add(propDesc);
			}
		}
		
		return listProp;
	}
	*/
	protected static boolean isPropertyHasReadWriteMethod(PropertyDescriptor propDesc) {
		return (propDesc.getReadMethod() != null && propDesc.getWriteMethod() != null);
	}
}
