package com.salama.service.cloud.data.junittest.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

import com.salama.service.clouddata.util.SimpleJSONDataUtil;

/**
 * 
 * @author XingGu Liu
 *
 */
public class JSONDataUtilTest {
	private static final Log logger = LogFactory.getLog(JSONDataUtilTest.class);

	@Ignore
	public void testURLEncode() {
		String str1 = "a b+c";
		String strEncoded;
		try {
			String strOldEncoded = URLEncoder.encode(str1, "utf-8");
			System.out.println("strOldEncoded:" + strOldEncoded);
			
			strEncoded = URLEncoder.encode(str1, "utf-8").replaceAll("\\+", "%20");
			System.out.println("strEncoded:" + strEncoded);
			
			String strDecoded = URLDecoder.decode(strEncoded, "utf-8");
			System.out.println("strDecoded:" + strDecoded);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Ignore
	public void testConvertObjectToJSON() {
		try {
			TestData1 data = createTestData1();
			
			String jsonString = SimpleJSONDataUtil.convertObjectToJSON(data);
			
			System.out.println("json:\r\n" + jsonString + "\r\n");
			
			TestData1 data2 = (TestData1)SimpleJSONDataUtil.convertJSONToObject(jsonString, TestData1.class);
			
			String jsonString2 = SimpleJSONDataUtil.convertObjectToJSON(data2);
			System.out.println("json2:\r\n" + jsonString2 + "\r\n");
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testConvertObjectToJSON2() {
		try {
			TestData2 data2 = new TestData2();
			data2.setItem2("test2");
			
			TestData1 data1 = createTestData1();
			data2.setData1(data1);
			
			String jsonString = SimpleJSONDataUtil.convertObjectToJSON(data2);
			
			System.out.println("json1:\r\n" + jsonString + "\r\n");
			
			TestData2 data2decoded = (TestData2) SimpleJSONDataUtil.convertJSONToObject(
					jsonString, TestData2.class);
			String jsonString2 = SimpleJSONDataUtil.convertObjectToJSON(data2decoded);
			System.out.println("json2:\r\n" + jsonString2 + "\r\n");
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static TestData1 createTestData1() {
		TestData1 data  = new TestData1();
		
		data.setP1((byte)11);
		data.setP2('a');
		data.setP3((short)13);
		data.setP4(14);
		data.setP5(15);
		data.setP6("p6");
		data.setP7(false);
		data.setP8(new Date());
		data.setP9(new Timestamp(System.currentTimeMillis()));
		data.setP10((float)10.001);
		data.setP11(10000.12345);
		data.setP12(new BigDecimal("12314153252352.644445454344"));
		data.setP13(new java.sql.Date(System.currentTimeMillis()));
		
		return data;
	}
	
}
