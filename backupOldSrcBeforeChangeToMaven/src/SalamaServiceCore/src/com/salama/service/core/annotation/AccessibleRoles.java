package com.salama.service.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author XingGu Liu
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessibleRoles {
	/**
	 * 
	 * @return Array of role name which have privilege to access
	 */
	String[] roles() default {};
	
	/**
	 * return error msg when no privilege to access. Default false means that return "";
	 * @return 
	 * <br>true : return error msg when no privilege to access 
	 * 	(xml such like "<Error><type>MethodAccessNoAuthorityException</type><msg>...</msg></Error>")
	 * <br>false:  return "" when no privilege to access
	 */
	boolean returnError() default false;
}
