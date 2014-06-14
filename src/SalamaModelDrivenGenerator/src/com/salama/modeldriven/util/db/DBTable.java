package com.salama.modeldriven.util.db;

import java.util.ArrayList;
import java.util.List;

public class DBTable {
    private String _tableName = "";

    private String _comment = "";

    private List<DBColumn> _uniqueIndex = new ArrayList<DBColumn>();

    private List<DBColumn> _columns = new ArrayList<DBColumn>();

	public String getTableName() {
		return _tableName;
	}

	public void setTableName(String tableName) {
		_tableName = tableName;
	}

	public String getComment() {
		return _comment;
	}

	public void setComment(String comment) {
		_comment = comment;
	}

	public List<DBColumn> getUniqueIndex() {
		return _uniqueIndex;
	}

	public void setUniqueIndex(List<DBColumn> uniqueIndex) {
		_uniqueIndex = uniqueIndex;
	}

	public List<DBColumn> getColumns() {
		return _columns;
	}

	public void setColumns(List<DBColumn> columns) {
		_columns = columns;
	}

    
}
