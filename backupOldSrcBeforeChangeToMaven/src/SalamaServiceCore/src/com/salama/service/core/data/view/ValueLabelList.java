package com.salama.service.core.data.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author XingGu Liu
 *
 */
public class ValueLabelList implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2220376198992778780L;

	private String _name = "";
	
	private List<ValueLabel> _valueLabels = new ArrayList<ValueLabel>();

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public List<ValueLabel> getValueLabels() {
		return _valueLabels;
	}

	public void setValueLabels(List<ValueLabel> valueLabels) {
		_valueLabels = valueLabels;
	}

}
