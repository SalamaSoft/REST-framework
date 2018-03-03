package com.salama.service.clouddata.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author XingGu Liu
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ClientService {
	public enum NotificationNameParamType {
		ByName, FirstParam, LastParam
	};
	public static final String DEFAULT_CALLBACK_NAME_FROM_REQUEST_PARAM = "callbackForNotification";
	public static final String DEFAULT_NOTIFICATION_NAME_FROM_REQUEST_PARAM = "notificationName";
	
	/**
	 * The parameter name in request to get the the name of java script call back function.
	 * @return
	 */
	String callBackNameFromRequestParam() default DEFAULT_CALLBACK_NAME_FROM_REQUEST_PARAM;

	NotificationNameParamType notificationNameParamType() default NotificationNameParamType.LastParam; 
	
	/**
	 * The parameter name in request to get the the notification name for java script call back function.
	 * The format of the java script call back function is always(In this example, the call back is "test", 
	 * the notification name is "noti1", the returnValue is the return value of this invoking) : 
	 *  test(noti1, returnValue)   
	 * @return
	 */
	String notificationNameFromRequestParam() default DEFAULT_NOTIFICATION_NAME_FROM_REQUEST_PARAM;
}
