package org.andreidodu.tbot.util;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ConfigUtil {
	private static String CONFIG_GLOBAL_PREFIX = "global";

	public String loadCustomOrGlobalConfigValue(Map<String, String> configuration, String configPrefix, String suffix, String def) {
		String key = configPrefix + suffix;
		String value = configuration.get(key);
		if (value != null) {
			// logga(configPrefix, "custom value: [" + key + "=" + value + "]");
			return value;
		}
		return retrieveGlobalOrDefault(configuration, configPrefix, suffix, def);
	}

	public String loadCustomOrGlobalConfigValue(Map<String, String> configuration, String configPrefix, String suffix, String def, int idxUrl) {
		String key = configPrefix + suffix + "." + idxUrl;
		String value = configuration.get(key);
		if (value != null) {
			// logga(configPrefix, "custom value: [" + key + "=" + value + "]");
			return value;
		}
		return retrieveGlobalOrDefault(configuration, configPrefix, suffix, def);
	}

	public String retrieveGlobalOrDefault(Map<String, String> configuration, String configPrefix, String suffix, String def) {
		String key;
		key = CONFIG_GLOBAL_PREFIX + suffix;
		if (configuration.get(key) != null) {
			// logga(configPrefix, "global value: [" + key + "=" + configuration.get(key) +
			// "]");
			return configuration.get(CONFIG_GLOBAL_PREFIX + suffix);
		}
		return def;
	}

	public String loadCustomConfigValue(Map<String, String> configuration, String configPrefix, String suffix) {
		String key = configPrefix + suffix;
		String value = configuration.get(key);
		// logga(configPrefix, "custom value: [" + key + "=" + value + "]");
		return value;
	}

	public String loadCustomConfigValue(Map<String, String> configuration, String configPrefix, String suffix, int idxUrl) {
		String key = configPrefix + suffix + "." + idxUrl;
		String value = configuration.get(key);
		// logga(configPrefix, "custom value: [" + key + "=" + value + "]");
		return value;
	}

	protected void logga(String configPrefix, String string) {
		log.info("[" + configPrefix + "] " + string);
	}
}
