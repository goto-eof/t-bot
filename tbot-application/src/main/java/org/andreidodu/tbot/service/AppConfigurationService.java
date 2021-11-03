package org.andreidodu.tbot.service;

import java.util.Map;

public interface AppConfigurationService {

	Map<String, String> getConfigurationByPrefix(String prefix);
	
	Map<String, String> getConfigurationByPrefix();
	
}
