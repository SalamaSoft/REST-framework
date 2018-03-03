package com.salama.util.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class DBMetaUtil {
	public static String getOnly1PrimaryKey(Connection conn, String tableName) throws SQLException {
		ResultSet rs = null;
		
		try {
			String pkName = null;
			
			//get pk name
			DatabaseMetaData dbMeta = conn.getMetaData();
			rs = dbMeta.getPrimaryKeys(null, null, tableName.toUpperCase());
			
			if(rs.next()) {
				pkName = rs.getString("COLUMN_NAME");
			}
			
			return pkName;
		} finally {
			try {
				rs.close();
			} catch(Exception e) {
			}
		}
		
	}
	
	public static List<String> getPrimaryKeys(Connection conn, String tableName) throws SQLException {
		ResultSet rs = null;
		
		try {
			List<String> pkNames = new ArrayList<String>();
			
			//get pk name
			DatabaseMetaData dbMeta = conn.getMetaData();
			rs = dbMeta.getPrimaryKeys(null, null, tableName.toUpperCase());
			
			while(rs.next()) {
				pkNames.add(rs.getString("COLUMN_NAME"));
			}
			
			return pkNames;
		} finally {
			try {
				rs.close();
			} catch(Exception e) {
			}
		}
		
	}

}
