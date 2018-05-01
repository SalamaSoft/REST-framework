package com.salama.service.clouddata.util;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.util.List;

/**
 * 
 * @author XingGu Liu
 *
 */
public class JavaAssistUtil {

    public static String[] getParameterNames(String className, String methodName, boolean isStaticMethod, int paramCount) throws NotFoundException {
        LocalVariableAttribute localVarAttr = getLocalVariableAttribute(className, methodName);

        String[] paramNames = new String[paramCount];
        int paramIndex = 0;

        int tableLen = localVarAttr.tableLength();
        for(int i = 0; i < tableLen; i++) {
            int startPc = localVarAttr.startPc(i);
            if(startPc == 0) {
                String varName = localVarAttr.variableName(i);
                if(isStaticMethod) {
                    paramNames[paramIndex++] = varName;
                    if(paramIndex >= paramCount) {
                        break;
                    }
                } else {
                    //skip the 1st param 'this'
                    if(paramIndex > 0) {
                        paramNames[paramIndex - 1] = varName;
                    }
                    paramIndex++;
                    if(paramIndex > paramCount) {
                        break;
                    }
                }
            }
        }

        return paramNames;
    }

	public static LocalVariableAttribute getLocalVariableAttribute(String className, String methodName) throws NotFoundException {
		ClassPool clsPool = ClassPool.getDefault();
		
		CtClass ctCls = clsPool.get(className);
		CtMethod[] ctMethods = ctCls.getMethods();
		CtMethod ctMethod = null;
		
		for(CtMethod methodTmp : ctMethods) {
			if(methodTmp.getName().equals(methodName)
					&& (methodTmp.getModifiers() & Modifier.PUBLIC) != 0
					) {
				ctMethod = methodTmp;
				break;
			}
		}

		MethodInfo methodInfo = ctMethod.getMethodInfo();
		CodeAttribute codeAttr = methodInfo.getCodeAttribute();
		LocalVariableAttribute localVarAttr = (LocalVariableAttribute) codeAttr.getAttribute(LocalVariableAttribute.tag);

		return localVarAttr;
	}

	/* incorrect in some cases
	public static String getParameterName(LocalVariableAttribute localVarAttr, int parameterIndex, boolean isStaticMethod) {
        int targetIndex = isStaticMethod? parameterIndex : (parameterIndex + 1);

        int tableLen = localVarAttr.tableLength();
        for(int i = 0; i < tableLen; i++) {
            int index = localVarAttr.index(i);
            if(index == targetIndex) {
                final String varName = localVarAttr.variableName(i);
                return varName;
            }
        }

        return null;
    }
	*/

	/* unused
	public static String[] getParameterNames(String className, String methodName) throws NotFoundException {
		ClassPool clsPool = ClassPool.getDefault();
		
		CtClass ctCls = clsPool.get(className);

		CtMethod[] ctMethods = ctCls.getMethods();
		CtMethod ctMethod = null;

		for(CtMethod methodTmp : ctMethods) {
			if(methodTmp.getName().equals(methodName)
					&& (methodTmp.getModifiers() & Modifier.PUBLIC) != 0
				) {
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
	*/

}
