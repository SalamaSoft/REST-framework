package com.salama.service.core.data.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author XingGu Liu
 *
 */
public class SessionValueSet implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5809341517922203414L;
	
	List<MapValueSet> _sessionValueSets = new ArrayList<MapValueSet>();

	public List<MapValueSet> getSessionValueSets() {
		return _sessionValueSets;
	}

	public void setSessionValueSets(List<MapValueSet> sessionValueSets) {
		_sessionValueSets = sessionValueSets;
	} 
}
