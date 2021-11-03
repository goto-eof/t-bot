package org.andreidodu.tbot.util;

import java.util.Map;

import org.andreidodu.tbot.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Deprecated
public class ConfigUtilCustom extends ConfigUtil {
	private static String CONFIG_GLOBAL_PREFIX = "global";

	@Autowired
	private ConfigurationService configurationService;

	public String loadCustomOrGlobalConfigValue(Map<String, String> configuration, String configPrefix, String suffix, String def) {
		String key = configPrefix + suffix;
		String value = configuration.get(key);
		if (value != null) {
			// logga(configPrefix, "custom value: [" + key + "=" + value + "]");
			return value;
		}
		return retrieveGlobalOrDefault(suffix, def);
	}

	public String loadCustomOrGlobalConfigValue(Map<String, String> configuration, String configPrefix, String suffix, String def, int idxUrl) {
		String key = configPrefix + suffix + "." + idxUrl;
		String value = configuration.get(key);
		if (value != null) {
			// logga(configPrefix, "custom value: [" + key + "=" + value + "]");
			return value;
		}
		return retrieveGlobalOrDefault(suffix, def);
	}

	/*****************************************************************************
	 * OK
	 *****************************************************************************/
	public String retrieveGlobalOrDefault(String suffix, String def) {
		String key = CONFIG_GLOBAL_PREFIX + suffix;
		/*
		 * String value = this.configurationService.loadGlobalValue(key); if (value !=
		 * null) { return value; }
		 */
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
