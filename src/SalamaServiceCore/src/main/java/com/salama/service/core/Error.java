package com.salama.service.core;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class Error implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7446575421535606219L;

	private String _type = "";
	
	private String _msg = "";

	public String getType() {
		return _type;
	}

	public void setType(String type) {
		_type = type;
	}

	public String getMsg() {
		return _msg;
	}

	public void setMsg(String msg) {
		_msg = msg;
	}
}
