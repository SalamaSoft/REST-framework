package com.salama.util.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.salama.util.JndiFactory;

public class DBUtil {
	/** Log */
	private static final Logger log = Logger.getLogger(DBUtil.class);

	private DBUtil() {
	}

	public static void commit(Connection conn) throws SQLException {
		if (conn == null) {
			return;
		}
		if (conn.getAutoCommit()) {
			return;
		}
		conn.commit();
	}

	public static void rollback(Connection conn) throws SQLException {
		if (conn == null) {
			return;
		}
		if (conn.getAutoCommit()) {
			return;
		}
		conn.rollback();
	}

	public static Connection getConnection(String inJndiName)
			throws NamingException, SQLException {
		JndiFactory factory = JndiFactory.getInstance();
		DataSource ds = factory.getDataSource(inJndiName);
		Connection conn = ds.getConnection();
		conn.setAutoCommit(false);
		log.debug("getAutoCommit()=" + conn.getAutoCommit());
		return conn;
	}

	public static void closeConnection(Connection conn)
			throws SQLException {
		log.debug(">>closeConection()");
		if (conn == null) {
			log.debug("Connection is null");
			return;
		}
		conn.close();
	}

	public static void closeStatement(Statement stmt) throws SQLException {
		log.debug(">>closeStatement()");
		if (stmt == null) {
			log.debug("Statement is null");
			return;
		}
		stmt.close();
	}

	public static void closeResultSet(ResultSet rs) throws SQLException {
		log.debug(">>closeResultSet()");
		if (rs == null) {
			log.debug("ResultSet is null");
			return;
		}
		rs.close();
	}

	public static List<DBColumnInfo> getColumnInfoList(ResultSet rs)
			throws SQLException {
		log.debug(">>getColumnInfoList(ResultSet)");
		ResultSetMetaData meta = rs.getMetaData();
		List<DBColumnInfo> list = new ArrayList<DBColumnInfo>();
		int columnCount = meta.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			list.add(new DBColumnInfo(meta, i + 1));
		}
		return list;
	}

	public static List<DBColumnInfo> getColumnInfoList(Connection conn,
			String tableName) throws SQLException {
		try {
			return getColumnInfoListImp(conn, tableName);
		} catch(SQLException e) {
			try {
				return getColumnInfoListImp(conn, tableName.toUpperCase());
			} catch(SQLException e1) {
				return getColumnInfoListImp(conn, tableName.toLowerCase());
			}
		}
	}
	
	private static List<DBColumnInfo> getColumnInfoListImp(Connection conn,
			String tableName) throws SQLException {
		log.debug(">>getColumnInfoListImp(Connection) tableName=" + tableName);
		List<DBColumnInfo> list = new ArrayList<DBColumnInfo>();
		
		ResultSet rs = null;
		DatabaseMetaData meta = conn.getMetaData();
		
		try {
			rs = meta.getColumns(null, null, tableName, "%");
			while (rs.next()) {
				list.add(new DBColumnInfo(rs));
			}
		} finally {
			rs.close();
		}

		try {
			rs = meta.getPrimaryKeys(null, null, tableName);
			while (rs.next()) {
				String columnName = rs.getString("COLUMN_NAME");
				int keySeq = rs.getInt("KEY_SEQ");
				for (int i = 0; i < list.size(); i++) {
					DBColumnInfo info = (DBColumnInfo) list.get(i);
					if (columnName.equals(info.getName())) {
						info.setPrimaryKeySeq(keySeq);
						break;
					}
				}
			}
		} finally {
			rs.close();
		}

		Collections.sort(list);

		return list;
	}

	public static List<DBColumnInfo> getColumnInfoList(String jndiName,
			String tableName) throws SQLException, NamingException {
		log.debug(">>getColumnInfoList(JNDI) tableName=" + tableName);
		Connection conn = null;
		try {
			conn = getConnection(jndiName);
			List<DBColumnInfo> list = getColumnInfoList(conn, tableName);
			commit(conn);
			return list;
		} catch (SQLException e) {
			rollback(conn);
			throw e;
		} finally {
			closeConnection(conn);
		}
	}

	public static String replaceQuote(String strFieldValue) {
		if (strFieldValue == null) {
			return null;
		} else {
			return strFieldValue.replace("'", "''");
		}
	}
}
