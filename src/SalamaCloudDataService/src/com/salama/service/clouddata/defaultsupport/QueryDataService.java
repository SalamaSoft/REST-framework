package com.salama.service.clouddata.defaultsupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.json.JSONException;

import com.salama.service.clouddata.CloudDataService;
import com.salama.service.clouddata.core.AppContext;
import com.salama.service.clouddata.core.AppException;
import com.salama.service.clouddata.util.dao.QueryDataDao;
import com.salama.service.clouddata.util.SqlParamValidator;
import com.salama.service.core.net.RequestWrapper;
import com.salama.service.core.net.ResponseWrapper;

/**
 * 
 * @author XingGu Liu
 *
 */
public class QueryDataService {
	/*
	private static Logger logger = Logger.getLogger(QueryDataService.class);

	public static final String PARAM_NAME_METHOD = "method";

	public static final String PARAM_NAME_RESPONSE_TYPE = "responseType";
	
	public static final String PARAM_VALUE_RESPONSE_TYPE_XML = "xml";
	public static final String PARAM_VALUE_RESPONSE_TYPE_JSON = "json";

	public final static String PARAM_NAME_ROOT_NODE_NAME = "rootDataName";
	public final static String PARAM_NAME_DATA_NODE_NAME = "dataNodeName";

	public final static String PARAM_NAME_SQL = "sql";

	public String handleRequest(RequestWrapper request, ResponseWrapper response, AppContext appContext) {
		try {
			if("executeQuery".equals(method)) {
			} else if ("findAllData".equals(method)) {
				String tableName = request.getParameter("tableName");
				
				return findAllData(conn, responseType, tableName);
			} else if("findDataAfterUpdateTime".equals(method)) {
				String tableName = request.getParameter("tableName");
				String updateTime = request.getParameter("updateTime");
				
				return findDataAfterUpdateTime(conn, responseType, tableName, Long.parseLong(updateTime));
			} else {
				return null;
			} 
		} catch(SQLException e) {
			logger.error("handleRequest() method:" + method, e);
			return null;
		}
	}

	public static String executeQuery(RequestWrapper request, ResponseWrapper response) {
		String sql = request.getParameter(PARAM_NAME_SQL);
		String rootNodeName = request.getParameter(PARAM_NAME_ROOT_NODE_NAME);
		String dataNodeName = request.getParameter(PARAM_NAME_DATA_NODE_NAME);
		
		if(rootNodeName == null) {
			rootNodeName = "List";
		}
		
		if(dataNodeName == null) {
			dataNodeName = "Data";
		}

		String responseType = request.getParameter(PARAM_NAME_RESPONSE_TYPE);
		
		Connection conn = null;
		
		try {
			conn = appContext.getDBConnection();
			return executeQuery(conn, responseType, sql, rootNodeName, dataNodeName);
		} catch (Exception e) {
			logger.error("executeQuery()", e);
			return null;
		} finally {
			try {
				conn.close();
			} catch(Exception e) {
			}
		}
	}
	
	public static String executeQuery(Connection conn, String responseType, 
			String sql, String rootNodeName, String dataNodeName) 
			throws SQLException {
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(sql);
	
			if(PARAM_VALUE_RESPONSE_TYPE_JSON.equals(responseType)) {
				try {
					return QueryDataDao.findDataJSON(stmt, dataNodeName);
				} catch(JSONException e) {
					logger.error("executeQuery()", e);
					return null;
				}
			} else {
				return QueryDataDao.findDataXml(stmt, rootNodeName, dataNodeName);
			}
		} finally {
			try {
				stmt.close();
			} catch(Exception e) {
			}
		}
	}
	
	public static String findAllData(Connection conn, String responseType, 
			String tableName) throws SQLException {
		if(!SqlParamValidator.isValidTableName(tableName)) {
			logger.error("findAllData() Invalid tableName.");
			return null;
		}
		
		PreparedStatement pstmt = null;

		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select * from ").append(tableName);

			pstmt = conn.prepareStatement(sql.toString());
			
			if(PARAM_VALUE_RESPONSE_TYPE_JSON.equals(responseType)) {
				try {
					return QueryDataDao.findDataJSON(pstmt, tableName);
				} catch(JSONException e) {
					logger.error("findAllData()", e);
					return null;
				}
			} else {
				return QueryDataDao.findDataXml(pstmt, "List", tableName);
			}
		} finally {
			try {
				pstmt.close();
			} catch(Exception e) {
			}
		}
	}

	public static String findDataAfterUpdateTime(Connection conn, String responseType, 
			String tableName, long updateTime) throws SQLException {
		if(!SqlParamValidator.isValidTableName(tableName)) {
			logger.error("findAllData() Invalid tableName.");
			return null;
		}
		
		PreparedStatement pstmt = null;

		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select * from ").append(tableName).append(" where updateTime >= ? order by updateTime desc");

			pstmt = conn.prepareStatement(sql.toString());
			
			pstmt.setLong(1, updateTime);
			
			if(PARAM_VALUE_RESPONSE_TYPE_JSON.equals(responseType)) {
				try {
					return QueryDataDao.findDataJSON(pstmt, tableName);
				} catch(JSONException e) {
					logger.error("findAllData()", e);
					return null;
				}
			} else {
				return QueryDataDao.findDataXml(pstmt, "List", tableName);
			}
		} finally {
			try {
				pstmt.close();
			} catch(Exception e) {
			}
		}
	}
	*/
}
