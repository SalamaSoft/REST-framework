package com.salama.util.test;

import java.nio.charset.Charset;

import com.salama.service.auth.config.Authentication;
import com.salama.service.core.context.config.ContextSetting;
import com.salama.service.core.context.config.ServiceContext;
import com.salama.service.invoke.config.BasePackage;

import MetoXML.XmlDeserializer;
import MetoXML.XmlSerializer;


public class ServiceContextTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TestServiceContext();
			
			TestInvokeServiceContext();

			TestAuthenticationContext();
			
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}

	public static void TestServiceContext() throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		ContextSetting invokeServiceContextSetting = new ContextSetting();
		invokeServiceContextSetting.setConfigLocation("/WEB-INF/salama/InvokeServiceContext.xml");
		invokeServiceContextSetting.setContextClass("com.salama.service.invoke.ServiceContext");
		
		serviceContext.getContexts().add(invokeServiceContextSetting);

		ContextSetting authenticationContextSetting = new ContextSetting();
		authenticationContextSetting.setConfigLocation("/WEB-INF/salama/AuthenticationContext.xml");
		authenticationContextSetting.setContextClass("com.salama.service.auth.AuthenticationContext");
		
		serviceContext.getContexts().add(authenticationContextSetting);
		
		XmlSerializer xmlSer = new XmlSerializer();
		xmlSer.Serialize("ServiceContext.xml", serviceContext, ServiceContext.class, XmlDeserializer.DefaultCharset);
		
	}
	
	public static void TestInvokeServiceContext() throws Exception{
		com.salama.service.invoke.config.ServiceContext invokeServiceContext = new com.salama.service.invoke.config.ServiceContext();

		BasePackage package1 = new BasePackage();
		package1.setBasePackage("com.salama.appserver.test.data");
		
		BasePackage package2 = new BasePackage();
		package2.setBasePackage("com.salama.appserver.service");
		
		invokeServiceContext.getDataScan().add(package1);

		invokeServiceContext.getServiceScan().add(package2);
		
		XmlSerializer xmlSer = new XmlSerializer();
		xmlSer.Serialize("InvokeServiceContext.xml", invokeServiceContext, 
				com.salama.service.invoke.config.ServiceContext.class, XmlDeserializer.DefaultCharset);
		
	}
	
	public static void TestAuthenticationContext() throws Exception{
		Authentication authentication = new Authentication();
		
		XmlSerializer xmlSer = new XmlSerializer();
		xmlSer.Serialize("AuthenticationContext.xml", authentication, 
				Authentication.class, XmlDeserializer.DefaultCharset);
	}
}
