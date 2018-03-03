package com.salama.service.core.auth;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import com.salama.service.core.annotation.AccessibleRoles;

/**
 * 
 * @author XingGu Liu
 *
 */
public class AnnotationMethodAccessController implements MethodAccessController {

	public void Init() {
		
	}
	
	public void Release() {
		
	}
	
	public boolean isMethodAccessible(Method method, List<GrantedAuthority> grantedAuthorities) {
		AccessibleRoles accessibleRoles = method.getAnnotation(AccessibleRoles.class);
		
		if(accessibleRoles == null || accessibleRoles.roles() == null || accessibleRoles.roles().length == 0 ) {
			return true;
		} else {
			boolean isAccessible = false;

			Iterator<GrantedAuthority> iteGranted = grantedAuthorities.iterator();
			String accessibleRoleName;
			GrantedAuthority grantedAuthority;
			int i;
			
			while(iteGranted.hasNext()) {
				grantedAuthority = iteGranted.next();

				for(i = 0; i < accessibleRoles.roles().length; i++) {
					accessibleRoleName = accessibleRoles.roles()[i];
					
					if(accessibleRoleName.equals(grantedAuthority.getAuthority())) {
						isAccessible = true;
						break;
					}
				}
				
				if(isAccessible) {
					break;
				}
			}
			
			return isAccessible;
		}
		
	}
	
}
