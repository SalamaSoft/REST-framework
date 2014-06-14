package com.salama.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import MetoXML.Util.ClassFinder;

/**
 * 
 * @author XingGu Liu
 *
 */
public class MethodInvokeUtil {
	/*
	protected ClassFinder _classFinder = null;
	
	private MethodInvokeUtil(ClassFinder classFinder) {
		_classFinder = classFinder;
	}

	public Object InvokeMethod(String serviceType, String methodName, Object[] paramValues, String[] parameterTypes) 
 	throws IllegalAccessException, InstantiationException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException
 	{
 		return InvokeMethod(serviceType, methodName, paramValues, parameterTypes, _classFinder);
 	}
 	
	public Object InvokeMethod(String serviceType, String methodName, Object[] paramValues, String[] parameterTypes, ClassFinder classFinder) 
 	throws IllegalAccessException, InstantiationException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException
 	{
 		Method method = GetMethod(serviceType, methodName, parameterTypes, classFinder);
 		return InvokeMethod(method, paramValues);
 	}

	public Object InvokeMethod(Class<?> serviceType, String methodName, Object[] paramValues, String[] parameterTypes) 
 	throws IllegalAccessException, InstantiationException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException
 	{
 		return InvokeMethod(serviceType, methodName, paramValues, parameterTypes, _classFinder);
 	}
 	
	public Object InvokeMethod(Class<?> serviceType, String methodName, Object[] paramValues, String[] parameterTypes, ClassFinder classFinder) 
 	throws IllegalAccessException, InstantiationException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException
 	{
 		Method method = GetMethod(serviceType, methodName, parameterTypes, classFinder);
 		return InvokeMethod(method, paramValues);
 	}
 	
	public Object InvokeMethod(Class<?> serviceType, String methodName, Object[] paramValues, Class<?>[] parameterTypes) 
 		 	throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException
 	{
 		Method method = GetMethod(serviceType, methodName, parameterTypes);
 		return InvokeMethod(method, paramValues);
 	}
 	*/

	/*
 	public static Object InvokeMethod(Method method, Object[] paramValues) 
 	throws IllegalAccessException, InstantiationException, InvocationTargetException
 	{
        if (!IsMethodStatic(method))
        {
            throw new IllegalAccessError("Must be a static method:" + method.getName());
        }
        
        return InvokeMethod(null, method, paramValues);
 	}	
 	*/
 	public static Object InvokeMethod(Object service, Method method, Object[] paramValues) 
 	throws IllegalAccessException, InstantiationException, InvocationTargetException
 	{
        Object returnVal = null;
        
        if(paramValues != null && paramValues.length > 0) {
            if (method.getReturnType() == void.class)
            {
                method.invoke(service, paramValues);
            }
            else
            {
                returnVal = method.invoke(service, paramValues);
            }
        } else {
            if (method.getReturnType() == void.class)
            {
                method.invoke(service, (Object[])null);
            }
            else
            {
                returnVal = method.invoke(service, (Object[])null);
            }
        }
    
        return returnVal;
	}

 	public static Method GetMethod(Class<?> serviceType, String methodName, Class<?>[] parameterTypes) 
    throws NoSuchMethodException
    {
 		try {
 	    	return serviceType.getMethod(methodName, parameterTypes);
 		} catch(NoSuchMethodException e) {
			Method[] methods = serviceType.getMethods();
			Method methodTmp = null;
			Class<?>[] paramTypesTmp = null;
			int i, j;
			boolean isParamTypesMatch;
			
			for(i = 0; i < methods.length; i++) {
				methodTmp = methods[i];
				
				if(methodTmp.getName().equals(methodName)) {
					paramTypesTmp = methodTmp.getParameterTypes();
					//check the params
					if(parameterTypes.length != paramTypesTmp.length) {
						continue;
					} else {
 						isParamTypesMatch = true;
 						for(j = 0; j < parameterTypes.length; j++) {
 							if(!paramTypesTmp[j].isAssignableFrom(parameterTypes[j])) {
 								isParamTypesMatch = false;
 								break;
 							}
 						}
						
 						if(isParamTypesMatch) {
 							return methodTmp;
 						}
					}
				}
			} //for
			
			throw e;
 		}
    }

 	/*
 	public Method GetMethod(String serviceType, String methodName, String[] parameterTypeNames) 
    throws NoSuchMethodException, ClassNotFoundException
    {
    	return GetMethod(serviceType, methodName, parameterTypeNames, _classFinder);
    }   
    
 	public Method GetMethod(String serviceType, String methodName, String[] parameterTypeNames, ClassFinder classFinder) 
    throws NoSuchMethodException, ClassNotFoundException
    {
    	Class<?> serviceClass = classFinder.findClass(serviceType);
    	
    	return GetMethod(serviceClass, methodName, parameterTypeNames);
    }

 	public Method GetMethod(Class<?> serviceType, String methodName, String[] parameterTypeNames) 
    throws NoSuchMethodException, ClassNotFoundException
    {
    	return GetMethod(serviceType, methodName, parameterTypeNames, _classFinder);
    }    
    */
    
 	public static Method GetMethod(Class<?> serviceType, String methodName, 
 			String[] parameterTypeNames, ClassFinder dataClassFinder) 
    throws NoSuchMethodException, ClassNotFoundException
    {
    	if(parameterTypeNames == null || parameterTypeNames.length == 0) {
        	return GetMethod(serviceType, methodName, (Class<?>[])null);
    	} else {
        	Class<?>[] parameterTypes = new Class[parameterTypeNames.length];
        	for(int i = 0; i < parameterTypeNames.length; i++) {
        		parameterTypes[i] = dataClassFinder.findClass(parameterTypeNames[i]);
        	}
        	
        	return GetMethod(serviceType, methodName, parameterTypes);
    	}
    }

 	public static boolean IsMethodStatic(Method method)
    {
    	return ((method.getModifiers() & Modifier.STATIC) != 0);
    }

 	/*
    public static boolean IsInterfaceType(Class<?> cls, Class<?> interfaceClass) {
    	if(cls.isInterface() && cls.getName().equals(interfaceClass.getName())) {
    		return true;
    	} else {
        	Class<?>[] interfaces = cls.getInterfaces();
        	for(int i = 0; i < interfaces.length; i++) {
        		if(interfaces[i].getName().equals(interfaceClass.getName())) {
        			return true;
        		}
        	}
        	
        	return false;
    	}
    }
 	*/
}
