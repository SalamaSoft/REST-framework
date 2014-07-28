package com.salama.service.clouddata.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * 
 * @author XingGu Liu
 *
 */
public final class SqlParamValidator {
	public static boolean isValidTableName(String tableName) {
		char c;
		for(int i = 0; i < tableName.length(); i++) {
			c = tableName.charAt(i);
			if((c >= 'a' && c <= 'z')
					|| (c >= 'A' && c <= 'Z')
					|| (c >= '0' && c <= '9')
					|| (c == '_')
					) {
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean isValidColumnName(String columnName) {
		char c;
		for(int i = 0; i < columnName.length(); i++) {
			c = columnName.charAt(i);
			if((c >= 'a' && c <= 'z')
					|| (c >= 'A' && c <= 'Z')
					|| (c >= '0' && c <= '9')
					|| (c == '_')
					) {
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	private static Object _lockOfGetIdentifierQuoteString = new Object();
	private static Class<?> _connectionClassOfLastGetIdentifierQuote = null;
	private static String _quoteStringOflastGetIdentifierQuote = null;
	
	public static String getIdentifierQuoteString(Connection conn) throws SQLException {
		if(conn.getClass() == _connectionClassOfLastGetIdentifierQuote) {
			return _quoteStringOflastGetIdentifierQuote;
		} else {
			synchronized (_lockOfGetIdentifierQuoteString) {
				DatabaseMetaData metaData = conn.getMetaData();
				
				_quoteStringOflastGetIdentifierQuote = metaData.getIdentifierQuoteString();
				_connectionClassOfLastGetIdentifierQuote = conn.getClass();
				
				return _quoteStringOflastGetIdentifierQuote;
			}
		}
	}
	
	public static String quoteSqlIdentifier(String identifierQuoteString, String name) {
		return identifierQuoteString + name + identifierQuoteString;
	}
	
}
