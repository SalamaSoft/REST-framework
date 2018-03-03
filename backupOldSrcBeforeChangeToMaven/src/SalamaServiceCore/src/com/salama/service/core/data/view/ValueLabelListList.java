package com.salama.service.core.data.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author XingGu Liu
 *
 */
public class ValueLabelListList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3526648176503252288L;
	
	private List<ValueLabelList> _valueLabelListList = new ArrayList<ValueLabelList>();

	public List<ValueLabelList> getValueLabelListList() {
		return _valueLabelListList;
	}

	public void setValueLabelListList(List<ValueLabelList> valueLabelListList) {
		_valueLabelListList = valueLabelListList;
	}
	
}
