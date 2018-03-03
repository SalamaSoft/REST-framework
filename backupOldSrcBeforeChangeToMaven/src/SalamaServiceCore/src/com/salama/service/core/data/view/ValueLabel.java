package com.salama.service.core.data.view;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class ValueLabel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7831012272714561761L;

	private String _value = "";
	
	private String _label = "";

	public String getValue() {
		return _value;
	}

	public void setValue(String value) {
		_value = value;
	}

	public String getLabel() {
		return _label;
	}

	public void setLabel(String label) {
		_label = label;
	}
	
	public ValueLabel() {
		
	}
	
	public ValueLabel(String value, String label) {
		this._value = value;
		this._label = label;
	} 
	
}
