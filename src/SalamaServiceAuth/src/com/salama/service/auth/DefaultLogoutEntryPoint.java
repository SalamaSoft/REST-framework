package com.salama.service.auth;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import MetoXML.Util.DataClassFinder;

import com.salama.service.auth.base.LogoutEntryPoint;

/**
 * 
 * @author XingGu Liu
 *
 */
public class DefaultLogoutEntryPoint implements LogoutEntryPoint {

	@Override
	public void doLogout(ServletRequest request, ServletResponse response) {
		if(DataClassFinder.IsInterfaceType(request.getClass(), HttpServletRequest.class)) {
			HttpSession session = ((HttpServletRequest)request).getSession();

			session.removeAttribute(session.getId());
		} else {
			//Do nothing for now
		}
	}
}
