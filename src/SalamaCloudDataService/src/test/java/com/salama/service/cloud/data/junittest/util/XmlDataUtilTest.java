package com.salama.service.cloud.data.junittest.util;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.junit.Test;
import org.omg.PortableInterceptor.SUCCESSFUL;

import com.salama.service.clouddata.util.XmlDataUtil;

/**
 * 
 * @author XingGu Liu
 *
 */
public class XmlDataUtilTest {

	@Test
	public void testConvertObjectToXml() {
		try {
			TestData1 data = createTestData1();
			
			String xmlString = XmlDataUtil.convertObjectToXml(data, TestData1.class);
			
			System.out.println("xml:\r\n" + xmlString + "\r\n");
			
			TestData1 data2 = (TestData1)XmlDataUtil.convertXmlToObject(xmlString, TestData1.class);
			
			String xmlString2 = XmlDataUtil.convertObjectToXml(data2, TestData1.class);
			System.out.println("xml2:\r\n" + xmlString2 + "\r\n");
			
			if(!xmlString.equals(xmlString2)) {
				fail("failed");
			}
			
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
