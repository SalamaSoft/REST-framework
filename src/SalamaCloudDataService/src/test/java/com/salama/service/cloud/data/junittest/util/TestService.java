package com.salama.service.cloud.data.junittest.util;

import com.salama.service.core.net.RequestWrapper;
import com.salama.service.core.net.ResponseWrapper;

public class TestService {

    public static String test1(
            RequestWrapper request, ResponseWrapper response,
            String param1, String param2, int param3
            ) {
        String var1 = "1111";
        long var2 = 222;
        int var3 = 333;

        System.out.println(" params ------>"
                + " param1:" + param1
                + " param2:" + param2
                + " param3:" + param3
                + " var1:" + var1
                + " var2:" + var2
                + " var3:" + var3
        );

        return "test<<<<<<";
    }
}
