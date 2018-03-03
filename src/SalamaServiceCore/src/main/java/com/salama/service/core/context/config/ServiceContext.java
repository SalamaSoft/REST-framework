package com.salama.service.core.context.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author XingGu Liu
 *
 */
public class ServiceContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2830887180055921782L;

	private List<ContextSetting> _Contexts = new ArrayList<ContextSetting>();

	public List<ContextSetting> getContexts() {
		return _Contexts;
	}

	public void setContexts(List<ContextSetting> contexts) {
		_Contexts = contexts;
	}

}
