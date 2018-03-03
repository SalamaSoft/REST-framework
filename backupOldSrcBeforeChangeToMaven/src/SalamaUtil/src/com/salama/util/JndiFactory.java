package com.salama.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class JndiFactory {
	private static final Logger log = Logger.getLogger(JndiFactory.class);

	private Map<String, Object> fMap;

	private InitialContext fCtx;

	private static JndiFactory singleton = null;

	public static JndiFactory getInstance() throws NamingException {
		if (singleton == null) {
			singleton = new JndiFactory();
		}
		return singleton;
	}

	private JndiFactory() throws NamingException {
		fMap = Collections.synchronizedMap(new HashMap<String, Object>());

		fCtx = new InitialContext();

	}

	/**
	 * Look up object by jndi
	 * @param jndiName e.g.:"java:comp/env/jdbc/portal"
	 * @param clazz
	 * @return
	 * @throws NamingException
	 */
	private Object lookup(String jndiName) throws NamingException {
		Object rtn = fMap.get(jndiName);
		if (rtn == null) {
			log.debug(">>lookup(" + jndiName + ")");
			rtn = fCtx.lookup(jndiName);
			fMap.put(jndiName, rtn);
		}
		return rtn;
	}

	/**
	 * Look up object by jndi
	 * @param jndiName e.g.:"java:comp/env/jdbc/portal"
	 * @param clazz
	 * @return
	 * @throws NamingException
	 */
	public Object lookupNarrow(String jndiName, Class<?> clazz)
			throws NamingException {
		Object rtn = fMap.get(jndiName);
		if (rtn == null) {
			log.debug(">>lookup(" + jndiName + ")");
			Object objRef = fCtx.lookup(jndiName);
			log.debug(">>PortableRemoteObject#narrow(" + clazz.getName() + ")");
			rtn = PortableRemoteObject.narrow(objRef, clazz);
			fMap.put(jndiName, rtn);
		}
		return rtn;
	}

	/**
	 * Get DataSource
	 * 
	 * @param jndiName e.g.:"java:comp/env/jdbc/portal"
	 * @return DataSource
	 * @throws NamingException
	 */
	public DataSource getDataSource(String jndiName) throws NamingException {
		return (DataSource) lookup(jndiName);
	}

}
