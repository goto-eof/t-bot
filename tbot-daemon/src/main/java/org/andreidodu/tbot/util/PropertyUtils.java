package org.andreidodu.tbot.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

@Component
public class PropertyUtils {

	@Autowired
	private Environment env;

	public List<String> asList(String str, String div) {
		List<String> result = new ArrayList<>();
		String property = this.env.getProperty(str);
		if (property != null) {
			String[] arr = property.split(div);
			return Arrays.asList(arr);
		}
		return result;
	}

	public List<String> asUniqueList(String str, String div) {
		List<String> duplicated = asList(str, div);
		return removeDuplicates(duplicated);
	}

	public List<String> removeDuplicates(List<String> source) {
		List<String> result = new ArrayList<>();
		source.forEach(item -> {
			if (!result.contains(item)) {
				result.add(item);
			}
		});
		return result;
	}

	public Map<String, String> getAllByPrefix(String keyPrefix) {
		Map<String, Object> all = getAll();
		Map<String, String> result = new HashMap<>();
		for (String key : all.keySet()) {
			if (key.startsWith(keyPrefix)) {
				result.put(key, all.get(key).toString());
			}
		}
		return result;
	}

	public Map<String, Object> getAll() {
		Map<String, Object> map = new HashMap();
		for (Iterator it = ((AbstractEnvironment) this.env).getPropertySources().iterator(); it.hasNext();) {
			PropertySource propertySource = (PropertySource) it.next();
			if (propertySource instanceof MapPropertySource) {
				map.putAll(((MapPropertySource) propertySource).getSource());
			}
		}
		return map;
	}

	public String getProperty(String key) {
		return this.env.getProperty(key);
	}

	public boolean getBool(String key) {
		return BooleanUtils.toBoolean(getProperty(key));
	}

	public Integer getInteger(String key) {
		String value = getProperty(key);
		return Integer.valueOf(value);
	}

}
