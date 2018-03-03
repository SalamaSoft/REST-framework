package com.salama.service.core.auth;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 
 * @author XingGu Liu
 *
 */
public interface MethodAccessController {
	
	public void Init();
	
	public void Release();
	
	public boolean isMethodAccessible(Method method, List<GrantedAuthority> grantedAuthorities);
}
