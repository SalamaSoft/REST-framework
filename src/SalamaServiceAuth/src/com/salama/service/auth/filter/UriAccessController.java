package com.salama.service.auth.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import com.salama.service.auth.config.HttpAuthentication;
import com.salama.service.auth.config.InterceptUri;
import com.salama.service.core.auth.GrantedAuthority;

/**
 * 
 * @author XingGu Liu
 *
 */
public class UriAccessController {
	private final static Logger logger = Logger.getLogger(UriAccessController.class);
	
	protected final static String PatternOfStar = "[^/]*";
	
	protected HttpAuthentication _httpAuthentication = null;
	
	protected List<Pattern> _uriRegexPatternList = new ArrayList<Pattern>();
	protected List<HashMap<String, String>> _uriAccessibleRolesList = new ArrayList<HashMap<String, String>>();
	
	protected PatternMatcher matcher = new Perl5Matcher();
	
	public UriAccessController(HttpAuthentication httpAuthentication) 
			throws MalformedPatternException {
		_httpAuthentication = httpAuthentication;
		
		LoadConfig();
	}
	
	protected void LoadConfig() throws MalformedPatternException {
		int count = _httpAuthentication.getInterceptUris().size();
		InterceptUri interceptUri = null;
		String uriPattern = "";
		String roles = "";
		
		String[] roleArray = null;
		Pattern regexPattern = null;
		
		_uriRegexPatternList.clear();
		_uriAccessibleRolesList.clear();
		PatternCompiler compiler = new Perl5Compiler();
		HashMap<String, String> rolesMap = null;
		String roleTmp;
		int j;
		for(int i = 0; i < count; i++) {
			interceptUri = _httpAuthentication.getInterceptUris().get(i);
			
			roles = interceptUri.getAccessRoles();
			roleArray = roles.split("[ ]*,[ ]*");
			
			rolesMap = new HashMap<String, String>();
			for(j = 0; j < roleArray.length; j++) {
				roleTmp = roleArray[j].trim();
				if(roleTmp.length() > 0) {
					rolesMap.put(roleTmp, roleTmp);
				}
			}
			
			_uriAccessibleRolesList.add(rolesMap);
			
			uriPattern = interceptUri.getPattern().replace("*", PatternOfStar);
			regexPattern = compiler.compile(uriPattern);
			_uriRegexPatternList.add(regexPattern);
		}
	}

	public boolean isAccessible(String uri, List<GrantedAuthority> grantedAuthorities)  {
		int index = -1;
		
		for(int i = 0 ; i < _uriRegexPatternList.size(); i++) {
			logger.debug("isAccessible() uri:" + uri + ";regexPattern:" + _uriRegexPatternList.get(i).getPattern());
			if(matcher.matches(uri, _uriRegexPatternList.get(i))) {
				index = i;
				break;
			} 
		}
		
		if(index < 0) {
			return true;
		} else {
			int countOfGranted = 0;
			if(grantedAuthorities != null) {
				countOfGranted = grantedAuthorities.size();
			}

			HashMap<String, String> accessibleRoleMap = _uriAccessibleRolesList.get(index);
			int countOfAccessible = accessibleRoleMap.size();
			
			if(countOfAccessible == 0) {
				return true;
			} else {
				if(countOfGranted == 0) {
					return false;
				} else {
					int i;
					String grantedAuthority;
					for(i = 0; i < countOfGranted; i++) {
						grantedAuthority = grantedAuthorities.get(i).getAuthority().trim();
						
						if(accessibleRoleMap.containsKey(grantedAuthority)) {
							return true;
						}
					}
					
					return false;
				}
			} //if else
		}//if else
	}
}
