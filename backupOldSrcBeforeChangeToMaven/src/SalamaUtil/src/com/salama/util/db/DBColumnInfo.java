package com.salama.util.db;

import java.sql.ParameterMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public final class DBColumnInfo implements Comparable<DBColumnInfo> {
	
	
	private String fSchemaName;
	private String fTableName;
	private int fPosition;
	private String fName;
	private int fType;
	private String fTypeName;
	private int fSize;
	private int fScale;
	private int fNullable;
	private int fPrimaryKeySeq = 0;

	public DBColumnInfo(ResultSet rs) throws SQLException {
		fSchemaName = rs.getString("TABLE_SCHEM");
		fTableName = rs.getString("TABLE_NAME");
		fPosition = rs.getInt("ORDINAL_POSITION");
		fName = rs.getString("COLUMN_NAME");
		fType = rs.getInt("DATA_TYPE");
		fTypeName = rs.getString("TYPE_NAME");
		fSize = rs.getInt("COLUMN_SIZE");
		fScale = rs.getInt("DECIMAL_DIGITS");
		fNullable = rs.getInt("NULLABLE");
	}

	public DBColumnInfo(ResultSetMetaData meta, int columnIndex)
			throws SQLException {
		fSchemaName = meta.getSchemaName(columnIndex);
		fTableName = meta.getTableName(columnIndex);
		fPosition = columnIndex;
		fName = meta.getColumnName(columnIndex);
		fType = meta.getColumnType(columnIndex);
		fTypeName = meta.getColumnTypeName(columnIndex);
		fSize = meta.getPrecision(columnIndex);
		fScale = meta.getScale(columnIndex);
		fNullable = meta.isNullable(columnIndex);
	}

	public DBColumnInfo(ParameterMetaData meta, int parameterIndex)
			throws SQLException {
		fSchemaName = null;
		fTableName = null;
		fPosition = parameterIndex;
		fName = null;
		fType = meta.getParameterType(parameterIndex);
		fTypeName = meta.getParameterTypeName(parameterIndex);
		fSize = meta.getPrecision(parameterIndex);
		fScale = meta.getScale(parameterIndex);
		fNullable = meta.isNullable(parameterIndex);
	}

	public int compareTo(DBColumnInfo o) {
		int w = ((DBColumnInfo) o).getPosition();
		return fPosition - w;
	}

	public boolean equals(Object obj) {
		int w = ((DBColumnInfo) obj).getPosition();
		return fPosition == w;
	}

	public int hashCode() {
		return fPosition;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		if (getSchemaName() != null && getSchemaName().length() > 0) {
			buf.append(getSchemaName());
			buf.append('.');
		}
		if (getTableName() != null && getTableName().length() > 0) {
			buf.append(getTableName());
			buf.append('.');
		}
		buf.append(getName());
		buf.append(':');
		buf.append(getTypeName());
		buf.append('(');
		buf.append(getSize());
		if (getScale() != 0) {
			buf.append(',');
			buf.append(getScale());
		}
		buf.append(')');
		return buf.toString();
	}

	public String getSchemaName() {
		return fSchemaName;
	}

	public String getTableName() {
		return fTableName;
	}

	public int getPosition() {
		return fPosition;
	}

	public String getName() {
		return fName;
	}

	public int getType() {
		return fType;
	}

	public String getTypeName() {
		return fTypeName;
	}

	public int getSize() {
		return fSize;
	}

	public int getScale() {
		return fScale;
	}

	public int getNullable() {
		return fNullable;
	}

	public int getPrimaryKeySeq() {
		return fPrimaryKeySeq;
	}

	public void setPrimaryKeySeq(int i) {
		fPrimaryKeySeq = i;
	}
}