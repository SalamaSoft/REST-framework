package com.salama.service.core.data.session;

import java.io.Serializable;

/**
 * 
 * @author XingGu Liu
 *
 */
public class MapValueSet implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2068182498792198850L;

	private String _key = "";

	private String _dataType = "";
	
	private String _property = "";
	
	private String _propertyType = "";
	
	private String _valueXml = "";
	

	public String getKey() {
		return _key;
	}

	public void setKey(String key) {
		_key = key;
	}

	public String getProperty() {
		return _property;
	}

	public void setProperty(String property) {
		_property = property;
	}

	public String getDataType() {
		return _dataType;
	}

	public void setDataType(String dataType) {
		_dataType = dataType;
	}

	public String getValueXml() {
		return _valueXml;
	}

	public void setValueXml(String valueXml) {
		_valueXml = valueXml;
	}
	
	public String getPropertyType() {
		return _propertyType;
	}

	public void setPropertyType(String propertyType) {
		_propertyType = propertyType;
	}
}
