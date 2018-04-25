package com.salama.service.cloud.data.junittest.util;

import com.salama.reflect.MethodInvokeUtil;
import com.salama.service.clouddata.CloudDataService;
import com.salama.service.clouddata.util.JavaAssistUtil;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.junit.Test;

import java.lang.reflect.Method;

public class TestJavaAssist {

    @Test
    public void test1() {
        Class<?> cls = TestService.class;
        String methodName = "test1";

        testJavasist(cls, methodName);

        try {
            testJavassit2(cls.getName(), methodName);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        Class<?> cls = CloudDataService.class;
        String methodName = "cloudDataService";

        testJavasist(cls, methodName);

        try {
            testJavassit2(cls.getName(), methodName);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    protected static void testJavasist(Class<?> cls, String methodName) {
        try {
            LocalVariableAttribute localVarAttr = JavaAssistUtil.getLocalVariableAttribute(
                    cls.getName(), methodName);

            Method method = findMethod(cls, methodName);
            Object[] paramTypes = method.getParameterTypes();
            int paramCnt = paramTypes.length;

            boolean isStaticMethod = true;
            if(!MethodInvokeUtil.IsMethodStatic(method)) {
                isStaticMethod = false;
            }

            System.out.println("class:" + cls.getName() + " method:" + methodName);
            for(int i = 0; i < paramCnt; i++) {
                String paramName = JavaAssistUtil.getParameterName(localVarAttr, i, isStaticMethod);
                System.out.println("paramName[" + paramName + "]:" + paramName);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void testJavassit2(String className, String methodName) throws NotFoundException {

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

        //debug
        int tableLen = localVarAttr.tableLength();
        for(int i = 0; i < tableLen; i++) {
            String varName = localVarAttr.variableName(i);
            int codeLen = localVarAttr.codeLength(i);
            int startPc = localVarAttr.startPc(i);
            int index = localVarAttr.index(i);
            String signature = localVarAttr.signature(i);
            String desc = localVarAttr.descriptor(i);

            System.out.println("varAttr[" + i + "]"
                    + " varName:" + varName
                    + " codeLen:" + codeLen
                    + " startPc:" + startPc
                    + " index:" + index
                    + " signature:" + signature
                    + " desc:" + desc
            );
        }
        /*
        for(int i = 0; i < tableLen; i++) {
            int index = localVarAttr.nameIndex(i);
            String varName = methodInfo.getConstPool().getUtf8Info(index);
            System.out.println("nameIndex --- varName[" + index + "]:" + varName);
        }
        */

    }

    private static Method findMethod(Class<?> cls, String methodName) {
        Method[] methods = cls.getDeclaredMethods();
        for(Method m : methods) {
            if(m.getName().equals(methodName)) {
                return m;
            }
        }

        return null;
    }
}
