package com.salama.service.invoke.config;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class XmlResultSetting implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3609522119732209562L;

	private boolean useClassFullName = false;
	
	private boolean makeFirstCharUpperCase = false;

	public boolean isUseClassFullName() {
		return useClassFullName;
	}

	public void setUseClassFullName(boolean useClassFullName) {
		this.useClassFullName = useClassFullName;
	}

	public boolean isMakeFirstCharUpperCase() {
		return makeFirstCharUpperCase;
	}

	public void setMakeFirstCharUpperCase(boolean makeFirstCharUpperCase) {
		this.makeFirstCharUpperCase = makeFirstCharUpperCase;
	}

	
}
