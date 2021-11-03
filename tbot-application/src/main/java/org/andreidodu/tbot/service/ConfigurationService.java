package org.andreidodu.tbot.service;

import java.util.Map;
import java.util.Set;

import org.andreidodu.tbot.dto.ConfigurationDTO;

public interface ConfigurationService {

	/*
	 * public void init();
	 * 
	 * public String loadGlobalValue(String key);
	 * 
	 * public String loadCustomOrGlobal(String name, String key, String def);
	 * 
	 * public String loadGlobalValueOrDefault(String key, String def);
	 * 
	 * public String loadCustomValue(String name, String key);
	 * 
	 * 
	 * 
	 * public String loadUrlBotValue(String name, String key, String link) ;
	 * 
	 * public String loadUrlBotValueCustomOrGlobal(String name, String key, String def, String link) ;
	 * 
	 * public Set<String> retrieveEnabledBots();
	 * 
	 * public List<UrlInternalDTO> loadUrls(String configPrefix);
	 */
	ConfigurationDTO loadConfiguration(String botName);

	ConfigurationDTO loadConfiguration(ConfigurationDTO configuration, String codiceQuestionario);

	Set<String> retrieveEnabledBots();

	Map<String, String> loadBotConfigurationMap(String botName);

	Map<String, String> loadRisposteByCodiceQuestionario(String codiceQuestionario);

	void aggiornaStatoCaricamentoConfigurazione(String codiceBot, String reloadBotConfiguration, String reloadConfigurationValueAggiornato);

	String loadCodiceQuestionarioBotConfiguration(String codiceBot);

	String loadValueImmediately(String codiceQuestionario, String codiceDomanda);

}
