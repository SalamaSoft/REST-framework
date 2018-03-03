package com.salama.service.clouddata.defaultsupport;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import MetoXML.XmlDeserializer;
import MetoXML.XmlReader;
import MetoXML.Base.XmlNode;

import com.salama.service.clouddata.CloudDataService;
import com.salama.service.clouddata.util.dao.UpdateDataDao;
import com.salama.service.core.net.RequestWrapper;

/**
 * @deprecated 
 * @author XingGu Liu
 *
 */
/*
public class UpdateDataService {
	private static Logger logger = Logger.getLogger(UpdateDataService.class);
	
	public static final String PARAM_NAME_METHOD = "method";

	public static final String PARAM_NAME_DATA_TYPE = "dataType";
	public static final String PARAM_NAME_DATA = "data";
	
	
	public static final String PARAM_VALUE_DATA_TYPE_XML = "xml";
	public static final String PARAM_VALUE_DATA_TYPE_JSON = "json";
	
	@Override
	public String handleRequest(RequestWrapper request, Connection conn) {
		String method = request.getParameter(PARAM_NAME_METHOD);
		String dataType = request.getParameter(PARAM_NAME_DATA_TYPE);
		String data = request.getParameter(PARAM_NAME_DATA);
		String tableName = request.getParameter("tableName");
		
		try {
			if("updateData".equals(method)) {
				return updateData(conn, dataType, tableName, data);
			} else if ("insertData".equals(method)) {
				return insertData(conn, dataType, tableName, data);
			} else if("deleteData".equals(method)) {
				return deleteData(conn, dataType, tableName, data);
			} else if("insertOrUpdateData".equals(method)) {
				return insertOrUpdateData(conn, dataType, tableName, data);
			} else {
				return null;
			} 
		} catch(SQLException e) {
			logger.error("handleRequest() method:" + method, e);
			return null;
		}
	}

	public static String updateData(Connection conn, String dataType, String tableName, String data) throws SQLException {
		String[] primaryKeys = getPrimaryKeyArray(conn, tableName);
		
		if(PARAM_VALUE_DATA_TYPE_JSON.equals(dataType)) {
			return Integer.toString(UpdateDataDao.updateDataJSON(conn, tableName, data, primaryKeys));
		} else {
			try {
				XmlReader xmlReader = new XmlReader();
				XmlNode dataNode = xmlReader.StringToXmlNode(data, XmlDeserializer.DefaultCharset);
				return Integer.toString(UpdateDataDao.updateDataXml(conn, tableName, dataNode, primaryKeys));
			} catch(Exception e) {
				logger.error("updateData()", e);
				return null;
			}
		}
	}

	public static String insertData(Connection conn, String dataType, String tableName, String data) throws SQLException {
		if(PARAM_VALUE_DATA_TYPE_JSON.equals(dataType)) {
			return Integer.toString(UpdateDataDao.insertDataJSON(conn, tableName, data));
		} else {
			try {
				XmlReader xmlReader = new XmlReader();
				XmlNode dataNode = xmlReader.StringToXmlNode(data, XmlDeserializer.DefaultCharset);
				return Integer.toString(UpdateDataDao.insertDataXml(conn, tableName, dataNode));
			} catch(Exception e) {
				logger.error("insertData()", e);
				return null;
			}
		}
	}
	
	public static String deleteData(Connection conn, String dataType, String tableName, String data) throws SQLException {
		String[] primaryKeys = getPrimaryKeyArray(conn, tableName);

		if(PARAM_VALUE_DATA_TYPE_JSON.equals(dataType)) {
			return Integer.toString(UpdateDataDao.deleteDataJSON(conn, tableName, data, primaryKeys));
		} else {
			try {
				XmlReader xmlReader = new XmlReader();
				XmlNode dataNode = xmlReader.StringToXmlNode(data, XmlDeserializer.DefaultCharset);
				return Integer.toString(UpdateDataDao.deleteDataXml(conn, tableName, dataNode, primaryKeys));
			} catch(Exception e) {
				logger.error("insertData()", e);
				return null;
			}
		}
	}
		
	public static String insertOrUpdateData(Connection conn, String dataType, String tableName, String data) throws SQLException {
		if(PARAM_VALUE_DATA_TYPE_JSON.equals(dataType)) {
			try {
				return Integer.toString(UpdateDataDao.insertDataJSON(conn, tableName, data));
			} catch(SQLException e) {
				String[] primaryKeys = getPrimaryKeyArray(conn, tableName);
				return Integer.toString(UpdateDataDao.updateDataJSON(conn, tableName, data, primaryKeys));
			}
		} else {
			try {
				XmlReader xmlReader = new XmlReader();
				XmlNode dataNode = xmlReader.StringToXmlNode(data, XmlDeserializer.DefaultCharset);

				try {
					return Integer.toString(UpdateDataDao.insertDataXml(conn, tableName, dataNode));
				} catch(SQLException e) {
					String[] primaryKeys = getPrimaryKeyArray(conn, tableName);
					return Integer.toString(UpdateDataDao.updateDataXml(conn, tableName, dataNode, primaryKeys));
				}
			} catch(Exception e) {
				logger.error("insertOrUpdateData()", e);
				return null;
			}
		}
	}
	
	protected static List<String> getPrimaryKeys(Connection conn, String tableName) throws SQLException {
		ResultSet rs = null;
		
		try {
			DatabaseMetaData meta = conn.getMetaData();
			rs = meta.getPrimaryKeys(null, null, tableName.toUpperCase());
			List<String> pkList = new ArrayList<String>();

			while(rs.next()) {
				pkList.add(rs.getString("COLUMN_NAME"));
			}
			
			return pkList;
		} finally {
			try {
				rs.close();
			} catch(Exception e) {
			}
		}
	}

	protected static String[] getPrimaryKeyArray(Connection conn, String tableName) throws SQLException {
		List<String> pkList = getPrimaryKeys(conn, tableName);
		
		String[] pks = new String[pkList.size()];
		for(int i = 0; i < pkList.size(); i++) {
			pks[i] = pkList.get(i);
		}
		
		return pks;
	}
}
*/
