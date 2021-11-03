package org.andreidodu.tbot.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.andreidodu.tbot.service.AppConfigurationService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import lombok.Data;

@Data
@Service
@ConfigurationProperties(prefix = "application")
public class AppConfigurationServiceImpl implements AppConfigurationService {

	private Map<String, String> configuration;

	public Map<String, String> getConfigurationByPrefix(String prefix) {
		if (prefix == null) {
			return this.configuration;
		}
		Map<String, String> result = new HashMap<>();
		this.configuration.keySet().forEach(key -> {
			if (key.startsWith(prefix)) {
				System.out.print(key.replaceFirst(prefix + ".", ""));
				result.put(key.replaceFirst(prefix + ".", ""), this.configuration.get(key));
			}
		});
		return result;
	}

	public Map<String, String> getConfigurationByPrefix() {
		return this.configuration;
	}

}
