package com.salama.modeldriven.util.db;

public class DBColumn {
    private String _name = "";

    private String _columnType = "";

    private boolean _isNullable = true;

    private boolean _isPrimaryKey = false;

    private String _default = "";

    /**
     * if extra == "auto_increment", the column is an auto increment column.
     */
    private String _extra = "";

    private String _comment = "";

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getColumnType() {
		return _columnType;
	}

	public void setColumnType(String columnType) {
		_columnType = columnType;
	}

	public boolean isNullable() {
		return _isNullable;
	}

	public void setNullable(boolean isNullable) {
		_isNullable = isNullable;
	}

	public boolean isPrimaryKey() {
		return _isPrimaryKey;
	}

	public void setPrimaryKey(boolean isPrimaryKey) {
		_isPrimaryKey = isPrimaryKey;
	}

	public String getDefault() {
		return _default;
	}

	public void setDefault(String default1) {
		_default = default1;
	}

	public String getExtra() {
		return _extra;
	}

	public void setExtra(String extra) {
		_extra = extra;
	}

	public String getComment() {
		return _comment;
	}

	public void setComment(String comment) {
		_comment = comment;
	}
    
    
}
