package com.salama.service.clouddata.util;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

/**
 * 
 * @author XingGu Liu
 *
 */
public class JavaAssistUtil {
	public static LocalVariableAttribute getLocalVariableAttribute(String className, String methodName) throws NotFoundException {
		ClassPool clsPool = ClassPool.getDefault();
		
		CtClass ctCls = clsPool.get(className);
		CtMethod[] ctMethods = ctCls.getMethods();
		CtMethod ctMethod = null;
		
		for(CtMethod methodTmp : ctMethods) {
			if(methodTmp.getName().equals(methodName)) {
				ctMethod = methodTmp;
				break;
			}
		}

		MethodInfo methodInfo = ctMethod.getMethodInfo();
		CodeAttribute codeAttr = methodInfo.getCodeAttribute();
		LocalVariableAttribute localVarAttr = (LocalVariableAttribute) codeAttr.getAttribute(LocalVariableAttribute.tag);

		return localVarAttr;
	}

	public static String getParameterName(LocalVariableAttribute localVarAttr, int parameterIndex, boolean isStaticMethod) {
		if(isStaticMethod) {
			return localVarAttr.variableName(parameterIndex);
		} else {
			return localVarAttr.variableName(parameterIndex + 1);
		}
	}
	
	public static String[] getParameterNames(String className, String methodName) throws NotFoundException {
		ClassPool clsPool = ClassPool.getDefault();
		
		CtClass ctCls = clsPool.get(className);

		CtMethod[] ctMethods = ctCls.getMethods();
		CtMethod ctMethod = null;

		for(CtMethod methodTmp : ctMethods) {
			if(methodTmp.getName().equals(methodName)) {
				ctMethod = methodTmp;
				break;
			}
		}

		MethodInfo methodInfo = ctMethod.getMethodInfo();
		CodeAttribute codeAttr = methodInfo.getCodeAttribute();
		LocalVariableAttribute localVarAttr = (LocalVariableAttribute) codeAttr.getAttribute(LocalVariableAttribute.tag);
		
		if(localVarAttr == null) {
			return null;
		}
		
		String[] paramNames = new String[ctMethod.getParameterTypes().length];
		int startIndex = 0;
		if(!Modifier.isStatic(ctMethod.getModifiers())) {
			startIndex = 1;
		}
		for(int i = 0; i < paramNames.length; i++) {
			paramNames[i] = localVarAttr.variableName(i + startIndex);
		}
		
		return paramNames;
	}
}
