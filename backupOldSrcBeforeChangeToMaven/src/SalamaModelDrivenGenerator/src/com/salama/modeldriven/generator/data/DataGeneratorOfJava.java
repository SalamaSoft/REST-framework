package com.salama.modeldriven.generator.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.salama.util.db.DBColumnInfo;
import com.salama.util.db.DBUtil;

/**
 * 
 * @author XingGu Liu
 *
 */
public class DataGeneratorOfJava {
	private final static Logger logger = Logger.getLogger(DataGeneratorOfJava.class);
	
//	public static void generateAllDataFromDB(Connection conn, String namespace, String saveDirPath) {
//	}
//	
//	public static void generateOneData()
	
	protected final static String TEMPLATE_FILE = "JavaData.vm";
	protected final static String TEMPLATE_DIR = "/com/salama/modeldriven/generator/data/template/";

	public static void createAllTableData(Connection conn, File fileSaveDir, String namespace) throws ResourceNotFoundException, ParseErrorException, Exception {
		List<String> tableNameList = getAllTableName(conn);
		
		File templateFile = new File(fileSaveDir, TEMPLATE_FILE);  
		
		//Copy template file to saveDir
		copyTemplateFileTo(templateFile);

		String templateDirPath = fileSaveDir.getPath();
		if(!templateDirPath.endsWith(File.separator)) {
			templateDirPath = templateDirPath.concat(File.separator);
		}
		
		logger.debug("templateDirPath:" + templateDirPath);
		
		try {
			
			Properties velocityProperties = new Properties();
			//velocityProperties.setProperty("file.resource.loader.path", templateDir.getPath());
			velocityProperties.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, templateDirPath);
			String velocityLogPath = templateDirPath + "velocity_generator.log";
			velocityProperties.setProperty("runtime.log", velocityLogPath);
			velocityProperties.setProperty("input.encoding", "utf-8");
			velocityProperties.setProperty("output.encoding", "utf-8");
			
			VelocityEngine vEngine = new VelocityEngine();
			vEngine.init(velocityProperties);

			for(String tableName : tableNameList) {
				createTableData(conn, vEngine,  tableName, fileSaveDir, templateDirPath, 
						TEMPLATE_FILE, namespace);
			}
			
			//vEngine.clearProperty("runtime.log");
		} finally {
			templateFile.delete();
		}
		
	}
	
	protected static void copyTemplateFileTo(File templateFile) throws IOException {
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			is = DataGeneratorOfObjectiveC.class.getResourceAsStream(TEMPLATE_DIR + TEMPLATE_FILE);
			fos = new FileOutputStream(templateFile);
			copyFile(is, fos);
			
		} finally {
			try {
				is.close();
			} catch(IOException e) {
			} 

			try {
				fos.close();
			} catch(IOException e) {
			} 
		}
		
	}
	
	protected static void copyFile(InputStream src, OutputStream dest) throws IOException {
		byte[] tempBuff = new byte[256];
		int readCnt = 0;
		
		while(true) {
			readCnt = src.read(tempBuff, 0, tempBuff.length);
			
			if(readCnt <= 0) {
				break;
			}
			
			dest.write(tempBuff, 0, readCnt);
		}
		
		dest.flush();
	}
	
	public static void createTableData(
			Connection conn, VelocityEngine vEngine, 
			String tableName, 
			File fileSaveDir,
			String templateDirPath,
			String templateFileName, String namespace) throws Exception {
		
		List<DBColumnInfo> dbColInfoList;
		dbColInfoList = DBUtil.getColumnInfoList(conn, tableName);
		
		List<JavaProperty> propertyList = new ArrayList<JavaProperty>();
		
		JavaProperty property = null;
		String colName;
		for(DBColumnInfo dbColInfo : dbColInfoList) {
			try {
				colName = dbColInfo.getName();
				
				property = new JavaProperty();
				property.setName(colName.substring(0, 1).toLowerCase() + colName.substring(1));
				property.setNameWithUpperCasePrefix(colName.substring(0, 1).toUpperCase() + colName.substring(1));
				property.setType(getTypeOfSqlType(dbColInfo.getType()));
				property.setDefaultValue(getDefaultValueOfSqlType(dbColInfo.getType()));

				propertyList.add(property);
			} catch(RuntimeException e) {
				throw new RuntimeException("Not support this type:" + dbColInfo.getType() + " table:" + tableName + " col:" + dbColInfo.getName() );
			}
		}
	
		File targetFile = new File(fileSaveDir, tableName + ".java");
		
		VelocityContext context = new VelocityContext();
		context.put("namespace", namespace);
		context.put("dataName", tableName);
		context.put("propertyList", propertyList);

		FileOutputStream fos = null;
		OutputStreamWriter writer = null;
		
		try {
			fos = new FileOutputStream(targetFile);
			writer = new OutputStreamWriter(fos);
		
			//Template template = vEngine.getTemplate(templateHFileName, "utf-8");
			//StringWriter sw = new StringWriter();
			//template.merge(context, sw);
			//template.merge(context, writer);
			vEngine.mergeTemplate(templateFileName, "utf-8", context, writer);
			
			writer.flush();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
			try {
				writer.close();
			} catch (Exception e) {
			}
		}
		
	}

	protected static String getTypeOfSqlType(int sqlType) {
		if(sqlType == java.sql.Types.BIGINT) {
			return "long";
		} else if(sqlType == java.sql.Types.BOOLEAN) {
			return "boolean";
		} else if(sqlType == java.sql.Types.CHAR) {
			return "String";
		} else if(sqlType == java.sql.Types.DATE) {
			return "java.util.Date";
		} else if(sqlType == java.sql.Types.DECIMAL) {
			return "double";
		} else if(sqlType == java.sql.Types.DOUBLE) {
			return "double";
		} else if(sqlType == java.sql.Types.FLOAT) {
			return "double";
		} else if(sqlType == java.sql.Types.INTEGER) {
			return "int";
		} else if(sqlType == java.sql.Types.LONGNVARCHAR) {
			return "String";
		} else if(sqlType == java.sql.Types.LONGVARCHAR) {
			return "String";
		} else if(sqlType == java.sql.Types.NCHAR) {
			return "String";
		} else if(sqlType == java.sql.Types.NUMERIC) {
			return "double";
		} else if(sqlType == java.sql.Types.REAL) {
			return "double";
		} else if(sqlType == java.sql.Types.SMALLINT) {
			return "short";
		} else if(sqlType == java.sql.Types.TIMESTAMP) {
			return "java.sql.Timestamp";
		} else if(sqlType == java.sql.Types.TINYINT) {
			return "short";
		} else if(sqlType == java.sql.Types.VARCHAR) {
			return "String";
		} else {
			throw new RuntimeException("Not support the type in java:" + sqlType);
		}
		
	}
	
	protected static String getDefaultValueOfSqlType(int sqlType) {
		if(sqlType == java.sql.Types.BIGINT) {
			return "0";
		} else if(sqlType == java.sql.Types.BOOLEAN) {
			return "false";
		} else if(sqlType == java.sql.Types.CHAR) {
			return "\"\"";
		} else if(sqlType == java.sql.Types.DATE) {
			return "null";
		} else if(sqlType == java.sql.Types.DECIMAL) {
			return "0";
		} else if(sqlType == java.sql.Types.DOUBLE) {
			return "0";
		} else if(sqlType == java.sql.Types.FLOAT) {
			return "0";
		} else if(sqlType == java.sql.Types.INTEGER) {
			return "0";
		} else if(sqlType == java.sql.Types.LONGNVARCHAR) {
			return "\"\"";
		} else if(sqlType == java.sql.Types.LONGVARCHAR) {
			return "\"\"";
		} else if(sqlType == java.sql.Types.NCHAR) {
			return "\"\"";
		} else if(sqlType == java.sql.Types.NUMERIC) {
			return "0";
		} else if(sqlType == java.sql.Types.REAL) {
			return "0";
		} else if(sqlType == java.sql.Types.SMALLINT) {
			return "0";
		} else if(sqlType == java.sql.Types.TIMESTAMP) {
			return "null";
		} else if(sqlType == java.sql.Types.TINYINT) {
			return "0";
		} else if(sqlType == java.sql.Types.VARCHAR) {
			return "\"\"";
		} else {
			throw new RuntimeException("Not support the type in java:" + sqlType);
		}
		
	}

	protected static List<String> getAllTableName(Connection conn) throws SQLException {
		ResultSet rs = null;
		
		DatabaseMetaData meta = conn.getMetaData();
		
		try {
			rs = meta.getTables(conn.getCatalog(), null, null, new String[]{"TABLE"});
			
			List<String> tableNameList = new ArrayList<String>();
			
			while(rs.next()) {
				tableNameList.add(rs.getString("TABLE_NAME"));
			}

			return tableNameList;
		} finally {
			try {
				rs.close();
			} catch(Exception e) {}
		}
	}
}
