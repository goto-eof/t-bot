package org.andreidodu.tbot.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ConfigurationDTO {

	private String codiceBot;
	private String codiceQuestionarioBot;
	Map<String, String> globalConfiguration = new HashMap<>();
	Map<String, String> botConfiguration = new HashMap<>();
	Map<String, Map<String, String>> urlsConfiguration = new HashMap<>();

	public List<UrlInternalDTO> loadUrls() {
		List<UrlInternalDTO> result = new ArrayList<>();
		this.urlsConfiguration.keySet().forEach(codiceQuestionario -> {
			Map<String, String> risposte = this.urlsConfiguration.get(codiceQuestionario);
			UrlInternalDTO toAdd = new UrlInternalDTO();
			toAdd.setCodiceBot(this.codiceBot);
			toAdd.setCodiceQuestionario(codiceQuestionario);
			toAdd.setUrl(risposte.get("U020"));
			result.add(toAdd);
		});

		return result;
	}

	public String loadCustomValue(String codiceDomanda) {
		return this.botConfiguration.get(codiceDomanda);

	}

	public String loadCustomValue(String codiceDomanda, String defaultValue) {
		String value = this.loadCustomValue(codiceDomanda);
		return value == null ? defaultValue : value;
	}

	public String loadUrlBotValue(UrlInternalDTO urlInternal, String codiceDomanda) {
		return this.urlsConfiguration.get(urlInternal.getCodiceQuestionario()).get(codiceDomanda);
	}

	public String loadUrlBotValueCustomOrGlobal(UrlInternalDTO urlInternal, String codiceDomanda, String defaultValue) {
		final String urlValue = this.loadUrlBotValue(urlInternal, codiceDomanda);
		return urlValue == null ? this.loadCustomValue(codiceDomanda) == null ? defaultValue : this.loadCustomValue(codiceDomanda) : urlValue;
	}

}
