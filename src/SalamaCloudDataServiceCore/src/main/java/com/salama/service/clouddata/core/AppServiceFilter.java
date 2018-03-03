package com.salama.service.clouddata.core;

import com.salama.service.core.net.RequestWrapper;
import com.salama.service.core.net.ResponseWrapper;
import java.lang.reflect.Method;
import javax.servlet.ServletContext;

public abstract interface AppServiceFilter
{
  public abstract ServiceFilterResult filter(RequestWrapper paramRequestWrapper, ResponseWrapper paramResponseWrapper, boolean paramBoolean, Method paramMethod, Object paramObject, Object[] paramArrayOfObject);

  public abstract void reload(ServletContext paramServletContext, AppContext paramAppContext);

  public abstract void destroy();

  public static class ServiceFilterResult
  {
    public boolean isServiceOverrided = false;

    public Object serviceReturnValue = null;
  }
}