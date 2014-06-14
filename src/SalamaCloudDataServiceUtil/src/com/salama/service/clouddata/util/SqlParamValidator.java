package com.salama.service.clouddata.util;

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
	
}
