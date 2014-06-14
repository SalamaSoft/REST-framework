package com.salama.modeldriven.util.db.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.salama.modeldriven.util.db.DBColumn;
import com.salama.modeldriven.util.db.DBTable;
import com.salama.util.StringUtil;

public class MysqlTableInfoUtil {
	private final static String SQL_SHOW_TABLES = "show tables";
	
    private final static String SQL_GET_TABLE_COMMENT = 
    		"SELECT TABLE_COMMENT " +
    		"FROM information_schema.tables " +
    		"WHERE upper(table_name)=upper('{0}')";
    
    private final static String SQL_GET_COLUMNS_INFO = 
    		"SELECT COLUMN_NAME, COLUMN_DEFAULT, IS_NULLABLE, COLUMN_TYPE, COLUMN_KEY, COLUMN_COMMENT, EXTRA " +
    		"FROM information_schema.columns " +
    		"WHERE upper(table_name)=upper('{0}') order by ORDINAL_POSITION";
    
    public static List<String> getAllTables(Connection conn) throws SQLException {
    	List<String> tableNameList = new ArrayList<String>();
    	Statement stmt = null;
    	ResultSet rs = null;
    	
    	try {
        	stmt = conn.createStatement();
        	rs = stmt.executeQuery(SQL_SHOW_TABLES);
        	
        	while(rs.next()) {
        		tableNameList.add(rs.getString(0));
        	}

        	return tableNameList;
    	} finally {
    		try {
        		stmt.close();
    		} catch(SQLException e) {
    		}
    	}
    }
    
    public static DBTable GetTable(Connection conn, String tableName) throws SQLException {
        DBTable table = new DBTable();
        table.setTableName(tableName);

    	String sql = "";
        Statement stmt = null;
    	ResultSet rs = null;
        
    	//Get comment
    	try {
            sql = StringUtil.formatString(SQL_GET_TABLE_COMMENT, tableName);
        	stmt = conn.createStatement();
        	rs = stmt.executeQuery(sql);
        	
        	if(rs.next()) {
        		table.setComment(rs.getString("TABLE_COMMENT"));
        	}
    	} finally {
    		try {
        		stmt.close();
    		} catch(SQLException e) {
    		}
    	}

    	//Get column info
    	try {
            sql = StringUtil.formatString(SQL_GET_COLUMNS_INFO, tableName);
        	stmt = conn.createStatement();
        	rs = stmt.executeQuery(sql);
        	
        	Object defaultValue = null;
        	String isNullable = "";
        	String colKey = "";
        	
        	while(rs.next()) {
                DBColumn col = new DBColumn();

                col.setName(rs.getString("COLUMN_NAME"));
                col.setColumnType(rs.getString("COLUMN_TYPE"));
                
                isNullable = rs.getString("IS_NULLABLE"); 
                if (isNullable.toUpperCase().equals("YES"))
                {
                    col.setNullable(true);
                }
                else
                { 
                    col.setNullable(false);
                }

                colKey = rs.getString("COLUMN_KEY");
                if (colKey.toUpperCase().equals("PRI"))
                {
                    col.setPrimaryKey(true);
                }
                else
                {
                	col.setPrimaryKey(false);
                }


                defaultValue = rs.getObject("COLUMN_DEFAULT");
                if ((defaultValue != null) && !defaultValue.toString().toUpperCase().equals("NULL"))
                {
                    col.setDefault(defaultValue.toString());
                }

                col.setExtra(rs.getString("EXTRA"));
                col.setComment(rs.getString("COLUMN_COMMENT"));

                table.getColumns().add(col);
        	}
        	
        	return table;
    	} finally {
    		try {
        		stmt.close();
    		} catch(SQLException e) {
    		}
    	}
    	
    }

}
