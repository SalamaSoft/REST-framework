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
public final class DataGeneratorOfObjectiveC {

	private final static Logger logger = Logger.getLogger(DataGeneratorOfObjectiveC.class);
	
//	public static void generateAllDataFromDB(Connection conn, String namespace, String saveDirPath) {
//	}
//	
//	public static void generateOneData()
	
	protected final static String MEM_TYPE_ASSIGN = "assign";
	protected final static String MEM_TYPE_RETAIN = "retain";
	protected final static String TEMPLATE_H_FILE = "objectiveCDataH.vm";
	protected final static String TEMPLATE_M_FILE = "objectiveCDataM.vm";
	protected final static String TEMPLATE_DIR = "/com/salama/modeldriven/generator/data/template/";

	public static void createAllTableData(Connection conn, File fileSaveDir) throws ResourceNotFoundException, ParseErrorException, Exception {
		List<String> tableNameList = getAllTableName(conn);
		
		File templateHFile = new File(fileSaveDir, TEMPLATE_H_FILE);  
		File templateMFile = new File(fileSaveDir, TEMPLATE_M_FILE);
		
		//Copy template file to saveDir
		copyTemplateFileTo(templateHFile, templateMFile);

		String templateDirPath = fileSaveDir.getPath();
		if(!templateDirPath.endsWith(File.separator)) {
			templateDirPath = templateDirPath.concat(File.separator);
		}
		
		logger.debug("templateDirPath:" + templateDirPath);
		
		try {
			
			Properties velocityProperties = new Properties();
			//velocityProperties.setProperty("file.resource.loader.path", templateDir.getPath());
			velocityProperties.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, templateDirPath);
			String velocityLogPath = templateDirPath + "velocity_aijiaka.log";
			velocityProperties.setProperty("runtime.log", velocityLogPath);
			velocityProperties.setProperty("input.encoding", "utf-8");
			velocityProperties.setProperty("output.encoding", "utf-8");
			
			VelocityEngine vEngine = new VelocityEngine();
			vEngine.init(velocityProperties);

			for(String tableName : tableNameList) {
				createTableData(conn, vEngine,  tableName, fileSaveDir, templateDirPath, 
						TEMPLATE_H_FILE, TEMPLATE_M_FILE);
			}
			
			//vEngine.clearProperty("runtime.log");
		} finally {
			templateHFile.delete();
			templateMFile.delete();
		}
		
	}
	
	protected static void copyTemplateFileTo(File templateHFile, File templateMFile) throws IOException {
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			is = DataGeneratorOfObjectiveC.class.getResourceAsStream(TEMPLATE_DIR + TEMPLATE_H_FILE);
			fos = new FileOutputStream(templateHFile);
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
		
		try {
			is = DataGeneratorOfObjectiveC.class.getResourceAsStream(TEMPLATE_DIR + TEMPLATE_M_FILE);
			fos = new FileOutputStream(templateMFile);
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
			String templateHFileName, String templateMFileName) throws Exception {
		
		List<DBColumnInfo> dbColInfoList;
		dbColInfoList = DBUtil.getColumnInfoList(conn, tableName);
		
		List<ObjectiveCProperty> propertyList = new ArrayList<ObjectiveCProperty>();
		
		ObjectiveCProperty property = null;
		for(DBColumnInfo dbColInfo : dbColInfoList) {
			try {
				property = new ObjectiveCProperty();
				property.setName(dbColInfo.getName());
				property.setType(getTypeOfSqlType(dbColInfo.getType()));
				property.setMemType(getMemTypeOfType(property.getType()));

				propertyList.add(property);
			} catch(RuntimeException e) {
				throw new RuntimeException("Not support this type:" + dbColInfo.getType() + " table:" + tableName + " col:" + dbColInfo.getName() );
			}
		}
	
		File targetHFile = new File(fileSaveDir, tableName + ".h");
		File targetMFile = new File(fileSaveDir, tableName + ".m");
		
		VelocityContext context = new VelocityContext();
		context.put("dataName", tableName);
		context.put("propertyList", propertyList);

		FileOutputStream fos = null;
		OutputStreamWriter writer = null;
		
		try {
			fos = new FileOutputStream(targetHFile);
			writer = new OutputStreamWriter(fos);
		
			//Template template = vEngine.getTemplate(templateHFileName, "utf-8");
			//StringWriter sw = new StringWriter();
			//template.merge(context, sw);
			//template.merge(context, writer);
			vEngine.mergeTemplate(templateHFileName, "utf-8", context, writer);
			
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
		
		try {
			fos = new FileOutputStream(targetMFile);
			writer = new OutputStreamWriter(fos);
		
//			Template template = vEngine.getTemplate(templateMFileName, "utf-8");
//			template.merge(context, writer);
			vEngine.mergeTemplate(templateMFileName, "utf-8", context, writer);
			writer.flush();
			
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

	protected static String getMemTypeOfType(String type) {
		if(type.endsWith("*")) {
			return MEM_TYPE_RETAIN;
		} else {
			return MEM_TYPE_ASSIGN;
		}
	}
	protected static String getTypeOfSqlType(int sqlType) {
		if(sqlType == java.sql.Types.BIGINT) {
			return "long long";
		} else if(sqlType == java.sql.Types.BOOLEAN) {
			return "bool";
		} else if(sqlType == java.sql.Types.CHAR) {
			return "NSString*";
		} else if(sqlType == java.sql.Types.DATE) {
			return "NSDate*";
		} else if(sqlType == java.sql.Types.DECIMAL) {
			//return "NSDecimalNumber*";
			return "double";
		} else if(sqlType == java.sql.Types.DOUBLE) {
			return "double";
		} else if(sqlType == java.sql.Types.FLOAT) {
			return "double";
		} else if(sqlType == java.sql.Types.INTEGER) {
			return "int";
		} else if(sqlType == java.sql.Types.LONGNVARCHAR) {
			return "NSString*";
		} else if(sqlType == java.sql.Types.LONGVARCHAR) {
			return "NSString*";
		} else if(sqlType == java.sql.Types.NCHAR) {
			return "NSString*";
		} else if(sqlType == java.sql.Types.NUMERIC) {
			//return "NSDecimalNumber*";
			return "double";
		} else if(sqlType == java.sql.Types.REAL) {
			return "double";
		} else if(sqlType == java.sql.Types.SMALLINT) {
			return "short";
		} else if(sqlType == java.sql.Types.TIMESTAMP) {
			return "NSDate*";
		} else if(sqlType == java.sql.Types.TINYINT) {
			return "short";
		} else if(sqlType == java.sql.Types.VARCHAR) {
			return "NSString*";
		} else {
			throw new RuntimeException("Not support the type in objective-C:" + sqlType);
		}
		
	}
	
	protected static List<String> getAllTableName(Connection conn) throws SQLException {
		ResultSet rs = null;
		
		DatabaseMetaData meta = conn.getMetaData();
		
		rs = meta.getTables(conn.getCatalog(), null, null, new String[]{"TABLE"});
		
		List<String> tableNameList = new ArrayList<String>();
		
		while(rs.next()) {
			tableNameList.add(rs.getString("TABLE_NAME"));
		}

		return tableNameList;
	}
}
